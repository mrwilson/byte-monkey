/**
 *
 */
package com.github.sworm.spojo.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.util.Assert;

import com.github.sworm.spojo.data.Property;
import com.github.sworm.spojo.exceptions.SpojoException;

/**
 * Static convenience methods for JavaBeans: for instantiating beans,copying bean properties,etc. <br>
 * <p>
 * <i>This Class is strongly inspired by the BeanUtils of Spring.</i><br>
 * <br>
 * Thanks a lot to Spring Team for their great job. <br>
 * Especially thanks to the authors of BeanUtils class: <br>
 * <ul>
 * <li><b>Rod Johnson</b></li>
 * <li><b>Juergen Hoeller</b></li>
 * <li><b>Rob Harrop</b></li>
 * <li><b>Sam Brannen</b></li>
 * </ul>
 * </p>
 * We are glad to contribute to the evolution of Big Open Source code.<br>
 * 
 * @author Vincent Palau
 * @Since Feb 27, 2011
 */
public abstract class SpojoUtils {

	static Set<Class<?>> defaultImmutableClasses = new HashSet<Class<?>>();
	static {
		addImmutableClass(Character.class);
		addImmutableClass(String.class);
		addImmutableClass(Date.class);
		addImmutableClass(java.sql.Date.class);
		addImmutableClass(Timestamp.class);
	}

	/**
	 * add a class to the list of immutable classes
	 * 
	 * @param clazz
	 */
	static void addImmutableClass(final Class<?> clazz) {
		defaultImmutableClasses.add(clazz);
	}

	/**
	 * Calling a convenience method to instantiate a class using its no-arg constructor. As this method doesn't try to load
	 * classes by name, it should avoid class-loading issues.
	 * 
	 * @param <T>
	 * @param clazz
	 *            class to instantiate
	 * @return the new instance
	 * @throws BeanInstantiationException
	 *             if the bean cannot be instantiated
	 */
	public static <T> T instantiate(final Class<T> clazz) throws BeanInstantiationException {
		return BeanUtils.instantiate(clazz);
	}

	/**
	 * Instantiate a new object based on the type of the parameter object. It can be a collection or array type. The parameter can
	 * not be null
	 * 
	 * @param object
	 *            object which class is to be instantiate
	 * @return the new instance
	 * @throws BeanInstantiationException
	 */
	public static Object instantiateAs(final Object object) throws BeanInstantiationException {
		Assert.notNull(object, "Object must not be null");
		final Class<?> class2Instantiate = findClazztype2instantiate(null, object);
		Object innerTarget = null;
		if (class2Instantiate.isArray()) {
			innerTarget = instantiateArray(class2Instantiate.getComponentType(), Array.getLength(object));
		} else {
			innerTarget = instantiate(class2Instantiate);
		}
		return innerTarget;
	}

	/**
	 * instantiate an array
	 * 
	 * @param clazz
	 *            the component type of the array
	 * @param length
	 *            the length of the array
	 * @return
	 */
	public static Object instantiateArray(final Class<?> clazz, final int length) {
		return Array.newInstance(clazz, length);
	}

	/**
	 * Copy the property values of the given source bean into the given target bean, excluding the given <code>property</code>.
	 * <p>
	 * Note: The source and target classes do not have to match or even be derived from each other, as long as the properties
	 * match. Any bean properties that the source bean exposes but the target bean does not will silently be ignored.
	 * <p>
	 * 
	 * @param source
	 *            the source bean
	 * @param target
	 *            the target bean
	 * @param property
	 *            property names to exclude
	 * @throws BeansException
	 *             if the copying failed
	 * @throws BeanInstantiationException
	 *             if the bean cannot be instantiated
	 * @throws SpojoException
	 *             if any else errors occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void copyPropertiesWithExclude(final Object source, final Object target, final Property<String> property)
			throws BeansException, BeanInstantiationException, SpojoException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		if (source instanceof Collection) {
			copyPropertiesCollection((Collection) source, (Collection) target, property, false);
			return;
		}

		final Class<?> actualEditable = target.getClass();
		if (actualEditable.isArray()) {
			copyPropertiesArray(source, target, property, false);
			return;
		}

		final PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);

		for (PropertyDescriptor targetPd : targetPds) {
			if (hasWritableMethod(targetPd)) {

				final String propertyName = targetPd.getName();
				final PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), propertyName);

				if (isPropertyCopyable(sourcePd, propertyName)) {
					try {
						final Method readMethod = makeMethodAccessible(sourcePd.getReadMethod());
						final Method writeMethod = makeMethodAccessible(targetPd.getWriteMethod());
						Object value = readMethod.invoke(source);
						final Property<String> innerProperty = (property == null) ? null : property.getChild(propertyName);
						if (innerProperty == null || innerProperty.isComplexType()) {
							value = copyComplexProperty(innerProperty, writeMethod, value, false);
							writeMethod.invoke(target, value);
						}

					} catch (Throwable ex) {
						throw new SpojoException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}

	/**
	 * Copy the contents of the source collection into target
	 * 
	 * @param <T>
	 * @param source
	 *            the source collection
	 * @param target
	 *            the target collection
	 * @param property
	 *            property configuration
	 * @param include
	 *            true/false to include/exclude properties
	 * @throws BeansException
	 * @throws BeanInstantiationException
	 * @throws SpojoException
	 */
	@SuppressWarnings("unchecked")
	protected static <T> void copyPropertiesCollection(final Collection<T> source, final Collection<T> target,
			final Property<String> property, final boolean include) throws BeansException, BeanInstantiationException,
			SpojoException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		for (Object sourceElement : source) {
			target.add((T) copyComplexProperty(property, null, sourceElement, include));
		}
	}

	/**
	 * Copy the contents of the source array into target
	 * 
	 * @param <T>
	 * @param source
	 *            the source array
	 * @param target
	 *            the target array
	 * @param property
	 *            property configuration
	 * @param include
	 *            true/false to include/exclude properties
	 * @throws BeansException
	 * @throws BeanInstantiationException
	 * @throws SpojoException
	 */
	protected static void copyPropertiesArray(final Object source, final Object target, final Property<String> property,
			final boolean include) throws BeansException, BeanInstantiationException, SpojoException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		final int length = Array.getLength(source);

		for (int i = 0; i < length; i++) {
			Array.set(target, i, copyComplexProperty(property, null, Array.get(source, i), include));
		}
	}

	/**
	 * Copy the property values of the given source bean into the given target bean, including the given <code>property</code>.
	 * <p>
	 * Note: The source and target classes do not have to match or even be derived from each other, as long as the properties
	 * match. Any bean properties that the source bean exposes but the target bean does not will silently be ignored.
	 * <p>
	 * This is just a convenience method. For more complex transfer needs, consider using a full BeanWrapper.
	 * 
	 * @param source
	 *            the source bean
	 * @param target
	 *            the target bean
	 * @param property
	 *            property names to include
	 * @throws BeansException
	 *             if the copying failed
	 * @throws BeanInstantiationException
	 *             if the bean cannot be instantiated
	 * @throws SpojoException
	 *             if any else errors occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void copyPropertiesWithInclude(final Object source, final Object target, final Property<String> property)
			throws BeansException, BeanInstantiationException, SpojoException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		if (source instanceof Collection) {
			copyPropertiesCollection((Collection) source, (Collection) target, property, true);
			return;
		}
		final Class<?> actualEditable = target.getClass();
		if (actualEditable.isArray()) {
			copyPropertiesArray(source, target, property, true);
			return;
		}

		if (property != null && !property.isEmpty()) {

			final PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);

			// Retrieve the JavaBeans PropertyDescriptors of a given class.
			for (PropertyDescriptor targetPd : targetPds) {
				final String propertyName = targetPd.getName();
				if (hasWritableMethod(targetPd) && property.containsChild(propertyName)) {
					final PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), propertyName);

					if (isPropertyCopyable(sourcePd, propertyName)) {
						try {
							final Method readMethod = makeMethodAccessible(sourcePd.getReadMethod());
							final Method writeMethod = makeMethodAccessible(targetPd.getWriteMethod());
							Object value = readMethod.invoke(source);

							// retrieve inner property
							final Property<String> innerProperty = property.getChild(propertyName);

							if (innerProperty.isComplexType()) {
								// property has childs defined
								value = copyComplexProperty(innerProperty, writeMethod, value, true);
							} else {
								// property has not childs defined and is a complex bean
								// we must copy all the properties in a new bean instance
								value = copyComplexProperty(innerProperty, writeMethod, value, false);
							}
							writeMethod.invoke(target, value);
						} catch (Throwable ex) {
							throw new SpojoException("Could not copy properties from source to target", ex);
						}
					}
				}
			}
		}
	}

	/**
	 * Copy the property values of the given source bean into the given target bean, including/excluding the given
	 * <code>property</code>.
	 * <p>
	 * Note: The source and target classes do not have to match or even be derived from each other, as long as the properties
	 * match. Any bean properties that the source bean exposes but the target bean does not will silently be ignored.
	 * <p>
	 * 
	 * @param source
	 *            the source bean
	 * @param target
	 *            the target bean
	 * @param property
	 *            property names to include
	 * @param include
	 *            true/false to include or exclude the properties
	 * @throws BeansException
	 *             if the copying failed
	 * @throws BeanInstantiationException
	 *             if the bean cannot be instantiated
	 * @throws SpojoException
	 *             if any else errors occurs
	 */
	public static void copyProperties(final Object source, final Object target, final Property<String> property,
			final boolean include) throws BeansException, BeanInstantiationException, SpojoException {
		if (include) {
			copyPropertiesWithInclude(source, target, property);
		} else {
			copyPropertiesWithExclude(source, target, property);
		}
	}

	/**
	 * Returns if the class is immutable:<br>
	 * - is a primitive type <br>
	 * - is a number type <br>
	 * - is a immutable type <br>
	 * - is an enumeration<br>
	 * 
	 * @param value
	 *            the object which class to test
	 * @return true/false. Return true is value is null
	 */
	static boolean isImmutable(final Object value) {
		if (value == null) {
			return true;
		}
		final Class<?> clazz = value.getClass();
		return clazz.isPrimitive() || clazz.isEnum() || Number.class.isAssignableFrom(clazz)
				|| defaultImmutableClasses.contains(clazz);
	}

	/**
	 * Copy the property values of the given source bean value into a new bean, including/excluding the given
	 * <code>property</code>.
	 * 
	 * The algorithm is:<br>
	 * - Instantiates a new bean if it's necessary <br>
	 * - If value is null, returns a null value <br>
	 * - If value is immutable (@see isImmutable), returns the value <br>
	 * - copy the attributes into the new bean <br>
	 * - return the new bean (value) <br>
	 * 
	 * @param property
	 *            property names to include/exclude
	 * @param writeMethod
	 *            the method used to set the given value
	 * @param value
	 *            the value to evaluate and copy if it's needed
	 * @param include
	 *            true/false to include/exclude properties
	 * @return the bean with properties copied
	 */
	protected static Object copyComplexProperty(final Property<String> property, final Method writeMethod, final Object value,
			final boolean include) {
		// null case
		if (value == null) {
			return value;
		}
		// immutable case
		if (isImmutable(value)) {
			return value;
		}
		final Object innerTarget = instantiateAs(value);

		// copy all include/exclude inner properties
		copyProperties(value, innerTarget, property, include);
		return innerTarget;
	}

	/**
	 * Verify if the given PropertyDescriptor has a method that should be used as writable.
	 * 
	 * @param targetPd
	 *            the PropertyDescriptor
	 * @return <code>true</code> if the given PropertyDescriptor has a writable method.
	 */
	protected static boolean hasWritableMethod(final PropertyDescriptor targetPd) {
		return targetPd.getWriteMethod() != null;
	}

	/**
	 * Verify if the given propertyName is copyable as field into the given object.
	 * 
	 * @param sourcePd
	 *            the source propertyDescriptor
	 * @param propertyName
	 *            the field name to copy
	 * @return <code>true</code> if the given PropertyDescriptor has a readable method.
	 */
	protected static boolean isPropertyCopyable(final PropertyDescriptor sourcePd, final String propertyName) {
		return sourcePd != null && sourcePd.getReadMethod() != null;
	}

	/**
	 * Search for the Type Class to instantiate from the value or the method. If the type class is an interface an exception is
	 * thrown. If the type class is an interface and a collection, then converts to a specific collection implementation.
	 * <p>
	 * List to ArrayList<br>
	 * SortedSet to TreeSet<br>
	 * Set to LinkedHashSet<br>
	 * Collection to LinkedHashSet<br>
	 * </p>
	 * 
	 * @param writeMethod
	 *            the method used to lookup
	 * @param value
	 *            the value used to lookup
	 * @return the type class to instance
	 */
	protected static Class<?> findClazztype2instantiate(final Method writeMethod, final Object value) {
		final Class<?> ret = (value == null) ? writeMethod.getParameterTypes()[0] : value.getClass();

		if (ret.isInterface()) {
			// check for collections
			if (List.class.equals(ret)) {
				return ArrayList.class;
			} else if (SortedSet.class.equals(ret)) {
				return TreeSet.class;
			} else if (Set.class.equals(ret) || Collection.class.equals(ret)) {
				return LinkedHashSet.class;
			}
			throw new SpojoException("The interface: '" + ret + "' can't be instanciated");
		} else {
			// check for collections
			if (Collection.class.isAssignableFrom(ret)) {
				try {
					ret.getDeclaredConstructor();
				} catch (NoSuchMethodException e) {
					if (LinkedList.class.isAssignableFrom(ret)) {
						return LinkedList.class;
					} else if (List.class.isAssignableFrom(ret)) {
						return ArrayList.class;
					} else if (SortedSet.class.isAssignableFrom(ret)) {
						return TreeSet.class;
					} else {
						return LinkedHashSet.class;
					}
				}
			}
		}

		return ret;
	}

	/**
	 * Helper Method to make the given method accessible, just in case!
	 * 
	 * @param method
	 *            the given method
	 * @return the given method modified
	 */
	protected static Method makeMethodAccessible(final Method method) {
		if (!Modifier.isPublic(method.getModifiers())) {
			method.setAccessible(true);
		}
		return method;
	}

	/**
	 * Try to derive the class from the object. If it can't, null is returned. <br>
	 * If source is an array or collection, returns the type of its elements<br>
	 * 
	 * @param <T>
	 * @param <E>
	 * @param source
	 *            the source object
	 * @return the class or null if it can't be derived
	 */
	@SuppressWarnings("rawtypes")
	public static <T> Class<?> deriveClassFromSource(final T source) {
		Class<?> actualClass = source.getClass();
		if (source instanceof Collection) {
			Collection sourceCollection = (Collection) source;
			if (!sourceCollection.isEmpty()) {
				actualClass = sourceCollection.iterator().next().getClass();
			} else {
				actualClass = null;
			}
		}
		if (actualClass != null && actualClass.isArray()) {
			actualClass = actualClass.getComponentType();
		}
		return actualClass;
	}

}

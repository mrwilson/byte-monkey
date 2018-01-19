/**
 * 
 */
package com.github.sworm.spojo;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static com.github.sworm.spojo.enums.RuleType.*;
import static com.github.sworm.spojo.model.Gender.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;
import com.github.sworm.spojo.config.RuleMetadata;
import com.github.sworm.spojo.config.RuleMetadataImpl;
import com.github.sworm.spojo.config.SpojoConfiguration;
import com.github.sworm.spojo.exceptions.RuleException;
import com.github.sworm.spojo.exceptions.RuleNotFoundException;
import com.github.sworm.spojo.exceptions.SpojoException;
import com.github.sworm.spojo.model.Chef;
import com.github.sworm.spojo.model.Client;
import com.github.sworm.spojo.model.Gender;
import com.github.sworm.spojo.model.Shop;

/**
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
public class SpojoTest extends BaseTestCase {

	private static Spojo spojo = null;

	private final static String NAME_GENDER = "nameGender";
	private final static String SOME_2_NULL = "some2Null";
	private final static String EMPTY = "empty";
	private final static String CLIENT_NAMESPACE = Client.class.getSimpleName();
	private final static String SHOP_NAMESPACE = Shop.class.getSimpleName();

	public static class Employee {
		private String name = null;
		private Gender gender = null;
		private Integer category = null;

		public Employee(final String name, final Gender gender, final Integer category) {
			this.name = name;
			this.gender = gender;
			this.category = category;
		}

		public Employee() {
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(final String name) {
			this.name = name;
		}

		/**
		 * @return the gender
		 */
		public Gender getGender() {
			return gender;
		}

		/**
		 * @param gender
		 *            the gender to set
		 */
		public void setGender(final Gender gender) {
			this.gender = gender;
		}

		/**
		 * @return the category
		 */
		public Integer getCategory() {
			return category;
		}

		/**
		 * @param category
		 *            the category to set
		 */
		public void setCategory(final Integer category) {
			this.category = category;
		}

	}

	@BeforeClass
	public static void config() {

		List<RuleMetadata> metadataList = new ArrayList<RuleMetadata>();
		// MetaData definition

		String[] empty = new String[] {};
		String[] propsNameGender = new String[] { "name", "gender" };

		// Client Bean

		metadataList.add(new RuleMetadataImpl(CLIENT_NAMESPACE + NAME_GENDER + INCLUDE, INCLUDE, empty, propsNameGender));
		metadataList.add(new RuleMetadataImpl(CLIENT_NAMESPACE + NAME_GENDER + EXCLUDE, EXCLUDE, empty, propsNameGender));

		String[] propsWithNull = new String[] { "street", null, null };
		metadataList.add(new RuleMetadataImpl(CLIENT_NAMESPACE + SOME_2_NULL + INCLUDE, INCLUDE, empty, propsWithNull));
		metadataList.add(new RuleMetadataImpl(CLIENT_NAMESPACE + SOME_2_NULL + EXCLUDE, EXCLUDE, empty, propsWithNull));

		metadataList.add(new RuleMetadataImpl(CLIENT_NAMESPACE + EMPTY + INCLUDE, INCLUDE, empty, empty));
		metadataList.add(new RuleMetadataImpl(CLIENT_NAMESPACE + EMPTY + EXCLUDE, EXCLUDE, empty, empty));

		metadataList.add(new RuleMetadataImpl(CLIENT_NAMESPACE + EMPTY + DISABLED, DISABLED, empty, empty));

		// @formatter:off
		String[] propsShop = new String[] { 
											"id", "name", "type",
											"clients.street", "clients.gender",
											"chef.name", "chef.birthday", "chef.gender"
		// @formatter:on
		};

		metadataList.add(new RuleMetadataImpl(SHOP_NAMESPACE + EMPTY + INCLUDE, INCLUDE, empty, empty));
		metadataList.add(new RuleMetadataImpl(SHOP_NAMESPACE + EMPTY + EXCLUDE, EXCLUDE, empty, empty));

		metadataList.add(new RuleMetadataImpl(SHOP_NAMESPACE + INCLUDE, INCLUDE, empty, propsShop));
		metadataList.add(new RuleMetadataImpl(SHOP_NAMESPACE + EXCLUDE, EXCLUDE, empty, propsShop));

		Map<String, RuleMetadata> ruleNameByClazzMap = buildMetadataMap(metadataList);

		// initialize configuration
		SpojoConfiguration spojoConfiguration = new SpojoConfiguration(ruleNameByClazzMap);

		// create SpojoInstance
		spojo = new Spojo(spojoConfiguration);
	}

	/**
	 * @param metadataList
	 * @return
	 */
	private static Map<String, RuleMetadata> buildMetadataMap(final List<RuleMetadata> metadataList) {

		Map<String, RuleMetadata> ruleNameMap = new HashMap<String, RuleMetadata>();

		for (RuleMetadata metadata : metadataList) {
			ruleNameMap.put(metadata.getName(), metadata);
		}

		return ruleNameMap;
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.util.Collection, java.lang.String)}.
	 */
	@Test
	public void testShrinkCollectionExclude1() {
		Date birthday = new Date();
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			String counter = Integer.toString(i);
			Client client = new Client(counter, WOMAN, birthday, counter);
			clientMap.put(client.getName(), client);
		}

		Collection<Client> shrunkenClients = spojo.shrink(clientMap.values(), CLIENT_NAMESPACE + EMPTY + EXCLUDE);

		@SuppressWarnings("unchecked")
		Matcher<String> anyOf = anyOf(is("1"), is("2"), is("3"), is("4"), is("5"));
		for (Client shrunkenClient : shrunkenClients) {
			assertNotSame(clientMap.get(shrunkenClient.getName()), shrunkenClient);
			assertThat(shrunkenClient.getName(), anyOf);
			assertEquals(WOMAN, shrunkenClient.getGender());
			assertEquals(birthday, shrunkenClient.getBirthday());
			assertThat(shrunkenClient.getStreet(), anyOf);
		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.util.Collection, java.lang.String)}.
	 */
	@Test
	public void testShrinkCollectionExclude2() {
		Date birthday = new Date();
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			String counter = Integer.toString(i);
			Client client = new Client(counter, WOMAN, birthday, counter);
			clientMap.put(client.getName(), client);
		}

		Collection<Client> shrunkenClients = spojo.shrink(clientMap.values(), CLIENT_NAMESPACE + NAME_GENDER + EXCLUDE);

		@SuppressWarnings("unchecked")
		Matcher<String> anyOf = anyOf(is("1"), is("2"), is("3"), is("4"), is("5"));
		for (Client shrunkenClient : shrunkenClients) {
			assertNotSame(clientMap.get(shrunkenClient.getName()), shrunkenClient);
			assertNull(shrunkenClient.getName());
			assertNull(shrunkenClient.getGender());
			assertEquals(birthday, shrunkenClient.getBirthday());
			assertThat(shrunkenClient.getStreet(), anyOf);
		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.util.Collection, java.lang.String)}.
	 */
	@Test
	public void testShrinkCollectionExclude3() {
		Date birthday = new Date();
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			String counter = Integer.toString(i);
			Client client = new Client(counter, MAN, birthday, counter);
			clientMap.put(client.getName(), client);
		}

		Collection<Client> shrunkenClients = spojo.shrink(clientMap.values(), CLIENT_NAMESPACE + SOME_2_NULL + EXCLUDE);

		@SuppressWarnings("unchecked")
		Matcher<String> anyOf = anyOf(is("1"), is("2"), is("3"), is("4"), is("5"));
		for (Client shrunkenClient : shrunkenClients) {
			assertNotSame(clientMap.get(shrunkenClient.getName()), shrunkenClient);
			assertThat(shrunkenClient.getName(), anyOf);
			assertEquals(MAN, shrunkenClient.getGender());
			assertEquals(birthday, shrunkenClient.getBirthday());
			assertNull(shrunkenClient.getStreet());

		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.util.Collection, java.lang.String)}.
	 */
	@Test
	public void testShrinkCollectionInclude1() {
		Date birthday = new Date();
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			String counter = Integer.toString(i);
			Client client = new Client(counter, WOMAN, birthday, counter);
			clientMap.put(client.getName(), client);
		}

		Collection<Client> shrunkenClients = spojo.shrink(clientMap.values(), CLIENT_NAMESPACE + EMPTY + INCLUDE);

		for (Client shrunkenClient : shrunkenClients) {
			assertNotSame(clientMap.get(shrunkenClient.getName()), shrunkenClient);
			assertNull(shrunkenClient.getName());
			assertNull(shrunkenClient.getGender());
			assertNull(shrunkenClient.getBirthday());
			assertNull(shrunkenClient.getStreet());
		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.util.Collection, java.lang.String)}.
	 */
	@Test
	public void testShrinkCollectionInclude2() {
		Date birthday = new Date();
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			String counter = Integer.toString(i);
			Client client = new Client(counter, WOMAN, birthday, counter);
			clientMap.put(client.getName(), client);
		}

		Collection<Client> shrunkenClients = spojo.shrink(clientMap.values(), CLIENT_NAMESPACE + NAME_GENDER + INCLUDE);

		@SuppressWarnings("unchecked")
		Matcher<String> anyOf = anyOf(is("1"), is("2"), is("3"), is("4"), is("5"));
		for (Client shrunkenClient : shrunkenClients) {
			assertNotSame(clientMap.get(shrunkenClient.getName()), shrunkenClient);
			assertThat(shrunkenClient.getName(), anyOf);
			assertEquals(Gender.WOMAN, shrunkenClient.getGender());
			assertNull(shrunkenClient.getBirthday());
			assertNull(shrunkenClient.getStreet());
		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.util.Collection, java.lang.String)}.
	 */
	@Test
	public void testShrinkCollectionInclude3() {
		Date birthday = new Date();
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			String counter = Integer.toString(i);
			Client client = new Client(counter, MAN, birthday, counter);
			clientMap.put(client.getName(), client);
		}

		Collection<Client> shrunkenClients = spojo.shrink(clientMap.values(), CLIENT_NAMESPACE + SOME_2_NULL + INCLUDE);

		@SuppressWarnings("unchecked")
		Matcher<String> anyOf = anyOf(is("1"), is("2"), is("3"), is("4"), is("5"));
		for (Client shrunkenClient : shrunkenClients) {
			assertNotSame(clientMap.get(shrunkenClient.getName()), shrunkenClient);
			assertNull(shrunkenClient.getName());
			assertNull(shrunkenClient.getGender());
			assertNull(shrunkenClient.getBirthday());
			assertThat(shrunkenClient.getStreet(), anyOf);
		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.util.Collection, java.lang.String)}.
	 */
	@Test
	public void testShrinkCollectionNullSource() {
		Collection<Object> shrunkenCollection = spojo.shrink(null, CLIENT_NAMESPACE + NAME_GENDER + INCLUDE);
		assertNull(shrunkenCollection);
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.util.Collection, java.lang.String)}.
	 */
	@Test
	public void testShrinkListNullSource() {
		List<Object> shrunkenCollection = spojo.shrink(null, CLIENT_NAMESPACE + NAME_GENDER + INCLUDE);
		assertNull(shrunkenCollection);
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.util.Collection, java.lang.String)}.
	 */
	@Test
	public void testShrinkSetNullSource() {
		Set<Object> shrunkenCollection = spojo.shrink(null, CLIENT_NAMESPACE + NAME_GENDER + INCLUDE);
		assertNull(shrunkenCollection);
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.util.Collection, java.lang.String)}.
	 */
	@Test(expected = RuleNotFoundException.class)
	public void testShrinkCollectionExcludeNullFilterName() {
		Date birthday = new Date();
		Collection<Client> clients = new ArrayList<Client>();

		for (int i = 1; i <= 5; i++) {
			String counter = Integer.toString(i);
			clients.add(new Client(counter, MAN, birthday, counter));
		}

		Collection<Client> shrunkenCollection = spojo.shrink(clients, null);
		assertNull(shrunkenCollection);
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrinkList(java.util.List, java.lang.String)}.
	 */
	@Test
	public void testShrinkListExclude1() {
		Date birthday = new Date();
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			String counter = Integer.toString(i);
			Client client = new Client(counter, WOMAN, birthday, counter);
			clientMap.put(client.getName(), client);
		}

		List<Client> shrunkenClients = spojo
				.shrink(new ArrayList<Client>(clientMap.values()), CLIENT_NAMESPACE + EMPTY + EXCLUDE);

		@SuppressWarnings("unchecked")
		Matcher<String> anyOf = anyOf(is("1"), is("2"), is("3"), is("4"), is("5"));
		for (Client shrunkenClient : shrunkenClients) {
			assertNotSame(clientMap.get(shrunkenClient.getName()), shrunkenClient);
			assertThat(shrunkenClient.getName(), anyOf);
			assertEquals(WOMAN, shrunkenClient.getGender());
			assertEquals(birthday, shrunkenClient.getBirthday());
			assertThat(shrunkenClient.getStreet(), anyOf);
		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrinkSet(java.util.Set, java.lang.String)}.
	 */
	@Test
	public void testShrinkSet() {
		Date birthday = new Date();
		Set<Client> clients = new HashSet<Client>();
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			String counter = Integer.toString(i);
			Client client = new Client(counter, WOMAN, birthday, counter);
			clients.add(client);
			clientMap.put(client.getName(), client);
		}

		Set<Client> shrunkenClients = spojo.shrink(clients, CLIENT_NAMESPACE + EMPTY + EXCLUDE);

		@SuppressWarnings("unchecked")
		Matcher<String> anyOf = anyOf(is("1"), is("2"), is("3"), is("4"), is("5"));
		for (Client shrunkenClient : shrunkenClients) {
			assertNotSame(clientMap.get(shrunkenClient.getName()), shrunkenClient);
			assertThat(shrunkenClient.getName(), anyOf);
			assertEquals(WOMAN, shrunkenClient.getGender());
			assertEquals(birthday, shrunkenClient.getBirthday());
			assertThat(shrunkenClient.getStreet(), anyOf);
		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test(expected = RuleException.class)
	public void testShrinkTStringDisabled1() {
		Date birthday = new Date();
		Client client = new Client("name1", UNDEFINED, birthday, "street1");
		spojo.shrink(client, CLIENT_NAMESPACE + EMPTY + DISABLED);

		fail("A RuleException must be thrown!");
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testShrinkTStringExclude1() {
		Date birthday = new Date();
		Client client = new Client("name", UNDEFINED, birthday, "street");
		Client shrunkenClient = spojo.shrink(client, CLIENT_NAMESPACE + NAME_GENDER + EXCLUDE);

		assertNull(shrunkenClient.getName());
		assertNull(shrunkenClient.getGender());
		assertEquals(birthday, shrunkenClient.getBirthday());
		assertEquals("street", shrunkenClient.getStreet());
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testShrinkTStringExclude2() {
		Date birthday = new Date();
		Client client = new Client("name", UNDEFINED, birthday, "street");
		Client shrunkenClient = spojo.shrink(client, CLIENT_NAMESPACE + SOME_2_NULL + EXCLUDE);

		assertEquals("name", shrunkenClient.getName());
		assertEquals(UNDEFINED, shrunkenClient.getGender());
		assertEquals(birthday, shrunkenClient.getBirthday());
		assertNull(shrunkenClient.getStreet());
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testShrinkTStringExclude3() {
		Date birthday = new Date();
		Client client = new Client("name", UNDEFINED, birthday, "street");
		Client shrunkenClient = spojo.shrink(client, CLIENT_NAMESPACE + EMPTY + EXCLUDE);

		assertEquals("name", shrunkenClient.getName());
		assertEquals(UNDEFINED, shrunkenClient.getGender());
		assertEquals(birthday, shrunkenClient.getBirthday());
		assertEquals("street", shrunkenClient.getStreet());
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.util.Collection, java.lang.String)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testShrinkTStringExclude4() {
		Date birthday = new Date();
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			String counter = Integer.toString(i);
			Client client = new Client(counter, WOMAN, birthday, counter);
			clientMap.put(client.getName(), client);
		}

		Collection<Client> shrunkenClients = spojo.shrink(clientMap.values(), CLIENT_NAMESPACE + NAME_GENDER + EXCLUDE);

		Matcher<String> anyOf = anyOf(is("1"), is("2"), is("3"), is("4"), is("5"));
		for (Client shrunkenClient : shrunkenClients) {
			assertNotSame(clientMap.get(shrunkenClient.getName()), shrunkenClient);
			assertNull(shrunkenClient.getName());
			assertNull(shrunkenClient.getGender());
			assertEquals(birthday, shrunkenClient.getBirthday());
			assertThat(shrunkenClient.getStreet(), anyOf);
		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testShrinkTStringExclude5() {
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			Client client = new Client("name" + i, getRandomGender(), new Date(), "street" + i);
			clientMap.put(client.getName(), client);
		}

		Chef chef = new Chef("ChefName", WOMAN, new Date(), "ChefStreet");
		Shop shop = new Shop(15L, "shopName", "shopType", chef, clientMap.values());

		Shop shrunkenShop = spojo.shrink(shop, SHOP_NAMESPACE + EMPTY + EXCLUDE);

		assertNotSame(shop, shrunkenShop);
		assertEquals(shop.getId(), shrunkenShop.getId());
		assertEquals(shop.getType(), shrunkenShop.getType());
		assertEquals(shop.getName(), shrunkenShop.getName());

		assertNotSame(shop.getChef(), shrunkenShop.getChef());
		assertEquals(shop.getChef().getName(), shrunkenShop.getChef().getName());
		assertEquals(WOMAN, shrunkenShop.getChef().getGender());
		assertEquals(shop.getChef().getBirthday(), shrunkenShop.getChef().getBirthday());
		assertEquals(shop.getChef().getStreet(), shrunkenShop.getChef().getStreet());

		for (Client shrunkenClient : shrunkenShop.getClients()) {
			Client unexpected = clientMap.get(shrunkenClient.getName());
			assertNotSame(unexpected, shrunkenClient);
			assertEquals(unexpected.getName(), shrunkenClient.getName());
			assertEquals(unexpected.getGender(), shrunkenClient.getGender());
			assertEquals(unexpected.getBirthday(), shrunkenClient.getBirthday());
			assertEquals(unexpected.getStreet(), shrunkenClient.getStreet());
		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testShrinkTStringExclude6() {
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			Client client = new Client("name" + i, getRandomGender(), new Date(), "street" + i);
			clientMap.put(client.getName(), client);
		}

		Chef chef = new Chef("ChefName", UNDEFINED, new Date(), "ChefStreet");
		Shop shop = new Shop(15L, "shopName", "shopType", chef, clientMap.values());

		Shop shrunkenShop = spojo.shrink(shop, SHOP_NAMESPACE + EXCLUDE);

		// @formatter:off
		//			"id", "name", "type",
		//			"clients.street", "clients.gender",
		//			"chef.name", "chef.birthday", "chef.gender"
		// @formatter:on

		assertNotSame(shop, shrunkenShop);
		assertNull(shrunkenShop.getId());
		assertNull(shrunkenShop.getType());
		assertNull(shrunkenShop.getName());

		assertNotSame(shop.getChef(), shrunkenShop.getChef());
		assertNull(shrunkenShop.getChef().getName());
		assertNull(shrunkenShop.getChef().getGender());
		assertNull(shrunkenShop.getChef().getBirthday());
		assertEquals(shop.getChef().getStreet(), shrunkenShop.getChef().getStreet());

		for (Client shrunkenClient : shrunkenShop.getClients()) {
			Client unexpected = clientMap.get(shrunkenClient.getName());
			assertNotSame(unexpected, shrunkenClient);
			assertEquals(unexpected.getName(), shrunkenClient.getName());
			assertNull(shrunkenClient.getGender());
			assertEquals(unexpected.getBirthday(), shrunkenClient.getBirthday());
			assertNull(shrunkenClient.getStreet());
		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testShrinkTStringInclude1() {
		Client client = new Client("name", UNDEFINED, new Date(), "street");
		Client shrunkenClient = spojo.shrink(client, CLIENT_NAMESPACE + NAME_GENDER + INCLUDE);

		assertEquals("name", shrunkenClient.getName());
		assertEquals(Gender.UNDEFINED, shrunkenClient.getGender());
		assertNull(shrunkenClient.getBirthday());
		assertNull(shrunkenClient.getStreet());
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testShrinkTStringInclude2() {
		Client client = new Client("name", UNDEFINED, new Date(), "street");
		Client shrunkenClient = spojo.shrink(client, CLIENT_NAMESPACE + SOME_2_NULL + INCLUDE);

		assertNotSame(client, shrunkenClient);
		assertNull(shrunkenClient.getName());
		assertNull(shrunkenClient.getGender());
		assertNull(shrunkenClient.getBirthday());
		assertEquals("street", shrunkenClient.getStreet());
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testShrinkTStringInclude3() {
		Client client = new Client("name", UNDEFINED, new Date(), "street");
		Client shrunkenClient = spojo.shrink(client, CLIENT_NAMESPACE + EMPTY + INCLUDE);

		assertNull(shrunkenClient.getName());
		assertNull(shrunkenClient.getGender());
		assertNull(shrunkenClient.getBirthday());
		assertNull(shrunkenClient.getStreet());
	}

	private Gender getRandomGender() {
		Gender[] values = Gender.values();
		return values[new Random().nextInt(values.length)];
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrinkCollection(java.util.Collection, java.lang.String)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testShrinkTStringInclude4() {
		Date birthday = new Date();
		Map<String, Client> clientMap = new HashMap<String, Client>();

		for (int i = 1; i <= 5; i++) {
			String counter = Integer.toString(i);
			Client client = new Client(counter, WOMAN, birthday, counter);
			clientMap.put(client.getName(), client);
		}

		Collection<Client> shrunkenClients = spojo.shrink(clientMap.values(), CLIENT_NAMESPACE + NAME_GENDER + INCLUDE);

		Matcher<String> anyOf = anyOf(is("1"), is("2"), is("3"), is("4"), is("5"));
		for (Client shrunkenClient : shrunkenClients) {
			assertNotSame(clientMap.get(shrunkenClient.getName()), shrunkenClient);
			assertThat(shrunkenClient.getName(), anyOf);
			assertEquals(Gender.WOMAN, shrunkenClient.getGender());
			assertNull(shrunkenClient.getBirthday());
			assertNull(shrunkenClient.getStreet());
		}
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testShrinkTStringInclude5() {
		List<Client> clients = new ArrayList<Client>();
		for (int i = 1; i <= 5; i++) {
			clients.add(new Client("name" + i, getRandomGender(), new Date(), "street" + i));
		}

		Chef chef = new Chef("ChefName", WOMAN, new Date(), "ChefStreet");
		Shop shop = new Shop(15L, "shopName", "shopType", chef, clients);

		Shop shrunkenShop = spojo.shrink(shop, CLIENT_NAMESPACE + EMPTY + INCLUDE);

		assertNotSame(shop, shrunkenShop);
		assertNull(shrunkenShop.getId());
		assertNull(shrunkenShop.getType());
		assertNull(shrunkenShop.getName());
		assertNull(shrunkenShop.getChef());
		assertNull(shrunkenShop.getClients());
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test(expected = RuleNotFoundException.class)
	public void testShrinkTStringNullFilterName() {
		Date birthday = new Date();
		Client client = new Client("name", UNDEFINED, birthday, "street");

		spojo.shrink(client, null);

		fail("A RuleException must be thrown!");
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testShrinkTStringNullSource() {
		Client shrunkenClient = spojo.shrink(null, CLIENT_NAMESPACE + NAME_GENDER + INCLUDE);
		assertNull(shrunkenClient);
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(Object, Object, String)}.
	 */
	@Test
	public void testShrinkTEStringNullSource1() {
		Date birthday = new Date();
		Client client = new Client("name", UNDEFINED, birthday, "street");
		Client shrunkenClient = spojo.shrink(client, null, CLIENT_NAMESPACE + NAME_GENDER + INCLUDE);
		assertNull(shrunkenClient);
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(Object, Object, String)}.
	 */
	@Test
	public void testShrinkTEStringNullSource2() {
		Date birthday = new Date();
		Client client = new Client("name", UNDEFINED, birthday, "street");
		Client shrunkenClient = spojo.shrink(null, client, CLIENT_NAMESPACE + NAME_GENDER + INCLUDE);
		assertEquals(client, shrunkenClient);
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(Object, Object, String)}.
	 */
	@Test
	public void testShrinkTEStringNullSource3() {
		Client shrunkenClient = spojo.shrink(null, null, SHOP_NAMESPACE + NAME_GENDER + INCLUDE);
		assertNull(shrunkenClient);
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(Object, Object, String)}.
	 */
	@Test
	public void testShrinkTEStringSource1() {
		Date birthday = new Date();
		Client client = new Client("name", UNDEFINED, birthday, "street");
		Employee shrunkenEmployee = spojo.shrink(client, new Employee(), CLIENT_NAMESPACE + NAME_GENDER + INCLUDE);
		assertNotNull(shrunkenEmployee);
		assertEquals(client.getName(), shrunkenEmployee.getName());
		assertEquals(client.getGender(), shrunkenEmployee.getGender());
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(Object, Object, String)}.
	 */
	@Test(expected = SpojoException.class)
	public void testShrinkTEStringNull() {
		Spojo spojoTmp = new Spojo(null);
		spojoTmp.shrink(new Object(), new Object(), SHOP_NAMESPACE + NAME_GENDER + INCLUDE);
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(Object, Object, String)}.
	 */
	@Test
	public void testShrinkTEStringNull2() {
		Object target = new Object();
		Object shrinkObject = spojo.shrink(new ArrayList<Object>(), target, SHOP_NAMESPACE + NAME_GENDER + INCLUDE);
		assertSame(target, shrinkObject);
	}

	/**
	 * Test method for {@link com.github.sworm.spojo.Spojo#shrink(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public void testShrinkTStringUnDerivableSource() {
		List<Integer> unDerivableClass = new ArrayList<Integer>();
		List<Integer> shrunkenUnDerivableClass = spojo.shrink(unDerivableClass, SHOP_NAMESPACE + EMPTY);
		assertSame(unDerivableClass, shrunkenUnDerivableClass);
	}
}

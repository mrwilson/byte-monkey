/**
 * 
 */
package com.github.sworm.spojo.model;

import java.util.Date;

/**
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
public class Person extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3077631154911392741L;

	private String name = null;
	private Gender gender = null;
	private Date birthday = null;
	private String street = null;

	/**
	 * 
	 */
	public Person() {
	}

	/**
	 * @param name
	 * @param gender
	 * @param birthday
	 * @param street
	 */
	public Person(final String name, final Gender gender, final Date birthday, final String street) {
		this.name = name;
		this.gender = gender;
		this.birthday = birthday;
		this.street = street;
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
	 * @return the birthday
	 */
	public Date getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday
	 *            the birthday to set
	 */
	public void setBirthday(final Date birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @param street
	 *            the street to set
	 */
	public void setStreet(final String street) {
		this.street = street;
	}

	@Override
	public String toString() {
		return String.format("%s [name=%s, gender=%s, birthday=%s, street=%s]", getClass().getSimpleName(), getName(),
				getGender(), getBirthday(), getStreet());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Person other = (Person) obj;
		if (birthday == null) {
			if (other.birthday != null) {
				return false;
			}
		} else if (!birthday.equals(other.birthday)) {
			return false;
		}
		if (gender != other.gender) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (street == null) {
			if (other.street != null) {
				return false;
			}
		} else if (!street.equals(other.street)) {
			return false;
		}
		return true;
	}

}

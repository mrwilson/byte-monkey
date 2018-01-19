/**
 * 
 */
package com.github.sworm.spojo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
public class Shop extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1169964539131430644L;

	private Long id = null;

	private String name = null;

	private String type = null;

	private Chef chef = null;

	private List<Client> clients = null;

	public Shop() {
	}

	public Shop(final Long id, final String name, final String type, final Chef chef) {
		this(id, name, type, chef, new ArrayList<Client>());
	}

	public Shop(final Long id, final String name, final String type, final Chef chef, Collection<Client> clients) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.chef = chef;
		if (clients != null) {
			this.clients = new ArrayList<Client>(clients);
		}
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final Long id) {
		this.id = id;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * @return the chef
	 */
	public Chef getChef() {
		return chef;
	}

	/**
	 * @param chef
	 *            the chef to set
	 */
	public void setChef(final Chef chef) {
		this.chef = chef;
	}

	/**
	 * @return the clients
	 */
	public List<Client> getClients() {
		return clients;
	}

	/**
	 * @param clients
	 *            the clients to set
	 */
	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	@Override
	public String toString() {
		return String.format("Shop [id=%s, name=%s, type=%s, chef=%s, clients=%s]", getId(), getName(), getType(), getChef(),
				getClients());
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
		Shop other = (Shop) obj;
		if (chef == null) {
			if (other.chef != null) {
				return false;
			}
		} else if (!chef.equals(other.chef)) {
			return false;
		}
		if (clients == null) {
			if (other.clients != null) {
				return false;
			}
		} else if (!clients.equals(other.clients)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

}

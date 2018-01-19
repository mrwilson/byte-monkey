/**
 * 
 */
package com.github.sworm.spojo.model;

import java.io.Serializable;

/**
 * @author Vincent Palau
 * @Since Feb 27, 2011
 * 
 */
public abstract class BaseModel implements Serializable {

	private static final long serialVersionUID = 7184906489246117967L;

	public abstract boolean equals(Object obj);
}

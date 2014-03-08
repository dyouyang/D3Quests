/**
 * 
 */
package com.dyo.d3quests;

/**
 * @author davido
 *
 */
public class Quest {
	
	String slug;
	String name;
	/**
	 * 
	 */
	public Quest(String slug, String name) {
		this.slug = slug;
		this.name = name;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}

}

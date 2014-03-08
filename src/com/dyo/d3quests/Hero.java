/**
 * 
 */
package com.dyo.d3quests;

/**
 * @author davido
 *
 */
public class Hero {

	String name;
	int id;
	/**
	 * 
	 */
	public Hero(int id, String name) {
			this.id = id;
			this.name = name;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + id;
	}
	
}

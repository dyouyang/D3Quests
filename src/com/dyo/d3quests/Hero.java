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
	int level;
	String d3class;
	/**
	 * 
	 */
	public Hero(int id, String name, int level, String d3class) {
			this.id = id;
			this.name = name;
			this.level = level;
			this.d3class = d3class;
	}
	@Override
	public String toString() {
		return String.format("%s (%d %s)", name, level, d3class.replace("-", " "));
	}
	
}

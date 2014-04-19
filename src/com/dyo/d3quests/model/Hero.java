/**
 *
 */
package com.dyo.d3quests.model;

/**
 * @author davido
 *
 */
public class Hero {

	private String name;
	private String id;
	private int level;
	private String d3class;
	/**
	 *
	 */
	public Hero(String id, String name, int level, String d3class) {
			this.setId(id);
			this.setName(name);
			this.setLevel(level);
			this.setD3class(d3class);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getD3class() {
		return d3class;
	}
	public void setD3class(String d3class) {
		this.d3class = d3class;
	}
	@Override
	public String toString() {
		return String.format("%s (%d %s)", getName(), getLevel(), getD3class().replace("-", " "));
	}

}

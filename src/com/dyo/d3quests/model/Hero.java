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
	private int gender; // Male = 0, Female = 1;
	private String d3class;
	/**
	 * @param gender
	 *
	 */
	public Hero(String id, String name, int level, int gender, String d3class) {
			this.setId(id);
			this.setName(name);
			this.setLevel(level);
			this.setGender(gender);
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

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getGender() {
		return this.gender;
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

	public String getFormattedLevelAndClass() {
		return String.format("%d %s", getLevel(), getD3class().replace("-", " "));
	}

}

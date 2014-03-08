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
	boolean complete;
	/**
	 * 
	 */
	public Quest(String slug, String name) {
		this.slug = slug;
		this.name = name;
		this.complete = false;
	}
	@Override
	public String toString() {
		if(complete) {
			return name + " completed";
		} else {
			return name + " not completed";
		}
	}
	
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	@Override
	public boolean equals(Object o) {
	    if(o == null)                	return false;
	    if(!(o instanceof Quest)) 		return false;
	    Quest other = (Quest) o;
	    return this.slug.equalsIgnoreCase(other.slug);
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.slug.hashCode();
	}

}

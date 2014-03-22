/**
 * 
 */
package com.dyo.d3quests;

/**
 * @author yinglong
 *
 */
public class SavedHero {

	private long id;
	private String heroName;
	private String heroId;
	private String battleTagFull;
	private String region;
	private int heroLevel;
	private String heroClass;
	
	/**
	 * @param region 
	 * @param battletagFull 
	 * @param d3class 
	 * @param level 
	 * @param name 
	 * @param id 
	 * 
	 */
	public SavedHero(int id, String name, int level, String d3class, String battletagFull, String region) {
		this.heroId = Integer.toString(id);
		this.heroName = name;
		this.heroLevel = level;
		this.heroClass = d3class;
		this.battleTagFull = battletagFull;
		this.region = region;
	}

	public SavedHero() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHeroName() {
		return heroName;
	}

	public void setHeroName(String heroName) {
		this.heroName = heroName;
	}

	public String getHeroId() {
		return heroId;
	}

	public void setHeroId(String heroId) {
		this.heroId = heroId;
	}

	public String getBattleTagFull() {
		return battleTagFull;
	}

	public void setBattleTagFull(String battleTagFull) {
		this.battleTagFull = battleTagFull;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getHeroLevel() {
		return heroLevel;
	}

	public void setHeroLevel(int heroLevel) {
		this.heroLevel = heroLevel;
	}

	public String getHeroClass() {
		return heroClass;
	}

	public void setHeroClass(String heroClass) {
		this.heroClass = heroClass;
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", heroName, heroClass.replace("-", " "));
	}

	
}

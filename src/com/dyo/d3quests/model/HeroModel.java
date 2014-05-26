package com.dyo.d3quests.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author yinglong
 * Main model for quest completion.  Retrieved from D3 API and notifies all quest list
 * fragments when it is updated.
 */
public class HeroModel {

	private static HeroModel instance = null;

	private HashMap<String, ArrayList<Quest>> progression;
	private HashMap<String, Integer> stats;
	private boolean updated;

	private HeroModel() {
		setProgression(new HashMap<String, ArrayList<Quest>>());
		setStats(new HashMap<String, Integer>());
		setUpdated(false);
	}

	public static HeroModel getInstance() {
		if (instance == null) {
			instance = new HeroModel();
		}
		return instance;
	}

	/**
	 * @return the model is updated with quest data.
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * @param set quest data updated status
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	/**
	 * @return The list of quests completed hashed by act number.
	 */
	public HashMap<String, ArrayList<Quest>> getProgression() {
		return progression;
	}

	/**
	 * @param progression The hashmap containing quest completion data.
	 */
	public void setProgression(HashMap<String, ArrayList<Quest>> progression) {
		this.progression = progression;
	}

	public HashMap<String, Integer> getStats() {
		return stats;
	}

	public void setStats(HashMap<String, Integer> stats) {
		this.stats = stats;
	}

}

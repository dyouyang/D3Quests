package com.dyo.d3quests.model;

import java.util.ArrayList;
import java.util.HashMap;

public class CompletedQuests {
	
	private HashMap<String, ArrayList<Quest>> progression;
	private boolean updated;

	public CompletedQuests() {
		setProgression(new HashMap<String, ArrayList<Quest>>());
		setUpdated(false);
	}

	/**
	 * @return the updated
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	/**
	 * @return the progression
	 */
	public HashMap<String, ArrayList<Quest>> getProgression() {
		return progression;
	}

	/**
	 * @param progression the progression to set
	 */
	public void setProgression(HashMap<String, ArrayList<Quest>> progression) {
		this.progression = progression;
	}

}

/**
 * 
 */
package com.dyo.d3quests;

/**
 * @author yinglong
 *
 */
public class BattleTagConverter {

	/**
	 * 
	 */
	public BattleTagConverter() {
		// TODO Auto-generated constructor stub
	}
	
	public static String convertHumanToAPI(String battleTag) {
		return battleTag.replace('#', '-');
	}
	
	public static String convertAPIToHuman(String battleTag) {
		return battleTag.replace('-', '#');
	}

}

/**
 * 
 */
package com.dyo.d3quests;

/**
 * @author yinglong
 *
 */
public class APIUtils {

	/**
	 * 
	 */
	public APIUtils() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param region
	 * @param battleTag
	 * @param battleTagNum
	 * @return Ready to be called REST API URL
	 */
	public static String buildURL (String region, String battleTag, String battleTagNum) {
		return  String.format("http://%s.battle.net/api/d3/profile/%s-%s/",
	    		region, battleTag, battleTagNum);
	}
	
	public static String buildURL (String region, String battleTag, String battleTagNum, String heroId) {
		return  String.format("http://%s.battle.net/api/d3/profile/%s/hero/%d",
	    		region, battleTag, battleTagNum, heroId);
	}


	public static String convertHumanToAPI(String battleTag) {
		return battleTag.replace('#', '-');
	}
	
	public static String convertAPIToHuman(String battleTag) {
		return battleTag.replace('-', '#');
	}

}

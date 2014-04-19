package com.dyo.d3quests;

/**
 * @author yinglong
 *
 * Interface that activities using GetD3DataTask should implement in order
 * to have a callback method when the D3 API call is finished, so UI updates can occur.
 * 
 * @param <T> The listening activity.
 */
public interface D3TaskListener<T> {

	/**
	 * Callback called when API call is finished.
	 * @param result Results of API call
	 */
	public void onTaskFinished(String result);
}

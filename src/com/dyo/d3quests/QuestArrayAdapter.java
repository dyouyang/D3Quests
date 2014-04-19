/**
 * 
 */
package com.dyo.d3quests;

import java.util.List;

import com.dyo.d3quests.model.Quest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

/**
 * @author yinglong
 *
 */
public class QuestArrayAdapter extends ArrayAdapter<Quest> {

	public QuestArrayAdapter(Context context, int resource, List<Quest> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		Quest quest = getItem(position);
		((CheckedTextView)v).setChecked(quest.isComplete());
		return v;
	}

}

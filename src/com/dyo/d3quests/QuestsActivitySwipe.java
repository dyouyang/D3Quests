package com.dyo.d3quests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dyo.d3quests.model.CompletedQuests;
import com.dyo.d3quests.model.Quest;
import com.flurry.android.FlurryAgent;

public class QuestsActivitySwipe extends FragmentActivity implements
		ActionBar.TabListener, D3TaskListener {

	public static final int NUM_ACTS = 5;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	// Currently viewed hero.
	private static String heroId;
	private String name;
	private static String region;
	private static String battleTagFull;

	private static CompletedQuests completedQuests;
	private static boolean fullActCompleted[] = new boolean[5];

	private List<D3ModelUpdateListener> modelUpdateListeners;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_quests_activity_swipe);

		heroId = getIntent().getExtras().getString("heroId");
		name = getIntent().getExtras().getString("heroName");
		battleTagFull = getIntent().getExtras().getString("battleTagFull");
		region = getIntent().getExtras().getString("region");
		completedQuests = new CompletedQuests();
		modelUpdateListeners = new ArrayList<D3ModelUpdateListener>();
		setTitle(name);
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setBackgroundResource(R.drawable.background_tyrael);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		// Gets the URL from the UI's text field.
        String stringUrl = String.format("http://%s.battle.net/api/d3/profile/%s/hero/%s",
        		region, battleTagFull, heroId);
        ConnectivityManager connMgr = (ConnectivityManager)
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new GetD3DataTask(this, this).execute(stringUrl);
        } else {
        	Toast.makeText(this, "No network connection.", Toast.LENGTH_LONG).show();
        }
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_api_key));
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quests_activity_swipe, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_feedback:
	        Intent Email = new Intent(Intent.ACTION_SEND);
	        Email.setType("text/email");
	        Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "douyang@gmail.com" });
	        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
	        startActivity(Intent.createChooser(Email, "Send Feedback:"));
	        return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a SectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new SectionFragment();
			Bundle args = new Bundle();
			args.putInt(SectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			modelUpdateListeners.add((D3ModelUpdateListener) fragment);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 5 total pages.
			return NUM_ACTS;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * Each fragment corresponds to one act, and display the act's quests and
	 * total completion.
	 * @param <T>
	 */
	public static class SectionFragment extends Fragment implements D3ModelUpdateListener {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		private int currentAct;

		private final ArrayList<Quest> actList = new ArrayList<Quest>();
		QuestArrayAdapter questAdapter;
		ListView questListView;

		TextView fullCompleted;
		String fractionComplete;
		TextView tip;

		public SectionFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_quests_activity_swipe, container,
					false);
			currentAct = getArguments().getInt(ARG_SECTION_NUMBER);
			initAllQuests(currentAct);

			fullCompleted = (TextView) rootView.findViewById(R.id.section_label);
			questListView = (ListView) rootView.findViewById(R.id.quest_list);
		    questAdapter = new QuestArrayAdapter(this.getActivity(),
	    	        android.R.layout.simple_list_item_checked, actList);
	    	questListView.setAdapter(questAdapter);

	    	// Random tip at bottom of quest list.
	    	tip = (TextView) rootView.findViewById(R.id.quests_tip);
	    	Random r = new Random();
	    	int randomTip = r.nextInt(2);
	    	if (randomTip == 0) {
	    		tip.setText(getString(R.string.quest_tip_reset));
	    	} else if (randomTip == 1) {
	    		tip.setText(getString(R.string.quest_tip_delay));
	    	}

	    	// Calculate quest completion on initialize of this fragment.
	    	updateQuests();
			return rootView;
		}

		private void initAllQuests(int act) {
			actList.clear();
			if (act == 1) {
				actList.add(new Quest("the-fallen-star", "The Fallen Star"));
				actList.add(new Quest("the-legacy-of-cain", "The Legacy of Cain"));
				actList.add(new Quest("a-shattered-crown", "A Shattered Crown"));
				actList.add(new Quest("reign-of-the-black-king", "Reign of the Black King"));
				actList.add(new Quest("sword-of-the-stranger", "Sword of the Stranger"));
				actList.add(new Quest("the-broken-blade", "The Broken Blade"));
				actList.add(new Quest("the-doom-in-wortham", "The Doom in Wortham"));
				actList.add(new Quest("trailing-the-coven", "Trailing the Coven"));
				actList.add(new Quest("the-imprisoned-angel", "The Imprisoned Angel"));
				actList.add(new Quest("return-to-new-tristram", "Return to New Tristram"));
			}
			if (act == 2) {
				actList.add(new Quest("shadows-in-the-desert", "Shadows in the Desert"));
				actList.add(new Quest("the-road-to-alcarnus", "The Road to Alcarnus"));
				actList.add(new Quest("city-of-blood", "City of Blood"));
				actList.add(new Quest("a-royal-audience", "A Royal Audience"));
				actList.add(new Quest("unexpected-allies", "Unexpected Allies"));
				actList.add(new Quest("betrayer-of-the-horadrim", "Betrayer of the Horadrim"));
				actList.add(new Quest("blood-and-sand", "Blood and Sand"));
				actList.add(new Quest("the-black-soulstone", "The Black Soulstone"));
				actList.add(new Quest("the-scouring-of-caldeum", "The Scouring of Caldeum"));
				actList.add(new Quest("lord-of-lies", "Lord of Lies"));
			}
			if (act == 3) {
				actList.add(new Quest("the-siege-of-bastions-keep", "The Siege of Bastion's Keep"));
				actList.add(new Quest("turning-the-tide", "Turning the Tide"));
				actList.add(new Quest("the-breached-keep", "The Breached Keep"));
				actList.add(new Quest("tremors-in-the-stone", "Tremors in the Stone"));
				actList.add(new Quest("machines-of-war", "Machines of War"));
				actList.add(new Quest("siegebreaker", "Siegebreaker"));
				actList.add(new Quest("heart-of-sin", "Heart of Sin"));
			}
			if (act == 4) {
				actList.add(new Quest("fall-of-the-high-heavens", "Fall of the High Heavens"));
				actList.add(new Quest("the-light-of-hope", "The Light of Hope"));
				actList.add(new Quest("beneath-the-spire", "Beneath the Spire"));
				actList.add(new Quest("prime-evil", "Prime Evil"));
			}
			if (act == 5) {
				actList.add(new Quest("the-fall-of-westmarch", "The Fall of Westmarch"));
				actList.add(new Quest("souls-of-the-dead", "Souls of the Dead"));
				actList.add(new Quest("the-harbinger", "The Harbinger"));
				actList.add(new Quest("the-witch", "The Witch"));
				actList.add(new Quest("the-pandemonium-gate", "The Pandemonium Gate"));
				actList.add(new Quest("the-battlefields-of-eternity", "The Battlefields of Eternity"));
				actList.add(new Quest("breaching-the-fortress", "Breaching the Fortress"));
				actList.add(new Quest("angel-of-death", "Angel of Death"));
			}
		}

		public void updateQuests() {

				// If the model isn't updated yet, wait for listener
				// callback later to take care of it.  This usually occurs on the
				// first two acts due to ViewPager instantiating them on load.
				if (!completedQuests.isUpdated()) return;

				// Compare static all quests list with completed quests
				// returned from API.
				List<Quest> completed = completedQuests.getProgression().get("act"+currentAct);
				for (int i = 0; i < completed.size(); i++) {
					Quest thisQuest = completed.get(i);
					if (actList.contains(thisQuest)) {
						actList.get(actList.indexOf(thisQuest)).setComplete(true);
					}
				}

				// Calculate completed over total quests.
				if (completed.size() == actList.size()) {
					fullActCompleted[currentAct-1] = true;
				} else {
					fullActCompleted[currentAct-1] = false;
				}
				fractionComplete = completed.size() + "/" + actList.size();

				// Update quest list and completion summary at top.
				questAdapter.notifyDataSetChanged();
		    	if (fullActCompleted[currentAct-1]) {
		    		fullCompleted.setText(String.format("Act completed (%s)", fractionComplete));
		    	} else {
		    		if (fractionComplete != null) {
		    			fullCompleted.setText(String.format("Act not complete (%s)", fractionComplete));
		    		} else {
		    			fullCompleted.setText(getString(R.string.d3_api_maintenance));
		    		}
		    		fullCompleted.setTextColor(Color.RED);
		    	}
		}

		@Override
		public void onUpdateFinished() {

			// Callback when the completedQuests model is finished updating from API.
			updateQuests();
		}

	}

	@Override
	public void onTaskFinished(String result) {

		// Callback when JSON data from API has been retrieved, so we go ahead and populate
		// the model.  Send callbacks to the fragments when we're finished.
		try {
			JSONObject hero = new JSONObject(result);
			JSONObject progression = hero.getJSONObject("progression");

			Iterator<String> actsIter = progression.keys();
			while (actsIter.hasNext()) {
				String key = actsIter.next();
				JSONObject act = progression.getJSONObject(key);
				JSONArray quests = act.getJSONArray("completedQuests");

				ArrayList<Quest> questsList = new ArrayList<Quest>();
				for (int i = 0; i < quests.length(); i++) {
					JSONObject quest = quests.getJSONObject(i);
					String slug = quest.getString("slug");
					String name = quest.getString("name");
					Quest thisQuest = new Quest(slug, name);
					questsList.add(thisQuest);
				}
				completedQuests.getProgression().put(key, questsList);
			}
			completedQuests.setUpdated(true);
			for (D3ModelUpdateListener listener : modelUpdateListeners) {
				listener.onUpdateFinished();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Diablo 3 API error occurred.", Toast.LENGTH_SHORT).show();
		}

	}



}

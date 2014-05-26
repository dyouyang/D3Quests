package com.dyo.d3quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dyo.d3quests.model.HeroModel;
import com.dyo.d3quests.model.Quest;

public class StatsActivity extends Activity implements ActionBar.TabListener, D3TaskListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private static String heroId;
	private static String name;
	private static String region;
	private static String battleTagFull;

	private static HeroModel heroModel;
	private static List<D3ModelUpdateListener> modelUpdateListeners;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		heroId = getIntent().getExtras().getString("heroId");
		name = getIntent().getExtras().getString("heroName");
		battleTagFull = getIntent().getExtras().getString("battleTagFull");
		region = getIntent().getExtras().getString("region");

		setTitle(name);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
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

		heroModel = HeroModel.getInstance();
		modelUpdateListeners = new ArrayList<D3ModelUpdateListener>();

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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stats, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
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
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
			case 0:
				return StatsOverviewFragment.newInstance(position + 1);
			case 1:
				return StatsOffensiveFragment.newInstance(position + 1);
			default:
				return StatsOffensiveFragment.newInstance(position + 1);
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.stats_section1).toUpperCase(l);
			case 1:
				return getString(R.string.stats_section2).toUpperCase(l);
			case 2:
				return getString(R.string.stats_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class StatsOverviewFragment extends Fragment implements D3ModelUpdateListener{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static StatsOverviewFragment newInstance(int sectionNumber) {
			StatsOverviewFragment fragment = new StatsOverviewFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			modelUpdateListeners.add(fragment);
			return fragment;
		}

		public StatsOverviewFragment() {
		}

		TextView damageView;
		TextView strengthView;
		TextView dexterityView;
		TextView vitalityView;
		TextView intelligenceView;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_stats,
					container, false);

			damageView = (TextView) rootView.findViewById(R.id.damage_value);
			strengthView = (TextView) rootView.findViewById(R.id.strength_value);
			dexterityView = (TextView) rootView.findViewById(R.id.dexterity_value);
			vitalityView = (TextView) rootView.findViewById(R.id.vitality_value);
			intelligenceView = (TextView) rootView.findViewById(R.id.intelligence_value);

			Button quests = (Button)rootView.findViewById(R.id.quests);
			quests.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick (View view) {
					String item = ((TextView)view).getText().toString();

					Intent i = new Intent(view.getContext(), QuestsActivitySwipe.class);
					i.putExtra("heroId", heroId);
					i.putExtra("heroName", name);
					i.putExtra("battleTagFull", battleTagFull);
					i.putExtra("region", region);
					startActivity(i);
				}
			});

			updateStats();
			return rootView;
		}

		@Override
		public void onUpdateFinished() {
			updateStats();
		}

		private void updateStats() {
			// If the model isn't updated yet, wait for listener
			// callback later to take care of it.  This usually occurs on the
			// first two acts due to ViewPager instantiating them on load.
			if (!heroModel.isUpdated()) return;

			HashMap<String, Integer> stats = heroModel.getStats();
        	damageView.setText(stats.get("damage").toString());
        	strengthView.setText(stats.get("strength").toString());
        	dexterityView.setText(stats.get("dexterity").toString());
        	vitalityView.setText(stats.get("vitality").toString());
        	intelligenceView.setText(stats.get("intelligence").toString());

		}

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class StatsOffensiveFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static StatsOffensiveFragment newInstance(int sectionNumber) {
			StatsOffensiveFragment fragment = new StatsOffensiveFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public StatsOffensiveFragment() {
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_stats_offense,
					container, false);
			return rootView;
		}

	}

	@Override
	public void onTaskFinished(String result) {
		// Callback when JSON data from API has been retrieved, so we go ahead and populate
		// the model.  Send callbacks to the fragments when we're finished.
		try {
			JSONObject hero = new JSONObject(result);

			JSONObject jsonStats = hero.getJSONObject("stats");
			Iterator keys = jsonStats.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				heroModel.getStats().put(key, jsonStats.getInt(key));
			}

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
				heroModel.getProgression().put(key, questsList);
			}
			heroModel.setUpdated(true);
			for (D3ModelUpdateListener listener : modelUpdateListeners) {
				listener.onUpdateFinished();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Diablo 3 API error occurred.", Toast.LENGTH_SHORT).show();
		}

	}

}

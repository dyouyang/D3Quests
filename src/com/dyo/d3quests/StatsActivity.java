package com.dyo.d3quests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v13.app.FragmentPagerAdapter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StatsActivity extends Activity implements ActionBar.TabListener {

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

	private static int heroId;
	private static String name;
	private static String region;
	private static String battleTagFull;
	
	private Button questsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		heroId = getIntent().getExtras().getInt("heroId");
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
	public static class StatsOverviewFragment extends Fragment {
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
			return fragment;
		}

		public StatsOverviewFragment() {
		}

		Map<String, Integer> stats;
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
			
	        String stringUrl = String.format("http://%s.battle.net/api/d3/profile/%s/hero/%d",
	        		region, battleTagFull, heroId);
	        ConnectivityManager connMgr = (ConnectivityManager) 
	            getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected()) {
	            new getD3DataTask().execute(stringUrl);
	        } else {
	        	Toast.makeText(getActivity(), "No network connection.", Toast.LENGTH_LONG).show();
	        }
	        
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
			//TextView textView = (TextView) rootView
			//		.findViewById(R.id.section_label);
			//textView.setText(Integer.toString(getArguments().getInt(
			//		ARG_SECTION_NUMBER)));
			return rootView;
		}
		
		private class getD3DataTask extends AsyncTask<String, Void, String> {
	        @Override
	        protected String doInBackground(String... urls) {
	              
	            // params comes from the execute() call: params[0] is the url.
	            try {
	                return downloadUrl(urls[0]);
	            } catch (IOException e) {
	                return "Unable to retrieve web page. URL may be invalid.";
	            }
	        }
	        // onPostExecute displays the results of the AsyncTask.
	        @Override
	        protected void onPostExecute(String result) {
	        	parseHero(result);
	        	damageView.setText(stats.get("damage").toString());
	        	strengthView.setText(stats.get("strength").toString());
	        	dexterityView.setText(stats.get("dexterity").toString());
	        	vitalityView.setText(stats.get("vitality").toString());
	        	intelligenceView.setText(stats.get("intelligence").toString());
	        	//heroName.setText(name);
	       }

	    }
	    
	    private String downloadUrl(String myurl) throws IOException {
	        InputStream is = null;
	        // Only display the first 500 characters of the retrieved
	        // web page content.
	        int len = 99999;
	            
	        try {
	            URL url = new URL(myurl);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setReadTimeout(10000 /* milliseconds */);
	            conn.setConnectTimeout(15000 /* milliseconds */);
	            conn.setRequestMethod("GET");
	            conn.setDoInput(true);
	            // Starts the query
	            conn.connect();
	            int response = conn.getResponseCode();
	            Log.d("D3", "The response is: " + response);
	            is = conn.getInputStream();

	            // Convert the InputStream into a string
	            String contentAsString = readIt(is, len);
	            return contentAsString;
	            
	        // Makes sure that the InputStream is closed after the app is
	        // finished using it.
	        } finally {
	            if (is != null) {
	                is.close();
	            } 
	        }
	    }
	    
	 public void parseHero(String result) {
		 	try {
				JSONObject hero = new JSONObject(result);
				//name = (String) hero.getString("name");
				JSONObject jsonStats = hero.getJSONObject("stats");
				stats = new HashMap<String, Integer>();
				Iterator keys = jsonStats.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					stats.put(key, jsonStats.getInt(key));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Reads an InputStream and converts it to a String.
	    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
	        BufferedReader reader = null;
	        reader = new BufferedReader(new InputStreamReader(stream, "UTF-8")); 
	        StringBuilder finalString = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	finalString.append(line);
	        }
	        return finalString.toString();
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

}

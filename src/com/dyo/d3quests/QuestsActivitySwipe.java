package com.dyo.d3quests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class QuestsActivitySwipe extends FragmentActivity implements
		ActionBar.TabListener {

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

	private static int heroId;
	private String name;
	private static String region;
	private TextView heroName;
	private ListView questListView;
	private ArrayAdapter<Quest> adapter;
	
	private ArrayList<Quest> act1List;
	private ArrayList<Quest> act2List;
	private ArrayList<Quest> act3List;
	private ArrayList<Quest> act4List;
	
	private static boolean fullActCompleted[] = new boolean[5];

	private static String battleTagFull;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quests_activity_swipe);

		heroId = getIntent().getExtras().getInt("heroId");
		name = getIntent().getExtras().getString("heroName");
		battleTagFull = getIntent().getExtras().getString("battleTagFull");
		region = getIntent().getExtras().getString("region");
		
		setTitle(name);
		// Set up the action bar.
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
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 5;
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
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		private ArrayList<Quest> act1List = new ArrayList<Quest>();
		QuestArrayAdapter adapter;
		private int act;
		ListView questListView;
		
		TextView fullCompleted;
		String fractionComplete;
		TextView tip;
		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_quests_activity_swipe_dummy, container,
					false);
			fullCompleted = (TextView) rootView.findViewById(R.id.section_label);
			//fullCompleted.setText(Integer.toString(getArguments().getInt(
					//ARG_SECTION_NUMBER)));
			act = getArguments().getInt(
					ARG_SECTION_NUMBER);
			initAllQuests(act);
			questListView = (ListView) rootView.findViewById(R.id.quest_list2);
		    adapter = new QuestArrayAdapter(this.getActivity(), 
	    	        android.R.layout.simple_list_item_checked, act1List);	
	    	questListView.setAdapter(adapter);
	    	
	    	tip = (TextView) rootView.findViewById(R.id.quests_tip);
	    	Random r = new Random();
	    	int randomTip = r.nextInt(2);
	    	if (randomTip == 1) {
	    		tip.setText("Note: The diablo 3 database has a small delay, so completion may not always be up to date.");
	    	}
	    	
			// Gets the URL from the UI's text field.
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
			return rootView;
		}
		
		private void initAllQuests(int act) {
			act1List.clear();
			if (act == 1) {
				act1List.add(new Quest("the-fallen-star", "The Fallen Star"));
				act1List.add(new Quest("the-legacy-of-cain", "The Legacy of Cain"));
				act1List.add(new Quest("a-shattered-crown", "A Shattered Crown"));
				act1List.add(new Quest("reign-of-the-black-king", "Reign of the Black King"));
				act1List.add(new Quest("sword-of-the-stranger", "Sword of the Stranger"));
				act1List.add(new Quest("the-broken-blade", "The Broken Blade"));
				act1List.add(new Quest("the-doom-in-wortham", "The Doom in Wortham"));
				act1List.add(new Quest("trailing-the-coven", "Trailing the Coven"));
				act1List.add(new Quest("the-imprisoned-angel", "The Imprisoned Angel"));
				act1List.add(new Quest("return-to-new-tristram", "Return to New Tristram"));
			}
			if (act == 2) {
				// TODO: What's up with blood and sand? doesn't come back in API
				act1List.add(new Quest("shadows-in-the-desert", "Shadows in the Desert"));
				act1List.add(new Quest("the-road-to-alcarnus", "The Road to Alcarnus"));
				act1List.add(new Quest("city-of-blood", "City of Blood"));
				act1List.add(new Quest("a-royal-audience", "A Royal Audience"));
				act1List.add(new Quest("unexpected-allies", "Unexpected Allies"));
				act1List.add(new Quest("betrayer-of-the-horadrim", "Betrayer of the Horadrim"));
				act1List.add(new Quest("blood-and-sand", "Blood and Sand"));
				act1List.add(new Quest("the-black-soulstone", "The Black Soulstone"));
				act1List.add(new Quest("the-scouring-of-caldeum", "The Scouring of Caldeum"));
				act1List.add(new Quest("lord-of-lies", "Lord of Lies"));
			}
			if (act == 3) {
				act1List.add(new Quest("the-siege-of-bastions-keep", "The Siege of Bastion's Keep"));
				act1List.add(new Quest("turning-the-tide", "Turning the Tide"));
				act1List.add(new Quest("the-breached-keep", "The Breached Keep"));
				act1List.add(new Quest("tremors-in-the-stone", "Tremors in the Stone"));
				act1List.add(new Quest("machines-of-war", "Machines of War"));
				act1List.add(new Quest("siegebreaker", "Siegebreaker"));
				act1List.add(new Quest("heart-of-sin", "Heart of Sin"));
			}
			if (act == 4) {
				act1List.add(new Quest("fall-of-the-high-heavens", "Fall of the High Heavens"));
				act1List.add(new Quest("the-light-of-hope", "The Light of Hope"));
				act1List.add(new Quest("beneath-the-spire", "Beneath the Spire"));
				act1List.add(new Quest("prime-evil", "Prime Evil"));
			}
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
	        	//heroName.setText(name);
	        	adapter.notifyDataSetChanged();
	        	
	        	if (act == 5) {
	        		fullCompleted.setText("Happy RoS! Unfortunately, the Diablo 3 database API does not yet report act 5 quest completion." +
	        				" Rest assured, the app will be updated ASAP when it does." +
	        				" In the meantime, enjoy the glorious new act!");
	        	}
	        	else if (fullActCompleted[act-1]) {
	        		fullCompleted.setText(String.format("Act completed (%s)", fractionComplete));
	        	} else {
	        		fullCompleted.setText(String.format("Act not complete (%s)", fractionComplete));
	        		fullCompleted.setTextColor(Color.RED);
	        	}
	        	// TODO: Handle updates better (without notifying).
	            //missingQuests.setText(heroesList.toString());

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
				JSONObject normalQuests = hero.getJSONObject("progress").getJSONObject("normal");
				JSONObject act1 = normalQuests.getJSONObject("act"+act);
				
				JSONArray quests1 = act1.getJSONArray("completedQuests");
				
				for (int i = 0; i < quests1.length(); i++) {
					JSONObject quest = quests1.getJSONObject(i);
					String slug = quest.getString("slug");
					String name = quest.getString("name");
					Quest thisQuest = new Quest(slug, name);
					if (act1List.contains(thisQuest)) {
						act1List.get(act1List.indexOf(thisQuest)).setComplete(true);
					}
				}
				
				if (quests1.length() == act1List.size()) {
					fullActCompleted[act-1] = true;
				} else {
					fullActCompleted[act-1] = false;
				}
				
				fractionComplete = quests1.length() + "/" + act1List.size();
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


	
}

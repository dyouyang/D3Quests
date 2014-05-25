package com.dyo.d3quests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dyo.d3quests.db.HeroesDataSource;
import com.dyo.d3quests.model.Hero;
import com.dyo.d3quests.model.SavedHero;
import com.flurry.android.FlurryAgent;

/**
 * @author yinglong
 *
 * Main activity of the app.  Allows user to input a battle tag to retrieve a list of heroes on the account.
 * Selecting a hero will open up the details activity.  Also has a side navigation menu (which is used to store
 * favorited heroes)
 */
public class HeroesActivity extends Activity implements OnNavigationListener, D3TaskListener<String> {

	// The UI consists of battle tag input, a button to get heroes,
	// and the list of heroes for an account.
	private AutoCompleteTextView battleTagInput;
	private EditText battleTagNumInput;
	private Button getHeroes;
	private ListView heroesView;

	// Variables related to an account.
	private String battleTag;
	private String battleTagNum;
	private String region = "us";
	// Layout at top of screen with battle tag input fields.
    private RelativeLayout battleTagInputLayout;

	ArrayList<Hero> heroesList = new ArrayList<Hero>();
	ArrayAdapter<Hero> heroesListAdapter;

	// Spinner for account region.
	SpinnerAdapter mSpinnerAdapter;
	private final String [] regions = {"us", "eu", "kr", "tw"};

	// Autocomplete previously entered battle tags.
	SharedPreferences settings;
	Set<String> recentAccounts;
	ArrayAdapter<String> adapterAutoComplete;
	private ArrayList<String> recentAccountsList;

	// Left navigation drawer.
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private ArrayAdapter<SavedHero> drawerAdapter;

	// Battle Tag shown in left navigation drawer.
    private LinearLayout drawerBattletag;
    private TextView drawerBattleTagLabel;

	// Datasource to access saved heroes DB.
	private HeroesDataSource datasource;

	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heroes);

		getWindow().setBackgroundDrawableResource(R.drawable.background_tyrael);
		// Set up action bar with spinner.
		setTitle("D3 Helper");
		actionBar = getActionBar();
		actionBar.setNavigationMode(getActionBar().NAVIGATION_MODE_LIST);
		mSpinnerAdapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.action_list,
		          android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);

		// Get saved heroes for navigation drawer.
		datasource = new HeroesDataSource(this);
		datasource.open();
		List<SavedHero> savedHeroes = datasource.getAllHeroes();

		// Set up navigation drawer for saved heroes.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
        		R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            @Override
			public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
			public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener.
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.addHeaderView(View.inflate(this, R.layout.drawer_header, null), null, false);

        drawerBattletag = (LinearLayout) findViewById(R.id.drawer_battletag);
        drawerBattleTagLabel = (TextView) findViewById(R.id.drawer_battletag_label);
        drawerBattletag.setOnClickListener(new OnClickListener() {

        	// When user taps on battle tag in left drawer, clear the list of heroes
        	// and show battle tag input view.
			@Override
			public void onClick(View v) {
				heroesListAdapter.clear();
				mDrawerLayout.closeDrawers();

			}
		});

        // Set the adapter for the list view
        drawerAdapter = new ArrayAdapter<SavedHero>(this, R.layout.drawer_list_item, savedHeroes);
        mDrawerList.setAdapter(drawerAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setOnItemLongClickListener(new DrawerItemLongClickListener());

        // Retrieve saved accounts from sharedprefs for battle tag autocomplete.
		settings = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
		recentAccounts = settings.getStringSet("recentAccounts", new HashSet<String>());
		recentAccountsList = new ArrayList<String>(recentAccounts);
		adapterAutoComplete = new ArrayAdapter<String>(
				this, android.R.layout.simple_dropdown_item_1line, recentAccountsList);

		// Input is the left input for account string, NumInput for code portion.
		battleTagInput = (AutoCompleteTextView) findViewById(R.id.battletag);
		battleTagInput.setAdapter(adapterAutoComplete);
		battleTagNumInput = (EditText) findViewById(R.id.battletag_num);
		battleTagInputLayout = (RelativeLayout) findViewById(R.id.top_layout);
		// Onclick for an autocomplete suggestion.
		battleTagInput.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {

					// If a suggestion is clicked, split the battle tag on #,
					// then insert the left and right parts.
					String item = ((TextView)v).getText().toString();
					String [] battleTagSplit = item.split("#");
					if (battleTagSplit.length == 2) {
						battleTagInput.setText(battleTagSplit[0]);
						battleTagNumInput.setText(battleTagSplit[1]);
					} else {
						// If somehow we don't get a string/num split, report error.
						Toast.makeText(getApplicationContext(), "Battle Tag error", Toast.LENGTH_SHORT).show();
					}
			}
		});

		// Fill in id from last button click from shared prefs.
		// Note, fields must be set explicity once first for auto-loading list on resume feature.
		battleTag = settings.getString("battleTag", "");
		battleTagInput.setText(battleTag);
		battleTagNum = settings.getString("battleTagNum", "");
		battleTagNumInput.setText(battleTagNum);
		// Default region to 0, or US
		region = regions[settings.getInt("region", 0)];
		actionBar.setSelectedNavigationItem(settings.getInt("region", 0));

		// Button to retrieve heroes from account.
		getHeroes = (Button) findViewById(R.id.get_heroes_button);
		getHeroes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                           InputMethodManager.HIDE_NOT_ALWAYS);

				// Remove focus from the autocompletetextview to prevent
				// flashing of the dropdown.
				getCurrentFocus().clearFocus();
				battleTag = battleTagInput.getText().toString();
				battleTag = battleTag.replaceAll("\\s",""); 	// Strip whitespace.
				battleTagInput.setText(battleTag);				// Replace stripped version back in.
				battleTagNum = battleTagNumInput.getText().toString();

				if (battleTag.length() < 1 || battleTagNum.length() < 1) {
					Toast.makeText(getApplicationContext(), "Enter Battle.net ID and 4 digit code", Toast.LENGTH_LONG).show();
					heroesListAdapter.clear();
					return;
				}

				// Save id.
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("battleTag", battleTag);
				editor.putString("battleTagNum", battleTagNum);
				editor.putInt("region", actionBar.getSelectedNavigationIndex());
				editor.commit();

				// Set the left navigation drawer battle tag label.
		        drawerBattleTagLabel.setText(battleTag + "#" + battleTagNum);

				// Gets the URL from the UI's text field.
		        String stringUrl = APIUtils.buildURL(region, battleTag, battleTagNum);
		        ConnectivityManager connMgr = (ConnectivityManager)
		            getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		        if (networkInfo != null && networkInfo.isConnected()) {
		            new GetD3DataTask(HeroesActivity.this, HeroesActivity.this).execute(stringUrl);
		        } else {
		            Toast.makeText(getApplicationContext(), "No network connection.", Toast.LENGTH_LONG).show();
		        }




			}
		});

		// List all heroes under the inputted account.
		heroesView = (ListView) findViewById(R.id.heroes_list);
    	heroesListAdapter = new HeroesArrayAdapter(this,
    	        R.layout.heroes_list_item, heroesList);
    	heroesView.setAdapter(heroesListAdapter);
    	heroesView.setEmptyView(battleTagInputLayout);
    	heroesView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				// Send the hero data over to quests activity.
				Intent i = new Intent(view.getContext(), QuestsActivitySwipe.class);
				Hero hero = heroesListAdapter.getItem(position);
				i.putExtra("heroId", hero.getId());
				i.putExtra("heroName", hero.getName());
				i.putExtra("battleTagFull", battleTag + "-" + battleTagNum);
				i.putExtra("region", region);
				startActivity(i);
			}
		});

    	heroesView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int position, long id) {
				Hero hero = heroesListAdapter.getItem(position);
				addSavedHero(hero.getId(), hero.getName(), hero.getLevel(), hero.getD3class(), battleTag + "-" + battleTagNum, region);
				return true;
			}
		});

    	// Auto load heroes on first app open if we have filled in the battle tag from settings.
    	if (battleTagInput.getText().toString().length() > 0
    			&& battleTagNumInput.getText().toString().length() > 0) {
    		getHeroes.post(new Runnable() {
				@Override
				public void run() {
					getHeroes.performClick();
				}
			});
    	}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Private Flurry API key, needs to be replaced with local own version.
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {

    	// Handle the drawer app icon.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

		switch (item.getItemId()) {
		case R.id.action_feedback:
	        Intent Email = new Intent(Intent.ACTION_SEND);
	        Email.setType("text/email");
	        Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "douyang@gmail.com" });
	        Email.putExtra(Intent.EXTRA_SUBJECT, "D3 Quest Helper Feedback");
	        startActivity(Intent.createChooser(Email, "Send Feedback:"));
	        return true;
	    }
		return false;
	}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void parseHeroes(String json) {
    	JSONObject profile;
    	try {
			profile = new JSONObject(json);

			JSONArray heroes = profile.getJSONArray("heroes");

			for(int i = 0; i < heroes.length(); i++) {
				JSONObject hero = heroes.getJSONObject(i);
				String heroName = hero.getString("name");
				int level = hero.getInt("level");
				int gender = hero.getInt("gender");
				String d3class = hero.getString("class");
				String heroId = hero.getString("id");
				heroesList.add(new Hero(heroId, heroName, level, gender, d3class));
			}

			addToRecentAccounts(battleTag + "#" + battleTagNum);

		} catch (JSONException e) {
			try {
				profile = new JSONObject(json);
				String error = profile.getString("reason");
				Toast.makeText(getApplicationContext(), error + "Double check your region and ID.", Toast.LENGTH_LONG).show();
			} catch (JSONException e1) {
				Toast.makeText(getApplicationContext(), "Unknown Diablo 3 API error", Toast.LENGTH_LONG).show();
				e1.printStackTrace();
			}

			e.printStackTrace();
		}

    }

    private void addToRecentAccounts(String account) {

    	if (!recentAccountsList.contains(account)) {
    		recentAccountsList.add(account);
    		// AutocompleteTextView makes a copy of the data when the adapter is created, so
    		// we must recreate the adapter each time.
    		adapterAutoComplete = new ArrayAdapter<String>(
    				this, android.R.layout.simple_dropdown_item_1line, recentAccountsList);
    		battleTagInput.setAdapter(adapterAutoComplete);

			SharedPreferences.Editor editor = settings.edit();
			editor.putStringSet("recentAccounts", new HashSet<String>(recentAccountsList));
			editor.commit();
    	}
	}

	private void addSavedHero(String id, String name, int level, String d3class, String battletagFull, String region) {
    	SavedHero newHero = new SavedHero(id, name, level, d3class, battletagFull, region);
    	List<SavedHero> queryResult = datasource.findHero(newHero);
    	if (queryResult.size() < 1) {
    		SavedHero hero = datasource.createSavedHero(newHero);
    		drawerAdapter.add(hero);
    		Toast.makeText(getApplicationContext(), hero.getHeroName() + " added to saved heroes.", Toast.LENGTH_SHORT).show();
    	} else {
    		Toast.makeText(getApplicationContext(), newHero + " already in saved heroes.", Toast.LENGTH_SHORT).show();
    	}
    	drawerAdapter.notifyDataSetChanged();
	}



	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		// Navigation item in action bar selects region for use in API calls
		switch(itemPosition) {
		case 0:
			region = "us";
			break;
		case 1:
			region = "eu";
			break;
		case 2:
			region = "kr";
			break;
		case 3:
			region = "tw";
			break;
		}
		return true;
	}

	/**
	 * @author yinglong
	 *
	 */
	private class DrawerItemClickListener implements OnItemClickListener {

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long arg3) {

			// Click on a saved hero opens the quest completion activity for that hero.
			SavedHero hero = (SavedHero) mDrawerList.getItemAtPosition(position);
			Intent i = new Intent(v.getContext(), QuestsActivitySwipe.class);
			i.putExtra("heroId", hero.getHeroId());
			i.putExtra("heroName", hero.getHeroName());
			i.putExtra("battleTagFull", hero.getBattleTagFull());
			i.putExtra("region", hero.getRegion());
			startActivity(i);
		}

	}

	/**
	 * @author yinglong
	 *
	 */
	public class DrawerItemLongClickListener implements OnItemLongClickListener {

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View v,
				int position, long arg3) {

			// Long click deletes the saved hero from the list and db.
			SavedHero hero = (SavedHero) mDrawerList.getItemAtPosition(position);
		    datasource.deleteSavedHero(hero);
		    drawerAdapter.remove(hero);
		    drawerAdapter.notifyDataSetChanged();
		    Toast.makeText(getApplicationContext(), hero.getHeroName() + " removed from saved heroes.", Toast.LENGTH_SHORT).show();
			return true;
		}

	}

	@Override
	public void onTaskFinished(String result) {

    	heroesListAdapter.clear();
    	parseHeroes(result);
    	heroesListAdapter.notifyDataSetChanged();
	}

}

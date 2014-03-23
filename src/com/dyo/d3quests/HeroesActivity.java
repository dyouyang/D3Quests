package com.dyo.d3quests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HeroesActivity extends Activity implements OnNavigationListener{


	private AutoCompleteTextView battleTagInput;
	private EditText battleTagNumInput;
	private Button findQuests;
	private ListView heroesView;
	
	private String battleTag;
	private String battleTagNum;
	HashMap<String, Integer> heroesMap = new HashMap<String, Integer>();
	ArrayList<Hero> heroesList = new ArrayList<Hero>();
	ArrayAdapter<Hero> adapter;
	
	ActionBar actionBar;
	SpinnerAdapter mSpinnerAdapter;
	private String region = "us";
	private String [] regions = {"us", "eu", "kr", "tw"};
	
	SharedPreferences settings;
	Set<String> recentAccounts;
	ArrayAdapter<String> adapterAutoComplete;
	private ArrayList<String> recentAccountsList;
	
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private ArrayAdapter<SavedHero> drawerAdapter;
	
	private HeroesDataSource datasource;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heroes);
		setTitle("D3 Helper");
		
		//recentAccounts = new LinkedHashMap<String, String>(5, (float) 0.75, true);
		actionBar = getActionBar();
		mSpinnerAdapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.action_list,
		          android.R.layout.simple_spinner_dropdown_item);
		
		actionBar.setNavigationMode(getActionBar().NAVIGATION_MODE_LIST);
		
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
		//actionBar.setDisplayShowTitleEnabled(false);
		
		datasource = new HeroesDataSource(this);
		datasource.open();
		
		List<SavedHero> savedHeroes = datasource.getAllHeroes();
		
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 
        		R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
        	
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.addHeaderView(View.inflate(this, R.layout.drawer_header, null), null, false);
 
        // Set the adapter for the list view
        drawerAdapter = new ArrayAdapter<SavedHero>(this, R.layout.drawer_list_item, savedHeroes);
        mDrawerList.setAdapter(drawerAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setOnItemLongClickListener(new DrawerItemLongClickListener());
        
        
		settings = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

		recentAccounts = settings.getStringSet("recentAccounts", new HashSet<String>());
		recentAccountsList = new ArrayList<String>(recentAccounts);
		adapterAutoComplete = new ArrayAdapter<String>(
				this, android.R.layout.simple_dropdown_item_1line, recentAccountsList);
			
		battleTagInput = (AutoCompleteTextView) findViewById(R.id.battletag);

		battleTagInput.setAdapter(adapterAutoComplete);

		battleTagNumInput = (EditText) findViewById(R.id.battletag_num);
		
		// Onclick for autocomplete suggestion.
		battleTagInput.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
					String item = ((TextView)v).getText().toString();
					String [] battleTagSplit = item.split("#");
					if (battleTagSplit.length == 2) {
						battleTagInput.setText(battleTagSplit[0]);
						battleTagNumInput.setText(battleTagSplit[1]);
					} else {
						Toast.makeText(getApplicationContext(), "Battle Tag error", Toast.LENGTH_SHORT).show();
					}
			}
			
		});

		// Fill in id from last button click.
		// Note, fields must be set explicity first for auto-loading list on resume feature.
		battleTag = settings.getString("battleTag", "");
		battleTagInput.setText(battleTag); 
		battleTagNum = settings.getString("battleTagNum", "");
		battleTagNumInput.setText(battleTagNum);
		region = regions[settings.getInt("region", 0)];
		actionBar.setSelectedNavigationItem(settings.getInt("region", 0));
		
		findQuests = (Button) findViewById(R.id.findQuests);
		findQuests.setOnClickListener(new OnClickListener() {
			
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
				battleTag = battleTag.replaceAll("\\s","");
				battleTagInput.setText(battleTag);
				battleTagNum = battleTagNumInput.getText().toString();
				
				if (battleTag.length() < 1 || battleTagNum.length() < 1) {
					Toast.makeText(getApplicationContext(), "Enter Battle.net ID and 4 digit code", Toast.LENGTH_LONG).show();
					adapter.clear();
					return;
				}
				
				// Save id.
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("battleTag", battleTag);
				editor.putString("battleTagNum", battleTagNum);
				editor.putInt("region", actionBar.getSelectedNavigationIndex());
				editor.commit();
				
				// Gets the URL from the UI's text field.
		        String stringUrl = String.format("http://%s.battle.net/api/d3/profile/%s-%s/",
		        		region, battleTag, battleTagNum);
		        ConnectivityManager connMgr = (ConnectivityManager) 
		            getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		        if (networkInfo != null && networkInfo.isConnected()) {
		            new getD3DataTask().execute(stringUrl);
		        } else {
		            Toast.makeText(getApplicationContext(), "No network connection.", Toast.LENGTH_LONG).show();
		        }
		        
				//missingQuests.setText(battleTag);				
			}
		});
		
		heroesView = (ListView) findViewById(R.id.missingQuests);
    	adapter = new ArrayAdapter<Hero>(this, 
    	        android.R.layout.simple_list_item_1, heroesList);	
    	heroesView.setAdapter(adapter);	
    	
    	heroesView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String item = ((TextView)view).getText().toString();
				
				Intent i = new Intent(view.getContext(), QuestsActivitySwipe.class);
				Hero hero = (Hero)adapter.getItem(position);
				i.putExtra("heroId", hero.id);
				i.putExtra("heroName", hero.name);
				i.putExtra("battleTagFull", battleTag + "-" + battleTagNum);
				i.putExtra("region", region);
				startActivity(i);
			}
		});
    	
    	heroesView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int position, long id) {
				Hero hero = (Hero)adapter.getItem(position);
				addSavedHero(hero.id, hero.name, hero.level, hero.d3class, battleTag + "-" + battleTagNum, region);
				return true;
			}
		});

    	// Auto load heroes on first app open
    	if (battleTagInput.getText().toString().length() > 0
    			&& battleTagNumInput.getText().toString().length() > 0) {
    		findQuests.post(new Runnable() {
				@Override
				public void run() {
					findQuests.performClick();
					
				}
			});
    		
    	}
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
	        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
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
 
	public class getD3DataTask extends AsyncTask<String, Void, String> {
    	ProgressDialog mProgress;

        @Override
		protected void onPreExecute() {
        	super.onPreExecute();
        	mProgress = new ProgressDialog(HeroesActivity.this);
        	mProgress.setMessage("Getting heroes...");
			mProgress.show();
			
		}
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
        	adapter.clear();
        	parseHeroes(result);
        	adapter.notifyDataSetChanged();
        	mProgress.hide();
        	// TODO: Handle updates better (without notifying).
            //missingQuests.setText(heroesList.toString());

       }

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
				String d3class = hero.getString("class");
				int heroId = hero.getInt("id");
				heroesList.add(new Hero(heroId, heroName, level, d3class));
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

	private void addSavedHero(int id, String name, int level, String d3class, String battletagFull, String region) {
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

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
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
			
			SavedHero hero = (SavedHero) mDrawerList.getItemAtPosition(position);
			
		    // Highlight the selected item, update the title, and close the drawer
		    //mDrawerList.setItemChecked(position, true);
		    //mDrawerLayout.closeDrawer(mDrawerList);
		    
			Intent i = new Intent(v.getContext(), QuestsActivitySwipe.class);
			i.putExtra("heroId", Integer.valueOf(hero.getHeroId()));
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
			SavedHero hero = (SavedHero) mDrawerList.getItemAtPosition(position);
			
		    // Highlight the selected item, update the title, and close the drawer
		    //mDrawerList.setItemChecked(position, true);
		    //mDrawerLayout.closeDrawer(mDrawerList);
		    
		    datasource.deleteSavedHero(hero);
		    drawerAdapter.remove(hero);
		    drawerAdapter.notifyDataSetChanged();
		    Toast.makeText(getApplicationContext(), hero.getHeroName() + " removed from saved heroes.", Toast.LENGTH_SHORT).show();
			return true;
		}

	}
	
}

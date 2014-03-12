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
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HeroesActivity extends Activity implements OnNavigationListener{

	private EditText battleTagInput;
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
	
	SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heroes);
		setTitle("D3 Helper");
		actionBar = getActionBar();
		mSpinnerAdapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.action_list,
		          android.R.layout.simple_spinner_dropdown_item);
		
		actionBar.setNavigationMode(getActionBar().NAVIGATION_MODE_LIST);
		
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
		//actionBar.setDisplayShowTitleEnabled(false);
		
		settings = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
		
		battleTagInput = (EditText) findViewById(R.id.battletag);
		battleTagNumInput = (EditText) findViewById(R.id.battletag_num);
		
		// Fill in id from last button click.
		battleTagInput.setText(settings.getString("battleTag", "")); 
		battleTagNumInput.setText(settings.getString("battleTagNum", ""));
		actionBar.setSelectedNavigationItem(settings.getInt("region", 0));
		
		findQuests = (Button) findViewById(R.id.findQuests);
		findQuests.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE); 

inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                           InputMethodManager.HIDE_NOT_ALWAYS);

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
				// TODO: Save region.
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
}

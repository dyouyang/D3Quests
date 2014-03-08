package com.dyo.d3quests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dyo.d3quests.HeroesActivity.getD3DataTask;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class QuestsActivity extends Activity {

	private int heroId;
	private String name;
	private TextView heroName;
	private ListView questListView;
	private ArrayAdapter<Quest> adapter;
	
	private ArrayList<Quest> act1List;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quests);
		// Show the Up button in the action bar.
		setupActionBar();
		
		heroId = getIntent().getExtras().getInt("heroId");
		heroName = (TextView)findViewById(R.id.hero_name);
		heroName.setText("" + heroId);
		act1List = new ArrayList<Quest>();
		questListView = (ListView) findViewById(R.id.quest_list);
		adapter = new ArrayAdapter<Quest>(this, 
    	        android.R.layout.simple_list_item_1, act1List);	
    	questListView.setAdapter(adapter);
		
		// Gets the URL from the UI's text field.
        String stringUrl = "http://us.battle.net/api/d3/profile/zzilong-1758/hero/" + heroId;
        ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new getD3DataTask().execute(stringUrl);
        } else {
            //missingQuests.setText("No network connection available.");
        }
 
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quests, menu);
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
		}
		return super.onOptionsItemSelected(item);
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
        	adapter.clear();
        	parseHero(result);
        	heroName.setText(name);
        	adapter.notifyDataSetChanged();
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
			name = (String) hero.getString("name");
			JSONObject normalQuests = hero.getJSONObject("progress").getJSONObject("normal");
			JSONObject act1 = normalQuests.getJSONObject("act1");
			JSONArray quests1 = act1.getJSONArray("completedQuests");
			for (int i = 0; i < quests1.length(); i++) {
				JSONObject quest = quests1.getJSONObject(i);
				String slug = quest.getString("slug");
				String name = quest.getString("name");
				act1List.add(new Quest(slug, name));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");        
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}

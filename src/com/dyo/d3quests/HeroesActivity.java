package com.dyo.d3quests;

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class HeroesActivity extends Activity {

	private EditText battleTagInput;
	private Button findQuests;
	private ListView heroesView;
	
	private String battleTag;
	HashMap<String, Integer> heroesMap = new HashMap<String, Integer>();
	ArrayList<Hero> heroesList = new ArrayList<Hero>();
	ArrayAdapter<Hero> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heroes);
		
		
		battleTagInput = (EditText) findViewById(R.id.battletag);
		
		findQuests = (Button) findViewById(R.id.findQuests);
		findQuests.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Gets the URL from the UI's text field.
		        String stringUrl = "http://us.battle.net/api/d3/profile/zzilong-1758/";
		        ConnectivityManager connMgr = (ConnectivityManager) 
		            getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		        if (networkInfo != null && networkInfo.isConnected()) {
		            new getD3DataTask().execute(stringUrl);
		        } else {
		            //missingQuests.setText("No network connection available.");
		        }
		        
				battleTag = battleTagInput.getText().toString();
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
				
				Intent i = new Intent(view.getContext(), QuestsActivity.class);
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
        	parseHeroes(result);
        	adapter.notifyDataSetChanged();
        	// TODO: Handle updates better (without notifying).
            //missingQuests.setText(heroesList.toString());

       }

    }
	
    private void parseHeroes(String json) {
    	try {
			JSONObject profile = new JSONObject(json);
			JSONArray heroes = profile.getJSONArray("heroes");
			
			for(int i = 0; i < heroes.length(); i++) {
				JSONObject hero = heroes.getJSONObject(i);
				String heroName = hero.getString("name");
				int heroId = hero.getInt("id");
				heroesList.add(new Hero(heroId, heroName));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");        
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}

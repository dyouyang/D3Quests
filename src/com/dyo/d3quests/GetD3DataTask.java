/**
 *
 */
package com.dyo.d3quests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author yinglong
 *
 * Generic task to call Blizzard's Diablo 3 REST API with a constructed URL. Returns the JSON response.
 */
public class GetD3DataTask extends AsyncTask<String, Void, String> {

	ProgressDialog mProgress;

	// Context and listener are both the activity that is initiating this task,
	// and so needs to show a progress dialog and be notified when the task
	// is finished.
	private final Context context;
	private final D3TaskListener<String> listener;

    public GetD3DataTask(Context context, D3TaskListener<String> listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
    	super.onPreExecute();
    	mProgress = new ProgressDialog(context);
    	mProgress.setMessage(context.getString(R.string.loading_dialog));
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

    // Hide the progress dialog and notify calling activity of result.
    @Override
    public void onPostExecute(String result) {
    	super.onPostExecute(result);
    	mProgress.hide();
    	listener.onTaskFinished(result);
	}

	private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

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
            String contentAsString = readIt(is);
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
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
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

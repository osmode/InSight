package com.logisome.insight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

public class ProfileActivity extends Activity {

	private List<Card> mCards;
	private CardScrollView mCardScrollView;
	private static final String TAG = "ProfileActivity";
	private static final String GET_PROFILE_URL = "http://insightforglass.com/api/users/";
	private static final int NUM_CARDS = 6;
	public static final String USERNAME = "USERNAME";
	private String mUsername;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setUsername(getIntent().getStringExtra(USERNAME));
		Log.d(TAG, "username set to: " + getUsername());
		
		createCards();
		mCardScrollView = new CardScrollView(this);
		ListCardScrollAdapter adapter = new ListCardScrollAdapter(mCards);
		mCardScrollView.setAdapter(adapter);
		mCardScrollView.activate();
		setContentView(mCardScrollView);
		
		new FetchItemsTask().execute();
	}
	
	
	private void createCards() {
		mCards = new ArrayList<Card>();
		
		for (int i=0; i < NUM_CARDS; i++) {
			
			Card locationCard = new Card(this);
			locationCard.setText("Loading profile...");
			mCards.add(locationCard);
		}
		
	}

	private void updateCards() {
		
		String status = DataStore.get(getApplication()).getStatus();
		String workingon = DataStore.get(getApplication()).getWorkingOn();
		String relationship = DataStore.get(getApplication()).getRelationship();
		String interestedin = DataStore.get(getApplication()).getInterestedIn();
		String favQuote = DataStore.get(getApplication()).getFavQuote();
		String event = DataStore.get(getApplication()).getEvent();
		
		mCards.get(0).setText(status);
		mCards.get(1).setText(workingon);
		mCards.get(2).setText(relationship);
		mCards.get(3).setText(interestedin);
		mCards.get(4).setText(favQuote);
		mCards.get(5).setText(event);	
		
		// iterate through all cards, popping each blank one
		/*
		ListIterator<Card> iter = mCards.listIterator();
		while (iter.hasNext()) {
			if(iter.next().getText().length() == 0) {
				iter.remove();
			}
		}
		*/
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

   private class FetchItemsTask extends AsyncTask<Void, Void, Void> {
    	@Override
    	protected Void doInBackground(Void...params) {
    		try {
    			pullUserData(GET_PROFILE_URL + getUsername());
    		} catch (Exception e) {
    			Log.e(TAG, "Failed to fetch URL: ", e);
    		}
    		
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void v) {
    		
    		updateCards();
    		mCardScrollView.updateViews(true);
    	}
    }	
   
	private void pullUserData(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response;
		JSONObject jsonObject;
				
		try {
			response = client.execute(httpget);			
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				jsonObject = new JSONObject(result);
								
				DataStore.get(getApplicationContext()).parseJSON(jsonObject);
				instream.close();
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	   
    private static String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;
    try {
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    return sb.toString();
    }

	
}

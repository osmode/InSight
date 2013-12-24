package com.logisome.insight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

public class MainActivity extends Activity {
	
	private List<Card> mCards;
	private CardScrollView mCardScrollView;
	
	private static final String BASE_URL = "http://162.243.213.58/api/users/osmode";
	public static final String EXTRA_USERNAME = "EXTRA_USERNAME";
	private static final String TAG = "MainActivity";

	private String username;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		username = getIntent().getStringExtra(EXTRA_USERNAME);
		
		new FetchItemsTask().execute();
		
		createCards();
		
		final Context ctx = this;
		mCardScrollView = new CardScrollView(this);
		ListCardScrollAdapter adapter = new ListCardScrollAdapter(mCards);
		mCardScrollView.setAdapter(adapter);
		mCardScrollView.activate();
		setContentView(mCardScrollView);
		
		mCardScrollView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (position == 0) {
					
				}
			}
		});
	}
	
	private void createCards() {
		mCards = new ArrayList<Card>();
		
		String username = DataStore.get(getApplication()).getUsername();
		String funfact = DataStore.get(getApplication()).getFunFact();
		String workingOn = DataStore.get(getApplication()).getWorkingOn();
		String afraidOf = DataStore.get(getApplication()).getAfraidOf();
		String favQuote = DataStore.get(getApplication()).getFavQuote();
		String event = DataStore.get(getApplication()).getEvent();
		String contact = DataStore.get(getApplication()).getContact();
		
		// create cards 
		Card usernameCard = new Card(this);
		Card funfactCard = new Card(this);
		Card workingOnCard = new Card(this);
		Card afraidOfCard = new Card(this);
		Card favQuoteCard = new Card(this);
		Card eventCard = new Card(this);
		Card contactCard = new Card(this);
		
		// populate cards based on pulled data in DataStore
		usernameCard.setText(username);
		funfactCard.setText(funfact);
		workingOnCard.setText(workingOn);
		afraidOfCard.setText(afraidOf);
		favQuoteCard.setText(favQuote);
		eventCard.setText(event);
		contactCard.setText(contact);
		
		// add cards to mCards
		mCards.add(usernameCard);
		mCards.add(funfactCard);
		mCards.add(workingOnCard);
		mCards.add(afraidOfCard);
		mCards.add(favQuoteCard);
		mCards.add(eventCard);
		mCards.add(contactCard);
		
	}
	
	private void pullUserData(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response;
		JSONObject jsonObject;
		
		Log.d(TAG, "Trying to pull data from " + url);
		
		try {
			response = client.execute(httpget);
			Log.d(TAG, "Response line: " + response.getStatusLine().toString());
			
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				Log.d(TAG, "Result: " + result);
				jsonObject = new JSONObject(result);
				
				Log.d(TAG, "Working on: " + jsonObject.getString("workingon"));
				
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
    
    private class FetchItemsTask extends AsyncTask<Void, Void, Void> {
    	@Override
    	protected Void doInBackground(Void...params) {
    		try {
    			pullUserData(BASE_URL);
    		} catch (Exception e) {
    			Log.e(TAG, "Failed to fetch URL: ", e);
    		}
    		
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void v) {
    		createCards();
    	}
    }
	
}

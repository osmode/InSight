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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

public class MainActivity extends Activity {
	
	private List<Card> mCards;
	private CardScrollView mCardScrollView;
	
	private static final String BASE_URL = "http://162.243.213.58/api/users/";
	public static final String EXTRA_USERNAME = "EXTRA_USERNAME";
	private static final String TAG = "MainActivity";
	private int mResponseCode;
	
	public int getResponseCode() {
		return mResponseCode;
	}

	public void setResponseCode(int responseCode) {
		mResponseCode = responseCode;
	}

	public static final int RESPONSE_FOUND_USER = 1;
	public static final int RESPONSE_USER_NOT_FOUND = 0;

	private String username;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setUsername(getIntent().getStringExtra(EXTRA_USERNAME));
		
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
	
    // when Glass is tapped, inflate the options menu (defined in remedy.xml)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
          if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        	  Log.d(TAG, "Finishing activity");
        	  finish();
          }
          return false;
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	
    	Log.d(TAG, "Key code: "+keyCode);
    	
    	if (keyCode == 4) {
    		Log.d(TAG, "Swiped down");
    		finish();
    		Intent i = new Intent(this, ScanActivity.class);
    		startActivity(i);
    		
    		return true;
    	}
    	return false;
    }
	
	private void createCards() {
		mCards = new ArrayList<Card>();
		
		// create cards 
		Card usernameCard = new Card(this);
		Card funfactCard = new Card(this);
		Card workingOnCard = new Card(this);
		Card afraidOfCard = new Card(this);
		Card favQuoteCard = new Card(this);
		Card eventCard = new Card(this);
		Card contactCard = new Card(this);
		
		//populate cards based on pulled data in DataStore
		
		usernameCard.setFootnote("username");
		funfactCard.setFootnote("status");
		workingOnCard.setFootnote("working on");
		afraidOfCard.setFootnote("afraid of");
		favQuoteCard.setFootnote("fav quote");
		eventCard.setFootnote("fav book/film/show/concert");
		contactCard.setFootnote("contact");
		
		// add cards to mCards
		mCards.add(usernameCard);
		mCards.add(funfactCard);
		mCards.add(workingOnCard);
		mCards.add(afraidOfCard);
		mCards.add(favQuoteCard);
		mCards.add(eventCard);
		mCards.add(contactCard);
		
	}
	
	private void updateCards() {
		
		String username = DataStore.get(getApplication()).getUsername();
		String funfact = DataStore.get(getApplication()).getFunFact();
		String workingOn = DataStore.get(getApplication()).getWorkingOn();
		String afraidOf = DataStore.get(getApplication()).getAfraidOf();
		String favQuote = DataStore.get(getApplication()).getFavQuote();
		String event = DataStore.get(getApplication()).getEvent();
		String contact = DataStore.get(getApplication()).getContact();
		
		mCards.get(0).setText(username);
		mCards.get(1).setText(funfact);
		mCards.get(2).setText(workingOn);
		mCards.get(3).setText(afraidOf);
		mCards.get(4).setText(favQuote);
		mCards.get(5).setText(event);
		mCards.get(6).setText(contact);
		
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
			setResponseCode(response.getStatusLine().getStatusCode());
			Log.d(TAG, "Response code: " + getResponseCode());
			
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
    			pullUserData(BASE_URL + getUsername());
    		} catch (Exception e) {
    			Log.e(TAG, "Failed to fetch URL: ", e);
    		}
    		
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void v) {
    		
    		// if no user was found, show error toast and return to ScanActivity
    		if (getResponseCode() == 200 ) {
        		//Toast toast = Toast.makeText(getApplicationContext(), "User found", Toast.LENGTH_SHORT);
        		//toast.show();
    		} else {
    			
	    		Toast toast = Toast.makeText(getApplicationContext(), "Unable to find user", Toast.LENGTH_SHORT);
	    		toast.show();
	    		finish();
	    		startScanActivity();
	    		
    		}
    		
    		updateCards();
    		mCardScrollView.updateViews(true);
    	}
    }
    
    private void startScanActivity() {
		Intent i = new Intent(this, ScanActivity.class);
		startActivity(i);
    }
    
}


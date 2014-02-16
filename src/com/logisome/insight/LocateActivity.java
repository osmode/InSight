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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

public class LocateActivity extends Activity {

	private List<Card> mCards;
	private CardScrollView mCardScrollView;
	private Handler mHandler;
	
	static final int QR_SCAN_REQUEST = 10;
	static final int AUTOSCROLL_WAIT = 1500;
	static final int NUM_CARDS = 10;
	
	// minimum amount of time between coordinate uploads to server
	static final int MIN_LAST_SEND = 30;
	
	private double mLastLatitude;
	private double mLastLongitude;
	private float mLastUpdate;
	private long mLastTimestamp;
	
	private Context mCtx = this;
	
	private static final String TAG = "LocateActivity";
	private static final String POST_URL = "http://www.insightforglass.com/upload_coordinates";
	private static final String GET_NEIGHBORS_URL = "http://www.insightforglass.com/get_neighbors";
	
	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
		
		@Override
		protected void onLocationReceived(Context context, Location loc) {
			mLastLocation = loc;
			updateUI();
		}
		
	};
	
	private RunManager mRunManager;
	private Location mLastLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		createCards();
		setCtx(this);
		mRunManager = RunManager.get(this);
		mRunManager.startLocationUpdates();
		updateUI();
				
		mCardScrollView = new CardScrollView(this);
		ListCardScrollAdapter adapter = new ListCardScrollAdapter(mCards);
		mCardScrollView.setAdapter(adapter);
		mCardScrollView.activate();
		setContentView(mCardScrollView);
		
		mCardScrollView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				String username = mCards.get(position).getText();
				Log.d(TAG, "Pulling profile for: " + username);
				Intent i = new Intent(getCtx(), ProfileActivity.class);
				i.putExtra(ProfileActivity.USERNAME, username);
				startActivity(i);
			
			}
		});		
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		registerReceiver(mLocationReceiver, new IntentFilter(RunManager.ACTION_LOCATION));
		
	}
	
	@Override
	public void onStop() {
		unregisterReceiver(mLocationReceiver);
		super.onStop();
	}
	
	private void createCards() {
		mCards = new ArrayList<Card>();

		for (int i=0; i < NUM_CARDS; i++) {
			
			Card locationCard = new Card(this);
			locationCard.setText("Location Card");
			mCards.add(locationCard);
		}
	}
	
	private void updateUI() {
		boolean started = mRunManager.isTrackingRun();
		
		if (mLastLocation != null) {
			setLastLatitude(mLastLocation.getLatitude());
			setLastLongitude(mLastLocation.getLongitude());
			
			String firstName;
			String latitudeString = String.valueOf(mLastLocation.getLatitude());
			String longitudeString = String.valueOf(mLastLocation.getLongitude());
			
			/*
			if (DataStore.get(this).getNeighbors() != null) {
				if (DataStore.get(this).getNeighbors().size() >0) {
				firstName = DataStore.get(this).getNeighbors().get(0);
				mCards.get(0).setText(firstName);
				}
			}
			*/
			//mCards.get(0).setText("Latitude: " + latitudeString + "\n" + "Longitude: " + longitudeString);
			
			// Upload coordinates to server
			if (getCurrentTimestamp() - getLastTimestamp() > MIN_LAST_SEND) {
				Log.d(TAG, "Trying to upload coordinates");
				new FetchItemsTask().execute();
			} else {
				Log.d(TAG, "last timestamp: " + getLastTimestamp() + " , MIN_LAST_SEND: " + MIN_LAST_SEND);
			}
			
			mCardScrollView.updateViews(true);
		}
	}
	
    private class FetchItemsTask extends AsyncTask<Void, Void, Void> {
    	@Override
    	protected Void doInBackground(Void...params) {
    		try {
    			setLastTimestamp(System.currentTimeMillis()/1000);
    			uploadCoordinates("osmode", getLastLatitude(), getLastLongitude());
    			getNeighbors(GET_NEIGHBORS_URL);
    			
    		} catch (Exception e) {
    			Log.e(TAG, "Failed to fetch URL: ", e);
    		}
    		
    		return null;
    	}  // doInBackground
    	
    	@Override
    	protected void onPostExecute(Void v) {
    		updateCards();

    	}  // onPostExecute
    	
    }  // FetchItemsTask
    
    private void updateCards() {
    	
    	int currentIndex = 0;
    	
   		for (String username : DataStore.get(getApplicationContext()).getNeighbors()) {

   			// currentIndex must be less than or equal to number of cards - 1
   			if (currentIndex > mCards.size() - 1)
   				return;
   			
   			mCards.get(currentIndex).setText(username);
   			currentIndex++;
		}
				
		mCardScrollView.updateViews(true);
    	
    }
    
	private void uploadCoordinates(String username, double latitude, double longitude) {
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(POST_URL);
		HttpResponse response;
		JSONObject jsonObject = new JSONObject();
		String jsonString = "";
				
		try {
			
			jsonObject.accumulate("username", username);
			jsonObject.accumulate("latitude", String.valueOf(latitude));
			jsonObject.accumulate("longitude", String.valueOf(longitude));
			
			jsonString = jsonObject.toString();
			StringEntity se = new StringEntity(jsonString);
			httpPost.setEntity(se);
			
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			
			response = client.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			
			if (responseEntity != null) {
				InputStream instream = responseEntity.getContent();
				String result = convertStreamToString(instream);
				//JSONObject resultObject = new JSONObject(result);
				
				Log.d(TAG, "Result: " + result);
				
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void getNeighbors(String url) {
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
				Log.d(TAG, "Neighbor info:" + result);
				jsonObject = new JSONObject(result);
				
				Log.d(TAG, "Nearby users: " + jsonObject);
				DataStore.get(getApplicationContext()).parseUserList(jsonObject);
				//List<String> userList = DataStore.get(getApplicationContext()).getNeighbors();
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

	public double getLastLatitude() {
		return mLastLatitude;
	}

	public void setLastLatitude(double lastLatitude) {
		mLastLatitude = lastLatitude;
	}

	public double getLastLongitude() {
		return mLastLongitude;
	}

	public void setLastLongitude(double lastLongitude) {
		mLastLongitude = lastLongitude;
	}

	public double getLastUpdate() {
		return mLastUpdate;
	}

	public void setLastUpdate(float lastUpdate) {
		mLastUpdate = lastUpdate;
	}
	 
	private long getCurrentTimestamp() {
		return System.currentTimeMillis()/1000;
	}

	public long getLastTimestamp() {
		return mLastTimestamp;
	}

	public void setLastTimestamp(long lastTimestamp) {
		mLastTimestamp = lastTimestamp;
	}

	public Context getCtx() {
		return mCtx;
	}

	public void setCtx(Context ctx) {
		mCtx = ctx;
	}
	
}



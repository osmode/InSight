package com.logisome.insight;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

public class LocateActivity extends Activity {

	private List<Card> mCards;
	private CardScrollView mCardScrollView;
	private Handler mHandler;
	
	static final int QR_SCAN_REQUEST = 10;
	static final int AUTOSCROLL_WAIT = 1500;
	
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
		mRunManager = RunManager.get(this);
		mRunManager.startLocationUpdates();
		updateUI();
		
		mCardScrollView = new CardScrollView(this);
		ListCardScrollAdapter adapter = new ListCardScrollAdapter(mCards);
		mCardScrollView.setAdapter(adapter);
		mCardScrollView.activate();
		setContentView(mCardScrollView);
		
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
		
		Card locationCard = new Card(this);
		locationCard.setText("Location Card");
		
		mCards.add(locationCard);
	}
	
	private void updateUI() {
		boolean started = mRunManager.isTrackingRun();
		
		if (mLastLocation != null) {
			String latitudeString = String.valueOf(mLastLocation.getLatitude());
			String longitudeString = String.valueOf(mLastLocation.getLongitude());
			mCards.get(0).setText("Longitude: " + latitudeString + "\n" + "Longitude: " + longitudeString);
			
			mCardScrollView.updateViews(true);
		}
		

		
	}
	
}


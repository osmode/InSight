package com.logisome.insight;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.glass.app.Card;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

public class ScanActivity extends Activity {
	
	private List<Card> mCards;
	private CardScrollView mCardScrollView;
	private Handler mHandler;
	
	static final int QR_SCAN_REQUEST = 10;
	static final int AUTOSCROLL_WAIT = 1500;
	
	private static final String TAG = "ScanActivity";
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
					Intent i = new Intent (ctx, CaptureActivity.class);
					startActivityForResult(i, QR_SCAN_REQUEST);
				}
			}
		});
		
	}
	
	private void createCards() {
		mCards = new ArrayList<Card>();
		
		Card card;
		
		card = new Card(this);
		card.setText("Tap to scan QR code");
		mCards.add(card);
	}
	
	// called after CaptureACtivity finishes (i.e. QR Code reader)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode != RESULT_OK) 
			return;
		
		if (requestCode == QR_SCAN_REQUEST) {
			String qrData = data.getStringExtra(Intents.Scan.RESULT);
			
			Log.d(TAG, "QR Code data: " + qrData);
			
			// try pulling user data; if successful, 
			Intent i = new Intent(this, MainActivity.class);
			i.putExtra(MainActivity.EXTRA_USERNAME, qrData);
			finish();
			startActivity(i);
		}
		
		
		
	}
	
}

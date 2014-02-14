package com.logisome.insight;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
	
	private RunManager mRunManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		createCards();
		mRunManager = RunManager.get(this);
		mRunManager.startLocationUpdates();
		//updateUI();
		
		mCardScrollView = new CardScrollView(this);
		ListCardScrollAdapter adapter = new ListCardScrollAdapter(mCards);
		mCardScrollView.setAdapter(adapter);
		mCardScrollView.activate();
		setContentView(mCardScrollView);
		
	}
	
	private void createCards() {
		mCards = new ArrayList<Card>();
		
		Card locationCard = new Card(this);
		locationCard.setText("Location Card");
		
		mCards.add(locationCard);
	}
}

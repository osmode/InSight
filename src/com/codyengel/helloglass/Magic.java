package com.codyengel.helloglass;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.glass.app.Card;

public class Magic extends Activity implements GestureDetector.OnGestureListener, LocationListener{

	private GestureDetector mGestureDetector;
	private LocationManager locationManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10; //10 seconds
 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		/*
		 * We're creating a card for the interface.
		 * 
		 * More info here: http://developer.android.com/guide/topics/ui/themes.html
		 */
		Card card1 = new Card(this);
		card1.setText("Hello, Sir!");
		card1.setInfo("..or Ma'am");
		View card1View = card1.toView();
		
		mGestureDetector = new GestureDetector(this, this);
		
		/*
		locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(true);
		
		String provider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
		*/
		// Display the card we just created
		setContentView(card1View);
	}
	   
	   @Override
	   public boolean onGenericMotionEvent(MotionEvent event) {
	       mGestureDetector.onTouchEvent(event);
	       return true;
	   }
	   @Override
	   public void onBackPressed() {
	       Log.d("Gesture Example", "onBackPressed");
	       Toast.makeText(getApplicationContext(), "Go Back", Toast.LENGTH_SHORT).show();
	   }
	   @Override
	   public boolean onDown(MotionEvent e) {
	       Log.d("Gesture Example", "onDown");
	       return true;
	   }
	   @Override
	   public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	       Log.d("Gesture Example", "onFling: velocityX:" + velocityX + " velocityY:" + velocityY);
	       if (velocityX < -3500) {
	           Toast.makeText(getApplicationContext(), "Fling Right", Toast.LENGTH_SHORT).show();
	       } else if (velocityX > 3500) {
	           Toast.makeText(getApplicationContext(), "Fling Left", Toast.LENGTH_SHORT).show();
	       }
	       return true;
	   }
	   
	   @Override
	   public void onLongPress(MotionEvent e) {
	       Log.d("Gesture Example", "onLongPress");
	       Toast.makeText(getApplicationContext(), "Long Press", Toast.LENGTH_SHORT).show();
	   }
	   
	   @Override
	   public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
	       Log.d("Gesture Example", "onScroll: distanceX:" + distanceX + " distanceY:" + distanceY);
	       if (distanceX < -1) {
	           Log.d("Gesture Example", "OnScrollLeft");
	           //Scroll Right or Scroll Up Command Here
	       } else if (distanceX > 1) { 
	           Log.d("Gesture Example", "OnScrollLeft"); 
	           //Scroll left or Scroll Down Command Here 
	       } return true;
	   }
	   
	   @Override
	    public void onShowPress(MotionEvent e) {
	    Log.d("Gesture Example", "onShowPress");
	}

	   @Override
	   public boolean onSingleTapUp(MotionEvent e) {
		   
		   startPhoto();
		   
	       Log.d("Gesture Test", "onSingleTapUp");
	       Toast.makeText(getApplicationContext(), "Single Tap Up", Toast.LENGTH_SHORT).show();
	       return true;
	   }

	   	private void startPhoto() {
	   			Log.d("TEST", "startPhoto");
			   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			   startActivityForResult(intent, 0);
	   	}
	   	
	   	@Override
	   	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   		Log.d("TEST", "onActivityResult");
	   	}
	   	
	   	@Override 
	   	public void onLocationChanged(Location location) {
	   		Log.d("Test", "onLocationChanged");
	   		
	   	}
	   	
	    @Override
	    public void onProviderDisabled(String provider) {
	    }
	 
	    @Override
	    public void onProviderEnabled(String provider) {
	    }
	 
	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	    }
	 

	   		   	

}

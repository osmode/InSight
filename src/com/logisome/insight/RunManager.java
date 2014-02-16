package com.logisome.insight;

import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;

public class RunManager {
	private static final String TAG = "RunManager";
	
	public static final String ACTION_LOCATION = "com.logisome.insight.ACTION_LOCATION";
	private static RunManager sRunManager;
	private Context mAppContext;
	private LocationManager mLocationManager;
	
	// The private construction forces users to use RunManager
	private RunManager(Context appContext) {
		mAppContext = appContext;
		mLocationManager = (LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public static RunManager get(Context c) {
		if (sRunManager == null) {
			// use application context to avoid leaking activities
			sRunManager = new RunManager(c.getApplicationContext());
		}
		return sRunManager;
	}
	
	private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
		
		return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
		
	}
	
	public void startLocationUpdates() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(true);
		
		List<String> providers = mLocationManager.getProviders(criteria, true);
		//String provider = LocationManager.GPS_PROVIDER;
		
		// start updates from location manager
		PendingIntent pi = getLocationPendingIntent(true);
		
		for (String provider : providers) {
			mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
		} 
	}
	
	public void stopLocationUpdates() {
		PendingIntent pi = getLocationPendingIntent(false);
		if (pi != null) {
			mLocationManager.removeUpdates(pi);
			pi.cancel();
		}
	}
	
	public boolean isTrackingRun() {
		return getLocationPendingIntent(false) != null;
	}
	
	
}

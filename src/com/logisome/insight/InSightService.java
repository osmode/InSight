package com.logisome.insight;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.google.zxing.client.android.CaptureActivity;;

public class InSightService extends Service {

	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	class IncomingHandler extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	}
	
	@Override
	public IBinder onBind(Intent i) {
		return mMessenger.getBinder();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		
		Intent captureActivity = new Intent(this, CaptureActivity.class);
		captureActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		getApplication().startActivity(captureActivity);
		
		return START_STICKY;
	}

}

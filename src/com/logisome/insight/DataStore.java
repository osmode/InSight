package com.logisome.insight;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class DataStore {

	private static DataStore sDataStore;
	private Context mAppContext;
	private String mUsername;
	private String mFunFact;
	private String mWorkingOn;
	private String mAfraidOf;
	private String mFavQuote;
	private String mEvent;
	private String mContact;
	
	
	private DataStore(Context appContext) {
		mAppContext = appContext;
	
		// create and initialize sample JSON object
		JSONObject myProfile = new JSONObject();
		try {
			myProfile.put("username", "osmode");
			myProfile.put("fun_fact", "I read Wittgenstein in German");
			myProfile.put("working_on", "Fixing the U.S. healthcare system");
			myProfile.put("afraid_of", "Being alone");
			myProfile.put("fav_quote", "You are at once both the calm and confusion of my heart");
			myProfile.put("event", "Flaming Lips");
			myProfile.put("contact", "omar.metwally@gmail.com");
	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		parseJSON(myProfile);
	}
	
	public static DataStore get(Context c) {
		if( sDataStore == null) {
			sDataStore = new DataStore(c.getApplicationContext());
		}
		
		return sDataStore;
	}
	
	private void parseJSON(JSONObject obj) {
		try {
			mUsername = (String)obj.get("username");
			mFunFact = (String)obj.get("fun_fact");
			mWorkingOn = (String)obj.get("working_on");
			mAfraidOf = (String)obj.get("afraid_of");
			mFavQuote = (String)obj.get("fav_quote");
			mEvent = (String)obj.get("event");
			mContact = (String)obj.get("contact");
				
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getUsername() {
		return mUsername;
	}

	public String getFunFact() {
		return mFunFact;
	}

	public String getWorkingOn() {
		return mWorkingOn;
	}

	public String getAfraidOf() {
		return mAfraidOf;
	}

	public String getFavQuote() {
		return mFavQuote;
	}

	public String getEvent() {
		return mEvent;
	}

	public String getContact() {
		return mContact;
	}
	
	
}

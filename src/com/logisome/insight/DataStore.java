package com.logisome.insight;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class DataStore {

	private static DataStore sDataStore;
	private Context mAppContext;
	
	private String mStatus;
	private String mWorkingOn;
	private String mRelationship;
	private String mInterestedIn;
	private String mFavQuote;
	private String mEvent;
	
	private DataStore(Context appContext) {
		mAppContext = appContext;
	
		// create and initialize sample JSON object
		/*
		JSONObject myProfile = new JSONObject();
		try {
			myProfile.put("username", "osmode");
			myProfile.put("funfact", "I read Wittgenstein in German");
			myProfile.put("workingon", "Fixing the U.S. healthcare system");
			myProfile.put("afraidof", "Being alone");
			myProfile.put("favquote", "You are at once both the calm and confusion of my heart");
			myProfile.put("event", "Flaming Lips");
			myProfile.put("contact", "omar.metwally@gmail.com");
	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		parseJSON(myProfile);
		*/
	}
	
	public static DataStore get(Context c) {
		if( sDataStore == null) {
			sDataStore = new DataStore(c.getApplicationContext());
		}
		
		return sDataStore;
	}
	
	public void parseJSON(JSONObject obj) {
		try {
			
			mStatus = (String)obj.get("status");
			mWorkingOn = (String)obj.get("workingon");
			mRelationship = (String)obj.get("relationship");
			mInterestedIn = (String)obj.get("interestedin");
			mFavQuote = (String)obj.get("favquote");
			mEvent = (String)obj.get("event");
				
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		mStatus = status;
	}

	public String getWorkingOn() {
		return mWorkingOn;
	}

	public void setWorkingOn(String workingOn) {
		mWorkingOn = workingOn;
	}

	public String getRelationship() {
		return mRelationship;
	}

	public void setRelationship(String relationship) {
		mRelationship = relationship;
	}

	public String getInterestedIn() {
		return mInterestedIn;
	}

	public void setInterestedIn(String interestedIn) {
		mInterestedIn = interestedIn;
	}

	public String getFavQuote() {
		return mFavQuote;
	}

	public void setFavQuote(String favQuote) {
		mFavQuote = favQuote;
	}

	public String getEvent() {
		return mEvent;
	}

	public void setEvent(String event) {
		mEvent = event;
	}
	
	
}

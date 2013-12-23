package com.logisome.insight;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

public class MainActivity extends Activity {
	
	private List<Card> mCards;
	private CardScrollView mCardScrollView;

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
					
				}
			}
		});
	}
	
	private void createCards() {
		mCards = new ArrayList<Card>();
		
		String username = DataStore.get(getApplication()).getUsername();
		String funfact = DataStore.get(getApplication()).getFunFact();
		String workingOn = DataStore.get(getApplication()).getWorkingOn();
		String afraidOf = DataStore.get(getApplication()).getAfraidOf();
		String favQuote = DataStore.get(getApplication()).getFavQuote();
		String event = DataStore.get(getApplication()).getEvent();
		String contact = DataStore.get(getApplication()).getContact();
		
		// create cards 
		Card usernameCard = new Card(this);
		Card funfactCard = new Card(this);
		Card workingOnCard = new Card(this);
		Card afraidOfCard = new Card(this);
		Card favQuoteCard = new Card(this);
		Card eventCard = new Card(this);
		Card contactCard = new Card(this);
		
		// populate cards based on pulled data in DataStore
		usernameCard.setText(username);
		funfactCard.setText(funfact);
		workingOnCard.setText(workingOn);
		afraidOfCard.setText(afraidOf);
		favQuoteCard.setText(favQuote);
		eventCard.setText(event);
		contactCard.setText(contact);
		
		// add cards to mCards
		mCards.add(usernameCard);
		mCards.add(funfactCard);
		mCards.add(workingOnCard);
		mCards.add(afraidOfCard);
		mCards.add(favQuoteCard);
		mCards.add(eventCard);
		mCards.add(contactCard);
		
	}
	
}

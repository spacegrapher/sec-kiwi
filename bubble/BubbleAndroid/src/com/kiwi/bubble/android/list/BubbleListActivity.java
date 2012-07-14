package com.kiwi.bubble.android.list;

import com.kiwi.bubble.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class BubbleListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubblelist);
		
		Intent intent = this.getIntent();
		String email = intent.getStringExtra("email");
		
		TextView textViewWelcome = (TextView) findViewById(R.id.textViewListWelcome);
		textViewWelcome.setText("Welcome " + email + "!");
		
	}

}

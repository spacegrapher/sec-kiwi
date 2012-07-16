package com.kiwi.bubble.android.list;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.kiwi.bubble.android.BubbleCreateActivity;
import com.kiwi.bubble.android.MainActivity;
import com.kiwi.bubble.android.R;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BubbleListActivity extends Activity {
	private static final int REQUEST_CODE_CREATE = 101;
	private String strEmail;
	private ListView lvBubbleList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubblelist);
		
		Intent intent = this.getIntent();
		strEmail = intent.getStringExtra("email");
		
		TextView textViewWelcome = (TextView) findViewById(R.id.textViewListWelcome);
		textViewWelcome.setText("Welcome " + strEmail + "!");
		
		lvBubbleList = (ListView) findViewById(R.id.listViewBubbleList);
		updateListView();
	}
	
	public void updateListView() {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/list";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl + "?email=" + strEmail, client);
		List<BubbleData> bubbles = ObjectParsers.parseBubbleData(response);
		
		ArrayList<String> bubbleTitle = new ArrayList<String>();
		for(int i=0; i<bubbles.size(); i++) {
			bubbleTitle.add(bubbles.get(i).getTitle() + " (" + bubbles.get(i).getText() + ")");
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bubbleTitle);
		lvBubbleList.setAdapter(adapter);
		
		//Toast.makeText(BubbleListActivity.this, response, 0).show();
	}
	
	public void onClickCreateBubble(View v) {
		Intent intent = new Intent(this, BubbleCreateActivity.class);
		intent.putExtra("email", strEmail);
		startActivityForResult(intent, REQUEST_CODE_CREATE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK){
			if (requestCode == REQUEST_CODE_CREATE) {
				updateListView();
				Toast.makeText(BubbleListActivity.this, "Bubble Created!", 0).show();
			}
		}
	}
}

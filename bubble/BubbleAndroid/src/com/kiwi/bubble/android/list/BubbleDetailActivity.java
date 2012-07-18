package com.kiwi.bubble.android.list;

import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import com.kiwi.bubble.android.BubbleCreateActivity;
import com.kiwi.bubble.android.R;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class BubbleDetailActivity extends Activity {
	TextView tvName;
	TextView tvTitle;
	TextView tvText;
	TextView tvTag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubbledetail);
		
		tvName = (TextView) findViewById(R.id.textViewBubbleDetailName);
		tvTitle = (TextView) findViewById(R.id.textViewBubbleDetailTitle);
		tvText = (TextView) findViewById(R.id.textViewBubbleDetailText);
		tvTag = (TextView) findViewById(R.id.textViewBubbleDetailTag);
		
		Intent intent = this.getIntent();
		Long id = intent.getLongExtra("id", -1);
		
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/detail";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + id, client);
		List<BubbleData> bubbles = ObjectParsers.parseBubbleData(response);
		
		assert(bubbles.size() == 1);
		
		tvName.setText(bubbles.get(0).getAuthorEmail());
		tvTitle.setText(bubbles.get(0).getTitle());
		tvText.setText(bubbles.get(0).getText());
		tvTag.setText(bubbles.get(0).getTag().toString());
		
	}
	
	public void onClickButtonBack(View v) {
		finish();
	}
}

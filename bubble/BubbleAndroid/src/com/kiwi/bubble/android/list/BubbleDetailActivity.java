package com.kiwi.bubble.android.list;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

import com.kiwi.bubble.android.BubbleCreateActivity;
import com.kiwi.bubble.android.R;
import com.kiwi.bubble.android.common.BubbleComment;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class BubbleDetailActivity extends Activity {
	private TextView tvName;
	private TextView tvTitle;
	private TextView tvText;
	private TextView tvTag;
	private EditText etComment;
	private Long id;
	private String strEmail;
	private ListView lvCommentList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubbledetail);
		
		tvName = (TextView) findViewById(R.id.textViewBubbleDetailName);
		tvTitle = (TextView) findViewById(R.id.textViewBubbleDetailTitle);
		tvText = (TextView) findViewById(R.id.textViewBubbleDetailText);
		tvTag = (TextView) findViewById(R.id.textViewBubbleDetailTag);
		etComment = (EditText) findViewById(R.id.editTextBubbleDetailComment);
		lvCommentList = (ListView) findViewById(R.id.listViewComments);
		
		Intent intent = this.getIntent();
		id = intent.getLongExtra("id", -1);
		strEmail = intent.getStringExtra("email");
		
		getBubbleData();
		getCommentData();
	}
	
	private void getBubbleData() {
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
	
	private void getCommentData() {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/comment";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + id, client);
		Log.i("COMMENT", response);
		List<BubbleComment> comments = ObjectParsers.parseBubbleComment(response);
		
		ArrayList<String> commentText = new ArrayList<String>();
		for(int i=0; i<comments.size(); i++) {
			commentText.add("[" + comments.get(i).getEmail() + "] " + comments.get(i).getText());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, commentText);
		lvCommentList.setAdapter(adapter);
	}
	
	public void onClickButtonBack(View v) {
		finish();
	}
	
	public void onClickSubmitComment(View v) {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/comment";
		
		String strComment = etComment.getText().toString();
				
		HttpPostUtil util = new HttpPostUtil();
		HashMap result = new HashMap();
		String resultStr = new String();
		Map<String, String> param = new HashMap<String, String>();
		param.put("bubbleid", Long.toString(id));
		param.put("email", strEmail);
		param.put("comment", strComment);
		
		
		try {
			resultStr = util.httpPostData(pageUrl, param);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		finish();
		startActivity(getIntent());
	}
}

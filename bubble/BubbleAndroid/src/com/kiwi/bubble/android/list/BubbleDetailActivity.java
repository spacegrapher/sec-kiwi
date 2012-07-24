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
import com.kiwi.bubble.android.common.BubbleTag;
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
	private long id;
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
		BubbleData bubble = BubbleData.getBubbleData(id);
		
		tvName.setText(bubble.getAuthorEmail());
		tvTitle.setText(bubble.getTitle());
		tvText.setText(bubble.getText());
		
		String strTag = "[";
		List<Long> tags = bubble.getTag();
		for(int i=0; i<tags.size(); i++) {
			BubbleTag tag = BubbleTag.getBubbleTag(tags.get(i));
			if(i>0) strTag += ", ";
			strTag += tag.getText();
		}
		strTag += "]";
		
		tvTag.setText(strTag);
	}
	
	private void getCommentData() {		
		List<BubbleComment> comments = BubbleComment.getCommentData(id);
		
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

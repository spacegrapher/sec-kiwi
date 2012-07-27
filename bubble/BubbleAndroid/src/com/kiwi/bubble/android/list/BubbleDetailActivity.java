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
import com.kiwi.bubble.android.common.UserInfo;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;
import com.kiwi.bubble.android.list.BubbleListActivity.BubbleListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
	private Long bubbleId;
	private Long authorId;
	//private String strEmail;
	private ListView lvCommentList;
	private BubbleCommentListAdapter adapter;
	
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
		bubbleId = Long.valueOf(intent.getLongExtra("bubbleid", -1));
		authorId = Long.valueOf(intent.getLongExtra("authorid", -1));
		//strEmail = intent.getStringExtra("email"); 
		
		getBubbleData();
		getCommentData();
	}
	
	private void getBubbleData() {
		BubbleData bubble = BubbleData.getBubbleData(bubbleId.longValue());
		UserInfo userInfo = UserInfo.getUserInfo(bubble.getAuthorId().longValue());
		
		tvName.setText(userInfo.getName());
		tvTitle.setText(bubble.getTitle());
		tvText.setText(bubble.getText());
		
		String strTag = "[";
		List<Long> tags = bubble.getTag();
		for(int i=0; i<tags.size(); i++) {
			BubbleTag tag = BubbleTag.getBubbleTag(tags.get(i));
			if(tag==null) continue;
			if(i>0) strTag += ", ";
			strTag += tag.getText();
		}
		strTag += "]";
		
		tvTag.setText(strTag);
	}
	
	private void getCommentData() {		
		List<BubbleComment> comments = BubbleComment.getCommentData(bubbleId.longValue());
		
		
		/*ArrayList<String> commentText = new ArrayList<String>();
		for(int i=0; i<comments.size(); i++) {
			UserInfo userInfo = UserInfo.getUserInfo(comments.get(i).getAuthorId().longValue());
			commentText.add("[" + userInfo.getName() + "] " + comments.get(i).getText());
		}*/
		adapter = new BubbleCommentListAdapter(comments);
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
		param.put("bubbleid", bubbleId.toString());
		param.put("authorid", authorId.toString());
		param.put("comment", strComment);
		
		
		try {
			resultStr = util.httpPostData(pageUrl, param);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		finish();
		startActivity(getIntent());
	}
	
	class BubbleCommentListAdapter extends BaseAdapter {
		private List<BubbleComment> bubbleComment;
		
		public BubbleCommentListAdapter(List<BubbleComment> bubbleComment) {
			super();
			this.bubbleComment = bubbleComment;
		}

		@Override
		public int getCount() {
			return bubbleComment.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			UserInfo userInfo;
			
			TextView tvName;
			TextView tvText;
			
			if(convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(BubbleDetailActivity.this);
				convertView = inflater.inflate(R.layout.listview_bubblecomment, parent, false);
			}
			tvName = (TextView)convertView.findViewById(R.id.textViewBubbleListCommentViewName);
			tvText = (TextView)convertView.findViewById(R.id.textViewBubbleListCommentViewText);
			
			
			userInfo = UserInfo.getUserInfo(bubbleComment.get(position).getAuthorId().longValue());
						
			tvName.setText("" + userInfo.getName());
			tvText.setText(bubbleComment.get(position).getText());
			
			
			return convertView;
		}		
	}
}

package com.kiwi.bubble.android.member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
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
import com.kiwi.bubble.android.list.BubbleListActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class UserProfileActivity extends SherlockActivity implements ActionBar.TabListener {
	private Long id;
	private Long selectedId;
	private TextView tvName;
	private TextView tvEmail;
	private Button btnSetting;
	private ListView lvBubbleList;
	private UserBubbleListAdapter adapter;
	private UserInfo userInfo;
	private List<BubbleData> bubbles;
	private String[] tabLabel = {"Explore", "Bubble", "Me"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userprofile);
		
		Intent intent = this.getIntent();
		id = Long.valueOf(intent.getLongExtra("id", -1));
		selectedId = Long.valueOf(intent.getLongExtra("selectedid", -1));
	 
		tvName = (TextView) findViewById(R.id.textViewUserProfileName);
		tvEmail = (TextView) findViewById(R.id.textViewUserProfileEmail);
		btnSetting = (Button) findViewById(R.id.buttonUserProfileSetting);
		lvBubbleList = (ListView) findViewById(R.id.listViewUserBubbleList);
		
		if(id == selectedId) {
			btnSetting.setVisibility(View.VISIBLE);
		} else {
			btnSetting.setVisibility(View.INVISIBLE);
		}
		
		if(id == selectedId) {
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < 3; i++) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText(tabLabel[i]);
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }
		getSupportActionBar().setSelectedNavigationItem(2);
		} else {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		getUserInfo();
		updateListView();
	}
	
	private void getUserInfo() {
		userInfo = UserInfo.getUserInfo(selectedId.longValue());
		
		tvName.setText(userInfo.getName());
		tvEmail.setText(userInfo.getEmail());
	}
	
	public void updateListView() {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/list";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + selectedId.toString(), client);
		bubbles = ObjectParsers.parseBubbleData(response);
		adapter = new UserBubbleListAdapter(bubbles);
		lvBubbleList.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
	public void onClickButtonBack(View v) {
		finish();
	}
	
	class UserBubbleListAdapter extends BaseAdapter {
		private List<BubbleData> bubbleData;
		
		public UserBubbleListAdapter(List<BubbleData> bubbleData) {
			super();
			this.bubbleData = bubbleData;
		}

		@Override
		public int getCount() {
			return bubbleData.size();
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
			List<BubbleComment> comments;
			TextView tvName;
			TextView tvDate;
			TextView tvText;
			TextView tvTagCount;
			if(convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(UserProfileActivity.this);
				convertView = inflater.inflate(R.layout.listview_bubblelist, parent, false);
			}
			tvName = (TextView)convertView.findViewById(R.id.textViewBubbleListViewName);
			tvDate = (TextView)convertView.findViewById(R.id.textViewBubbleListViewDate);
			tvText = (TextView)convertView.findViewById(R.id.textViewBubbleListViewText);
			tvTagCount = (TextView)convertView.findViewById(R.id.textViewBubbleListViewTagCount);
			
			userInfo = UserInfo.getUserInfo(bubbleData.get(position).getAuthorId().longValue());
			comments = BubbleComment.getCommentData(bubbleData.get(position).getId().longValue());
			
			tvName.setText("" + userInfo.getName());
			tvDate.setText(bubbleData.get(position).getPostTime().toString());
			tvText.setText(bubbleData.get(position).getText());
			tvTagCount.setText("Tag: " + bubbleData.get(position).getTag().size() + ", Comments: " + comments.size());
			
			tvName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
					startActivity(intent);
				}
				
			});
			
			return convertView;
		}		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		switch (tab.getPosition()) {
		case 0:
			break;
		case 1:
			Intent intent = new Intent(UserProfileActivity.this, BubbleListActivity.class);
			intent.putExtra("id", id.longValue());
			intent.putExtra("selectedid", id.longValue());
			startActivity(intent);
			overridePendingTransition(0, 0);
			break;
		case 2:
			break;
		}		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}

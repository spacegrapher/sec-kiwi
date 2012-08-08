package com.kiwi.bubble.android.member;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.kiwi.bubble.android.R;
import com.kiwi.bubble.android.common.BubbleComment;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.BubbleTag;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.UserInfo;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;
import com.kiwi.bubble.android.list.BubbleListActivity;


public class UserProfileActivity extends SherlockActivity implements ActionBar.TabListener {
	private Long id;
	private Long selectedId;
	private TextView tvName;
	private TextView tvEmail;
	private Button btnSetting;
	private ListView lvBubbleList;
	private LinearLayout llBody;
	private ProgressBar progressBar;
	private UserBubbleListAdapter adapter;
	private UserInfo userInfo;
	private List<BubbleData> bubbles;
	private String[] tabLabel = {"Bubble", "Explore", "Me"};
	
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
		llBody = (LinearLayout) findViewById(R.id.linearLayoutUserProfileBody);
		progressBar = (ProgressBar) findViewById(R.id.progressBarUserProfile);
		
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
		
		new BackgroundTask().execute();
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
			final BubbleData currentBubble;
			TextView tvName;
			TextView tvDate;
			TextView tvText;
			TextView tvTagCount;
			LinearLayout llTag;
			final long lSelectedId;
			
			if(convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(UserProfileActivity.this);
				convertView = inflater.inflate(R.layout.listview_bubblelist, parent, false);
			}
			tvName = (TextView)convertView.findViewById(R.id.textViewBubbleListViewName);
			tvDate = (TextView)convertView.findViewById(R.id.textViewBubbleListViewDate);
			tvText = (TextView)convertView.findViewById(R.id.textViewBubbleListViewText);
			tvTagCount = (TextView)convertView.findViewById(R.id.textViewBubbleListViewTagCount);
			llTag = (LinearLayout)convertView.findViewById(R.id.linearLayoutBubbleListViewTag);
			llTag.removeAllViews();
			
			currentBubble = bubbleData.get(position);
			
			tvName.setText("" + currentBubble.getAuthorInfo().getName());
			tvDate.setText(currentBubble.getPostTime().toString());
			tvText.setText(currentBubble.getText());
			tvTagCount.setText("Tag: " + currentBubble.getTag().size() + ", Comments: " + currentBubble.getComments().size());
			
			for(int i=0; i<currentBubble.getTag().size(); i++) {
				BubbleTag tag = currentBubble.getRealTag().get(i);
				
				TextView tagText = new TextView(UserProfileActivity.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 10, 0);
				tagText.setLayoutParams(params);
				tagText.setBackgroundColor(0xFFFFFF00);
				tagText.setTextSize(TypedValue.COMPLEX_UNIT_PT, 6);
				tagText.setText(tag.getText());
				llTag.addView(tagText);
			}
			
			return convertView;
		}		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		switch (tab.getPosition()) {
		case 0:
			Intent intent = new Intent(UserProfileActivity.this, BubbleListActivity.class);
			intent.putExtra("id", id.longValue());
			intent.putExtra("selectedid", id.longValue());
			startActivity(intent);
			overridePendingTransition(0, 0);
			break;
		case 1:			
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
	
	private class BackgroundTask extends AsyncTask<String, Integer, Long> {
		@Override
		protected Long doInBackground(String... arg0) {
			getUserInfo();
			updateListView();
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			tvName.setText(userInfo.getName());
			tvEmail.setText(userInfo.getEmail());
			
			adapter = new UserBubbleListAdapter(bubbles);
			lvBubbleList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			
			progressBar.setVisibility(View.INVISIBLE);
			llBody.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
			llBody.setVisibility(View.INVISIBLE);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
		
		private void getUserInfo() {
			userInfo = UserInfo.getUserInfo(selectedId.longValue());
		}
		
		private void updateListView() {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/list";
			DefaultHttpClient client = new DefaultHttpClient();
			
			String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + selectedId.toString(), client);
			bubbles = ObjectParsers.parseBubbleData(response);
			
			for(int i=0; i<bubbles.size(); i++) {
				BubbleData bubble = bubbles.get(i);
				
				// Get UserInfo
				UserInfo userInfo = UserInfo.getUserInfo(bubble.getAuthorId());
				bubble.setAuthorInfo(userInfo);
				
				// Get Tag
				List<Long> longTag = bubble.getTag();
				List<BubbleTag> bubbleTag = new ArrayList<BubbleTag>();
				
				for(int j=0; j<longTag.size(); j++) {
					BubbleTag tag = BubbleTag.getBubbleTag(longTag.get(j));
					bubbleTag.add(tag);
				}
				bubble.setRealTag(bubbleTag);
				
				// Get Comments
				List<BubbleComment> comments = BubbleComment.getCommentData(bubble.getId().longValue());
				bubble.setComments(comments);
				
				bubbles.set(i, bubble);
			}
		}
	}
}

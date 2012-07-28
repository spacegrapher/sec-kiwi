package com.kiwi.bubble.android.list;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.kiwi.bubble.android.BubbleCreateActivity;
import com.kiwi.bubble.android.MainActivity;
import com.kiwi.bubble.android.R;
import com.kiwi.bubble.android.TagSelectActivity;
import com.kiwi.bubble.android.common.BubbleComment;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.BubbleTag;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.UserInfo;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BubbleListActivity extends Activity {
	private static final int REQUEST_CODE_CREATE = 101;
	private Long id;
	private ListView lvBubbleList;
	private BubbleListAdapter adapter;
	private List<BubbleData> bubbles;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubblelist);
		
		Intent intent = this.getIntent();
		id = Long.valueOf(intent.getLongExtra("id", -1));
		
		lvBubbleList = (ListView) findViewById(R.id.listViewBubbleList);
		lvBubbleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long _id) {
				Intent intent = new Intent(BubbleListActivity.this, BubbleDetailActivity.class);
				intent.putExtra("bubbleid", bubbles.get(position).getId());
				intent.putExtra("authorid", id.longValue());
				startActivity(intent);
			}
			
		});
		updateListView();
	}
	
	public void updateListView() {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/list";
		DefaultHttpClient client = new DefaultHttpClient();
		
		String response = HttpGetUtil.doGetWithResponse(pageUrl /*+ "?email=" + strEmail*/, client);
		bubbles = ObjectParsers.parseBubbleData(response);
		adapter = new BubbleListAdapter(bubbles);
		lvBubbleList.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
	public void onClickCreateBubble(View v) {
		Intent intent = new Intent(this, TagSelectActivity.class);
		//intent.putExtra("email", strEmail);
		intent.putExtra("id", id.longValue());
		startActivityForResult(intent, REQUEST_CODE_CREATE);
	}
	
	public void onClickRefresh(View v) {
		updateListView();
				
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
	
	class BubbleListAdapter extends BaseAdapter {
		private List<BubbleData> bubbleData;
		
		public BubbleListAdapter(List<BubbleData> bubbleData) {
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
				LayoutInflater inflater = LayoutInflater.from(BubbleListActivity.this);
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
			
			return convertView;
		}
		
		
		
	}
	
}

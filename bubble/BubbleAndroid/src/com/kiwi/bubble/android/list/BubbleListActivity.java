package com.kiwi.bubble.android.list;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kiwi.bubble.android.R;
import com.kiwi.bubble.android.TagSelectActivity;
import com.kiwi.bubble.android.common.BubbleComment;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.BubbleTag;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.UserInfo;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;
import com.kiwi.bubble.android.member.UserPhotoActivity;
import com.kiwi.bubble.android.member.UserProfileActivity;

public class BubbleListActivity extends SherlockActivity implements ActionBar.TabListener {
	private static final int REQUEST_CODE_CREATE = 101;
	private Long id;
	private ListView lvBubbleList;
	private BubbleListAdapter adapter;
	private List<BubbleData> bubbles;
	private String[] tabLabel = {"Bubble", "Explore", "Me"};
	private ProgressBar progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubblelist);
		
		Intent intent = this.getIntent();
		id = Long.valueOf(intent.getLongExtra("id", -1));
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < tabLabel.length; i++) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText(tabLabel[i]);
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }
		getSupportActionBar().setSelectedNavigationItem(0);
        
		progressBar = (ProgressBar) findViewById(R.id.progressBarBubbleList);
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
		new BackgroundTask().execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Refresh")
		.setIcon(R.drawable.icon_refresh)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		menu.add("Tag")
		.setIcon(R.drawable.icon_tag)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("MENU", "item: " + item.toString() + ", id: " + item.getGroupId() + ", order: " + item.getOrder());
		if (item.toString().equals("Refresh")) {
			new BackgroundTask().execute();
		} else if (item.toString().equals("Tag")) {
			Intent intent = new Intent(this, TagSelectActivity.class);
			intent.putExtra("id", id.longValue());
			startActivityForResult(intent, REQUEST_CODE_CREATE);
		}
		return true;
	}

	public void onClickCreateBubble(View v) {
		Intent intent = new Intent(this, TagSelectActivity.class);
		intent.putExtra("id", id.longValue());
		startActivityForResult(intent, REQUEST_CODE_CREATE);
	}
	
	public void onClickRefresh(View v) {
		new BackgroundTask().execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK){
			if (requestCode == REQUEST_CODE_CREATE) {
				new BackgroundTask().execute();
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
			final BubbleData currentBubble;
			TextView tvName;
			TextView tvDate;
			TextView tvText;
			TextView tvTagCount;
			LinearLayout llTag;
			ImageView ivBubblePhoto;
			ImageView ivBubbleUserImage;
			final long lSelectedId;
			
			if(convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(BubbleListActivity.this);
				convertView = inflater.inflate(R.layout.listview_bubblelist, parent, false);
			}
			tvName = (TextView)convertView.findViewById(R.id.textViewBubbleListViewName);
			tvDate = (TextView)convertView.findViewById(R.id.textViewBubbleListViewDate);
			tvText = (TextView)convertView.findViewById(R.id.textViewBubbleListViewText);
			tvTagCount = (TextView)convertView.findViewById(R.id.textViewBubbleListViewTagCount);
			ivBubblePhoto = (ImageView) convertView.findViewById(R.id.imageViewBubbleImage);
			ivBubbleUserImage = (ImageView) convertView.findViewById(R.id.imageViewBubbleUserImage);
			llTag = (LinearLayout)convertView.findViewById(R.id.linearLayoutBubbleListViewTag);
			llTag.removeAllViews();
			
			currentBubble = bubbleData.get(position);
			
			tvName.setText("" + currentBubble.getAuthorInfo().getName());
			tvDate.setText(currentBubble.getPostTime().toString());
			tvText.setText(currentBubble.getText());
			tvTagCount.setText("태그: " + currentBubble.getTag().size() + ", 댓글: " + currentBubble.getComments().size());
			
			final Bitmap userImage = currentBubble.getAuthorInfo().getImage();
			if(userImage != null) {
				ivBubbleUserImage.setImageBitmap(userImage);
			}
			
			final Bitmap photo = currentBubble.getPhoto();
			if(photo != null) {
				ivBubblePhoto.setImageBitmap(photo);
				ivBubblePhoto.setVisibility(View.VISIBLE);
			} else
				ivBubblePhoto.setVisibility(View.GONE);
			
			for(int i=0; i<currentBubble.getTag().size(); i++) {
				BubbleTag tag = currentBubble.getRealTag().get(i);
				
				TextView tagText = new TextView(BubbleListActivity.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 10, 0);
				tagText.setLayoutParams(params);
				tagText.setBackgroundColor(0xFFFFFF00);
				tagText.setTextSize(TypedValue.COMPLEX_UNIT_PT, 6);
				tagText.setText(tag.getText());
				llTag.addView(tagText);
			}
			
			tvName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(BubbleListActivity.this, UserProfileActivity.class);
					intent.putExtra("id", id.longValue());
					intent.putExtra("selectedid", currentBubble.getAuthorId().longValue());
					startActivity(intent);
					if(id.longValue() == currentBubble.getAuthorId().longValue())
						overridePendingTransition(0, 0);
				}
				
			});
			
			ivBubbleUserImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(BubbleListActivity.this, UserPhotoActivity.class);
					intent.putExtra("id", currentBubble.getAuthorId().longValue());
					intent.putExtra("editable", false);
					startActivity(intent);
				}
			});
			
			return convertView;
		}		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Log.i("TAB", "position: " + tab.getPosition());
		switch (tab.getPosition()) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			Intent intent = new Intent(BubbleListActivity.this, UserProfileActivity.class);
			intent.putExtra("id", id.longValue());
			intent.putExtra("selectedid", id.longValue());
			startActivity(intent);
			overridePendingTransition(0, 0);
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
			this.updateListView();
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.INVISIBLE);
			adapter = new BubbleListAdapter(bubbles);
			lvBubbleList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			new BackgroundPhotoTask().execute();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
		private void updateListView() {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/list";
			DefaultHttpClient client = new DefaultHttpClient();
			
			String response = HttpGetUtil.doGetWithResponse(pageUrl, client);
			bubbles = ObjectParsers.parseBubbleData(response);			
			
			for(int i=0; i<bubbles.size(); i++) {
				BubbleData bubble = bubbles.get(i);
				
				// Get UserInfo
				UserInfo userInfo = UserInfo.getUserInfo(bubble.getAuthorId());
								
				// Get User Image
				/*String userImageUrl = Constant.SERVER_DOMAIN_URL + "/userimage";
				DefaultHttpClient userImageClient = new DefaultHttpClient();
				String userImageRes = HttpGetUtil.doGetWithResponse(userImageUrl + "?id=" + bubble.getAuthorId(), userImageClient);
				//Log.i("USER", "userImageRes: " + userImageRes);
				if(!userImageRes.equals("")) {
					byte[] photoByte = Base64.decode(userImageRes, Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
									
					userInfo.setImage(bmp);
				}*/
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
				
				// Get Photo
				/*String photoUrl = Constant.SERVER_DOMAIN_URL + "/image";
				DefaultHttpClient photoClient = new DefaultHttpClient();
				String photoRes = HttpGetUtil.doGetWithResponse(photoUrl + "?bubbleid=" + bubble.getId(), photoClient);
				if(!photoRes.equals("")) {
					byte[] photoByte = Base64.decode(photoRes, Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
									
					bubble.setPhoto(bmp);
				}*/
				
				bubbles.set(i, bubble);
			}
		}
	}
	
	private class BackgroundPhotoTask extends AsyncTask<String, Integer, Long> {
		@Override
		protected Long doInBackground(String... arg0) {
			this.loadPhoto();
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			//progressBar.setVisibility(View.INVISIBLE);
			//adapter = new BubbleListAdapter(bubbles);
			//lvBubbleList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
		private void loadPhoto() {
			for(int i=0; i<bubbles.size(); i++) {
				BubbleData bubble = bubbles.get(i);
				
				UserInfo userInfo = bubble.getAuthorInfo();
				// Get User Image
				String userImageUrl = Constant.SERVER_DOMAIN_URL + "/userimage";
				DefaultHttpClient userImageClient = new DefaultHttpClient();
				String userImageRes = HttpGetUtil.doGetWithResponse(userImageUrl + "?id=" + bubble.getAuthorId(), userImageClient);
				//Log.i("USER", "userImageRes: " + userImageRes);
				if(!userImageRes.equals("")) {
					byte[] photoByte = Base64.decode(userImageRes, Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
									
					userInfo.setImage(bmp);
				}
				bubble.setAuthorInfo(userInfo);
				
				// Get Photo
				String photoUrl = Constant.SERVER_DOMAIN_URL + "/image";
				DefaultHttpClient photoClient = new DefaultHttpClient();
				String photoRes = HttpGetUtil.doGetWithResponse(photoUrl + "?bubbleid=" + bubble.getId(), photoClient);
				if(!photoRes.equals("")) {
					byte[] photoByte = Base64.decode(photoRes, Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
									
					bubble.setPhoto(bmp);
				}
				
				bubbles.set(i, bubble);
			}
		}
	}
}

package com.kiwi.bubble.android.list;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import com.kiwi.bubble.android.ExploreActivity;
import com.kiwi.bubble.android.R;
import com.kiwi.bubble.android.TagSelectActivity;
import com.kiwi.bubble.android.common.BubbleComment;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.BubbleTag;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.UserInfo;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;
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
	private boolean bEnableTabListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubblelist);
		
		Intent intent = this.getIntent();
		id = Long.valueOf(intent.getLongExtra("id", -1));
		
		bEnableTabListener = false;
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < tabLabel.length; i++) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText(tabLabel[i]);
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }
		getSupportActionBar().setSelectedNavigationItem(0);
		getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_USE_LOGO | getSupportActionBar().DISPLAY_SHOW_HOME);
		getSupportActionBar().setLogo(R.drawable.bubble_logo);
		bEnableTabListener = true;
        
		progressBar = (ProgressBar) findViewById(R.id.progressBarBubbleList);
		lvBubbleList = (ListView) findViewById(R.id.listViewBubbleList);
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
			TextView tvCommentCount;
			LinearLayout llTag;
			ImageView ivBubblePhoto;
			ImageView ivBubbleUserImage;
			final ImageView ivBubbleFavorite;
			final LinearLayout llCommentButton;
			
			if(convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(BubbleListActivity.this);
				convertView = inflater.inflate(R.layout.listview_bubblelist, parent, false);
			}
			tvName = (TextView)convertView.findViewById(R.id.textViewBubbleListViewName);
			tvDate = (TextView)convertView.findViewById(R.id.textViewBubbleListViewDate);
			tvText = (TextView)convertView.findViewById(R.id.textViewBubbleListViewText);
			tvCommentCount = (TextView)convertView.findViewById(R.id.textViewBubbleListViewCommentCount);
			ivBubblePhoto = (ImageView) convertView.findViewById(R.id.imageViewBubbleImage);
			ivBubbleUserImage = (ImageView) convertView.findViewById(R.id.imageViewBubbleUserImage);
			ivBubbleFavorite = (ImageView) convertView.findViewById(R.id.imageViewBubbleListFavorite);
			llCommentButton = (LinearLayout)convertView.findViewById(R.id.linearLayoutBubbleListCommentButton);
			llTag = (LinearLayout)convertView.findViewById(R.id.linearLayoutBubbleListViewTag);
			llTag.removeAllViews();
			
			currentBubble = bubbleData.get(position);
			
			tvName.setText("" + currentBubble.getAuthorInfo().getName());
			tvDate.setText(currentBubble.getPostTime().toString());
			tvText.setText(currentBubble.getText());
			tvCommentCount.setText("" + currentBubble.getCommentCount());
			
			Bitmap userImage = currentBubble.getAuthorInfo().getImage();
			if(userImage != null) {
				ivBubbleUserImage.setImageBitmap(userImage);
			}
			
			Bitmap photo = currentBubble.getPhoto();
			if(photo != null) {
				ivBubblePhoto.setImageBitmap(photo);
				ivBubblePhoto.setVisibility(View.VISIBLE);
			} else
				ivBubblePhoto.setVisibility(View.GONE);
			
			if(currentBubble.isFavorite()) {
				ivBubbleFavorite.setImageResource(R.drawable.icon_star);
			} else {
				ivBubbleFavorite.setImageResource(R.drawable.icon_empty_star);
			}
						
			for(int i=0; i<currentBubble.getTag().size(); i++) {
				final BubbleTag tag = currentBubble.getRealTag().get(i);
				
				TextView tagText = new TextView(BubbleListActivity.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 10, 0);
				tagText.setLayoutParams(params);
				tagText.setBackgroundColor(0xFFFFFF00);
				tagText.setTextSize(TypedValue.COMPLEX_UNIT_PT, 6);
				tagText.setText(tag.getText());
				tagText.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(BubbleListActivity.this, TagSearchActivity.class);
						intent.putExtra("id",  id.longValue());
						intent.putExtra("tag", tag.getText());
						startActivity(intent);
					}
					
				});
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
			
			ivBubblePhoto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(BubbleListActivity.this, BubbleDetailActivity.class);
					intent.putExtra("bubbleid", currentBubble.getId());
					intent.putExtra("authorid", id.longValue());
					startActivity(intent);	
				}
			});
			
			llCommentButton.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						llCommentButton.setBackgroundColor(0xFF999999);
						llCommentButton.invalidate();
						break;
					case MotionEvent.ACTION_UP:
						llCommentButton.setBackgroundColor(0);
						llCommentButton.invalidate();
						Intent intent = new Intent(BubbleListActivity.this, BubbleDetailActivity.class);
						intent.putExtra("bubbleid", currentBubble.getId());
						intent.putExtra("authorid", id.longValue());
						startActivity(intent);	
						break;
					}
					
					return true;
				}
				
			});
			
			ivBubbleFavorite.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						ivBubbleFavorite.setBackgroundColor(0xFF999999);
						ivBubbleFavorite.invalidate();
						break;
					case MotionEvent.ACTION_UP:
						ivBubbleFavorite.setBackgroundColor(0);
						ivBubbleFavorite.invalidate();
						
						String pageUrl = Constant.SERVER_DOMAIN_URL + "/favorite";

						HttpPostUtil util = new HttpPostUtil();

						Map<String, String> param = new HashMap<String, String>();
						param.put("id", String.valueOf(id));
						param.put("bubbleid", String.valueOf(currentBubble.getId()));
						String ret = null;

						try {
							ret = util.httpPostData(pageUrl, param);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						currentBubble.setFavorite(!currentBubble.isFavorite());
						adapter.notifyDataSetChanged();
						break;						
					}
					
					return true;
				}
				
			});
						
			return convertView;
		}		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (bEnableTabListener) {
			Log.i("TAB", "position: " + tab.getPosition());
			switch (tab.getPosition()) {
			case 0:
				break;
			case 1:
			{
				Intent intent = new Intent(BubbleListActivity.this, ExploreActivity.class);
				intent.putExtra("id", id.longValue());
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
				break;
			case 2:
			{
				Intent intent = new Intent(BubbleListActivity.this, UserProfileActivity.class);
				intent.putExtra("id", id.longValue());
				intent.putExtra("selectedid", id.longValue());
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
				break;
			}
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (bEnableTabListener) {
			
		}		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		if (bEnableTabListener) {
			
		}		
	}
	
	private class CheckFavoriteTask extends AsyncTask<String, Integer, Long> {
		private String bubbleId;		
		private boolean isFavorite = false;
		@Override
		protected Long doInBackground(String... arg0) {
			bubbleId = arg0[0]; 
			checkFavorite();
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
		
		private void checkFavorite() {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/favorite";
			DefaultHttpClient client = new DefaultHttpClient();

			String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + id.toString() + "&bubbleid=" + bubbleId, client);
			if(response.equals("OK")) {
				//isFriend = true;
			} else {
				//isFriend = false;
			}
		}
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
			
			String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id=" + id + "&friend=true", client);
			bubbles = ObjectParsers.parseBubbleData(response);			
			
			for(int i=0; i<bubbles.size(); i++) {
				BubbleData bubble = bubbles.get(i);
				
				// Get UserInfo
				UserInfo userInfo = UserInfo.getUserInfo(bubble.getAuthorId());
				bubble.setAuthorInfo(userInfo);
				
				// Get Tag
				List<Long> longTag = bubble.getTag();
				if (longTag.size() != 0) {
					List<BubbleTag> bubbleTag = null;
					String tagStr = new String();
					
					for(int j=0; j<longTag.size(); j++) {
						if(j>0) tagStr += ",";
						tagStr += longTag.get(j);
					}
					bubbleTag = BubbleTag.getBubbleTagMultiple(tagStr);
					bubble.setRealTag(bubbleTag);
				}
				
				// Get Comments
				//List<BubbleComment> comments = BubbleComment.getCommentData(bubble.getId().longValue());
				//bubble.setComments(comments);
				
				// Check favorite
				String pageUrlFavorite = Constant.SERVER_DOMAIN_URL + "/favorite";
				DefaultHttpClient clientFavorite = new DefaultHttpClient();

				String responseFavorite = HttpGetUtil.doGetWithResponse(pageUrlFavorite + "?id=" + id.toString() + "&bubbleid=" + bubble.getId(), clientFavorite);
				if(responseFavorite.equals("OK")) {
					bubble.setFavorite(true);
				} else {
					bubble.setFavorite(false);
				}
				
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

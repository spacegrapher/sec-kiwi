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
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.BubbleTag;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.UserInfo;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;
import com.kiwi.bubble.android.member.UserPhotoActivity;
import com.kiwi.bubble.android.member.UserProfileActivity;

public class BubbleListActivity extends SherlockActivity implements
		ActionBar.TabListener {
	private static final int REQUEST_CODE_CREATE = 101;
	private static final int OPTIONS_MENU_REFRESH = 0;
	private static final int OPTIONS_MENU_TAG = 1;

	private Long id;
	private ListView lvBubbleList;
	private BubbleListAdapter adapter;
	private List<BubbleData> bubbles;
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
		for (int i = 0; i < Constant.tabLabel.length; i++) {
			ActionBar.Tab tab = getSupportActionBar().newTab();
			tab.setText(Constant.tabLabel[i]);
			tab.setTabListener(this);
			getSupportActionBar().addTab(tab);
		}
		getSupportActionBar().setSelectedNavigationItem(0);
		getSupportActionBar().setDisplayOptions(
				getSupportActionBar().DISPLAY_USE_LOGO
						| getSupportActionBar().DISPLAY_SHOW_HOME);
		getSupportActionBar().setLogo(R.drawable.bubble_logo);
		bEnableTabListener = true;

		progressBar = (ProgressBar) findViewById(R.id.progressBarBubbleList);
		lvBubbleList = (ListView) findViewById(R.id.listViewBubbleList);
		bubbles = new ArrayList<BubbleData>();
		new BackgroundTask().execute();

	}

	@Override
	protected void onResume() {
		super.onResume();
		//new BackgroundTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, OPTIONS_MENU_REFRESH, 0, "Refresh")
				.setIcon(R.drawable.icon_refresh)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(0, OPTIONS_MENU_TAG, 0, "Tag")
				.setIcon(R.drawable.icon_tag)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPTIONS_MENU_REFRESH:
			onClickRefresh(null);
			return true;
		case OPTIONS_MENU_TAG:
			Intent intent = new Intent(this, TagSelectActivity.class);
			intent.putExtra("id", id.longValue());
			startActivityForResult(intent, REQUEST_CODE_CREATE);
			return true;
		}
		return false;
	}

	public void onClickCreateBubble(View v) {
		Intent intent = new Intent(this, TagSelectActivity.class);
		intent.putExtra("id", id.longValue());
		startActivityForResult(intent, REQUEST_CODE_CREATE);
	}

	public void onClickRefresh(View v) {
		bubbles = new ArrayList<BubbleData>();
		new BackgroundTask().execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_CREATE) {
				new BackgroundTask().execute();
				Toast.makeText(BubbleListActivity.this, "버블이 생성되었습니다!", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	class BubbleListAdapter extends BaseAdapter {
		private List<BubbleData> bubbleData;
		private int loadedDataSize;

		public BubbleListAdapter(List<BubbleData> bubbleData) {
			super();
			this.bubbleData = bubbleData;
			this.loadedDataSize = 0;
		}

		public void changeData(List<BubbleData> bubbleData, int size) {
			this.bubbleData = bubbleData;
			this.loadedDataSize = size;
			this.notifyDataSetChanged();
		}
		@Override
		public int getCount() {
			return loadedDataSize;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
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

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater
						.from(BubbleListActivity.this);
				convertView = inflater.inflate(R.layout.listview_bubblelist,
						parent, false);
			}
			tvName = (TextView) convertView
					.findViewById(R.id.textViewBubbleListViewName);
			tvDate = (TextView) convertView
					.findViewById(R.id.textViewBubbleListViewDate);
			tvText = (TextView) convertView
					.findViewById(R.id.textViewBubbleListViewText);
			tvCommentCount = (TextView) convertView
					.findViewById(R.id.textViewBubbleListViewCommentCount);
			ivBubblePhoto = (ImageView) convertView
					.findViewById(R.id.imageViewBubbleImage);
			ivBubbleUserImage = (ImageView) convertView
					.findViewById(R.id.imageViewBubbleUserImage);
			ivBubbleFavorite = (ImageView) convertView
					.findViewById(R.id.imageViewBubbleListFavorite);
			llCommentButton = (LinearLayout) convertView
					.findViewById(R.id.linearLayoutBubbleListCommentButton);
			llTag = (LinearLayout) convertView
					.findViewById(R.id.linearLayoutBubbleListViewTag);
			llTag.removeAllViews();

			currentBubble = bubbleData.get(position);

			tvName.setText("" + currentBubble.getAuthorInfo().getName());
			tvDate.setText(currentBubble.getPostTime().toString());
			tvText.setText(currentBubble.getText());
			tvCommentCount.setText("" + currentBubble.getCommentCount());

			Bitmap userImage = currentBubble.getAuthorInfo().getImage();
			if (userImage != null) {
				ivBubbleUserImage.setImageBitmap(userImage);
			}

			Bitmap photo = currentBubble.getPhoto();
			if (photo != null) {
				ivBubblePhoto.setImageBitmap(photo);
				ivBubblePhoto.setVisibility(View.VISIBLE);
			} else
				ivBubblePhoto.setVisibility(View.GONE);

			if (currentBubble.isFavorite()) {
				ivBubbleFavorite.setImageResource(R.drawable.icon_star);
			} else {
				ivBubbleFavorite.setImageResource(R.drawable.icon_empty_star);
			}

			for (int i = 0; i < currentBubble.getTag().size(); i++) {
				final BubbleTag tag = currentBubble.getRealTag().get(i);

				TextView tagText = new TextView(BubbleListActivity.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 10, 0);
				tagText.setLayoutParams(params);
				tagText.setPadding(0, 5, 0, 5);
				tagText.setBackgroundColor(0xFFFFFF00);
				tagText.setTextSize(TypedValue.COMPLEX_UNIT_PT, 6);
				tagText.setText(tag.getText());
				tagText.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(BubbleListActivity.this,
								TagSearchActivity.class);
						intent.putExtra("id", id.longValue());
						intent.putExtra("tag", tag.getText());
						startActivity(intent);
					}

				});
				llTag.addView(tagText);
			}

			tvName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(BubbleListActivity.this,
							UserProfileActivity.class);
					intent.putExtra("id", id.longValue());
					intent.putExtra("selectedid", currentBubble.getAuthorId()
							.longValue());
					startActivity(intent);
					if (id.longValue() == currentBubble.getAuthorId()
							.longValue())
						overridePendingTransition(0, 0);
				}

			});

			ivBubbleUserImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(BubbleListActivity.this,
							UserPhotoActivity.class);
					intent.putExtra("id", currentBubble.getAuthorId()
							.longValue());
					intent.putExtra("editable", false);
					startActivity(intent);
				}
			});

			ivBubblePhoto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(BubbleListActivity.this,
							BubbleDetailActivity.class);
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
						Intent intent = new Intent(BubbleListActivity.this,
								BubbleDetailActivity.class);
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

						new CheckFavoriteTask().execute(currentBubble.getId());
						currentBubble.setFavorite(!currentBubble.isFavorite());
						if (currentBubble.isFavorite()) {							
							ivBubbleFavorite.setImageResource(R.drawable.icon_star);
						} else {
							ivBubbleFavorite.setImageResource(R.drawable.icon_empty_star);
						}
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
			switch (tab.getPosition()) {
			case 0:
				break;
			case 1: {
				Intent intent = new Intent(BubbleListActivity.this,
						ExploreActivity.class);
				intent.putExtra("id", id.longValue());
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
				break;
			case 2: {
				Intent intent = new Intent(BubbleListActivity.this,
						UserProfileActivity.class);
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

	private class CheckFavoriteTask extends AsyncTask<Long, Integer, Long> {
		@Override
		protected Long doInBackground(Long... arg0) {
			checkFavorite(arg0[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		private void checkFavorite(Long bubbleId) {
			String pageUrl = Constant.SERVER_DOMAIN_URL
					+ "/favorite";

			HttpPostUtil util = new HttpPostUtil();

			Map<String, String> param = new HashMap<String, String>();
			param.put("id", String.valueOf(id));
			param.put("bubbleid",
					String.valueOf(bubbleId));
			
			try {
				util.httpPostData(pageUrl, param);
			} catch (IOException e) {
				e.printStackTrace();
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
			adapter.changeData(bubbles, bubbles.size());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
			adapter = new BubbleListAdapter(bubbles);
			lvBubbleList.setAdapter(adapter);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			adapter.changeData(bubbles, values[0]);
			new BackgroundPhotoTask().execute(values[0]-1);
		}

		private void updateListView() {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/list";
			DefaultHttpClient client = new DefaultHttpClient();

			String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id="
					+ id + "&friend=true", client);
			bubbles = ObjectParsers.parseBubbleData(response);

			for (int i = 0; i < bubbles.size(); i++) {
				BubbleData bubble = bubbles.get(i);

				// Get UserInfo
				UserInfo userInfo = UserInfo.getUserInfo(bubble.getAuthorId());
				bubble.setAuthorInfo(userInfo);

				// Get Tag
				List<Long> longTag = bubble.getTag();
				if (longTag.size() != 0) {
					List<BubbleTag> bubbleTag = null;
					String tagStr = new String();

					for (int j = 0; j < longTag.size(); j++) {
						if (j > 0)
							tagStr += ",";
						tagStr += longTag.get(j);
					}
					bubbleTag = BubbleTag.getBubbleTagMultiple(tagStr);
					bubble.setRealTag(bubbleTag);
				}

				// Check favorite
				String pageUrlFavorite = Constant.SERVER_DOMAIN_URL
						+ "/favorite";
				DefaultHttpClient clientFavorite = new DefaultHttpClient();

				String responseFavorite = HttpGetUtil.doGetWithResponse(
						pageUrlFavorite + "?id=" + id.toString() + "&bubbleid="
								+ bubble.getId(), clientFavorite);
				if (responseFavorite.equals("OK")) {
					bubble.setFavorite(true);
				} else {
					bubble.setFavorite(false);
				}

				bubbles.set(i, bubble);
				publishProgress(i+1);
			}
		}
	}

	private class BackgroundPhotoTask extends AsyncTask<Integer, Integer, Long> {
		@Override
		protected Long doInBackground(Integer... arg0) {
			this.loadPhoto(arg0[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		private void loadPhoto(Integer index) {
			{
				BubbleData bubble = bubbles.get(index.intValue());

				UserInfo userInfo = bubble.getAuthorInfo();
				// Get User Image
				String userImageUrl = Constant.SERVER_DOMAIN_URL + "/userimage";
				DefaultHttpClient userImageClient = new DefaultHttpClient();
				String userImageRes = HttpGetUtil.doGetWithResponse(
						userImageUrl + "?id=" + bubble.getAuthorId(),
						userImageClient);
				if (!userImageRes.equals("")) {
					byte[] photoByte = Base64.decode(userImageRes,
							Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photoByte, 0,
							photoByte.length);
					
					if (bmp != null)
						userInfo.setImage(bmp);
				}
				bubble.setAuthorInfo(userInfo);

				// Get Photo
				String photoUrl = Constant.SERVER_DOMAIN_URL + "/image";
				DefaultHttpClient photoClient = new DefaultHttpClient();
				String photoRes = HttpGetUtil.doGetWithResponse(photoUrl
						+ "?bubbleid=" + bubble.getId(), photoClient);
				if (photoRes != null && !photoRes.equals("")) {
					byte[] photoByte = Base64.decode(photoRes, Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photoByte, 0,
							photoByte.length);

					bubble.setPhoto(bmp);
				}

				bubbles.set(index.intValue(), bubble);
			}
		}
	}
}

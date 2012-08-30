package com.kiwi.bubble.android.member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.kiwi.bubble.android.list.BubbleDetailActivity;
import com.kiwi.bubble.android.list.BubbleListActivity;

public class UserProfileActivity extends SherlockActivity implements
		ActionBar.TabListener {
	private static final int REQUEST_CODE_CREATE = 101;
	private static final int OPTIONS_MENU_REFRESH = 0;
	private static final int OPTIONS_MENU_TAG = 1;

	private Long id;
	private Long selectedId;
	private TextView tvName;
	private TextView tvEmail;
	private TextView tvPhotoEdit;
	private Button btnAddFriend;
	private ListView lvBubbleList;
	private LinearLayout llBody;
	private ProgressBar progressBar;
	private UserBubbleListAdapter adapter;
	private UserInfo userInfo;
	private List<BubbleData> bubbles;
	private boolean bEnableTabListener;
	private ImageView ivUserProfile;
	private boolean isFriend;

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
		tvPhotoEdit = (TextView) findViewById(R.id.textViewUserPhotoEdit);
		btnAddFriend = (Button) findViewById(R.id.buttonUserProfileAddFriend);
		lvBubbleList = (ListView) findViewById(R.id.listViewUserBubbleList);
		llBody = (LinearLayout) findViewById(R.id.linearLayoutUserProfileBody);
		progressBar = (ProgressBar) findViewById(R.id.progressBarUserProfile);
		ivUserProfile = (ImageView) findViewById(R.id.imageViewUserProfile);

		if (id.equals(selectedId)) {
			tvPhotoEdit.setVisibility(View.VISIBLE);
			btnAddFriend.setVisibility(View.INVISIBLE);
		} else {
			tvPhotoEdit.setVisibility(View.INVISIBLE);
			btnAddFriend.setVisibility(View.VISIBLE);
		}

		bEnableTabListener = false;
		if (id.equals(selectedId)) {
			getSupportActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_TABS);
			for (int i = 0; i < Constant.tabLabel.length; i++) {
				ActionBar.Tab tab = getSupportActionBar().newTab();
				tab.setText(Constant.tabLabel[i]);
				tab.setTabListener(this);
				getSupportActionBar().addTab(tab);
			}
			getSupportActionBar().setSelectedNavigationItem(2);
			getSupportActionBar().setDisplayOptions(
					getSupportActionBar().DISPLAY_USE_LOGO
							| getSupportActionBar().DISPLAY_SHOW_HOME);
			getSupportActionBar().setLogo(R.drawable.bubble_logo);
			bEnableTabListener = true;
		} else {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		lvBubbleList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long _id) {
						Intent intent = new Intent(UserProfileActivity.this,
								BubbleDetailActivity.class);
						intent.putExtra("bubbleid", bubbles.get(position)
								.getId());
						intent.putExtra("authorid", id.longValue());
						startActivity(intent);
					}

				});

		ivUserProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserProfileActivity.this,
						UserPhotoActivity.class);
				intent.putExtra("id", selectedId.longValue());
				if (id.equals(selectedId)) {
					intent.putExtra("editable", true);
				} else {
					intent.putExtra("editable", false);
				}
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		new BackgroundTask().execute();
		if (!id.equals(selectedId)) {
			new CheckFriendTask().execute();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (id.equals(selectedId)) {
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
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case OPTIONS_MENU_REFRESH:
			new BackgroundTask().execute();
			return true;
		case OPTIONS_MENU_TAG:
			intent = new Intent(this, TagSelectActivity.class);
			intent.putExtra("id", id.longValue());
			startActivityForResult(intent, REQUEST_CODE_CREATE);
			return true;
		case android.R.id.home:
			intent = new Intent();
			setResult(Activity.RESULT_CANCELED, intent);
			finish();
			return true;
		}

		return false;
	}

	public void onClickButtonBack(View v) {
		finish();
	}

	public void onClickAddFriend(View v) {
		if (isFriend) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setPositiveButton("확인",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							postFriendRequest();
						}
					})
					.setNegativeButton("취소",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).setMessage("정말 친구를 끊으시겠습니까?");
			builder.show();
		} else {
			postFriendRequest();
		}

	}

	private void postFriendRequest() {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/friend";

		HttpPostUtil util = new HttpPostUtil();

		Map<String, String> param = new HashMap<String, String>();
		param.put("id", String.valueOf(id));
		param.put("friendid", String.valueOf(selectedId));
		String ret = null;

		try {
			ret = util.httpPostData(pageUrl, param);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (!ret.equals("OK"))
			;
		new CheckFriendTask().execute();
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
						.from(UserProfileActivity.this);
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
			tvCommentCount.setText("" + currentBubble.getComments().size());

			final Bitmap userImage = currentBubble.getAuthorInfo().getImage();
			if (userImage != null) {
				ivBubbleUserImage.setImageBitmap(userImage);
			}

			final Bitmap photo = currentBubble.getPhoto();
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
				BubbleTag tag = currentBubble.getRealTag().get(i);

				TextView tagText = new TextView(UserProfileActivity.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 10, 0);
				tagText.setLayoutParams(params);
				tagText.setBackgroundColor(0xFFFFFF00);
				tagText.setTextSize(TypedValue.COMPLEX_UNIT_PT, 6);
				tagText.setText(tag.getText());
				llTag.addView(tagText);
			}

			ivBubbleUserImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(UserProfileActivity.this,
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
					Intent intent = new Intent(UserProfileActivity.this,
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
						Intent intent = new Intent(UserProfileActivity.this,
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

						String pageUrl = Constant.SERVER_DOMAIN_URL
								+ "/favorite";

						HttpPostUtil util = new HttpPostUtil();

						Map<String, String> param = new HashMap<String, String>();
						param.put("id", String.valueOf(id));
						param.put("bubbleid",
								String.valueOf(currentBubble.getId()));
						
						try {
							util.httpPostData(pageUrl, param);
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
			Log.i("USER", "tab : " + tab.getPosition() + " selected");
			switch (tab.getPosition()) {
			case 0: {
				Intent intent = new Intent(UserProfileActivity.this,
						BubbleListActivity.class);
				intent.putExtra("id", id.longValue());
				intent.putExtra("selectedid", id.longValue());
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
				break;
			case 1: {
				Intent intent = new Intent(UserProfileActivity.this,
						ExploreActivity.class);
				intent.putExtra("id", id.longValue());
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
				break;
			case 2:
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

	private class CheckFriendTask extends AsyncTask<String, Integer, Long> {
		@Override
		protected Long doInBackground(String... arg0) {
			checkFriend();
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);

			if (id.equals(selectedId)) {
				btnAddFriend.setVisibility(View.INVISIBLE);
			} else {
				if (isFriend) {
					btnAddFriend.setText("친구");
				} else {
					btnAddFriend.setText("친구 추가");
				}
				btnAddFriend.setVisibility(View.VISIBLE);
			}

			btnAddFriend.invalidate();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		private void checkFriend() {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/friend";
			DefaultHttpClient client = new DefaultHttpClient();

			String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id="
					+ id.toString() + "&friendid=" + selectedId.toString(),
					client);
			if (response.equals("OK")) {
				isFriend = true;
			} else {
				isFriend = false;
			}
		}
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

			final Bitmap userImage = userInfo.getImage();
			if (userImage != null) {
				ivUserProfile.setImageBitmap(userImage);
			}

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

			// Get User Image
			String userImageUrl = Constant.SERVER_DOMAIN_URL + "/userimage";
			DefaultHttpClient userImageClient = new DefaultHttpClient();
			String userImageRes = HttpGetUtil.doGetWithResponse(userImageUrl
					+ "?id=" + selectedId.longValue(), userImageClient);
			// Log.i("USER", "userImageRes: " + userImageRes);
			if (!userImageRes.equals("")) {
				byte[] photoByte = Base64.decode(userImageRes, Base64.DEFAULT);
				Bitmap bmp = BitmapFactory.decodeByteArray(photoByte, 0,
						photoByte.length);

				userInfo.setImage(bmp);
			}
		}

		private void updateListView() {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/list";
			DefaultHttpClient client = new DefaultHttpClient();

			String response = HttpGetUtil.doGetWithResponse(pageUrl + "?id="
					+ selectedId.toString() + "&friend=false", client);
			bubbles = ObjectParsers.parseBubbleData(response);

			for (int i = 0; i < bubbles.size(); i++) {
				BubbleData bubble = bubbles.get(i);

				// Get UserInfo
				UserInfo userInfo = UserInfo.getUserInfo(bubble.getAuthorId());

				// Get User Image
				String userImageUrl = Constant.SERVER_DOMAIN_URL + "/userimage";
				DefaultHttpClient userImageClient = new DefaultHttpClient();
				String userImageRes = HttpGetUtil.doGetWithResponse(
						userImageUrl + "?id=" + bubble.getAuthorId(),
						userImageClient);
				// Log.i("USER", "userImageRes: " + userImageRes);
				if (!userImageRes.equals("")) {
					byte[] photoByte = Base64.decode(userImageRes,
							Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photoByte, 0,
							photoByte.length);

					userInfo.setImage(bmp);
				}

				bubble.setAuthorInfo(userInfo);

				// Get Tag
				List<Long> longTag = bubble.getTag();
				List<BubbleTag> bubbleTag = new ArrayList<BubbleTag>();

				for (int j = 0; j < longTag.size(); j++) {
					BubbleTag tag = BubbleTag.getBubbleTag(longTag.get(j));
					bubbleTag.add(tag);
				}
				bubble.setRealTag(bubbleTag);

				// Get Comments
				List<BubbleComment> comments = BubbleComment
						.getCommentData(bubble.getId().longValue());
				bubble.setComments(comments);

				// Get Photo
				String photoUrl = Constant.SERVER_DOMAIN_URL + "/image";
				DefaultHttpClient photoClient = new DefaultHttpClient();
				String photoRes = HttpGetUtil.doGetWithResponse(photoUrl
						+ "?bubbleid=" + bubble.getId(), photoClient);
				if (!photoRes.equals("")) {
					byte[] photoByte = Base64.decode(photoRes, Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photoByte, 0,
							photoByte.length);

					bubble.setPhoto(bmp);
				}

				bubbles.set(i, bubble);
			}
		}
	}
}

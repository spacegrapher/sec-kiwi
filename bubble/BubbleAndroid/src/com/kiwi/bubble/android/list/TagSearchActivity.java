package com.kiwi.bubble.android.list;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.Base64;
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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kiwi.bubble.android.R;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.BubbleTag;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.UserInfo;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;
import com.kiwi.bubble.android.member.UserPhotoActivity;
import com.kiwi.bubble.android.member.UserProfileActivity;

public class TagSearchActivity extends SherlockActivity {
	// private static final int REQUEST_CODE_CREATE = 101;
	private Long id;
	private String strInputTag;
	private ListView lvBubbleList;
	private BubbleListAdapter adapter;
	private List<BubbleData> bubbles;
	private ProgressBar progressBar;
	private TextView tvTagSearch;
	private List<String> strTagList;
	private boolean isMultipleTag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tagsearch);

		Intent intent = this.getIntent();
		id = Long.valueOf(intent.getLongExtra("id", -1));
		strInputTag = intent.getStringExtra("tag");
		if (strInputTag == null) {
			strTagList = Arrays.asList(intent.getStringArrayExtra("tags"));
			isMultipleTag = true;
		} else {
			strTagList = new ArrayList<String>();
			strTagList.add(strInputTag);
		}

		progressBar = (ProgressBar) findViewById(R.id.progressBarTagSearchList);
		tvTagSearch = (TextView) findViewById(R.id.textViewTagSearch);
		lvBubbleList = (ListView) findViewById(R.id.listViewTagSearchList);
		lvBubbleList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long _id) {
						Intent intent = new Intent(TagSearchActivity.this,
								BubbleDetailActivity.class);
						intent.putExtra("bubbleid", bubbles.get(position)
								.getId());
						intent.putExtra("authorid", id.longValue());
						startActivity(intent);
					}

				});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		bubbles = new ArrayList<BubbleData>();
		new BackgroundTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent();
			setResult(Activity.RESULT_CANCELED, intent);
			finish();
			return true;
		}

		return false;
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
						.from(TagSearchActivity.this);
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

				TextView tagText = new TextView(TagSearchActivity.this);
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

			tvName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(TagSearchActivity.this,
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
					Intent intent = new Intent(TagSearchActivity.this,
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
					Intent intent = new Intent(TagSearchActivity.this,
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
						Intent intent = new Intent(TagSearchActivity.this,
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
			setTagListText(strTagList);
		}

		private void setTagListText(List<String> selectedTagList) {
			if (selectedTagList.size() <= 0)
				return;

			String text = "태그 검색: ";

			int[] start = new int[selectedTagList.size()];
			int[] end = new int[selectedTagList.size()];
			start[0] = text.length();
			for (int i = 0; i < selectedTagList.size(); i++) {
				if (i > 0) {
					text += " ";
					start[i] = end[i - 1] + 1;
				}
				text += selectedTagList.get(i);
				end[i] = start[i] + selectedTagList.get(i).length();
			}

			tvTagSearch.setText(text, TextView.BufferType.SPANNABLE);
			Spannable sText = (Spannable) tvTagSearch.getText();

			for (int i = 0; i < selectedTagList.size(); i++) {
				sText.setSpan(new BackgroundColorSpan(Color.YELLOW), start[i],
						end[i], 0);
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			adapter.changeData(bubbles, values[0]);
			new BackgroundPhotoTask().execute(values[0] - 1);
		}

		private void updateListView() {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/tag/search";
			DefaultHttpClient client = new DefaultHttpClient();

			String strInputTagNoSpace = new String();

			if (isMultipleTag) {
				for (int i = 0; i < strTagList.size(); i++) {
					String strTemp = strTagList.get(i).replace(" ", "%20");
					if (i > 0)
						strInputTagNoSpace += ",";
					strInputTagNoSpace += strTemp;
				}
			} else {
				strInputTagNoSpace = strInputTag.replace(" ", "%20");
			}

			String response = HttpGetUtil.doGetWithResponse(pageUrl + "?tag="
					+ strInputTagNoSpace, client);
			bubbles = ObjectParsers.parseBubbleData(response);

			for (int i = 0; i < bubbles.size(); i++) {
				BubbleData bubble = bubbles.get(i);

				// Get UserInfo
				UserInfo userInfo = UserInfo.getUserInfo(bubble.getAuthorId());
				bubble.setAuthorInfo(userInfo);

				// Get Tag
				List<Long> longTag = bubble.getTag();
				List<BubbleTag> bubbleTag = new ArrayList<BubbleTag>();

				for (int j = 0; j < longTag.size(); j++) {
					BubbleTag tag = BubbleTag.getBubbleTag(longTag.get(j));
					bubbleTag.add(tag);
				}
				bubble.setRealTag(bubbleTag);

				bubbles.set(i, bubble);
				publishProgress(i + 1);
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
				// Log.i("USER", "userImageRes: " + userImageRes);
				if (!userImageRes.equals("")) {
					byte[] photoByte = Base64.decode(userImageRes,
							Base64.DEFAULT);
					Bitmap bmp = BitmapFactory.decodeByteArray(photoByte, 0,
							photoByte.length);

					userInfo.setImage(bmp);
				}
				bubble.setAuthorInfo(userInfo);

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

				bubbles.set(index.intValue(), bubble);
			}
		}
	}
}

package com.kiwi.bubble.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.list.BubbleListActivity;
import com.kiwi.bubble.android.list.TagSearchActivity;
import com.kiwi.bubble.android.member.UserProfileActivity;

public class ExploreActivity extends SherlockActivity implements
		ActionBar.TabListener {
	private static final int REQUEST_CODE_CREATE = 101;
	private static final int OPTIONS_MENU_REFRESH = 0;
	private static final int OPTIONS_MENU_TAG = 1;

	private Long id;
	private ListView lvBubbleList;
	private ExploreAdapter adapter;
	private boolean bEnableTabListener;
	private EditText etExploreTag;
	private List<ExploreData> exploreData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explore);

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
		getSupportActionBar().setSelectedNavigationItem(1);
		getSupportActionBar().setDisplayOptions(
				getSupportActionBar().DISPLAY_USE_LOGO
						| getSupportActionBar().DISPLAY_SHOW_HOME);
		getSupportActionBar().setLogo(R.drawable.bubble_logo);
		bEnableTabListener = true;

		etExploreTag = (EditText) findViewById(R.id.editTextExploreTag);
		etExploreTag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ExploreActivity.this,
						TagSelectActivity.class);
				intent.putExtra("id", id.longValue());
				intent.putExtra("search", true);
				startActivityForResult(intent, REQUEST_CODE_CREATE);
				overridePendingTransition(0, 0);
			}
		});

		setUpTempData();
		lvBubbleList = (ListView) findViewById(R.id.listViewExplore);
		lvBubbleList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long _id) {
						if (position == 0) {
							// Header View - do nothing
							return;
						}
						List<String> tags = exploreData.get(position - 1)
								.getTags();
						String[] tagArray = new String[tags.size()];
						int index = 0;
						for (int i = 0; i < tags.size(); i++) {
							tagArray[index++] = tags.get(i);
						}
						Intent intent = new Intent(ExploreActivity.this,
								TagSearchActivity.class);
						intent.putExtra("id", id);
						intent.putExtra("tags", tagArray);
						startActivity(intent);
					}

				});

		TextView tvListTitle = new TextView(ExploreActivity.this);
		tvListTitle.setPadding(5, 0, 5, 0);
		tvListTitle.setText("인기있는 태그");
		tvListTitle.setBackgroundColor(0xFFECF9C6);
		tvListTitle.setTextColor(0xFF000000);
		tvListTitle.setTypeface(Typeface.DEFAULT_BOLD);
		lvBubbleList.addHeaderView(tvListTitle);

		adapter = new ExploreAdapter();
		lvBubbleList.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		// new BackgroundTask().execute();
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
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_CREATE) {
				onClickRefresh(null);
				Toast.makeText(ExploreActivity.this, "버블이 생성되었습니다!", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	class ExploreData {
		private int backgroundResource;
		private List<String> tags;

		public ExploreData(int backgroundResource, List<String> tags) {
			super();
			this.backgroundResource = backgroundResource;
			this.tags = tags;
		}

		public int getBackgroundResource() {
			return backgroundResource;
		}

		public void setBackgroundResource(int backgroundResource) {
			this.backgroundResource = backgroundResource;
		}

		public List<String> getTags() {
			return tags;
		}

		public void setTags(List<String> tags) {
			this.tags = tags;
		}

	}

	private void setUpTempData() {
		exploreData = new ArrayList<ExploreData>();

		// Galaxy Note 2
		{
			int backgroundResource = R.drawable.explore_note2;
			List<String> tags = new ArrayList<String>();
			tags.add("삼성");
			tags.add("안드로이드");
			tags.add("갤럭시노트");
			ExploreData data = new ExploreData(backgroundResource, tags);
			exploreData.add(data);
		}

		// Coffee
		{
			int backgroundResource = R.drawable.explore_coffee;
			List<String> tags = new ArrayList<String>();
			tags.add("커피");
			tags.add("디지털시티");
			tags.add("커피와사람들");
			ExploreData data = new ExploreData(backgroundResource, tags);
			exploreData.add(data);
		}

		// QPR
		{
			int backgroundResource = R.drawable.explore_qpr;
			List<String> tags = new ArrayList<String>();
			tags.add("EPL");
			tags.add("축구");
			tags.add("QPR");
			tags.add("박지성");
			ExploreData data = new ExploreData(backgroundResource, tags);
			exploreData.add(data);
		}
	}

	class ExploreAdapter extends BaseAdapter {

		public ExploreAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return exploreData.size();
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
			LinearLayout llExploreList;
			LinearLayout llTagList;

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater
						.from(ExploreActivity.this);
				convertView = inflater.inflate(R.layout.listview_explore,
						parent, false);
			}

			llExploreList = (LinearLayout) convertView
					.findViewById(R.id.linearLayoutExploreList);
			llTagList = (LinearLayout) convertView
					.findViewById(R.id.linearLayoutTagList);
			llTagList.removeAllViews();

			final ExploreData currentData = exploreData.get(position);

			llExploreList.setBackgroundResource(currentData
					.getBackgroundResource());

			for (int i = 0; i < currentData.getTags().size(); i++) {
				final String text = currentData.getTags().get(i);
				TextView tagText = new TextView(ExploreActivity.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 5, 0);
				tagText.setLayoutParams(params);
				tagText.setPadding(5, 5, 5, 5);
				tagText.setBackgroundColor(0x80000000);
				tagText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
				tagText.setTextColor(0xFFFFFFFF);
				tagText.setText(text);
				tagText.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ExploreActivity.this,
								TagSearchActivity.class);
						intent.putExtra("id", id.longValue());
						intent.putExtra("tag", text);
						startActivity(intent);
					}

				});
				llTagList.addView(tagText);
			}

			return convertView;
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (bEnableTabListener) {
			Log.i("TAB", "position: " + tab.getPosition());
			switch (tab.getPosition()) {
			case 0: {
				Intent intent = new Intent(ExploreActivity.this,
						BubbleListActivity.class);
				intent.putExtra("id", id.longValue());
				intent.putExtra("selectedid", id.longValue());
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
				break;
			case 1:
				break;
			case 2:
				Intent intent = new Intent(ExploreActivity.this,
						UserProfileActivity.class);
				intent.putExtra("id", id.longValue());
				intent.putExtra("selectedid", id.longValue());
				startActivity(intent);
				overridePendingTransition(0, 0);
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

}

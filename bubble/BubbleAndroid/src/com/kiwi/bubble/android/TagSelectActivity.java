package com.kiwi.bubble.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kiwi.bubble.android.common.BubbleTag;
import com.kiwi.bubble.android.list.TagSearchActivity;

public class TagSelectActivity extends SherlockActivity {
	private static final int REQUEST_BUBBLE_CREATE = 101;
	public static final String ACTION_KILL_COMMAND = "ACTION_KILL_COMMAND";
	public static final String ACTION_KILL_DATATYPE = "content://ACTION_KILL_DATATYPE";
	
	private long id;
	private boolean isSearch;
	private EditText etAddTag;
	private ArrayList<String> tagList;
	private List<BubbleTag> selectedTagList = new ArrayList<BubbleTag>();
	
	private List<BubbleTag> bubbleTagList = new ArrayList<BubbleTag>();
	private List<BubbleTag> bubbleTagListPartial = new ArrayList<BubbleTag>();
	private boolean bSameTagExists = false;
	private BubbleTagAdapter adapter;
	private ListView lvTagList;
	private TextView tvSelectedTag;
	
	private KillReceiver mKillReceiver;
	
	/*+GPS+*/
	private LocationListener locListenD;
	private Button buttonLocation;
	public TextView tvLatitude;
	public TextView tvLongitude;
	public Location loc;
	/*-GPS-*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tagselect);
		
		Intent intent = this.getIntent();
		id = intent.getLongExtra("id", -1);
		isSearch = intent.getBooleanExtra("search", false);
		
		etAddTag = (EditText) findViewById(R.id.editTextAddTag);
		if (isSearch) {
			etAddTag.setFocusableInTouchMode(true);
			etAddTag.requestFocus();
		}
		tvSelectedTag = (TextView) findViewById(R.id.textViewSelectedTag);
		
		tagList = new ArrayList<String>();
		lvTagList = (ListView) findViewById(R.id.listViewTagSuggest);
		lvTagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long _id) {
				BubbleTag currentTag = null;				
				
				if (etAddTag.getText().toString().isEmpty()) {
					// EditText is empty
					// First row is add location
					if (position == 0) {
						// Add location
					} else {
						currentTag = bubbleTagListPartial.get(position - 1);
					}
				} else if (checkAddNewTag()) {
					// First row is new tag
					if (position == 0) {
						currentTag = new BubbleTag(BubbleTag.TAG_TYPE_TEXT);
						currentTag.setText(etAddTag.getText().toString());
					} else {
						currentTag = bubbleTagListPartial.get(position - 1);
					}
				} else {
					// First row is the first existing tag
					currentTag = bubbleTagListPartial.get(position);
				}
				
				
				if (selectedTagList.contains(currentTag)) {
					selectedTagList.remove(currentTag);
				} else {
					selectedTagList.add(currentTag);
				}
				
				updateSelectedTextView();
				etAddTag.setText("");
				etAddTag.clearFocus();
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etAddTag.getWindowToken(), 0);
			}
			
		});
		
		adapter = new BubbleTagAdapter(bubbleTagListPartial);
		lvTagList.setAdapter(adapter);
		
		etAddTag.addTextChangedListener(new TextWatcher() {
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	        	AlterAdapter();
	        	lvTagList.setSelection(0);
	        }
	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after)
	        {
	        }

	        @Override
	        public void afterTextChanged(Editable s)
	        {               
	        }
		}
	    );
		
		mKillReceiver = new KillReceiver();
	    registerReceiver(mKillReceiver, IntentFilter.create(ACTION_KILL_COMMAND, ACTION_KILL_DATATYPE));
	    
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setTitle("태그 선택하기");
	    
	    new BackgroundTask().execute();
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    unregisterReceiver(mKillReceiver);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Create")
		.setIcon(R.drawable.icon_new_tag)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.toString().equals("Create")) {
			String[] tagArray = new String[selectedTagList.size()];
			int index = 0;
			for(int i=0; i<selectedTagList.size(); i++) {
				tagArray[index++] = selectedTagList.get(i).getText();
			}
			Intent intent = new Intent(this, TagSearchActivity.class);
			intent.putExtra("id", id);
			intent.putExtra("tags", tagArray);			
			startActivityForResult(intent, REQUEST_BUBBLE_CREATE);
		} else if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent();
			setResult(Activity.RESULT_CANCELED, intent);
			finish();
		}
		return true;
	}
	
	public void onClickAddLocation(View v){
        // �ؽ�Ʈ�並 ã�´�
        tvLatitude = (TextView)findViewById(R.id.tvLatitude);
        tvLongitude = (TextView)findViewById(R.id.tvLongitude);
        
        Log.d("GetLocation","1!!");
        LocationManager lm =(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        	Log.d("GetLocation","ERROR!!");
        }
        
        
        Log.d("GetLocation","1-1!!");
        Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        Log.d("GetLocation","2!!");

        //double latitude = loc.getLatitude();//37.519576;
        //double longitude = loc.getLongitude();//126.940245;
       
        //String pos = String.format("geo:%f,%f?z=16", latitude, longitude);
         //Uri uri = Uri.parse(pos);
        //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        //startActivity(intent);
        
        
        Intent intent = new Intent(TagSelectActivity.this,BubbleMap.class);
        startActivity(intent);
        
	}
		
        private class DispLocListener implements LocationListener {
        	public void onLocationChanged(Location location) {
        		tvLatitude.setText(Double.toString(location.getLatitude()));
        		tvLongitude.setText(Double.toString(location.getLongitude()));  
        	}
        	public void onProviderDisabled(String provider) { 
        	}
        	public void onProviderEnabled(String provider) {
        	}
        	public void onStatusChanged(String provider, int status, Bundle extras) { 
        	}
        }
        
	private final class KillReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	finish();
	    }
	}
	
	private void updateSelectedTextView() {
		String text = new String();
		
		int[] start = new int[selectedTagList.size()];
		int[] end = new int[selectedTagList.size()];
		start[0] = 0;
		for(int i=0; i<selectedTagList.size(); i++) {
			if (i > 0) {
				text += " ";
				start[i] = end[i-1] + 1;
			}
			text += selectedTagList.get(i).getText();
			end[i] = start[i] + selectedTagList.get(i).getText().length();
		}		
		
		tvSelectedTag.setText(text, TextView.BufferType.SPANNABLE);
		Spannable sText = (Spannable)tvSelectedTag.getText();
		
		for(int i=0; i<selectedTagList.size(); i++) {
			sText.setSpan(new BackgroundColorSpan(Color.YELLOW), start[i], end[i], 0);
		}
	}
	
	private boolean checkAddNewTag() {
		boolean isEditTextEmpty = etAddTag.getText().toString().isEmpty();
		return !isEditTextEmpty && !bSameTagExists;
	}
	
	private void AlterAdapter() {
        if (etAddTag.getText().toString().isEmpty()) {
        	bubbleTagListPartial.clear();
        	for (int i = 0; i < bubbleTagList.size(); i++) {
                bubbleTagListPartial.add(bubbleTagList.get(i));
            }
            adapter.notifyDataSetChanged();
        }
        else {
        	bubbleTagListPartial.clear();
        	bSameTagExists = false;
            for (int i = 0; i < bubbleTagList.size(); i++) {
                if (bubbleTagList.get(i).getText().toUpperCase().contains(etAddTag.getText().toString().toUpperCase())) {
                	bubbleTagListPartial.add(bubbleTagList.get(i));
                }
                if (bubbleTagList.get(i).getText().equals(etAddTag.getText().toString())) {
                	bSameTagExists = true;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }
	
	class BubbleTagAdapter extends BaseAdapter {
		private List<BubbleTag> bubbleTag;
		private boolean bAddLocation = false;
		private boolean bAddNewTag = false;
		
		public BubbleTagAdapter(List<BubbleTag> bubbleTag) {
			super();
			this.bubbleTag = bubbleTag;			
		}

		@Override
		public int getCount() {
			int count = bubbleTag.size();
			bAddLocation = false;
			bAddNewTag = false;
			if (etAddTag.getText().toString().isEmpty()) {
				bAddLocation = true;
				count += 1;
			} else if (!bSameTagExists) {
				bAddNewTag = true;
				count += 1;
			}
			
			return count;
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
			final BubbleTag currentTag;
			int actualPosition;
			ImageView ivTagIcon;
			TextView tvTagName;
			
			if(convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(TagSelectActivity.this);
				convertView = inflater.inflate(R.layout.listview_tagselect, parent, false);
			}
			ivTagIcon = (ImageView) convertView.findViewById(R.id.imageViewTagIcon);
			tvTagName = (TextView) convertView.findViewById(R.id.textViewTagName);
			
			
			if (bAddLocation || bAddNewTag) {
				if (position == 0) {
					actualPosition = position;
					if(bAddLocation) {
						ivTagIcon.setVisibility(View.VISIBLE);
						tvTagName.setText("위치 태그 추가");
					} else if(bAddNewTag) {
						ivTagIcon.setVisibility(View.VISIBLE);
						tvTagName.setText("새 태그 \"" + etAddTag.getText().toString() + "\" 추가");
					}
				} else {
					actualPosition = position - 1;
					
					currentTag = bubbleTag.get(actualPosition);
					tvTagName.setText(currentTag.getText());
					ivTagIcon.setVisibility(View.INVISIBLE);
				}
			} else {
				actualPosition = position;
				currentTag = bubbleTag.get(actualPosition);
				tvTagName.setText(currentTag.getText());
				ivTagIcon.setVisibility(View.INVISIBLE);
			}
			
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
			//progressBar.setVisibility(View.INVISIBLE);
			AlterAdapter();
			//adapter = new BubbleTagAdapter(bubbleTagListPartial);
			//lvTagList.setAdapter(adapter);
			//adapter.notifyDataSetChanged();
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
		
		private void updateListView() {
			bubbleTagList = BubbleTag.getAllBubbleTags();
		}
	}
}

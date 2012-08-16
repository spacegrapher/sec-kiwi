package com.kiwi.bubble.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class TagSelectActivity extends SherlockActivity {
	private static final int REQUEST_BUBBLE_CREATE = 101;
	public static final String ACTION_KILL_COMMAND = "ACTION_KILL_COMMAND";
	public static final String ACTION_KILL_DATATYPE = "content://ACTION_KILL_DATATYPE";
	
	long id;
	//String strEmail;
	EditText etAddTag;
	Button btnAddTag;
	GridView gvSelectedTag;
	ArrayAdapter<String> gridAdapter;
	ArrayList<String> tagList;
	
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
		//strEmail = intent.getStringExtra("email");
		id = intent.getLongExtra("id", -1);
		
		etAddTag = (EditText) findViewById(R.id.editTextAddTag);
		btnAddTag = (Button) findViewById(R.id.buttonAddTag);
		btnAddTag.setEnabled(false);
		gvSelectedTag = (GridView) findViewById(R.id.gridViewSelectedTag);
		
		tagList = new ArrayList<String>();
		gridAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tagList);
		gvSelectedTag.setAdapter(gridAdapter);
		
		etAddTag.addTextChangedListener(new TextWatcher() {
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	            checkEnableAddTagButton();
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
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    unregisterReceiver(mKillReceiver);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Create")
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("MENU", "item: " + item.toString() + ", id: " + item.getGroupId() + ", order: " + item.getOrder());
		if (item.toString().equals("Create")) {
			String[] tagArray = (String[]) tagList.toArray(new String[tagList.size()]);
			Intent intent = new Intent(this, BubbleCreateActivity.class);
			intent.putExtra("tag", tagArray);
			intent.putExtra("id", id);
			startActivityForResult(intent, REQUEST_BUBBLE_CREATE);
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
        		// TextView�� ������Ʈ �Ѵ�.
        		Log.d("GetLocation","5!!");
        		tvLatitude.setText(Double.toString(location.getLatitude()));
        		tvLongitude.setText(Double.toString(location.getLongitude()));  
        		Log.d("GetLocation","6!!");
        	}
        	public void onProviderDisabled(String provider) { 
        	}
        	public void onProviderEnabled(String provider) {
        	}
        	public void onStatusChanged(String provider, int status, Bundle extras) { 
        	}
        }
        
	public void checkEnableAddTagButton() {
		boolean isEmpty = etAddTag.getText().toString().isEmpty();
		
		if(!isEmpty) {
			btnAddTag.setEnabled(true);
		} else {
			btnAddTag.setEnabled(false);
		}
	}

	public void onClickAddTag(View v) {
		if(etAddTag.getText().toString().isEmpty())
			return;
		
		tagList.add(etAddTag.getText().toString());
		etAddTag.setText("");
		gridAdapter.notifyDataSetChanged();
	}
	
	public void onClickButtonCreateBack(View v) {
		Intent intent = new Intent();
		setResult(Activity.RESULT_CANCELED, intent);
		finish();
	}
	
	public void onClickButtonCreatePost(View v) {
		String[] tagArray = (String[]) tagList.toArray(new String[tagList.size()]);
		Intent intent = new Intent(this, BubbleCreateActivity.class);
		intent.putExtra("tag", tagArray);
		//intent.putExtra("email", strEmail);
		intent.putExtra("id", id);
		startActivityForResult(intent, REQUEST_BUBBLE_CREATE);
	}
	
	private final class KillReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	finish();
	    }
	}
}

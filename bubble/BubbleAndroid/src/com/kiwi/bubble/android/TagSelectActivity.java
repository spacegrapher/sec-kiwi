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

public class TagSelectActivity extends Activity {
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
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    unregisterReceiver(mKillReceiver);
	}
	
	public void onClickAddLocation(View v){
        // 텍스트뷰를 찾는다
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
        if(loc!=null) 
        {
            // TextView를 채운다
           	 tvLatitude.setText(Double.toString(loc.getLatitude()));
           	tvLongitude.setText(Double.toString(loc.getLongitude()));         

               // Location Manager에게 위치정보를 업데이트해달라고 요청한다.
           	Log.d("GetLocation","3!!");
               locListenD = new DispLocListener();
               Log.d("GetLocation","4!!");
               lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000L, 10.0f, locListenD);    
               Log.d("GetLocation","4-1!!");
           }
           else {
           	Log.d("location", "location is null");
           	tvLatitude.setText("0");
           	tvLongitude.setText("0");
           }
	}
		
        private class DispLocListener implements LocationListener {
        	public void onLocationChanged(Location location) {
        		// TextView를 업데이트 한다.
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

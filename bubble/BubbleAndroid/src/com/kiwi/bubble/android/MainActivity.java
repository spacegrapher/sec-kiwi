package com.kiwi.bubble.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

import com.kiwi.bubble.android.common.BubbleComment;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.BubbleTag;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.TEA;
import com.kiwi.bubble.android.common.UserInfo;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;
import com.kiwi.bubble.android.common.parser.ObjectParsers;
import com.kiwi.bubble.android.member.SignupActivity;
import com.kiwi.bubble.android.list.BubbleListActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int REQUEST_CODE_SIGNUP = 101;
	private EditText editTextEmail;
	private EditText editTextPassword;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Typeface tf = Typeface.createFromAsset(getAssets(), "font/Roboto-Regular.ttf");
        
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        
        editTextEmail.setTypeface(tf);
        editTextPassword.setTypeface(tf);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onClickLogin(View v) {
    	new BackgroundTask().execute();
    }
    
    public void onClickSignup(View v) {
    	Intent intent = new Intent(this, SignupActivity.class);
		startActivityForResult(intent, REQUEST_CODE_SIGNUP);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK){
			if (requestCode == REQUEST_CODE_SIGNUP) {
				Toast.makeText(MainActivity.this, "Account Created!", 0).show();
			}
		}
	}
    
	private class BackgroundTask extends AsyncTask<String, Integer, Long> {
		String resultStr;
		ProgressDialog progressDialog;
		
		@Override
		protected Long doInBackground(String... arg0) {
			resultStr = new String();
			String pageUrl = Constant.SERVER_DOMAIN_URL;
	    	TEA tea = new TEA(Constant.TEA_ENCRYPT_KEY.getBytes());
			
			String strEmail = editTextEmail.getText().toString();
			String strPassword = new String(tea.encrypt(editTextPassword.getText().toString().getBytes()));
			
			if(strEmail.isEmpty()) {
				Toast.makeText(MainActivity.this, "Please enter email address", 0).show();
			} else if(strPassword.isEmpty()) {
				Toast.makeText(MainActivity.this, "Please enter password", 0).show();
			} else {		
				HttpPostUtil util = new HttpPostUtil();
				HashMap result = new HashMap();
				Map<String, String> param = new HashMap<String, String>();
				param.put("email", strEmail);
				param.put("password", strPassword);
				
				try {
					resultStr = util.httpPostData(pageUrl, param);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			progressDialog.hide();
			if (resultStr.isEmpty()) {
				Toast.makeText(MainActivity.this, "Please enter correct information", 0).show();
			} else {
				Intent intent = new Intent(MainActivity.this, BubbleListActivity.class);
				intent.putExtra("id", Long.valueOf(resultStr));
				startActivity(intent);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Logging in...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
		
	}
}

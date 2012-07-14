package com.kiwi.bubble.android;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;
import com.kiwi.bubble.android.member.SignupActivity;
import com.kiwi.bubble.android.list.BubbleListActivity;

import android.os.Bundle;
import android.app.Activity;
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
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {
	private static final int REQUEST_CODE_SIGNUP = 101;
	private TextView textViewWelcome;
	private EditText editTextEmail;
	private EditText editTextPassword;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Typeface tf = Typeface.createFromAsset(getAssets(), "font/Roboto-Regular.ttf");
        
        textViewWelcome = (TextView)findViewById(R.id.textViewWelcome);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        
        textViewWelcome.setTypeface(tf);
        editTextEmail.setTypeface(tf);
        editTextPassword.setTypeface(tf);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onClickLogin(View v) {
    	String pageUrl = Constant.SERVER_DOMAIN_URL;
		
		String strEmail = editTextEmail.getText().toString();
		String strPassword = editTextPassword.getText().toString();
		
		if(strEmail.isEmpty()) {
			Toast.makeText(MainActivity.this, "Please enter email address", 0).show();
		} else if(strPassword.isEmpty()) {
			Toast.makeText(MainActivity.this, "Please enter password", 0).show();
		} else {		
			HttpPostUtil util = new HttpPostUtil();
			HashMap result = new HashMap();
			String resultStr = new String();
			Map<String, String> param = new HashMap<String, String>();
			param.put("email", strEmail);
			param.put("password", strPassword);
			
			try {
				resultStr = util.httpPostData(pageUrl, param);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (resultStr.isEmpty()) {
				Toast.makeText(MainActivity.this, "Please enter correct information", 0).show();
			} else {
				Intent intent = new Intent(this, BubbleListActivity.class);
				intent.putExtra("email", resultStr);
				startActivity(intent);
			}
		}
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
    
    
}

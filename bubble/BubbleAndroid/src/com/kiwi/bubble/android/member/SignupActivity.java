package com.kiwi.bubble.android.member;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kiwi.bubble.android.MainActivity;
import com.kiwi.bubble.android.R;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.TEA;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends Activity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
	}

	public void onClickCreateAccount(View v) {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/signup";
		TEA tea = new TEA(Constant.TEA_ENCRYPT_KEY.getBytes());
		
		EditText editTextEmail = (EditText)findViewById(R.id.editTextSignupEmail);
		EditText editTextName = (EditText)findViewById(R.id.editTextSignupName);
		EditText editTextPassword = (EditText)findViewById(R.id.editTextSignupPassword);
		
		String strEmail = editTextEmail.getText().toString();
		String strName = editTextName.getText().toString();
		String strPassword = new String(tea.encrypt(editTextPassword.getText().toString().getBytes()));
		
		if(strEmail.isEmpty()) {
			Toast.makeText(SignupActivity.this, "Please enter email address", 0).show();
		} else if(strName.isEmpty()) {
			Toast.makeText(SignupActivity.this, "Please enter your name", 0).show();
		} else if(strPassword.isEmpty()) {
			Toast.makeText(SignupActivity.this, "Please enter password", 0).show();
		} else {		
			HttpPostUtil util = new HttpPostUtil();
			HashMap result = new HashMap();
			String resultStr = new String();
			Map<String, String> param = new HashMap<String, String>();
			param.put("email", strEmail);
			param.put("name", strName);
			param.put("password", strPassword);
			
			try {
				resultStr = util.httpPostData(pageUrl, param);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Toast.makeText(SignupActivity.this, "Account created!\n" + resultStr, 0).show();
			
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}
}

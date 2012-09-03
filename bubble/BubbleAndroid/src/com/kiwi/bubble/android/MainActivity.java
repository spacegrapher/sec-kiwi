package com.kiwi.bubble.android;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.TEA;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;
import com.kiwi.bubble.android.list.BubbleListActivity;
import com.kiwi.bubble.android.member.SignupActivity;

public class MainActivity extends Activity {
	private static final int REQUEST_CODE_SIGNUP = 101;
	private EditText editTextEmail;
	private EditText editTextPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"font/Roboto-Regular.ttf");

		editTextEmail = (EditText) findViewById(R.id.editTextEmail);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);

		editTextEmail.setTypeface(tf);
		editTextPassword.setTypeface(tf);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onClickLogin(View v) {
		TEA tea = new TEA(Constant.TEA_ENCRYPT_KEY.getBytes());

		String strEmail = editTextEmail.getText().toString();
		String strPassword = editTextPassword.getText().toString();

		if (strEmail.isEmpty()) {
			Toast.makeText(MainActivity.this, "이메일 주소를 입력하세요", Toast.LENGTH_SHORT).show();
		} else if (strPassword.isEmpty()) {
			Toast.makeText(MainActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
		} else {
			new BackgroundTask().execute(strEmail, new String(tea.encrypt(strPassword.getBytes())));
		}
	}

	public void onClickSignup(View v) {
		Intent intent = new Intent(this, SignupActivity.class);
		startActivityForResult(intent, REQUEST_CODE_SIGNUP);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_SIGNUP) {
				Toast.makeText(MainActivity.this, "계정이 생성되었습니다", Toast.LENGTH_SHORT).show();
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
			
			HttpPostUtil util = new HttpPostUtil();
			Map<String, String> param = new HashMap<String, String>();
			param.put("email", arg0[0]);
			param.put("password", arg0[1]);

			try {
				resultStr = util.httpPostData(pageUrl, param);
			} catch (IOException e) {
				e.printStackTrace();
			}			

			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			progressDialog.hide();
			if (resultStr.isEmpty()) {
				Toast.makeText(MainActivity.this, "로그인 정보가 맞지 않습니다", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent(MainActivity.this,
						BubbleListActivity.class);
				intent.putExtra("id", Long.valueOf(resultStr));
				startActivity(intent);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("로그인 중...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

	}
}

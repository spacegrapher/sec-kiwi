package com.kiwi.bubble.android;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;
import com.kiwi.bubble.android.list.BubbleListActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BubbleCreateActivity extends Activity {
	private EditText editTextTitle;
	private EditText editTextText;
	private Button buttonPost;
	private String strEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubblecreate);
		
		Intent intent = this.getIntent();
		strEmail = intent.getStringExtra("email");
		
		editTextTitle = (EditText) findViewById(R.id.editTextBubbleCreateTitle);
		editTextText = (EditText) findViewById(R.id.editTextBubbleCreateText);
		buttonPost = (Button) findViewById(R.id.buttonBubbleCreatePost);
		buttonPost.setEnabled(false);
		
		editTextTitle.addTextChangedListener(new TextWatcher() {
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	            checkEnablePostButton();
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
		
		editTextText.addTextChangedListener(new TextWatcher() {
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	            checkEnablePostButton();
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
	}	

	public void onClickButtonCreateBack(View v) {
		Intent intent = new Intent();
		setResult(Activity.RESULT_CANCELED, intent);
		finish();
	}
	
	public void checkEnablePostButton() {
		boolean isTitleEmpty = editTextTitle.getText().toString().isEmpty();
		boolean isTextEmpty = editTextText.getText().toString().isEmpty();
		
		if(!isTitleEmpty && !isTextEmpty) {
			buttonPost.setEnabled(true);
		} else {
			buttonPost.setEnabled(false);
		}
	}
	public void onClickButtonCreatePost(View v) {
		String pageUrl = Constant.SERVER_DOMAIN_URL + "/create";
		
		String strTitle = editTextTitle.getText().toString();
		String strText = editTextText.getText().toString();
		
		HttpPostUtil util = new HttpPostUtil();
		HashMap result = new HashMap();
		String resultStr = new String();
		Map<String, String> param = new HashMap<String, String>();
		param.put("email", strEmail);
		param.put("title", strTitle);
		param.put("text", strText);
		
		try {
			resultStr = util.httpPostData(pageUrl, param);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Intent intent = new Intent();
		setResult(Activity.RESULT_OK, intent);
		finish();
		//Toast.makeText(BubbleCreateActivity.this, "Bubble Created!", 0).show();
	}
}

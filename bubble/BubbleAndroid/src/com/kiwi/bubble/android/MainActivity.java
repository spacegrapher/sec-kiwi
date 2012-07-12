package com.kiwi.bubble.android;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final EditText editText = (EditText)findViewById(R.id.editText1);
             
        
        Button testBtn = (Button)findViewById(R.id.button1);
        testBtn.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		String editTextString = editText.getText().toString();
        		String pageUrl = Constant.SERVER_DOMAIN_URL;
        		HttpPostUtil util = new HttpPostUtil();
        		
        		HashMap result = new HashMap();
        		String resultStr = new String();
        		Map<String, String> param = new HashMap<String, String>();
        		param.put("text", editTextString);
        		
        		try {
        			resultStr = util.httpPostData(pageUrl, param);
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
       		
        		Toast.makeText(MainActivity.this, resultStr, 0).show();
        	}
        });
        
        
		
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}

package com.kiwi.bubble.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;

public class BubbleCreateActivity extends SherlockActivity implements OnClickListener {
	private EditText editTextTitle;
	private EditText editTextText;
	private EditText editTextTag;
	private Button buttonPost;
	private long id;
	//private String strEmail;
	private List<String> strTagList;
	private TextView tvTag;
	
	private Button buttonCamera;
	private ImageView imageviewPhoto;
	private Bitmap bmpPhoto;
	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private Uri mImageCaptureUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubblecreate);
		
		Intent intent = this.getIntent();
		strTagList = Arrays.asList(intent.getStringArrayExtra("tag"));
		id = intent.getLongExtra("id", -1);
		//strEmail = intent.getStringExtra("email");		
		
		
		tvTag = (TextView) findViewById(R.id.textViewBubbleCreateTag);
		tvTag.setText(strTagList.toString());
		editTextTitle = (EditText) findViewById(R.id.editTextBubbleCreateTitle);
		editTextText = (EditText) findViewById(R.id.editTextBubbleCreateText);
		//editTextTag = (EditText) findViewById(R.id.editTextBubbleCreateTag);
		buttonPost = (Button) findViewById(R.id.buttonBubbleCreatePost);
		buttonPost.setEnabled(false);
		imageviewPhoto = (ImageView) findViewById(R.id.imageViewBubbleResultPhoto);
		buttonCamera = (Button) findViewById(R.id.buttonBubbleTakePhoto);
		
		buttonCamera.setOnClickListener(this);
		
	
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
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Post")
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("MENU", "item: " + item.toString() + ", id: " + item.getGroupId() + ", order: " + item.getOrder());
		if (item.toString().equals("Post")) {
			onClickButtonCreatePost(null);
		} 
		return true;
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
		new BackgroundTask().execute();
	}
	
	private void doTakePhotoAction()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
		mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
		
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PICK_FROM_CAMERA);
	}
	
	private void doTakeAlbumAction()
	{
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode != RESULT_OK)
		{
			return;
		}

		switch(requestCode)
		{
			case CROP_FROM_CAMERA:
			{
				final Bundle extras = data.getExtras();
	
				if(extras != null)
				{
					bmpPhoto = extras.getParcelable("data");
					imageviewPhoto.setImageBitmap(bmpPhoto);
				}
	
				File f = new File(mImageCaptureUri.getPath());
				if(f.exists())
				{
					f.delete();
				}
	
				break;
			}
	
			case PICK_FROM_ALBUM:
			{
				mImageCaptureUri = data.getData();
			}
			
			case PICK_FROM_CAMERA:
			{
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(mImageCaptureUri, "image/*");
	
				intent.putExtra("outputX", 500);
				intent.putExtra("outputY", 500);
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("scale", true);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, CROP_FROM_CAMERA);
	
				break;
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.buttonBubbleTakePhoto){
		DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				doTakePhotoAction();
			}
		};
		
		DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				doTakeAlbumAction();
			}
		};
		
		DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		};
		
		new AlertDialog.Builder(this)
			.setTitle("Select Image")
			.setPositiveButton("Camera", cameraListener)
			.setNeutralButton("Gallery", albumListener)
			.setNegativeButton("Cancel", cancelListener)
			.show();
		}		
	}
	
	private class BackgroundTask extends AsyncTask<String, Integer, Long> {
		private ProgressDialog progressDialog;

		@Override
		protected Long doInBackground(String... arg0) {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/create";
			
			String strTitle = editTextTitle.getText().toString();
			String strText = editTextText.getText().toString();
			String strTag = strTagList.toString().substring(1, strTagList.toString().length()-1);
			
			HttpPostUtil util = new HttpPostUtil();
			
			String bubbleId = new String();
			Map<String, String> param = new HashMap<String, String>();
			param.put("id", String.valueOf(id));
			param.put("title", strTitle);
			param.put("text", strText);
			param.put("tag", strTag);
			
			try {
				bubbleId = util.httpPostData(pageUrl, param);
			} catch (IOException e) {
				e.printStackTrace();
			}		
			
			if(bmpPhoto != null) 
				uploadBitmap(bubbleId);
			
			return null;
		}
		
		private void uploadBitmap(String bubbleId) {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/image";
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        bmpPhoto.compress(CompressFormat.JPEG, 75, bos);
	        byte[] data = bos.toByteArray();
	        String b64 = Base64.encodeToString(data, Base64.DEFAULT);
	        
	        HttpPostUtil util = new HttpPostUtil();
			String resultStr = new String();
			Map<String, String> param = new HashMap<String, String>();
			param.put("bubbleid", bubbleId);
	        param.put("image", b64);
	        try {
				resultStr = util.httpPostData(pageUrl, param);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			progressDialog.hide();
			Intent killIntent = new Intent(TagSelectActivity.ACTION_KILL_COMMAND);
	        killIntent.setType(TagSelectActivity.ACTION_KILL_DATATYPE);
	        sendBroadcast(killIntent);
	        
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			finish();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(BubbleCreateActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("버블 생성 중...");
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

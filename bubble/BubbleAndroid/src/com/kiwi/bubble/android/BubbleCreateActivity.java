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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;

public class BubbleCreateActivity extends SherlockActivity {
	private static final int OPTIONS_MENU_POST = 0;

	private EditText editTextText;
	private long id;
	private List<String> strTagList;
	private TextView tvTag;

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

		tvTag = (TextView) findViewById(R.id.textViewBubbleCreateTag);
		// tvTag.setText(strTagList.toString());
		updateSelectedTextView(strTagList);
		editTextText = (EditText) findViewById(R.id.editTextBubbleCreateText);
		imageviewPhoto = (ImageView) findViewById(R.id.imageViewBubbleResultPhoto);

		editTextText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					// Hide the keyboard
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					handled = true;
				}
				return handled;
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("버블 생성하기");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, OPTIONS_MENU_POST, 0, "Post").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPTIONS_MENU_POST:
			onClickButtonCreatePost(null);
			return true;
		case android.R.id.home:
			onClickButtonCreateBack(null);
			return true;
		}

		return false;
	}

	public void onClickButtonCreateBack(View v) {
		Intent intent = new Intent();
		setResult(Activity.RESULT_CANCELED, intent);
		finish();
	}

	public void onClickButtonCreatePost(View v) {
		if (!editTextText.getText().toString().isEmpty()) {
			new BackgroundTask().execute();
		}
	}

	private void updateSelectedTextView(List<String> selectedTagList) {
		if (selectedTagList.size() <= 0)
			return;

		String text = new String();

		int[] start = new int[selectedTagList.size()];
		int[] end = new int[selectedTagList.size()];
		start[0] = 0;
		for (int i = 0; i < selectedTagList.size(); i++) {
			if (i > 0) {
				text += " ";
				start[i] = end[i - 1] + 1;
			}
			text += selectedTagList.get(i);
			end[i] = start[i] + selectedTagList.get(i).length();
		}

		tvTag.setText(text, TextView.BufferType.SPANNABLE);
		Spannable sText = (Spannable) tvTag.getText();

		for (int i = 0; i < selectedTagList.size(); i++) {
			sText.setSpan(new BackgroundColorSpan(Color.YELLOW), start[i],
					end[i], 0);
		}
	}

	private void doTakePhotoAction() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String url = "tmp_" + String.valueOf(System.currentTimeMillis())
				+ ".jpg";
		mImageCaptureUri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), url));

		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				mImageCaptureUri);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PICK_FROM_CAMERA);
	}

	private void doTakeAlbumAction() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case CROP_FROM_CAMERA: {
			final Bundle extras = data.getExtras();

			if (extras != null) {
				bmpPhoto = extras.getParcelable("data");
				imageviewPhoto.setImageBitmap(bmpPhoto);
				imageviewPhoto.setScaleType(ScaleType.CENTER_CROP);
			}

			File f = new File(mImageCaptureUri.getPath());
			if (f.exists()) {
				f.delete();
			}

			break;
		}

		case PICK_FROM_ALBUM: {
			mImageCaptureUri = data.getData();
		}

		case PICK_FROM_CAMERA: {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(mImageCaptureUri, "image/*");

			intent.putExtra("outputX", 400);
			intent.putExtra("outputY", 400);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, CROP_FROM_CAMERA);

			break;
		}
		}
	}

	public void onClickPhoto(View v) {
		DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doTakePhotoAction();
			}
		};

		DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doTakeAlbumAction();
			}
		};

		DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};

		new AlertDialog.Builder(this).setTitle("사진 선택")
		.setPositiveButton("카메라", cameraListener)
		.setNeutralButton("갤러리", albumListener)
		.setNegativeButton("취소", cancelListener).show();
	}

	private class BackgroundTask extends AsyncTask<String, Integer, Long> {
		private ProgressDialog progressDialog;

		private String convertTagListToString(List<String> tagList) {
			String ret = new String();

			for (int i = 0; i < tagList.size(); i++) {
				if (i > 0)
					ret += ",";
				ret += tagList.get(i);
			}

			return ret;
		}

		@Override
		protected Long doInBackground(String... arg0) {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/create";

			String strText = editTextText.getText().toString();
			String strTag = convertTagListToString(strTagList);

			HttpPostUtil util = new HttpPostUtil();

			String bubbleId = new String();
			Map<String, String> param = new HashMap<String, String>();
			param.put("id", String.valueOf(id));
			param.put("text", strText);
			param.put("tag", strTag);

			try {
				bubbleId = util.httpPostData(pageUrl, param);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (bmpPhoto != null)
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
			Map<String, String> param = new HashMap<String, String>();
			param.put("bubbleid", bubbleId);
			param.put("image", b64);
			try {
				util.httpPostData(pageUrl, param);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			progressDialog.hide();
			Intent killIntent = new Intent(
					TagSelectActivity.ACTION_KILL_COMMAND);
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
			super.onProgressUpdate(values);
		}

	}
}

package com.kiwi.bubble.android.member;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kiwi.bubble.android.R;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;

public class UserPhotoActivity extends SherlockActivity {
	private static final int OPTIONS_MENU_EDIT = 0;

	private Long id;
	private boolean bIsEditable;
	private ImageView ivUserPhoto;

	// private ImageView imageviewPhoto;
	private Bitmap bmpPhoto;
	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private Uri mImageCaptureUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userphoto);

		Intent intent = this.getIntent();
		id = Long.valueOf(intent.getLongExtra("id", -1));
		bIsEditable = intent.getBooleanExtra("editable", false);

		ivUserPhoto = (ImageView) findViewById(R.id.imageViewUserPhoto);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new BackgroundFetchProfileTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (bIsEditable) {
			menu.add(0, OPTIONS_MENU_EDIT, 0, "Edit").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_IF_ROOM
							| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPTIONS_MENU_EDIT:
			if (bIsEditable) {
				onClickEdit();
			}
			return true;
		case android.R.id.home:
			Intent intent = new Intent();
			setResult(Activity.RESULT_CANCELED, intent);
			finish();
			return true;
		}

		return false;
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
				ivUserPhoto.setImageBitmap(bmpPhoto);
			}

			File f = new File(mImageCaptureUri.getPath());
			if (f.exists()) {
				f.delete();
			}

			new BackgroundTask().execute();

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

	public void onClickEdit() {
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

		new AlertDialog.Builder(this).setTitle("Select Image")
				.setPositiveButton("Camera", cameraListener)
				.setNeutralButton("Gallery", albumListener)
				.setNegativeButton("Cancel", cancelListener).show();

	}

	public void onClickButtonBack(View v) {
		finish();
	}

	private class BackgroundFetchProfileTask extends
			AsyncTask<String, Integer, Long> {
		private Bitmap profileBitmap;

		@Override
		protected Long doInBackground(String... arg0) {
			this.fetchUserPhoto();
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			if (profileBitmap != null)
				ivUserPhoto.setImageBitmap(profileBitmap);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		private void fetchUserPhoto() {
			String userImageUrl = Constant.SERVER_DOMAIN_URL + "/userimage";
			DefaultHttpClient userImageClient = new DefaultHttpClient();
			String userImageRes = HttpGetUtil.doGetWithResponse(userImageUrl
					+ "?id=" + id, userImageClient);
			// Log.i("USER", "userImageRes: " + userImageRes);
			if (!userImageRes.equals("")) {
				byte[] photoByte = Base64.decode(userImageRes, Base64.DEFAULT);
				profileBitmap = BitmapFactory.decodeByteArray(photoByte, 0,
						photoByte.length);
			} else {
				profileBitmap = null;
			}
		}
	}

	private class BackgroundTask extends AsyncTask<String, Integer, Long> {
		private ProgressDialog progressDialog;

		@Override
		protected Long doInBackground(String... arg0) {
			this.uploadUserPhoto();
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			progressDialog.hide();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(UserPhotoActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Updating photo...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		private void uploadUserPhoto() {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/userimage";

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bmpPhoto.compress(CompressFormat.JPEG, 75, bos);
			byte[] data = bos.toByteArray();
			String b64 = Base64.encodeToString(data, Base64.DEFAULT);

			HttpPostUtil util = new HttpPostUtil();
			Map<String, String> param = new HashMap<String, String>();
			param.put("id", id.toString());
			param.put("image", b64);
			try {
				util.httpPostData(pageUrl, param);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

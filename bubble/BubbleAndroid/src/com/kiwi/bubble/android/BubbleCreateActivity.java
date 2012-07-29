package com.kiwi.bubble.android;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.parser.HttpImagePostUtil;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;
import com.kiwi.bubble.android.list.BubbleListActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BubbleCreateActivity extends Activity implements OnClickListener {
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
	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private Uri mImageCaptureUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		String strTag = strTagList.toString().substring(1, strTagList.toString().length()-1);
				
		HttpPostUtil util = new HttpPostUtil();
		
		String resultStr = new String();
		Map<String, String> param = new HashMap<String, String>();
		//param.put("email", strEmail);
		param.put("id", String.valueOf(id));
		param.put("title", strTitle);
		param.put("text", strText);
		param.put("tag", strTag);
		
		try {
			resultStr = util.httpPostData(pageUrl, param);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(mImageCaptureUri != null){ /*FixMe : I'm not sure this is correct error handling*/
			HttpImagePostUtil image_util = new HttpImagePostUtil();
			String resultImageStr = new String();
			
			try {
				resultImageStr = image_util.httpPostData(pageUrl, mImageCaptureUri.getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Intent killIntent = new Intent(TagSelectActivity.ACTION_KILL_COMMAND);
        killIntent.setType(TagSelectActivity.ACTION_KILL_DATATYPE);
        sendBroadcast(killIntent);
        
		Intent intent = new Intent();
		setResult(Activity.RESULT_OK, intent);
		finish();
		//Toast.makeText(BubbleCreateActivity.this, "Bubble Created!", 0).show();
	}
	
	/**
	 * 카메라에서 이미지 가져오기
	 */
	private void doTakePhotoAction()
	{
		/*
		 * 참고 해볼곳
		 * http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
		 * http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
		 * http://www.damonkohler.com/2009/02/android-recipes.html
		 * http://www.firstclown.us/tag/android/
		 */

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		// 임시로 사용할 파일의 경로를 생성
		String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
		mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
		
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PICK_FROM_CAMERA);
	}
	
	/**
	 * 앨범에서 이미지 가져오기
	 */
	private void doTakeAlbumAction()
	{
		// 앨범 호출
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
				// 크롭이 된 이후의 이미지를 넘겨 받습니다. 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
				// 임시 파일을 삭제합니다.
				final Bundle extras = data.getExtras();
	
				if(extras != null)
				{
					Bitmap photo = extras.getParcelable("data");
					imageviewPhoto.setImageBitmap(photo);
				}
	
				// 임시 파일 삭제
				File f = new File(mImageCaptureUri.getPath());
				if(f.exists())
				{
					f.delete();
				}
	
				break;
			}
	
			case PICK_FROM_ALBUM:
			{
				// 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
				// 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
				
				mImageCaptureUri = data.getData();
			}
			
			case PICK_FROM_CAMERA:
			{
				// 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
				// 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
	
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(mImageCaptureUri, "image/*");
	
				intent.putExtra("outputX", 90);
				intent.putExtra("outputY", 90);
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
}

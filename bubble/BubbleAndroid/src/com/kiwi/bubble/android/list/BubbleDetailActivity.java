package com.kiwi.bubble.android.list;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.kiwi.bubble.android.R;
import com.kiwi.bubble.android.common.BubbleComment;
import com.kiwi.bubble.android.common.BubbleData;
import com.kiwi.bubble.android.common.BubbleTag;
import com.kiwi.bubble.android.common.Constant;
import com.kiwi.bubble.android.common.UserInfo;
import com.kiwi.bubble.android.common.parser.HttpGetUtil;
import com.kiwi.bubble.android.common.parser.HttpPostUtil;
import com.kiwi.bubble.android.member.UserProfileActivity;


public class BubbleDetailActivity extends SherlockActivity {
	private LinearLayout llBody;
	private LinearLayout llComments;
	private ProgressBar progressBar;
	private TextView tvName;
	private TextView tvTitle;
	private TextView tvDate;
	private TextView tvText;
	private TextView tvTag;
	private EditText etComment;
	private ImageView ivPhoto;
	private Long bubbleId;
	private Long authorId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubbledetail);
		
		llBody = (LinearLayout) findViewById(R.id.linearLayoutBubbleDetailBody);
		llComments = (LinearLayout) findViewById(R.id.linearLayoutBubbleDetailComments);
		progressBar = (ProgressBar) findViewById(R.id.progressBarBubbleDetail);
		tvName = (TextView) findViewById(R.id.textViewBubbleDetailName);
		tvTitle = (TextView) findViewById(R.id.textViewBubbleDetailTitle);
		tvDate = (TextView) findViewById(R.id.textViewBubbleDetailDate);
		tvText = (TextView) findViewById(R.id.textViewBubbleDetailText);
		tvTag = (TextView) findViewById(R.id.textViewBubbleDetailTag);
		etComment = (EditText) findViewById(R.id.editTextBubbleDetailComment);
		ivPhoto = (ImageView) findViewById(R.id.imageViewBubbleDetailProfile);
				
		Intent intent = this.getIntent();
		bubbleId = Long.valueOf(intent.getLongExtra("bubbleid", -1));
		authorId = Long.valueOf(intent.getLongExtra("authorid", -1));
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		new BackgroundTask(true).execute();
	}
	
	public void onClickButtonBack(View v) {
		finish();
	}
	
	public void onClickSubmitComment(View v) {
		new CommentTask().execute();
	}
	
	private class BackgroundTask extends AsyncTask<String, Integer, Long> {
		BubbleData bubble;
		UserInfo userInfo;
		String strTag;
		List<BubbleComment> comments;
		boolean bHideBody;
		
		public BackgroundTask(boolean hideBody) {
			super();
			bHideBody = hideBody;
		}
		
		@Override
		protected Long doInBackground(String... arg0) {
			getBubbleData();
			getCommentData();
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			tvName.setText(userInfo.getName());
			tvTitle.setText(bubble.getTitle());
			tvDate.setText(bubble.getPostTime().toString());
			tvText.setText(bubble.getText());
			tvTag.setText(strTag);
			
			final Bitmap userImage = userInfo.getImage();
			if(userImage != null) {
				ivPhoto.setImageBitmap(userImage);
			}
			
			llComments.removeAllViews();
			for(int i=0; i<comments.size(); i++) {
				final BubbleComment comment = comments.get(i);
				LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View commentView = inflater.inflate(R.layout.listview_bubblecomment, null);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				commentView.setLayoutParams(params);
				TextView tvcName = (TextView)commentView.findViewById(R.id.textViewBubbleListCommentViewName);
				TextView tvcText = (TextView)commentView.findViewById(R.id.textViewBubbleListCommentViewText);
				TextView tvcDate = (TextView)commentView.findViewById(R.id.textViewBubbleListCommentViewDate);
				tvcName.setText("" + comment.getAuthorInfo().getName());
				tvcText.setText(comment.getText());
				tvcDate.setText(comment.getPostTime().toString());
				llComments.addView(commentView);
				
				tvcName.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(BubbleDetailActivity.this, UserProfileActivity.class);
						intent.putExtra("id", authorId.longValue());
						intent.putExtra("selectedid", comment.getAuthorId().longValue());
						startActivity(intent);
						if(authorId.longValue() == comment.getAuthorId().longValue())
							overridePendingTransition(0, 0);
					}
					
				});
			}
			
			tvName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(BubbleDetailActivity.this, UserProfileActivity.class);
					intent.putExtra("id", authorId.longValue());
					intent.putExtra("selectedid", bubble.getAuthorId().longValue());
					startActivity(intent);
					if(authorId.longValue() == bubble.getAuthorId().longValue())
						overridePendingTransition(0, 0);
				}
				
			});
			
			if(bHideBody) llBody.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
			if(bHideBody) llBody.setVisibility(View.INVISIBLE);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
		private void getBubbleData() {
			bubble = BubbleData.getBubbleData(bubbleId.longValue());
			userInfo = UserInfo.getUserInfo(bubble.getAuthorId().longValue());
			
			// Get User Image
			String userImageUrl = Constant.SERVER_DOMAIN_URL + "/userimage";
			DefaultHttpClient userImageClient = new DefaultHttpClient();
			String userImageRes = HttpGetUtil.doGetWithResponse(userImageUrl + "?id=" + bubble.getAuthorId(), userImageClient);
			//Log.i("USER", "userImageRes: " + userImageRes);
			if(!userImageRes.equals("")) {
				byte[] photoByte = Base64.decode(userImageRes, Base64.DEFAULT);
				Bitmap bmp = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
								
				userInfo.setImage(bmp);
			}
			
			strTag = "[";
			List<Long> tags = bubble.getTag();
			for(int i=0; i<tags.size(); i++) {
				BubbleTag tag = BubbleTag.getBubbleTag(tags.get(i));
				if(tag==null) continue;
				if(i>0) strTag += ", ";
				strTag += tag.getText();
			}
			strTag += "]";
		}
		
		private void getCommentData() {		
			comments = BubbleComment.getCommentData(bubbleId.longValue());
			
			for(int i=0; i<comments.size(); i++) {
				BubbleComment comment = comments.get(i);
				UserInfo commentAuthor = UserInfo.getUserInfo(comment.getAuthorId().longValue());
				
				comment.setAuthorInfo(commentAuthor);
				comments.set(i, comment);
			}
		}
	}
	
	private class CommentTask extends AsyncTask<String, Integer, Long> {
		
		private ProgressDialog progressDialog;

		@Override
		protected Long doInBackground(String... arg0) {
			String pageUrl = Constant.SERVER_DOMAIN_URL + "/comment";
			
			String strComment = etComment.getText().toString();
					
			HttpPostUtil util = new HttpPostUtil();
			HashMap result = new HashMap();
			String resultStr = new String();
			Map<String, String> param = new HashMap<String, String>();
			param.put("bubbleid", bubbleId.toString());
			param.put("authorid", authorId.toString());
			param.put("comment", strComment);
			
			
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
			etComment.clearFocus();
			etComment.setText("");
			new BackgroundTask(false).execute();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			
			progressDialog = new ProgressDialog(BubbleDetailActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Posting comment...");
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

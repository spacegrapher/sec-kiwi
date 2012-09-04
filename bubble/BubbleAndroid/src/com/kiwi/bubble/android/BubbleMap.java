package com.kiwi.bubble.android;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class BubbleMap extends MapActivity implements LocationListener {
	int latitude = 37500000;
	int longitude = 127500000;
	MapView mapView;
	MapController controller;
	LocationManager manager;
	Location curLocation;
	String provider;
	Geocoder coder;
	String TAG = "MapTestActivity";
	final static int QuestionDialog = 1;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_map);

		Log.d("BubbleMap onCreate", "1!!");

		mapView = (MapView) findViewById(R.id.mapView);
		controller = mapView.getController();
		mapView.setBuiltInZoomControls(true);
		checkProvider();
		coder = new Geocoder(getApplicationContext(), Locale.KOREA);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("BubbleMap onResume", "1!!");
		// 마지막으로 얻은 위치가 있으면 보기
		Location loc = manager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (loc != null) {
			Log.d("BubbleMap onResume last known locatio not null", "1!!");
			onLocationChanged(loc);

		} else {
			Log.d("BubbleMap onResume last known locatio is null", "1!!");
		}
		// 현재 위치가 변화하는 경우 메서드가 호출되도록 등록, 1초마다 5km 이동시
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5,
				this);

		Log.d("BubbleMap onResume last known locatio after request location update",
				"1!!");
		// 기지국으로 부터 위치정보를 업데이트 요청
		// manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// 1000, 5, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 위치 정보 통지 등록 취소
		Log.d("BubbleMap onPause", "1!!");
		manager.removeUpdates(this);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	// 지도상에 화살표 이미지와 텍스트 문자열을 표시한다.
	public void setOverlay(GeoPoint p, String addr) {
		Log.d("BubbleMap setOverlay", "1!!");
		Bitmap icon = BitmapFactory.decodeResource(getResources(),
				R.drawable.position);
		IconOverlay overlay = new IconOverlay(icon, p, addr);
		List<Overlay> overlays = mapView.getOverlays();
		overlays.clear(); // 위치가 이동되면 기존 overlay 정보는 제거
		overlays.add(overlay);
	}

	// GeoCoder 를 이용하여 수신된 위치정보에 대한 주소를 찾아서 문자열로 반환
	private String showLocationName(Location loc) {
		double latitude = loc.getLatitude();
		double longitude = loc.getLongitude();

		Log.d("BubbleMap showLocationName", "1!!");

		StringBuffer buff = new StringBuffer();
		try {
			List<Address> addrs = coder.getFromLocation(latitude, longitude, 1);

			for (Address addr : addrs) {
				int index = addr.getMaxAddressLineIndex();
				for (int i = 0; i <= index; ++i) {
					buff.append(addr.getAddressLine(i));
					buff.append(" ");
				}
				// buff.append("\n");
			}
		} catch (IOException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
		}
		return buff.toString();
	}

	// 가장 베스트하게 사용가능한 Provider 를 검색
	public void checkProvider() {

		Log.d("BubbleMap checkProvider", "1!!");

		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria cta = new Criteria();
		cta.setAccuracy(Criteria.ACCURACY_FINE); // 정확도
		cta.setPowerRequirement(Criteria.POWER_HIGH); // 전원 소비량
		cta.setAltitudeRequired(false); // 고도, 높이 값을 얻어 올지를 결정
		cta.setSpeedRequired(true); // 속도
		cta.setCostAllowed(false); // 위치 정보를 얻어 오는데 들어가는 금전적 비용
		provider = manager.getBestProvider(cta, true);
	}

	// 위치정보가 갱신되면 호출
	public void onLocationChanged(Location location) {

		Log.d("BubbleMap onLocationChanged", "1!!");

		GeoPoint gp = new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
		controller.animateTo(gp);
		curLocation = location;
		String strloc = String.format("위도:%f 경도:%f 속도:%f",
				(float) location.getLatitude(),
				(float) location.getLongitude(),
				(float) (location.getSpeed() * 3.6));
		setOverlay(gp, strloc);
	}

	// 위치정보를 제공하는 프로바이더가 disable 되었을때 호출
	public void onProviderDisabled(String provider) {
	}

	// 위치정보를 제공하는 프로바이더가 enable 되었을때 호출
	public void onProviderEnabled(String provider) {
	}

	// 위치정보를 제공하는 프로바이더가 enable 또는 disable 되었을때 호출
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	// 지도위에 이미지나 텍스트를 출력하기 위한 클래스
	private class IconOverlay extends Overlay {

		Bitmap mIcon;
		GeoPoint mPoint;
		String addr;
		int mOffsetX;
		int mOffsetY;

		IconOverlay(Bitmap icon, GeoPoint initial, String addr) {
			Log.d("BubbleMap IconOverlay", "1!!");

			mIcon = icon;
			mOffsetX = 0 - icon.getWidth() / 2 + 5;
			mOffsetY = 0; // -icon.getHeight();
			mPoint = initial;
			this.addr = addr;
		}

		// 지도를 탭하면 호출
		@Override
		public boolean onTap(GeoPoint point, MapView mapView) {
			mPoint = point;

			Log.d("BubbleMap onTap", "1!!");
			return super.onTap(point, mapView);
		}

		// 지도를 그릴 때, shadow = true, shadow = false를 2 번 호출
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);

			Log.d("BubbleMap draw", "1!!");
			if (!shadow) {
				// 지도상의 위치와 그린의 Canvas의 좌표 변환
				Projection projection = mapView.getProjection();
				Point point = new Point();
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				paint.setShadowLayer(10, 0, 0, Color.GRAY);
				paint.setTextSize(14);
				paint.setColor(Color.RED);
				projection.toPixels(mPoint, point);
				canvas.drawText(addr, point.x - 20, point.y + 50, paint);
				point.offset(mOffsetX, mOffsetY);
				// 아이콘 그리기
				canvas.drawBitmap(mIcon, point.x, point.y, null);
			}
		}
	}// IconOverlay??

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Log.d("BubbleMap onCreateOptionsMenu", "1!!");
		menu.add(0, 1, 0, "프로그램 소개");
		menu.add(0, 2, 0, "현재위치").setIcon(R.drawable.green_dot);
		menu.add(0, 3, 0, "주소보기");
		menu.add(0, 4, 0, "종료");

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		Log.d("BubbleMap onOptionsItemSelected", "1!!");

		switch (item.getItemId()) {
		case 1:
			new AlertDialog.Builder(this)
					.setTitle("프로그램 소개")
					.setMessage(
							"안드로이드 학습을 위하여 만든 어플입니다.\n"
									+ "소스코드가 공개되어 있으며 자유롭게 사용하실수 있습니다.\n"
									+ "http://neueziel.blog.me")
					.setIcon(R.drawable.icon)
					.setPositiveButton("닫기",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).show();
			return true;
		case 2:
			if (curLocation != null) {
				onLocationChanged(curLocation);
			}
			return true;
		case 3:
			if (curLocation != null)
				Toast.makeText(this, showLocationName(curLocation),
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this, "GPS 연결안됨", Toast.LENGTH_SHORT).show();
			return true;
		case 4:
			finish();
			System.exit(0);
			return true;
		}
		return false;
	}

	// Back 버튼을 누르면 선택 다이얼로그 창을 띄움
	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Log.d("BubbleMap onKeyDown", "1!!");

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			showDialog(QuestionDialog);
			break;
		case KeyEvent.KEYCODE_HOME:
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			am.restartPackage(getPackageName());
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 버튼이 눌렸을때 다이얼로그 창을 띄움
	protected Dialog onCreateDialog(int id) {

		Log.d("BubbleMap onCreateDialog", "1!!");

		switch (id) {
		case QuestionDialog:
			return new AlertDialog.Builder(BubbleMap.this).setTitle("질문")
					.setMessage("프로그램을 종료하시겠습니까?")
					.setPositiveButton("종료", mClick)
					.setNegativeButton("취소", mClick).create();
		}
		return null;
	}

	// 다이얼로그 창에서 버튼을 눌렀을때 처리, 종료버튼을 누르면 프로그램 종료
	DialogInterface.OnClickListener mClick = new DialogInterface.OnClickListener() {
		@SuppressWarnings("deprecation")
		public void onClick(DialogInterface dialog, int whichButton) {

			Log.d("BubbleMap onClick", "1!!");

			if (whichButton == DialogInterface.BUTTON1) {
				finish();
				System.exit(0);
			} else {
			}
		}
	};

}

package elink.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import demo.demo.R;
import com.coolkit.WebSocketManager;
import com.coolkit.common.HLog;
import com.coolkit.common.WsRequest;

import java.lang.reflect.Field;

import elink.HkApplication;
import elink.common.ABind;
import elink.common.UiHelper;
import elink.manager.SystemActionHolder;
import elink.utils.ThreadExecutor;

public class BasicActivity<T> extends Activity implements OnClickListener,SystemActionHolder.SystemMsgCallBack {
	private static final String TAG = BasicActivity.class.getSimpleName();

	public static final int MSG_SHOW_NO_NET=1;
	public T mController;
	public HkApplication app;

	protected String mTitle = "";

	public boolean isDestroy = false;
	protected String[] mBroads;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (HkApplication) getApplicationContext();
		app.mSysManager.regesterSysMsg(this);


	}


	@Override
	protected void onPause() {
		super.onPause();
	HLog.i(TAG,"on pause");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isDestroy = true;
		if(null!=mBroadcastReciever){
			this.unregisterReceiver(mBroadcastReciever);
		}
		app.mSysManager.deStroyReciever(this);

	}
	
	protected void onResume() {
		HLog.i(TAG,"on resume");

		super.onResume();
		WebSocketManager.getInstance(app.mAppHelper).activeWs(app.mUser.isLogin,app.mUser.apikey,app.mUser.at);
		isDestroy = false;
		
	};




	public void doRegister(String... boradActions) {
		mBroads = boradActions;
		if (null != mBroads && mBroads.length > 0) {
			mBroadcastReciever = new BaseReciever();
			
			for (String action : mBroads) {
				this.registerReceiver(mBroadcastReciever, new IntentFilter(
						action));
			}

		}

	}



	public  void showNoNet(){
		HLog.i(TAG, "show no net");
		mBaseHandler.sendEmptyMessage(MSG_SHOW_NO_NET);
	}

	Handler mBaseHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				initDatas();
				initViews();
				setViews();
			break;
				case MSG_SHOW_NO_NET:
					UiHelper.showShortToast(BasicActivity.this
							,getString(R.string.not_internet_current));
break;
			default:
				break;
			}
		};
	};
	private BroadcastReceiver mBroadcastReciever;

	public void requestData() {

	};

	public void initDatas() {

	}

	public void setViews() {
		// TODO Auto-generated method stub

	}

	public void initViews() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			HLog.i(TAG,"on option item select home");
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void postWsRequest(WsRequest wsRequest) {

		WebSocketManager.getInstance(app.mAppHelper).sendWsRequest(wsRequest);

	}




	public void onRecieve(String action) {

	}

	protected void wsCallBack(String id, String msg) {

	}

	protected void setView() {
		if (null != this.getActionBar()) {
			getActionBar().setLogo(new BitmapDrawable());
			this.getActionBar().setDisplayHomeAsUpEnabled(true);
			setTitle(mTitle);
		}
	}

	public void postRequest(Runnable run) {
		ThreadExecutor.execute(run);

	}
	
	public void initDeclaration() {
		Field[] fields = this.getClass().getDeclaredFields();
		if (null != fields) {
			for (Field field : fields) {

				ABind an = field.getAnnotation(ABind.class);
				if (null != an) {
					try {
						field.setAccessible(true);
						View view = findViewById(an.id());
						field.set(this, view);
						if (an.click()) {
							view.setOnClickListener(this);
						}

					} catch (Exception e) {
						HLog.e(TAG, e);
					}
				}
			}
		}

	}

	@Override
	public boolean doDeviceOnline(String deviceId, boolean onLine, boolean hasGloble, String json) {
		return false;
	}

	@Override
	public boolean doUpdateParams(String deviceId, String params, boolean hasGloble, String json) {
		return false;
	}


	class BaseReciever extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent==null?"":intent.getAction();
			onRecieve(action);
			
		}
	}



	@Override
	public void onClick(View v) {
		
	}
}

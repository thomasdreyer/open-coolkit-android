package elink.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Handler;

import elink.common.UiHelper;
import demo.demo.R;
import elink.manager.SystemActionHolder;
import com.coolkit.WebSocketManager;
import com.coolkit.common.HLog;
import com.coolkit.common.WsRequest;

import elink.HkApplication;
import elink.utils.ThreadExecutor;

public class BaseHelper{
	private static final String TAG = BaseHelper.class.getSimpleName();

	public static final int MSG_SHOW_NO_NET=1;

	public HkApplication app;

	protected String mTitle = "";
	protected SystemActionHolder mSysManager;

	public boolean isDestroy = false;
	protected String[] mBroads;


	public BaseHelper(Context activity){
		app = (HkApplication) activity.getApplicationContext();

	}

	

	
	
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		isDestroy = true;
//		if(null!=mBroadcastReciever){
//			this.unregisterReceiver(mBroadcastReciever);
//		}
//		mSysManager.deStroyReciever();
//
//	}
//
//	protected void onResume() {
//		super.onResume();
//		WebSocketManager.getInstance(this).activityWorker();
//		isDestroy = false;
//
//	};


//	public void doRegister(String... boradActions) {
//		mBroads = boradActions;
//		if (null != mBroads && mBroads.length > 0) {
//			mBroadcastReciever = new BaseReciever();
//
//			for (String action : mBroads) {
//				this.registerReceiver(mBroadcastReciever, new IntentFilter(
//						action));
//			}
//
//		}
//
//	}



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
					UiHelper.showShortToast(app
							,app.getString(R.string.not_internet_current));
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

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == android.R.id.home) {
//			finish();
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	public void postWsRequest(WsRequest wsRequest) {
		WebSocketManager.getInstance(app.mAppHelper).sendWsRequest(wsRequest);

	}




	public void onRecieve(String action) {

	}

	protected void wsCallBack(String id, String msg) {

	}

//	protected void setView() {
//		if (null != this.getActionBar()) {
//			getActionBar().setLogo(new BitmapDrawable());
//			this.getActionBar().setDisplayHomeAsUpEnabled(true);
//			setTitle(mTitle);
//		}
//	}

	public void postRequest(Runnable run) {
		ThreadExecutor.execute(run);

	}
	
//	public void initDeclaration() {
//		Field[] fields = this.getClass().getDeclaredFields();
//		if (null != fields) {
//			for (Field field : fields) {
//
//				ABind an = field.getAnnotation(ABind.class);
//				if (null != an) {
//					try {
//						field.setAccessible(true);
//						View view = findViewById(an.id());
//						field.set(this, view);
//						if (an.click()) {
//							view.setOnClickListener(this);
//						}
//
//					} catch (Exception e) {
//						HLog.e(TAG, e);
//					}
//				}
//			}
//		}
//
//	}
	
//
//	class BaseReciever extends BroadcastReceiver{
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action=intent==null?"":intent.getAction();
//			onRecieve(action);
//
//		}
//	}
//
//
//
//	@Override
//	public void onClick(View v) {
//
//	}
}

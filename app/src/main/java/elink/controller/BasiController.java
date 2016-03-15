package elink.controller;

import com.coolkit.common.HLog;

import elink.HkApplication;
import elink.activity.BasicActivity;

public class BasiController<T extends BasicActivity> {
	public T mContext;
	protected String mMsg;
	public HkApplication app;



	public BasiController(T context) {
		mContext = context;
		app = (HkApplication) mContext.getApplicationContext();
		HLog.d("", "hzy BasiController context" + context + app);

	}
	
	public HkApplication getApp(){
		return app;
	}

//	public void setTest() {
//		if (null != mContext && mContext instanceof NetTestActivity) {
//
//			new Handler(Looper.getMainLooper()) {
//				@Override
//				public void handleMessage(Message msg) {
//					((NetTestActivity) mContext).setMsg(mMsg);
//					super.handleMessage(msg);
//				}
//			}.sendEmptyMessage(0);
//
//		}
//	}


}

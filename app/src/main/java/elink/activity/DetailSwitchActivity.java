package elink.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;

import java.util.List;

import elink.DeviceHelper;
import elink.HkConst;
import elink.activity.details.DetailHelper;
import elink.activity.details.SwitchHelper;
import elink.common.UiHelper;
import elink.entity.DeviceEntity;
import elink.entity.Timer;
import elink.model.DbManager;
import elink.utils.IntentHelper;

public class DetailSwitchActivity<T> extends BasicActivity<T> implements
		OnClickListener {
	private static final String TAG = DetailSwitchActivity.class.getSimpleName();
	T mController;

	private ImageView mIvShare;
	private ImageView mIvSetTimer;
	DetailHelper helper = null;
	private View mSwitchLayout;

	private List<Timer> mTimerList;
	private DeviceEntity mDeviceEntity;
	private ImageView mIvSetting;
	private ViewGroup mLlayoutContainer;
	private TextView mTvManuDes;

	private TextView mTvManufactor;
	private int timerCount;
	private String mDeviceId;
	public int mSwitchSizes;
	T Context;
	private boolean isupgradeinfo;

//	public DetailSwitchActivity(T mContext) {
//		Context=mContext;
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HLog.i(TAG, "oncreate DetailSwitchActivity");
		setContentView(R.layout.activity_detail_switch);
		mDeviceId= getIntent().getStringExtra(HkConst.EXTRA_D_ID);
		//isupgradeinfo= getIntent().getBooleanExtra("isupgradeinfo",false);

		handler.sendEmptyMessage(0);
		regester();
	}

	private void regester() {


		this.registerReceiver(mReciever, new IntentFilter(
				"com.homekit.action.EDIT_NAME"));
		this.registerReceiver(mReciever, new IntentFilter(
				"com.homekit.action.TimerChange"));
		this.registerReceiver(mReciever, new IntentFilter("com.homekit.action.AT_OVER_TIMER"));
		this.registerReceiver(mReciever, new IntentFilter(HkConst.INTENT_SYNC_LOCAL));
		this.registerReceiver(mReciever, new IntentFilter("com.homekit.action.UPDATE_ENTITY"));
	}

	@Override
	protected void onDestroy() {
		if (null != mReciever) {

			this.unregisterReceiver(mReciever);
		}
		super.onDestroy();

	}

	protected void setView() {
		super.setView();
		if (null != mDeviceEntity && (helper != null)) {
			setTitle(mDeviceEntity.mName
					+ ("true".equals(mDeviceEntity.mOnLine) ? getString(R.string.online) : getString(R.string.offline)));

			getActionBar().setLogo(new BitmapDrawable());
			helper.setView();

			if (!TextUtils.isEmpty(mDeviceEntity.mBrand)) {
				HLog.i(TAG,"mDeviceEntity.mBrand"+mDeviceEntity.mBrand);
				mTvManufactor.setText(getString(R.string.device_manufacturers) + mDeviceEntity.mBrand);
			}else {
				if(!TextUtils.isEmpty(mDeviceEntity.mManufact)){
					HLog.i(TAG,"mDeviceEntity.mManufact"+mDeviceEntity.mManufact);
					mTvManufactor.setText(getString(R.string.device_manufacturers) + mDeviceEntity.mManufact);
				}
			}

			if (!TextUtils.isEmpty(mDeviceEntity.mProductModel)) {
				HLog.i(TAG,"mDeviceEntity.mProductModel"+mDeviceEntity.mProductModel);
				mTvManuDes.setText(getString(R.string.device_type) + mDeviceEntity.mProductModel);
			}else {
				if(!TextUtils.isEmpty(mDeviceEntity.mDes)){
					HLog.i(TAG,"mDeviceEntity.mDes"+mDeviceEntity.mDes);
					mTvManuDes.setText(getString(R.string.device_type) + mDeviceEntity.mDes);
				}

			}

			if (timerCount > 0) {
				mTvTimerCount.setVisibility(View.VISIBLE);
				mTvTimerCount.setText(timerCount + "");

			}else{
				mTvTimerCount.setVisibility(View.GONE);
			}
			if("false".equals(mDeviceEntity.mOnLine)){
				Toast.makeText(getApplicationContext(),getResources().getString(R.string.lineoff_device),Toast.LENGTH_SHORT).show();
			}
		}

	}

	protected void initData() {
		HLog.i(TAG, "query device "+mDeviceId);
		mDeviceEntity = DbManager.getInstance(this).queryDeviceyByDeviceId(mDeviceId);
		if (null != mDeviceEntity) {
			HLog.i(TAG, "device id:" + mDeviceEntity.mId + " params :"
					+ mDeviceEntity.mParams);

			if(!TextUtils.isEmpty(mDeviceEntity.mUpdateInfo)&&TextUtils.isEmpty(mDeviceEntity.mOwer)&&mDeviceEntity.mOnLine.equals("true")){
				Toast.makeText(DetailSwitchActivity.this, R.string.infoupgrade,Toast.LENGTH_SHORT).show();
			}
			if(null==helper){
				HLog.i(TAG,"mDeviceEntity.mUi:"+mDeviceEntity.mUi);
				helper = DeviceHelper.setDetailHelper(this, mDeviceEntity.mUi);

			}

			if (null == helper) {
				finish();
				HLog.i(TAG, "has no helper ,finish()");
				return;
			}
			helper.initData(mDeviceEntity);

			mTimerList = DbManager.getInstance(this).queryTimerByDeviceId(
					mDeviceEntity.mDeviceId);
			HLog.i(TAG, "DetailSwitchActivity mTimerList:" + mTimerList.size());
			timerCount = null != mTimerList ? mTimerList.size() : 0;
		}else{
			UiHelper.showShortToast(this,getString(R.string.data_exception));
			finish();
			HLog.i(TAG, "has no device ,finish()");
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			initData();
			if(mDeviceEntity!=null){
				initView();
				setView();
			}

		}
	};
	private ImageView mIvManu;
	private TextView mTvTimerCount;


	BroadcastReceiver mReciever = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(HkConst.INTENT_SYNC_LOCAL.equals(intent.getAction())){
					HLog.i(TAG,"receivce intent sync_local broadcast");
						mTimerList = DbManager.getInstance(
						DetailSwitchActivity.this).queryTimerByDeviceId(
						mDeviceEntity.mDeviceId);
						timerCount = null != mTimerList ? mTimerList.size() : 0;
					if (timerCount > 0) {
						mTvTimerCount.setVisibility(View.VISIBLE);
						mTvTimerCount.setText(timerCount + "");

					} else {
						mTvTimerCount.setVisibility(View.GONE);

					}
					handler.removeMessages(0);
					handler.sendEmptyMessage(0);
				}
				else if ("com.homekit.action.TimerChange".equals(intent.getAction())) {
					HLog.i(TAG,"receive timerchange");
					mTimerList = DbManager.getInstance(
							DetailSwitchActivity.this).queryTimerByDeviceId(
							mDeviceEntity.mDeviceId);
					timerCount = null != mTimerList ? mTimerList.size() : 0;
					if (timerCount > 0) {
						mTvTimerCount.setVisibility(View.VISIBLE);
						mTvTimerCount.setText(timerCount + "");

					} else {
						mTvTimerCount.setVisibility(View.GONE);

					}
				}
				else if (("com.homekit.action.SYNC_DEVICE_DETAIL"
						.equals(intent.getAction()))||("com.homekit.action.UPDATE_ENTITY".equals(intent.getAction()))) {
					HLog.i(TAG,"receive sync_device_detail broadcast");
					handler.removeMessages(0);
					handler.sendEmptyMessage(0);
				}else if("com.homekit.action.AT_OVER_TIMER".equals(intent.getAction())){
					finish();
				} else {
					String newName = intent.getStringExtra("extra_new_name");
					if (!TextUtils.isEmpty(newName)) {
						setTitle(newName
								+ ("true".equals(mDeviceEntity.mOnLine) ? getString(R.string.online)
								: getString(R.string.offline)));

					}
				}
			}
		};




	public boolean doDeviceOnline(String deviceId, boolean onLine,
								  boolean hasGlobl,String json) {
		HLog.i(TAG, "do device onLine,has gloal:" + hasGlobl);
		handler.removeMessages(0);
		handler.sendEmptyMessage(0);
		return true;
	}

	public boolean doUpdateParams(String deviceId, String params,
								  boolean hasGlobl, String json) {
		HLog.i(TAG, "super update params,");
		handler.removeMessages(0);
		handler.sendEmptyMessage(0);

		return true;
	}


	protected void initView() {
		if (null != helper) {
			mLlayoutContainer = (ViewGroup) findViewById(R.id.ll_detail_container);
			helper.initView(mLlayoutContainer);

			mTvManufactor = (TextView) findViewById(R.id.tv_manufactor);
			mTvManuDes = (TextView) findViewById(R.id.tv_des);

			mIvShare = (ImageView) findViewById(R.id.iv_sw_share);
			mIvSetTimer = (ImageView) findViewById(R.id.iv_timer);
			mIvSetting = (ImageView) findViewById(R.id.iv_setting);
			mIvManu = (ImageView) findViewById(R.id.iv_info);
			mTvTimerCount = (TextView) findViewById(R.id.tv_timer_count);

			mIvShare.setOnClickListener(this);
			mIvSetTimer.setOnClickListener(this);
			mIvManu.setOnClickListener(this);
			mIvSetting.setOnClickListener(this);

			return;
		}
		HLog.i(TAG, "has no helper ,finish()");
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		HLog.i(TAG,"OnTouchEvent");
		if(helper!=null){
			return  helper.onTouchEvent(event);
		}else{
			return super.onTouchEvent(event);
		}
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.iv_sw_share:

			if (TextUtils.isEmpty(mDeviceEntity.mShareUsers)) {
				Toast.makeText(this, getString(R.string.share_owner_device), Toast.LENGTH_SHORT).show();

			} else {

				IntentHelper.startShareDeviceActvity(this, mDeviceEntity.mDeviceId);
			}
			break;
		case R.id.iv_timer:
			if (!"true".equals(mDeviceEntity.mOnLine)) {
				Toast.makeText(this, getString(R.string.lineoff_device), Toast.LENGTH_SHORT).show();
				return;
			}

			if (helper instanceof SwitchHelper) {
				HLog.i(TAG,"click switch helper");
				SwitchHelper helperTemp = (SwitchHelper) helper;
				mSwitchSizes = helperTemp.mSwitchSize;
				HLog.i(TAG,"mSwitchSizes :"+mSwitchSizes);
				//int m = ((SwitchHelper) helper).mSwitchSize;
				int outlet = helperTemp.mCurrentS.outlet;
				if (((SwitchHelper) helper).mSingle) {
					outlet = -1;//dan
				}

				IntentHelper.startNewTimerActvity(DetailSwitchActivity.this, mDeviceEntity.mDeviceId, outlet);
				//IntentHelper.startTimerActvity(DetailSwitchActivity.this, mDeviceEntity.mDeviceId);

			}
			else
			{
				IntentHelper.startTimerActvity(this, mDeviceEntity.mDeviceId);

			}
			break;

		case R.id.iv_setting: {
//			if (!"true".equals(mDeviceEntity.mOnLine)) {
//				Toast.makeText(this, getString(R.string.device_not_online), Toast.LENGTH_SHORT).show();
//				return;
//			}
			IntentHelper.starSettingNameActvity(this,mDeviceEntity.mDeviceId);
			break;
		}
		case R.id.iv_info: {
			IntentHelper.startDeviceManuFactorActivity(this, mDeviceEntity);
		}
			;
			break;
		default:
			break;
		}

	}

}

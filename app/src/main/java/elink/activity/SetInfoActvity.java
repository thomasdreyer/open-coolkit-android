package elink.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;
import com.coolkit.common.WsRequest;
import com.coolkit.protocol.request.DeviceProtocol;
import com.coolkit.protocol.request.ProtocolHandler;
import com.coolkit.protocol.request.Result;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import elink.DeviceHelper;
import elink.HkConst;
import elink.common.Helper;
import elink.common.UiHelper;
import elink.entity.DeviceEntity;
import elink.model.DbManager;
import elink.utils.DialogHelper;
import elink.utils.SpHelper;

public class SetInfoActvity extends BasicActivity implements OnClickListener {

	private static final String TAG = SetInfoActvity.class.getSimpleName();
	private TextView mTvSetName;
	private Dialog mShareDialog;
	private TextView mTvDash;
	private EditText mEt;
	private DeviceEntity mDeviceEntity;
	private Dialog mLoding;
	private BroadcastReceiver mReciever;
	private TextView mTvWifiName;
	private TextView mTvWifiPwd;
	public SpHelper sp;
	private String WlanSSID;
	private String WlanSSIDPwd;
	private TextView mTvTips;
	private TextView mWifiName;
	private TextView mWifiPwd;
	private ImageView mSettingName;
	private TextView mDeviceName;
	private RadioGroup mRadioGroup;
	private RadioButton mRadioButtonOn;
	private RadioButton mRadioButtonOff;
	private boolean hasStartup;
	private String jsonStartup;
	private JSONObject json;
	private JSONObject deviceParams;
	private JSONObject jsonObject;
	private RelativeLayout rlname;
	private RelativeLayout rlpwd;
	private ImageView mIvSetHumidity;
	private TextView mTvSetHumidity;
	private ImageView mIvHumidityClearBtn;
	private String action;


	private String mParms;
	private String mModel;
	private Integer mCurrentTemperature;
	private Integer mCurrentHumidity;
	private String mTarget;
	private String mSwtich;
	private String mTargetReaction;
	private RadioButton mRbTemperature;
	private RadioButton mRbHumidity;
	private String mTargetDes;
	private TextView mTvTargetDes;
	private View mTargetLayout;
	private EditText mEtTarget;
	private RadioGroup mRgDeviceType;
	private RadioGroup mRgReaction;
	private RadioButton mRbOpen;
	private RadioButton mRbClose;
	private boolean mHidePwd=true
			;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HLog.i("11", "SetInfoActvity oncreate");
		setContentView(R.layout.activity_set_name);
		sp = new SpHelper(this);
		initView();
		initData();
		if (mDeviceEntity == null) {
			finish();
			return;
		}

		setView();

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
//		unregisterReceiver(mSettingReceiver);
	}

	protected void setView() {
		super.setView();
		getActionBar().setLogo(new BitmapDrawable());
		setTitle(getString(R.string.change_device));


		HLog.i(TAG, "Owner:" + mDeviceEntity.mOwer);
setWifi();
		mDeviceName.setText(mDeviceEntity.mName);

		boolean isSupportedFw = !TextUtils.isEmpty(mDeviceEntity.mFwVersion) && (UiHelper.Compare("1.2.0", mDeviceEntity.mFwVersion) <= 0);
		HLog.i(TAG, "model:" + mDeviceEntity.mModel + ",fwversion:" + mDeviceEntity.mFwVersion + " is fw support:" + isSupportedFw);

		if (!"ITA-GZ1-GL".equals(mDeviceEntity.mModel) || !isSupportedFw) {
			HLog.i(TAG, "DeviceEntity not support this version and model");
			mRadioGroup.setVisibility(View.GONE);
		} else {
			HLog.i(TAG, "jsonstartup:" + jsonStartup);
			if ("off".equals(jsonStartup)) {
				mRadioButtonOff.setChecked(true);
			} else {
				mRadioButtonOn.setChecked(true);
			}
		}

		if((null!=mDeviceEntity)&&(DeviceHelper.UI_TEMPERATURE_AND_HUMIDITY_KEEPER==mDeviceEntity.mUi)){
			HLog.i(TAG, "set target layout visible");

			mTvTargetDes.setText(mTargetDes);
			mTargetLayout.setVisibility(View.VISIBLE);

		}


	}

	private void setWifi() {
		if (TextUtils.isEmpty(mDeviceEntity.mOwer)) {

			mWifiName.setText(WlanSSID);
			if (TextUtils.isEmpty(WlanSSIDPwd)) {

				mWifiPwd.setText(getString(R.string.wifi_password_none));
			} else {
				mWifiPwd.setTextColor(getResources().getColor(R.color.black));
				if(mHidePwd){
					mWifiPwd.setText("******");
				}else {
					mWifiPwd.setText(WlanSSIDPwd);

				}

			}
		} else {
			mWifiName.setText(getString(R.string.only_owner));
			mWifiPwd.setTextColor(getResources().getColor(R.color.black));
			mWifiPwd.setText(getString(R.string.only_owner));
		}
	}

	private void initData() {
		String devId = getIntent().getStringExtra(HkConst.EXTRA_D_ID);
		mDeviceEntity = DbManager.getInstance(this).queryDeviceyByDeviceId(devId);
		if (mDeviceEntity != null) {
			if (mDeviceEntity.mUi.equals(DeviceHelper.UI_PLUG_SINGLE) && !TextUtils.isEmpty(mDeviceEntity.mShareUsers)) {
				mRadioGroup.setVisibility(View.VISIBLE);
			}
			String diveceId = mDeviceEntity.mDeviceId;
			try {
				if (null == mDeviceEntity) {
					HLog.i(TAG, "mDeviceEntity is null");
					return;
				}
				HLog.i(TAG, "mDeviceEntitymparams:" + mDeviceEntity.mParams+" ui is :"+mDeviceEntity.mUi);
				if (null != mDeviceEntity.mParams) {
					HLog.i(TAG, "mParam is not null" + "");
					jsonObject = new JSONObject(mDeviceEntity.mParams);

					jsonStartup = jsonObject.has("startup") ? jsonObject.getString("startup") : "off";
					HLog.i(TAG, "JSONObject:" + jsonObject + ",switcher:" + (!jsonObject.has("switches")?"":jsonObject.get("switches").toString()) + "startup" + jsonStartup);
				}


			} catch (JSONException e) {
				HLog.e(TAG, e);
			}

			WlanSSID = sp.getWlanSSID(diveceId);

			if (!TextUtils.isEmpty(WlanSSID)) {
				rlname.setVisibility(View.VISIBLE);
				rlpwd.setVisibility(View.VISIBLE);
				final ImageView mBtnEye = (ImageView)findViewById(R.id.iv_pwd_eye);
				mBtnEye.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mHidePwd=!mHidePwd;
						if (mHidePwd) {
							mBtnEye.setImageResource(R.drawable.eye_on_black);

						} else {
							mBtnEye.setImageResource(R.drawable.eye_off_black);
						}
					setWifi();


					}
				});
				WlanSSIDPwd = sp.getWlanSSIDPwd(diveceId);
				HLog.i(TAG, "wifiSSID:" + WlanSSID + ",wifiPWD:" + WlanSSIDPwd);
			}
			HLog.i(TAG, "DeviceId:" + diveceId + "," + " ssid is " + WlanSSID + " pwd is " + WlanSSIDPwd + "devicename:" + mDeviceEntity.mName);
		}

		if((null!=mDeviceEntity)&&(DeviceHelper.UI_TEMPERATURE_AND_HUMIDITY_KEEPER==mDeviceEntity.mUi)){
			initData(mDeviceEntity);

			setState();
		}
	}

	private void initView() {

		rlname = (RelativeLayout) findViewById(R.id.rl_wifiname);
		rlpwd = (RelativeLayout) findViewById(R.id.rl_wifipwd);
		mDeviceName = (TextView) findViewById(R.id.tv_devicename);
		mWifiName = (TextView) findViewById(R.id.wifi_name);
		mWifiPwd = (TextView) findViewById(R.id.wifi_pwd);
		mSettingName = (ImageView) findViewById(R.id.setting_name);
		mSettingName.setOnClickListener(this);
		mRadioGroup = (RadioGroup) findViewById(R.id.rg_button);
		mRadioButtonOn = (RadioButton) findViewById(R.id.rb_button1);
		mRadioButtonOff = (RadioButton) findViewById(R.id.rb_button2);
		mRadioButtonOn.setOnClickListener(this);
		mRadioButtonOff.setOnClickListener(this);
		mIvSetHumidity = (ImageView) findViewById(R.id.iv_set_humidity_target);
		mIvSetHumidity.setOnClickListener(this);
		mTvSetHumidity = (TextView) findViewById(R.id.tv_set_humidity_target);

		mIvHumidityClearBtn = (ImageView) findViewById(R.id.iv_humidity_clear);
		mIvHumidityClearBtn.setOnClickListener(this);
		mTargetLayout=findViewById(R.id.rl_target);

		mTvTargetDes=(TextView)findViewById(R.id.tv_set_humidity_target);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.setting_name:
				HLog.i(TAG, "on clikc tv set name");
				if (TextUtils.isEmpty(mDeviceEntity.mOwer)) {
					showSetView();

				} else {
					Toast.makeText(this, getString(R.string.info_not_change), Toast.LENGTH_SHORT)
							.show();
				}

				break;

			case R.id.rb_button1:
				HLog.i(TAG, "ClickOn");
				json = new JSONObject();


				try {
					deviceParams = new JSONObject(mDeviceEntity.mParams);
					deviceParams.put("startup", "on");
					json.put("action", "update");
					json.put("apikey", mDeviceEntity.mApiKey);
					json.put("deviceid", mDeviceEntity.mDeviceId);
					json.put("params", deviceParams);
					json.put("userAgent", "app");
					HLog.i(TAG, "onClick on:" + json);
					Helper.addSelfKey(json, app.mUser.apikey);
					this.postWsRequest(new WsRequest(json) {
						@Override
						public void callback(String msg) {
							HLog.i(TAG, "oncallback:" + msg);
							if (!TextUtils.isEmpty(msg)) {
								try {
									JSONObject onjson = new JSONObject(msg);
									if (onjson.has("error") && 0 == onjson.getInt("error")) {

										mDeviceEntity.mParams = deviceParams.toString();
										app.mDbManager.updateObject(
												mDeviceEntity, mDeviceEntity.mId);
									}
								} catch (Exception e) {
									HLog.e(TAG, e);
								}
							}
						}
					});
				} catch (JSONException e) {
					HLog.e(TAG, e);
				}

				break;

			case R.id.rb_button2:
				HLog.i(TAG, "ClickOff");
				json = new JSONObject();

				try {
					deviceParams = new JSONObject(mDeviceEntity.mParams);

					deviceParams.put("startup", "off");
					json.put("action", "update");
					json.put("apikey", mDeviceEntity.mApiKey);
					json.put("deviceid", mDeviceEntity.mDeviceId);
					json.put("params", deviceParams);
					json.put("userAgent", "app");
					HLog.i(TAG, "onClick off:" + json);
					Helper.addSelfKey(json, app.mUser.apikey);
					this.postWsRequest(new WsRequest(json) {
						@Override
						public void callback(String msg) {
							HLog.i(TAG, "offcallback:" + msg);
							if (!TextUtils.isEmpty(msg)) {
								try {
									JSONObject offjson = new JSONObject(msg);
									if (offjson.has("error") && 0 == offjson.getInt("error")) {
										mDeviceEntity.mParams = deviceParams.toString();
										app.mDbManager.updateObject(
												mDeviceEntity, mDeviceEntity.mId);
									}
								} catch (Exception e) {
									HLog.e(TAG, e);
								}
							}
						}
					});
				} catch (JSONException e) {
					HLog.e(TAG, e);
				}


				break;
			case R.id.iv_set_humidity_target:

				showSettingHumidity();
				break;
			case R.id.iv_humidity_clear:
			{
clear();

			}
			default:
				break;
		}

	}



	private void showSettingHumidity() {
	new SetTargetDialog(this).show();

	}


	public void call(Result result) {
		mLoding.dismiss();
		mShareDialog.dismiss();

		if (HttpStatus.SC_OK == result.mCode) {
			HLog.i(TAG, "MSg:" + result.mMsg);
			try {
				JSONObject json = new JSONObject(result.mMsg);
				mDeviceEntity.mName = json.getString("name");
			} catch (JSONException e) {
				HLog.e(TAG, e);
			}
			mDeviceName.setText(mDeviceEntity.mName);
			Toast.makeText(this, getString(R.string.change_name_success), Toast.LENGTH_SHORT).show();
			try {
				app.mDbManager.updateObject(mDeviceEntity, mDeviceEntity.mId);
				Helper.broadcastSynLocalDevice(this);
				Helper.broadcastEditName(this, mDeviceEntity.mName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;

		}
		mEt.setText(mDeviceName.getText().toString());
		mEt.setSelection(mDeviceName.getText().toString().length());
		Toast.makeText(this, getString(R.string.change_name_failed), Toast.LENGTH_SHORT).show();
	}

	private void setName(final String name) {
		mLoding = DialogHelper.createProgressDialog(SetInfoActvity.this,
				getString(R.string.waiting));
		mLoding.show();
//		mDeviceEntity.mName = name;
		final JSONObject json = new JSONObject();
		try {
//			json.put("name", mDeviceEntity.mName);
			json.put("name", name);
			json.put("group", mDeviceEntity.mGroup);
		} catch (JSONException e) {
			HLog.e(TAG, e);
		}

		postRequest(new Runnable() {

			@Override
			public void run() {

				try {
					json.put("deviceid", mDeviceEntity.mDeviceId);

				} catch (Exception e) {
					HLog.e(TAG, e);
				}
				;
				new DeviceProtocol(SetInfoActvity.this.app.mAppHelper).doPostDetail(app.mUser.at, mDeviceEntity.mDeviceId,
						new ProtocolHandler(SetInfoActvity.this, 0, new ProtocolHandler.CallBack() {

							@Override
							public void callBack(Result result) {
								call(result);

							}
						}), json);

			}
		});

	}

	private void showSetView() {
		if (null == mShareDialog) {

			mShareDialog = new Dialog(this);
			mShareDialog.setTitle(getString(R.string.change_name)); // 设置标题
			View view = getLayoutInflater().inflate(
					R.layout.dialog_set_device_name, null);
			mShareDialog.setContentView(view);
			view.findViewById(R.id.btn_sure).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (mEt.getEditableText().toString().equals(mDeviceEntity.mName)) {
								mTvDash.setText(getString(R.string.name_repeat));
								return;
							}
							if (!TextUtils.isEmpty(mEt.getEditableText()
									.toString().trim())) {
//								mDeviceName.setText(mEt.getEditableText().toString());
								setName(mEt.getEditableText().toString());
							} else {
//								Toast.makeText(getApplicationContext(),
//										getString(R.string.device_name_not), Toast.LENGTH_SHORT).show();
								mTvDash.setText(getString(R.string.device_name_not));
							}

						}

					});
			view.findViewById(R.id.btn_cancle).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View arg0) {

							mShareDialog.dismiss();
							mShareDialog = null;
						}
					});

			mEt = (EditText) view.findViewById(R.id.et_share_to);
			mEt.setText(mDeviceEntity.mName);
			try{
				mEt.setSelection(mDeviceEntity.mName.length());
			} catch (Exception e){
				HLog.e(TAG,e);
			}

			mEt.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
											  int arg2, int arg3) {
					// TODO Auto-generated method stub
				}

				@Override
				public void afterTextChanged(Editable arg0) {

					mTvDash.setText("");
				}
			});

			mTvDash = (TextView) view.findViewById(R.id.tv_dash);

		}
		mShareDialog.show();
	}



	public void initData(DeviceEntity mDeviceEntity) {
		mParms = mDeviceEntity.mParams;
		mModel = app.mSp.getKeepType(mDeviceEntity.mDeviceId);
		HLog.i(TAG, "init data model is:" + mModel);

		if (TextUtils.isEmpty(mDeviceEntity.mParams)) {
			mParms = "";
			mCurrentTemperature = null;
			mCurrentHumidity = null;

		} else {
			try {
				JSONObject json = new JSONObject(mDeviceEntity.mParams);
				mCurrentTemperature = !json.has("currentTemperature") ? null : Integer.valueOf(json.getString("currentTemperature"));
				mCurrentHumidity = !json.has("currentHumidity") ? null : Integer.valueOf(json.getString("currentHumidity"));
				mModel = !json.has("deviceType") ? "" : json.getString("deviceType");

				String newString = !json.has("target") ? null : json.getString("target");
				if (TextUtils.isEmpty(mTarget) || !TextUtils.isEmpty(newString)) {
					mTarget = newString;

				}


				newString = !json.has("switch") ? "off" : json.getString("switch");
				if (TextUtils.isEmpty(mSwtich) || !TextUtils.isEmpty(newString)) {
					mSwtich = newString;

				}
				newString = json.has("reaction") ? (new JSONObject(json.getString("reaction")).getString("switch")) : null;

				if (TextUtils.isEmpty(mTargetReaction) || !TextUtils.isEmpty(newString)) {
					mTargetReaction = newString;

				}
				if (TextUtils.isEmpty(mModel)) {
					mModel = "temperature";
				}

			} catch (JSONException e) {
				HLog.e(TAG, e);
			}
		}
	}
	private void setState() {
		HLog.i(TAG," setState ，mTargetReaction is "+mTargetReaction+" model is:"+mModel+" action is:"+action);
		if (mTargetReaction != null) {
			if ("on".equals(mTargetReaction)) {
				action = "执行打开";
			} else {
				action = "执行关闭";
			}


		}

		if(TextUtils.isEmpty(mTarget)){
			mTargetDes=("temperature".equals(mModel) ? "目标温度" : "目标湿度")+"未设置";
		}else{
			mTargetDes = "当"+("temperature".equals(mModel) ? "温度" : "湿度") +"超过"+ ((mTarget == null) ? "未读取" : ((TextUtils.isEmpty(mTarget) ? "未设置" : mTarget + action)));

		}
		mTvTargetDes.setText(mTargetDes);
	}

	class SetTargetDialog extends Dialog implements View.OnClickListener {



		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_sure:

					if (!"true".equals(mDeviceEntity.mOnLine)) {
						Toast.makeText(SetInfoActvity.this, getString(R.string.device_not_online), Toast.LENGTH_SHORT)
								.show();
						return;
					}
					if (TextUtils.isEmpty(mEtTarget.getText().toString().trim())) {
						Toast.makeText(SetInfoActvity.this, "请输入目标" + ("temperature".equals(mModel) ? "温度" : "湿度"), Toast.LENGTH_SHORT)
								.show();
						return;
					}
					finishSetting();
					dismiss();

					break;
				case R.id.btn_cancle:
					dismiss();
					break;

				default:
					break;
			}
		}


		public SetTargetDialog(Context context) {
			super(context);
		}





		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_set_humidity);
			Button btnSure = (Button) findViewById(R.id.btn_sure);
			Button btnCancle = (Button) findViewById(R.id.btn_cancle);

			 mRgDeviceType = (RadioGroup) findViewById(R.id.rg_device_type);
	 		mRgReaction=(RadioGroup)findViewById(R.id.rg_reaction);
			mRbTemperature=(RadioButton)findViewById(R.id.rb_temperature);
			mRbHumidity=(RadioButton)findViewById(R.id.rb_humidity);
			mEtTarget=(EditText)findViewById(R.id.et_target);
			mRbOpen=(RadioButton)findViewById(R.id.rb_open);
			mRbClose=(RadioButton)findViewById(R.id.rb_close);

			boolean temperature="temperature".equals(mModel);
			if(temperature){
				mRbTemperature.setChecked(temperature);
			}else {
				mRbHumidity.setChecked(true);

			}
			if(!TextUtils.isEmpty(mTarget)){
				mEtTarget.setText(mTarget);
			}

			mRgDeviceType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					HLog.i(TAG,"on checkout ,current model is:"+mModel);
					if(checkedId==R.id.rb_temperature){
						if(!"temperature".equals(mModel)){
							mModel = "temperature";
							app.mSp.saveKeetype(mDeviceEntity.mDeviceId,"temperature");
							setState();
						}

					}else {
						if(!"humidity".equals(mModel)){
							mModel="humidity";
							app.mSp.saveKeetype(mDeviceEntity.mDeviceId,"temperature");
							setState();
						}
					}


				}

			});

if(TextUtils.isEmpty(mTargetReaction)){
	mTargetReaction="on";

}
			if("on".equals(mTargetReaction)){
				mRbOpen.setChecked(true);
			}else {
				mRbClose.setChecked(true);
			}
			btnSure.setOnClickListener(this);
			btnCancle.setOnClickListener(this);




		}
	}

	private void clear() {

			final JSONObject json = new JSONObject();

			try {
				final JSONObject jsonParm = new JSONObject();
				jsonParm.put("deviceType", mModel);
				String targetStr = "";
				jsonParm.put("target", "");
				final JSONObject reaction = new JSONObject();

				if (!TextUtils.isEmpty(targetStr)) {
					reaction.put("switch", mRadioGroup.getCheckedRadioButtonId() == R.id.rb_open ? "on" : "off");
					jsonParm.put("reaction", reaction);
				}


				json.put("action", "update");
				json.put("apikey", mDeviceEntity.mApiKey);
				json.put("deviceid", mDeviceEntity.mDeviceId);
				json.put("params", jsonParm);

				json.put("userAgent", "app");
				json.put("sequence", System.currentTimeMillis() + "");

				elink.common.Helper.addSelfKey(json, app.mUser.apikey);

				postWsRequest(new WsRequest(json) {
					@Override
					public void callback(String msg) {
						mDeviceEntity = DbManager.getInstance(SetInfoActvity.this).queryDeviceyByDeviceId(mDeviceEntity.mDeviceId);

						if (!TextUtils.isEmpty(msg)) {

							try {

								JSONObject obj;


								obj = new JSONObject(msg);
								if (obj.has("error") && (0 == obj.getInt("error"))) {


									JSONObject params = TextUtils
											.isEmpty(mDeviceEntity.mParams) ? new JSONObject()
											: new JSONObject(mDeviceEntity.mParams);
									params.put("target", jsonParm.get("target"));
									params.put("deviceType", jsonParm.get("deviceType"));

									mTarget = "";



									setState();


									mDeviceEntity.mParams = params.toString();

									app.mDbManager.updateObject(
											mDeviceEntity, mDeviceEntity.mId);
									sendBroadcast(new Intent("com.homekit.action.UPDATE_ENTITY"));

									return;
								}
							} catch (JSONException e) {
								HLog.e(TAG, e);
							} catch (Exception e) {
								HLog.e(TAG, e);
							}

						}
						HLog.i(TAG, msg);
						Toast.makeText(SetInfoActvity.this, getString(R.string.operation_failure), Toast.LENGTH_SHORT).show();
					}
				});
			} catch (JSONException e) {
				HLog.e(TAG, e);
			}
	}


	private void finishSetting() {

			final JSONObject json = new JSONObject();


			try {
				final JSONObject jsonParm = new JSONObject();
				jsonParm.put("deviceType", mModel);
				String targetStr = mEtTarget.getText().toString();
				jsonParm.put("target", targetStr);
				final JSONObject reaction = new JSONObject();

				if (!TextUtils.isEmpty(targetStr)) {
					reaction.put("switch", mRgReaction.getCheckedRadioButtonId() == R.id.rb_open ? "on" : "off");
					jsonParm.put("reaction", reaction);
				}


				json.put("action", "update");
				json.put("apikey", mDeviceEntity.mApiKey);
				json.put("deviceid", mDeviceEntity.mDeviceId);
				json.put("params", jsonParm);

				json.put("userAgent", "app");
				json.put("sequence", System.currentTimeMillis() + "");

				elink.common.Helper.addSelfKey(json, app.mUser.apikey);

				postWsRequest(new WsRequest(json) {
					@Override
					public void callback(String msg) {
						mDeviceEntity = DbManager.getInstance(SetInfoActvity.this).queryDeviceyByDeviceId(mDeviceEntity.mDeviceId);

						if (!TextUtils.isEmpty(msg)) {

							try {

								JSONObject obj;


								obj = new JSONObject(msg);
								if (obj.has("error") && (0 == obj.getInt("error"))) {





									JSONObject params = TextUtils
											.isEmpty(mDeviceEntity.mParams) ? new JSONObject()
											: new JSONObject(mDeviceEntity.mParams);
									params.put("target",jsonParm.get("target"));
									params.put("deviceType",jsonParm.get("deviceType"));
									params.put("reaction",reaction);

									mTarget = jsonParm.getString("target");
									mTargetReaction = reaction.getString("switch");

									setState();

sendBroadcast(new Intent("com.homekit.action.UPDATE_ENTITY"));

									mDeviceEntity.mParams = params.toString();

									app.mDbManager.updateObject(
											mDeviceEntity, mDeviceEntity.mId);

									return;
								}
							} catch (JSONException e) {
								HLog.e(TAG, e);
							} catch (Exception e) {
								HLog.e(TAG, e);
							}

						}
						HLog.i(TAG, msg);
						Toast.makeText(SetInfoActvity.this, getString(R.string.operation_failure), Toast.LENGTH_SHORT).show();
					}
				});
			} catch (JSONException e) {
				HLog.e(TAG, e);
			}
		}



}

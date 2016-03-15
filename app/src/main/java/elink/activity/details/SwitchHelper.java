package elink.activity.details;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;
import com.coolkit.common.WsRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import elink.DeviceHelper;
import elink.activity.BasicActivity;
import elink.common.Helper;
import elink.common.UiHelper;
import elink.entity.DeviceEntity;
import elink.model.DbManager;

public class SwitchHelper extends DetailHelper implements OnClickListener {

	static final String TAG = SwitchHelper.class.getSimpleName();

	Switch s0 = new Switch();
	Switch s1 = new Switch();
	Switch s2 = new Switch();
	Switch s3 = new Switch();

	List<Switch> switches = new ArrayList<Switch>();


	public  class Switch {
		public boolean isOpen;
		public String name;
		public int outlet = 0;
	}

	ImageView mIvS0;
	ImageView mIvS1;
	ImageView mIvS3;
	ImageView mIvS2;
	ImageView mSwitchBtn;
	public Switch mCurrentS;
	ImageView mSelectedS;
	ImageView mIvState;
	private int mCurentItem;

	private BasicActivity mContext;

	public String mParms;

	public boolean mSingle;

	View viewContent;

	private LinearLayout mLlayout;
	List<View> tabs=new ArrayList<View>();

	public SwitchHelper(BasicActivity context) {
		//super(context);
		mContext = context;
	}

	public View findViewById(int id) {
		return viewContent.findViewById(id);

	}

	public void initView(ViewGroup viewParent) {
		viewContent = mContext.getLayoutInflater().inflate(
				R.layout.detail_switch, viewParent);

		mLlayout = (LinearLayout) findViewById(R.id.ll_state);
		mSwitchBtn = (ImageView) findViewById(R.id.iv_btn);
		mIvState = (ImageView) findViewById(R.id.iv_state);

		mIvState.setOnClickListener(this);
		mSwitchBtn.setOnClickListener(this);

			if (!mSingle) {
				HLog.i(TAG, "is not sigle");

				switches.clear();
				switches.add(s0);
				switches.add(s1);
				switches.add(s2);
				switches.add(s3);
				mIvS0 = (ImageView) findViewById(R.id.iv_sw_1);
				mIvS1 = (ImageView) findViewById(R.id.iv_sw_2);
				mIvS2 = (ImageView) findViewById(R.id.iv_sw_3);
				mIvS3 = (ImageView) findViewById(R.id.iv_sw_4);

				for (int i = 0; i < 4; i++) {
					View child=mLlayout.getChildAt(i);
					if(i<mSwitchSize){
						tabs.add(child);
					//此处添加
//						if("false".equals(mDeviceEntity.mOnLine)){
////							mLlayout.getChildAt(i).setEnabled(false);
//							child.setOnClickListener(new OnClickListener() {
//								@Override
//								public void onClick(View v) {
////									mSelectedS = mLlayout.getChildAt(i);
//									Toast.makeText(mContext,mContext.getResources().getString(R.string.lineoff_device),Toast.LENGTH_SHORT).show();
//								}
//							});
//						}else{
//							child.setOnClickListener(this);
//						}
						child.setOnClickListener(this);
					}else{
						child.setVisibility(View.GONE);
					}
				}
				if(mSelectedS==null){
					mSelectedS = mIvS0;
				}

			}
			mLlayout.setVisibility(mSingle?View.GONE:View.VISIBLE);

		}




	OnGestureListener listener = new OnGestureListener() {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			HLog.i(TAG,"onSingleTapUp");
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			HLog.i(TAG,"onShowPress");

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			HLog.i(TAG,"onScroll");
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			HLog.i(TAG,"onLongPress");

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			HLog.i(TAG,"onFling");
			float dx = e2.getX() - e1.getX();
			float dy = e2.getY() - e1.getY();
			boolean mIsHorizontal = false;
			if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
				mIsHorizontal = true;
			} else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
				mIsHorizontal = false;
			} else {
			}
			if (mIsHorizontal) {
				// slip to right
				if (dx > 0) {
					if (mCurentItem > 0) {
						tabs.get(mCurentItem - 1).callOnClick();
					}
				} else {
					if (mCurentItem < mSwitchSize-1) {
						tabs.get(mCurentItem + 1).callOnClick();
					}

				}
			}

			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			HLog.i(TAG,"onDown");
			return true;
		}
	};

	public boolean onTouchEvent(MotionEvent event) {
		HLog.i(TAG,"onTouchevent");
		if (!mSingle) {
			return detector.onTouchEvent(event);
		}
		return false;

	}

	GestureDetector detector = new GestureDetector(listener);

	private boolean mIsSwitch;

	public  int mSwitchSize;

	public void initData(DeviceEntity deviceEntity) {
		mDeviceEntity = deviceEntity;
		if(null!=mDeviceEntity){
			HLog.i(TAG, "switch helper : params is:" + mDeviceEntity.mParams);
			mSingle = DeviceHelper.UI_PLUG_SINGLE == mDeviceEntity.mUi
					|| DeviceHelper.UI_SWITH_ONE == mDeviceEntity.mUi||DeviceHelper.UI_PLUG_WATER==mDeviceEntity.mUi;

			boolean hasParms = true;
			parseType(mDeviceEntity.mUi);
			if (TextUtils.isEmpty(mDeviceEntity.mParams)) {
				hasParms = false;
			}

			try {
				s0.name = mContext.getString(R.string.device_one);
				s0.outlet = 0;

				s1.name = mContext.getString(R.string.device_two);
				s1.outlet = 1;

				s2.name = mContext.getString(R.string.device_three);
				s2.outlet = 2;

				s3.name = mContext.getString(R.string.device_four);
				s3.outlet = 3;

				if (!mSingle && hasParms) {
					HLog.i(TAG, "switch helper : no single and has params");

					JSONObject params = new JSONObject(mDeviceEntity.mParams);

					JSONArray switcher = new JSONArray(params.getString("switches"));

					JSONObject obj = switcher.getJSONObject(0);
					s0.isOpen = obj.get("switch").equals("on");

					JSONObject obj1 = switcher.getJSONObject(1);
					s1.isOpen = obj1.get("switch").equals("on");

					JSONObject obj2 = switcher.getJSONObject(2);
					s2.isOpen = obj2.get("switch").equals("on");

					JSONObject obj3 = switcher.getJSONObject(3);
					s3.isOpen = obj3.get("switch").equals("on");
					HLog.i(TAG,params.toString());
				}

				else {
					if (hasParms) {
						JSONObject params = new JSONObject(mDeviceEntity.mParams);

						HLog.i(TAG,"switchHelper switch:"+params.get("switch"));
						s0.isOpen = params.get("switch").equals("on");
					}

					s0.name = mContext.getString(R.string.device_one);
				}
			} catch (JSONException e) {
				HLog.e(TAG,e);
			}
			if(null==mCurrentS){
				mCurrentS = s0;
			}

		}
	}

	@Override
	public String getTimerParams() {
		JSONObject parm3 = new JSONObject();
		try {
			parm3.put("switch", mCurrentS.isOpen ? "on" : "off");
			if(!mSingle){
				parm3.put("outlet", mCurrentS.outlet);
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parm3.toString();
	}

	public void postStateChange() {
		final JSONObject json = new JSONObject();



		try {
			JSONObject jsonParm=new JSONObject(mParms);
			HLog.i(TAG,"jsonParm1:"+jsonParm.toString());
			jsonParm.remove("startup");
			HLog.i(TAG,"jsonParm1++:"+jsonParm.toString());
			if (!mSingle) {
				json.put("action", "update");
				json.put("apikey", mDeviceEntity.mApiKey);
				json.put("deviceid", mDeviceEntity.mDeviceId);
				json.put("params", jsonParm);
				json.put("userAgent", "app");
				HLog.i(TAG, "send switch:" + json);
				Helper.addSelfKey(json, mContext.app.mUser.apikey);

			} else {
				json.put("action", "update");
				json.put("apikey", mDeviceEntity.mApiKey);
				json.put("deviceid", mDeviceEntity.mDeviceId);
				json.put("params", jsonParm);

				json.put("userAgent", "app");
				json.put("sequence", System.currentTimeMillis() + "");
			}


			Helper.addSelfKey(json,mContext.app.mUser.apikey);

			mContext.postWsRequest(new WsRequest(json) {
				@Override
				public void callback(String msg) {
					mDeviceEntity = DbManager.getInstance(mContext).queryDeviceyByDeviceId(mDeviceEntity.mDeviceId);

					if (!TextUtils.isEmpty(msg)) {
						JSONObject obj;
						try {
							obj = new JSONObject(msg);
							if (obj.has("error") && (0 == obj.getInt("error"))) {
								mCurrentS.isOpen = !mCurrentS.isOpen;

								setState();
								JSONObject params = TextUtils
										.isEmpty(mDeviceEntity.mParams) ? new JSONObject()
										: new JSONObject(mDeviceEntity.mParams);

								JSONObject mLocalParm = new JSONObject(
										SwitchHelper.this.mParms);

								HLog.i(TAG,"mDeviceEntity params is:"+mDeviceEntity.mParams+" local parms is:"+SwitchHelper.this.mParms);

								if (mSingle) {
									if (params.has("switch")) {
										params.remove("switch");

									}
									params.put("switch",
											mLocalParm.get("switch"));
								} else {

									if (params.has("switch")) {
										params.remove("switches");

									}

									params.put("switches",
											mLocalParm.get("switches"));
								}


								HLog.i(TAG,"update to db:"+params.toString());

								mDeviceEntity.mParams = params.toString();
								mContext.app.mDbManager.updateObject(
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
					Toast.makeText(mContext,mContext.getString(R.string.operation_failure) , Toast.LENGTH_SHORT).show();
				}
			});
		} catch (JSONException e) {
			HLog.e(TAG,e);
		}
	}

	public void parseType(int ui) {
		switch (ui) {
		case DeviceHelper.UI_SWITH_ONE:
			mSwitchSize = 1;
			mIsSwitch = true;
			return;
		case DeviceHelper.UI_SWITH_TWO:
			mSwitchSize = 2;
			mIsSwitch = true;
			return;
		case DeviceHelper.UI_SWITH_THREE:
			mSwitchSize = 3;
			mIsSwitch = true;
			return;
		case DeviceHelper.UI_SWITH_FOUR:
			mSwitchSize = 4;
			mIsSwitch = true;
			return;

		case DeviceHelper.UI_PLUG_SINGLE:
		case DeviceHelper.UI_PLUG_SINGLE_WITH_POWER:
			mSwitchSize = 1;
			break;
		case DeviceHelper.UI_PLUG_TWO:
			mSwitchSize = 2;
			break;
		case DeviceHelper.UI_PLUG_THREE:
			mSwitchSize = 3;
			break;
		case DeviceHelper.UI_PLUG_FOUR:
			mSwitchSize = 4;
			break;

		default:
			break;
		}
		return;

	}

	public Drawable getImageViewState() {
		//添加代码

			if("true".equals(mDeviceEntity.mOnLine)){
				if (DeviceHelper.UI_PLUG_WATER == mDeviceEntity.mUi) {
					return getResources().getDrawable(
							mCurrentS.isOpen ? R.drawable.detail_water_on
									: R.drawable.detail_water_off);
				} else if (mIsSwitch) {
					return getResources().getDrawable(
							mCurrentS.isOpen ? R.drawable.switch_btn_state_open
									: R.drawable.switch_btn_state_close);
				}

				else {

					return getResources().getDrawable(
							mCurrentS.isOpen ? R.drawable.switch_state_opne
									: R.drawable.switch_state_close);
				}

			}else{
				return getResources().getDrawable(R.drawable.new_offline_switch);
			}

	}

	public Drawable setTab(Switch s) {
		if (mIsSwitch)
			return getResources().getDrawable(
					s.isOpen ? R.drawable.switch_tab_state_open
							: R.drawable.switch_tab_state_o);
		else {
			return getResources().getDrawable(
					s.isOpen ? R.drawable.swich_detail_open
							: R.drawable.switch_detail_close);
		}

	}

	public void setState() {
		HLog.i(TAG,"set state :"+mCurrentS.name);

		mSwitchBtn.setImageDrawable(mContext.getResources()
				.getDrawable(
						mCurrentS.isOpen ? R.drawable.icon_open
								: R.drawable.icon_close));

		mIvState.setImageDrawable(getImageViewState());

		if (!mSingle) {
			mIvS0.setImageDrawable(setTab(s0));
			mIvS1.setImageDrawable(setTab(s1));
			mIvS2.setImageDrawable(setTab(s2));
			mIvS3.setImageDrawable(setTab(s3));

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				//mIvS0.setBackgroundResource(R.color.white);
			mIvS0.setBackground(mContext.getResources().getDrawable(
					R.color.white));
			//	mIvS1.setBackgroundResource(R.color.white);
			mIvS1.setBackground(mContext.getResources().getDrawable(
					R.color.white));
			//	mIvS2.setBackgroundResource(R.color.white);
			mIvS2.setBackground(mContext.getResources().getDrawable(
					R.color.white));
			//	mIvS3.setBackgroundResource(R.color.white);
			mIvS3.setBackground(mContext.getResources().getDrawable(
					R.color.white));
			//	mSelectedS.setBackgroundResource(R.color.gray);
			mSelectedS.setBackground(mContext.getResources().getDrawable(
					R.color.gray));


			} else {
				//mIvS0.setImageDrawable(mContext.getDrawable(R.color.white));
				mIvS0.setBackgroundResource(R.color.white);
//			mIvS0.setBackground(mContext.getResources().getDrawable(
//					R.color.white));
				mIvS1.setBackgroundResource(R.color.white);
//			mIvS1.setBackground(mContext.getResources().getDrawable(
//					R.color.white));
				mIvS2.setBackgroundResource(R.color.white);
//			mIvS2.setBackground(mContext.getResources().getDrawable(
//					R.color.white));
				mIvS3.setBackgroundResource(R.color.white);
//			mIvS3.setBackground(mContext.getResources().getDrawable(
//					R.color.white));
//				mSelectedS.setImageDrawable(mContext.getDrawable(R.color.gray));
				mSelectedS.setBackgroundResource(R.color.gray);
//			mSelectedS.setBackground(mContext.getResources().getDrawable(
//					R.color.gray));

			}
//			mIvS0.setBackgroundResource(R.color.white);
////			mIvS0.setBackground(mContext.getResources().getDrawable(
////					R.color.white));
//			mIvS1.setBackgroundResource(R.color.white);
////			mIvS1.setBackground(mContext.getResources().getDrawable(
////					R.color.white));
//			mIvS2.setBackgroundResource(R.color.white);
////			mIvS2.setBackground(mContext.getResources().getDrawable(
////					R.color.white));
//			mIvS3.setBackgroundResource(R.color.white);
////			mIvS3.setBackground(mContext.getResources().getDrawable(
////					R.color.white));
//			mSelectedS.setBackgroundResource(R.color.gray);
////			mSelectedS.setBackground(mContext.getResources().getDrawable(
////					R.color.gray));

		}

	}

	public void setParms() {

		mCurrentS.isOpen = !mCurrentS.isOpen;

		if (!mSingle) {

			mParms = "{\"switches\":[{\"switch\":\""
					+ (s0.isOpen ? "on" : "off")
					+ "\",\"outlet\":0},{\"switch\":\""
					+ (s1.isOpen ? "on" : "off")
					+ "\",\"outlet\":1},{\"switch\":\""
					+ (s2.isOpen ? "on" : "off")
					+ "\",\"outlet\":2},{\"switch\":\""
					+ (s3.isOpen ? "on" : "off") + "\",\"outlet\":3}]}";
		} else {
			mParms = "{\"switch\":\"" + (mCurrentS.isOpen ? "on" : "off")
					+ "\"}";
		}
		if (!isTimer) {

			mCurrentS.isOpen = !mCurrentS.isOpen;
		}

	}

	public void submitTimer(String type, String at) {
		final JSONObject json = new JSONObject();
		try {

			json.put("action", "update");
			json.put("apikey", mDeviceEntity.mApiKey);
			json.put("deviceid", mDeviceEntity.mDeviceId);
			Helper.addSelfKey(json, mContext.app.mUser.apikey);

			if (!mSingle) {

				// JSONObject aTimer0 = new JSONObject();
				// aTimer0.put("enabled", true);
				// aTimer0.put("type", type);
				// // aTimer0.put("at", "2015-07-14T03:04:00.000Z");
				// aTimer0.put("at", at);
				// JSONObject parm = new JSONObject();
				// parm.put("switch", s0.isOpen ? "on" : "off");
				// parm.put("outlet", 0);
				// aTimer0.put("do", parm);

				JSONObject aTimer1 = new JSONObject();
				aTimer1.put("enabled", true);
				aTimer1.put("type", type);
				aTimer1.put("at", at);
				JSONObject parm1 = new JSONObject();
				parm1.put("switch", s1.isOpen ? "on" : "off");
				parm1.put("outlet", 1);
				aTimer1.put("do", parm1);

				JSONObject aTimer2 = new JSONObject();
				aTimer2.put("enabled", true);
				aTimer2.put("type", type);
				aTimer2.put("at", at);
				JSONObject parm2 = new JSONObject();
				parm2.put("switch", s2.isOpen ? "on" : "off");
				parm2.put("outlet", 2);
				aTimer2.put("do", parm2);

				JSONObject aTimer3 = new JSONObject();
				aTimer3.put("enabled", true);
				aTimer3.put("type", type);
				aTimer3.put("at", at);
				JSONObject parm3 = new JSONObject();
				parm3.put("switch", s3.isOpen ? "on" : "off");
				parm3.put("outlet", 3);
				aTimer3.put("do", parm3);
				JSONArray array = new JSONArray();

				// array.put(aTimer0);
				array.put(aTimer1);
				array.put(aTimer2);
				array.put(aTimer3);
				JSONObject timers = new JSONObject();
				timers.put("timers", array);
				// json.put("timers", array);
				json.put("params", timers);
				json.put("userAgent", "app");


				this.mContext.postWsRequest(new WsRequest(json) {
					@Override
					public void callback(String msg) {
						HLog.i(TAG, "post call back:" + msg);
						super.callback(msg);
					}
				});
			} else {
				JSONObject aTimer0 = new JSONObject();
				aTimer0.put("enabled", true);
				aTimer0.put("type", type);
				// aTimer0.put("at", "2015-07-14T03:04:00.000Z");
				aTimer0.put("at", at);
				JSONObject parm = new JSONObject();
				parm.put("switch", s0.isOpen ? "on" : "off");

				aTimer0.put("do", parm);
				JSONArray array = new JSONArray();
				array.put(aTimer0);
				JSONObject timers = new JSONObject();
				timers.put("timers", array);
				// json.put("timers", array);
				json.put("params", timers);
				json.put("userAgent", "app");


				this.mContext.postWsRequest(new WsRequest(json) {
					@Override
					public void callback(String msg) {
						HLog.i(TAG, "post call back:" + msg);
						super.callback(msg);
					}
				});
			}

			// json.p
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setView() {

		// if(timerCount>0&&!isTimer){
		//
		// mIvSetTimer.setImageBitmap(ImageUtils.drawTextToBitmap(mContext,
		// R.drawable.timer, 8+""));
		// }
		// selectItem();
		setState();

	}

	public void selectItem() {
		setState();
//		if (true)
//			return;
//		if (!mSingle) {
//			mIvS0.setImageDrawable(s0.isOpen ? getResources().getDrawable(
//					R.drawable.swich_detail_open) : getResources().getDrawable(
//					R.drawable.switch_detail_close));
//			mIvS1.setImageDrawable(s1.isOpen ? getResources().getDrawable(
//					R.drawable.swich_detail_open) : getResources().getDrawable(
//					R.drawable.switch_detail_close));
//			mIvS2.setImageDrawable(s2.isOpen ? getResources().getDrawable(
//					R.drawable.swich_detail_open) : getResources().getDrawable(
//					R.drawable.switch_detail_close));
//			mIvS3.setImageDrawable(s3.isOpen ? getResources().getDrawable(
//					R.drawable.swich_detail_open) : getResources().getDrawable(
//					R.drawable.switch_detail_close));
//
//			mIvS0.setBackground(mContext.getResources().getDrawable(
//					R.color.white));
//
//			// ColorDrawable bd = (ColorDrawable)mIvS0.getBackground();
//			// Bitmap.cre
//			// Bitmap bm = bd.get
//			//
//			// BitmapDrawable sss=new
//			// BitmapDrawable(ImageUtils.createWaterMaskImage(mContext,bm,
//			// BitmapFactory.decodeResource(getResources(),
//			// R.drawable.timer_tip)));
//			// mIvS0.setBackground(sss);
//			mIvS1.setBackground(mContext.getResources().getDrawable(
//					R.color.white));
//
//			mIvS2.setBackground(mContext.getResources().getDrawable(
//					R.color.white));
//
//			mIvS3.setBackground(mContext.getResources().getDrawable(
//					R.color.white));
//			mSelectedS.setBackground(mContext.getResources().getDrawable(
//					R.color.gray));
//
//		}
//
//		mSwitchBtn.setImageDrawable(getResources()
//				.getDrawable(
//						mCurrentS.isOpen ? R.drawable.icon_open
//								: R.drawable.icon_close));
//
//		if (HkConst.UI_PLUG_WATER == mDeviceEntity.mUi) {
//			mIvState.setImageDrawable(getResources().getDrawable(
//					mCurrentS.isOpen ? R.drawable.detail_water_on
//							: R.drawable.detail_water_off));
//		} else {
//			mIvState.setImageDrawable(getResources().getDrawable(
//					mCurrentS.isOpen ? R.drawable.switch_state_opne
//							: R.drawable.switch_state_close));
//		}

	}

	private Resources getResources() {
		return mContext.getResources();
	}

	@Override
	public void onClick(View arg0) {
		if(null!=mDeviceEntity){
			if("false".equals(mDeviceEntity.mOnLine)){
				switch (arg0.getId()){
					case R.id.iv_sw_1:
						mCurentItem = 0;
						mSelectedS = mIvS0;
						mCurrentS = s0;
						UiHelper.showShortToast(mContext,getResources().getString(R.string.lineoff_device));
						selectItem();
						break;
					case R.id.iv_sw_2:
						mCurentItem = 1;
						mSelectedS = mIvS1;
						mCurrentS = s1;
						UiHelper.showShortToast(mContext,getResources().getString(R.string.lineoff_device));
						selectItem();
						break;
					case R.id.iv_sw_3:
						mCurentItem = 2;
						mSelectedS = mIvS2;
						mCurrentS = s2;
						UiHelper.showShortToast(mContext,getResources().getString(R.string.lineoff_device));
						selectItem();
						break;
					case R.id.iv_sw_4:
						mCurentItem = 3;
						mSelectedS = mIvS3;
						mCurrentS = s3;
						UiHelper.showShortToast(mContext,getResources().getString(R.string.lineoff_device));
						selectItem();
						break;
					case R.id.iv_btn:
						if (isTimer) {
							setParms();
							setState();
						} else {
							if (!"true".equals(mDeviceEntity.mOnLine)) {
								Toast.makeText(mContext, mContext.getString(R.string.device_not_online), Toast.LENGTH_SHORT)
										.show();
								return;
							}
							setParms();
							postStateChange();
						}

					default:
						break;
				}
			}else{
				switch (arg0.getId()) {
					case R.id.iv_sw_1:
						mCurentItem = 0;
						mSelectedS = mIvS0;
						mCurrentS = s0;
						selectItem();
						break;
					case R.id.iv_sw_2:
						mCurentItem = 1;
						mSelectedS = mIvS1;
						mCurrentS = s1;
						selectItem();
						break;
					case R.id.iv_sw_3:
						mCurentItem = 2;
						mSelectedS = mIvS2;
						mCurrentS = s2;
						selectItem();
						break;
					case R.id.iv_sw_4:
						mCurentItem = 3;
						mSelectedS = mIvS3;
						mCurrentS = s3;
						selectItem();
						break;
					case R.id.iv_btn:
						if (isTimer) {
							setParms();
							setState();
						} else {
							if (!"true".equals(mDeviceEntity.mOnLine)) {
								Toast.makeText(mContext, mContext.getString(R.string.device_not_online), Toast.LENGTH_SHORT)
										.show();
								return;
							}
							setParms();
							postStateChange();
						}

					default:
						break;
				}
			}
		}else{
			UiHelper.showShortToast(mContext,mContext.getString(R.string.data_exception));
		}

	}

}

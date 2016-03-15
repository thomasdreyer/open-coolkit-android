package elink.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import elink.common.UiHelper;
import demo.demo.R;
import com.coolkit.common.HLog;
import com.coolkit.common.WsRequest;

import elink.DeviceHelper;
import elink.HkConst;
import elink.common.Helper;
import elink.activity.details.DetailHelper;
import elink.adapt.BaseListAdapt;
import elink.entity.DeviceEntity;
import elink.entity.Timer;
import elink.model.DbManager;
import elink.utils.Debugg;
import elink.utils.HomekitFormate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SetTimerActvity extends BasicActivity implements OnClickListener,
		OnItemClickListener, OnTouchListener {

	private static final String TAG = SetTimerActvity.class.getSimpleName();

	protected int retry = 60;
	public int mBlue;

	private Button mBtnAddTimer;

	private View layoutTimerList;

	private View layoutAdd;

	private TextView mTvone;

	private TextView mTvThree;

	private TextView mTvFour;

	private TextView mTvFive;

	private TextView mTvSix;

	private TextView mTvSeven;

	private TextView mTvTwo;

	Set repeateList = new HashSet();

	private DetailHelper helper;

	private DeviceEntity mDeviceEntity;

	private int mCurrentHour;

	protected int mCurrentMin;
	private List<Timer> mTimerList = new ArrayList<Timer>();

	private ListView mLvTimer;

	private TimerListAdapt mAdapt;

	protected int mCurrentYear;

	protected int mCurrentDay;

	private Button mBtnFinish;

	private TimePicker mTimePicker;

	private DatePicker mDataPicker;

	private ViewGroup mContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HLog.i("11", "ShareDeviceActvity oncreate");
		setContentView(R.layout.activiy_timer);

		initData();
		if(helper==null){
			finish();
			return;
		}
		initView();
		setView();
		doPost();
		Helper.broadcastTimerChangeList(SetTimerActvity.this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (layoutTimerList.getVisibility() != View.VISIBLE) {
			goBackList();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home
				&& layoutTimerList.getVisibility() != View.VISIBLE) {
			goBackList();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (Debugg.DEBUG_TOUCH) {
			return helper.onTouchEvent(event);
		}

		return super.onTouchEvent(event);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAdapt.refresh();
			try {
				UiHelper.setListViewHeightBasedOnChildren(mLvTimer,
						SetTimerActvity.this);
			} catch (Exception e) {
				HLog.e(TAG, e);
			}
		};

	};

	private ScrollView mScrollView;

	private void doPost() {
		this.postRequest(new Runnable() {

			@Override
			public void run() {
				mTimerList.clear();
				List<Timer> list = DbManager.getInstance(SetTimerActvity.this)
						.queryTimerByDeviceId(mDeviceEntity.mDeviceId);
				HLog.i(TAG, "query device timer size:"
						+ ((null == list) ? "null" : list.size()));

				mTimerList.addAll(list);
				mHandler.sendEmptyMessage(0);
			}
		});

	}

	// return cal.
	private void initData() {
		mTitle = getString(R.string.timer);
		String devId = getIntent().getStringExtra(HkConst.EXTRA_D_ID);
		mDeviceEntity = DbManager.getInstance(this).queryDeviceyByDeviceId(devId);

		mAdapt = new TimerListAdapt(mTimerList);
		if (null == mDeviceEntity) {
			finish();
			HLog.i(TAG, "has no Device entity ,finish()");
			return;
		}
		if(helper==null){
			helper = DeviceHelper.setDetailHelper(this, mDeviceEntity.mUi);
		}

		if (null == helper) {
			finish();
			HLog.i(TAG, "has no helper ,finish()");
			return;
		}
		helper.isTimer = true;
		helper.initData(mDeviceEntity);

	}

	class TimerListAdapt extends BaseListAdapt<Timer> {

		public TimerListAdapt(List<Timer> timers) {
			super(timers);
		}


	public String getStr(int input){
		if(input<10) return "0"+input;
		else return input+"";
	}
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			final Timer data = mData.get(arg0);
			View view = null;
			HLog.i(TAG, "get view timer:" + data.at);
			view = getLayoutInflater().inflate(R.layout.t_item, null);

			TextView timer = (TextView) view.findViewById(R.id.tv_time);

			// timer.setText(" d");

			TextView report = (TextView) view.findViewById(R.id.tv_repet_type);
			if ("once".equals(data.typ)) {
				report.setText(getString(R.string.once));
				SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
				timer.setText(formatter.format(HomekitFormate.getLocal(data.at)));
			} else {

				String[] arrays = data.at.split(" ");
				if (arrays != null && arrays.length == 5) {
					String hour=getStr(HomekitFormate.toLocalHour(Integer
							.parseInt(arrays[1])));
					String minus=getStr(Integer.parseInt(arrays[0]));
					timer.setText(hour + ":" + minus);
					String repeatype = "";

					if ("0,1,2,3,4,5,6".equals(arrays[4])) {

						repeatype = getString(R.string.everyday);
					} else if ("1,2,3,4,5".equals(arrays[4])) {
						repeatype = getString(R.string.days);
					} else if ("0,6".equals(arrays[4])) {
						repeatype = getString(R.string.weekends);
					} else {
						String[] days = arrays[4].split(",");
						StringBuffer sb = new StringBuffer();
						if (null != days) {
							sb.append(getString(R.string.week));
							for (int i = 0; i < days.length; i++) {
								if ("1".equals(days[i])) {
									sb.append(getString(R.string.one));
								} else if ("2".equals(days[i])) {
									sb.append(getString(R.string.two));
								} else if ("3".equals(days[i])) {
									sb.append(getString(R.string.three));
								}

								else if ("4".equals(days[i])) {
									sb.append(getString(R.string.four));
								} else if ("5".equals(days[i])) {
									sb.append(getString(R.string.five));
								} else if ("6".equals(days[i])) {
									sb.append(getString(R.string.six));
								} else if ("0".equals(days[i])) {
									sb.append(getString(R.string.day));
								}
							}
						}
						repeatype = sb.toString();

					}
					report.setText(repeatype);
				}

			}

			ImageView del = (ImageView) view.findViewById(R.id.btn_del);
			del.setTag(data);
			del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					app.mDbManager.deleteObject(data, "mId", data.mId);
					mTimerList.remove(data);
					mHandler.sendEmptyMessage(0);
					submitTimerList(false);

				}
			});

			return view;
		}
	}

	public void addTimer() {
		if(mTimerList!=null&&mTimerList.size()>7){
			UiHelper.showShortToast(this,getString(R.string.only_number));
		}else{
			layoutTimerList.setVisibility(View.GONE);
			mScrollView.setVisibility(View.VISIBLE);
		}

	}


	private void initView() {

		mScrollView = (ScrollView) findViewById(R.id.sv_parent);
		mLvTimer = (ListView) findViewById(R.id.lv_timers);

		mTvone = (TextView) findViewById(R.id.tv_one);
		mTvTwo = (TextView) findViewById(R.id.tv_two);

		mTvThree = (TextView) findViewById(R.id.tv_three);

		mTvFour = (TextView) findViewById(R.id.tv_four);

		mTvFive = (TextView) findViewById(R.id.tv_five);

		mTvSix = (TextView) findViewById(R.id.tv_six);

		mTvSeven = (TextView) findViewById(R.id.tv_seven);
		mBtnFinish = (Button) findViewById(R.id.btn_finish);
		mBtnFinish.setOnClickListener(this);

		mTvone.setOnClickListener(this);
		mTvTwo.setOnClickListener(this);

		mTvThree.setOnClickListener(this);

		mTvFour.setOnClickListener(this);

		mTvFive.setOnClickListener(this);

		mTvSix.setOnClickListener(this);

		mTvSeven.setOnClickListener(this);

		mBtnAddTimer = (Button) findViewById(R.id.btn_add_timer);
		mBtnAddTimer.setOnClickListener(this);

		layoutTimerList = findViewById(R.id.layou_timers);
		layoutAdd = findViewById(R.id.ll_cal);

		mTimePicker = (TimePicker) this.findViewById(R.id.time_picker);
		mDataPicker = (DatePicker) this.findViewById(R.id.date_picker);

		mContainer = (ViewGroup) this.findViewById(R.id.ll_detail_container);
		helper.initView(mContainer);

		mScrollView.setOnTouchListener(this);
	}

	protected void setView() {
		super.setView();
		setTitleColor(mBlue);
		((ViewGroup) ((ViewGroup) mDataPicker.getChildAt(0)).getChildAt(0))
				.getChildAt(0).setVisibility(View.GONE);

		UiHelper.resizePikcer(mDataPicker);
		UiHelper.resizePikcer(mTimePicker);

		helper.setView();
		mTimePicker.setIs24HourView(true);
		mLvTimer.setAdapter(mAdapt);

	}

	public void setWeek(TextView view) {
		Boolean selected = view.getTag() == null ? false : (Boolean) view
				.getTag();

		if (selected) {

			view.setBackgroundResource(R.drawable.round_gray);
//			view.setBackground(getResources()
//					.getDrawable(R.drawable.round_gray));

		} else {
			view.setBackgroundResource(R.drawable.round_blue);
//			view.setBackground(getResources()
//					.getDrawable(R.drawable.round_blue));
		}
		selected = !selected;
		view.setTag(selected);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_add_timer:
			Log.i(TAG, " on clikc btn_add_timer");
			addTimer();
			break;
		case R.id.tv_one:
			setWeek((TextView)arg0);
			break;
		case R.id.tv_two:
			setWeek((TextView)arg0);
			break;
		case R.id.tv_three:
			setWeek((TextView)arg0);
			break;
		case R.id.tv_four:
			setWeek((TextView)arg0);
			break;
		case R.id.tv_five:
			setWeek((TextView)arg0);
			break;
		case R.id.tv_six:
			setWeek((TextView)arg0);
			break;
		case R.id.tv_seven:
			setWeek((TextView)arg0);
			break;
		case R.id.btn_finish:
			submitTimer();

			break;

		default:
			break;
		}

	}

	public void submitTimerList(final boolean add) {
		final JSONObject json = new JSONObject();
		try {

			json.put("action", "update");
			json.put("apikey", mDeviceEntity.mApiKey);
			json.put("deviceid", mDeviceEntity.mDeviceId);

			if (null != mTimerList) {
				JSONArray array = new JSONArray();

				for (Timer aTimer : mTimerList) {
					JSONObject aTimer0 = new JSONObject();
					aTimer0.put("enabled", 1);
					aTimer0.put("type", aTimer.typ);

					aTimer0.put("at", aTimer.at);
					aTimer0.put("do", new JSONObject(aTimer.doAction));

					array.put(aTimer0);
				}

				JSONObject timers = new JSONObject();

				timers.put("timers", array);
				json.put("params", timers);
				json.put("userAgent", "app");
				Helper.addSelfKey(json, app.mUser.apikey);
				json.put("sequence", System.currentTimeMillis() + "");
				postWsRequest(new WsRequest(json) {
					@Override
					public void callback(String msg) {
						if (!TextUtils.isEmpty(msg)) {
							JSONObject obj;
							try {
								obj = new JSONObject(msg);
								if (obj.has("error")
										&& (0 == obj.getInt("error"))) {
									if (add) {
										Toast.makeText(SetTimerActvity.this,
												getString(R.string.add_timer_success), Toast.LENGTH_SHORT)
												.show();
										goBackList();
									} else {
										Toast.makeText(SetTimerActvity.this,
												getString(R.string.delete_timer_success), Toast.LENGTH_SHORT)
												.show();
										goBackList();
									}
									Helper.broadcastSynLocalDevice(SetTimerActvity.this);
									Helper.broadcastTimerChangeList(SetTimerActvity.this);

									return;
								}
							} catch (JSONException e) {
								HLog.e(TAG, e);
							}

						}
						HLog.i(TAG, msg);
						if(add){
							Toast.makeText(SetTimerActvity.this, getString(R.string.add_timer_failed),
									Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(SetTimerActvity.this, getString(R.string.delete_timer_failed),
									Toast.LENGTH_SHORT).show();
						}
						
					}
				});

			}

			// json.p
		} catch (JSONException e) {
			HLog.e(TAG,e);
		}

	}

	protected void goBackList() {
		layoutTimerList.setVisibility(View.VISIBLE);
		mScrollView.setVisibility(View.GONE);

	}

	public boolean getSelectString(View view) {
		return (view.getTag() == null) ? false : (Boolean) view.getTag();
	}

	// aTimer0.put("at", "2015-07-14T03:04:00.000Z");
	private void submitTimer() {

		String timer = (getSelectString(mTvSeven) ? "0," : "")
				+ (getSelectString(mTvone) ? "1," : "")
				+ (getSelectString(mTvTwo) ? "2," : "")
				+ (getSelectString(mTvThree) ? "3," : "")
				+ (getSelectString(mTvFour) ? "4," : "")
				+ (getSelectString(mTvFive) ? "5," : "")
				+ (getSelectString(mTvSix) ? "6," : "");

		if (timer.endsWith(",")) {
			timer = timer.substring(0, timer.length() - 1);
		}

		String type = TextUtils.isEmpty(timer) ? "once" : "repeat";

		String at ;
		if ("once".equals(type)) {



			//
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, mDataPicker.getYear());
			cal.set(Calendar.MONTH, mDataPicker.getMonth() + 1);
			cal.set(Calendar.DAY_OF_MONTH, mDataPicker.getDayOfMonth());
			cal.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
			cal.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
			int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

			// 3、取得夏令时差：
			int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

			// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
			cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));


			
			at = cal.get(Calendar.YEAR) + "-"
					+ getIntString(cal.get(Calendar.MONTH)) + "-"
					+ getIntString(cal.get(Calendar.DAY_OF_MONTH)) + "T"
					+ getIntString(cal.get(Calendar.HOUR_OF_DAY)) + ":"
					+ getIntString(cal.get(Calendar.MINUTE)) + ":00.000Z";
			HLog.i(TAG,"set timer :"+HomekitFormate.getLocal(at).toString());
			
			if (HomekitFormate.getLocal(at).before(new Date())) {
				Toast.makeText(this, getString(R.string.timer_set_err), Toast.LENGTH_SHORT).show();
				return;
			}

		} else {
			//
			at = mTimePicker.getCurrentMinute() + " "
					+ HomekitFormate.toUTCHour(mTimePicker.getCurrentHour())
					+ " " + "* " + "* " + timer;
		}

		Timer newTimer = new Timer();
		newTimer.at = at;
		newTimer.typ = type;
		newTimer.enable = true;
		newTimer.deviceId = mDeviceEntity.mDeviceId;
		newTimer.mId = UUID.randomUUID() + "";

		newTimer.doAction = helper.getTimerParams();
		// mTimerList.clear();

		mTimerList.add(newTimer);
		try {
			DbManager.getInstance(this).inSert(newTimer);
			mHandler.sendEmptyMessage(0);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		submitTimerList(true);

	}

	public String getIntString(int i) {
		return (i < 10) ? ("0" + i) : ("" + i);
	}

	private void setRepe() {
		// mSwitchHelper.swtichState();

		final JSONObject json = new JSONObject();
		try {

			json.put("action", "update");
			json.put("apikey", mDeviceEntity.mApiKey);
			json.put("deviceid", mDeviceEntity.mDeviceId);

			JSONArray array = new JSONArray();

			JSONObject aTimer = new JSONObject();
			aTimer.put("enabled", true);
			aTimer.put("type", "repeat");
			aTimer.put("at", "2015-07-14T03:04:00.000Z");
			JSONObject parm = new JSONObject();
			parm.put("switch", "on");
			parm.put("outlet", 1);
			HLog.i(TAG, "parm:" + parm);

			aTimer.put("do", parm);

			array.put(aTimer);
			JSONObject timers = new JSONObject();
			timers.put("timers", array);

			Helper.addSelfKey(json, app.mUser.apikey);
			json.put("params", timers);
			json.put("userAgent", "app");
			this.postWsRequest(new WsRequest(json) {

			});
			// json.p
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {

		return super.onTouchEvent(arg1);
	}

}

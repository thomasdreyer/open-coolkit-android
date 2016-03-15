package elink.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;
import com.coolkit.common.WsRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import elink.common.Helper;
import elink.common.UiHelper;
import elink.entity.DeviceEntity;
import elink.entity.Timer;
import elink.model.DbManager;
import elink.utils.DialogHelper;
import elink.utils.HomekitFormate;
import elink.utils.NetHelper;


/**
 * Created by app on 15/9/24.
 */
public class AddTimerActivity extends BasicActivity implements View.OnClickListener {
    private static final String TAG = AddTimerActivity.class.getSimpleName();
    public static final int TYPE_SET_DETAIL = 0;
    public static final int TYPE_SET_QUICK = 1;
    public static final int TYPE_SET_REPEAT = 2;
    public static final int TYPE_SET_CONTINUE = 3;

    private static final int MSG_CLOSE_DIALOG = 0;
    private static final int MSG_FINISH = 1;
    private static final int ERROR_BACK = MSG_FINISH+1;

    private View lay_set;
    private TextView mTvOne;
    private TextView mTvTwo;
    private TextView mTvThree;
    private TextView mTvFour;
    private TextView mTvFive;
    private TextView mTvSix;
    private TextView mTvSeven;
    Button btn_finish;
    private boolean isOpen;
    int hour;
    int minus;

    ContinueSetPopupWindow mContinueSetPopupWindow;
    OnceTypeDetailSetPopupWindow mOnceTypeDetailSetPopupWindow;
    OnceTypeQuickDelaySetPopupWindow mOnceTypeQuickDelaySetPopupWindow;
    private CheckBox cb_open, cb_down;
    private CheckBox cb_sing;
    private CheckBox cb_ret;
    private TextView mTvTime;
    private String at;

    private RepeatTimePopupWindows mRepeatTypePopupWindow;
    private ViewGroup mViewParent;
    public Timer mTimer;
    public DatePicker mDetailDatePick;
    public TimePicker mDetailTimerPick;
    public int onceHistory = 0;
    public TimePicker mRepeatePicker;
    public Date mDate;
    public final static int DEFAULT_DELAY_TIME = 3;
    private View mVSelectDay;
    private TextView mRdOne;
    private TextView mRdTwo;
    private TextView mRdThree;
    private TextView mRdFour;
    private View road_select;
    private DetailSwitchActivity<Activity> mHelper;
    private String[] arraydate;
    private String months = "";
    public NumberPicker mDayPicker;
    public NumberPicker mHourPicker;
    public NumberPicker mQuikTimerPicker;
    private String mDeviceId;
    private int mSwitchSize = -1;
    private int mOutlet = -1;
    private boolean isEdit;
    private Dialog mSubmitting;
    private List<Timer> mTimerList;
    private CheckBox cb_cont;
    private View continue_start;
    private View continue_end;
    private View sing_repeat_open;
    private View sing_repeat_close;
    private View select_week;
    private View ll_continue_time;
    private TextView tv_start_time;
    private TextView tv_interval_time;
    private TextView tv_continue_time;
    private CheckBox cb_end_open;
    private CheckBox cb_end_close;
    private CheckBox cb_start_open;
    private CheckBox cb_start_close;
    public Date mContinueData;
    private int continuehour;
    private int continueminus;
    public static final int TYPE_INTERVAL_QUICKE = TYPE_SET_CONTINUE+1;
    private static final int TYPE_CONTINUE_QUICKE = TYPE_SET_CONTINUE+2;
    public IntervalQuickSetPopupWindow mIntervalQuickSetPopupWindow;
    private KeepTimeSetPopupWindow mKeepTimeSetPopupWindow;
    public Date mIntervaltimeData;
    public int mDay = 0;
    public int mHour = 0;
    public int mMinutes = 0;

    public Date mKeepTimeDate;
    public int mKeepDay = 0;
    public int mKeepHour =0;
    public int mKeepMinutes =0;
    public int interval_total_time;
    public int keep_total_time;
    public boolean continueOpen;
    public boolean continueStopisOpen;
    private int timertype;
    private TextView mTvOpen;
    private TextView mTvClose;
    private boolean isFirePlace;
    private View mLayoutContinueBtn;

    @Override
    protected void setView() {
        mTitle = getString(R.string.timers);
        super.setView();
        if(isFirePlace)
        {
            mTvOpen.setText(getString(R.string.fireplace_power_on));
            mTvClose.setText(getString(R.string.fireplace_power_off));

        }
        mLayoutContinueBtn.setVisibility(View.GONE);

    }


    public void initData() {
        mDeviceId = this.getIntent().getStringExtra("Extra_device_id");
        String timerId = this.getIntent().getStringExtra("Extra_timer_id");
        mOutlet = this.getIntent().getIntExtra("Extra_outlet", -1);
        HLog.i(TAG,"single outlet:"+mOutlet);
        mSwitchSize = this.getIntent().getIntExtra("Extra_switch_size", -1);
        isFirePlace=this.getIntent().getBooleanExtra("Extra_IS_FIRE_PLACE", false);

        HLog.i(TAG, "init data :" + timerId+"switchsize:"+mSwitchSize);

        isEdit = null != timerId;
        if (isEdit) {
            mTimer = app.mDbManager.queryTimerById(timerId);
            if(mTimer!=null){
                try {
                    if(mTimer.typ.equals("duration")){
                        JSONObject startopen = new JSONObject(mTimer.startDo);
                        JSONObject endopen = new JSONObject(mTimer.endDo);
                        continueOpen = "on".equals(startopen.getString("switch"));
                        continueStopisOpen = "on".endsWith(endopen.getString("switch"));
                        mOutlet = startopen.has("outlet")?startopen.getInt("outlet"):-1;
                        HLog.i(TAG,"timer_at:"+mTimer.at);
                        mContinueData = HomekitFormate.getLocal(mTimer.at);
                        String[] oldTime = mTimer.at.split(" ");
                        HLog.i(TAG,"intervaltime"+oldTime[1]);
                        HLog.i(TAG,"keeptime"+oldTime[2]);
                        mDay = Integer.parseInt(oldTime[1])/60/24;
                        mHour = Integer.parseInt(oldTime[1])/60%24;
                        mMinutes = Integer.parseInt(oldTime[1])%60;
                        mKeepDay = Integer.parseInt(oldTime[2])/60/24;
                        mKeepHour = Integer.parseInt(oldTime[2])/60%24;
                        mKeepMinutes = Integer.parseInt(oldTime[2])%60;

                        type = TYPE_SET_CONTINUE;
                    }else{
                        JSONObject jsonObject = new JSONObject(mTimer.doAction);
                        isOpen = "on".equals(jsonObject.getString("switch"));
                        mOutlet = jsonObject.has("outlet") ? jsonObject.getInt("outlet") : -1;
                        if(mTimer.typ.equals("once")){//单次
                            mDate = HomekitFormate.getLocal(mTimer.at);
                        }else{//持续
                            mDate = new Date(new Date().getTime() + DEFAULT_DELAY_TIME * 60 * 1000);
                            type = TYPE_SET_REPEAT;
                            String[] arrays = mTimer.at.split(" ");
                            HLog.i(TAG, "timer at:" + mTimer.at.toString());
                            if (arrays != null && arrays.length == 5) {
                                hour = Integer.parseInt(getStr(HomekitFormate.toLocalHour(Integer.parseInt(arrays[1]))));
                                minus = Integer.parseInt(getStr(Integer.parseInt(arrays[0])));
                                HLog.i(TAG, "Timer hour:" + hour + "Timer minus:" + minus);
                            }
                        }
                    }
                } catch (JSONException e) {
                    HLog.e(TAG, e);
                }
            }else{
                UiHelper.showShortToast(this,getString(R.string.data_exception));
            }

        } else {
            mTimer = new Timer();
            mTimer.enable = true;
            mTimer.deviceId = mDeviceId;
            mTimer.mId = UUID.randomUUID() + "";
            mTimer.typ = "once";
            mDate = new Date(new Date().getTime() + DEFAULT_DELAY_TIME * 60 * 1000);
            HLog.i(TAG, "timer is null:" + mDate.toString());
            type = 0;
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        if(mTimer!=null){
            initView();
            setView();
        }else{
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mSubmitting && mSubmitting.isShowing()) {
            mSubmitting.dismiss();
        }
    }

    public String getStr(int input) {
        if (input < 10) return "0" + input;
        else return input + "";
    }

    public void initView() {
        setContentView(R.layout.activity_set_new_timer_add);
        lay_set = findViewById(R.id.lay_set);
        mVSelectDay = findViewById(R.id.lay_recovset);
        cb_open = (CheckBox) findViewById(R.id.cb_open);
        cb_down = (CheckBox) findViewById(R.id.cb_down);
        cb_sing = (CheckBox) findViewById(R.id.cb_sing);
        cb_ret = (CheckBox) findViewById(R.id.cb_ret);
        cb_cont = (CheckBox) findViewById(R.id.cb_cont);
        mTvOpen=(TextView)findViewById(R.id.text_open);
        mTvClose=(TextView)findViewById(R.id.text_down);
        mTvTime = (TextView) findViewById(R.id.info_time_date);

        continue_start = findViewById(R.id.continue_start);
        continue_end = findViewById(R.id.continue_end);
        sing_repeat_open = findViewById(R.id.sing_repeat_open);
        sing_repeat_close = findViewById(R.id.sing_repeat_close);
        select_week = findViewById(R.id.select_week);
        ll_continue_time = findViewById(R.id.ll_continue_time);
        cb_end_open = (CheckBox) findViewById(R.id.end_cb_open);
        cb_end_close = (CheckBox)findViewById(R.id.end_cb_close);
        cb_start_open = (CheckBox)findViewById(R.id.start_cb_open);
        cb_start_close = (CheckBox)findViewById(R.id.start_cb_close);
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_interval_time = (TextView) findViewById(R.id.tv_jiange_time);
        tv_continue_time = (TextView) findViewById(R.id.tv_chixu_time);
        mLayoutContinueBtn=findViewById(R.id.laycontinuetime);

        //添加的持续定时器相关按钮
        cb_end_open.setOnClickListener(this);
        cb_end_close.setOnClickListener(this);
        cb_start_open.setOnClickListener(this);
        cb_start_close.setOnClickListener(this);
        tv_start_time.setOnClickListener(this);
        tv_interval_time.setOnClickListener(this);
        tv_continue_time.setOnClickListener(this);


        HLog.i(TAG,"initview switchsize:"+mSwitchSize);

        if (mSwitchSize > 1) {

            road_select = findViewById(R.id.road_select);
            road_select.setVisibility(View.VISIBLE);
            mRdOne = (TextView) findViewById(R.id.rd_one);
            mRdTwo = (TextView) findViewById(R.id.rd_two);
            mRdThree = (TextView) findViewById(R.id.rd_three);
            mRdFour = (TextView) findViewById(R.id.rd_four);


            if (mSwitchSize == 2) {
                mRdThree.setVisibility(View.GONE);
                mRdFour.setVisibility(View.GONE);
            }
            if (mSwitchSize == 3) {
                mRdFour.setVisibility(View.GONE);
            }
            mRdOne.setTag(0);
            mRdTwo.setTag(1);
            mRdThree.setTag(2);
            mRdFour.setTag(3);
            if (!isEdit) {
                setRoad(mRdOne);
            }
            mRdOne.setOnClickListener(this);
            mRdTwo.setOnClickListener(this);
            mRdThree.setOnClickListener(this);
            mRdFour.setOnClickListener(this);

            showRoad();

        }

        if (getResources().getConfiguration().locale.getLanguage().endsWith("zh")) {

            mTvOne = (TextView) findViewById(R.id.tv_one);
            mTvTwo = (TextView) findViewById(R.id.tv_two);
            mTvThree = (TextView) findViewById(R.id.tv_three);
            mTvFour = (TextView) findViewById(R.id.tv_four);
            mTvFive = (TextView) findViewById(R.id.tv_five);
            mTvSix = (TextView) findViewById(R.id.tv_six);
            mTvSeven = (TextView) findViewById(R.id.tv_seven);
        } else {

            findViewById(R.id.week_detail_cn).setVisibility(View.GONE);
            findViewById(R.id.week_detail_en).setVisibility(View.VISIBLE);
            mTvOne = (TextView) findViewById(R.id.tv_one_en);
            mTvTwo = (TextView) findViewById(R.id.tv_two_en);
            mTvThree = (TextView) findViewById(R.id.tv_three_en);
            mTvFour = (TextView) findViewById(R.id.tv_four_en);
            mTvFive = (TextView) findViewById(R.id.tv_five_en);
            mTvSix = (TextView) findViewById(R.id.tv_six_en);
            mTvSeven = (TextView) findViewById(R.id.tv_seven_en);
        }

        btn_finish = (Button) findViewById(R.id.btn_finish);

        cb_sing.setOnClickListener(this);
        cb_ret.setOnClickListener(this);
        cb_cont.setOnClickListener(this);
        cb_open.setOnClickListener(this);
        cb_down.setOnClickListener(this);

        mTvOne.setOnClickListener(this);
        mTvTwo.setOnClickListener(this);
        mTvThree.setOnClickListener(this);
        mTvFour.setOnClickListener(this);
        mTvFive.setOnClickListener(this);
        mTvSix.setOnClickListener(this);
        mTvSeven.setOnClickListener(this);
        lay_set.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        mTvTime.setOnClickListener(this);
        if (type == TYPE_SET_REPEAT) {
            cb_ret.setChecked(true);
            cb_sing.setChecked(false);
            cb_cont.setChecked(false);
            HLog.i(TAG,"type is repeat");
            mVSelectDay.setVisibility(View.VISIBLE);
            String[] arrays = mTimer.at.split(" ");
            HLog.i(TAG,"arrays size:"+arrays.length+",==== "+arrays.toString());
            if (arrays != null && arrays.length == 5) {
                hour = Integer.parseInt(getStr(HomekitFormate.toLocalHour(Integer.parseInt(arrays[1]))));
                minus = Integer.parseInt(getStr(Integer.parseInt(arrays[0])));
                if (arrays[4].contains("0")) {
                    mTvSeven.setTag(false);
                    setWeek(mTvSeven);
                }
                if (arrays[4].contains("1")) {
                    mTvOne.setTag(false);
                    setWeek(mTvOne);

                }
                if (arrays[4].contains("2")) {
                    mTvTwo.setTag(false);
                    setWeek(mTvTwo);
                }
                if (arrays[4].contains("3")) {
                    mTvThree.setTag(false);
                    setWeek(mTvThree);
                }
                if (arrays[4].contains("4")) {
                    mTvFour.setTag(false);
                    setWeek(mTvFour);
                }
                if (arrays[4].contains("5")) {
                    mTvFive.setTag(false);
                    setWeek(mTvFive);
                }
                if (arrays[4].contains("6")) {
                    mTvSix.setTag(false);
                    setWeek(mTvSix);
                }
            }
            cb_open.setChecked(isOpen);
            cb_down.setChecked(!isOpen);
        } else if(type == TYPE_SET_DETAIL){
            cb_ret.setChecked(false);
            cb_sing.setChecked(true);
            cb_cont.setChecked(false);
            mVSelectDay.setVisibility(View.GONE);
            cb_open.setChecked(isOpen);
            cb_down.setChecked(!isOpen);
        } else if(type == TYPE_SET_CONTINUE){
            cb_ret.setChecked(false);
            cb_sing.setChecked(false);
            cb_cont.setChecked(true);
            mVSelectDay.setVisibility(View.VISIBLE);
            lay_set.setVisibility(View.GONE);
            select_week.setVisibility(View.GONE);
            ll_continue_time.setVisibility(View.VISIBLE);
            sing_repeat_open.setVisibility(View.GONE);
            sing_repeat_close.setVisibility(View.GONE);
            continue_start.setVisibility(View.VISIBLE);
            cb_start_open.setChecked(continueOpen);
            cb_start_close.setChecked(!continueOpen);
            cb_end_open.setChecked(continueStopisOpen);
            cb_end_close.setChecked(!continueStopisOpen);
            continue_end.setVisibility(View.VISIBLE);
        }

        showTVDate();
    }

    private void showRoad() {
        mRdOne.setBackgroundResource(R.drawable.round_gray);
        mRdTwo.setBackgroundResource(R.drawable.round_gray);
        mRdThree.setBackgroundResource(R.drawable.round_gray);
        mRdFour.setBackgroundResource(R.drawable.round_gray);
        if (mOutlet == 0) {
            mRdOne.setBackgroundResource(R.drawable.oneroad);
        } else if (mOutlet == 1) {
            mRdTwo.setBackgroundResource(R.drawable.tworoad);
        } else if (mOutlet == 2) {
            mRdThree.setBackgroundResource(R.drawable.threeroad);

        } else if (mOutlet == 3) {
            mRdFour.setBackgroundResource(R.drawable.fourroad);
//
        }

    }


    public void showTVDate() {

        if (type == TYPE_SET_REPEAT) {
            mTvTime.setText(getIntString(hour) + ":" + getIntString(minus));
        } else if(type == TYPE_SET_DETAIL){
            SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            mTvTime.setText(yearFormatter.format(mDate));

        } else if(type == TYPE_SET_CONTINUE) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            tv_start_time.setText(format.format(mContinueData));
            tv_interval_time.setText(mDay + "天-" + mHour + "小时-" + mMinutes + "分钟");
            interval_total_time = mDay*24*60+mHour*60+mMinutes;
            tv_continue_time.setText(mKeepDay+"天-"+mKeepHour+"小时-"+mKeepMinutes+"分钟");
            keep_total_time = mKeepDay*24*60+mKeepHour*60+mKeepMinutes;
        }


    }


    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.info_time_date:
                setTimerpop();
                break;
            case R.id.cb_open:
                isOpen = true;
                cb_down.setChecked(false);
                cb_open.setChecked(true);
                break;
            case R.id.cb_down:
                isOpen = false;
                cb_open.setChecked(false);
                cb_down.setChecked(true);
                break;
            case R.id.cb_sing:
                type = 0;   //0
                cb_ret.setChecked(false);
                cb_sing.setChecked(true);
                cb_cont.setChecked(false);
                lay_set.setVisibility(View.VISIBLE);
                mVSelectDay.setVisibility(View.GONE);

                sing_repeat_close.setVisibility(View.VISIBLE);
                sing_repeat_open.setVisibility(View.VISIBLE);
                continue_start.setVisibility(View.GONE);
                continue_end.setVisibility(View.GONE);


                showTVDate();
                break;
            case R.id.cb_ret:
                if (type != 2) {
                    onceHistory = type;
                }
                type = 2;
                cb_sing.setChecked(false);
                cb_ret.setChecked(true);
                cb_cont.setChecked(false);
                lay_set.setVisibility(View.VISIBLE);
                mVSelectDay.setVisibility(View.VISIBLE);


                sing_repeat_close.setVisibility(View.VISIBLE);
                sing_repeat_open.setVisibility(View.VISIBLE);
                continue_start.setVisibility(View.GONE);
                continue_end.setVisibility(View.GONE);
                select_week.setVisibility(View.VISIBLE);
                ll_continue_time.setVisibility(View.GONE);


                if (hour == 0) {
                    mDate = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(mDate);
                    hour = cal.get(Calendar.HOUR_OF_DAY);
                    minus = cal.get(Calendar.MINUTE);

                }
                showTVDate();
                break;
            case R.id.cb_cont:
                type = TYPE_SET_CONTINUE;
                cb_sing.setChecked(false);
                cb_ret.setChecked(false);
                cb_cont.setChecked(true);
                lay_set.setVisibility(View.GONE);
                mVSelectDay.setVisibility(View.VISIBLE);
                if(continuehour==0){
                    mContinueData = new Date(new Date().getTime() + DEFAULT_DELAY_TIME * 60 * 1000);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(mContinueData);
                    continuehour = cal.get(Calendar.HOUR_OF_DAY);
                    continueminus = cal.get(Calendar.MINUTE);
                }
                showTVDate();

                sing_repeat_close.setVisibility(View.GONE);
                sing_repeat_open.setVisibility(View.GONE);
                continue_start.setVisibility(View.VISIBLE);
                continue_end.setVisibility(View.VISIBLE);
                select_week.setVisibility(View.GONE);
                ll_continue_time.setVisibility(View.VISIBLE);
                cb_start_open.setChecked(continueOpen);
                cb_start_close.setChecked(!continueOpen);
                cb_end_open.setChecked(continueStopisOpen);
                cb_end_close.setChecked(!continueStopisOpen);


                break;
            case R.id.rd_one:
                setRoad((TextView) arg0);
                break;
            case R.id.rd_two:
                setRoad((TextView) arg0);
                break;
            case R.id.rd_three:
                setRoad((TextView) arg0);
                break;
            case R.id.rd_four:
                setRoad((TextView) arg0);
                break;
            case R.id.tv_one:
            case R.id.tv_one_en:

                setWeek((TextView) arg0);
                break;
            case R.id.tv_two:
            case R.id.tv_two_en:
                setWeek((TextView) arg0);
                break;
            case R.id.tv_three:
            case R.id.tv_three_en:
                setWeek((TextView) arg0);
                break;
            case R.id.tv_four:
            case R.id.tv_four_en:
                setWeek((TextView) arg0);
                break;
            case R.id.tv_five:
            case R.id.tv_five_en:
                setWeek((TextView) arg0);
                break;
            case R.id.tv_six:
            case R.id.tv_six_en:
                setWeek((TextView) arg0);
                break;
            case R.id.tv_seven:
            case R.id.tv_seven_en:
                setWeek((TextView) arg0);
                break;
            case R.id.btn_finish:
                submitTimer();
                break;

            //持续定时相关按钮
            case R.id.start_cb_open:
                continueOpen = true;
                cb_start_close.setChecked(false);
                cb_start_open.setChecked(true);
                break;
            case R.id.start_cb_close:
                continueOpen = false;
                cb_start_close.setChecked(true);
                cb_start_open.setChecked(false);
                break;
            case R.id.end_cb_open:
                continueStopisOpen = true;
                cb_end_open.setChecked(true);
                cb_end_close.setChecked(false);
                break;
            case R.id.end_cb_close:
                continueStopisOpen = false;
                cb_end_open.setChecked(false);
                cb_end_close.setChecked(true);
                break;
            case R.id.tv_start_time:
                timertype = TYPE_SET_CONTINUE;
                setTimerpop();
                break;
            case R.id.tv_jiange_time:
                timertype = TYPE_INTERVAL_QUICKE;
                setTimerpop();
                break;
            case R.id.tv_chixu_time:
                timertype = TYPE_CONTINUE_QUICKE;
                setTimerpop();
                break;
            default:
                break;
        }

    }


    public void setWeek(TextView view) {
        Boolean selected = view.getTag() == null ? false : (Boolean) view.getTag();
        if (selected) {
            view.setBackgroundResource(R.drawable.round_gray);

        } else {
            view.setBackgroundResource(R.drawable.round_blue);
        }
        selected = !selected;
        view.setTag(selected);
    }


    public void setRoad(TextView view) {
        mRdOne.setBackgroundResource(R.drawable.round_gray);
        mRdTwo.setBackgroundResource(R.drawable.round_gray);
        mRdThree.setBackgroundResource(R.drawable.round_gray);
        mRdFour.setBackgroundResource(R.drawable.round_gray);
        mOutlet = (Integer) view.getTag();
        if ((Integer) view.getTag() == 0) {
            view.setBackgroundResource(R.drawable.oneroad);
        } else if ((Integer) view.getTag() == 1) {
            view.setBackgroundResource(R.drawable.tworoad);
        } else if ((Integer) view.getTag() == 2) {
            view.setBackgroundResource(R.drawable.threeroad);
        } else if ((Integer) view.getTag() == 3) {
            view.setBackgroundResource(R.drawable.fourroad);
        }
        HLog.i(TAG, "set road :" + mOutlet);
    }


    private boolean getSelectString(TextView view) {
        return (view.getTag() == null) ? false : (Boolean) view.getTag();
    }


    private String getIntString(int i) {
        return (i < 10) ? ("0" + i) : ("" + i);
    }


    public void submitTimer() {

        HLog.i(TAG,"time.typ:"+mTimer.typ+",intervaltimer:"+interval_total_time+",keeptimer"+keep_total_time);

        if (!NetHelper.isDetailConnnected(getApplicationContext())) {
            UiHelper.showShortToast(getApplicationContext(), getString(R.string.not_intent_word));
            return;
        }
        if(type == TYPE_SET_CONTINUE){
            HLog.i(TAG,"set typ  continue");

            if(interval_total_time == 0 ){
                UiHelper.showShortToast(this, getString(R.string.interval_not));
                return;
            }

            if(keep_total_time == 0){
                UiHelper.showShortToast(this,getString(R.string.keep_not));
                return;
            }
            //持续
            if(keep_total_time>=interval_total_time){
                UiHelper.showShortToast(this, getString(R.string.keep_smaller_interval));
                return;
            }
            mTimer.typ = "duration";
            if (mContinueData.before(new Date())) {
                Toast.makeText(this, R.string.equesdate, Toast.LENGTH_SHORT).show();
                return;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(mContinueData);
            int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
            // 3、取得夏令时差：
            int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
            // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
            cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
            at = cal.get(Calendar.YEAR) + "-"
                    + getIntString(cal.get(Calendar.MONTH) + 1) + "-"
                    + getIntString(cal.get(Calendar.DAY_OF_MONTH)) + "T"
                    + getIntString(cal.get(Calendar.HOUR_OF_DAY)) + ":"
                    + getIntString(cal.get(Calendar.MINUTE)) + ":00.000Z"+" "+interval_total_time+" "+keep_total_time;
        }
        else if (type == TYPE_SET_DETAIL||type==TYPE_SET_QUICK) {  //单次
            if (mDate.before(new Date())) {
                Toast.makeText(this, R.string.equesdate, Toast.LENGTH_SHORT).show();
                HLog.i(TAG, "setting date is:" + HLog.formate.format(mDate) + " currrent is:" + HLog.formate.format(new Date()));
                return;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(mDate);
            int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
            // 3、取得夏令时差：
            int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
            // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
            cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
            at = cal.get(Calendar.YEAR) + "-"
                    + getIntString(cal.get(Calendar.MONTH) + 1) + "-"
                    + getIntString(cal.get(Calendar.DAY_OF_MONTH)) + "T"
                    + getIntString(cal.get(Calendar.HOUR_OF_DAY)) + ":"
                    + getIntString(cal.get(Calendar.MINUTE)) + ":00.000Z";
            mTimer.typ = "once";
        } else {//重复
            mTimer.typ = "repeat";
            String timer;
            timer = (getSelectString(mTvSeven) ? "0," : "")
                    + (getSelectString(mTvOne) ? "1," : "")
                    + (getSelectString(mTvTwo) ? "2," : "")
                    + (getSelectString(mTvThree) ? "3," : "")
                    + (getSelectString(mTvFour) ? "4," : "")
                    + (getSelectString(mTvFive) ? "5," : "")
                    + (getSelectString(mTvSix) ? "6," : "");
            if (timer.endsWith(",")) {
                timer = timer.substring(0, timer.length() - 1);
            }
            if (timer.equals("")) {
                Toast.makeText(this, R.string.select_date, Toast.LENGTH_SHORT).show();
                at = null;
                return;
            } else {
                at = minus + " " + HomekitFormate.toUTCHour(hour) + " " + "* " + "* " + timer;
            }
        }


        mTimer.at = at;
        HLog.i(TAG, "at:" + at);
        JSONObject parm3 = new JSONObject();
        JSONObject startDo = new JSONObject();
        JSONObject endDo = new JSONObject();

        if(mTimer.typ .equals("duration")){
            if(0==interval_total_time){
                UiHelper.showShortToast(this,"keep_interval_not");
                return;
            }
            try {
                startDo.put("switch",continueOpen?"on":"off");
                endDo.put("switch",continueStopisOpen?"on":"off");
                if(mOutlet != -1){
                    startDo.put("outlet", mOutlet);
                    endDo.put("outlet", mOutlet);
                }
            } catch (JSONException e) {
                HLog.e(TAG, e);
            }
            HLog.i(TAG,"start do is:"+startDo.toString()+",endDo is:"+ endDo.toString());
            mTimer.startDo = startDo.toString();
            mTimer.endDo = endDo.toString();

        }else {
            try {
                parm3.put("switch", isOpen ? "on" : "off");
                HLog.i(TAG, " m out let is :" + mOutlet);
                if (mOutlet != -1) {
                    parm3.put("outlet", mOutlet);
                }
            } catch (JSONException e) {
                HLog.e(TAG, e);
            }
            HLog.i(TAG, "singAndrepeat parm3 : " + parm3.toString());
            mTimer.doAction = parm3.toString();
        }
        submitTimerList(mTimer);


    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CLOSE_DIALOG:
                    mSubmitting.dismiss();
                    break;
                case MSG_FINISH:
                    finish();
                    break;
                case ERROR_BACK:
                    UiHelper.showShortToast(getApplicationContext(),getString(R.string.try_again_later));
                    mSubmitting.dismiss();
                    break;

            }
        }
    };

    /**
     * 0==add timer
     * 1==edit timer
     * 2=delete timer
     */
    public void submitTimerList(final Timer timer) {
        HLog.i(TAG,"doaction:"+timer.doAction+"startdo:"+timer.startDo+"enddo:"+timer.endDo);
        //timer.mId
        final JSONObject json = new JSONObject();
        DeviceEntity temp = DbManager.getInstance(this).queryDeviceyByDeviceId(mDeviceId);
        if (null == temp) {
            Toast.makeText(this, R.string.dataerr, Toast.LENGTH_SHORT).show();

            return;
        } else if (temp.mOnLine.equals("false")) {
            Toast.makeText(this, R.string.dev_offline, Toast.LENGTH_SHORT).show();

        } else {
            try {
                mTimerList = DbManager.getInstance(this)
                        .queryTimerByDeviceId(mDeviceId);


                if (null == mTimerList) {
                    mTimerList = new ArrayList<>();

                }
                if (!isEdit) {
                    HLog.i(TAG, "insert mTimer");
                    mTimerList.add(mTimer);
//                    mTimerList.add(timer);
                }

                json.put("action", "update");

                json.put("apikey", temp.mApiKey);
                json.put("deviceid", mDeviceId);
                JSONArray array = new JSONArray();
                for (Timer aTimer : mTimerList) {
                    HLog.i(TAG,"id:"+aTimer.mId+",type:"+aTimer.typ+",at:"+aTimer.at+",startDo:"+aTimer.startDo+",endDo:"+aTimer.endDo);
                    JSONObject aTimer0 = new JSONObject();
                    if (!aTimer.mId.equals(mTimer.mId)) {
//                    if (!aTimer.mId.equals(timer.mId)) {
                        if(aTimer.typ.equals("duration")){ //持续定时
                            aTimer0.put("enabled", 1);
                            aTimer0.put("type", aTimer.typ);
                            aTimer0.put("at", aTimer.at);
//                            JSONObject startDo = new JSONObject(aTimer.startDo);
//                            JSONObject endDo = new JSONObject(aTimer.endDo);

                            JSONObject startDo = new JSONObject(aTimer.startDo);
                            JSONObject endDo = new JSONObject(aTimer.endDo);
                            aTimer0.put("startDo",startDo);
                            aTimer0.put("endDo", endDo);
                        }else{
                            aTimer0.put("enabled", 1);
                            aTimer0.put("type", aTimer.typ);
                            aTimer0.put("at", aTimer.at);
                            HLog.i(TAG,"for do action:"+aTimer.doAction);
                            aTimer0.put("do", new JSONObject(aTimer.doAction));

                        }

                        array.put(aTimer0);
                    } else {

                        if(aTimer.typ.equals("duration")){ //持续定时
                            aTimer0.put("enabled", 1);
                            aTimer0.put("type", mTimer.typ);
                            aTimer0.put("at", mTimer.at);
                            JSONObject startDo = new JSONObject(mTimer.startDo);
                            JSONObject endDo = new JSONObject(mTimer.endDo);
                            aTimer0.put("startDo",startDo);
                            aTimer0.put("endDo", endDo);
                        }else{
                            aTimer0.put("enabled", 1);
                            aTimer0.put("type", mTimer.typ);
                            aTimer0.put("at", mTimer.at);
                            HLog.i(TAG, "for do action2:" + mTimer.doAction);
                            aTimer0.put("do", new JSONObject(mTimer.doAction));
                        }
//                        aTimer0.put("enabled", 1);
//                        aTimer0.put("type", mTimer.typ);
//                        aTimer0.put("at", mTimer.at);
//                        aTimer0.put("do", new JSONObject(mTimer.doAction));//
                        array.put(aTimer0);
                    }
                }
                HLog.i(TAG, "array" + array.toString());
                JSONObject timers = new JSONObject();
                timers.put("timers", array);
                json.put("params", timers);
                json.put("userAgent", "app");

                json.put("sequence", System.currentTimeMillis() + "");
                Helper.addSelfKey(json, app.mUser.apikey);

                if (!isEdit) {
                    mSubmitting = DialogHelper.createProgressDialog(this, getString(R.string.committime));
                } else {
                    mSubmitting = DialogHelper.createProgressDialog(this, getString(R.string.edit_dialog));
                }
                mSubmitting.show();


                postWsRequest(new WsRequest(json) {
                    @Override
                    public void callback(String msg) {
                        HLog.i(TAG, "callback msg:" + msg);
                        handler.sendEmptyMessage(MSG_CLOSE_DIALOG);
                        if (!TextUtils.isEmpty(msg)) {
                            try {
                                JSONObject obj = new JSONObject(msg);
                                HLog.i(TAG, "obj.getInt(error):" + obj.getInt("error"));
                                if (obj.has("error") && (0 == obj.getInt("error"))) {

                                    try {
                                        if (!isEdit) {
                                            Toast.makeText(AddTimerActivity.this,
                                                    R.string.add_timers_success, Toast.LENGTH_SHORT)
                                                    .show();
                                            app.mDbManager.inSert(timer);
                                        } else {
                                            Toast.makeText(AddTimerActivity.this,
                                                    R.string.update_timers_ok, Toast.LENGTH_SHORT)
                                                    .show();
                                            app.mDbManager.updateObject(timer, timer.mId/*mDeviceEntity.mDeviceId*/);


                                        }

                                        Helper.broadcastSynLocalDevice(AddTimerActivity.this);

                                    } catch (Exception e) {
                                        HLog.e(TAG, e);
                                    }
                                    handler.sendEmptyMessage(MSG_FINISH);

                                } else if (obj.has("error") && (504 == obj.getInt("error"))) {
                                    HLog.i(TAG, "obj.getInt(error)1:" + obj.getInt("error"));
                                    Toast.makeText(AddTimerActivity.this, R.string.outtimer, Toast.LENGTH_SHORT).show();

                                    return;
                                } else if (obj.has("error") && (503 == obj.getInt("error"))) {//503 设备未在线
                                    HLog.i(TAG, "obj.getInt(error)2:" + obj.getInt("error"));

                                    Toast.makeText(AddTimerActivity.this,
                                            getString(R.string.device_not_online), Toast.LENGTH_SHORT).show();

                                    return;
                                }

                            } catch (JSONException e) {
                                HLog.e(TAG, e);

                            }

                        } else {
                            HLog.i(TAG, "callback err");
                            handler.sendEmptyMessage(ERROR_BACK);
                        }

                    }
                });

            } catch (JSONException e) {
                HLog.e(TAG, e);
            }
        }


    }

    /**
     * 0 means detail once set,1 menas quick once set, 2 means repeat set
     */
    public int type = TYPE_SET_DETAIL;


    protected void setTimerpop() {
        if (type == TYPE_SET_DETAIL) {
            mOnceTypeDetailSetPopupWindow = new OnceTypeDetailSetPopupWindow(this, this);
            mOnceTypeDetailSetPopupWindow.showAtLocation(this.findViewById(R.id.view_time_sets),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        } else if (type == TYPE_SET_QUICK) {
            mOnceTypeQuickDelaySetPopupWindow = new OnceTypeQuickDelaySetPopupWindow(this, this);
            mOnceTypeQuickDelaySetPopupWindow.showAtLocation(this.findViewById(R.id.view_time_sets),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        } else if (type == TYPE_SET_REPEAT) {
            mRepeatTypePopupWindow = new RepeatTimePopupWindows(this, this);
            mRepeatTypePopupWindow.showAtLocation(this.findViewById(R.id.view_time_sets),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        } else if(timertype == TYPE_SET_CONTINUE){ //持续定时开始时间
            mContinueSetPopupWindow = new ContinueSetPopupWindow(this, this);
            mContinueSetPopupWindow.showAtLocation(this.findViewById(R.id.view_time_sets),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        } else if(timertype == TYPE_INTERVAL_QUICKE){ //持续定时间隔时间
            mIntervalQuickSetPopupWindow = new IntervalQuickSetPopupWindow(this,this);
            mIntervalQuickSetPopupWindow.showAtLocation(this.findViewById(R.id.view_time_sets),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        } else if(timertype == TYPE_CONTINUE_QUICKE){ //持续定时保持时间
            mKeepTimeSetPopupWindow = new KeepTimeSetPopupWindow(this, this);
            mKeepTimeSetPopupWindow.showAtLocation(this.findViewById(R.id.view_time_sets),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }

    }


}
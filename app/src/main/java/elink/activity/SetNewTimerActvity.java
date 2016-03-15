package elink.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.Locale;

import elink.DeviceHelper;
import elink.HkConst;
import elink.adapt.BaseListAdapt;
import elink.common.Helper;
import elink.common.UiHelper;
import elink.entity.DeviceEntity;
import elink.entity.Timer;
import elink.model.DbManager;
import elink.utils.DialogHelper;
import elink.utils.HomekitFormate;
import elink.utils.IntentHelper;
import elink.utils.NetHelper;


public class SetNewTimerActvity extends BasicActivity implements View.OnClickListener/*, AdapterView.OnItemClickListener*/ {

    private static final String TAG = SetNewTimerActvity.class.getSimpleName();
    private static final int MSG_CLOSE_DIALOG = 0;
    private static final int MSG_FINISH =MSG_CLOSE_DIALOG+1 ;

    private View layoutTimerList;  //listview 和添加计时器
    private View view_bg_timer;//背景图
    private View view_add_timer; //添加计时器
    private ListView mLvTimer;
    private DeviceEntity mDeviceEntity;
    private List<Timer> mTimerList = new ArrayList<Timer>();
    private TimerListAdapt mAdapt;
    ListTimerHelper mListTimerHelper;

    private View mViewAddingContainer;
    private View mTvNOTimerTip;
    boolean isInList = true;
    public int mOutlet;
    private Dialog mSubmittingDialog;
    private int listviewsize;
    public int mSwitchSize;


    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

                    mTimerList.clear();
            List tem=DbManager.getInstance(SetNewTimerActvity.this)
                    .queryTimerByDeviceId(mDeviceEntity.mDeviceId);
            if(null!=tem){
                HLog.i(TAG, "query list is:" + tem.size());
                mTimerList.addAll(tem);
            }

                     mAdapt.refresh();
                    showbBgView();
        }


    };
    private BroadcastReceiver mReciever=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mHandler.sendEmptyMessage(0);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_timer);
        Intent intent = getIntent();
        mOutlet = intent.getIntExtra("extra_outlet",0);

        HLog.i(TAG, "mOutlet:" + mOutlet);
        initData();
        if(mDeviceEntity!=null){
            initView();
            setView();
            mHandler.sendEmptyMessage(0);
            Helper.broadcastTimerChangeList(SetNewTimerActvity.this);//计时器个数角标

            Helper.broadcastSynLocalDevice(SetNewTimerActvity.this);
            this.registerReceiver(mReciever, new IntentFilter(HkConst.INTENT_SYNC_LOCAL));

        }else {
            HLog.i(TAG, "has no device entity ,finish()");

            finish();
            return;
        }



    }

    private void showbBgView() {
        HLog.i(TAG, "mTimerList.size()" + mTimerList.size());
        if (mTimerList.size() == 0) {
            mLvTimer.setVisibility(View.GONE);
            view_bg_timer.setVisibility(View.VISIBLE);
            mTvNOTimerTip.setVisibility(View.VISIBLE);
            view_add_timer.setVisibility(View.VISIBLE);
        } else {
            layoutTimerList.setVisibility(View.VISIBLE);
            view_bg_timer.setVisibility(View.GONE);
            mTvNOTimerTip.setVisibility(View.GONE);
            view_add_timer.setVisibility(View.VISIBLE);
            mLvTimer.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReciever);
    }


    private void initView() {
        layoutTimerList = findViewById(R.id.layoutTimerList);
        view_add_timer = findViewById(R.id.btn_add_timer); //添加计时器
        mLvTimer = (ListView) findViewById(R.id.lv_timers);//listview
        view_bg_timer = findViewById(R.id.img_bg);//背景图
        mTvNOTimerTip = findViewById(R.id.info_list);
        view_add_timer.setOnClickListener(mListTimerHelper);
        mViewAddingContainer = findViewById(R.id.layout_adding_view_container);//set view
        mLvTimer.setOnItemClickListener(mListTimerHelper);

    }


    private void initData() {
        String devId = getIntent().getStringExtra(HkConst.EXTRA_D_ID);
        mDeviceEntity = DbManager.getInstance(this).queryDeviceyByDeviceId(devId);
        if (null == mDeviceEntity) {
            finish();
            HLog.i(TAG, "has no Device entity ,finish()");
            return;
        }

       mSwitchSize= Helper.parseType(mDeviceEntity.mUi);
        mListTimerHelper = new ListTimerHelper(this);
        mTitle = getString(R.string.timers);
        mAdapt = new TimerListAdapt(mTimerList);//数据填充
        HLog.i(TAG, "hzy mDeviceEntity " + mDeviceEntity);
    }


    class TimerListAdapt extends BaseListAdapt<Timer> {
        public String outlets;
        public String startDo;
        public String endDo;

        public TimerListAdapt(List<Timer> timers) {
            super(timers);
        }

        @Override
        public int getCount() {
            listviewsize=super.getCount();
            HLog.i(TAG, "listviewsizeP:" + listviewsize);
            return super.getCount();
        }

        @Override
        public Object getItem(int arg0) {
            return super.getItem(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return super.getItemId(arg0);
        }

        public String getStr(int input) {
            if (input < 10) return "0" + input;
            else return input + "";
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            if(mData.size()==0){
                return null;
            }
            final Timer data = mData.get(arg0);
            View view = getLayoutInflater().inflate(R.layout.t_new_item, null);//计时器 item
            TextView timer = (TextView) view.findViewById(R.id.tv_time);//时间
            ImageView imgtimer = (ImageView) view.findViewById(R.id.iv_timer);//时间
            TextView roadone = (TextView) view.findViewById(R.id.rd_one);
            TextView report = (TextView) view.findViewById(R.id.tv_repet_type);//周期

            view.setTag(data);
            if(data.typ.equals("duration")){
                startDo = data.startDo;
                endDo = data.endDo;
                HLog.i(TAG,"StartDo:"+startDo+"endDo:"+endDo+"type:"+data.typ+",at:"+data.at);
                String[] arrays = data.at.split(" ");
                SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
                Date date=HomekitFormate.getLocal(arrays[0]);
                Calendar timerCal=Calendar.getInstance();
                timerCal.setTime(date);

                if(Calendar.getInstance().get(Calendar.YEAR)!=timerCal.get(Calendar.YEAR)) {
                    timer.setText(yearFormatter.format(date));//时间显示

                }else{
                    timer.setText(formatter.format(date)+" "+arrays[1]+" "+arrays[2]);
                }
                report.setText(R.string.continuetime); //
//                timer.setText(R.string.continuetime);//时间显示
                try {
                        JSONObject jsonObjectstart = new JSONObject(startDo);
                        JSONObject jsonObjectend = new JSONObject(endDo);
                        String outlects = jsonObjectstart.has("outlet")?jsonObjectstart.getString("outlet"):"-1"; //持续定时通道都是一样
                        String continueStart = jsonObjectstart.getString("switch");
                        int i = Integer.parseInt(outlects)+1;
                        roadone.setText(i+"");
                        if(outlects.equals("-1")){
                            roadone.setVisibility(View.INVISIBLE);
                        }
                        if (outlects.equals("0")) {
                            roadone.setBackgroundResource(R.drawable.oneroad);
                        }
                        if (outlects.equals("1")) {
                            roadone.setBackgroundResource(R.drawable.tworoad);
                        }
                        if (outlects.equals("2")) {
                            roadone.setBackgroundResource(R.drawable.threeroad);
                        }
                        if (outlects.equals("3")) {
                            roadone.setBackgroundResource(R.drawable.fourroad);
                        }

                } catch (JSONException e) {
                    HLog.e(TAG,e);
                }
            }else{
               outlets = data.doAction;
                HLog.i(TAG,"outlets::"+outlets);
                try {
                    JSONObject jsonObject = new JSONObject(outlets);

                    String outlects =jsonObject.has("outlet")? jsonObject.getString("outlet"):"-1";
                    // String outlects = jsonObject.getString("outlet");
                    String isopen = jsonObject.getString("switch");
                    HLog.i(TAG,"getview:outlects:"+outlects+isopen);
                    if(isopen.equals("on")){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            imgtimer.setBackground(getResources().getDrawable(R.drawable.switch_state_opne));
                        } else {
                            imgtimer.setImageDrawable(getResources().getDrawable(R.drawable.switch_state_opne));
                        }

                        // imgtimer.setBackgroundResource();
                        //  imgtimer.setBackground(getResources().getDrawable(R.drawable.switch_state_opne));
                    }else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            imgtimer.setBackground(getResources().getDrawable(R.drawable.switch_state_close));
                        } else {
                            imgtimer.setImageDrawable(getResources().getDrawable(R.drawable.switch_state_close));
                        }
                    }

                    int i = Integer.parseInt(outlects)+1;
                    roadone.setText(i+"");
                    if(outlects.equals("-1")){
                        roadone.setVisibility(View.INVISIBLE);
                    }
                    if (outlects.equals("0")) {
                        roadone.setBackgroundResource(R.drawable.oneroad);
                    }
                    if (outlects.equals("1")) {
                        roadone.setBackgroundResource(R.drawable.tworoad);
                    }
                    if (outlects.equals("2")) {
                        roadone.setBackgroundResource(R.drawable.threeroad);
                    }
                    if (outlects.equals("3")) {
                        roadone.setBackgroundResource(R.drawable.fourroad);
                    }

                } catch (JSONException e) {
                    HLog.e(TAG,e);
                }

                if ("once".equals(data.typ)) {
                    report.setText(R.string.onces);
                    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
                    SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");


                    Date date=HomekitFormate.getLocal(data.at);
                    Calendar timerCal=Calendar.getInstance();
                    timerCal.setTime(date);

                    if(Calendar.getInstance().get(Calendar.YEAR)!=timerCal.get(Calendar.YEAR)){
                        timer.setText(yearFormatter.format(date));//时间显示

                    }else {
                        timer.setText(formatter.format(date));//时间显示

                    }
                } else {
                    String[] arrays = data.at.split(" ");
                    if (arrays != null && arrays.length == 5) {
                        String hour = getStr(HomekitFormate.toLocalHour(Integer.parseInt(arrays[1])));
                        String minus = getStr(Integer.parseInt(arrays[0]));
                        timer.setText(hour + ":" + minus);
                        String repeatype = "";
                        if ("0,1,2,3,4,5,6".equals(arrays[4])) {
                            repeatype = getString(R.string.everydays);
                        } else if ("1,2,3,4,5".equals(arrays[4])) {
                            repeatype = getString(R.string.workdate);
                        } else if ("0,6".equals(arrays[4])) {
                            repeatype = getString(R.string.weekdate);
                        } else {
                            String[] days = arrays[4].split(",");
                            StringBuffer sb = new StringBuffer();
                            if(isZh()){
                                sb.append(getString(R.string.weeks));
                            }else{
                                sb.append("");
                            }
                            if (null != days) {
//                            sb.append(getString(R.string.weeks));
                                for (int i = 0; i < days.length; i++) {
                                    if ("1".equals(days[i])) {
                                        sb.append(getString(R.string.ones) + " ");
                                    } else if ("2".equals(days[i])) {
                                        sb.append(getString(R.string.twos) + " ");
                                    } else if ("3".equals(days[i])) {
                                        sb.append(getString(R.string.threes) + " ");
                                    } else if ("4".equals(days[i])) {
                                        sb.append(getString(R.string.fours) + " ");
                                    } else if ("5".equals(days[i])) {
                                        sb.append(getString(R.string.fives) + " ");
                                    } else if ("6".equals(days[i])) {
                                        sb.append(getString(R.string.sixs) + " ");
                                    } else if ("0".equals(days[i])) {
                                        sb.append(getString(R.string.timeday) + " ");
                                    }
                                }
                            }
                            repeatype = sb.toString();
                        }
                        report.setText(repeatype);
                    }
                }
            }




            ImageView del = (ImageView) view.findViewById(R.id.btn_del);
            del.setTag(data);
            del.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    submitTimerList(data);
                   //
                }
            });
            return view;
        }
    }

    private void goBackList() {
        layoutTimerList.setVisibility(View.VISIBLE);
        mViewAddingContainer.setVisibility(View.GONE);
        isInList=true;
    }

    protected void setView() {
        super.setView();
        mLvTimer.setAdapter(mAdapt);
    }


    /**
     * 判断国家语言
     */
    private boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CLOSE_DIALOG:
                    mSubmittingDialog.dismiss();
                    break;
                case MSG_FINISH:
                    finish();
                    break;

            }
        }
    };

    /**
     * 0==add timer
     * 1==edit timer
     * 2=delete timer
     *
     * @param timer
     */
    public void submitTimerList(final Timer timer) {
        //添加代码
        if(!NetHelper.isDetailConnnected(getApplicationContext())){
            UiHelper.showShortToast(getApplicationContext(), getString(R.string.not_intent_word));
            return;
        }

        //timer.mId
        final JSONObject json = new JSONObject();
        try {
            json.put("action", "update");
            json.put("apikey", mDeviceEntity.mApiKey);
            json.put("deviceid", mDeviceEntity.mDeviceId);
            if (null != mTimerList) {
                JSONArray array = new JSONArray();
                for (Timer aTimer : mTimerList) {
                    if(timer.mId.equals(aTimer.mId)){
                        continue;
                    }
                    JSONObject aTimer0 = new JSONObject();

                    if(aTimer.typ.endsWith("duration")){
                        aTimer0.put("enabled", 1);
                        aTimer0.put("type", aTimer.typ);
                        aTimer0.put("at", aTimer.at);
                        aTimer0.put("startDo", new JSONObject(aTimer.startDo));
                        aTimer0.put("endDo",new JSONObject(aTimer.endDo));
                        array.put(aTimer0);
                    }else{
                        aTimer0.put("enabled", 1);
                        aTimer0.put("type", aTimer.typ);
                        aTimer0.put("at", aTimer.at);
                        aTimer0.put("do", new JSONObject(aTimer.doAction));//
                        array.put(aTimer0);
                    }

                }
                JSONObject timers = new JSONObject();
                timers.put("timers", array);
                json.put("params", timers);
                json.put("userAgent", "app");
                Helper.addSelfKey(json, app.mUser.apikey);
                json.put("sequence", System.currentTimeMillis() + "");

                DeviceEntity temp = DbManager.getInstance(this).queryDeviceyByDeviceId(mDeviceEntity.mDeviceId);
                HLog.i(TAG, "hzy ---------temp:" + temp);
                //// TODO: 10/19/15
                if(null==temp){
                        Toast.makeText(this, R.string.dataerr,Toast.LENGTH_SHORT).show();
                    mAdapt.refresh();
                        return;
                }else{
                    HLog.i(TAG, "temp.mOnLine:" + temp.mOnLine);
                    if(temp.mOnLine.equals("false")){
                        Toast.makeText(this, R.string.dev_offline,Toast.LENGTH_SHORT).show();
                        return;
                    }else{

                        mSubmittingDialog = DialogHelper.createProgressDialog(this, getString(R.string.del_dialog));

                            mSubmittingDialog.show();

                    }

                }

                postWsRequest(new WsRequest(json) {
                    @Override
                    public void callback(String msg) {
//                        mHandler.sendEmptyMessage(MSG_CLOSE_DIALOG);
                        HLog.i(TAG, "callback msg:" + msg);
                        if (!TextUtils.isEmpty(msg)) {
                            HLog.i(TAG, "msg:" + msg);
                            JSONObject obj;
                            try {
                                obj = new JSONObject(msg);
                                HLog.i(TAG,"obj.getInt(error):"+obj.getInt("error"));
                                if (obj.has("error") && (0 == obj.getInt("error"))) {
                                        mSubmittingDialog.dismiss();
                                        HLog.i(TAG, "hzy delete 定时器成功 timer :"+timer);
                                        app.mDbManager.deleteObject(timer, "mId", timer.mId);
                                        mTimerList.remove(timer);
                                        //  app.mDbManager.deleteObject(timer, "ziduan", "id");
                                        mHandler.sendEmptyMessage(0);
                                        Toast.makeText(SetNewTimerActvity.this,
                                                R.string.delete_timers_ok, Toast.LENGTH_SHORT)
                                                .show();
                                        HLog.i(TAG, "删除定时器成功");

                                    Helper.broadcastTimerChangeList(SetNewTimerActvity.this);

                                    return;
                                }else if(obj.has("error") && (504 == obj.getInt("error"))){
                                    HLog.i(TAG,"obj.getInt(error)1:"+obj.getInt("error"));

                                    Toast.makeText(SetNewTimerActvity.this, R.string.outtimer, Toast.LENGTH_SHORT).show();
                                    //添加代码
                                    mSubmittingDialog.dismiss();
                                    goBackList();
                                    return;
                                }
                                else if(obj.has("error") && (503 == obj.getInt("error"))){//503 设备未在线
                                    HLog.i(TAG,"obj.getInt(error)2:"+obj.getInt("error"));
                                    Toast.makeText(SetNewTimerActvity.this,
                                            getString(R.string.device_not_online), Toast.LENGTH_SHORT).show();
                                    //添加代码
                                    mSubmittingDialog.dismiss();
                                    goBackList();
                                    return;
                                }

                            } catch (JSONException e) {
                                HLog.e(TAG, e);
                            }

                        } else {
                            HLog.i(TAG, "callback err");
                        }

                    }
                });
            }
        } catch (JSONException e) {
            HLog.e(TAG, e);
        }

    }




    public class ListTimerHelper implements View.OnClickListener, AdapterView.OnItemClickListener {

        SetNewTimerActvity mListContext;
        private JSONObject onItemClickjson;
        private JSONObject durationClickjson;

        public ListTimerHelper(SetNewTimerActvity mActivity) {
            mListContext = mActivity;
        }

        @Override
        public void onClick(View view) {
            HLog.i(TAG, "onclict view in list helper");
            switch (view.getId()) {
                case R.id.btn_add_timer:
                    HLog.i(TAG, "onclick add_timer_mTimerList.size():" + mTimerList.size());
                    if (mTimerList.size() <=7) {

                        try {
                            HLog.i(TAG,"setnewtimer outlet:"+mOutlet+"switchsize:"+mSwitchSize);
                            IntentHelper.startAddTimerActivity(SetNewTimerActvity.this,mDeviceEntity.mDeviceId,null,mOutlet,mSwitchSize, DeviceHelper.UI_PLUG_FIRE_PLACE==mDeviceEntity.mUi);

                        } catch (Exception e) {
                            HLog.e(TAG,e);
                        }
                    } else {
                        UiHelper.showShortToast(mListContext, mListContext.getString(R.string.only_number));
                        return;
                    }
                    break;
            }

        }


        @Override
        public void onItemClick(AdapterView<?> adapterView, View data, int posistion, long l) {
//

            try {
           Timer timer=(Timer) data.getTag();
                if(timer.typ.equals("duration")){
                    durationClickjson = new JSONObject(timer.startDo);
                    mOutlet = durationClickjson.has("outlet")?Integer.parseInt(durationClickjson.getString("outlet")):-1;
                }else{
                    onItemClickjson = new JSONObject(timer.doAction);
                    mOutlet = onItemClickjson.has("outlet")?Integer.parseInt(onItemClickjson.getString("outlet")):-1;
                }

                IntentHelper.startAddTimerActivity(SetNewTimerActvity.this,mDeviceEntity.mDeviceId,timer.mId, mOutlet, mSwitchSize, DeviceHelper.UI_PLUG_FIRE_PLACE==mDeviceEntity.mUi);

            } catch (JSONException e) {
               HLog.e(TAG,e);
            }


        }

    }


}
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import elink.HkConst;
import elink.adapt.BaseListAdapt;
import elink.common.UiHelper;
import elink.entity.Timer;
import elink.utils.HomekitFormate;
import elink.utils.IntentHelper;


public class SetNewVirtualTimerActvity extends BasicActivity  {

    private static final String TAG = SetNewVirtualTimerActvity.class.getSimpleName();
    private static final int MSG_CLOSE_DIALOG = 0;
    private static final int MSG_FINISH =MSG_CLOSE_DIALOG+1 ;

    private View layoutTimerList;  //listview 和添加计时器
    private View view_bg_timer;//背景图
    private View view_add_timer; //添加计时器
    private ListView mLvTimer;
    private List<Timer> mTimerList = null;
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
            mAdapt.refresh();
            showbBgView();
        }


    };

    private BroadcastReceiver mTimerReciever=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mHandler.sendEmptyMessage(0);
        }
    };
    private boolean isOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_timer);
        Intent intent = getIntent();
        mOutlet = intent.getIntExtra("extra_outlet",0);
        this.registerReceiver(mTimerReciever,new IntentFilter(HkConst.VIRTUALTIMER));
        HLog.i(TAG, "mOutlet:" + mOutlet);
        initData();
        initView();
        setView();
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
        unregisterReceiver(mTimerReciever);
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

        mSwitchSize=4;
        mListTimerHelper = new ListTimerHelper(this);
        mTitle = getString(R.string.timers);
        app.mCache.mTimerList=new ArrayList<>();
        mTimerList=app.mCache.mTimerList;
        mAdapt = new TimerListAdapt(mTimerList);//数据填充

    }


    class TimerListAdapt extends BaseListAdapt<Timer> {


        public TimerListAdapt(List<Timer> timers) {
            super(timers);
        }

        @Override
        public int getCount() {
            listviewsize=super.getCount();
//            HLog.i(TAG, "listviewsizeP:" + listviewsize);
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


            view.setTag(data);
            TextView report = (TextView) view.findViewById(R.id.tv_repet_type);//周期
            String outlets = data.doAction;
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
        mAdapt.refresh();
        mLvTimer.setAdapter(mAdapt);
        mHandler.sendEmptyMessage(0);
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
//        //添加代码
//        if(!NetHelper.isDetailConnnected(getApplicationContext())){
//            UiHelper.showShortToast(getApplicationContext(), getString(R.string.not_intent_word));
//            return;
//        }

        mTimerList.remove(timer);
        //  app.mDbManager.deleteObject(timer, "ziduan", "id");
        mHandler.sendEmptyMessage(0);
        Toast.makeText(SetNewVirtualTimerActvity.this,
                R.string.delete_timers_ok, Toast.LENGTH_SHORT)
                .show();

    }




    public class ListTimerHelper implements View.OnClickListener, AdapterView.OnItemClickListener {

        SetNewVirtualTimerActvity mListContext;

        public ListTimerHelper(SetNewVirtualTimerActvity mActivity) {
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
                            IntentHelper.startAddVirtualTimerActivity(SetNewVirtualTimerActvity.this);
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

            try {
                Timer timer=(Timer) data.getTag();
                HLog.i(TAG,"timer:"+timer.deviceId+timer.mId);
                JSONObject json = new JSONObject(timer.doAction);
                mOutlet = json.has("outlet")?Integer.parseInt(json.getString("outlet")):-1;
                isOpen = "on".equals(json.getString("switch"));
//                IntentHelper.startAddVirtualTimerActivity(SetNewVirtualTimerActvity.this);
                IntentHelper.startAddVirtualTimerFromListActivity(SetNewVirtualTimerActvity.this,timer.mId);

                HLog.i(TAG,"onItemClick timer:"+timer.at.toString()+app.mCache.mTimerList.size());
            } catch (JSONException e) {
                HLog.e(TAG,e);
            }


        }

    }


}
package elink.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import elink.common.UiHelper;
import elink.controller.addCopy;
import elink.utils.DialogHelper;
import elink.utils.NetHelper;
import elink.widget.ConfirmDialog;

public class AddDeviceActivity extends BasicActivity<addCopy>
        implements OnClickListener, OnItemClickListener {

    private static final String TAG = AddDeviceActivity.class.getSimpleName();
    public static final int MSG_SEND_CONFIG_OVER_TIMER = 0;
    public static final int MSG_HOST_EXCEPTION = MSG_SEND_CONFIG_OVER_TIMER+1;

    private View mllSearching;
    private View mllGuide;
    private View mBtnGotoSearch;
    private View mLlAroundDevice;
    private View mLlConfigView;
    private View mBtnGoConfig;

    private Spinner mEditWlans;
    private EditText mEdPwd;

    private ListView mLlDeviceList;
    private ImageView mIvShowWlans;
    private Button mBtnSendConfig;
    private TextView mBtnGoback;

    private List<ScanResult> mAroundDevice;
    private List<ScanResult> mVps;

    protected DeviceListAdapt mDeviceAdapt;

    public int mBlue;
    private int mGray;

    private View mRlWlans;

    private CheckBox mCSave;

    private Dialog mSendingDialog;

    private TextView mTvSelectedDeviceSSID;

    private Drawable mDrawableOn;

    private Drawable mDrawableClose;

    private ImageView mIvPwd;
    private boolean mHidePwd = true;

    private TextView mTvSavePwd;

    private View mLLSetName;

    private EditText mEtSetName;

    private Button mBtnGoEegister;

    private Dialog mRegistingDialog;
    private TextView mTvNoWifi;
    public Dialog mSendingEmptyPwdHomeWifiConfireDIalog;
    private boolean mIsEndSending=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HLog.i(TAG, "AddDeviceActivity oncreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_device);
        initView();
        initData();
        setView();
        doRegister();
    }

    private void doRegister() {
        mController.doRegister();

    }

    @Override
    protected void onDestroy() {
        if (null != mSearchingThread) {

            mSearchingThread.stopSearching();
        }
        mController.connectToHomeSSID();

        super.onDestroy();
        mController.unRegister();
        if(null!=mSendingEmptyPwdHomeWifiConfireDIalog&&mSendingEmptyPwdHomeWifiConfireDIalog.isShowing()){
            mSendingEmptyPwdHomeWifiConfireDIalog.dismiss();
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // if(eve)
        return super.onKeyDown(keyCode, event);
    }

    protected void initData() {
        mController = new addCopy(this);
        mController.saveCurrentWlan();

        mAroundDevice = new ArrayList<ScanResult>();
        mDeviceAdapt = new DeviceListAdapt(mAroundDevice);
        mBlue = getResources().getColor(R.color.blue);
        mGray = getResources().getColor(R.color.white);

        mDrawableClose = getResources().getDrawable(R.drawable.eye_off_black);
        mDrawableOn = getResources().getDrawable(R.drawable.eye_on_black);
    }

    class DeviceListAdapt extends BaseAdapter {

        private List<ScanResult> mData;

        public DeviceListAdapt(List<ScanResult> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return null == mData ? 0 : mData.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null == mData ? null : mData.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        public void refresh() {
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ScanResult data = mData.get(arg0);
            TextView view = null;
            HLog.i(TAG, "get view,ssid:" + data.SSID);
            view = (TextView) getLayoutInflater().inflate(R.layout.ar_item,
                    null);
            view.setText(data.SSID);
            view.setTag(data);
            if (data == mSelectedDevice) {
                view.setBackgroundColor(mBlue);
            } else {
                view.setBackgroundColor(mGray);

            }
            return view;
        }
    }

    protected void initView() {
        mllGuide = this.findViewById(R.id.ll_guide);
        mllSearching = this.findViewById(R.id.ll_searching);
        mLlAroundDevice = this.findViewById(R.id.ll_device_list);
        mTvNoWifi= (TextView)findViewById(R.id.tv_no_wifi);

        mBtnGotoSearch = mllGuide.findViewById(R.id.btn_gotoSearch);
        mLlDeviceList = (ListView) mLlAroundDevice
                .findViewById(R.id.lv_around_device);
        mBtnGoConfig = mLlAroundDevice.findViewById(R.id.btn_go_config);

        mLlConfigView = this.findViewById(R.id.ll_config);
        mTvSelectedDeviceSSID = (TextView) mLlConfigView
                .findViewById(R.id.tv_config_title);
        mEditWlans = (Spinner) this.findViewById(R.id.et_wlan_name);
        mEdPwd = (EditText) this.findViewById(R.id.et_pwd);
        mIvPwd = (ImageView) this.findViewById(R.id.iv_pwd_hide);
        mTvSavePwd = (TextView) findViewById(R.id.tv_save_pwd);

        // set name

        mLLSetName = findViewById(R.id.ll_set_name);
        mEtSetName = (EditText) findViewById(R.id.et_device_name2);

        mTvSavePwd.setOnClickListener(this);

        mIvShowWlans = (ImageView) this.findViewById(R.id.iv_select_wlan);
        mBtnGoEegister = (Button) this.findViewById(R.id.btn_go_register);
        mBtnGoEegister.setOnClickListener(this);

        mCSave = (CheckBox) this.findViewById(R.id.cb_save);
        mCSave.setOnClickListener(this);
        // mBtn=m
        mBtnSendConfig = (Button) mLlConfigView
                .findViewById(R.id.btn_send_config);
        mBtnGoback = (TextView) this.findViewById(R.id.tv_goback);
        mBtnGotoSearch.setOnClickListener(this);
        mBtnSendConfig.setOnClickListener(this);
        mBtnGoConfig.setOnClickListener(this);
        mLlDeviceList.setOnItemClickListener(this);
        mBtnGoback.setOnClickListener(this);
        mIvShowWlans.setOnClickListener(this);
        mRlWlans = findViewById(R.id.rl_wlan);

        mRlWlans.setOnClickListener(this);
        // mEditWlans.setOnClickListener(this);

        // 设置EditText文本为可见的
        mEdPwd.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        mIvPwd.setImageDrawable(mDrawableClose);

        mIvPwd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mHidePwd) {
                    // 设置EditText文本为可见的
                    mEdPwd.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                    mIvPwd.setImageDrawable(mDrawableClose);
                } else {
                    // 设置EditText文本为隐藏的
                    mEdPwd.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    mIvPwd.setImageDrawable(mDrawableOn);
                }
                mHidePwd = !mHidePwd;
                mEdPwd.postInvalidate();
                // 切换后将EditText光标置于末尾
                CharSequence charSequence = mEdPwd.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }

            }
        });
    }

    protected void setView() {
        super.setView();
        mLlDeviceList.setAdapter(mDeviceAdapt);

        setTitleColor(mBlue);

    }


    public  void clickSendConfig(){

        HLog.i(TAG,"clickSendConfig:"+NetHelper.openWifi(app.getWifiManager()));
        if(!NetHelper.openWifi(app.getWifiManager())){
            //关闭wifi时候的提示
            UiHelper.showShortToast(getApplicationContext(),getString(R.string.wifiswitch));
            return;
        }


        //没有选择家庭网络
        if (null == mEditWlans.getSelectedItem()) {//String.valueOf(this.getText(R.string.))
            UiHelper.showShortToast(this, this.getString(R.string.chose_internet));
            return;
        }



        mController.mHomeSSid = ((ScanResult) mEditWlans.getSelectedItem()).SSID.toString();
        mController.mPWD = mEdPwd.getEditableText().toString();
        //添加代码判断中英文
        Pattern p = Pattern.compile("^[\u4e00-\u9fa5]*$");
        Matcher m = p.matcher(mController.mHomeSSid);

        String ssid = mController.mHomeSSid;
        for(int i = 0; i<ssid.length(); i++){
            String aa = ssid.substring(i,i+1);
            boolean ssidHasChinese = Pattern.matches("[\u4E00-\u9FA5]",aa);
            if(ssidHasChinese){
                UiHelper.showShortToast(this,getResources().getString(R.string.SSID_no_chinese));
                return;
            }
        }

        String pwd = mController.mPWD;
        for(int i =0; i<pwd.length(); i++){
            String bb = pwd.substring(i,i+1);
            boolean pwdHasChinese = Pattern.matches("[\u4E00-\u9FA5]",bb);
            if(pwdHasChinese){
                UiHelper.showShortToast(this,getResources().getString(R.string.pwd_no_chinese));
                return;
            }
        }

        if(TextUtils.isEmpty(mController.mHomeSSid)){
            UiHelper.showShortToast(this,getString(R.string.select_wifi));
            return;
        }
        else if(mController.mHomeSSid.indexOf(" ")>0){
            UiHelper.showShortToast(this,getResources().getString(R.string.SSID_no_space));
            return;
        }
//        else if(m.find()){
//            UiHelper.showShortToast(this,getResources().getString(R.string.SSID_no_chinese));
//            return;
//        }

        m = p.matcher(mController.mPWD);
        if(TextUtils.isEmpty(mController.mPWD)){
            {
                mSendingEmptyPwdHomeWifiConfireDIalog=new ConfirmDialog(this,"",getResources().getString(R.string.connectto_nopwd),getResources().getString(R.string.ok),getResources().getString(R.string.cancel)){
                    @Override
                    public void onOkayClick() {
                        super.onOkayClick();
                        sendConfig();

                    }
                };

                mSendingEmptyPwdHomeWifiConfireDIalog.show();
            }
        }
//        else if(m.find()){
//            UiHelper.showShortToast(this,getResources().getString(R.string.pwd_no_chinese));
//            return;
//        }
        else {
            sendConfig();
        }



    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_gotoSearch:
                HLog.i(TAG, " on clikc goto search");

                searchingDevice();

                break;
            case R.id.btn_go_config:
                HLog.i(TAG, "onclic go config device");
                if (null == mSelectedDevice) {
                    Toast.makeText(this, this.getString(R.string.chose_version), Toast.LENGTH_SHORT).show();
                    return;
                }
                connectToVpIfPossible();
                goConfig();
                break;
            case R.id.btn_send_config:
                HLog.i(TAG, "click send config");
                clickSendConfig();
                break;
            case R.id.tv_goback:
                HLog.i(TAG, "on click go back");

                finish();
                break;
            case R.id.rl_wlan:
            case R.id.et_wlan_name:
            case R.id.iv_select_wlan:
                HLog.i(TAG, "on click select wlan");

                // showHomeWlans();
                break;

            case R.id.cb_save:

                savePwd();
                break;
            case R.id.tv_save_pwd:
                mCSave.setChecked(!mCSave.isChecked());
                savePwd();
                break;
            case R.id.btn_go_register:
                HLog.i(TAG, "on click set device name and register");
                if(TextUtils.isEmpty(mEtSetName.getEditableText().toString().trim())){
                    UiHelper.showShortToast(getApplicationContext(),getString(R.string.device_name_not));
                    return;
                }

                mBtnGoEegister.setEnabled(false);
                mRegistingDialog = DialogHelper.createProgressDialog(this,
                        this.getString(R.string.registing));
                mRegistingDialog.show();
                mRegistingDialog.setCanceledOnTouchOutside(false);
                mRegistingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(AddDeviceActivity.this, getResources().getString(R.string.cancle_add), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                mController.regesterDevice(mEtSetName.getEditableText().toString());
                //over time in 15 seconds
                mController.resultHandler.sendEmptyMessageDelayed(mController.MSG_OVER_TIMER, 60*1000);
            default:
                break;
        }

    }

    public void dissmissRegistingDialog() {
        if (null != mRegistingDialog && mRegistingDialog.isShowing()&&!isDestroy) {
            mRegistingDialog.dismiss();
        }
    }

    private void savePwd() {
        if (mCSave.isChecked()) {

            this.app.mSp.saveWlanPwd(mSendingVp.SSID, mEdPwd.getEditableText()
                    .toString());
        }

    }

    private void connectToVpIfPossible() {

        this.mController.connectTOVp(mSelectedDevice.SSID);
    }

    /**
     * 切换网络并连接，显示配置
     */
    private void goConfig() {
        mLlAroundDevice.setVisibility(View.GONE);
        mLlConfigView.setVisibility(View.VISIBLE);

        mTvSelectedDeviceSSID.setText(this.getString(R.string.config_device) + mSelectedDevice.SSID);
        mVps = NetHelper.scan(mController.app.getWifiManager());

        if ((null != mVps) && (mVps.size() > 0)) {
            mSendingVp = mVps.get(0);

            HLog.i(TAG, "show wlans ");
            mEditWlans.setAdapter(new DeviceListAdapt(mVps));

            mEditWlans.setSelection(0);

            mEditWlans.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    mSendingVp = (ScanResult) view.getTag();

                    mEdPwd.setText(app.mSp.getWlanPwd(mSendingVp.SSID));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub

                }

            });

            mEdPwd.setText(app.mSp.getWlanPwd(mSendingVp.SSID));
        }

    }

   public Handler mHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SEND_CONFIG_OVER_TIMER:
                    if(!isDestroy&&!mIsEndSending){
                        HLog.i(TAG,"has not end sending config yet ,finish adding ");
                        Toast.makeText(AddDeviceActivity.this,AddDeviceActivity.this.getResources().getString(R.string.send_config_over_time),Toast.LENGTH_LONG).show();
                        AddDeviceActivity.this.finish();
                    }
                    break;
                case MSG_HOST_EXCEPTION:
                    doHostException();
                    break;


            }

        }


   };

    private void doHostException() {
        WifiManager wifimanager = this.app.getWifiManager();
        WifiInfo wifimanagerConnectionInfo = wifimanager.getConnectionInfo();

        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activityNetworkInfo = connectivityManager.getActiveNetworkInfo();

        dismissSendConfigdialog();
        finish();

        if (activityNetworkInfo != null && wifimanagerConnectionInfo != null) {

            NetworkInfo.DetailedState detailedState = activityNetworkInfo.getDetailedState();

            String connectedSsid = wifimanagerConnectionInfo.getSSID();


            if ((NetworkInfo.State.CONNECTED == activityNetworkInfo.getState()) && (detailedState == NetworkInfo.DetailedState.CONNECTED)) {

                //CONNECTTED TO DEVICE
                if ((mController.mVpSSID != null) && (("\"" + mController.mVpSSID + "\"").equals(connectedSsid) || (mController.mVpSSID.equals(connectedSsid)))) {
                    HLog.i(TAG, " has connected to device vp,while host excepiton happens");

                    UiHelper.showLongToast(this,getString(R.string.wifi_exception));

                    return;

                }
            }
        }
        UiHelper.showLongToast(this,  getString(R.string.connect_failed));

    }

    /**
     * 发送配置信息
     */
    private void sendConfig() {


        mSendingDialog = DialogHelper.createProgressDialog(this, this.getString(R.string.send_config_msg));

        mSendingDialog.show();
        mSendingDialog.setCancelable(true);
        mSendingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(AddDeviceActivity.this, getResources().getString(R.string.cancle_add), Toast.LENGTH_LONG).show();
                finish();
            }
        });
        mSendingDialog.setCanceledOnTouchOutside(false);

        mHandler.sendEmptyMessageDelayed(MSG_SEND_CONFIG_OVER_TIMER, 60 * 1000);

        this.mController.sendConfigInfo();
    }

    public void dismissSendConfigdialog(){
        mSendingDialog.dismiss();
    }

    private ScanResult mSelectedDevice;
    protected ScanResult mSendingVp;

    private class SearchingThread extends Thread {
        protected boolean stopSearching;
        protected int retry = 60;
        private void stopSearching() {

            stopSearching = true;
        }

        @Override
        public void run() {
            while (!stopSearching && retry-- > 1) {
                HLog.i(TAG, "seaching retry:" + retry);
                boolean openWifi = NetHelper.openWifi(app.getWifiManager());
                HLog.i(TAG, "open wifi result:" + openWifi);
                List<ScanResult> wifis = NetHelper.scan(app.getWifiManager());
                if(null==wifis||wifis.isEmpty()){
                    if(retry<=15){
                        HLog.i(TAG, "set auth none");
                        mSearchingHandler.obtainMessage(3,getString(R.string.not_find_device)).sendToTarget();

                    }


                }else {
//                    mSearchingHandler.obtainMessage(3,"").sendToTarget();
                    if(wifis.size()==1 && "00:00:00:00:00:00".equals(wifis.get(0).BSSID.toString())){
                        HLog.i(TAG,"wifi_BSSID:"+wifis.get(0).BSSID.toString()+"wifi_size:"+wifis.size());
                        if(retry<=40){
                            mSearchingHandler.obtainMessage(3,getString(R.string.check_get_location)).sendToTarget();
                        }
                    }else{
                        mSearchingHandler.obtainMessage(4,"").sendToTarget();
                    }

                }

                List<ScanResult> list = mController.getAroudDevice();
                if (null != list && list.size() == 1) {
                    stopSearching();
                    mSelectedDevice = list.get(0);
                    HLog.i(TAG,"searched one device :"+mSelectedDevice.SSID);
                    mSearchingHandler.sendEmptyMessage(1);
                } else {
                    Message msg = Message.obtain();
                    msg.obj = list;
                    msg.what = 0;
                    mSearchingHandler.removeMessages(0);
                    mSearchingHandler.sendMessage(msg);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        HLog.e(TAG,e);
                    }
                }

            }
            if (retry == 0) {
//                HLog.i(TAG,"retry=0.sendEmptyMessage");
                mSearchingHandler.sendEmptyMessage(2);
            }

            HLog.i(TAG, "end searching");
        }
    }



    Handler mSearchingHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (null != msg.obj) {
                        List<ScanResult> result = (List<ScanResult>) msg.obj;

                        if (!result.isEmpty()) {
                            HLog.i(TAG, "handleMessage searching result :" + result.size());
                            mllSearching.setVisibility(View.GONE);
                            mLlAroundDevice.setVisibility(View.VISIBLE);

                            mAroundDevice.clear();
                            mAroundDevice.addAll(result);
                            mDeviceAdapt.refresh();

                        } else {
                            HLog.i(TAG, "handleMessage searching result :" + 0);

                        }
                    }
                    break;
                case 1:
                    connectToVpIfPossible();
                    goConfig();
                    break;
                case 2:
//                    HLog.i(TAG,"excute case:2");
//                    finish();
//                    UiHelper.showShortToast(AddDeviceActivity.this, getString(R.string.not_found_device));
                    mTvNoWifi.setText(getString(R.string.not_found_device));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                    break;

                case 3:
                    mTvNoWifi.setText((String) msg.obj);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                    break;

                case 4:
                    mTvNoWifi.setText((String)msg.obj);
                    break;
                default:
                    break;
            }

        }


    };

    private SearchingThread mSearchingThread;

    private void searchingDevice() {
        mllGuide.setVisibility(View.GONE);
        mllSearching.setVisibility(View.VISIBLE);
        if (null == mSearchingThread || mSearchingThread.stopSearching) {
            HLog.i(TAG,
                    "to start searcing thread,before started:there has a thread:"
                            + (mSearchingThread != null));
            mSearchingThread = new SearchingThread();
            mSearchingThread.start();
        }else{
            HLog.i(TAG,
                    "to start searcing thread,before started:there has a thread:");
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mSelectedDevice = (ScanResult) arg1.getTag();
        mDeviceAdapt.refresh();
        mSearchingThread.stopSearching();
    }

    String text = "";

    public Dialog mConfigOkayDialog;

    public void DoAfterConnectToDevice(String deviceSsid,int i, String msg) {
        mIsEndSending=true;
        WifiConfiguration tempConfig = NetHelper.isExsits(deviceSsid, app.getWifiManager());

        if (tempConfig != null) {
            HLog.i(TAG, "remove old config of device");
            app.getWifiManager().removeNetwork(tempConfig.networkId);
        }else {
            HLog.i(TAG, "has no remove device config");

        }
        boolean reass=app.getWifiManager().reassociate();;
        HLog.i(TAG, "reassociate:"+reass);


        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!isDestroy){
            mSendingDialog.dismiss();
        }

        if (0 == i) {
            HLog.i(TAG, "DoAfterConnectToDevice okay");
            new Handler(Looper.getMainLooper()) {
                public void handleMessage(Message msg) {
                    HLog.i(TAG,"Message msg:" + msg);
                    switch (msg.what) {
                        case 0:
                            if (!isDestroy) {
                                mConfigOkayDialog = DialogHelper
                                        .createProgressDialog(
                                                AddDeviceActivity.this, getString(R.string.config_sucess));
                                mLLSetName.setVisibility(View.VISIBLE);
                                mLlConfigView.setVisibility(View.GONE);
                                mController.connectToHomeSSID();
                                if(!isDestroy){
                                    mConfigOkayDialog.show();
                                }

                                sendEmptyMessageAtTime(1, 2000);
                            }

                            break;
                        case 1:
                            if (null != mConfigOkayDialog
                                    && mConfigOkayDialog.isShowing()&&!isDestroy) {
                                mConfigOkayDialog.dismiss();

                            }
                        default:
                            break;
                    }

                }
            }.sendEmptyMessage(0);

        } else if (2 == i) {
            text = getString(R.string.err_internet);
            new Handler(Looper.getMainLooper()) {
                public void handleMessage(Message msg) {
                    Toast.makeText(AddDeviceActivity.this, text,
                            Toast.LENGTH_SHORT).show();
                    finish();

                }
            }.sendEmptyMessage(0);
        }
        ;

    }
}

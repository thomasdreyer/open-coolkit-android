package elink.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import demo.demo.R;
import com.coolkit.WebSocketManager;
import com.coolkit.common.HLog;

import java.util.ArrayList;
import java.util.List;

import elink.HkApplication;
import elink.HkConst;
import elink.common.UiHelper;
import elink.manager.SystemActionHolder;
import elink.reciver.ShareInvitedReciver;
import elink.utils.Debugg;
import elink.utils.IntentHelper;

public class DeviceActivity extends FragmentActivity implements View.OnClickListener,SystemActionHolder.SystemMsgCallBack {

    public static final String TAG = DeviceActivity.class.getSimpleName();

    private Button mBtnGoUc;
    private BroadcastReceiver mRe;
    private Dialog mOtaDialog;
    private View mVD;
    private View mAiraRl;
    private View mCameraRl;

    public DeviceListFragment mDeviceListFragment;
    private UsercenterFragment mUserCenterFragment;

    public HkApplication app;


    private LinearLayout linerlayout;
    private Button mBtnGoDeviceList;
    private Fragment mcurrentFragment;
    private BroadcastReceiver mBroadCast;
    Bundle mBundle = null;

    public static  int devicei=0;
    public int activityi=0;
    private Menu mMenu;
    private View mViewbar;
    private ImageView mCoolKitBtn;
    private PopupWindow pwMyPopWindow;
    private LinearLayout view;
    private ListView lvPopupList;
    private List<String> moreList;
    private TextView mTvtitlename;
    private Button mFabAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);


        mBundle = savedInstanceState;
        activityi=++devicei;
        HLog.i("configchange", "oncreate activity:" + activityi);
        HLog.i(TAG, "DeviceActivity oncreate,save instanse isempty:" + (savedInstanceState == null));
        setContentView(R.layout.activity_device);
        UiHelper.getOverflowMenu(this);
        initView();
        setView();

        app = (HkApplication) this.getApplicationContext();

        doRegiester();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        HLog.i(TAG, "on configraction change");
        this.finish();
        IntentHelper.startDeviceActvity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HLog.i(TAG, "on destroy");
        this.unregisterReceiver(mRe);
        this.unregisterReceiver(mBroadCast);
        app.mSysManager.deStroyReciever();
        app.mSysManager.deStroyReciever(this);

    }



    private void doRegiester() {

        mRe = new ShareInvitedReciver(this);
        this.registerReceiver(mRe, new IntentFilter("com.homekit.action.SHARE"));
        this.registerReceiver(mRe, new IntentFilter(
                "com.homekit.action.CANCLE_SHARE"));
        this.registerReceiver(mRe, new IntentFilter("com.homekit.action.NOTIFY"));
        mBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onRecieve(intent.getAction(),intent);
            }
        };
        app.mSysManager.regesterSysMsg();
        app.mSysManager.regesterSysMsg(this);

        this.registerReceiver(mBroadCast, new IntentFilter("com.homekit.action.SYNC_DEVICE"));
        this.registerReceiver(mBroadCast, new IntentFilter(HkConst.INTENT_SYNC_LOCAL));
        this.registerReceiver(mBroadCast, new IntentFilter("com.homekit.EDIT_IMG"));
        this.registerReceiver(mBroadCast, new IntentFilter("com.homekit.action.EDIT_NICK_NAME"));
        this.registerReceiver(mBroadCast, new IntentFilter("com.homekit.action.AT_OVER_TIMER"));

//        mReciever = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                if (""
//                        .equals(arg1.getAction())) {
//
//                } else {
//                    byte[] b = arg1.getByteArrayExtra("bitmap");
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0,
//                            b.length);
//                    if (bitmap != null) {
//                        mIvHead.setImageBitmap(bitmap);
//                    }
//                }
//
//            }
//        };




    }
    public boolean doDeviceOnline(String deviceId, boolean onLine,
                                  boolean hasGlobl,String json) {
        HLog.i(TAG, "do device onLine");
        mDeviceListFragment.mController.synLocal();
        if(onLine){
            mDeviceListFragment.cancleOta(deviceId);
        }

        return true;
    }

    public boolean doUpdateParams(String deviceId, String params,
                                  boolean hasGlobl,String json) {
        HLog.i(TAG, "super update params");
        mDeviceListFragment.mController.synLocal();


        return true;
    }

    //	@Override
    public void onRecieve(String action,Intent intent) {
        if ("com.homekit.action.SYNC_DEVICE".equals(action)) {
            int appid=intent.getIntExtra("appid",0);
            boolean isCurrentApp=getApplicationContext().hashCode()==appid;
            HLog.i(TAG,"onrecieve syn local ,is current app:"+isCurrentApp);
            if(isCurrentApp){
                mDeviceListFragment.mController.synUserDevice();
            };
        } else if (HkConst.INTENT_SYNC_LOCAL.equals(action)) {
            int appid=intent.getIntExtra("appid",0);
            if(getApplicationContext().hashCode()==appid){
                mDeviceListFragment.mController.synLocal();
            };

        }else if ("com.homekit.action.EDIT_NICK_NAME".equals(action)) {
            HLog.i(TAG, "on recieve nicked edit");
            mUserCenterFragment.setView();
        } else if ("com.homekit.action.AT_OVER_TIMER".equals(action)) {
            logout();

        }

    }


    protected void setView() {
//		mTitle="我的设备";
//		super.setView();
//        this.getActionBar().setDisplayHomeAsUpEnabled(false);

//        getActionBar().setLogo(new BitmapDrawable());


    }


    public void selectUc(boolean uc) {
        Drawable startDra = getResources().getDrawable(uc ? R.drawable.uc_press : R.drawable.uc_normal);
        startDra.setBounds(0, 0, startDra.getMinimumWidth(), startDra.getMinimumHeight());
        mBtnGoUc.setCompoundDrawables(null, startDra, null, null);

        mBtnGoUc.setTextColor(getResources().getColor(uc ? R.color.blue : R.color.black));


        Drawable deListDr = getResources().getDrawable(uc ? R.drawable.home_device_tab_normal : R.drawable.home_device_press);
        deListDr.setBounds(0, 0, deListDr.getMinimumWidth(), deListDr.getMinimumHeight());

        mBtnGoDeviceList.setCompoundDrawables(null, deListDr, null, null);
        mBtnGoDeviceList.setTextColor(getResources().getColor(uc ? R.color.black : R.color.blue));

    }

    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()) {
            case R.id.fab_add_device:
                HLog.i(TAG, "go add device activity");
                if(Debugg.DEBUG_ESP_TOUCH){
                    IntentHelper.startAddEspTouchActivity(this);

                }else {
                    IntentHelper.startAddDeviceActivity(this);

                }
                break;
            case R.id.btn_go_uc:
                mcurrentFragment = mUserCenterFragment;
                findViewById(R.id.viewbar).setVisibility(View.GONE);
                selectUc(true);
                FragmentTransaction transe = this.getSupportFragmentManager().beginTransaction();
                transe.replace(R.id.linearlayout_container, mUserCenterFragment);
                transe.commit();
//                setMunu(false);
                break;
            case R.id.btn_go_device_list:
                findViewById(R.id.viewbar).setVisibility(View.VISIBLE);
                mcurrentFragment = mDeviceListFragment;
                mDeviceListFragment.isNew=true;
                selectUc(false);
                FragmentTransaction transe2 = this.getSupportFragmentManager().beginTransaction();
                transe2.replace(R.id.linearlayout_container, mDeviceListFragment);
                transe2.commit();
//                setMunu(true);

                break;

        }
    }

    public void initpop(){
        HLog.i(TAG,"initpop excute");
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_list, null);
        pwMyPopWindow = new PopupWindow(layout, ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT);
        pwMyPopWindow.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件

        layout.findViewById(R.id.tv_exit_coolkit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                pwMyPopWindow.dismiss();
            }
        });
        layout.findViewById(R.id.tv_exit_process).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceActivity.this.finish();
                pwMyPopWindow.dismiss();
            }
        });


        // 控制popupwindow点击屏幕其他地方消失
        pwMyPopWindow.setBackgroundDrawable(new BitmapDrawable());// 设置背景图片，不能在布局中设置，要通过代码来设置
        pwMyPopWindow.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功，如上


    }



//



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mcurrentFragment == mUserCenterFragment) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        mMenu=menu;
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

    public void setMunu(boolean show){
//        mMenu.setGroupVisible(0, show);
//        mMenu.setGroupVisible(1,show);
//        mMenu.setGroupVisible(2,show);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        HLog.i(TAG, "on option order click:" + item.getOrder());
//        if (item.getItemId() == R.id.action_groups) {
//            IntentHelper.startGroupsActivity(this);
//            return true;
//        } else if (item.getItemId() == R.id.action_publish) {
//            logout();
//            return true;
//
//        }else if(item.getItemId() == R.id.action_close){
//            DeviceActivity.this.finish();
//        }
//        return false;
//    }

    public void logout() {

        app.mUser.isLogin = false;
        app.mSp.saveLogout(app.mUser.userName);
        app.mUser.apikey = "";
        WebSocketManager.getInstance(this.app.mAppHelper).close();
        finish();
        IntentHelper.startUserActvity(this);
    }

    protected void initView() {

        mFabAdd = (Button) findViewById(R.id.fab_add_device);
        mBtnGoUc = (Button) findViewById(R.id.btn_go_uc);
        mBtnGoDeviceList = (Button) findViewById(R.id.btn_go_device_list);

        mFabAdd.setOnClickListener(this);
        mBtnGoUc.setOnClickListener(this);
        initPager();
        mBtnGoDeviceList.setOnClickListener(this);


    }

    int i = 10;

    public void initPager() {
        if (null == mDeviceListFragment) {
            linerlayout = (LinearLayout) this.findViewById(R.id.linearlayout_container);

            List fragments = new ArrayList();
            mDeviceListFragment = new DeviceListFragment();
            mUserCenterFragment = new UsercenterFragment();
            fragments.add(mDeviceListFragment);
            fragments.add(mUserCenterFragment);


            FragmentTransaction transe = this.getSupportFragmentManager().beginTransaction();
            transe.replace(R.id.linearlayout_container, mDeviceListFragment);
            transe.commit();

            mcurrentFragment = mDeviceListFragment;
            selectUc(false);
        }


    }


}

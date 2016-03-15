package elink.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;
import com.coolkit.common.WsRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import elink.DeviceHelper;
import elink.HkConst;
import elink.activity.details.DetailHelper;
import elink.adapt.BaseListAdapt;
import elink.common.Helper;
import elink.common.UiHelper;
import elink.controller.DeviceListFragmentController;
import elink.dslv.DragSortListView;
import elink.entity.DeviceEntity;
import elink.entity.Timer;
import elink.model.DbManager;
import elink.model.DeviceModel;
import elink.utils.DialogHelper;
import elink.utils.IntentHelper;

public class DeviceListFragment extends Fragment implements OnClickListener {
    protected static final String TAG = DeviceActivity.class.getSimpleName();
    public static final int MSG_OTA_ING = 5;
    public static final int MSG_OTA_OK = 4;
    public static final int MSG_OTA_FAIL = 3;
    public static final int MSG_REFRESH_ADAPT = 0;
    public static final int MSG_OTA_RESULT_TIMER = MSG_OTA_ING + 1;

    private DragSortListView mDeviceListView;
    private DeviceListAdapt mAdapt;
    public DeviceListFragmentController mController;
    public BaseHelper basicHeper;
    public DeviceModel mMode;


    private Dialog mDialog;
    private Dialog mOtaDialog;

    private Dialog mOtaConfireDialog;
    private Dialog mDeleteConfireDialog;
    private View mView;
    private View mCameraRl;
    private View mVD;
    Context mContext;


    private HashMap otaMap =null;
    private boolean isnewtimer;
    private int outlet;
    public static  int i=0;
    public int fragmenti=0;


    boolean isNew=true;
    private View mViewbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmenti=++i;
        DeviceActivity a= (DeviceActivity) getActivity();
        HLog.i("configchange", "oncreate fragment:" + fragmenti + " while activity is:" + a.activityi);
        mContext=this.getActivity();
        basicHeper=new BaseHelper(getActivity());
        otaMap=basicHeper.app.otaMap;
        setRetainInstance(true);
        HLog.i(TAG, " on create fragement:" + HkConst.CONFIG_APK_VERSION);
        mContext.registerReceiver(mRefreshReciever, new IntentFilter("com.homekit.action.TimerChange"));
//        Toast.makeText(getActivity(),"version:"+1,Toast.LENGTH_LONG).show();
    }

    BroadcastReceiver mRefreshReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapt.refresh();
            mDeviceListView.invalidate();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mRefreshReciever);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(isNew){
            HLog.i(TAG, "on create fragment view");

            initData();
            mView = inflater.inflate(R.layout.fragment_device, null, false);
            initView();
            isNew=false;
            return  mView;
        }else{
            return  super.onCreateView(inflater,container,savedInstanceState);
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        this.getActivity().getActionBar().show();
//        this.getActivity().getActionBar();


    }


    private void   initView() {
        mDeviceListView = (DragSortListView) mView.findViewById(R.id.lv_device_list);
        mViewbar = getActivity().findViewById(R.id.viewbar);

//        if (Host.isShow) {
//            mCameraRl = mView.findViewById(R.id.rl_camara);
//            // mAiraRl = findViewById(R.id.rl_air);
//            mCameraRl.setVisibility(View.VISIBLE);
//            // // mAiraRl.setVisibility(View.VISIBLE);
//            mCameraRl.setOnClickListener(this);
//            // mAiraRl.setOnClickListener(this);
//
//        }

        mAdapt = new DeviceListAdapt(new ArrayList<DeviceModel.ItemInfo>(mController.mMode.mUserDevice));
        mDeviceListView.setAdapter(mAdapt);

        mDeviceListView.setDropListener(new DeviceDropListener());



    }

    private String mDeviceId;
    DetailHelper helper = null;
    private DeviceEntity mDeviceEntity;
    private int timerCount;

    public void initData() {



        mController = new DeviceListFragmentController((DeviceActivity) this.getActivity(), this);
        mController.synLocal();
        mController.synUserDevice();
    }



        public Handler uiHandler = new Handler() {


        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_ADAPT:
                    HLog.i(TAG, " ui messge to refresh adapt");
                    mAdapt.setData(new ArrayList<DeviceModel.ItemInfo>((List<DeviceModel.ItemInfo>) msg.obj));
                    mAdapt.refresh();
                    mDeviceListView.invalidate();
                    break;
                case MSG_OTA_OK:
                    showOtaOkay((String) msg.obj);

                    if (null!=getActivity()){
                       Helper.broadcastSynLocalDevice(getActivity());
                    }

                    break;
                case MSG_OTA_FAIL:
                    showOtaError((String) msg.obj);
                    break;
                case MSG_OTA_ING:


                    showOtaIng((String) msg.obj);

                    Message msg2 = obtainMessage(MSG_OTA_RESULT_TIMER, (String) msg.obj);
                    sendMessageDelayed(msg2, 300 * 1000);

                    break;
                case MSG_OTA_RESULT_TIMER:
                    HLog.i(TAG,"ota over timer ");
                    showOtaFinish((String) msg.obj, true);
                    break;
                default:
                    break;
            }

        }

        ;
    };


    public void onSingleTapUp(DeviceModel.ItemInfo info) {
        if (null != info) {
            switch (info.deviceType) {


                default:
                    if (otaMap.containsKey(info.mDId)) {
                        UiHelper.showShortToast(getActivity(), mContext.getString(R.string.title_update));
                        return;
                    }
                    IntentHelper.startSwichDetail(getActivity(), info);
                    break;
            }

        }
    }


    public void onClick(View arg0) {

        switch (arg0.getId()) {

            case R.id.tv_ota:
                HLog.i(TAG, "on click ota device");
                    DeviceModel.ItemInfo item = (DeviceModel.ItemInfo) arg0.getTag();
                    doOta(item);

                break;
            case R.id.tv_del:
                HLog.i(TAG, "on click delete device");
                doDele(arg0);
                break;

            case R.id.time_icon:
                HLog.i(TAG, "on click  time icon");
//                HLog.i(TAG,"gettag："+arg0.getTag(0));
//                HLog.i(TAG,"gettag："+arg0.getTag());
//                HLog.i(TAG,"gettag："+arg0.getTag().toString());
//                int tag=Integer.parseInt(arg0.getTag().toString());
                DeviceModel.ItemInfo items = (DeviceModel.ItemInfo) arg0.getTag();

                if(items.onLine){
                    doTimer(items, (Boolean)(arg0.getTag(R.id.key_has_timer)));
                }else {
                    Toast.makeText(basicHeper.app,basicHeper.app.getString(R.string.device_not_online),Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            default:
                break;
        }

    }

    private void doTimer(DeviceModel.ItemInfo items, boolean hasTimer) {
        //SwitchHelper helperTemp = (SwitchHelper) helper;
        HLog.i(TAG,"items.type:"+items.type);
        mDeviceEntity = DbManager.getInstance(basicHeper.app).queryDeviceyByDeviceId(items.mDId);
        if(null==mDeviceEntity)return;

        if (DeviceHelper.isPlug(mDeviceEntity.mUi) || DeviceHelper.isSwitch(mDeviceEntity.mUi) || DeviceHelper.isWater(mDeviceEntity.mUi)) {
            if(!hasTimer){
                IntentHelper.startAddTimerActivity(getActivity(), mDeviceEntity.mDeviceId, null, -1, Helper.parseType(mDeviceEntity.mUi),DeviceHelper.UI_PLUG_FIRE_PLACE==mDeviceEntity.mUi);

            }else {
                IntentHelper.startNewTimerActvity(getActivity(), mDeviceEntity.mDeviceId,  -1);

            }

        } else if(DeviceHelper.UI_PLUG_FIRE_PLACE==mDeviceEntity.mUi||DeviceHelper.UI_TEMPERATURE_AND_HUMIDITY_KEEPER==mDeviceEntity.mUi){
            IntentHelper.startNewTimerActvity(getActivity(), mDeviceEntity.mDeviceId, -1);
        }
        else{
            IntentHelper.startTimerActvity(basicHeper.app, mDeviceEntity.mDeviceId);
        }
    }

    public void cancleOta(String deviceId) {
        if (!TextUtils.isEmpty(deviceId)) {
            showOtaFinish(deviceId, false);
        }
    }

    public void doOta(DeviceModel.ItemInfo item) {
        final DeviceEntity entity = basicHeper.app.mDbManager.queryDeviceyById(item.mId);
        HLog.i(TAG, "do ota device:"
                + ((null == entity) ? "null" : entity.mName));
        if (null != entity) {
            if (!"true".equals(entity.mOnLine)) {
                Toast.makeText(getActivity(), R.string.lineoff_device, Toast.LENGTH_SHORT).show();
                return;
            }
            if (otaMap.containsKey(entity.mDeviceId)) {
                Toast.makeText(getActivity(), R.string.deviceupdate, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                final JSONObject obj = new JSONObject(entity.mUpdateInfo);

                if (obj.has("version")) {

                    final String version = obj.getString("version");

                    String text = getString(R.string.device_upgraded_to) + version
                            +getString(R.string.info_wait_upgrade) ;

                    mOtaConfireDialog = UiHelper.showConfireDialog("", text,
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    try {

                                        Message msg = uiHandler.obtainMessage(MSG_OTA_ING);
                                        msg.obj = entity.mDeviceId;
                                        HLog.i(TAG,"devicelistfragment_before_otamap:"+otaMap.toString());
                                        otaMap.put(entity.mDeviceId, entity.mName);
                                        HLog.i(TAG,"devicelistfragment_after_otamap:"+otaMap.toString());
                                        msg.sendToTarget();


                                        JSONObject json = new JSONObject();
                                        json.put("action", "upgrade");
                                        json.put("apikey", entity.mApiKey);
                                        json.put("deviceid", entity.mDeviceId);
                                        json.put("userAgent", "app");
                                        json.put("sequence", System.currentTimeMillis() + "");
                                        obj.remove("upgradeText");
                                        json.put("params", obj);
                                        basicHeper.postWsRequest(new WsRequest(
                                                json.toString().replaceAll("\\\\/", "/")) {
                                            @Override
                                            public void callback(String msg) {
                                                HLog.i(TAG,
                                                        "do ota device,msg:"
                                                                + msg);

                                                try {
                                                    JSONObject json = new JSONObject(
                                                            msg);



                                                    boolean failure=json
                                                            .has("error")
                                                            && 0 != json
                                                            .getInt("error");
                                                    HLog.i(TAG,"ota fw:"+entity.mFwVersion+" to "+version+" "+!failure);
                                                    if(!failure){
                                                        entity.mUpdateInfo="";
                                                        entity.mFwVersion=version;
                                                        long result=DbManager.getInstance(getActivity()).updateObject(entity, "mDeviceId", entity.mDeviceId);

                                                    }
                                                    uiHandler.obtainMessage( failure? MSG_OTA_FAIL
                                                            : MSG_OTA_OK, entity.mDeviceId).sendToTarget();
                                                } catch (JSONException e) {
                                                    HLog.e(TAG, e);
                                                } catch (Exception e) {
                                                    HLog.e(TAG,e);
                                                }

                                                super.callback(msg);
                                            }

                                        });
                                        mOtaConfireDialog.dismiss();
                                        mOtaConfireDialog.dismiss();
                                    } catch (JSONException e) {
                                        HLog.e(TAG, e);
                                    }

                                }
                            }, new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    if (null != mOtaConfireDialog) {
                                        mOtaConfireDialog.dismiss();
                                    }

                                }
                            }, getActivity());
                    mOtaConfireDialog.show();

                }
            } catch (Exception e1) {
                HLog.e(TAG, e1);
                if (null != entity) {
                    HLog.e(TAG, entity.mUpdateInfo);
                }
            }

        }
    }


    private void doDele(View arg0) {
        DeviceModel.ItemInfo item = (DeviceModel.ItemInfo) arg0.getTag();
        final DeviceEntity entity = basicHeper.app.mDbManager.queryDeviceyById(item.mId);
        if (entity != null) {
            if (otaMap.containsKey(entity.mDeviceId)) {
                Toast.makeText(getActivity(), getString(R.string.title_update), Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), R.string.upgradeing, Toast.LENGTH_SHORT).show();
                return;
            }
            mDeleteConfireDialog = UiHelper.showConfireDialog("",
                    getString(R.string.info_sure_delete), new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mController.deDelete(entity);
                            if (null != mDeleteConfireDialog) {
                                mDeleteConfireDialog.dismiss();
                            }


                        }
                    }, new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (null != mDeleteConfireDialog) {
                                mDeleteConfireDialog.dismiss();
                            }

                        }
                    }, getActivity());
            mDeleteConfireDialog.show();
            HLog.i(TAG, "show delete dialog");

        } else {
            mAdapt.mData.remove(item);
            mAdapt.refresh();
            HLog.i(TAG, "delete query return  null");
        }

    }

    public boolean onLongClick(View arg0) {
        HLog.i(TAG, " long press");
        return true;
    }

    public void dissMissOta(String id) {
        if (null != mOtaDialog && mOtaDialog.isShowing()) {
            otaMap.remove(id);
            mOtaDialog.dismiss();
        }
    }

    public void showOtaError(String id) {
        String name = (String) otaMap.get(id);
        otaMap.remove(id);

        if(null != getActivity()) {
            UiHelper.showShortToast(getActivity(), getString(R.string.ota_failed_upgrade,name));

        }

        dissMissOta(id);


    }

    public void showOtaOkay(String id) {
        String name = (String) otaMap.get(id);
        otaMap.remove(id);
        if(null != getActivity()) {
            Toast.makeText(getActivity(), getString(R.string.ota_success_upgrade,name), Toast.LENGTH_SHORT).show();
            dissMissOta(id);
        }


    }




    public void showOtaIng(final String id  ) {

        final String name = (String) otaMap.get(id);
        mOtaDialog = DialogHelper.createProgressDialog(getActivity(), getString(R.string.firmware_upgradeing,name));
        mOtaDialog.show();
        mOtaDialog.setCanceledOnTouchOutside(false);
        mOtaDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (otaMap.containsKey(id)) {
                    UiHelper.showShortToast(getActivity(), getString(R.string.background_updateing,name));
                }

            }
        });


    }

    public void showOtaFinish(String obj, boolean show) {

        if (otaMap.containsKey(obj)) {
            HLog.i(TAG, "ota finish overtimer");

            String name = (String) otaMap.get(obj);
            otaMap.remove(obj);
            Context context=getActivity();
            if (show && null !=context ) {

                Toast.makeText(context, context.getString(R.string.ota_finish_upgrade, name),Toast.LENGTH_SHORT).show();


            }
            dissMissOta(obj);
        } else {
            HLog.i(TAG, "ota finish overtimer,but has no key");
        }


    }


    public void refresh() {
        mAdapt.refresh();
    }

    class DeviceListAdapt extends BaseListAdapt<DeviceModel.ItemInfo> {

        private Dialog mDeleteConfireDialog;
        private Activity activity;
        private ImageView time_icon;

        public DeviceListAdapt(List<DeviceModel.ItemInfo> data) {
            mData = data;

        }

        public int getIcon(String type) {
            if ("mall".equals(type)) {
                return R.drawable.mall;

            } else {
                return R.drawable.restrant;
            }
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            final DeviceModel.ItemInfo data = mData.get(arg0);
            View view = null;

            HLog.i(TAG, "get view:" + data.name);
            if (HkConst.ITEM_TYPE_DIVER.equals(data.type)) {
                view = getActivity().getLayoutInflater()
                        .inflate(R.layout.dl_item_diver, null);

            } else if (HkConst.ITEM_TYPE_DEVICE.equals(data.type)) {
                view = getActivity().getLayoutInflater().inflate(R.layout.dl_item_device_new,
                        null);

                ((TextView) view.findViewById(R.id.tv_name)).setText(data.name);
                ((TextView) view.findViewById(R.id.tv_des)).setText(data.des);

                ((ImageView) view.findViewById(R.id.iv_icon))
                        .setImageDrawable(DeviceHelper.getDeviceListIcon(
                                getActivity(), data.deviceType,
                                data.onLine));
                View del = view.findViewById(R.id.tv_del);
                 time_icon = (ImageView) view.findViewById(R.id.time_icon);
                time_icon.setOnClickListener(DeviceListFragment.this);

                List<Timer> list = DbManager.getInstance(basicHeper.app)
                        .queryTimerByDeviceId(data.mDId);

                if(list!=null&&list.size()>0){
                    data.ishastimer=1;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        time_icon.setBackground(getResources().getDrawable(R.drawable.timer_has));
                    } else {
                        time_icon.setImageResource(R.drawable.timer_has);
                    }

                }else{
                    data.ishastimer=0;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        time_icon.setBackground(getResources().getDrawable(R.drawable.timer_not));
                    } else {
                        time_icon.setImageResource(R.drawable.timer_not);
                    }

                }
                time_icon.setTag(data);
                time_icon.setTag(R.id.key_has_timer,((list!=null)&&list.size()>0));

                del.setTag(data);
                del.setOnClickListener(DeviceListFragment.this);

                view.setTag(data);

                if (data.scollUiCount == 2) {
                    View otaBtn = view.findViewById(R.id.tv_ota);
                    otaBtn.setTag(data);
                    otaBtn.setVisibility(View.VISIBLE);
                    otaBtn.setOnClickListener(DeviceListFragment.this);
                }

            }

            view.setTag(data);
            return view;
        }


    }

    class DeviceDropListener implements DragSortListView.DropListener {
        @Override
        public void drop(int from, int to) {
            HLog.i(TAG, "drag from:" + from + "-to：" + to);
            mController.updateToGroup(mAdapt.mData.get(from),
                    to == 0 ? null : mAdapt.mData.get(to > from ? to : to - 1));

        }
    }
}

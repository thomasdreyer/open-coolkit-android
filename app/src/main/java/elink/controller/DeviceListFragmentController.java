package elink.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import elink.common.UiHelper;

import com.coolkit.common.HLog;

import elink.DeviceHelper;
import elink.HkConst;
import elink.common.Helper;
import elink.activity.BaseHelper;
import elink.activity.DeviceActivity;
import elink.activity.DeviceListFragment;
import elink.activity.DialogActivity;
import elink.entity.DeviceEntity;
import elink.entity.Timer;
import elink.manager.OtaManager;
import elink.model.DeviceModel;

import com.coolkit.common.WsRequest;
import com.coolkit.protocol.request.DeviceProtocol;
import com.coolkit.protocol.request.ProtocolHandler;
import com.coolkit.protocol.request.Result;

import elink.utils.NetHelper;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DeviceListFragmentController {

    protected static final String TAG = DeviceListFragmentController.class.getSimpleName();
    private static final int MSG_SYN_LOCAL = 0;

    private static final int MSG_PARSE_REMOTE = MSG_SYN_LOCAL + 1;
    private static final int MSG_DELETE_ENTITY = MSG_PARSE_REMOTE + 1;
    private static final int MSG_CHECK_OTA = MSG_DELETE_ENTITY + 1;
    private final DeviceListFragment mFragment;


    private Handler mHandler;
    Byte lock = (byte) 0;
    public DeviceModel mMode;
    public BaseHelper basicHelper;
    private String mOldAt;
    public DeviceListFragmentController(DeviceActivity context, DeviceListFragment fragment) {
        basicHelper = fragment.basicHeper;

        init();
        mFragment = fragment;
    }


    public void getInstance(DeviceActivity context, DeviceListFragment fragment) {

    }

    private void init() {
        new OperateThread().start();
        mMode = new DeviceModel(this);
        if (mHandler == null) {
            synchronized (lock) {
                if (mHandler == null) {

                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        HLog.i(TAG, "mHandler created,continue");
                    }
                }

            }

        }
    }

    public void synLocal() {
        mHandler.removeMessages(MSG_SYN_LOCAL);
        mHandler.sendEmptyMessage(MSG_SYN_LOCAL);

    }

    public void synUserDevice() {
        HLog.i(TAG,"syn user data");
        HLog.i(TAG,"mUser.at:"+mFragment.basicHeper.app.mUser.at.toString());
        mFragment.basicHeper.postRequest(new Runnable() {

            @Override
            public void run() {
                mOldAt = mFragment.basicHeper.app.mUser.at;
                new DeviceProtocol(mFragment.basicHeper.app.mAppHelper).queryUserDevice(mFragment.basicHeper.app.mAppHelper, mFragment.basicHeper.app.mUser.at,
                        new ProtocolHandler.CallBack() {
                            @Override
                            public void callBack(Result result) {
                                mHandler.removeMessages(MSG_PARSE_REMOTE);
                                mHandler.obtainMessage(MSG_PARSE_REMOTE, result).sendToTarget();
                            }
                        });
            }
        });

    }


    public void handleSynLocal() {
        HLog.i(TAG, "handle sycn local");
        mMode.queryUserDevice();
        mFragment.uiHandler.removeMessages(mFragment.MSG_REFRESH_ADAPT);
        mFragment.uiHandler.obtainMessage(mFragment.MSG_REFRESH_ADAPT, mMode.mUserDevice).sendToTarget();

    }


    public void doCheckDevice() {

        mFragment.basicHeper.postRequest(new Runnable() {

            @Override
            public void run() {

                new OtaManager(basicHelper).doCheckOta();
                doCheckDeviceOnLine();
            }
        });

    }

    public void doCheckDeviceOnLine() {
        HLog.i(TAG, "do check devcie online");
        List<DeviceEntity> device = mFragment.basicHeper.app.mDbManager.getUserDevice("");
        for (DeviceEntity entity : device) {
            JSONObject object = new JSONObject();
            try {
                object.put("action", "query");
                object.put("apikey", entity.mApiKey);
                object.put("deviceid", entity.mDeviceId);
                object.put("userAgent", "app");
                JSONArray array = new JSONArray();
                array.put("any_not_exist_property");
                object.put("params", array);
                mFragment.basicHeper.postWsRequest(new WsRequest(object));
            } catch (JSONException e) {
                HLog.e(TAG, e);
            }

        }

    }


    public void updateToGroup(DeviceModel.ItemInfo sourceDevice, DeviceModel.ItemInfo desGroup) {

    }

    public void deDelete(DeviceEntity entity) {
        mHandler.obtainMessage(MSG_DELETE_ENTITY, entity).sendToTarget();

    }

    public void handleDeleEntity(DeviceEntity entity) {
        mFragment.basicHeper.app.mDbManager.deleteObject(entity,
                "mId", entity.mId);
        Helper.broadcastSynLocalDevice(mFragment.getActivity());

        new DeviceProtocol(mFragment.basicHeper.app.mAppHelper)
                .doDeleteDeviceDetail(
                        entity.mDeviceId, new DeleResponseHandler(mFragment.basicHeper.app),
                        mFragment.basicHeper.app.mUser.at);
    }

    class DeleResponseHandler extends ProtocolHandler {
        public DeleResponseHandler(Context context) {
            super(context);
        }

        @Override
        public void callBack(Result result) {
            super.callBack(result);
            if (result.mCode == 200) {

                try {
                    JSONObject object;
                    object = new JSONObject(
                            result.mMsg);
                    HLog.i(TAG,
                            "delete callback msg:" + result.mMsg);
                    if (!object
                            .has("error")) {
//                        Helper.broadcastSynDevice(mContext);
                    }
                } catch (JSONException e) {
                    HLog.e(TAG,
                            e);
                }
            }
        }


    }


    public void doCallBack(Object obj) {
        if (null != obj) {
            Result result = (Result) obj;
            if (result.action == 0) {

                HLog.i(TAG, "result:" + result);
                HLog.i(TAG, "call back :" + result.mCode);
                if(result.mCode==HttpStatus.SC_OK){
                    NetHelper.saveConnectionInfo(mFragment.getActivity());
                }

                if (result.mCode == HttpStatus.SC_OK
                        && !TextUtils.isEmpty(result.mMsg)) {


                    JSONArray array = null;
                    try {
                        array = new JSONArray(result.mMsg);

                        HLog.i(TAG, "clear device list");
                        mFragment.basicHeper.app.mDbManager.clearEnTiy(
                                DeviceEntity.class.getSimpleName());

                        mFragment.basicHeper.app.mDbManager.clearEnTiy(
                                Timer.class.getSimpleName());
                        if (null != array) {

                            for (int i = 0; i < array.length(); i++) {
                                try {
                                    JSONObject json = array.getJSONObject(i);
                                    DeviceEntity entity = DeviceHelper.getDevice(
                                            json, mFragment.basicHeper.app);

                                    // entity.mGroup="group0";
                                    mFragment.basicHeper.app.mDbManager.inSert(entity);

                                } catch (JSONException e) {
                                    HLog.e(TAG, e);
                                } catch (IllegalAccessException e) {
                                    HLog.e(TAG, e);
                                } catch (IllegalArgumentException e) {
                                    HLog.e(TAG, e);
                                }
                            }

                        }

                        mHandler.removeMessages(MSG_SYN_LOCAL);
                        mHandler.sendEmptyMessage(MSG_SYN_LOCAL);

                        mHandler.removeMessages(MSG_CHECK_OTA);
                        mHandler.sendEmptyMessage(MSG_CHECK_OTA);
                    } catch (JSONException e1) {
                        HLog.e(TAG, e1);
                        if (result.mCode == 200) {
                            JSONObject object = null;
                            try {
                                object = new JSONObject(result.mMsg);
                                int code = object.has("error") ? object.getInt("error") : 0;

                                if (code == 402) {
                                    HLog.e(TAG, "acess tocken over timer");
                                    if(this.basicHelper.app.mUser.isLogin&&this.basicHelper.app.mUser.at.equals(mOldAt)){

                                        this.basicHelper.app.mUser.isLogin = false;
                                        UiHelper.showReLogintDialog(this.basicHelper.app);

                                    }

                                } else if(code==401){

                                    HLog.e(TAG, "user login in other places");

                                    if(this.basicHelper.app.mUser.isLogin&&this.basicHelper.app.mUser.at.equals(mOldAt)){

                                        this.basicHelper.app.mUser.isLogin = false;
                                        UiHelper.showReLogintDialog(this.basicHelper.app);

                                    }
                                }


                            } catch (JSONException e) {
                                HLog.e(TAG, e);
                            }

                        }
                    }


                }
            }
        }
    }

    public void showLogoutDialog() {

        Intent intent = new Intent(basicHelper.app, DialogActivity.class);
        intent.putExtra(HkConst.EXTRA_DIALOG_TYPE, DialogActivity.DIALOG_AT_OVER_TIME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        basicHelper.app.startActivity(intent);
    }





    class OperateThread extends Thread {

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            synchronized (lock) {
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        try {
                            switch (msg.what) {
                                case MSG_PARSE_REMOTE:
                                    doCallBack(msg.obj);
                                    break;
                                case MSG_SYN_LOCAL:
                                    handleSynLocal();
                                    ;
                                    break;
                                case MSG_DELETE_ENTITY:
                                    handleDeleEntity((DeviceEntity) msg.obj);
                                    break;
                                case MSG_CHECK_OTA:

                                    doCheckDevice();
                                    break;

                                default:
                                    break;
                            }
                        } catch (IllegalArgumentException e) {
                            HLog.e(TAG, e);
                        }

                    }
                };
                lock.notifyAll();
            }
            Looper.loop();

        }
    }


}

package elink.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.coolkit.common.HLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import elink.HkApplication;
import elink.entity.DeviceEntity;

public class SystemActionHolder {
    protected static final String TAG = null;
    public Context mContext;


    public interface SystemMsgCallBack {

        boolean doDeviceOnline(String deviceId, boolean onLine,
                               boolean hasGloble, String json);

        public boolean doUpdateParams(String deviceId, String params,
                                      boolean hasGloble, String json);
    }

    List<SystemMsgCallBack> updateCallbacks = new ArrayList<>();


    public SystemActionHolder(Context context) {
        mContext = context;

    }

    private BroadcastReceiver mSystemMsgeReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("json");
            boolean hasGloble = intent.getBooleanExtra("hasGloableProcess",
                    false);
            String action = intent.getAction();
            if ("com.homekit.action.SYSMSG".equals(action)) {

                onReciveSysMsg(json, hasGloble);
            } else if ("com.homekit.action.UPDATE".equals(action)) {
                onReciveUpdate(json, hasGloble);
            }

        }

    };

    public void regesterSysMsg(SystemMsgCallBack callback) {
        updateCallbacks.add(callback);
    }

    public void deStroyReciever(SystemMsgCallBack callback) {
        updateCallbacks.remove(callback);
    }


    public void regesterSysMsg() {
        mContext.registerReceiver(mSystemMsgeReciever, new IntentFilter(
                "com.homekit.action.SYSMSG"));
        mContext.registerReceiver(mSystemMsgeReciever, new IntentFilter(
                "com.homekit.action.UPDATE"));
    }

    public void deStroyReciever() {
        if(mSystemMsgeReciever!=null){
            mContext.unregisterReceiver(mSystemMsgeReciever);
        }
    }


    protected void onReciveUpdate(String json, boolean hasGloble) {

        HLog.i(TAG, " holder got json,onReciveUpdate:" + json);
        if (!TextUtils.isEmpty(json)) {
            JSONObject jsonObj;
            try {
                jsonObj = new JSONObject(json);
                if (jsonObj.has("deviceid") && jsonObj.has("params")) {
                    String deviceId = jsonObj.getString("deviceid");
                    String params = jsonObj.getString("params");
                    HkApplication app = (HkApplication) mContext.getApplicationContext();

                    DeviceEntity entity = app.mDbManager
                            .queryDeviceyByDeviceId(deviceId);

                    HLog.i(TAG, "do update :" + deviceId
                            + "has entity =:" + (entity != null));
                    if (null != entity) {
                        HLog.i(TAG, "entity param before update:" + entity.mParams + " new parma is:" + params);
                        if (entity.mParams == null) {
                            entity.mParams = "";
                        }
                    } else {
                        HLog.i(TAG, "update entity while entity is null");
                    }
                    if (null != entity && !TextUtils.isEmpty(params) && !params.equals(entity.mParams)) {
                        JSONObject old = TextUtils.isEmpty(entity.mParams)?new JSONObject():new JSONObject(entity.mParams);
                        JSONObject newJ = new JSONObject(params);

                        Iterator<String> keys = newJ.keys();
                        while(keys!=null&&keys.hasNext()){
                            String key=keys.next();
                            old.put(key,newJ.get(key));
                        }
                        entity.mParams = old.toString();
                        try {
                            HLog.i(TAG, "find device:" + entity.mId
                                    + "do  doUpdateParams params is:" + entity.mParams);
                            long row = app.mDbManager.updateObject(entity, "mDeviceId", entity.mDeviceId);
                            if (row == 1) {
                                if (null != updateCallbacks) {
                                    HLog.i(TAG, "call back update");

                                    for (SystemMsgCallBack callBack : updateCallbacks) {
                                        callBack.doUpdateParams(deviceId, params, hasGloble, json);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            HLog.e(TAG, e);
                        }
                    }


                }
            } catch (JSONException e) {
                HLog.e(TAG, e);
            }

        }

    }

    protected void onReciveSysMsg(String json, boolean hasGloble) {

        HLog.i(TAG, " holder got json,onReciveSysMsg:" + json);
        if (!TextUtils.isEmpty(json)) {
            JSONObject jsonObj;
            try {
                jsonObj = new JSONObject(json);
                if (jsonObj.has("deviceid") && jsonObj.has("params")) {
                    String deviceId = jsonObj.getString("deviceid");
                    String params = jsonObj.getString("params");
                    JSONObject paramsO = new JSONObject(params);
                    if (paramsO.has("online")) {
                        boolean onLine = paramsO.getBoolean("online");

                        HkApplication app = (HkApplication) mContext.getApplicationContext();
                        DeviceEntity entity = app.mDbManager
                                .queryDeviceyByDeviceId(deviceId);

                        if (null != entity
                                && !entity.mOnLine.equals(String
                                .valueOf(onLine))) {
                            HLog.i(TAG, "find device:" + entity.mId
                                    + " do update online:" + entity.mOnLine + " params is:" + entity.mParams);
                            entity.mOnLine = String.valueOf(onLine);
                            try {
                                long result = app.mDbManager.updateObject(entity, "mDeviceId", entity.mDeviceId);
                                if (result == 1) {
                                    HLog.i(TAG, "up online db okay");
                                    if (null != updateCallbacks) {
                                        HLog.i(TAG, "call back  sys msg");
                                        for (SystemMsgCallBack callBack : updateCallbacks) {
                                            callBack.doDeviceOnline(deviceId, onLine, hasGloble, json);
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                HLog.e(TAG, e);
                            }

                        }else{
                            HLog.i(TAG, "find device null or entity value dont change");
                        }
                    }

                }
            } catch (JSONException e) {
                HLog.e(TAG, e);
            }

        }
    }


}

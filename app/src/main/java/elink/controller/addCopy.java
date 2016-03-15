package elink.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.widget.Toast;

import elink.common.UiHelper;

import demo.demo.R;
import com.coolkit.common.HLog;

import elink.HkConst;
import elink.common.Helper;
import elink.activity.AddDeviceActivity;

import com.coolkit.common.HttpClientHelper;
import com.coolkit.protocol.request.DeviceProtocol;
import com.coolkit.protocol.request.ProtocolHandler;
import com.coolkit.protocol.request.Result;

import elink.utils.NetHelper;
import elink.utils.NetHelper.WifiCipherType;
import elink.utils.SpHelper;
import elink.utils.ThreadExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class addCopy extends BasiController<AddDeviceActivity> {

    protected static final String TAG = addCopy.class.getSimpleName();
    public static final int MSG_OVER_TIMER = 3;
    private List<ScanResult> mAroundDevices = new ArrayList<ScanResult>();
    public String mHomeSSid = "";
    public String mPWD = "";
    private String deviceid;
    private String apikey;
    private String accept;

    private boolean mConnectTOHome = false;
    private ConnectRunnable mRun;
    private BroadcastReceiver mBroadCast;

    private boolean mConnectToVpOkay;

    public String mVpSSID;
    private WifiManager mWifiManager;
    public SpHelper sp = new SpHelper(mContext);


    public addCopy(AddDeviceActivity context) {
        super(context);
    }

    public List<ScanResult> getDevice() {
        return mAroundDevices;
    }

    public List<ScanResult> getAroudDevice() {

        PackageManager pm = this.app.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.INTERNET", this.app.getPackageName()));
        if (permission) {
            HLog.i(TAG, "havePermission");

        } else {
            HLog.i(TAG, "noPermission");
        }


        List<ScanResult> around = new ArrayList<ScanResult>();
        List<ScanResult> aroundAp = new ArrayList<ScanResult>(
                NetHelper.scan(this.app.getWifiManager()));

        for (ScanResult scanResult : aroundAp) {

            if (UiHelper.isIteadDevice(scanResult.SSID)) {
                around.add(scanResult);
            }

        }
        return around;
    }


    public void connectTOVp(String ssid) {
        HLog.i(TAG, "connect to vp:" + ssid);
        mVpSSID = ssid;
        mConnectToVpOkay = false;

        mWifiManager = app.getWifiManager();

//        /**
//         * TODO check wificipher type
//         */

        if (null != mRun) {
            mRun.stop = true;
            HLog.i(TAG, "stop old connect thread");
        }

        mRun = new ConnectRunnable(ssid, HkConst.SETTING_WIFI_PSW, WifiCipherType.WIFICIPHER_WPA,
                this.app.getWifiManager(), mContext);
        Thread thread = new Thread(mRun);
        thread.start();


    }


    public void doRegister() {
        mBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HLog.i(TAG, "on reciever connection change");
                addCopy.this.onReceive(intent);
            }
        };

        mContext.registerReceiver(mBroadCast, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


    }

    private void onReceive(Intent intent) {
        if (null == intent) {
            HLog.i(TAG, "on reciever but the intent is null");
            return;
        }
        HLog.i(TAG, "on recieve,mConnectTOHome:" + mConnectTOHome);
        WifiManager wifimanager = this.app.getWifiManager();
        WifiInfo wifimanagerConnectionInfo = wifimanager.getConnectionInfo();


        Parcelable par = intent
                .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);


        if (par != null && wifimanagerConnectionInfo != null) {
            NetworkInfo activityNetworkInfo = (NetworkInfo) par;
            NetworkInfo.DetailedState detailedState = activityNetworkInfo.getDetailedState();

            String connectedSsid = wifimanagerConnectionInfo.getSSID();
            HLog.e(TAG, "ssid is:" + connectedSsid + " mvpssid is:" + mVpSSID + " mHomessid is:" + mHomeSSid + " subTpe " + activityNetworkInfo.getSubtypeName() + " state()" + activityNetworkInfo.getState() + " detailedState()"
                    + (detailedState == null ? "null" : detailedState.name()) + " getExtra()" + activityNetworkInfo.getExtraInfo());


            if ((NetworkInfo.State.CONNECTED == activityNetworkInfo.getState()) && (detailedState == NetworkInfo.DetailedState.CONNECTED)) {

                //CONNECTTED TO DEVICE
                if (activityNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI && (mVpSSID != null) && (("\"" + mVpSSID + "\"").equals(connectedSsid) || (mVpSSID.equals(connectedSsid)))) {
                    mConnectToVpOkay = true;
                    mConnectTOHome = false;

                    HLog.i(TAG, "connect to device ssid:" + connectedSsid + " has pending：" + mPendingSendConfig);
                    if (mPendingSendConfig) {
                        mPendingSendConfig = false;
                        exePendingSendConfig();

                        return;
                    }

                } else
                //connect to home wlan
                {
                    //when you start add device activity ,may reach here
                    if (mConnectToVpOkay) {
                        if (!mConnectTOHome) {
                            mConnectToVpOkay = false;


                            HLog.i(TAG, "set connect to home wlan in 2500");
                            resultHandler.sendEmptyMessageDelayed(2, 2500);
                        } else {
                            HLog.i(TAG, "has alread set connect to home wlan");

                        }
                    } else {
                        HLog.i(TAG, "has not connectted to a  device vp");

                    }


                }
            } else {
                HLog.i(TAG, "connnect is not connected :" + activityNetworkInfo.getSubtypeName());
            }
        } else {
            HLog.i(TAG, "on reciever change ,but activityNetworkInfo info is null:" + (null == intent) + " intent extra is null:" + (null == wifimanagerConnectionInfo.getSSID()));

        }

    }


    private boolean mPendingSendConfig;
    private String mPengingName;

    // { "deviceid":"10 digits", "apikey":"uuid goes here", "accept": "post" }
    public void connectToDevice(final String url) {


        final ProtocolHandler pHandler = new ProtocolHandler(mContext, 0, new ProtocolHandler.CallBack() {

            @Override
            public void callBack(Result result) {
                if (result.mCode == 6000) {
                    mContext.mHandler.sendEmptyMessageDelayed(AddDeviceActivity.MSG_HOST_EXCEPTION, 1000);
                } else if (result.mCode != 200) {
                    Toast.makeText(mContext,
                            mContext.getString(R.string.connect_failed),
                            Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mContext.dismissSendConfigdialog();
                            mContext.finish();
                        }
                    }, 800);
                } else {
                    try {
                        JSONObject json = new JSONObject(result.mMsg);

                        HLog.i(TAG, "connect to device rsutl:" + result.mMsg);


                        deviceid = !json.has("deviceid") ? "" : json
                                .getString("deviceid");
                        apikey = !json.has("apikey") ? "" : json
                                .getString("apikey");

                        accept = !json.has("accept") ? "" : json
                                .getString("accept");

                        HLog.i(TAG, "addCopy:" + deviceid + "," + mHomeSSid + "  mPwd:" + mPWD);
//                        sp.saveSSIDPwd(deviceid,mHomeSSid,mPWD);
                        int gateWay = app.getWifiManager().getDhcpInfo().gateway;
                        String gateWayStr = Formatter.formatIpAddress(gateWay);
                        String url = "http://" + gateWayStr + "/ap";
                        HLog.i(TAG, "gateWayStr: " + gateWayStr);
                        if ("post".equals(accept)) {
                            doPostInfo(url);
                        } else {
                            doGetInfo(url);
                        }


                    } catch (JSONException e) {
                        HLog.e(TAG, e);
                    }
                }

            }
        });
        ThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                new DeviceProtocol(mContext.app.mAppHelper).connectToDevice(url, pHandler);

            }
        });

    }

    public void doPostInfo(final String url) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                HLog.i(TAG, "doPostInfo msg :" + " wifi is :mSSID="
                        + mHomeSSid + "&p" + "wd=" + mPWD);
                String msg = "";
                try {

                    msg = HttpClientHelper.postInfo(url, mHomeSSid, mPWD, mContext.app.mAppHelper.mHost.mDispatchServer, mContext.app.mAppHelper.mHost.mDeviceDispatchPort);
                    ;


                    mContext.DoAfterConnectToDevice(mVpSSID, 0, msg);
                } catch (Exception e) {
                    HLog.i(TAG, "doPostInfo msg exception");


                    mContext.DoAfterConnectToDevice(mVpSSID, 2, "");
                    HLog.e(TAG, e);
                }

            }
        }).start();
    }

    public void doGetInfo(final String url) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                HLog.i(TAG, "do getinfo  msg :" + " wifi is :mSSID="
                        + mHomeSSid + "&p" + "wd=" + mPWD);

                String newUrl = url + "?ssid=" + mHomeSSid + "&password="
                        + mPWD + "&serverName=" + mContext.app.mAppHelper.mHost.mDispatchServer + "&port=" + mContext.app.mAppHelper.mHost.mDeviceDispatchPort;

                HLog.i(TAG, "do doGetInfo " + newUrl);
                String msg = "";
                try {

                    msg = HttpClientHelper.getInfo(newUrl);
                    // msg = HttpClientHelper
                    // .postInfo(url, "\"Coolkit\"", "\"mqtt.coap\"");

                } catch (Exception e) {
                    HLog.i(TAG, "do get msg exception");

                }


                mContext.DoAfterConnectToDevice(mVpSSID, 0, msg);

            }
        }).start();

    }

    public void unRegister() {
        mContext.unregisterReceiver(mBroadCast);
    }

    public void saveCurrentWlan() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netWork = connectivityManager.getActiveNetworkInfo();
//        netWork.

    }

    //注册成功
    class Regester extends ProtocolHandler {

        public Regester() {
            super(mContext);


        }

        @Override
        public void callBack(Result result) {
            super.callBack(result);

            if (0 == result.action) {
                mContext.dissmissRegistingDialog();
                if (200 == result.mCode) {

                    if (!TextUtils.isEmpty(result.mMsg)) {
                        JSONObject json;
                        try {
                            json = new JSONObject(result.mMsg);
                            if (json.has("error")) {
                                mContext.finish();
                                Toast.makeText(mContext, json.getString("error"),
                                        Toast.LENGTH_SHORT).show();
                                resultHandler.sendEmptyMessage(1);
                            } else {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.register_success),
                                        Toast.LENGTH_SHORT).show();
                                sp.saveSSIDPwd(deviceid, mHomeSSid, mPWD);
                                HLog.i(TAG, "send sync broa");
                                resultHandler.sendEmptyMessageDelayed(0, 1000);

                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else if (4000 == result.mCode) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.switch_net_error),
                                Toast.LENGTH_SHORT).show();
                        mContext.finish();
                    }
                } else {
                    mContext.finish();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.add_failed),
                            Toast.LENGTH_SHORT).show();
                }
                HLog.i(TAG, "action is:" + result + " msg :" + result.mMsg);
            }
        }
    }

    public void regesterDevice(final String name) {
        if (mConnectTOHome) {
            HLog.i(TAG, "has already connect to home wlan,do register");
            mContext.postRequest(new Runnable() {

                @Override
                public void run() {
                    new DeviceProtocol(mContext.app.mAppHelper).doAddDevice(new Regester(), deviceid, apikey, name,
                            app.mUser.at);

                }
            });
        } else {
            HLog.i(TAG, "pending register to user device ");
            mPengingName = name;

        }

    }

    public Handler resultHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Helper.broadcastSynDevice(mContext);
                    mContext.finish();
                    mContext.dissmissRegistingDialog();
                    if (null != mContext.mConfigOkayDialog
                            && mContext.mConfigOkayDialog.isShowing()) {
                        mContext.mConfigOkayDialog.dismiss();
                    }
                    break;
                case 1:
                    mContext.dissmissRegistingDialog();
                    Helper.broadcastSynDevice(mContext);
                    mContext.finish();
                    if (null != mContext.mConfigOkayDialog
                            && mContext.mConfigOkayDialog.isShowing()) {
                        mContext.mConfigOkayDialog.dismiss();
                    }
                    ;
                    break;
                case 2: {
                    mConnectTOHome = true;
                    HLog.i(TAG, "connect to home ssid okay,has pending register :" + " pending name :" + mPengingName);
                    if (!TextUtils.isEmpty(mPengingName)) {
                        HLog.i(TAG, "go to do register");

                        mContext.postRequest(new Runnable() {

                            @Override
                            public void run() {

                                new DeviceProtocol(mContext.app.mAppHelper).doAddDevice(new Regester(), deviceid, apikey,
                                        mPengingName, app.mUser.at);
                                mPengingName = "";

                            }
                        });


                    }


                }
                break;
                case MSG_OVER_TIMER: {
                    if (!mContext.isDestroy) {
                        HLog.i(TAG, "has not register yet ,finish adding ");

                        Toast.makeText(mContext, mContext.getResources().getString(R.string.send_config_over_time), Toast.LENGTH_LONG).show();
                        mContext.finish();
                    }

                }
                default:
                    break;
            }

        }
    };


    /**
     * 注册设备成功
     */
    public void callBack(Result result) {

        if (0 == result.action) {
            if (200 == result.mCode) {
                if (!TextUtils.isEmpty(result.mMsg)) {
                    JSONObject json;
                    try {
                        json = new JSONObject(result.mMsg);
                        if (json.has("error")) {
                            mContext.finish();
                            Toast.makeText(mContext, json.toString(),
                                    Toast.LENGTH_SHORT).show();
                            resultHandler.sendEmptyMessage(1);
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.register_success),
                                    Toast.LENGTH_SHORT).show();
                            HLog.i(TAG, "send sync broa");
                            resultHandler.sendEmptyMessageDelayed(0, 1000);

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            } else {
                mContext.finish();
                Toast.makeText(mContext, mContext.getString(R.string.add_failed),
                        Toast.LENGTH_SHORT).show();
            }
            HLog.i(TAG, "action is:" + result + " msg :" + result.mMsg);
        }

    }


    public void exePendingSendConfig() {
        int gateWay = app.getWifiManager().getDhcpInfo().gateway;
        String gateWayStr = Formatter.formatIpAddress(gateWay);
        String url = "http://" + gateWayStr + "/device";
        HLog.i(TAG, "do penging sending config url is " + url);

        connectToDevice(url);
    }

    public void sendConfigInfo() {
        if (!mConnectToVpOkay) {
            mPendingSendConfig = true;
            HLog.i(TAG, "mConnectToVpOkay is not okay ,pengd sending config ");
            return;
        }
        int gateWayIp = app.getWifiManager().getDhcpInfo().gateway;
        HLog.i(TAG, "gateWayIp:" + gateWayIp);
        String gateWayStr = Formatter.formatIpAddress(gateWayIp);
        String url = "http://" + gateWayStr + "/device";
        HLog.i(TAG, "url is " + url);
        connectToDevice(url);

    }


    static class
    ConnectRunnable implements Runnable {
        private String ssid;

        private String password;

        private WifiCipherType type;
        WifiManager mWifiManager;

        Handler mHandler;
        Context mContext;
        boolean stop = false;


        public ConnectRunnable(String ssid, String password,
                               WifiCipherType type, WifiManager wifiManager,
                               Context context) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
            mWifiManager = wifiManager;

            mContext = context;
        }

        @Override
        public void run() {


            HLog.i(TAG, "try to change connect to a  ssid:" + ssid);
            // 打开wifi
            NetHelper.openWifi(mWifiManager);

            // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
            // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
            while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING && !stop) {
                try {
                    // 为了避免程序一直while循环，让它睡个100毫秒检测……
                    Thread.sleep(2000);
                    HLog.i(TAG,
                            "sleep for wifi state enable after open wifi");
                } catch (InterruptedException ie) {
                }
            }
            if (!stop) {
                mWifiManager.startScan();

                WifiConfiguration wifiConfig = NetHelper.createWifiInfo(ssid, password,
                        type);
                //
                if (wifiConfig == null) {
                    HLog.i(TAG, "wifiConfig is null!");
                    return;
                }

                WifiConfiguration tempConfig = NetHelper.isExsits(ssid, mWifiManager);

                if (tempConfig != null) {
                    HLog.i(TAG, "remove old config of new ssid");
                    mWifiManager.removeNetwork(tempConfig.networkId);
                }

                int netID = mWifiManager.addNetwork(wifiConfig);
                boolean enabled = mWifiManager.enableNetwork(netID, true);
                boolean connected =mWifiManager.reassociate();

                HLog.i(TAG, "enable:" + enabled + ", connected:" + connected );


            } else {
                HLog.i(TAG, "connection thread is stopped");
            }


        }

    }

    public void connectToHomeSSID() {
        ThreadExecutor.execute(new Runnable() {


            @Override
            public void run() {
                HLog.i(TAG, "start connect home ssid thread");

                WifiManager wifiManager = app.getWifiManager();
                wifiManager.startScan();

                String homeSsid = app.mSp.getHomeSSID();
                HLog.i(TAG, "home ssid is:" + homeSsid);
                ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activityNetworkInfo = connectivityManager.getActiveNetworkInfo();
                WifiInfo wifimanagerConnectionInfo = wifiManager.getConnectionInfo();


                if (activityNetworkInfo != null && wifimanagerConnectionInfo != null && (ConnectivityManager.TYPE_WIFI == activityNetworkInfo.getType())) {

                    String connectedSsid = wifimanagerConnectionInfo.getSSID();
                    HLog.i(TAG, "connectted ssid is:" + connectedSsid);

                    if (!TextUtils.isEmpty(connectedSsid)) {
                        if ((("\"" + connectedSsid + "\"").equalsIgnoreCase(homeSsid)) || ((connectedSsid.equalsIgnoreCase(homeSsid)))) {
                            HLog.i(TAG, " has already connect to home ssid ");
                            return;

                        }
                    }

                }


                List<WifiConfiguration> list = wifiManager
                        .getConfiguredNetworks();
                boolean hasConnectionInfo = false;

                if (null != list) {

                    for (WifiConfiguration wifiConfiguration : list) {
                        if (!TextUtils.isEmpty(homeSsid) && ((("\"" + homeSsid + "\"").equalsIgnoreCase(wifiConfiguration.SSID)) || ((homeSsid.toUpperCase()).equalsIgnoreCase(wifiConfiguration.SSID)))) {
                            HLog.i(TAG, "try to enable home ssid:" + wifiConfiguration.SSID);
                            wifiManager.enableNetwork(
                                    wifiConfiguration.networkId, true);
                            wifiManager.reconnect();
                            hasConnectionInfo = true;

                            break;
                        }
                    }
                }
                HLog.i(TAG, "try to enable home ssid,result is:" + hasConnectionInfo);
                if (hasConnectionInfo) {

                } else {

                }

            }
        });

    }

}
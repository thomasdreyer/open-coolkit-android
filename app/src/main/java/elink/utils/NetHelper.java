package elink.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.coolkit.common.HLog;

import elink.common.UiHelper;
import elink.HkApplication;
import elink.activity.AddDeviceActivity;

import java.util.List;


public class NetHelper {

    private static final String TAG = AddDeviceActivity.class.getSimpleName();

    public static List<ScanResult> scan(WifiManager wifiManager) {
        openWifi(wifiManager);

        wifiManager.startScan();

        HLog.i(TAG, "openWifi(wifiManager)" );
        List<ScanResult> list = wifiManager.getScanResults();


//        list = wifiManager.getScanResults();

        HLog.i("AddDeviceActivity", "scan result is:" + ((null == list) ? "null" : list.size()));
//         if (null != list) {
//         for (ScanResult scanResult : list) {
//         HLog.i("ScanResult", "scan result i:" + "scanResult.bsd="
//         + scanResult.BSSID + "&ssid=" + scanResult.SSID
//         + "&cap=" + scanResult.capabilities
//         + "&scanResult.frequecey=" + scanResult.frequency+"level:"+scanResult.level);
//         }
//         }
        return list;
    }

    public static void saveConnectionInfo(Activity activity) {
        if(null!=activity){
            HkApplication app=(HkApplication)activity.getApplicationContext();

            WifiManager wifimanager = app.getWifiManager();
            WifiInfo wifimanagerConnectionInfo = wifimanager.getConnectionInfo();

            ConnectivityManager connectivityManager = (ConnectivityManager) activity
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activityNetworkInfo = connectivityManager.getActiveNetworkInfo();


            if (activityNetworkInfo != null && wifimanagerConnectionInfo != null&&(ConnectivityManager.TYPE_WIFI==activityNetworkInfo.getType())) {

                String connectedSsid = wifimanagerConnectionInfo.getSSID();


                if ((NetworkInfo.State.CONNECTED == activityNetworkInfo.getState()) && (activityNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED)) {
                    HLog.i(TAG, "save connectedSsid : "+connectedSsid);

                    if(!TextUtils.isEmpty(connectedSsid)){
                        app.mSp.saveHomeSSID(connectedSsid);
                    }
                }else{
                    HLog.i(TAG, "wifi no connected ,no need to save connection ssid");

                }
            }
        }



    }


    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    // 提供一个外部接口，传入要连接的无线网
    public static void connect(String ssid, String password,
                               WifiCipherType type, WifiManager wifiManager, Handler callBack,
                               Context context) {
        Thread thread = new Thread(new ConnectRunnable(ssid, password, type,
                wifiManager, callBack, context));
        thread.start();
    }

    // 查看以前是否也配置过这个网络
    public static WifiConfiguration isExsits(String SSID,
                                             WifiManager wifiManager) {
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        if(!existingConfigs.isEmpty()){
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    public static WifiConfiguration createWifiInfo(String SSID,
                                                   String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // nopass
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wep
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wpa
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }



    // 打开wifi功能
    public static boolean openWifi(WifiManager wifiManager) {
        boolean bRet = true;

        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
            HLog.i(TAG, "open wifi:" + bRet);
        }
        return bRet;
    }

    static class
            ConnectRunnable implements Runnable {
        private String ssid;

        private String password;

        private WifiCipherType type;
        WifiManager mWifiManager;

        Handler mHandler;
        Context mContext;

        private boolean mConnectToVpOkay;

        public ConnectRunnable(String ssid, String password,
                               WifiCipherType type, WifiManager wifiManager, Handler callBack,
                               Context context) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
            mWifiManager = wifiManager;
            mHandler = callBack;
            mContext = context;
        }

        @Override
        public void run() {


            ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();

            int ip = mWifiManager.getConnectionInfo().getIpAddress();
            int gateWay = mWifiManager.getDhcpInfo() == null ? -1 : mWifiManager.getDhcpInfo().gateway;


            String gateWayStr = Formatter.formatIpAddress(gateWay);
            String ipStr = Formatter.formatIpAddress(ip);
            String infoEtr = null == connectivityManager.getActiveNetworkInfo() ? " connect info null"
                    : connectivityManager.getActiveNetworkInfo().getExtraInfo();

            HLog.i(TAG, "start Connect to ssid:" + ssid + " current  ssid :"
                    + mWifiManager.getConnectionInfo().getSSID()
                    + " current wifi state is:" + mWifiManager.getWifiState()
                    + " ip is:" + ip + " " + ipStr + " gateway is:" + gateWay
                    + " " + gateWayStr + " etra is:" + infoEtr);

            if ((mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
                    && mWifiManager.getConnectionInfo().getSSID()
                    .equals("\"" + ssid + "\"")
                    && (ip != 0)
                    && gateWay != 0
                    && null != connectivityManager.getActiveNetworkInfo()
                    && null != connectivityManager.getActiveNetworkInfo()
                    && connectivityManager.getActiveNetworkInfo()
                    .getExtraInfo().contains(ssid)) {
                {
                    HLog.i(TAG, "has alreadly connect to ssid:"
                            + ssid
                            + " gateway is:"
                            + gateWayStr
                            + " ip is:"
                            + ipStr
                            + " etra is:"
                            + connectivityManager.getActiveNetworkInfo()
                            .getExtraInfo());
                    mConnectToVpOkay = true;

                }
            }
            int i = 10;
            if (!mConnectToVpOkay) {
                HLog.i(TAG, "try to change connect to a  ssid:" + ssid);
                // 打开wifi
                openWifi(mWifiManager);

                // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
                // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
                while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                    try {
                        // 为了避免程序一直while循环，让它睡个100毫秒检测……
                        Thread.sleep(2000);
                        HLog.i(TAG,
                                "sleep for wifi state enable after open wifi");
                    } catch (InterruptedException ie) {
                    }
                }

                WifiConfiguration wifiConfig = createWifiInfo(ssid, password,
                        type);
                //
                if (wifiConfig == null) {
                    HLog.i(TAG, "wifiConfig is null!");
                    return;
                }

                WifiConfiguration tempConfig = isExsits(ssid, mWifiManager);

                if (tempConfig != null) {
                    HLog.i(TAG, "remove old config of new ssid");
                    mWifiManager.removeNetwork(tempConfig.networkId);
                }

                int netID = mWifiManager.addNetwork(wifiConfig);
                boolean enabled = mWifiManager.enableNetwork(netID, true);
                boolean connected = mWifiManager.reconnect();

                ip = mWifiManager.getConnectionInfo().getIpAddress();
                gateWay = mWifiManager.getDhcpInfo() == null ? -1 : mWifiManager.getDhcpInfo().gateway;
                ;
                gateWayStr = Formatter.formatIpAddress(gateWay);
                ipStr = Formatter.formatIpAddress(ip);
                infoEtr = null == connectivityManager.getActiveNetworkInfo() ? " connect info  null"
                        : connectivityManager.getActiveNetworkInfo()
                        .getExtraInfo();

                HLog.i(TAG,
                        "reconnect to:" + ssid + " current  ssid :"
                                + mWifiManager.getConnectionInfo().getSSID()
                                + " current wifi state is:"
                                + mWifiManager.getWifiState() + " ip is:" + ip
                                + " " + ipStr + " gateway is:" + gateWay + " "
                                + gateWayStr + " etra is:" + infoEtr);

                boolean w = true;


                while (w) {
                    try {
                        Thread.sleep(2000);
                        HLog.i(TAG, "sleep for ssid:" + ssid + " stable");
                    } catch (InterruptedException e) {
                        HLog.e(TAG, e);
                    }
                    info = connectivityManager.getActiveNetworkInfo();
                    try {
                        w = ((info == null || !info.getExtraInfo().contains(ssid)) && i-- > 0)
                                || (mWifiManager.getConnectionInfo().getIpAddress() == 0)
                                || (mWifiManager.getDhcpInfo() == null) || (mWifiManager.getDhcpInfo().gateway == 0);
                    } catch (Exception e) {
                        HLog.e(TAG, e);
                        w = true;
                    }
                }
            }

            ip = mWifiManager.getConnectionInfo().getIpAddress();


            gateWay = mWifiManager.getDhcpInfo().gateway;
            gateWayStr = Formatter.formatIpAddress(gateWay);
            ipStr = Formatter.formatIpAddress(ip);
            infoEtr = null == connectivityManager.getActiveNetworkInfo() ? " connect info  null"
                    : connectivityManager.getActiveNetworkInfo().getExtraInfo();

            HLog.i(TAG, "after stable try to:" + ssid + " current  ssid :"
                    + mWifiManager.getConnectionInfo().getSSID()
                    + " current wifi state is:" + mWifiManager.getWifiState()
                    + " ip is:" + ip + " " + ipStr + " gateway is:" + gateWay
                    + " " + gateWayStr + " etra is:" + infoEtr
                    + " left retry able:" + i);
            mHandler.sendEmptyMessage(0);

        }

//			ConnectivityManager connectivityManager = (ConnectivityManager) mContext
//					.getSystemService(Context.CONNECTIVITY_SERVICE);
//			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
//
//			int ip = mWifiManager.getConnectionInfo().getIpAddress();
//			int gateWay = mWifiManager.getDhcpInfo()==null?-1:mWifiManager.getDhcpInfo().gateway;
//			String gateWayStr = Formatter.formatIpAddress(gateWay);
//			String ipStr = Formatter.formatIpAddress(ip);
//			String infoEtr = null == connectivityManager.getActiveNetworkInfo() ? " connect info null"
//					: connectivityManager.getActiveNetworkInfo().getExtraInfo();
//
//			HLog.i(TAG, "start Connect to ssid:" + ssid + " current  ssid :"
//					+ mWifiManager.getConnectionInfo().getSSID()
//					+ " current wifi state is:" + mWifiManager.getWifiState()
//					+ " ip is:" + ip + " " + ipStr + " gateway is:" + gateWay
//					+ " " + gateWayStr + " etra is:" + infoEtr);
//
//			if ((mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
//					&& mWifiManager.getConnectionInfo().getSSID()
//							.equals("\"" + ssid + "\"")
//					&& (ip != 0)
//					&& gateWay != 0
//					&& null != connectivityManager.getActiveNetworkInfo()
//					&& null != connectivityManager.getActiveNetworkInfo()
//					&& connectivityManager.getActiveNetworkInfo()
//							.getExtraInfo().contains(ssid)) {
//				{
//					HLog.i(TAG, "has alreadly connect to ssid:"
//							+ ssid
//							+ " gateway is:"
//							+ gateWayStr
//							+ " ip is:"
//							+ ipStr
//							+ " etra is:"
//							+ connectivityManager.getActiveNetworkInfo()
//									.getExtraInfo());
//					mConnectToVpOkay = true;
//
//				}
//			}
//			int i = 10;
//			if (!mConnectToVpOkay) {
//				HLog.i(TAG, "try to change connect to a  ssid:" + ssid);
//				// 打开wifi
//				openWifi(mWifiManager);
//
//				// 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
//				// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
//				while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
//					try {
//						// 为了避免程序一直while循环，让它睡个100毫秒检测……
//						Thread.sleep(2000);
//						HLog.i(TAG,
//								"sleep for wifi state enable after open wifi");
//					} catch (InterruptedException ie) {
//					}
//				}
//
//				WifiConfiguration wifiConfig = createWifiInfo(ssid, password,
//						type);
//				//
//				if (wifiConfig == null) {
//					HLog.i(TAG, "wifiConfig is null!");
//					return;
//				}
//
//				WifiConfiguration tempConfig = isExsits(ssid, mWifiManager);
//
//				if (tempConfig != null) {
//					HLog.i(TAG, "remove old config of new ssid");
//					mWifiManager.removeNetwork(tempConfig.networkId);
//				}
//
//				int netID = mWifiManager.addNetwork(wifiConfig);
//				boolean enabled = mWifiManager.enableNetwork(netID, true);
//				boolean connected = mWifiManager.reconnect();
//
//				ip = mWifiManager.getConnectionInfo().getIpAddress();
//				gateWay = mWifiManager.getDhcpInfo()==null?-1:mWifiManager.getDhcpInfo().gateway;;
//				gateWayStr = Formatter.formatIpAddress(gateWay);
//				ipStr = Formatter.formatIpAddress(ip);
//				infoEtr = null == connectivityManager.getActiveNetworkInfo() ? " connect info  null"
//						: connectivityManager.getActiveNetworkInfo()
//								.getExtraInfo();
//
//				HLog.i(TAG,
//						"reconnect to:" + ssid + " current  ssid :"
//								+ mWifiManager.getConnectionInfo().getSSID()
//								+ " current wifi state is:"
//								+ mWifiManager.getWifiState() + " ip is:" + ip
//								+ " " + ipStr + " gateway is:" + gateWay + " "
//								+ gateWayStr + " etra is:" + infoEtr);
//
//				boolean w=true;
//
//
//				while (w) {
//					try {
//						Thread.sleep(2000);
//						HLog.i(TAG, "sleep for ssid:" + ssid + " stable");
//					} catch (InterruptedException e) {
//						HLog.e(TAG, e);
//					}
//					info = connectivityManager.getActiveNetworkInfo();
//					try
//					{
//						w=((info == null || !info.getExtraInfo().contains(ssid)) && i-- > 0)
//								|| (mWifiManager.getConnectionInfo().getIpAddress() == 0)
//								||(mWifiManager.getDhcpInfo()==null)|| (mWifiManager.getDhcpInfo().gateway == 0);
//					}catch (Exception e){
//						HLog.e(TAG,e);
//						w=true;
//					}
//				}
//			}
//
//			ip = mWifiManager.getConnectionInfo().getIpAddress();
//
//
//			gateWay = mWifiManager.getDhcpInfo().gateway;
//			gateWayStr = Formatter.formatIpAddress(gateWay);
//			ipStr = Formatter.formatIpAddress(ip);
//			infoEtr = null == connectivityManager.getActiveNetworkInfo() ? " connect info  null"
//					: connectivityManager.getActiveNetworkInfo().getExtraInfo();
//
//			HLog.i(TAG, "after stable try to:" + ssid + " current  ssid :"
//					+ mWifiManager.getConnectionInfo().getSSID()
//					+ " current wifi state is:" + mWifiManager.getWifiState()
//					+ " ip is:" + ip + " " + ipStr + " gateway is:" + gateWay
//					+ " " + gateWayStr + " etra is:" + infoEtr
//					+ " left retry able:" + i);
//			mHandler.sendEmptyMessage(0);
//
//		}
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }

    public static boolean isConnnected(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (null != connectivityManager) {
//            NetworkInfo networkInfo[] = connectivityManager.getAllNetworkInfo();
//
//            if (null != networkInfo) {
//                for (NetworkInfo info : networkInfo) {
//                    if (info.getState() == NetworkInfo.State.CONNECTED) {
//                        HLog.e(TAG, "the net is ok");
//                        return true;
//                    }
//                }
//            }
//        }
        return isDetailConnnected(context);
    }


    public static boolean isDetailConnnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivityManager) {
            NetworkInfo networkInfo[] = connectivityManager.getAllNetworkInfo();

            if (null != networkInfo) {
                for (NetworkInfo info : networkInfo) {
                    if (info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                        HLog.e(TAG, "the net is ok");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
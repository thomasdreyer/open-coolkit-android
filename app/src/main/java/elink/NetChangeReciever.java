package elink;

import elink.common.UiHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.coolkit.WebSocketManager;
import com.coolkit.common.HLog;

public class NetChangeReciever extends BroadcastReceiver {


    private static final String TAG = NetChangeReciever.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean needToActiveWs = false;
        String ssid = "";
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if(null!=networkInfos){
                for (int i = 0; i < networkInfos.length; i++) {
                    NetworkInfo.State state = networkInfos[i].getState();
                    if (NetworkInfo.State.CONNECTED == state) {

                        try {
                            ssid = ((HkApplication) context.getApplicationContext()).getWifiManager().getConnectionInfo().getSSID();
                            if(null!=ssid){
                                needToActiveWs = !UiHelper.isIteadDevice(ssid) && !(UiHelper.isIteadDevice("\"" + ssid + "\""));
                            }
                        } catch (Exception e) {
                            needToActiveWs = false;
                        }


                    }
                }
            }
        }
        HLog.i(TAG, "----------Network change ,current ssid:" + ssid + (needToActiveWs ? " net okay need to active network" : " net unable no need to active network"));
        if (needToActiveWs) {
            HkApplication app=(HkApplication)context.getApplicationContext();
            WebSocketManager.getInstance(app.mAppHelper).activeWs(app.mAppHelper.isLogin,app.mUser.apikey,app.mUser.at
            );

        }

    }

}

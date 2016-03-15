package elink;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by app on 15/9/22.
 */
public class TeleManager {
    public   String mImei="0000000000000";
    public String mModel = TextUtils.isEmpty(Build.MODEL) ? "model-none" : Build.MODEL+android.os.Build.MANUFACTURER;
    public String mOsVersion = TextUtils.isEmpty(android.os.Build.VERSION.RELEASE) ? "0" : android.os.Build.VERSION.RELEASE;
    private Context mContext;

    public TeleManager(Context context) {
        mContext = context;
        setImei();

    }

    public void setImei() {
        TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            mImei = manager.getDeviceId();
        } catch (Exception e) {

        }
//        String imei_sim1 = TelephonyManagerEx.getDeviceId(PhoneConstants.GEMINI_SIM_1);

//        String imei_sim2 = TelephonyManagerEx.getDeviceId(PhoneConstants.GEMINI_SIM_2);

    }
}


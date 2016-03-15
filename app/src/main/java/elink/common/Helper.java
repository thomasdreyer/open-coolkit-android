package elink.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.coolkit.Auth;
import demo.demo.R;
import com.coolkit.common.HLog;

import elink.DeviceHelper;
import elink.HkApplication;
import elink.HkConst;
import elink.activity.BaseHelper;
import elink.activity.BasicActivity;
import elink.entity.DeviceEntity;
import com.coolkit.protocol.request.DeviceProtocol;
import com.coolkit.protocol.request.ProtocolHandler;
import com.coolkit.protocol.request.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

public class Helper {

    private static final String TAG = Helper.class.getSimpleName();

    public static void broadcastSynDevice(Context context) {
        Intent inten=new Intent("com.homekit.action.SYNC_DEVICE");
        inten.putExtra("appid",context.getApplicationContext().hashCode());
        context.sendBroadcast(inten);
    }

    public static void broadcastSynDeviceDetail(Context context) {
        Intent inten=new Intent("com.homekit.action.SYNC_DEVICE_DETAIL");
        inten.putExtra("appid",context.getApplicationContext().hashCode());

        context.sendBroadcast(inten);
    }

    public static void broadcastSynDeviceStates(Context context) {
        context.sendBroadcast(new Intent("com.homekit.action.SYNC_DEVICE_STATES"));
    }


    public static void broadcastSynLocalDevice(Context context) {
        HLog.i(TAG, "broadcastSynLocalDevice");
        Intent inten=new Intent(HkConst.INTENT_SYNC_LOCAL);
        inten.putExtra("appid",context.getApplicationContext().hashCode());
        context.sendBroadcast(inten);
    }

    public static void broadcastEditNickName(BasicActivity context) {
        context.sendBroadcast(new Intent("com.homekit.action.EDIT_NICK_NAME"));
    }

    public static void broadcastTimerChangeList(Context context) {
        context.sendBroadcast(new Intent("com.homekit.action.TimerChange"));

    }

    public static void broadcastAtOverTime(Context context) {
        context.sendBroadcast(new Intent("com.homekit.action.AT_OVER_TIMER"));

    }


    public static void broadcastEditName(Context context, String mName) {
        Intent intent = new Intent("com.homekit.action.EDIT_NAME");
        intent.putExtra("extra_new_name", mName);
        context.sendBroadcast(intent);


    }

    public static void updateDeviceGroupProperty(
            final DeviceEntity deviceEntity, final Activity activity, final BaseHelper helper) {

        final JSONObject json = new JSONObject();
        try {
            json.put("name", deviceEntity.mName);
            json.put("group", deviceEntity.mGroup);
        } catch (JSONException e) {
            HLog.e(TAG, e);
        }
        helper.postRequest(new Runnable() {

            @Override
            public void run() {
                new DeviceProtocol(helper.app.mAppHelper).doPostDetail(helper.app.mUser.at, deviceEntity.mDeviceId,
                        new ProtocolHandler(activity, 0, new ProtocolHandler.CallBack() {

                            @Override
                            public void callBack(Result result) {
                                call(result);

                            }

                        }), json);
            }

            private void call(Result result) {

            }
        });
    }


    public static String getCountryCode(Context context) {
        String CountryID = "";
        String CountryZipCode = "";
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = Locale.getDefault().getCountry().toUpperCase();
        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                HLog.i(TAG, "set country code:" + CountryZipCode);
                break;
            }
        }
        return CountryZipCode;
    }

    public static String getNonce() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 8; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


    public static void addSelfKey(JSONObject json, String selfkey) {
        try {
            json.put("selfApikey", selfkey);

        } catch (Exception e) {
            HLog.e(TAG, e);
        }
    }


    public static  JSONObject getNotifyJson(HkApplication app) {
        HLog.i(TAG, "figuration msg notifyOnline  ");



        final JSONObject notifyOnline = new JSONObject();
        try {


            notifyOnline.put("action", "userOnline");
            notifyOnline.put("version", 2);
            notifyOnline.put("imei", app.mTele.mImei);
            notifyOnline.put("ts", System.currentTimeMillis() / 1000);
            notifyOnline.put("model", app.mTele.mModel);
            notifyOnline.put("os", "android");
            notifyOnline.put("romVersion", app.mTele.mOsVersion);
            notifyOnline.put("at", app.mUser.at.replace("Bearer ", ""));
            notifyOnline.put("userAgent", "app");

            notifyOnline
                    .put("apikey", app.mUser.apikey);

            notifyOnline.put("sequence", System.currentTimeMillis() + "");
            notifyOnline.put("appid", Auth.mAppid);
            notifyOnline.put("nonce", getNonce());
            notifyOnline.put("apkVesrion", HkConst.CONFIG_APK_VERSION);


        } catch (JSONException e) {
            HLog.e(TAG, "JSONException figuration notify online");
        }
        return notifyOnline;

    }


    public static int parseType(int ui) {
        int mSwitchSize=1;
        switch (ui) {
            case DeviceHelper.UI_SWITH_ONE:
                mSwitchSize = 1;
                break;
            case DeviceHelper.UI_SWITH_TWO:
                mSwitchSize = 2;
                break;
            case DeviceHelper.UI_SWITH_THREE:
                mSwitchSize = 3;
                break;
            case DeviceHelper.UI_SWITH_FOUR:
                mSwitchSize = 4;
                break;

            case DeviceHelper.UI_PLUG_SINGLE:
            case DeviceHelper.UI_PLUG_SINGLE_WITH_POWER:
                mSwitchSize = 1;
                break;
            case DeviceHelper.UI_PLUG_TWO:
                mSwitchSize = 2;
                break;
            case DeviceHelper.UI_PLUG_THREE:
                mSwitchSize = 3;
                break;
            case DeviceHelper.UI_PLUG_FOUR:
                mSwitchSize = 4;
                break;

            default:
                break;
        }
        return mSwitchSize;

    }


}

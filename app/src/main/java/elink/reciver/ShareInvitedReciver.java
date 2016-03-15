package elink.reciver;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.coolkit.WebSocketManager;
import com.coolkit.common.HLog;

import elink.HkApplication;
import elink.HkConst;
import elink.common.Helper;
import elink.activity.DeviceActivity;
import elink.activity.DialogActivity;

public class ShareInvitedReciver extends BroadcastReceiver {
    private static final String TAG = ShareInvitedReciver.class.getSimpleName();
    JSONObject jsonO;
    private Dialog mInviteDialog;
    private DeviceActivity context;

    public ShareInvitedReciver(DeviceActivity deviceActivity) {
        context = deviceActivity;
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        String intenAction = arg1.getAction();
HLog.i(TAG, "action :" + intenAction);
        if ("com.homekit.action.CANCLE_SHARE".equals(intenAction)) {
            Helper.broadcastSynDevice(arg0);
        } else if ("com.homekit.action.SHARE".equals(intenAction)) {
            String json = arg1.getStringExtra("json");
            Intent intent = new Intent(arg0, DialogActivity.class);
            intent.putExtra("json", json);
            intent.putExtra(HkConst.EXTRA_DIALOG_TYPE, DialogActivity.DIALOG_BEING_SHARED);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            arg0.startActivity(intent);
        } else if ("com.homekit.action.NOTIFY".equals(intenAction)) {
            String json = arg1.getStringExtra("json");
            Intent intent = new Intent(arg0, DialogActivity.class);

            try {
                JSONObject js = new JSONObject(json);
                if (js.has("params")) {
                    JSONObject params = new JSONObject(js.getString("params"));
                    String text = params.has("text") ? params.getString("text") : "";
                    if (!TextUtils.isEmpty(text)) {
                        intent.putExtra("json", text);

                        intent.putExtra(HkConst.EXTRA_DIALOG_TYPE, DialogActivity.DIALOG_NOTIFY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        arg0.startActivity(intent);

                    }

                    int hb = params.has("hb") ? params.getInt("hb") : -1;
                    int hbInterval = params.has("hbInterval") ? params.getInt("hbInterval") : -1;
                    WebSocketManager manager = WebSocketManager.getInstance(context.app.mAppHelper);
                    if (hb != hbInterval && hb != -1 && (hb != manager.mHb ||manager.mHbInterval != hbInterval)) {
                        HLog.i(TAG, "set heabeat change, new config:" + hb + " mHbinterval:" + hbInterval);

                        ((HkApplication) this.context.getApplicationContext()).mSp.saveHearBeat(hb, hbInterval);
                        manager.setHearBeat(hb, hbInterval);

                    }

                }
            } catch (Exception e) {
                HLog.e(TAG, "notify exception:" + arg1.getStringExtra("json"));
                HLog.e(TAG, e);
            }

        }

    }


}

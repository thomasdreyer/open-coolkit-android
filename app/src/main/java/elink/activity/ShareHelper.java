package elink.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import demo.demo.R;
import com.coolkit.common.HLog;
import com.coolkit.common.WsRequest;

import elink.common.Helper;
import elink.utils.ThreadExecutor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by app on 15/9/4.
 */
public class ShareHelper {
    private final Context mContext;
    JSONObject jsonO;
    BaseHelper helper;

    public ShareHelper(Context context) {
        mContext = context;
    }

    public Dialog doRecieverShare(String json) {

        helper = new BaseHelper(mContext);
        HLog.i("", "reciver sharedsfsdf, " + json);
        try {
            jsonO = new JSONObject(json);
            if (jsonO.has("params")) {


                final Dialog mInviteDialog = new Dialog(mContext);
                mInviteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mInviteDialog.setTitle(mContext.getString(R.string.recevice_share)); // 设置标题
                View view = mInviteDialog.getLayoutInflater().inflate(
                        R.layout.dialog_share_invited, null);

                mInviteDialog.setContentView(view);


                String parms = jsonO.getString("params");
                JSONObject obj = new JSONObject(parms);
                jsonO = new JSONObject(json);


                String user = obj.getString("userName");
                String devName = obj.getString("deviceName");
                ((TextView) view.findViewById(R.id.tv_tip)).setText(mContext
                        .getString(R.string.reciver_invite, user, devName));
                view.findViewById(R.id.btn_sure).setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                accept();
                                mInviteDialog.dismiss();
                            }

                        });
                view.findViewById(R.id.btn_cancle).setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                cancleInvite();
                                mInviteDialog.dismiss();

                            }
                        });
                return mInviteDialog;

            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }


    private void accept() {

        ThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    json.put("error", 0);
                    json.put("result", 2);

                    json.put("apikey", jsonO.getString("apikey"));
                    json.put("deviceid", jsonO.getString("deviceid"));
                    json.put("sequence", jsonO.getString("sequence"));

                    json.put("userAgent", "app");
                    helper.postWsRequest(new WsRequest(json) {
                        @Override
                        public void callback(String msg) {
                            HLog.i("", "res is:" + msg);
                        }
                    });

                    Thread.sleep(2000);
                    Helper.broadcastSynDevice(helper.app);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });

    }

    public void cancleInvite() {

        ThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    json.put("error", 0);
                    json.put("result", 3);

                    json.put("apikey", jsonO.getString("apikey"));
                    json.put("deviceid", jsonO.getString("deviceid"));
                    json.put("sequence", jsonO.getString("sequence"));
                    // json.put("action", "device.share");
                    json.put("userAgent", "app");
                    helper.postWsRequest(new WsRequest(json) {
                        @Override
                        public void callback(String msg) {
                            HLog.i("", "res is:" + msg);
                        }
                    });
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });

    }
}

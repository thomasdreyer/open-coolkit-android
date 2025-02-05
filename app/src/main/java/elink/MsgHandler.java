p

import android.os.Handler;

public class MsgHandler extends Handler {
//    private static final String TAG = MsgHandler.class.getSimpleName();
//    private Map<String, Callback> mResponseMap;
//    public MsgHandler(Looper mainLooper) {
//        super(mainLooper);
//    }
//
//    @Override
//    public void handleMessage(Message msg) {
//        super.handleMessage(msg);
//        String msgContent = (String) msg.obj;
//        JSONObject json;
//        HLog.i(TAG, "handleMessage " + msgContent);
//        try {
//            json = new JSONObject(msgContent);
//
//            if (json.has("action")) {
//                String action = json.getString("action");
//                doAction(action, json);
//            }
//            if (json.has("sequence")) {
//                String id = json.getString("sequence");
//                if (!TextUtils.isEmpty(id)) {
//                    WebSocketManager.Callback call = mResponseMap.get(id);
//                    boolean hasCallback = null != call;
//                    HLog.i(TAG, "has callback:" + hasCallback);
//                    if (hasCallback) {
//                        call.callback(msgContent);
//
//                    }
//
//                }
//
//            }
//        } catch (JSONException e) {
//            HLog.e(TAG, "JSONException:" + e);
//        } catch (Exception e) {
//            HLog.e(TAG, "do action exception:" + e);
//        }
//
//    }
//
//    ;
//
//    private void doAction(String action, JSONObject json) {
//        String actionName = "";
//        if ("share".equals(action)) {
//            actionName = "com.homekit.action.SHARE";
//
//        } else if ("cancelShare".equals(action)) {
//            actionName = "com.homekit.action.CANCLE_SHARE";
//
//        } else if ("update".equals(action)) {
//
//            actionName = "com.homekit.action.UPDATE";
//        } else if ("sysmsg".equals(action)) {
//            actionName = "com.homekit.action.SYSMSG";
//g
//        } else if ("redirect".equals(action)) {
//
//            doRedirectAction(json);
//            return;
//
//        } else if ("notify".equals(action)) {
//            actionName = "com.homekit.action.NOTIFY";
//        }
//        boolean hasGloableProcess = SystemActionHolder.process(
//                (HkApplication) mContext.getApplicationContext(), actionName,
//                json);
//
//        HLog.i(TAG, "broadcast:" + actionName + " has globel updated:"
//                + hasGloableProcess);
//        Intent intent = new Intent(actionName);
//        intent.putExtra("json", json.toString());
//        intent.putExtra("hasGloableProcess", hasGloableProcess);
//        mContext.sendBroadcast(intent);
//
//    }
//
//    public void doRedirectAction(JSONObject json) {
//        try {
//            HLog.i(TAG, "redirect ws socket");
//            String ip = !json.has("IP") ? "no ip" : json.getString("IP");
//            int port = !json.has("port") ? -1 : json.getInt("port");
//
//            ((HkApplication) mContext.getApplicationContext()).mHost.updateWsHost(port, ip);
//
//        } catch (Exception e) {
//            HLog.e(TAG, e);
//        }
//    }
}
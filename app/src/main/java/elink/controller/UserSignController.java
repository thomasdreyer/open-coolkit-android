package elink.controller;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;
import com.coolkit.protocol.request.ProtocolHandler;
import com.coolkit.protocol.request.Result;
import com.coolkit.protocol.request.SmsProtocol;
import com.coolkit.protocol.request.UserProtocol;

import elink.activity.SetUserAuthActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSignController extends BasiController<SetUserAuthActivity> {

    protected static final String TAG = UserSignController.class.getSimpleName();

    private UserProtocol mUserProtocol;
    SetUserAuthActivity activity;

    public UserSignController(SetUserAuthActivity context) {
        super(context);
        activity = context;
        mUserProtocol = new UserProtocol(context.app.mAppHelper);

    }


    public void doRequestSmsCode(final String phoneNum) {


        final ProtocolHandler handler = new ProtocolHandler(mContext) {
            @Override
            public void callBack(Result result) {
                super.callBack(result);
                if (result.mCode == 200) {
                    if (TextUtils.isEmpty(result.mMsg)) {
                        return;
                    } else if (result.mMsg.contains("\"error\":112314")) {
                        activity.mTimerHandler.sendEmptyMessage(1);

                    } else if (result.mMsg.contains("\"error\":160013")) {
                        activity.mTimerHandler.sendEmptyMessage(3);
                    }else if(result.mMsg.contains("\"error\":400")){
                        activity.mTimerHandler.sendEmptyMessage(4);

                    }
                } else if (result.mCode == 4000) {
                    activity.mTimerHandler.sendEmptyMessage(2);
                }


            }


        };
        new SmsProtocol(mContext.app.mAppHelper).doSms(handler, phoneNum);

    }

    public void doResetPwd(final String phone, final String pwd,
                           final String smsCode) {

        final Handler okHander = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 0:
                        Toast.makeText(mContext, mContext.getString(R.string.repeat_password_success), Toast.LENGTH_SHORT)
                                .show();
                        mContext.finish();
                        break;
                    case 1:

                        Toast.makeText(mContext, msg.obj + "",
                                Toast.LENGTH_SHORT).show();
                        return;
                    case 2:
                        Toast.makeText(mContext, msg.obj + "",
                                Toast.LENGTH_SHORT).show();

                        return;
                    case 3:
                        Toast.makeText(mContext, msg.obj + "",
                                Toast.LENGTH_SHORT).show();
                        return;
                    default:
                        break;
                }

            }

            ;
        };
        final ProtocolHandler handler = new ProtocolHandler(mContext) {


            @Override
            public void callBack(Result result) {

                if (200 == result.mCode) {
                    JSONObject json;
                    try {
                        json = new JSONObject(result.mMsg);

                        String errror = "";
                        if (json.has("error")) {
                            errror = json.getString("error");
                        }
                        if (TextUtils.isEmpty(errror)) {

                            okHander.sendEmptyMessage(0);
                        } else {
                            Message msg = okHander.obtainMessage();
//                            msg.obj = result.mMsg;
                            String getError = json.getString("error");
                            if ("498".equals(getError)) {
                                msg.obj = mContext.getString(R.string.error_sms_code);
                                msg.what = 1;
                                msg.sendToTarget();
                            } else {
                                msg.obj = mContext.getString(R.string.reset_error);
                                msg.what = 2;
                                msg.sendToTarget();
                            }
                        }
                    } catch (JSONException e1) {
                        HLog.e(TAG, e1);
                    }

                } else {
                    Message msg = okHander.obtainMessage();
                    msg.what = 3;
                    msg.obj = mContext.getString(R.string.reset_fail);
                    msg.sendToTarget();
                }

            }
        };
        mUserProtocol.doResetPwd(handler, phone, pwd, smsCode);

    }


    public void doRegester(final String phone, final String pwd, final String smsCode) {
        final Handler okHander = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 0:
                        Toast.makeText(mContext, mContext.getString(R.string.register_success), Toast.LENGTH_SHORT)
                                .show();

                        mContext.finish();
                        mContext.mLogingDialog.dismiss();
                        break;
                    case 1:
                        Toast.makeText(mContext, ""+msg.obj, Toast.LENGTH_SHORT).show();
                        mContext.mLogingDialog.dismiss();
                        return;
                    case 2:

                        Toast.makeText(mContext, ""+msg.obj, Toast.LENGTH_SHORT).show();
                        mContext.mLogingDialog.dismiss();
                        return;
                    case 3:
                        Toast.makeText(mContext, ""+msg.obj, Toast.LENGTH_SHORT).show();
                        mContext.mLogingDialog.dismiss();
                        return;
                    case 4:
                        Toast.makeText(mContext, mContext.getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
                        mContext.mLogingDialog.dismiss();
                        return;
                    case 5:
                        Toast.makeText(mContext, mContext.getString(R.string.countrycode_smscode_error), Toast.LENGTH_SHORT).show();
                        mContext.mLogingDialog.dismiss();
                        return;
                    default:
                        break;
                }

            }

            ;
        };

        mContext.postRequest(new Runnable() {
            @Override
            public void run() {
                mUserProtocol.doRegister(mContext, new ProtocolHandler(mContext, new ProtocolHandler.CallBack() {
                    @Override
                    public void callBack(Result result) {
                        String errror = "";
                        if (result.mCode == 200) {

                            try {
                                JSONObject json = new JSONObject(result.mMsg);

                                if (json.has("error")) {
                                    errror = json.getString("error");
                                }
                                if (TextUtils.isEmpty(errror)) {
                                    okHander.sendEmptyMessage(0);
                                } else {
                                    Message msg = okHander.obtainMessage();
                                    msg.obj = errror;
                                    if ("498".equals(errror)) {
                                        msg.obj = mContext.getString(R.string.error_sms_code);
                                        msg.what = 1;
                                        msg.sendToTarget();

                                    } else if ("409".equals(errror)) {
                                        msg.obj = mContext.getString(R.string.dont_reset_agin);
                                        msg.what = 2;
                                        msg.sendToTarget();
                                    } else if("400".equals(errror)){
                                        msg.obj = mContext.getString(R.string.countrycode_smscode_error);
                                        msg.what = 3;
                                        msg.sendToTarget();
                                    }else{
                                        msg.obj = errror;
                                        msg.what = 4;
                                        msg.sendToTarget();
                                    }


                                }

                            } catch (JSONException e) {
                                Message msg = okHander.obtainMessage();
                                msg.obj = errror;
                                msg.what = 1;
                                msg.sendToTarget();
                            }

                        }else{
                            Message msg = okHander.obtainMessage();
                            msg.obj = errror;
                            msg.what = 5;
                            msg.sendToTarget();
                        }
                    }

                }), phone, pwd, smsCode);

            }
        });


    }

    public void clearLogin() {

    }

}

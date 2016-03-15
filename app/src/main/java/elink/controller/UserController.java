package elink.controller;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.coolkit.WebSocketManager;
import com.coolkit.common.HLog;
import com.coolkit.protocol.request.ProtocolHandler;
import com.coolkit.protocol.request.Result;
import com.coolkit.protocol.request.UserProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import elink.activity.UserLoginActivity;
import elink.utils.IntentHelper;

public class UserController extends BasiController<UserLoginActivity> {

    protected static final String TAG = UserController.class.getSimpleName();

    private UserProtocol mUserProtocol;

    public UserController(UserLoginActivity context) {
        super(context);
        mUserProtocol = new UserProtocol(context.app.mAppHelper);

    }


    private int retry=2;
    private ProtocolHandler mLoginHandler = new ProtocolHandler(mContext,new ProtocolHandler.CallBack() {
        @Override
        public void callBack(Result result) {
            //当有wifi但是通过第三方安全软件禁用权限或者wifi状态不好时 result.mMsg返回空
            HLog.i(TAG, "login callback result:" + result.mMsg);
            if (TextUtils.isEmpty(result.mMsg)) {
                setCheckWifi();
            } else {
                try {



                    if (result.mCode == 200) {
                        JSONObject jsonObject = new JSONObject(result.mMsg);
                        HLog.i(TAG,"login callback:"+result.mMsg+",result"+result.toString()+"result code:"+result.mCode);
                        if (!jsonObject.has("error")) {

                            String user = jsonObject.getString("user");

                            JSONObject jsonUser = null;

                            jsonUser = new JSONObject(user);

                            String apiKey = jsonUser.getString("apikey");


                            app.mUser.userName = jsonUser.getString("phoneNumber");

                            app.mUser.pwd = mPwd;


                            app.mUser.apikey = apiKey;
                            app.mUser.isLogin = true;
                            app.mUser.at = "Bearer " + jsonObject.getString("at");

                            if (jsonUser.has("nickname")) {
                                app.mUser.nickName = jsonUser.getString("nickname");

                            }else{
                                app.mUser.nickName=app.mUser.userName;
                                app.mUser.nickName=app.mUser.userName.replace(app.mCountry,"");
                            }


                            app.mUser.region = jsonObject.getString("region");

                            mUser = app.mUser.userName.replace(app.mCountry, "");


                            String value=app.mSp.getCountryCode("mCountrycode");
                            HLog.i(TAG, "yaoyisave:" + value);
                            app.mSp.saveUserLogin(app.mUser.userName, mPwd, app.mUser.at, apiKey,
                                    app.mUser.nickName, value);

                            if(!app.mUser.region.equals(app.mSp.getRegion())){
                                app.mSp.saveRegion(app.mUser.region);
                                app.mAppHelper.mHost.setHost(app.mUser.at,app.mUser.apikey,app.mUser.region);
                            }
                            app.mAppHelper.isLogin=true;
                            WebSocketManager.getInstance(app.mAppHelper).activeWs(true,app.mUser.apikey,app.mUser.at);
                            mContext.doLaterLy();




                        }else if(jsonObject.getInt("error")==301&&--retry>0){
                            HLog.i(TAG, "login failure ,try a redirect url:"+jsonObject);

//                            app.mUser.region = jsonObject.getString("region");
//                            app.mSp.saveRegion(app.mUser.region);
//                            app.mAppHelper.mHost.setHost(app.mUser.at,app.mUser.apikey,app.mUser.region);
                            mUserProtocol.doLogin(mLoginHandler, mPwd, mEmail,
                                    mUser);


                        }else{
                            setError();
                        }

                    } else {
                        setError();
                    }


                } catch (JSONException e) {
                    HLog.e(TAG, e);
                    setError();
                }


            }

        }
    });

    private String mUser;

    private String mPwd;

    private  String mEmail;
    public void doLogin(final String password, final String email,
                        final String phoneNumber) {

        mPwd = password;
        mEmail=email;
        mUser=phoneNumber;

        mUserProtocol.doLogin(mLoginHandler, password, email,
                phoneNumber);

    }

    protected void setError() {
        new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mContext.setError();
            }
        }.sendEmptyMessage(0);

    }

    protected  void setCheckWifi(){
        new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mContext.setCheckWifi();
            }
        }.sendEmptyMessage(0);
    }


    public void gotoSignUp(String phoneNum) {
        IntentHelper.startSetUserAuthActvity(mContext, "signup", phoneNum);
    }


    public void gotoResetPwd(String phone) {
        IntentHelper.startSetUserAuthActvity(mContext, "reset", phone);

    }


}

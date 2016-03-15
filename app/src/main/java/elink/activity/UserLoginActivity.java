package elink.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;

import elink.HkApplication;
import elink.controller.UserController;
import elink.utils.DialogHelper;
import elink.utils.IntentHelper;
import elink.utils.NetHelper;
import elink.utils.SpHelper;

public class UserLoginActivity<T> extends BasicActivity<UserController>
        implements OnClickListener {
    private static final String TAG = UserLoginActivity.class.getSimpleName();
    private static final int MSG_GO_TO_DEVICE = 2;
    private EditText mEtUserName;
    private EditText mEtPwd;
    private Button mBtnLogin;
    private Button mBtnSignUp;
    private TextView mTvReset;
    private String mPhone;
    private ImageView mBtnEye;
    private ImageView mBtnClear;
    private String mUserName;
    private boolean mHidePwd = true;
    private Drawable mDrawableOn;
    private Drawable mDrawableClose;
    private TextView mTvTip;
    private Dialog mLogingDialog;
    private TextView mCountryId;
    public HkApplication app;
    public SpHelper sp;
    public String countrycode;
    public MyReciever mReciever;
    private View mSubmitFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_login);
        app = (HkApplication) getApplicationContext();

        sp = new SpHelper(this);
        initView();
        initData();
        setView();
        mReciever = new MyReciever();
        registerReceiver(mReciever, new IntentFilter("com.android.login"));

    }


    public void doLaterLy() {
        HLog.i(TAG, "do laterly");
        mHandler.sendEmptyMessageDelayed(MSG_GO_TO_DEVICE,2500);
    }

    public class MyReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String CountryCode = intent.getStringExtra("result");
            mCountryId.setText(CountryCode);
        }
    }

    protected void setView() {
        super.setView();
        mEtUserName.setText(mUserName);
//        登陆界面进入后光标显示在账号末尾；
        mEtUserName.setSelection(mUserName.length());
        // if(mIsEyeOn){
        //
        // mEtPwd.setText(mPwd);
        // }else{
        //
        // // mEtPwd.setText(text)
        // }
        if(countrycode!=null){
            mCountryId.setText(countrycode);
            HLog.i(TAG,getCountryCode()+"UserLoginActivity,CountryCode");
        }else{
            mCountryId.setText(app.mSp.getloginCode(app.mUser.userName));
        }


    }


    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(getApplicationContext(),getString(R.string.feedback_success),Toast.LENGTH_SHORT).show();

                    break;
                case 1:
                    Toast.makeText(getApplicationContext(),getString(R.string.feedback_fail),Toast.LENGTH_SHORT).show();
                    break;
                case MSG_GO_TO_DEVICE:
                    closeLogingDialog();
                    finish();
                    IntentHelper.startDeviceActvity(UserLoginActivity.this);
                    HLog.i(TAG, "login okay ,go to device list");
                    break;

            }
        }
    };


    private void initData() {
        mController = new UserController(this);
        if (null != app.mUser.userName) {
            String key=app.mUser.userName;
            String value=app.mSp.getloginCode(key);
            HLog.i(TAG,"username:"+app.mUser.userName+"count:"+key+" value:"+value);
//            mUserName = app.mUser.userName.replace(app.mCountry, "");
            mUserName = app.mUser.userName.replace(value, "");
        }

        mHidePwd = app.mSp.getUserEyeOn(app.mUser.userName);

        mDrawableClose = getResources().getDrawable(R.drawable.eye_close);
        mDrawableOn = getResources().getDrawable(R.drawable.eye_on);
    }

    public void initView() {

        mEtUserName = (EditText) this.findViewById(R.id.et_user_name);
        mEtPwd = (EditText) this.findViewById(R.id.et_pwd);

        mBtnLogin = (Button) this.findViewById(R.id.btn_login);
        mBtnSignUp = (Button) this.findViewById(R.id.btn_sign_up);

        mTvReset = (TextView) this.findViewById(R.id.tv_reset_pwd);
        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mCountryId = (TextView) findViewById(R.id.tv_countryid);

        mBtnLogin.setOnClickListener(this);
        mBtnSignUp.setOnClickListener(this);
        mTvReset.setOnClickListener(this);
        mBtnClear = (ImageView) this.findViewById(R.id.iv_clear_login);
        mBtnEye = (ImageView) this.findViewById(R.id.iv_pwd_eye);
        mBtnClear.setOnClickListener(this);
        mCountryId.setOnClickListener(this);
        countrycode = sp.getCountryCode("");
        mSubmitFeedback=findViewById(R.id.tv_feedback);

//        mSubmitFeedback.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                onClickFeedback(view);
//            }
//        });

        mBtnEye.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHidePwd) {
                    // 设置EditText文本为可见的
                    mEtPwd.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                    mBtnEye.setImageDrawable(mDrawableClose);
                } else {
                    // 设置EditText文本为隐藏的
                    mEtPwd.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    mBtnEye.setImageDrawable(mDrawableOn);
                }
                mHidePwd = !mHidePwd;
                mEtPwd.postInvalidate();
                // 切换后将EditText光标置于末尾
                CharSequence charSequence = mEtPwd.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }

            }
        });
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_login:
                HLog.i(TAG, "onclick login");
                String msg = mEtUserName.getEditableText().toString();
                HLog.i(TAG,msg);
                mPhone = "";
                String email = "";
                String pwd = mEtPwd.getEditableText().toString();
                if (msg.contains("@")) {
                    email = msg;
                } else {
//                    mPhone = app.mCountry + msg;
                    mPhone = getCountryCode() + msg;
//                    HLog.i(TAG,mPhone+"thisnumber");
                }

                mTvTip.setText("");
                if (NetHelper.isConnnected(this)) {
                    HLog.i(TAG, "login check  connection true");

                    if (TextUtils.isEmpty(msg.trim()) || TextUtils.isEmpty(mEtPwd.getEditableText().toString().trim())) {
                        Toast.makeText(this, getString(R.string.name_or_pwd_is_empty), Toast.LENGTH_SHORT).show();
                    } else {
                        mLogingDialog = DialogHelper.createProgressDialog(this, getString(R.string.logining));
                        mLogingDialog.show();
                        mController.doLogin(pwd, email, mPhone);
                    }
                } else {
                    HLog.i(TAG, "show no net");

                    showNoNet();


                }

                break;
            case R.id.btn_sign_up:
                HLog.i(TAG, "sign up click");
                msg = mEtUserName.getEditableText().toString();
                mPhone = "";
                if (!msg.contains("@")) {
//
                } else {

                }
                mController.gotoSignUp(mPhone);
                break;
            case R.id.tv_reset_pwd:
                msg = mEtUserName.getEditableText().toString();
                mPhone = msg;

                mController.gotoResetPwd(mPhone);
                break;
            case R.id.iv_clear_login: {
                mEtPwd.setText("");
                mEtUserName.setText("");

            }
            break;
            case R.id.tv_countryid:
                Intent intent = new Intent();
                intent.setClass(UserLoginActivity.this, CountryCodeActivity.class);
                intent.putExtra("key", "login");
                startActivity(intent);
//                Toast.makeText(this,"CountryCode",Toast.LENGTH_SHORT).show();
            break;

            default:
                break;
        }
    }


    public void setError() {
        mTvTip.setText(getString(R.string.login_failed));
        closeLogingDialog();

    }

    public void setCheckWifi(){
        mTvTip.setText(getString(R.string.check_wifi));
        closeLogingDialog();
    }

    public void closeLogingDialog() {

        mLogingDialog.dismiss();
    }



    public  String getCountryCode(){
        return sp.getCountryCode("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReciever);
    }
}

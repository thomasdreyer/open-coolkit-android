package elink.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import elink.common.UiHelper;

import demo.demo.R;
import com.coolkit.common.HLog;

import elink.controller.UserSignController;
import elink.utils.DialogHelper;
import elink.utils.NetHelper;
import elink.utils.SpHelper;

import java.util.regex.Pattern;

public class SetUserAuthActivity<T> extends BasicActivity<UserSignController>
        implements OnClickListener {
    private static final String TAG = SetUserAuthActivity.class.getSimpleName();
    // "reset","signup"
    private String mType;
    private boolean mIsReset;
    private String mTiTle;
    private EditText mEtPhone;
    private EditText mEtPwd;
    private String mBtnStr;
    private Button mBtnSubmit;
    private TextView mTvTimer;
    private CharSequence mPhoneNum;
    private String mPwdHint;
    int PERIOD = 60;
    int period = PERIOD;
    private TextView mCountrycode;
    private SpHelper sp;
    public MyReciever mReciever;
    String CountryCode;

    public Handler mTimerHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 0:

                    if (period-- > 0) {

                        mTvTimer.setText(period + getString(R.string.resend));
                        sendEmptyMessageDelayed(0, 1000);
                    } else {
                        period = PERIOD;
                        mTvTimer.setEnabled(true);
                        mTvTimer.setText(getString(R.string.send_verification_code));
                    }
                    break;

                case 1:
                    UiHelper.showShortToast(SetUserAuthActivity.this, getString(R.string.send_msg_limit));
                    break;
                case 3:
                    UiHelper.showShortToast(SetUserAuthActivity.this, getString(R.string.send_msg_fail_check));
                    break;
                case 2:
                    UiHelper.showShortToast(SetUserAuthActivity.this, getString(R.string.send_msg_fail_check));
                    break;
                case 4:
                    UiHelper.showShortToast(SetUserAuthActivity.this, getString(R.string.send_msg_fail));

                    break;
                case 5:
                    UiHelper.showShortToast(SetUserAuthActivity.this, getString(R.string.send_msg_fail));

                    break;


            }

            ;
        }
    };
    private EditText mEtSmsCode;
    private TextView mTvTitle;
    public Dialog mLogingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);
        sp = new SpHelper(this);
        initView();
        initData();
        setView();
        mReciever = new MyReciever();
        registerReceiver(mReciever, new IntentFilter("com.android.register"));

    }

    public class MyReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            CountryCode = intent.getStringExtra("register");
            mCountrycode.setText(CountryCode);
        }
    }

    protected void setView() {
        super.setView();
        setTitle(mTiTle);
        mEtPhone.setText(mPhoneNum);
//        重置密码时账号光标显示在末尾
        mEtPhone.setSelection(mPhoneNum.length());
        mEtPwd.setHint(mPwdHint);
        mBtnSubmit.setText(mBtnStr);
        mCountrycode.setText(sp.getCountryCode(""));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mLogingDialog && mLogingDialog.isShowing()) {
            mLogingDialog.dismiss();
        }
        unregisterReceiver(mReciever);
    }

    private void initData() {
        mController = new UserSignController(this);
        mType = this.getIntent().getStringExtra("type");
        mIsReset = "reset".equals(mType);
        if (mIsReset) {
            mTiTle = getString(R.string.repassword);
            mBtnStr = getString(R.string.commit);
            mPwdHint = getString(R.string.enter_newaccount_paswd);
            mCountrycode.setEnabled(true);
        } else {
            mTiTle = getString(R.string.user_register);
            mPwdHint = getString(R.string.enter_account_paswd);
            mBtnStr = getString(R.string.register);
            mCountrycode.setEnabled(true);
        }
        mPhoneNum = this.getIntent().getStringExtra("phone");

    }

    public void initView() {
        mEtPhone = (EditText) this.findViewById(R.id.et_phone);
        mEtPwd = (EditText) this.findViewById(R.id.et_pwd);
        mEtSmsCode = (EditText) this.findViewById(R.id.et_sms_code);

        mTvTimer = (TextView) findViewById(R.id.tv_timer);
        mBtnSubmit = (Button) this.findViewById(R.id.btn_submit);
        mTvTitle = (TextView) findViewById(R.id.tv_goback);
        mCountrycode = (TextView) findViewById(R.id.tv_countrycode);

        mBtnSubmit.setOnClickListener(this);
        mTvTimer.setOnClickListener(this);
        mCountrycode.setOnClickListener(this);

    }

    public boolean isValidPwd(String pwd) {
        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd.trim())) {
            Toast.makeText(this, getString(R.string.password_not), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            boolean match = (pwd.length() >= 8)
                    && Pattern.compile("[0-9]+?").matcher(pwd).find()
                    && Pattern.compile("[a-z]+?").matcher(pwd).find()
                    && Pattern.compile("[A-Z]+?").matcher(pwd).find() && pwd.matches("^[A-Za-z0-9]+$");

            if (!match) {
                Toast.makeText(this, getString(R.string.info_fomat_paswd), Toast.LENGTH_SHORT)
                        .show();
            }
            return match;
        }

    }

    @Override
    public void onClick(View arg0) {
//        String phoneNum = app.mCountry + mEtPhone.getEditableText().toString();
//        String phoneNum = sp.getCountryCode("") + mEtPhone.getEditableText().toString();
        String phoneNum = mCountrycode.getText().toString() + mEtPhone.getEditableText().toString();
        HLog.i(TAG, phoneNum + "OnClick");
        switch (arg0.getId()) {
            case R.id.btn_submit:
                HLog.i(TAG,"CountryCode:"+mCountrycode.getText().toString());
                if (TextUtils.isEmpty(mEtPhone.getEditableText().toString())
                        || TextUtils.isEmpty(mEtPhone.getEditableText().toString()
                        .trim())) {
                    Toast.makeText(this, getString(R.string.enter_phone_numbers), Toast.LENGTH_SHORT).show();
                    log();
                    return;
                }
                if (!isValidPwd(mEtPwd.getEditableText().toString())) {
                    log();
                    return;
                }

                if (NetHelper.isConnnected(this)) {
                    //添加新的判断(验证码不能为空)
                    if (TextUtils.isEmpty(mEtSmsCode.getEditableText().toString())) {
                        Toast.makeText(this, getString(R.string.sms_mustbenoempty), Toast.LENGTH_SHORT).show();
                        log();
                        return;

                    }
                    if (mIsReset) {
                        HLog.i(TAG, "onclick reset pwd:" + phoneNum);
                        mController.doResetPwd(phoneNum,
                                mEtPwd.getEditableText().toString(), mEtSmsCode
                                        .getEditableText().toString());
                    } else {
                        HLog.i(TAG, "onclick register:" + phoneNum);
                        mLogingDialog = DialogHelper.createProgressDialog(this, getString(R.string.registering));
                        mLogingDialog.show();
                        mController.doRegester(phoneNum,
                                mEtPwd.getEditableText().toString(), mEtSmsCode
                                        .getEditableText().toString());
                    }
                } else {

                    showNoNet();
                }

                log();
                break;
            case R.id.tv_timer:
                HLog.i(TAG, "on click request sms:country:" + mCountrycode.getText().toString());
                HLog.i(TAG,phoneNum+"timer_phonenumber");
                if (NetHelper.isConnnected(this)) {
//                    if (TextUtils.isEmpty(phoneNum) || phoneNum.equals(sp.getCountryCode(""))) {
                    if (TextUtils.isEmpty(phoneNum) || phoneNum.equals(mCountrycode.getText().toString())) {
                        Toast.makeText(this, getString(R.string.input_phone), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mController.doRequestSmsCode(phoneNum);
                    mTvTimer.setEnabled(false);
                    mTimerHandler.sendEmptyMessage(0);
                } else {

                    showNoNet();
                }
                break;

            case R.id.tv_countrycode:
                Intent intent = new Intent();
                intent.setClass(SetUserAuthActivity.this, CountryCodeActivity.class);
//                startActivityForResult(intent,200);
                intent.putExtra("key", "register");
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    private void log() {

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode == 0 && requestCode==200) {
//            if (data != null) {
//                String code ="+"+data.getStringExtra("result");
//                mCountrycode.setText("+" + data.getStringExtra("result"));
//                HLog.i(TAG,data.getStringExtra("result")+"databuweikong");
//            }
//
//        }else {
//            mCountrycode.setText(sp.getCountryCode(""));
//            HLog.i(TAG, sp.getCountryCode("") + "dataweikong");
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

}

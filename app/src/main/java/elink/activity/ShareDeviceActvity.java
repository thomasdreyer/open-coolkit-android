package elink.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;
import com.coolkit.common.WsRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import elink.HkConst;
import elink.common.Helper;
import elink.entity.DeviceEntity;
import elink.model.DbManager;
import elink.utils.NetHelper;

public class ShareDeviceActvity extends BasicActivity implements
        OnClickListener, OnItemClickListener {

    private static final String TAG = ShareDeviceActvity.class.getSimpleName();

    protected UsersListAdapt mGroupsAdapt;

    protected int retry = 60;
    public int mBlue;

    private Button mBtnShare;

    private ListView mLvUsers;

    private UsersListAdapt mAdapt;

    private List<ShareUser> shareUsers = new ArrayList<ShareDeviceActvity.ShareUser>();

    private Dialog mShareDialog;

    private DeviceEntity mDeviceEntity;

    private TextView mTvDash;

    private EditText mEt;

    private Button mBtnDel;

    private boolean mIsOwner;

    private ShareUser mSelectedUser;

    private Drawable mWhite;
    private Drawable mBlueD;
    ShareUser mShareUser;
    private String mCancleShare;

    List<ShareUser> addingShareUser = new ArrayList<ShareDeviceActvity.ShareUser>();

    private boolean mHasShare;
    private TextView mCountryCode;
    public ShareReceiver mShareReciever;
    public String countrycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HLog.i("11", "ShareDeviceActvity oncreate:" + app.mUser.userName);
        setContentView(R.layout.activity_share);
        mShareReciever = new ShareReceiver();
        registerReceiver(mShareReciever,new IntentFilter("com.android.share"));
        initData();
        if (null == mDeviceEntity) {
            finish();
            return;
        }
        initView();

        setView();

    }

    public class ShareReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            countrycode = intent.getStringExtra("share");
            mCountryCode.setText(countrycode);
            HLog.i(TAG,"share_countrycode: "+countrycode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mShareReciever);
    }

    private void initData() {

        String devId = getIntent().getStringExtra(HkConst.EXTRA_D_ID);
        mDeviceEntity = DbManager.getInstance(this).queryDeviceyByDeviceId(devId);

        if (null != mDeviceEntity) {
            HLog.i(TAG, "device id:" + mDeviceEntity.mId + " params :"
                    + mDeviceEntity.mParams + " mShare:"
                    + mDeviceEntity.mShareUsers);
            HLog.i(TAG,"mShareUsers: "+mDeviceEntity.mShareUsers);
        } else {
            return;
        }
        mIsOwner = TextUtils.isEmpty(mDeviceEntity.mOwer);

        try {
            JSONArray arrays = new JSONArray(mDeviceEntity.mShareUsers);
            ShareUser user;
            for (int i = 0; i < arrays.length(); i++) {
                user = new ShareUser();
                JSONObject obje = arrays.getJSONObject(i);
                if (obje.has("phoneNumber")) {
                    user.mUserName = obje.getString("phoneNumber");
                    HLog.i(TAG,"JSONObject:"+obje.toString()+"mUserName:"+user.mUserName);
                }
                user.mState = 2;
                shareUsers.add(user);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (mIsOwner) {

        }

        mHasShare = shareUsers.size() > 0;
        mAdapt = new UsersListAdapt(shareUsers);

        mBlueD = getResources().getDrawable(R.color.blue);
        mWhite = getResources().getDrawable(R.color.white);

    }

    class ShareUser {
        public String mUserName;
        // 0 已经共享 1：等待确认
        public int mState = 0;

        public String sequece;

        public ShareUser(String name, int state) {
            mUserName = name;
            mState = state;
        }

        public ShareUser() {
            // TODO Auto-generated constructor stub
        }
    }

    class UsersListAdapt extends BaseAdapter {

        private List<ShareUser> mData;

        public UsersListAdapt(List<ShareUser> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return null == mData ? 0 : mData.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null == mData ? null : mData.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        public void refresh() {
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ShareUser data = mData.get(arg0);
            RelativeLayout view = null;
            HLog.i(TAG, "get view group");
            view = (RelativeLayout) getLayoutInflater().inflate(R.layout.su_item, null);
            HLog.i(TAG, "inflate view");
            if (data == mSelectedUser) {
                HLog.i(TAG, "set view 1");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(mBlueD);
                } else {
                    view.setBackgroundResource(data.mState);
                }

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(mWhite);
                } else {

                    view.setBackgroundResource(R.color.white);

                }

                HLog.i(TAG, "set view 2");
            }
            ImageView icon = (ImageView) view.findViewById(R.id.iv_icon);

            HLog.i(TAG, "set view 3");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                icon.setBackground(getResources().getDrawable(
                    getStateIcon(data.mState)));
            } else {
                icon.setBackgroundResource(getStateIcon(data.mState));
            }


//            icon.setBackground(getResources().getDrawable(
//                    getStateIcon(data.mState)));

            HLog.i(TAG, "set view 4");
            TextView name = (TextView) view.findViewById(R.id.tv_name);
            HLog.i(TAG, "set view 5");
            name.setText(getString(R.string.user) + data.mUserName + "("
                    + getStateDes(data.mState) + ")");

            HLog.i(TAG, "set view 6");
            view.setTag(data);
            HLog.i(TAG, "set view 7");

            return view;
        }


        //
        private String getStateDes(int mState) {
            if (0 == mState) {
                return getString(R.string.user_online) ;

            } else if (1 == mState) {
                return getString(R.string.user_exist);
            } else if (2 == mState) {
                return getString(R.string.share_success);
            } else if (3 == mState) {
                return getString(R.string.user_denied);

            } else if (4 == mState) {
                return getString(R.string.user_sharex);

            } else if (5 == mState) {
                return getString(R.string.share_outtime);
            }else  return getString(R.string.wait_requset);
        }

        private int getStateIcon(int mState) {
            if (2 == mState) {
                return R.drawable.user_online;

            } else if (0 == mState||1 == mState||3 == mState||5==mState) {
                return R.drawable.user_offline;
            } else if (-1 == mState) {
                return R.drawable.user_wait;
            }
            return R.drawable.user_offline;
        }
    }

    private void initView() {
//        if(countrycode!=null){
//            mCountryCode.setText(countrycode);
//        }else{
//            mCountryCode.setText(app.mCountry);
//        }
        mBtnDel = (Button) findViewById(R.id.btn_cancle);
        mBtnShare = (Button) findViewById(R.id.btn_share);
        mLvUsers = (ListView) findViewById(R.id.lv_users);
        mBtnShare.setOnClickListener(this);
        mBtnDel.setOnClickListener(this);
        mLvUsers.setOnItemClickListener(this);

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void setView() {
        super.setView();
        setTitleColor(mBlue);
        getActionBar().setLogo(new BitmapDrawable());
        setTitle(getString(R.string.share_device));
        mBtnDel.setVisibility(View.VISIBLE);
        mBtnDel.setEnabled(mHasShare);

        mLvUsers.setAdapter(mAdapt);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_share:
                Log.i(TAG, " on clikc btn share");
                if (mIsOwner) {
                    createInviteDialog();
                    mShareDialog.show();
                } else {
                    Toast.makeText(this, getString(R.string.share_owner_device), Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.btn_cancle:
                if (null == mSelectedUser) {
                    Toast.makeText(this, getString(R.string.select_user), Toast.LENGTH_SHORT).show();

                } else {

                    cancleUser();
                }
                break;
            default:
                break;
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void cancleUser() {

        shareUsers.remove(mSelectedUser);


        try {
            HLog.i(TAG,"mUserName"+mSelectedUser.mUserName);
            int toDelet = -1;

            JSONArray NewArray = new JSONArray();

            JSONArray arrays = new JSONArray(mDeviceEntity.mShareUsers);

            for (int i = 0; i < arrays.length(); i++) {

                JSONObject obje = arrays.getJSONObject(i);

                HLog.i(TAG,app.mSp.getCountryCode(""));
//                if (obje.has("phoneNumber") && obje.getString("phoneNumber").equals(app.mCountry+ mSelectedUser.mUserName)) {


//                if (obje.has("phoneNumber") && obje.getString("phoneNumber").equals(mSelectedUser.mUserName)) {
//                    toDelet = i;
//                    break;
//
//                }else{
//                    NewArray.put(obje);
//                }
                if (obje.has("phoneNumber") && !obje.getString("phoneNumber").equals(mSelectedUser.mUserName)) {
                    NewArray.put(obje);

                }


            }


            //because array.remove need api 19,so we do re cycle to remove a json object
//            JSONArray newArray=new JSONArray();
//            for (int i = 0; i < arrays.length(); i++) {
//                if(toDelet!=i){
//                    newArray.put(arrays.getJSONObject(i));
//                }
//                mDeviceEntity.mShareUsers = arrays.toString();
//
//
//            }
            if (toDelet >= 0) {
//TODO need Api 19
                arrays.remove(toDelet);



            }


//
//            if (toDelet >= 0) {
//                arrays.remove(toDelet);
//
//            }
            mDeviceEntity.mShareUsers = NewArray.toString();
            app.mDbManager.updateObject(mDeviceEntity, "mDeviceId", mDeviceEntity.mDeviceId);
        } catch (JSONException e) {
            HLog.e(TAG, e);

        } catch (Exception e) {
            HLog.e(TAG, e);
        }


        mAdapt.refresh();

        mBtnDel.setEnabled(mHasShare = shareUsers.size() > 0);
        postRequest(new Runnable() {

            @Override
            public void run() {

                final JSONObject json = new JSONObject();
                JSONObject parm = new JSONObject();
                try {
                    mCancleShare = System.currentTimeMillis() + "";
//                    parm.put("uid", app.mCountry+ mSelectedUser.mUserName);
                    parm.put("uid", mSelectedUser.mUserName);
                    parm.put("deviceName", mDeviceEntity.mName);
                    //TODO +86 check
                    parm.put("userName", app.mUser.userName);
//                    parm.put("userName", app.mUser.userName);
                    json.put("action", "cancelShare");
                    json.put("apikey", mDeviceEntity.mApiKey);
                    json.put("deviceid", mDeviceEntity.mDeviceId);
                    json.put("params", parm);
                    json.put("userAgent", "app");
                    json.put("sequence", mCancleShare);
                    HLog.i(TAG, "send cancel share:" + json);

                    ShareDeviceActvity.this.postWsRequest(new WsRequest(json) {
                        public void callback(String msg) {

                        }

                        ;
                    });

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });

    }

    public void createInviteDialog() {
        if (null == mShareDialog) {

            mShareDialog = new Dialog(this);
            mShareDialog.setTitle(getString(R.string.share_device)); // 设置标题
            View view = getLayoutInflater().inflate(R.layout.dialog_invite,
                    null);
            mShareDialog.setContentView(view);
            view.findViewById(R.id.btn_sure).setOnClickListener(
                    new OnShareListener());
            view.findViewById(R.id.btn_cancle).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            mShareDialog.dismiss();
                            mShareDialog = null;
                        }
                    });

            TextView title = (TextView) getLayoutInflater().inflate(
                    R.layout.dialog_title, null);
            title.setText(getString(R.string.share_device));

            mCountryCode = (TextView) view.findViewById(R.id.tv_country);
            mCountryCode.setText(app.mSp.getCountryCode(""));
            HLog.i(TAG,"TextCountryCode: "+app.mSp.getCountryCode(""));
            mCountryCode.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(ShareDeviceActvity.this, CountryCodeActivity.class);
                    intent.putExtra("key", "share");
                    startActivity(intent);
                }
            });
            mEt = (EditText) view.findViewById(R.id.et_share_to);
            mEt.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    mTvDash.setText("");

                }
            });

            mTvDash = (TextView) view.findViewById(R.id.tv_dash);

        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (mIsOwner) {
            mSelectedUser = (ShareUser) arg1.getTag();
            mAdapt.refresh();
        }

    }


    class OnShareListener implements OnClickListener {

        private String mShareRequestid;

        @Override
        public void onClick(View arg0) {
            app.mSp.saveCountryCode(mCountryCode.getText() + "");
            final JSONObject json = new JSONObject();
            try {
                String phone = mEt.getEditableText().toString();
                if (!NetHelper.isConnnected(getApplicationContext())) {
                    mTvDash.setText(getString(R.string.not_internet_current));
                    return;
                }
                //if (phone.length() != 11 || !phone.startsWith("1")) {
                if (phone.length() > 20) {
                    mTvDash.setText(getString(R.string.enter_phone_number));
                    return;
                }
                if (phone.equals(app.mUser.userName.replace(mCountryCode.getText(), ""))) {
                    mTvDash.setText(getString(R.string.not_share_own));
                    return;
                }
                if(TextUtils.isEmpty(phone.trim())){
                    mTvDash.setText(getString(R.string.enter_phone_number));
                    return;
                }
                for (ShareUser uSer : shareUsers) {
                    HLog.i(TAG,"uSerName:"+uSer.mUserName+",state:"+uSer.mState);
                    if (mEt.getEditableText().toString().equals(uSer.mUserName)&&uSer.mState==2&&uSer.mState==4) {
                        mTvDash.setText(getString(R.string.has_share));
                        return;
                    }


                }


                mShareDialog.dismiss();
                mShareDialog = null;

                JSONObject parm = new JSONObject();
//                parm.put("uid", app.mCountry + mEt.getEditableText().toString());
                parm.put("uid",mCountryCode.getText()+mEt.getEditableText().toString().trim());
                parm.put("deviceName", mDeviceEntity.mName);
//                parm.put("userName", app.mUser.userName.replace(app.mCountry, ""));
                parm.put("userName",app.mUser.userName);
                HLog.i(TAG,"uid:"+mCountryCode.getText()+mEt.getEditableText().toString().trim()+"userName："+app.mUser.userName);
                json.put("action", "share");
                json.put("apikey", mDeviceEntity.mApiKey);
                json.put("deviceid", mDeviceEntity.mDeviceId);
                json.put("params", parm);
                json.put("userAgent", "app");
                mShareRequestid = System.currentTimeMillis() + "";
                json.put("sequence", mShareRequestid);
                HLog.i(TAG, "send share:" + json);
//                mShareUser = new ShareUser(phone.replace(app.mCountry, ""), -1);
                String shareuser=mCountryCode.getText()+mEt.getEditableText().toString().trim();
                HLog.i(TAG,"add share user:"+shareuser);
                mShareUser = new ShareUser(shareuser, -1);
                shareUsers.add(mShareUser);
                mAdapt.refresh();
                Helper.addSelfKey(json, app.mUser.apikey);

                ShareDeviceActvity.this.postWsRequest(new WsRequest(json) {
                    @Override
                    public void callback(String msg) {

                        HLog.i(TAG, "ShareDeviceActvity call back");
                        try {
                            JSONObject json = new JSONObject(msg);
                            if (json.has("result")) {
                                int result = json.getInt("result");
                                mShareUser.mState = result;
                                refreshHandler.obtainMessage(0, mShareUser).sendToTarget();
                                refreshHandler.sendEmptyMessage(0);
                            }

                            if(json.has("error")&&json.getInt("error")==504){
                                mShareUser.mState = 5;
                                refreshHandler.obtainMessage(0, mShareUser).sendToTarget();
                                refreshHandler.sendEmptyMessage(0);
                            }


//                            "error":504,
                        } catch (JSONException e) {
                            HLog.e(TAG, e);
                        }
                    }
                });

                // json.p
            } catch (JSONException e) {
                HLog.e(TAG, e);
            }

        }

    }

    Handler refreshHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ShareUser user=(ShareUser)msg.obj;



            mBtnDel.setEnabled(mHasShare = shareUsers.size() > 0);

            mAdapt.refresh();
        }

        ;
    };


    public void saveNewUser(){
//        JSONArray arrays = new JSONArray("");
//
//        if (obje.has("phoneNumber")) {
//            user.mUserName = obje.getString("phoneNumber").replace(
//                    app.mCountry, "");
//        }
//        for (int i = 0; i < arrays.length(); i++) {
//
//            JSONObject obje = arrays.getJSONObject(i);
//
//            if (obje.has("phoneNumber") && obje.getString("phoneNumber").equals(app.mCountry + mSelectedUser.mUserName)) {
//                toDelet = i;
//                break;
//
//            }
//
//        }
//        if (toDelet >= 0) {
//            arrays.remove(toDelet);
//
//        }
//        mDeviceEntity.mShareUsers = arrays.toString();
//        app.mDbManager.updateObject(mDeviceEntity, "mDeviceId", mDeviceEntity.mDeviceId);
    }


    public void doRecieverShare(String msg) {
        try {
            JSONObject json = new JSONObject(msg);
            if (json.has("result")) {
                int result = json.getInt("result");
                if (2 == result) {
                    for (ShareUser user : shareUsers) {
                        // if(user.equals(msg.get))
                    }
                }

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

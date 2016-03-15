package elink;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.coolkit.AppHelper;
import com.coolkit.Auth;

import com.coolkit.common.HLog;
import com.coolkit.protocol.request.ProtocolHandler;
import com.coolkit.protocol.request.Result;

import java.util.HashMap;

import elink.common.Helper;
import elink.manager.SystemActionHolder;
import elink.model.DbManager;
import elink.utils.Cache;
import elink.utils.EncodeAndAssetsHelper;
import elink.utils.IntentHelper;
import elink.utils.SpHelper;

public class HkApplication extends Application {
    private static final String TAG = HkApplication.class.getSimpleName();
    private WifiManager mWifiManager;
    public DbManager mDbManager;
    public TpManager mTpManager;
    public String mCountry;
    public TeleManager mTele;

    public HashMap otaMap = new HashMap();
    public Cache mCache;

    public AppHelper mAppHelper = null;
    public SystemActionHolder mSysManager;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        HLog.i(TAG, "on created application");


    }

    /**
     * maker should change here,or he may have problem finding devices and controlling devices.
     */
    private void initAppidAndSecrete() {

        // APPID and APPSECREATE for maker to run test, if your are building a commercial project, you must apply for a private APPID and APPSECREATE , and init mAppid and mSecret
        String mAppid = "1xMdjbmOBYctEJfye4EjFLR2M6YpYyyJ";
        String mSecret = "hd9yf3DB7Q4UL6gx8iCfUGXwtYoxhCs5";


        //if you are using private   mAppid and mSecret, block this statement
        mAppHelper.setDebug("54.223.98.144", "54.223.98.144");

        mAppHelper.initApp(mAppid, mSecret);
    }

    public SpHelper mSp;
    public User mUser = new User();

    public int mHomeNetWork = 0;


    private void init() {
        HLog.init("test");
        mCache = new Cache();

        mHomeNetWork = getWifiManager().getConnectionInfo().getNetworkId();
        if (null == mSp) {
            mSp = new SpHelper(this);

        }

        mUser.userName = mSp.getUser();
        if (TextUtils.isEmpty(mUser.userName)) {
            mUser.isLogin = false;

        } else {
            HLog.i(TAG, "application UserName:" + mUser.userName);
            mUser.isLogin = mSp.isLogin(mUser.userName);
            mUser.pwd = mSp.getUserPwd(mUser.userName);

            mUser.at = mSp.getAt(mUser.userName);

            mUser.apikey = mSp.getApiKey(mUser.userName);
            mUser.nickName = mSp.getNick(mUser.userName);
            mUser.region = mSp.getRegion();
            HLog.i(TAG, "init application,at:" + mUser.at);

        }
        mCountry = "+" + Helper.getCountryCode(this);
        mSp.saveCountryCode(mCountry);

        mAppHelper = new AppHelper(this);

        initAppidAndSecrete();


        mAppHelper.isLogin = mUser.isLogin;
        HLog.i(TAG, "user has logined:" + mUser.isLogin);
        if (mUser.isLogin) {

            mAppHelper.setHost(mUser.at, mUser.apikey, mUser.region);
        } else {
            mAppHelper.setHost("", "", "");
        }


        mTele = new TeleManager(this);
        mDbManager = DbManager.getInstance(this);


        mTpManager = new TpManager(this);
        mSysManager = new SystemActionHolder(this);
    }


    public WifiManager getWifiManager() {
        if (null == mWifiManager) {
            mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        }
        return mWifiManager;
    }

    public static class User {
        //user id
        public String userName;
        public String pwd;
        public boolean isLogin;

        public String apikey;
        public String nickName;
        /**
         * region mean which host this user data will be saved
         */
        public String region;

        public String at;
    }


}

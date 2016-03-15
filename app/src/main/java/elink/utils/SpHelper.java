package elink.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.coolkit.common.HLog;

public class SpHelper {

    private SharedPreferences mSp;

    public SpHelper(Context context) {
        if (null == mSp) {
            mSp = context.getSharedPreferences("setting.xml",
                    Context.MODE_PRIVATE);
        }

    }

    public String getUser() {
        String lastUserName = mSp.getString("user_tocken", "");
        return lastUserName;
    }

    public boolean isUserSave() {
        return false;
    }

    public void saveUserLogin(String userName, String pwd, String at,
                              String apiKey, String nickName,String countryCode) {
        Editor edit = mSp.edit();
        edit.putString("user_tocken", userName);

        HLog.i("","yaoyiyaoyi, key:"+userName+"countryCode" +" value:"+countryCode);

        edit.putString(userName+"countryCode", countryCode);
        edit.putString(userName + "_at", at);
        edit.putString(userName + "_apikey", apiKey);
        edit.putString(userName + "_user_nick_name", nickName);

        edit.putBoolean(userName + "_isLogin", true);
        edit.commit();

    }


    public void saveUserLogin(String value) {
        Editor edit = mSp.edit();
        HLog.i("", "yaoyisave111:" + value);
        edit.putString("1111", value);
        edit.commit();

    }


    public void saveLogout(String userName) {
        Editor edit = mSp.edit();
        edit.putBoolean(userName + "_isLogin", false);

        edit.commit();
    }

    public void clearUser(String userName) {
        Editor edit = mSp.edit();
        edit.putString(userName, "");
        edit.commit();

    }

    public String getUserPwd(String userName) {
        // TODO Auto-generated method stub
        return mSp.getString(userName + "_pwd", "");
    }

    public boolean isLogin(String userName) {
        return mSp.getBoolean(userName + "_isLogin", false);
    }

    public boolean getUserEyeOn(String userName) {
        return mSp.getBoolean(userName + "_eye", false);

    }

    public String getNick(String userName) {
        return mSp.getString(userName + "_user_nick_name", "");
    }

    public String getApiKey(String userName) {
        return mSp.getString(userName + "_apikey", "");

    }

    public String getAt(String userName) {
        return mSp.getString(userName + "_at", "");
    }

    public String getWlanPwd(String ssid) {
        return mSp.getString(ssid + "_ssid", "");
    }

    public void saveWlanPwd(String ssid, String pwd) {
        Editor edit = mSp.edit();
        edit.putString(ssid + "_ssid", pwd);
        edit.commit();

    }

    public boolean getIsDefaultInserted(String mUser) {
        return mSp.getBoolean("group_edit_" + mUser, false);
    }

    public void setHasDefaultGroups(String mUser) {
        Editor edit = mSp.edit();
        edit.putBoolean("group_edit_" + mUser, true);
        edit.commit();
    }

    public boolean getIsRelease() {
        return mSp.getBoolean("host_release", true);

    }

    public void switchHost() {
        boolean isRelease = mSp.getBoolean("host_release", true);
        Editor edit = mSp.edit();
        edit.putBoolean("host_release", !isRelease);
        edit.commit();

    }

    public int getThreadCurrrent(String mVersion) {
        // TODO Auto-generated method stub
        return 0;
    }



    public void saveHomeSSID(String ssid) {
        Editor edit = mSp.edit();
        edit.putString("coolkit_home_ssid", ssid);
        edit.commit();
    }

    public String getHomeSSID() {
        return mSp.getString("coolkit_home_ssid", "");
    }

    public void saveNickName(String userName, String nickName) {
        Editor edit = mSp.edit();
        edit.putString(userName + "_user_nick_name", nickName);
        edit.commit();

    }





    public void saveNewVersion(String version) {
        Editor edit = mSp.edit();
        edit.putString("apk_latest_version", version);
        edit.commit();
    }

    public String getNewVersion() {

        return mSp.getString("apk_latest_version", "");

    }

    public String getRegion() {
        return mSp.getString(getUser() + "_region", "");
    }

    public void saveRegion(String region) {
        Editor edit = mSp.edit();
        edit.putString(getUser() + "_region", region);
        edit.commit();
    }

    public void saveDispatch(String mWsServer, int mWsPort) {
        Editor edit = mSp.edit();
        edit.putString(getUser() + "_dispath_server", mWsServer);
        edit.putInt(getUser() + "_dispath_port", mWsPort);
        edit.commit();
    }

    public String getWsServer() {
        return mSp.getString(getUser() + "_dispath_server", "");
    }

    public int getWsPort() {
        return mSp.getInt(getUser() + "_dispath_port", -1);

    }

    public void saveHearBeat(int hb, int hbInterval) {
        Editor edit = mSp.edit();
        edit.putInt("_ws_hb", hb);
        edit.putInt("_ws_hb_interval", hbInterval);
        edit.commit();
    }

    /**
     * 10 second default
     * @return
     */
    public int getHeadBeatInterval() {
        return mSp.getInt("_ws_hb_interval", 5);
    }

    /**
     * 1 means open, 0 means close
     * @return
     */
    public int getHeadBeat() {
        return mSp.getInt("_ws_hb", 1);
    }

    public void saveCountryCode(String mCountry) {
        Editor edit = mSp.edit();
        edit.putString("mCountrycode",mCountry);
        edit.commit();
    }

    public String getCountryCode(String s) {
        return mSp.getString("mCountrycode",s);
    }


    public void saveDebugDevice(int deviceui) {
        Editor edit = mSp.edit();
        edit.putInt("deviceui", deviceui);
        edit.commit();
    }

    public Integer getDebugDevice() {
        return mSp.getInt("deviceui",-1);
    }

    public boolean getIsDebugDevice() {
        return mSp.getBoolean("isDebugingDevice", false);
    }

    public void setIsDebuggingDevice(boolean isDebugging){
        Editor edit = mSp.edit();
        edit.putBoolean("isDebugingDevice", isDebugging);
        edit.commit();
    }

    public void saveSSIDPwd(String deviceid, String SSID,String SSID_pwd){
        Editor editor  = mSp.edit();
        editor.putString(deviceid+"_ssid", SSID);
        editor.putString(deviceid + "_pwd", SSID_pwd);
        editor.commit();
    }

    public String getWlanSSIDPwd(String deviceid){
        return mSp.getString(deviceid+"_pwd","");
    }

    public String getWlanSSID(String deviceid){
        return mSp.getString(deviceid+"_ssid", "");
    }



    public long getLastVersionCheck() {
       return mSp.getLong("last_versioncheck", 0l);

    }

    public void saveLastVersionCheck(long timer){
        Editor edit = mSp.edit();
        edit.putLong("last_versioncheck", timer);
        edit.commit();

    }

    public String getloginCode(String userName){
        String value=mSp.getString(userName + "countryCode", "");
        HLog.i("", "yaoyiyaoyi2,get key:" + userName + "countryCode" + " value:" + value);

        return value;
    }

    public boolean getIsFirstCheck(String version) {
                return mSp.getBoolean("isFirstCheck_" + version, true);
    }

    public void setIsNotFirstCheck(String version) {
        Editor edit = mSp.edit();
        edit.putBoolean("isFirstCheck_" + version,false);
        edit.commit();
    }

    public String getKeepType(String deviceId) {
        //默认为恒温器
        return mSp.getString("keeper_type_"+deviceId,"temperature");
    }

    public void saveKeetype(String deviceid,String  type){
        Editor edit = mSp.edit();
        edit.putString("keeper_type_"+deviceid, type);
        edit.commit();
    }
}

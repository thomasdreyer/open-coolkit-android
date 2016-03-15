package elink.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import elink.common.UiHelper;
import demo.demo.R;
import com.coolkit.common.HLog;
import com.coolkit.common.WsRequest;

import elink.HkConst;
import elink.common.Helper;
import elink.entity.DeviceEntity;
import elink.model.DbManager;
import elink.utils.DialogHelper;
import elink.utils.NetHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DeviceManufactorActivity extends BasicActivity implements OnClickListener {

    private static final String TAG = DeviceManufactorActivity.class.getSimpleName();
    public static final int MSG_OTA_ING = 5;
    public static final int MSG_OTA_OK = 4;
    public static final int MSG_OTA_FAIL = 3;
    public static final int MSG_OTA_RESULT_TIMER = MSG_OTA_ING + 1;
    private String manu;
    private String fw;
    private String des;

    private DeviceEntity mDeviceEntity;
    private Dialog mOtaConfireDialog;
    private Dialog mOtaDialog;
    private BaseHelper basicHeper;
    private WebView mwebview;
    private java.lang.String weburl=null;
    private TextView otatitle;
    private TextView otaversion;
    private TextView otagradeversion;
    private RelativeLayout lineotagrade;
    ImageView imgupgradeinfo;
    private TextView tvotainfo;
    private int i=0;

    public Handler uiHandler = new Handler(){

        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case MSG_OTA_OK:
                    showOtaOkay((String) msg.obj);
                    if (null!=basicHeper.app){


                      //
                        Helper.broadcastSynLocalDevice(DeviceManufactorActivity.this);
                        mDeviceEntity = DbManager.getInstance(DeviceManufactorActivity.this).queryDeviceyByDeviceId(mID);
                        HLog.i(TAG, "manu :" + manu + " fw:" + fw + " des:" + des + " mid:" + mID + " mDeviceEntity:" + mDeviceEntity);
                        HLog.i(TAG, "mDeviceEntity.mUpdateInfo:: :" + mDeviceEntity.mUpdateInfo);
                        if (null == mDeviceEntity) {
                            finish();
                            HLog.i(TAG, "has no Device entity ,finish()");
                            return;
                        }
                        updateUI();
                                        }
                    break;
                case MSG_OTA_FAIL:
                    showOtaError((String) msg.obj);
                    break;
                case MSG_OTA_ING:

                    showOtaIng((String) msg.obj);

                    Message msg2 = obtainMessage(MSG_OTA_RESULT_TIMER, (String) msg.obj);
                    sendMessageDelayed(msg2, 300 * 1000);
                    break;
                case MSG_OTA_RESULT_TIMER:
                    HLog.i(TAG,"ota over timer ");

                    showOtaFinish((String) msg.obj, true);
                    break;
                default:
                    break;
            }
        }
    };
    private ImageView loadimg;
    private TextView tvinfo;
    private String mID;
    private String version;
    private HashMap otaMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manufactor);
        basicHeper=new BaseHelper(DeviceManufactorActivity.this);
        HLog.i(TAG, "weburl:" + weburl);
        mBaseHandler.sendEmptyMessage(0);
    }

    @Override
    public void initDatas() {
        super.initDatas();
        otaMap=app.otaMap;
        HLog.i(TAG, "init datas");

        Intent intent = this.getIntent();
         manu = intent.getStringExtra(HkConst.EXTRA_MANUFACTOR);
         fw = intent.getStringExtra(HkConst.EXTRA_FW_VERSION);
        des = intent.getStringExtra(HkConst.EXTRA_DES);
         mID = intent.getStringExtra(HkConst.EXTRA_D_ID);
        mDeviceEntity = DbManager.getInstance(this).queryDeviceyByDeviceId(mID);
        HLog.i(TAG, "manu :" + manu + " fw:" + fw + " des:" + des + " mid:" + mID + " mDeviceEntity:" + mDeviceEntity);
        if (null == mDeviceEntity) {
            finish();
            HLog.i(TAG, "has no Device entity ,finish()");
            return;
        }

    }


    @Override
    public void initViews() {
        super.initViews();
        if(null!=mDeviceEntity){
            HLog.i(TAG,"DeviceMAnufactorActivity: initViews");
            otatitle = (TextView) findViewById(R.id.tv_ota_title);
            otaversion = (TextView) findViewById(R.id.tv_ota_version);
            otagradeversion = (TextView) findViewById(R.id.tv_ota);
            tvotainfo = (TextView) findViewById(R.id.tv_otainfo);
            lineotagrade = (RelativeLayout) findViewById(R.id.line_upgrade_ota);

            imgupgradeinfo = (ImageView) findViewById(R.id.img_upgradeinfo);

            loadimg = (ImageView) findViewById(R.id.loadimg);
            tvinfo = (TextView) findViewById(R.id.tv_infodev);
            final ProgressBar bar = (ProgressBar) findViewById(R.id.myProgressBar);
            mwebview = (WebView) findViewById(R.id.webView);
            WebSettings webSettings = mwebview.getSettings();
            webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
            webSettings.setLoadWithOverviewMode(true);//自适应屏幕
            webSettings.setJavaScriptEnabled(true);//调用javascript
            webSettings.supportMultipleWindows();  //多窗口
            webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
            webSettings.setBuiltInZoomControls(true);
            webSettings.setSupportZoom(true);//页面支持缩放
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//LOAD_NO_CACHE  LOAD_CACHE_ELSE_NETWORK 设置缓存
            mwebview.requestFocusFromTouch(); //获取手势焦点
            HLog.i(TAG, "mDeviceEntity.mDesUrl :" + mDeviceEntity.mDesUrl);
            weburl = mDeviceEntity.mDesUrl;
            if (!TextUtils.isEmpty(weburl)) {
                mwebview.loadUrl(weburl);
            } else {
                mwebview.setVisibility(View.GONE);
                loadimg.setVisibility(View.VISIBLE);
                tvinfo.setVisibility(View.VISIBLE);
            }

            mwebview.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {

                    if (newProgress == 100) {
                        bar.setVisibility(View.INVISIBLE);

                    } else {
                        if (View.INVISIBLE == bar.getVisibility()) {
                            bar.setVisibility(View.VISIBLE);
                        }
                        bar.setProgress(newProgress);
                    }
                    super.onProgressChanged(view, newProgress);
                }
            });

            mwebview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    HLog.i(TAG, "i:" + i);
                    view.loadUrl(url);
                    i++;
                    HLog.i(TAG, "mwebview.getOriginalUrl():" + mwebview.getOriginalUrl());
                    HLog.i(TAG, "webView.getUrl();:" + mwebview.getUrl());
                    return true;
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();
                }
            });
            updateUI();
        }else{
            finish();
            UiHelper.showShortToast(this,getString(R.string.data_exception));
            return;
        }

    }

    private void updateUI() {
        HLog.i(TAG, "mDeviceEntity.mFwVersion:" + mDeviceEntity.mFwVersion + fw);
        if(!TextUtils.isEmpty(mDeviceEntity.mFwVersion)){
            otaversion.setText(mDeviceEntity.mFwVersion);


            if (mDeviceEntity.mOnLine.equals("true")) {
                HLog.i(TAG, "mUpdateInfo:" + mDeviceEntity.mUpdateInfo + " mOwer:" + mDeviceEntity.mOwer);
                if (!TextUtils.isEmpty(mDeviceEntity.mUpdateInfo) && TextUtils.isEmpty(mDeviceEntity.mOwer)) {
                    try {
                        JSONObject objs = new JSONObject(mDeviceEntity.mUpdateInfo);
                        if (objs.has("version")) {
                            version = objs.getString("version");
                            tvotainfo.setVisibility(View.VISIBLE);//可点提示
                            otagradeversion.setVisibility(View.VISIBLE);//可更新版本
                            otagradeversion.setText(version);
                            imgupgradeinfo.setImageDrawable(getResources().getDrawable(R.drawable.info_notupgrade));
                            lineotagrade.setBackgroundColor(getResources().getColor(R.color.yellow));
                            lineotagrade.setOnClickListener(DeviceManufactorActivity.this);
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }


                } else {
                    imgupgradeinfo.setImageDrawable(getResources().getDrawable(R.drawable.info_upgrade));
                    lineotagrade.setBackgroundColor(getResources().getColor(R.color.white));
                    tvotainfo.setVisibility(View.GONE);//隐藏可点提示
                    otagradeversion.setText(R.string.notgrade);//显示不可升级,已经是最新版本
                    otagradeversion.setVisibility(View.VISIBLE);

                }


            }else {
                HLog.i(TAG, "mDeviceEntity.mOnLine：" + mDeviceEntity.mOnLine + fw);
                Toast.makeText(DeviceManufactorActivity.this, R.string.lineoff_device,Toast.LENGTH_SHORT).show();
                otagradeversion.setText(getResources().getString(R.string.notversion));
                otagradeversion.setVisibility(View.VISIBLE);
            }
        }else {
            imgupgradeinfo.setVisibility(View.GONE);
            otatitle.setVisibility(View.GONE);
            otaversion.setText("");
            otagradeversion.setText(R.string.notgrade);//显示不可升级,已经是最新版本
            otagradeversion.setVisibility(View.VISIBLE);

        }




    }

    private void localImage() {
        try {
            // 本地文件处理
            String str = "file:///android_asset/guides.png";
            mwebview.loadUrl(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        HLog.i(TAG, "onKeyDown:");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            HLog.i(TAG,"keydown i:"+ i);
            if ((keyCode == KeyEvent.KEYCODE_BACK) && mwebview.canGoBack()/*&&i>=1*/) {
                HLog.i(TAG,"onKeyDown: mwebview.goBack");
                i=i-2;
                mwebview.goBack();
                return true;
            }else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void setViews() {
        super.setViews();
        HLog.i(TAG, "set views");

        mTitle =getResources().getString(R.string.device_info_title);
//        mTitle = getString(R.string.device_information);
//        mTitle = getString(R.string.productinfo);
        setView();
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.line_upgrade_ota:
                mDeviceEntity = DbManager.getInstance(this).queryDeviceyByDeviceId(mID);
                if (!"true".equals(mDeviceEntity.mOnLine)) {
                    Toast.makeText(this, R.string.info_updateerr,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (otaMap.containsKey(mDeviceEntity
                        .mDeviceId)) {
                    Toast.makeText(DeviceManufactorActivity.this, R.string.deviceupdate, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!NetHelper.isConnnected(this)){
                    Toast.makeText(this, R.string.not_internet_current,Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    doOTA(mDeviceEntity);
                }

                break;
            default:
                break;
        }
    }

    private void showOtaIng(final String id) {
        final String name = (String) otaMap.get(id);
        HLog.i(TAG,"name:"+name);
        mOtaDialog = DialogHelper.createProgressDialog(this, getString(R.string.firmware_upgradeing, name));
        mOtaDialog.show();
        mOtaDialog.setCanceledOnTouchOutside(false);

        mOtaDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                HLog.i(TAG,"mOtaDialog onDismiss:"+otaMap.containsKey(id));
                if (otaMap.containsKey(id)) {
                    UiHelper.showShortToast(basicHeper.app, getString(R.string.background_updateing,name));
                }

            }
        });
    }

    private void showOtaFinish(String obj, boolean show) {
        HLog.i(TAG, "showOtaFinish  :"+otaMap.containsKey(obj));
        if (otaMap.containsKey(obj)) {
            HLog.i(TAG, "ota finish overtimer");

            String name = (String) otaMap.get(obj);
            otaMap.remove(obj);
            Context context=basicHeper.app;
            if (show && null !=context ) {
                HLog.i(TAG, "ota finish ota_finish_upgrade");
                Toast.makeText(context, context.getString(R.string.ota_finish_upgrade, name),Toast.LENGTH_SHORT).show();


            }
            dissMissOta(obj);
        } else {
            HLog.i(TAG, "ota finish overtimer,but has no key");
        }

    }

    private void showOtaError(String id) {
        String name = (String) otaMap.get(id);
        otaMap.remove(id);

        if(null != basicHeper.app) {
            UiHelper.showShortToast(basicHeper.app, getString(R.string.ota_failed_upgrade,name));

        }

        dissMissOta(id);

    }

    private void showOtaOkay(String id) {
        String name = (String) otaMap.get(id);
        otaMap.remove(id);
        if(null != basicHeper.app) {
            Toast.makeText(basicHeper.app, getString(R.string.ota_success_upgrade,name), Toast.LENGTH_SHORT).show();
            dissMissOta(id);
        }
    }
class  OnclickOkay implements View.OnClickListener{
    public DeviceEntity entity;
    private JSONObject obj;

    OnclickOkay(DeviceEntity entity1,JSONObject obj1){
        entity=entity1;
        obj=obj1;
    }
    @Override
            public void onClick(View v) {
                try {
                    Message msg = uiHandler.obtainMessage(MSG_OTA_ING);
                    msg.obj = entity.mDeviceId;
                    HLog.i(TAG,"before_otamap:"+otaMap.toString());
                    otaMap.put(entity.mDeviceId, entity.mName);
                    HLog.i(TAG,"after_otamap:"+otaMap.toString());
                    msg.sendToTarget();
                    JSONObject json = new JSONObject();
                    json.put("action", "upgrade");
                    json.put("apikey", entity.mApiKey);
                    json.put("deviceid", entity.mDeviceId);
                    json.put("userAgent", "app");
                    json.put("sequence", System.currentTimeMillis() + "");
                    obj.remove("upgradeText");
                    json.put("params", obj);
                    HLog.i(TAG,"json:"+json);
                    postWsRequest(new WsRequest(json.toString().replaceAll("\\\\/", "/")) {
                        @Override
                        public void callback(String msg) {
                            HLog.i(TAG, "do ota device,msg:" + msg);
                            try {
                                JSONObject json = new JSONObject(msg);

                                boolean failure=json
                                        .has("error")
                                        && 0 != json
                                        .getInt("error");
                                HLog.i(TAG,"ota fw:"+entity.mFwVersion+" to "+version+" "+!failure);
                                if(!failure){
                                    entity.mUpdateInfo="";
                                    entity.mFwVersion=version;
                                    app.mDbManager.updateObject(entity, "mDeviceId", entity.mDeviceId);

                                }
                                uiHandler.obtainMessage( failure? MSG_OTA_FAIL
                                        : MSG_OTA_OK, entity.mDeviceId).sendToTarget();
                                uiHandler.obtainMessage(json.has("error") &&
                                        0 != json.getInt("error") ? MSG_OTA_FAIL : MSG_OTA_OK, entity.mDeviceId).sendToTarget();
                            } catch (JSONException e) {
                                HLog.e(TAG, e);
                            } catch (Exception e) {
                               HLog.e(TAG, e);
                            }

                            super.callback(msg);
                        }

                    });
                    mOtaConfireDialog.dismiss();
                    mOtaConfireDialog.dismiss();
                } catch (JSONException e) {
                    HLog.e(TAG, e);
                }

    }
}

    private void dissMissOta(String id) {
        if (null != mOtaDialog && mOtaDialog.isShowing()) {
            otaMap.remove(id);
            mOtaDialog.dismiss();
        }
    }

    private void doOTA(final DeviceEntity entity) {
        if (null != entity) {
            if (!"true".equals(entity.mOnLine)) {
                Toast.makeText(DeviceManufactorActivity.this, R.string.lineoff_device, Toast.LENGTH_SHORT).show();
                return;
            }

            if(!NetHelper.isConnnected(this)){
                Toast.makeText(this, R.string.not_internet_current,Toast.LENGTH_SHORT).show();
                return;
            }

            if (otaMap.containsKey(entity.mDeviceId)) {
                Toast.makeText(DeviceManufactorActivity.this, R.string.deviceupdate, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                if (!TextUtils.isEmpty(entity.mUpdateInfo)) {

                final JSONObject obj = new JSONObject(entity.mUpdateInfo);

                if (obj.has("version")) {

                    String version = obj.getString("version");
                    String text = getString(R.string.device_upgraded_to) + version
                            + getString(R.string.info_wait_upgrade);

                    HLog.i(TAG,"version:"+version);
          mOtaConfireDialog = UiHelper.showConfireDialog("", text,new OnclickOkay(entity,obj), new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    HLog.i(TAG,"mOtaConfireDialog.dismiss()");
                                    if (null != mOtaConfireDialog) {
                                        mOtaConfireDialog.dismiss();
                                    }

                                }
                            }, this);
                    mOtaConfireDialog.show();

                }
            }
            } catch (Exception e1) {
                HLog.e(TAG, e1);
                if (null != entity) {
                    HLog.e(TAG, entity.mUpdateInfo);
                }
            }

        }
    }


}

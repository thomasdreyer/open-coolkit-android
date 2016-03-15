package elink.manager;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.coolkit.common.HLog;
import com.coolkit.protocol.request.OtaProtocol;
import com.coolkit.protocol.request.ProtocolHandler;
import com.coolkit.protocol.request.Result;

import elink.common.UiHelper;
import elink.common.Helper;
import elink.activity.BaseHelper;
import elink.entity.DeviceEntity;
import elink.utils.ThreadExecutor;

public class OtaManager implements OnClickListener {
	private static final String TAG = OtaManager.class.getSimpleName();

	protected List<DeviceEntity> devices;
	BaseHelper mHelper=null;

	public OtaManager(BaseHelper helper) {
		mHelper = helper;

	}

	public void doCheckOta() {
		this.checkVersion();
	}

	private void checkVersion() {
		ThreadExecutor.execute(new Runnable() {

			@Override
			public void run() {
				HLog.i(TAG, "do check device ota");
				devices = mHelper.app.mDbManager.getUserDevice("");
				boolean canCheck=false;
				JSONArray array=new JSONArray();

				if (null != devices) {
					for (DeviceEntity deviceEntity : devices) {
						JSONObject aDevice = new JSONObject();
						if(TextUtils.isEmpty(deviceEntity.mModel)||TextUtils.isEmpty(deviceEntity.mFwVersion)||(TextUtils.isEmpty(deviceEntity.mDeviceId))
								)
							continue;;
						canCheck=true;
						try {
							aDevice.put("model", deviceEntity.mModel);
							aDevice.put("version", deviceEntity.mFwVersion);
							aDevice.put("deviceid", deviceEntity.mDeviceId);
							array.put(aDevice);
						} catch (JSONException e) {
							HLog.e(TAG,e);
						}

					}
					if(canCheck){
						new OtaProtocol(mHelper.app.mAppHelper).checkDevice(new ProtocolHandler(mHelper.app,0,
								new ResultCallback()),array,mHelper.app.mUser.at);
					}
				}


			}
		});

	}

	public DeviceEntity findEntity(String deviceId) {
		if (null != devices) {
			for (DeviceEntity entity : devices) {
				if (deviceId.equals(entity.mDeviceId)) {
					return entity;
				}

			}
		}
		return null;
	}

	class ResultCallback implements ProtocolHandler.CallBack {

		ResultCallback() {
		}

		// {"rtnCode":0,"upgradeInfoList":[{"bizRtnCode":10002,"deviceid":"10000004cd"},{"bizRtnCode":10001,"deviceid":"10000000d3","model":"PSB-A04-CN","version":"0.2.0","binList":[{"downloadUrl":"http://dl.itead.cn/ota/rom/opq2323232/user1.1024.new.bin","digest":"2a7d35bb7847b150968d40fb7aee90df8a1cc3098e911748601ecdc795e564e1","name":"user1.bin"},{"downloadUrl":"http://dl.itead.cn/ota/rom/rst45655354/user2.1024.new.bin","digest":"58848b27a355053ebb2038eb58a476c3ca7c7b09b5c6d8b8759a837aa89cceaa","name":"user2.bin"}]},{"bizRtnCode":10002,"deviceid":"10000005c4"},{"bizRtnCode":10001,"deviceid":"10000004f8","model":"PSC-B01-CN","version":"0.2.1","binList":[{"downloadUrl":"http://dl.itead.cn/ota/rom/test_user1.bin","digest":"2bca5ab86ec56ef892744399c87bf88f8cf502d4af2179188ed05d431b5e20f9","name":"user1.bin"},{"downloadUrl":"http://dl.itead.cn/ota/rom/test_user2.bin","digest":"a0bdd3890a375971958c3eff9a2e1291243d321ac5eda31ad086327042705f2f","name":"user2.bin"}]},{"bizRtnCode":10002,"deviceid":"10000004f7"}]}

		@Override
		public void callBack(Result result) {

			HLog.i(TAG, "do parse upgrade info：" + result.mMsg);
			try {
				boolean update = false;
				if (200 == result.mCode && !TextUtils.isEmpty(result.mMsg)) {

					JSONObject json = new JSONObject(result.mMsg);
					if (json.has("rtnCode") && 0 == json.getInt("rtnCode")
							&& json.has("upgradeInfoList")) {
					
						String upgradeList = json.getString("upgradeInfoList");
						if (!TextUtils.isEmpty(upgradeList)) {
							
							HLog.i(TAG, "do upgradeList is:"+upgradeList);
						
							JSONArray array = new JSONArray(upgradeList);
							
							
							HLog.i(TAG, "do parse upgrade info：" + array);
							for (int i = 0; i < array.length(); i++) {
								JSONObject tmp = array.getJSONObject(i);
								JSONObject upInfo=new JSONObject();
								if (tmp.has("deviceid")
										&& tmp.has("bizRtnCode")
										&& tmp.has("model")
										&& tmp.has("version")
										&& tmp.has("binList")) {

									DeviceEntity entiy = findEntity(tmp
											.getString("deviceid"));
									int bizRtnCode = tmp.getInt("bizRtnCode");
									HLog.i(TAG, "find entity ："
											+ ((null == entiy) ? "null"
													: entiy.mName)
											+ " bizRtnCode:" + bizRtnCode);

									if (10001 == bizRtnCode
											&& null != entiy
											&& tmp.getString("model").equals(
													entiy.mModel)) {
										HLog.i(TAG, "check update info");

										String newVersion = tmp
												.getString("version");
										boolean compareVersion = UiHelper
												.Compare(entiy.mFwVersion,
														newVersion) < 0;
										HLog.i(TAG, "compareVersion："
												+ entiy.mName + " has new ota:"
												+ compareVersion);
										if (compareVersion) {
											upInfo.put("model",tmp.get("model"));
											upInfo.put("version",tmp.get("version"));
											upInfo.put("binList",tmp.get("binList"));
											entiy.mUpdateInfo = upInfo+"";
											HLog.i(TAG, "mUpdateInfo："+entiy.mUpdateInfo );
											mHelper.app.mDbManager
													.updateObject(entiy,
															entiy.mId);
											update = true;
										}

									}
								}

							}
							if (update) {
								Helper.broadcastSynLocalDevice(mHelper.app);
							}
						}
					}
				}
			} catch (JSONException e) {
				HLog.e(TAG, e);
			} catch (Exception e) {
				HLog.e(TAG, e);
			}

		}
	}

	@Override
	public void onClick(View v) {

	}

}

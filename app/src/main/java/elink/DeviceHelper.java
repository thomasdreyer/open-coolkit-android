package elink;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import demo.demo.R;
import com.coolkit.common.HLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import elink.activity.BasicActivity;
import elink.activity.details.DetailHelper;

import elink.activity.details.SwitchHelper;
import elink.entity.DeviceEntity;
import elink.entity.Timer;
import elink.model.DbManager;
import elink.utils.Debugg;

public class DeviceHelper {

	public static final int UI_DEAULT = 0;
	public static final int UI_PLUG_FOUR = 1;
	public static final int UI_PLUG_SINGLE = 2;
	public static final int UI_WING = 3;
	public static final int UI_WINDOW = 4;
	public static final int UI_PLUG_WATER = 5;
	public static final int UI_TRANSE = 6;
	public static final int UI_SWITH_ONE = 7;
	public static final int UI_SWITH_TWO = 8;
	public static final int UI_SWITH_THREE = 9;
	public static final int UI_SWITH_FOUR = 10;
	public static final int UI_PLUG_SINGLE_WITH_POWER = 11;
	public static final int UI_PLUG_TWO = 12;
	public static final int UI_PLUG_THREE = 13;
	public static final int UI_PLUG_YOUDING = 14;
	public static final int UI_PLUG_CURTAIN = 15;
	public static final int UI_PLUG_FIRE_PLACE = 16;
	public static final Integer UI_CAMERA = 17;
	public static final int UI_POWER_PLUGIN=18;
	public static final int UI_TEMPERATURE_AND_HUMIDITY_KEEPER = UI_POWER_PLUGIN+1;


	private static final String TAG = DetailHelper.class.getSimpleName();

	public static boolean isSwitch(int uiType) {
		return (uiType == DeviceHelper.UI_SWITH_ONE)
				|| (uiType == DeviceHelper.UI_SWITH_TWO)
				|| (uiType == DeviceHelper.UI_SWITH_THREE)
				|| (uiType == DeviceHelper.UI_SWITH_FOUR);

	}

	public static boolean isPlug(int uiType) {
		return (uiType == DeviceHelper.UI_PLUG_SINGLE)
				|| (uiType == DeviceHelper.UI_PLUG_TWO)
				|| (uiType == DeviceHelper.UI_PLUG_THREE)
				|| (uiType == DeviceHelper.UI_PLUG_FOUR)
				|| (uiType == DeviceHelper.UI_PLUG_SINGLE_WITH_POWER);
	}

	public static boolean isWater(int uiType) {
		return (uiType == DeviceHelper.UI_PLUG_WATER);
	}


	public static DetailHelper setDetailHelper(BasicActivity context, int ui) {
		DetailHelper helper = new SwitchHelper(context);
		return helper;
	}


	public static void getUi(DeviceEntity entity, JSONObject extra)
			throws JSONException {
		Object ui = extra.get("ui");
		if ("四通道插座".equals(ui)) {
			entity.mUi = DeviceHelper.UI_PLUG_FOUR;

		} else if ("单通道插座".equals(ui)||"开关改装模块".equals(ui)) {
			entity.mUi = DeviceHelper.UI_PLUG_SINGLE;
			// TODO　: re design

			if (extra.has("description")) {

				if ("饮水机自用1".equals(extra.getString("description"))) {
					entity.mUi = DeviceHelper.UI_PLUG_WATER;

				}
				entity.mDes = extra.getString("description");
			}

		} else if ("双通道插座".equals(ui)) {
			entity.mUi = DeviceHelper.UI_PLUG_TWO;
		} else if ("三通道插座".equals(ui)) {
			entity.mUi = DeviceHelper.UI_PLUG_THREE;

		} else if ("透传模块".equals(ui)) {
			entity.mUi = DeviceHelper.UI_TRANSE;
		} else if ("单通道开关".equals(ui)) {
			entity.mUi = DeviceHelper.UI_SWITH_ONE;
		} else if ("双通道开关".equals(ui)) {
			entity.mUi = DeviceHelper.UI_SWITH_TWO;
		} else if ("三通道开关".equals(ui)) {
			entity.mUi = DeviceHelper.UI_SWITH_THREE;
			// entity.mUi = DeviceHelper.UI_PLUG_FIRE_PLACE;
		} else if ("四通道开关".equals(ui)) {
			entity.mUi = DeviceHelper.UI_SWITH_FOUR;
		}



	}

	public static Drawable getDeviceListIcon(Context context, int uiType,
			boolean onLine) {
		if (isPlug(uiType)) {
//			return context.getResources().getDrawable(
//					onLine ? R.drawable.switch_state_opne
//							: R.drawable.switch_state_close);
			return context.getResources().getDrawable(
					onLine ? R.drawable.switch_state_opne
							: R.drawable.new_offline_switch);
		} else if (DeviceHelper.UI_WING == uiType) {
			return context.getResources().getDrawable(
					onLine ? R.drawable.efanon : R.drawable.efanoff);

		} else if (DeviceHelper.UI_PLUG_WATER == uiType) {
			return context.getResources().getDrawable(
					onLine ? R.drawable.wateron : R.drawable.wateroff);
		} else if (isSwitch(uiType)) {
			return context.getResources().getDrawable(
					onLine ? R.drawable.switch_item_open
							: R.drawable.switch_item_close);
		} else if (DeviceHelper.UI_PLUG_YOUDING == uiType) {
			return context.getResources().getDrawable(
					onLine ? R.drawable.youding_state_open
							: R.drawable.youding_item_close);
		} else if (DeviceHelper.UI_TRANSE == uiType && false) {
			return context.getResources().getDrawable(
					onLine ? R.drawable.air_on : R.drawable.air_off);
		} else if (DeviceHelper.UI_PLUG_FIRE_PLACE == uiType) {
			return context.getResources().getDrawable(
					onLine ? R.drawable.fire_place_on
							: R.drawable.fire_place_off);
		}

//		return context.getResources().getDrawable(
//				onLine ? R.drawable.switch_state_opne
//						: R.drawable.switch_state_close);
		return context.getResources().getDrawable(
				onLine ? R.drawable.switch_state_opne
						: R.drawable.new_offline_switch);
	}

	public static int getImageViewState(boolean isOpen, int ui) {
		if (ui == UI_PLUG_FIRE_PLACE) {
			return isOpen ? R.drawable.fire_place_state_on
					: R.drawable.fire_place_state_off;
		} else {
			// TODO
			return 0;
		}

	}

	public static DeviceEntity getDevice(JSONObject json, HkApplication context)
			throws JSONException, IllegalAccessException {
		DeviceEntity entity = new DeviceEntity();
		if (json.has("_id")) {

			entity.mId = UUID.randomUUID() + "";
		}
		if (json.has("name")) {

			entity.mName = json.getString("name");
		}
		if (json.has("type")) {

			entity.mType = json.getString("type");
		}
		if (json.has("deviceid")) {

			entity.mDeviceId = json.getString("deviceid");
		}
		if (json.has("apikey")) {

			entity.mApiKey = json.getString("apikey");
		}
		if (json.has("extra")) {

			entity.mExtra = json.getString("extra");
		}
		if (json.has("__v")) {
			entity.mV = json.getString("__v");
		}
		if (json.has("params")) {
			entity.mParams = json.getString("params");
			if (!TextUtils.isEmpty(entity.mParams)) {
				JSONObject params = new JSONObject(entity.mParams);
				if (params.has("timers")) {

					JSONArray timers = params.getJSONArray("timers");
					for (int j = 0; j < timers.length(); j++) {
						JSONObject timer = timers.getJSONObject(j);
						Timer aTimer = new Timer();
						aTimer.mId = UUID.randomUUID() + "";
						if (timer.has("at")) {
							aTimer.at = timer.getString("at");

						}
						if (timer.has("type")) {
							aTimer.typ = timer.getString("type");
						}
						if (timer.has("enable")) {
							aTimer.enable = timer.getBoolean("enable");
						}
						if (timer.has("do")) {
							aTimer.doAction = timer.getString("do");
						}
						if(timer.has("startDo")){
							aTimer.startDo=timer.getString("startDo");
						}
						if(timer.has("endDo")){
							aTimer.endDo=timer.getString("endDo");
						}
						aTimer.deviceId = entity.mDeviceId;
						DbManager.getInstance(context).inSert(aTimer);
					}

				}

				if (params.has("fwVersion")) {
					entity.mFwVersion = params.getString("fwVersion");
				}

			}
		}

		if (json.has("online")) {

			entity.mOnLine = json.getString("online");
		}
		/*add deviceUrl*/
		if (json.has("deviceUrl")) {

			entity.mDesUrl = json.getString("deviceUrl");
		}/* add brandName productModel*/
		if (json.has("brandName")) {
			entity.mBrand = json.getString("brandName");
		}if (json.has("productModel")) {
			entity.mProductModel = json.getString("productModel");
		}
		if (json.has("createdAt")) {

			entity.mCreateTime = json.getString("createdAt");
		}

		if (json.has("sharedTo")) {

			entity.mShareUsers = json.getString("sharedTo");
		}
		if (json.has("sharedBy")) {
			entity.mOwer = json.getString("sharedBy");

		}



		if (!TextUtils.isEmpty(entity.mExtra)) {
			JSONObject ob = new JSONObject(entity.mExtra);
			if (ob.has("extra")) {
				JSONObject extra = new JSONObject(ob.getString("extra"));
				if (extra.has("ui")) {
					HLog.i(TAG,"set ui:"+extra.get("ui"));
					DeviceHelper.getUi(entity, extra);
					//模拟设备
					if(Debugg.DEBUG_IOT_2_0.equals(Debugg.DEBUG_HOST_NAME)&&context.mSp.getIsDebugDevice()){
						entity.mUi =context.mSp.getDebugDevice();

					}
					//
				}


				if (extra.has("model")) {
					entity.mModel = extra.getString("model");
				}

				if (extra.has("description")) {
					entity.mDes = extra.getString("description");
				}
				if (extra.has("manufacturer")) {
					entity.mManufact = extra.getString("manufacturer");
				}

			}

		}
		HLog.i(TAG,"set curtain");

		return entity;
	}

}
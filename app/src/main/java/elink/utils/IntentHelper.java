package elink.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.coolkit.common.HLog;

import elink.HkApplication;
import elink.HkConst;
import elink.activity.AddDeviceActivity;
import elink.activity.AddTimerActivity;
import elink.activity.DetailSwitchActivity;
import elink.activity.DeviceActivity;
import elink.activity.DeviceManufactorActivity;
import elink.activity.DialogActivity;
import elink.activity.SetInfoActvity;
import elink.activity.SetNewTimerActvity;
import elink.activity.SetNewVirtualTimerActvity;
import elink.activity.SetTimerActvity;
import elink.activity.SetUserAuthActivity;
import elink.activity.SetUserInfoActvity;
import elink.activity.ShareDeviceActvity;
import elink.activity.UserCenterActivity;
import elink.activity.UserLoginActivity;
import elink.entity.DeviceEntity;
import elink.model.DeviceModel.ItemInfo;

public class IntentHelper {


	public static void startDeviceActvity(Context context) {
		Intent intent = new Intent(context, DeviceActivity.class);
		//Intent intent = new Intent(context, DeviceActivityNew.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void startUserActvity(Activity context) {
		Intent intent = new Intent(context, UserLoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.startActivity(intent);
	}

	public static void startSetUserAuthActvity(Activity context, String type,
			String phoneNum) {
		Intent intent = new Intent(context, SetUserAuthActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("phone", phoneNum);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.startActivity(intent);
	}

	public static void startAddDeviceActivity(Context context) {
		Intent intent = new Intent(context, AddDeviceActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.startActivity(intent);

	}

	public static void startUcActvity(Activity deviceActivity) {
		Intent intent = new Intent(deviceActivity, UserCenterActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		deviceActivity.startActivity(intent);

	}


	public static void startSwichDetail(Context context, ItemInfo info) {

		Intent intent = new Intent(context, DetailSwitchActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(HkConst.EXTRA_D_ID, info.mDId);


		context.startActivity(intent);

	}

	public static void startShareDeviceActvity(Context context, String id) {
		Intent intent = new Intent(context, ShareDeviceActvity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(HkConst.EXTRA_D_ID, id);

		context.startActivity(intent);

	}

	public static void startTimerActvity(Context context, String id) {
		Intent intent = new Intent(context, SetTimerActvity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(HkConst.EXTRA_D_ID, id);
		context.startActivity(intent);

	}

	public static void startNewTimerActvity(Context context, String id,int outlet) {
		HLog.d("hzy startTimerActvity", "hzy context" + context + " id " + id);
		Intent intent = new Intent(context, SetNewTimerActvity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(HkConst.EXTRA_D_ID, id);
		intent.putExtra("extra_outlet", outlet);
		context.startActivity(intent);

	}
	public static void starSettingNameActvity(Context context, String id) {
		Intent intent = new Intent(context, SetInfoActvity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(HkConst.EXTRA_D_ID, id);

		context.startActivity(intent);

	}

	public static void startSetUserInfoActivity(Context context) {
		Intent intent = new Intent(context, SetUserInfoActvity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.startActivity(intent);

	}

	
	public static void startAbountkuzhaiActivity(Context context) {


	}

	public static void startDeviceManuFactorActivity(Context context,
			DeviceEntity info) {
		Intent intent = new Intent(context, DeviceManufactorActivity.class);

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(HkConst.EXTRA_DES, info.mDes);
		intent.putExtra(HkConst.EXTRA_MANUFACTOR, info.mManufact);
		intent.putExtra(HkConst.EXTRA_FW_VERSION, info.mFwVersion);
		intent.putExtra(HkConst.EXTRA_D_ID, info.mDeviceId);

		context.startActivity(intent);

	}
	public static void startDeviceVirtualManuFactorActivity(Context context
													 ) {



	}

	public static void startTraseFanActivity(Context deviceActivity,
			ItemInfo info) {


	}

	public static void startDialogActivity(Context context,int type) {
		Intent intent = new Intent(context, DialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(HkConst.EXTRA_DIALOG_TYPE, type);

		context.startActivity(intent);

	}


	public static void startQNTimerActvity(HkApplication context, String id, int tag, int outlet,int size) {
		HLog.d("hzy startTimerActvity", "hzy context" + context + " id " + id);
		Intent intent = new Intent(context, SetNewTimerActvity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(HkConst.EXTRA_D_ID, id);
		intent.putExtra("isdevlist", tag);
		intent.putExtra("extra_outlet", outlet);
		intent.putExtra("size", size);
		intent.putExtra("isdev", 1);
		context.startActivity(intent);
	}



	public static void startDeviceVirtualActvity(Context mContext) {

	}


	public static void startSetNewVirtualTimer(Context mContext, Class<SetNewVirtualTimerActvity> setNewVirtualTimerActvityClass) {
		Intent intent = new Intent(mContext,setNewVirtualTimerActvityClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	public static void startAddTimerActivity(Context mContext, String mDeviceId, String timerId, int mOutlet, int mSwitchSize,boolean isFirePlace) {
		Intent intent=new Intent(mContext,AddTimerActivity.class);
		intent.putExtra("Extra_device_id", mDeviceId);
		intent.putExtra("Extra_outlet", mOutlet);
		intent.putExtra("Extra_switch_size", mSwitchSize);
		if(null!=timerId){
			intent.putExtra("Extra_timer_id", timerId);

		}
		intent.putExtra("Extra_IS_FIRE_PLACE",isFirePlace);

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	public static void startAddVirtualTimerActivity(Context context){

	}


	public static void startAddVirtualTimerFromListActivity(Context context, String timerid) {



	}
	public static void startAddEspTouchActivity(Context context) {
//		Intent intent = new Intent(context, DeviceEspTouchActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//		context.startActivity(intent);

	}
}

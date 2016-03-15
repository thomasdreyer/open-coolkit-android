package elink.entity;

import elink.DeviceHelper;

public class DeviceEntity {

	public String mId;
	public String mType;
	public String mName;
	public String mExtra;
	public String mDeviceId;
	public String mApiKey;
	public String mOnLine;
	public String mCreateTime;
	public String mGroup;
	public String mShareUsers;
	public String mDes;
	public String mUser;
	public String mParams;
	public String mOwer;
	public Integer  mUi=DeviceHelper.UI_DEAULT;
	public String mModel;
	public String mFwVersion;
	public String mUpdateInfo;
	public String mV;
	public String mManufact;
	public String mDesUrl;
	public String mBrand;
	public String mProductModel;
	public  int devicetype;
	@Override
	public String toString() {
		return "name="+mName+"&group="+mGroup;
	}
	

}

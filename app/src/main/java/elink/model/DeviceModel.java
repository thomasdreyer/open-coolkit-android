package elink.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import android.text.TextUtils;

import demo.demo.R;
import com.coolkit.common.HLog;

import elink.HkConst;
import elink.activity.DeviceActivity;
import elink.controller.DeviceListFragmentController;
import elink.entity.DeviceEntity;

public class DeviceModel {
public static final String  TAG=DeviceActivity.TAG;
	public static class ItemInfo {
		public int icon;
		public String name;
		public String des;
		public String timer;
		public int ishastimer;

		// 0means title,1 means group, 2 menas device ,
		public String type;

		// group name
		public String groupId;
		// 0means group ， bigger than 0，means index of group;
		public String groupOrder = "1";
		public boolean isShow = false;
		public Integer deviceType;
		public String mId;
		/**
		 * 实体的id
		 */
		public String mDId;
		public boolean onLine;

		public int scollUiCount = 1;
		public boolean isScollShow = false;

		@Override
		public String toString() {
			return "name=" + name + "&Group=" + groupId+" mIsshow:"+isShow;
		}

	}

	private DeviceListFragmentController mControll;

	public DeviceModel(DeviceListFragmentController controller) {
		mControll = controller;

	}

	public List<ItemInfo> mUserDevice = new ArrayList<ItemInfo>();;
	private Comparator<ItemInfo> mCoparetor = new Comparator<ItemInfo>() {

		@Override
		public int compare(ItemInfo arg0, ItemInfo arg1) {
			if(TextUtils.isEmpty(arg0.groupId)&&!TextUtils.isEmpty(arg1.groupId)){
				return -1;
			}
			String sor0=arg0.type==HkConst.ITEM_TYPE_GRROUP?arg0.groupId:arg0.groupId+"_"+arg0.name;
			String sor1=arg1.type==HkConst.ITEM_TYPE_GRROUP?arg1.groupId:arg1.groupId+"_"+arg1.name;
			HLog.i(TAG, sor0+" compare to:"+sor1);
			return (sor0).compareTo(sor1);
		}
	};
	ResponseHandler<String> deviceHandler = new ResponseHandler<String>() {

		@Override
		public String handleResponse(HttpResponse arg0)
				throws ClientProtocolException, IOException {

			return null;
		}
	};

	public void queryUserDevice() {
		mUserDevice.clear();
		mUserDevice.addAll(getDeviceItem());

		HLog.i(TAG, "mUserDevice size is:" + mUserDevice.size());
		Collections.sort(mUserDevice, mCoparetor);
	};


	public List<ItemInfo> getDeviceItem() {
		
		List<ItemInfo> list = new ArrayList<ItemInfo>();
		
		

		
		
		List<DeviceEntity> device = mControll.basicHelper.app.mDbManager
				.getUserDevice("");
		if (null != device) {
			
			HLog.i(TAG, " device query no group device is:" + device.size());

			for (DeviceEntity deviceEntity : device) {

				ItemInfo item = new ItemInfo();
				item.name = deviceEntity.mName;
				// item.des="您可以进行模拟智能设备体验";
				item.icon = getDeviceStateIcon(deviceEntity);
				item.type = HkConst.ITEM_TYPE_DEVICE;
				item.mDId=deviceEntity.mDeviceId;

				item.groupId="";
				item.deviceType = deviceEntity.mUi;
				item.mId = deviceEntity.mId;
				item.onLine = "true".equals(deviceEntity.mOnLine);
				if(!TextUtils.isEmpty(deviceEntity.mUpdateInfo)&&TextUtils.isEmpty(deviceEntity.mOwer)){
					item.scollUiCount=2;
				}
				HLog.i(TAG, "device:" + item.toString());
				list.add(item);

			}
		}
		

		
		return list;

	}

	private int getDeviceStateIcon(DeviceEntity deviceEntity) {
		return R.drawable.switch_open;
	}



}

package elink.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.coolkit.common.HLog;

import elink.HkApplication;
import elink.entity.DeviceEntity;
import elink.entity.Timer;
import elink.utils.HomekitFormate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbManager {

	private static final String TAG = DbManager.class.getSimpleName();
	private SQLiteDatabase mDb;
	private static DbManager mInstance;
	Context mContext;
	HkApplication app;
	private Handler mHandler;
	Byte lock = (byte) 0;

	// DblockDBManagerThread mThread;

	private DbManager(Context context) {
		new DBManagerThread().start();
//		if (mHandler == null) {
//			synchronized (lock) {
//				if (mHandler == null) {
//
//					try {
//						lock.wait();
//					} catch (InterruptedException e) {
//						HLog.i(TAG, "mHandler created,continue");
//					}
//				}
//
//			}
//
//		}

		mContext = context;
		app = (HkApplication) mContext.getApplicationContext();
		mDb = new DbHelper(context).getWritableDatabase();
	}

	public static DbManager getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new DbManager(context);
		}
		return mInstance;
	}

	public void inSert(Object o) throws IllegalAccessException,
			IllegalArgumentException {
		ContentValues cv = new ContentValues();
		Class c = o.getClass();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			String key = field.getName();
			Class<?> type = field.getType();

			if (String.class.equals(type)) {
				cv.put(key, (String) field.get(o));
			} else if (Integer.class.equals(type)) {
				cv.put(key, (Integer) field.get(o));

			} else if (Boolean.class.equals(type)) {
				// cv.put(key, (Boolean) field.get(o).equals(true)?1:0);
			}
		}

		long row = mDb.insert(c.getSimpleName(), null, cv);
		HLog.i(TAG, "insert object:" + row);

	}

	public void syncInser(Object o) {
		if (null == mHandler) {

			Message msg = mHandler.obtainMessage(0);
			msg.obj = o;
			msg.sendToTarget();

		} else {
		}
	}



	private void setValue(Field field, Object c, ContentValues cv)
			throws IllegalAccessException, IllegalArgumentException {
		String key = field.getName();
		Class<?> type = field.getDeclaringClass();

		if (String.class.equals(type)) {
			cv.put(key, (String) field.get(c));
		} else if (Integer.class.equals(type)) {
			cv.put(key, (Integer) field.get(c));

		}

	}

	public void clearEnTiy(String clazz) {
		HLog.i(TAG, "clear device："+clazz);
		mDb.execSQL("delete  from " + clazz);

	}

	// Device
	static final String[] DEVICE_COLUMNS = new String[] { "mId,mType,mName,mExtra,mDeviceId,mApiKey,mOnLine,mCreateTime,mGroup,mShareUsers,mV,mParams,mUi,mOwer,mModel,mFwVersion,mUpdateInfo,mDes,mManufact,mDesUrl,mBrand,mProductModel" };

	public List<DeviceEntity> getUserDevice(String group) {
		HLog.i(TAG, "get user:" + app.mUser.userName + " device");
		List<DeviceEntity> list = new ArrayList<DeviceEntity>();
		String[] args = new String[] { group };
		String selection = " mGroup = ? ";
		Cursor cursor = null;
		if (TextUtils.isEmpty(group)) {
			cursor = mDb.query(DeviceEntity.class.getSimpleName(),
					DEVICE_COLUMNS, null, null, null, null, null);
		} else {
			cursor = mDb.query(DeviceEntity.class.getSimpleName(),
					DEVICE_COLUMNS, selection, args, null, null, null);
		}

		DeviceEntity dev = null;
		try {
			if (null != cursor) {
				boolean has = cursor.moveToFirst();
				while (has) {

					dev = getDeviceEntity(cursor);
					list.add(dev);
					has = cursor.moveToNext();
				}

			}
		}catch (Exception e){
			HLog.e(TAG,e);
		}finally {
			cursor.close();
		}

		return list;
	}

	public DeviceEntity queryDeviceyById(String devId) {
		HLog.i(TAG, "get user:" + app.mUser.userName + " device");

		String[] args = new String[] { devId };
		String selection = " mId = ? ";
		Cursor cursor = mDb.query(DeviceEntity.class.getSimpleName(),
				DEVICE_COLUMNS, selection, args, null, null, null);
		DeviceEntity dev = null;
		try{
			if (null != cursor) {
				boolean has = cursor.moveToFirst();
				if (has) {
					dev = getDeviceEntity(cursor);
				}

			}
		}catch (Exception e){
			HLog.e(TAG,e);
		}finally {
			cursor.close();
		}

		return dev;

	}

	public DeviceEntity queryDeviceyByDeviceId(String devId) {
		HLog.i(TAG, "get user:" + app.mUser.userName + " device");

		String[] args = new String[] { devId };
		String selection = " mDeviceId = ? ";
		Cursor cursor = mDb.query(DeviceEntity.class.getSimpleName(),
				DEVICE_COLUMNS, selection, args, null, null, null);
		DeviceEntity dev = null;
		try {
			if (null != cursor) {
				boolean has = cursor.moveToFirst();
				if (has) {
					dev = getDeviceEntity(cursor);
				}

			}
		}catch (Exception e){
			HLog.e(TAG,e);
		}finally {
			cursor.close();
		}

		return dev;

	}

	private DeviceEntity getDeviceEntity(Cursor cursor) {
		DeviceEntity dev;
		dev = new DeviceEntity();
		dev.mId = cursor.getString(0);

		dev.mType = cursor.getString(1);
		dev.mName = cursor.getString(2);
		dev.mExtra = cursor.getString(3);
		dev.mDeviceId = cursor.getString(4);
		dev.mApiKey = cursor.getString(5);
		dev.mOnLine = cursor.getString(6);
		dev.mCreateTime = cursor.getString(7);
		dev.mGroup = cursor.getString(8);
		dev.mShareUsers = cursor.getString(9);
		dev.mV = cursor.getString(10);
		dev.mParams = cursor.getString(11);
		dev.mUi = cursor.getInt(12);
		dev.mOwer = cursor.getString(13);
		dev.mModel = cursor.getString(14);
		dev.mFwVersion = cursor.getString(15);
		dev.mUpdateInfo = cursor.getString(16);
		dev.mDes = cursor.getString(17);
		dev.mManufact = cursor.getString(18);
		dev.mDesUrl=cursor.getString(19);
		dev.mBrand=cursor.getString(20);
		dev.mProductModel=cursor.getString(21);
		return dev;
	}



	public List<Timer> queryTimerByDeviceId(String devId) {

		List<Timer> list = new ArrayList<Timer>();

		String[] columns = new String[] { "at,typ,enable,doAction,deviceId,mId,startDo,endDo" };

		String[] args = new String[] { devId };
		String selection = " deviceId = ? ";
		Cursor cursor = mDb.query(Timer.class.getSimpleName(), columns,
				selection, args, null, null, null);
		Timer timer = null;
		try{
			if (null != cursor) {
				boolean has = cursor.moveToFirst();
				while (has) {

					timer = new Timer();
					timer.at = cursor.getString(0);
					timer.typ = cursor.getString(1);
					timer.enable = 1 == cursor.getInt(2);
					timer.doAction = cursor.getString(3);
					timer.deviceId = cursor.getString(4);
					timer.mId = cursor.getString(5);
					timer.startDo=cursor.getString(6);
					timer.endDo=cursor.getString(7);
					HLog.i(TAG,"query timer ,startDo :"+timer.startDo+"endDo:"+timer.endDo);

					boolean validate = ("once".equals(timer.typ) && (HomekitFormate
							.getLocal(timer.at).after(new Date())))
							|| !"once".equals(timer.typ);

					if (validate) {
						list.add(timer);
					}
					has = cursor.moveToNext();

				}

			}


		}catch (Exception e){
			HLog.e(TAG,e);
		}finally {
			cursor.close();
		}

		HLog.i(TAG, "queryTimerByDeviceId:" + devId + " size is:"
				+ ((null == list) ? "null " : list.size()));
		return list;

	}

	public Timer queryTimerById(String  id)  {




			List<Timer> list = new ArrayList<Timer>();

			String[] columns = new String[] { "at,typ,enable,doAction,deviceId,mId,startDo,endDo" };

			String[] args = new String[] { id };
			String selection = " mId = ? ";
			Cursor cursor = mDb.query(Timer.class.getSimpleName(), columns,
					selection, args, null, null, null);
			Timer timer = null;

		try {
			if (null != cursor) {
				boolean has = cursor.moveToFirst();
				while (has) {

					timer = new Timer();
					timer.at = cursor.getString(0);
					timer.typ = cursor.getString(1);
					timer.enable = 1 == cursor.getInt(2);
					timer.doAction = cursor.getString(3);
					timer.deviceId = cursor.getString(4);
					timer.mId = cursor.getString(5);
					timer.startDo=cursor.getString(6);
					timer.endDo=cursor.getString(7);
					boolean validate = ("once".equals(timer.typ) && (HomekitFormate
							.getLocal(timer.at).after(new Date())))
							|| !"once".equals(timer.typ);

					return timer;

				}

			}
		}catch (Exception e){
			HLog.e(TAG,e);
		}finally {
			cursor.close();
		}
		return null;
	};



	public void deleteObject(Object obj, String idColumn, String mId) {
		mDb.execSQL("delete  from " + obj.getClass().getSimpleName()
				+ " where " + idColumn + " = \"" + mId + "\"");
	}

	public long updateObject(Object o, String mId) throws Exception {
		ContentValues cv = new ContentValues();
		Class c = o.getClass();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			String key = field.getName();
			Class<?> type = field.getType();

			if (String.class.equals(type)) {
				cv.put(key, (String) field.get(o));
			} else if (Integer.class.equals(type)) {
				cv.put(key, (Integer) field.get(o));

			}
		}

		String[] args = new String[] { mId };
		String selection = " mId = ? ";

		long row = mDb
				.update(o.getClass().getSimpleName(), cv, selection, args);
		HLog.i(TAG, "update object class:" + o.getClass().getSimpleName()
				+ " rows:" + row);
		return row;
	};


	public long updateObject(Object o, String idColumn, String columnValue) throws Exception {
		ContentValues cv = new ContentValues();
		Class c = o.getClass();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			String key = field.getName();
			Class<?> type = field.getType();

			if (String.class.equals(type)) {
				cv.put(key, (String) field.get(o));
			} else if (Integer.class.equals(type)) {
				cv.put(key, (Integer) field.get(o));

			}
		}

		String[] args = new String[] { columnValue };
		String selection = " "+idColumn+" = ?";

		long row = mDb
				.update(o.getClass().getSimpleName(), cv, selection, args);
		HLog.i(TAG, "update object class:" + o.getClass().getSimpleName()
				+ " rows:" + row);
		return row;
	};



	public int updateObjectByUUID(Object o, String mUuid) throws Exception {
		ContentValues cv = new ContentValues();
		Class c = o.getClass();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			String key = field.getName();
			Class<?> type = field.getType();

			if (String.class.equals(type)) {
				cv.put(key, (String) field.get(o));
			} else if (Integer.class.equals(type)) {
				cv.put(key, (Integer) field.get(o));

			}
		}

		String[] args = new String[] { mUuid };
		String selection = " mUuid = ? ";

		int row = mDb.update(o.getClass().getSimpleName(), cv, selection, args);
		HLog.i(TAG, "update object class:" + o.getClass().getSimpleName()
				+ " rows:" + row);
		return row;

	};

	class DBManagerThread extends Thread {

		@Override
		public void run() {
			super.run();
			Looper.prepare();
			synchronized (lock) {
				mHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						try {
							switch (msg.what) {
							case 0:
								inSert(msg.obj);

								break;

							default:
								break;
							}
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				};
				lock.notify();
			}
			Looper.loop();

		}
	}
	// public void insertDefaultGroups(String user) {
	// HLog.i(TAG, "inser user:" + user + " groups");
	// DGroup home = new DGroup();
	// home.mIcon = R.drawable.mall;
	// home.mName = "客厅";
	// home.mUuid = UUID.randomUUID() + "";
	// home.mUser = user;
	// home.mIsShow = 0;
	//
	// DGroup rest = new DGroup();
	// rest.mIcon = R.drawable.restrant;
	// rest.mName = "餐厅";
	// rest.mUuid = UUID.randomUUID() + "";
	// rest.mUser = user;
	// rest.mIsShow = 0;
	//
	// // mDb
	//
	// try {
	// inSert(home);
	// inSert(rest);
	// } catch (IllegalAccessException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalArgumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

}

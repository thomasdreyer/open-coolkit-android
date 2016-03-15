package elink.model;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.coolkit.common.HLog;

import java.lang.reflect.Field;

import elink.entity.DeviceEntity;
import elink.entity.Timer;

public class DbHelper extends SQLiteOpenHelper {

	private static final String TAG = DbHelper.class.getSimpleName();

	/**
	 * version =14 ,add device entity field,brand and product model
	 */
	private static int DB_VERSION = 15;
	private static String DB_NAME = "homekit";

	private SQLiteDatabase mDb;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		HLog.i(TAG, "on create table");
		mDb = arg0;
		createTables();

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		mDb=arg0;
		createTables();
	}

	public void createTables() {
		createTable(DeviceEntity.class);
		createTable(Timer.class);

	};

	public void createTable(Class c) {
		mDb.execSQL("drop table if exists "+c.getSimpleName()+";");
		
		Field[] fields = c.getDeclaredFields();
		String sql = " create table " + c.getSimpleName()
				+ " ( _id integer primary key autoincrement ";
		for (Field field : fields) {
			sql += " , " + field.getName() + " " + getType(field.getType());

		}
		sql += " )";
		HLog.i(TAG, "exe sql:" + sql);
		mDb.execSQL(sql);
	}

	private String getType(Class type) {
		if (String.class.equals(type)) {
			return "text";
		} else if (Integer.class.equals(type)) {
			return "integer";

		}else if(Boolean.class.equals(type)){
			return "boolean";
		}
		return null;
	}

}

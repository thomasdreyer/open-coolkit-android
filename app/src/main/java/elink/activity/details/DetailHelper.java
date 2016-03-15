package elink.activity.details;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.coolkit.common.HLog;

import elink.common.ABind;
import elink.entity.DeviceEntity;

import java.lang.reflect.Field;

public class DetailHelper implements OnClickListener {
	String TAG=DetailHelper.class.getSimpleName();

	public String ui;
	public boolean isTimer=false;
	
	protected DeviceEntity mDeviceEntity;
	public DetailHelper() {
		super();
	}
	

	public void initView(ViewGroup viewParent) {
	
		
	
	}

	public void initData(DeviceEntity deviceEntity) {
		mDeviceEntity=deviceEntity;
		
	
	}

	public void setView() {
		
	}
	
	public String getTimerParams(){
		return "";
	}

	public void setState() {
		
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void initDeclaration() {
		Field[] fields = this.getClass().getDeclaredFields();
		if (null != fields) {
			for (Field field : fields) {

				ABind an = field.getAnnotation(ABind.class);
				if (null != an) {
					try {
						field.setAccessible(true);
						View view = findViewById(an.id());
						field.set(this, view);
						if (an.click()) {
							view.setOnClickListener(this);
						}

					} catch (Exception e) {
					
						HLog.e(TAG, e);
					}
				}
			}
		}

	}

	private View findViewById(int id) {
		return null;
	}

	@Override
	public void onClick(View v) {
		
	}

}
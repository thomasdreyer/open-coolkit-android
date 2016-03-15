package elink.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.coolkit.common.HLog;

public class CustomVierPager extends ViewPager {

	private boolean isCanScroll = false;

	public CustomVierPager(Context context) {
		super(context);
	}

	public CustomVierPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}



	public void setScanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}


	@Override
	public void scrollTo(int x, int y) {

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		HLog.i("CustomVierPager", "CustomVierPager on intercpet touch evnet:" + super.onInterceptTouchEvent(ev));
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}
}
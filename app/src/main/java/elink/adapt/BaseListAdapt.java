package elink.adapt;

import java.util.List;

import android.widget.BaseAdapter;

import com.coolkit.common.HLog;

public abstract class BaseListAdapt<T> extends BaseAdapter {

	public List<T> mData;

	public BaseListAdapt() {

	}

	public  void setData(List<T> data){
		mData=data;
	}

	public BaseListAdapt(List<T> data) {
		mData = data;
	}

	@Override
	public int getCount() {
		return null == mData ? 0 : mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null == mData ? null : mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void refresh() {
		HLog.i("BaseListAdapt", "notify data change:");
		this.notifyDataSetChanged();
	}

}

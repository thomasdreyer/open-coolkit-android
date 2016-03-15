package elink.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;

import elink.HkConst;
import elink.activity.DialogActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class UiHelper {

	private static final String TAG = UiHelper.class.getSimpleName();

	

	public static void resizePikcer(FrameLayout tp) {
		List<NumberPicker> npList = findNumberPicker(tp);
		for (NumberPicker np : npList) {
			resizeNumberPicker(np);
		}
	}
	
	
	public static void showLongToast(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_LONG)
		.show();
	}
	public static void showShortToast(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT)
		.show();
	}

	/*
	 * 调整numberpicker大小
	 */
	private static void resizeNumberPicker(NumberPicker np) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 250);
		params.setMargins(0, 0, 0, 0);
		np.setLayoutParams(params);
	}

	/**
	 * 得到viewGroup里面的numberpicker组件
	 * 
	 * @param viewGroup
	 * @return
	 */
	private static List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;
		if (null != viewGroup) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				child = viewGroup.getChildAt(i);
				if (child instanceof NumberPicker) {
					npList.add((NumberPicker) child);
				} else if (child instanceof LinearLayout) {
					List<NumberPicker> result = findNumberPicker((ViewGroup) child);
					if (result.size() > 0) {
						return result;
					}
				}
			}
		}
		return npList;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView,Activity context) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			// listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			// 计算子项View 的宽高
			try {
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			} catch (Exception e) {
				totalHeight += 60;
			}
			// 统计所有子项的总高度
			
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		
//		ViewGroup.LayoutParams paramsPare = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,params.height);
//		((View)listView.getParent()).setLayoutParams(paramsPare);
		listView.setLayoutParams(params);
	}



	public static Dialog showInstallDialog(String verSion,
			View.OnClickListener onClickListener, Context context) {
		Dialog dialog = new Dialog(context);
		// dialog.getWindow()
		// .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(demo.demo.R.layout.dialog_new_version);
		TextView tv = (TextView) dialog.findViewById(R.id.tv_tip);
		tv.setText(context.getString(R.string.new_home_kit, verSion));
		Button download = (Button) dialog.findViewById(R.id.btn_download);
		download.setText(context.getResources().getText(R.string.upgrade_immediately));
		download.setOnClickListener(onClickListener);
		return dialog;
	}

	public static int Compare(String old, String newVersion) {
		String[] v1Array = old.split("\\.");
		String[] v2Array = newVersion.split("\\.");
		if (v1Array != null) {
			if(v2Array!=null&&v1Array.length>v2Array.length){
				return  1;
			}
			for (int i = 0; i < v1Array.length; i++) {
//				HLog.i(TAG, " 1:" + v1Array[i] + " 2:" + v2Array[i]);
				if (v1Array[i].equals(v2Array[i])) {
					continue;
				}
				return v1Array[i].compareTo(v2Array[i]);

			}
		}

		return 0;
	};

	public static Dialog showConfireDialog(String title, String content,
			View.OnClickListener onOkayListener,
			View.OnClickListener onCancleListener, Context context) {
		Dialog dialog = new Dialog(context);
		// dialog.getWindow()
		// .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(demo.demo.R.layout.dialog_confire);
		TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
		TextView tvContent = (TextView) dialog.findViewById(R.id.tv_content);
		Button btnSure = (Button) dialog.findViewById(R.id.btn_sure);
		Button btnCancle = (Button) dialog.findViewById(R.id.btn_cancle);

		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}
		if (!TextUtils.isEmpty(content)) {
			tvContent.setText(content);

		}

		if (null != onOkayListener) {
			btnSure.setOnClickListener(onOkayListener);
		}

		if (null != onOkayListener) {
			btnCancle.setOnClickListener(onCancleListener);
		}
		HLog.i(TAG, "create confireDialog");

		return dialog;
	}
	// force to show overflow menu in actionbar
	public static void getOverflowMenu(Activity context) {
		try {
			ViewConfiguration config = ViewConfiguration.get(context);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	public static boolean isIteadDevice(String ssid) {
		return null!=ssid&&ssid.startsWith("ITEAD-")
				&& ssid.trim().length() == 16;
	}


	public static void showReLogintDialog(Context context) {

		Intent intent = new Intent(context, DialogActivity.class);
		intent.putExtra(HkConst.EXTRA_DIALOG_TYPE, DialogActivity.DIALOG_NEED_LOGIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}

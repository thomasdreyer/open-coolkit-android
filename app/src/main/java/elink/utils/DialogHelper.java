package elink.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import demo.demo.R;

public class DialogHelper {
	public static Dialog createProgressDialog(Context context, String text) {
		Dialog dialog = new Dialog(context, R.style.CustomDialog);
		dialog.setContentView(R.layout.dialog_con);
		TextView tv = (TextView)dialog.findViewById(R.id.tv_tip);
		tv.setText(text);
		return dialog;
	}


}

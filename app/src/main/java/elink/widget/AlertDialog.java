package elink.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import demo.demo.R;

public class AlertDialog extends Dialog implements View.OnClickListener {

	private String mTitle;
	private String confirmButtonText;
	private String cacelButtonText;
	private String mContent;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sure:
			onOkayClick();
			dismiss();
			break;
		case R.id.btn_cancle:
			onCancleClick();
			dismiss();
			break;
		default:
			break;
		}
	}

	public void onCancleClick() {
		// TODO Auto-generated method stub

	}

	public void onOkayClick() {
		// TODO Auto-generated method stub

	}

	public AlertDialog(Context context, String content) {
		this(context, context.getString(R.string.tips), content);

	}

	public AlertDialog(Context context, String title, String content) {
		this(context, title, content,  context.getString(R.string.ok),  context.getString(R.string.cancel));

	}

	public AlertDialog(Context context, String title, String content,
					   String confirmButtonText, String cacelButtonText) {
		super(context);
		this.mContent = content;
		this.mTitle = title;
		this.confirmButtonText = confirmButtonText;
		this.cacelButtonText = cacelButtonText;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_confire);
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		TextView tvContent = (TextView) findViewById(R.id.tv_content);
		Button btnSure = (Button) findViewById(R.id.btn_sure);
		Button btnCancle = (Button) findViewById(R.id.btn_cancle);

		if (!TextUtils.isEmpty(mTitle)) {
			tvTitle.setText(mTitle);
		}
		if (!TextUtils.isEmpty(mContent)) {
			tvContent.setText(mContent);
		}
		if (TextUtils.isEmpty(confirmButtonText)) {
			btnSure.setText(confirmButtonText);
		}

		if (TextUtils.isEmpty(cacelButtonText)) {
			btnCancle.setText(cacelButtonText);
		}

		btnSure.setOnClickListener(this);
		btnCancle.setOnClickListener(this);

//		Window dialogWindow = getWindow();
//		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//		DisplayMetrics d = getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
//		lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
//		dialogWindow.setAttributes(lp);
	}

	
	
}
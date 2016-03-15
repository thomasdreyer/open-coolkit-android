package elink.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import demo.demo.R;

public class EditDialog extends Dialog implements View.OnClickListener {

	private String mTitle;
	private String confirmButtonText;
	private String cacelButtonText;
	private String mContent;
	protected TextView mTvDash;
	public EditText mEt;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sure:
			if(!onOkayClick()){
				dismiss();
			};
			
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

	public boolean onOkayClick() {
		return false;

	}

	public EditDialog(Context context, String content) {
		this(context,  context.getString(R.string.tips), content);

	}

	public EditDialog(Context context, String title, String content) {
		this(context, title, content,  context.getString(R.string.ok),  context.getString(R.string.cancel));

	}

	public EditDialog(Context context, String title, String content,
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
		setContentView(demo.demo.R.layout.dialog_set_name);
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		mEt = (EditText) findViewById(R.id.et_content);
		Button btnSure = (Button) findViewById(R.id.btn_sure);
		Button btnCancle = (Button) findViewById(R.id.btn_cancle);
		mTvDash = (TextView) findViewById(R.id.tv_dash);

		if (!TextUtils.isEmpty(mTitle)) {
			tvTitle.setText(mTitle);
		}
		if (!TextUtils.isEmpty(mContent)) {
			mEt.setHint(mContent);
		}
		if (TextUtils.isEmpty(confirmButtonText)) {
			btnSure.setText(confirmButtonText);
		}

		if (TextUtils.isEmpty(cacelButtonText)) {
			btnCancle.setText(cacelButtonText);
		}

		btnSure.setOnClickListener(this);
		btnCancle.setOnClickListener(this);

		mEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			mTvDash.setText("");
				
			}
		});
	}

	
	
}
package elink.activity;

import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import demo.demo.R;
import com.coolkit.common.HLog;
import com.coolkit.protocol.request.ProtocolHandler;
import com.coolkit.protocol.request.Result;
import com.coolkit.protocol.request.UserProtocol;

import elink.common.Helper;
import elink.utils.DialogHelper;
import elink.utils.NetHelper;
import elink.widget.ConfireEditDialog;
import elink.widget.EditDialog;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class SetUserInfoActvity extends BasicActivity implements
		OnClickListener {

	private static final String TAG = SetUserInfoActvity.class.getSimpleName();
	private TextView mTvSetName;
	private Dialog mSetNickDialog;
	private Dialog mLoding;
	private String nickName;
	private Dialog mSetPwdDialog;
	private View mTvSetPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HLog.i("11", "SetInfoActvity oncreate");
		setContentView(R.layout.activity_set_nick);
		mBaseHandler.sendEmptyMessage(0);

	}

	@Override
	public void setViews() {
		if (null != this.getActionBar()) {
			getActionBar().setLogo(new BitmapDrawable());
			this.getActionBar().setDisplayHomeAsUpEnabled(true);
			setTitle(getString(R.string.change_user_info));
		}
	}

	@Override
	public void initDatas() {
		super.initDatas();

	}

	@Override
	public void initViews() {
		super.initViews();
		mTvSetName = (TextView) findViewById(R.id.tv_setname);
		mTvSetName.setOnClickListener(this);
		
		mTvSetPwd = (TextView) findViewById(R.id.tv_set_pass);
		mTvSetPwd.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tv_setname:
			HLog.i(TAG, "on clikc tv set name");
			showSetName();
			//
			// if (TextUtils.isEmpty(mDeviceEntity.mOwer)) {
			// showSetView();
			//
			// } else {
			// Toast.makeText(this, "该设备不属于您，不能修改设备名称", Toast.LENGTH_SHORT)
			// .show();
			// }
			break;

		case R.id.tv_set_pass: {
			showSetView();
		}
			break;

		default:
			break;
		}

	}

	private void showSetName() {

			mSetNickDialog = new EditDialog(this, getString(R.string.enter_name)){
				@Override
				public boolean onOkayClick() {
					if (TextUtils.isEmpty(this.mEt.getEditableText()
							.toString())) {
						this.mTvDash.setText(getString(R.string.name_not));
						return true;
					}else{
						setName(this.mEt.getEditableText().toString());
						return false;
					}
				}
			};
			


		mSetNickDialog.show();

	}



	public void call(Result result) {
		mLoding.dismiss();
		mSetNickDialog.dismiss();

		if (HttpStatus.SC_OK == result.mCode) {
			Toast.makeText(this,getString(R.string.modify_name_success) , Toast.LENGTH_SHORT).show();
			try {
				app.mUser.nickName = nickName;
				app.mSp.saveNickName(app.mUser.userName, nickName);
				Helper.broadcastEditNickName(this);
				// UiHelper.broadcastEditName(this, mDeviceEntity.mName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;

		}
		Toast.makeText(this, getString(R.string.modify_name_failed) , Toast.LENGTH_SHORT).show();
	}

	private void setName(final String name) {
		if(NetHelper.isConnnected(this)){
			nickName = name;
			mLoding = DialogHelper.createProgressDialog(SetUserInfoActvity.this,
					getString(R.string.waiting) );
			mLoding.show();

			postRequest(new Runnable() {

				@Override
				public void run() {
					new UserProtocol(SetUserInfoActvity.this.app.mAppHelper).doSetNickName(app.mUser.at,
							new ProtocolHandler(SetUserInfoActvity.this,0, new ProtocolHandler.CallBack() {

								@Override
								public void callBack(Result result) {
									call(result);

								}
							}), name);

				}
			});
		}else {
			showNoNet();
		}


	}

	private void showSetView() {

			mSetPwdDialog = new ConfireEditDialog(this,getString(R.string.change_password) ) {
				@Override
				public boolean onOkayClick() {

					if (TextUtils
							.isEmpty(this.mEt.getEditableText().toString())) {
						this.mTvDash.setText(getString(R.string.old_paswd_not));

						return true;
					}
					if (TextUtils.isEmpty(this.mEtNew.getEditableText()
							.toString())) {
						this.mTvDash.setText(getString(R.string.new_paswd_not));
						return true;
					}

					if (TextUtils.isEmpty(this.mEtNew2.getEditableText()
							.toString())) {
						this.mTvDash.setText(getString(R.string.secondary_not));
						return true;
					}
					if (!this.mEtNew2.getEditableText().toString()
							.equals(this.mEtNew.getEditableText().toString())) {
						this.mTvDash.setText(getString(R.string.two_passwords_notsam));
						return true;

					}

					String old = this.mEt.getEditableText().toString();

					boolean match = (old.length() >= 8)
							&& Pattern.compile("[0-9]+?").matcher(old).find()
							&& Pattern.compile("[a-z]+?").matcher(old).find()
							&& Pattern.compile("[A-Z]+?").matcher(old).find()&&old.matches("^[A-Za-z0-9]+$");
					if (!match) {
						this.mTvDash.setText(getString(R.string.info_old_paswd));
						return true;
					}

					String newS = this.mEtNew2.getEditableText().toString();
					boolean match2 = (newS.length() >= 8)
							&& Pattern.compile("[0-9]+?").matcher(newS).find()
							&& Pattern.compile("[a-z]+?").matcher(newS).find()
							&& Pattern.compile("[A-Z]+?").matcher(newS).find()&& newS.matches("^[A-Za-z0-9]+$");
					if (!match2) {
						this.mTvDash.setText(getString(R.string.info_fomat_paswd));
						return true;
					}
					if(!NetHelper.isConnnected(getApplicationContext())){
						this.mTvDash.setText(getString(R.string.info_check_internet));
						return true;

					}

					SetUserInfoActvity.this.postRequest(new Runnable() {
						
						@Override
						public void run() {
							setPwd(mEt.getEditableText().toString(), mEtNew
									.getEditableText().toString());
							
						}
					});
					return true;

				}

				protected void setPwd(String oldPassword, String newPassword) {
					new UserProtocol(SetUserInfoActvity.this.app.mAppHelper).changePwd(oldPassword, newPassword,
							new ProtocolHandler(SetUserInfoActvity.this,0, new ProtocolHandler.CallBack() {

								@Override
								public void callBack(Result result) {
									if (HttpStatus.SC_OK == result.mCode) {
										try {
											JSONObject json = new JSONObject(
													result.mMsg);
											if (json.has("error")) {
												mTvDash.setText(getString(R.string.info_again_enter));
											} else {
												Toast.makeText(SetUserInfoActvity.this,getString(R.string.change_susess), Toast.LENGTH_SHORT).show();
												mSetPwdDialog.dismiss();
											}
										} catch (JSONException e) {
											HLog.e(TAG, e);
										}

									}else{
										mTvDash.setText(getString(R.string.change_password_failed)+result.mCode);

									}

								}
							}),app.mUser.at);

				}
			};

		mSetPwdDialog.show();
	}

}

package elink.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import demo.demo.R;
import com.coolkit.common.HLog;

import elink.controller.UserCenterController;
import elink.utils.IntentHelper;

import java.io.File;

public class UserCenterActivity extends BasicActivity<UserCenterController>
		implements OnClickListener {
	private static final String TAG = UserCenterActivity.class.getSimpleName();
	private ImageView mIvHead;
	private BroadcastReceiver mReciever;
	private View mRlUserInfo;
	private TextView mTvNick;

	@Override
	public void initViews() {
		super.initViews();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_user_center);
		initView();
		initData();
		setView();
		doRegister();

	}

	private void doRegister() {
		mReciever = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				if ("com.homekit.action.EDIT_NICK_NAME"
						.equals(arg1.getAction())) {
					HLog.i(TAG, "on recieve nicked edit");
					setView();
				} else {
					byte[] b = arg1.getByteArrayExtra("bitmap");
					Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0,
							b.length);
					if (bitmap != null) {
						mIvHead.setImageBitmap(bitmap);
					}
				}

			}
		};
		this.registerReceiver(mReciever, new IntentFilter(
				"com.homekit.EDIT_IMG"));
		this.registerReceiver(mReciever, new IntentFilter(
				"com.homekit.action.EDIT_NICK_NAME"));

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (null != mReciever) {
			this.unregisterReceiver(mReciever);
		}
	}

	protected void setView() {
		super.setView();
		mTvNick.setText(app.mUser.nickName);

	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void initView() {
		mIvHead = (ImageView) this.findViewById(R.id.iv_head);
		mTvNick = (TextView) findViewById(R.id.tv_nick);
		mIvHead.setOnClickListener(this);
		mRlUserInfo = findViewById(R.id.rl_user_info);
		mRlUserInfo.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_head:
//			showImageRepo();
			break;
		case R.id.rl_user_info:
			IntentHelper.startSetUserInfoActivity(this);
			break;

		default:
			break;
		}

	}

	private void sendLogs() {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/homekit.txt")), "text/plain");
		startActivity(intent);
	}

	private void showImageRepo() {
		Intent intent = new Intent();
		/* 开启Pictures画面Type设定为image */
		intent.setType("text/plain");
		/* 使用Intent.ACTION_GET_CONTENT这个Action */
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 1);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		HLog.i(TAG, "on activity result");


		// if (resultCode == RESULT_OK) {
		// Uri uri = data.getData();
		// Log.e("uri", uri.toString());
		// ContentResolver cr = this.getContentResolver();
		// try {
		// Bitmap bitmap = BitmapFactory.decodeStream(cr
		// .openInputStream(uri));
		// ImageView imageView = (ImageView) findViewById(R.id.iv01);
		// /* 将Bitmap设定到ImageView */
		// imageView.setImageBitmap(bitmap);
		// } catch (FileNotFoundException e) {
		// Log.e("Exception", e.getMessage(), e);
		// }
		// }
		// super.onActivityResult(requestCode, resultCode, data);
	}
}

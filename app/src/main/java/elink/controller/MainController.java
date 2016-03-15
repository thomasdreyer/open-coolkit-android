package elink.controller;

import android.util.Log;

import com.coolkit.common.HLog;

import elink.activity.BasicActivity;
import elink.utils.Debugg;
import elink.utils.IntentHelper;

public class MainController extends BasiController {

	public MainController(BasicActivity context) {
		super(context);
		HLog.d("", "hzy MainController context" + context + app);

	}

	public void doJudgeLogin() {
		if (app.mUser.isLogin || Debugg.isLogin) {
			Log.i("", "doJudgeLogin is login:" + app.mUser.isLogin
					+ " debugLogin:" + Debugg.isLogin);
			IntentHelper.startDeviceActvity(mContext);

		} else {
			IntentHelper.startUserActvity(mContext);

			// mController.doRegister();
			// ntentHelper.st
			// final String pwd = "123456Abc";
			// final String account = "+8613714758450";
			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// new UserProtocol().doLogin(MainActivity.this, mHandler,
			// pwd, "", account);
			//
			// }
			// }).start();
		}
	}

}

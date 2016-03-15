package elink;

 public interface HkConst {

	/**
	 * Intent Cont start,should start with INTENT
	 */
	   String INTENT_USER_DEVICES = "com.coolkit.homekit.USER_DEVICES";
	   String INTENT_GO_TEST_PAGE = "com.coolkit.GO_TEST_PAGE";
	   String INTENT_WS_CALL_BACK = "com.coolkit.homekit.WS_CALL_BACK";
	   String INTENT_WS_OPNE = "com.coolkit.homekit.WS_OPEND";
	   String INTENT_SYNC_LOCAL = "com.coolkit.homekit.SYNC_LOCAL_DEVICES";

	//  String USER_DEVICES = "com.coolkit.homekit.user_device";
	//  String USER_DEVICES = "com.coolkit.homekit.user_device";

	/**
	 * networks
	 */

	//    String INTENT_USER_DEVICES =
	// "com.coolkit.homekit.USER_DEVICES";
	//
	// $scope.networks = [
	// 'ITEAD-xxxx111',
	// 'ITEAD-xxxx1xx',
	// 'ITx111',
	// 'ITEAD-aabb111'
	// ];

	   String SETTING_WIFI_PSW = "12345678";

	   String ITEM_TYPE_NORMARL = "normal";
	   String ITEM_TYPE_DEVICE = "device";

	   String ITEM_TYPE_GRROUP = "group";
	   String ITEM_TYPE_DIVER = "diver";

	   String EXTRA_WS_KEY = "extra_ws_key";
	   String EXTRA_D_ID = "extra_d_id";
	   String EXTRA_WS_MSG = "etra_ws_msg";
	   String EXTRA_FW_VERSION = "etra_ui_version";
	   String EXTRA_DES = "etra_des";
	   String EXTRA_MANUFACTOR = "etra_manufactor";
	   String  EXTRA_DIALOG_TYPE = "etra_dialog_type";
	 String  VIRTUALTIMER = "virtual_timer";

	
	
	/**
	 * 0.9.0，切换到aws服务器，协议2.0，此版本在信息显示界面显示错误0.8.7
	 */
	
	/**
	 * 0.9.1 修改登录注册的界面适配
	 */
	   String CONFIG_APK_VERSION = "1.0.9";


	   int SERVER_VERSION =2;
}

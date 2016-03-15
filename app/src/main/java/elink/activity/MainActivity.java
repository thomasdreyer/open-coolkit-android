package elink.activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import elink.controller.MainController;

public class MainActivity extends BasicActivity<MainController> {
    String TAG = "MainActivity";
    private LinearLayout layoutGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mController = new MainController(this);
        mController.doJudgeLogin();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

package elink.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import demo.demo.R;
import com.coolkit.common.HLog;

import elink.HkConst;
import elink.common.Helper;

import elink.widget.ConfirmDialog;

/**
 * Created by app on 15/9/1.
 */
public class DialogActivity extends BasicActivity implements DialogInterface.OnDismissListener {
    private static final String TAG = DialogActivity.class.getSimpleName();


    public static final int DIALOG_BEING_SHARED = 1;
    public static final int DIALOG_NOTIFY = DIALOG_BEING_SHARED + 1;
    public static final int DIALOG_AT_OVER_TIME = DIALOG_NOTIFY + 1;
    public static final int DIALOG_NEED_LOGIN = DIALOG_AT_OVER_TIME + 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        int type = -1;
        if (null != intent) {

            type = intent.getIntExtra(HkConst.EXTRA_DIALOG_TYPE, -1);


        }

        HLog.i(TAG, "create dialog type:" + type);
        if (type == -1) {
            finish();

        } else {
            showCustomDialog(type);
        }
    }


    private void showCustomDialog(int type) {
        switch (type) {
            case 0:
                showUpdateDialog();
                break;
            case DIALOG_BEING_SHARED:
                HLog.i(TAG, "show being shared");
                showBeingSharedDialog();
                break;
            case DIALOG_NOTIFY:
                HLog.i(TAG, "show ws notify");
                showNotify();

                break;
            case DIALOG_AT_OVER_TIME:
                showAtOverTimer();
                break;
            case DIALOG_NEED_LOGIN:
                showNeedLogin();
                break;


        }
    }


    private void showNeedLogin() {

        Dialog dialog = new ConfirmDialog(this,getString(R.string.tips),getString(R.string.info_relogin),getString(R.string.ok),getString(R.string.cancel)){
            @Override
            public void onOkayClick() {
                super.onOkayClick();
                Helper.broadcastAtOverTime(getApplicationContext());

                finish();
            }

            @Override
            public void onCancleClick() {
                super.onCancleClick();

                Helper.broadcastAtOverTime(getApplicationContext());
                finish();
            }
        };
        dialog.setCancelable(false);
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    private void showAtOverTimer() {
        showNeedLogin();
    }

    private void showNotify() {
        String json = this.getIntent().getStringExtra("json");


        Dialog dialog = new ConfirmDialog(this,getString(R.string.tips),json,getString(R.string.ok),getString(R.string.cancel));

        dialog.setOnDismissListener(this);

        dialog.show();
    }

    private void showBeingSharedDialog() {
        String json = this.getIntent().getStringExtra("json");
        Dialog dialog = new ShareHelper(this).doRecieverShare(json);

        dialog.setOnDismissListener(this);
        dialog.show();
    }

    public void showUpdateDialog() {



    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }


}

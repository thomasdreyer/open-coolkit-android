package elink.activity;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import demo.demo.R;
import com.coolkit.common.HLog;

import elink.utils.IntentHelper;

import java.io.File;

/**
 * Created by app on 15/9/2.
 */
public class UsercenterFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = UsercenterFragment.class.getSimpleName();
    private ImageView mIvHead;
    private BroadcastReceiver mReciever;
    private View mRlUserInfo;
    private TextView mTvNick;
    private View mView;
    BaseHelper basicHelper;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView=inflater.inflate(R.layout.activity_user_center,null,false);
        initData();
        initView();
        return mView;
    }


    protected void setView() {
HLog.i(TAG, "set nick name " + basicHelper.app.mUser.nickName);
        mTvNick.setText(basicHelper.app.mUser.nickName);

    }

    private void initData() {
        // TODO Auto-generated method stub

    }

    private void initView() {
        mIvHead = (ImageView)mView.findViewById(R.id.iv_head);
        mTvNick = (TextView) mView.findViewById(R.id.tv_nick);
        mIvHead.setOnClickListener(this);
        mRlUserInfo = mView.findViewById(R.id.rl_user_info);
        mRlUserInfo.setOnClickListener(this);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        basicHelper=new BaseHelper(this.getActivity());
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        this.getActivity().getActionBar().hide();
        setView();
    }



    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.iv_head:
//			showImageRepo();
                break;
            case R.id.rl_user_info:
                IntentHelper.startSetUserInfoActivity(this.getActivity());
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



}

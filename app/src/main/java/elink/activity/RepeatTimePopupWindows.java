package elink.activity;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import demo.demo.R;
import com.coolkit.common.HLog;

import java.util.Date;

/**
 * Created by hezhiyong on 9/18/15.
 */
public class RepeatTimePopupWindows extends PopupWindow {

    private AddTimerActivity mAddTimerHelper;
    View mReSetDetailView;
    private static final String TAG = RepeatTimePopupWindows.class.getSimpleName();
    TimePicker time_picker;
    Button btn_cancel, btn_ok;
    SetNewVirtualTimerActvity mContext;
    private Date mDate;
    private String[] mdates;

    public RepeatTimePopupWindows(AddTimerActivity helper, View.OnClickListener itemsOnClick) {
        super(helper);
        mAddTimerHelper = helper;
        LayoutInflater inflater = mAddTimerHelper.getLayoutInflater();
        mReSetDetailView = inflater.inflate(R.layout.timer_popwindows_retime, null);
        initViewdetail();
        setview();
    }

    private void setview() {
      //  UiHelper.resizePikcer(mMitutePicker);
        time_picker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        time_picker.setIs24HourView(true);
    }

    private void initViewdetail() {
        time_picker = (TimePicker) mReSetDetailView.findViewById(R.id.time_picker);
        time_picker.setIs24HourView(true);//TimePicker设置24小时制,不然下午时间会变成以0开头
        btn_cancel = (Button) mReSetDetailView.findViewById(R.id.btn_cancel);
        btn_ok = (Button) mReSetDetailView.findViewById(R.id.btn_ok);

        if (mAddTimerHelper.mRepeatePicker == null) {
            //添加代码
            time_picker.setCurrentHour(mAddTimerHelper.hour);
            time_picker.setCurrentMinute(mAddTimerHelper.minus);
            HLog.i(TAG, "repeatpicker is null" + "hour:" + mAddTimerHelper.hour + "minus:" + mAddTimerHelper.minus);
        } else {
            time_picker.setCurrentHour(mAddTimerHelper.mRepeatePicker.getCurrentHour());
            time_picker.setCurrentMinute(mAddTimerHelper.mRepeatePicker.getCurrentMinute());
            HLog.i(TAG,"Hours:"+mAddTimerHelper.mRepeatePicker.getCurrentHour()+"Minute:"+mAddTimerHelper.mRepeatePicker.getCurrentMinute());
        }


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();

            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mAddTimerHelper.mRepeatePicker = time_picker;
                mAddTimerHelper.type = 2;
                mAddTimerHelper.hour = time_picker.getCurrentHour();
                mAddTimerHelper.minus = time_picker.getCurrentMinute();
                mAddTimerHelper.mTimer.typ="repeat";

                mAddTimerHelper.showTVDate();


                dismiss();

            }
        });
        this.setContentView(mReSetDetailView);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimationSetTimer);  //AnimationFade   PopupAnimation
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
    }

}
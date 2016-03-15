package elink.activity;

import android.app.AlarmManager;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import demo.demo.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yaoyi on 11/25/15.
 */
public class KeepTimeSetPopupWindow extends PopupWindow{
    private static final String TAG = KeepTimeSetPopupWindow.class.getSimpleName();
    private View mSetQuickView;
    TextView detail;
    DatePicker date_picker;
    TimePicker time_picker;
    Button btn_cancel, btn_ok;
    AddTimerActivity mAddTimerHelper;
    private Calendar c;
    private NumberPicker mDayPicker;
    private NumberPicker mHourPicker;
    private NumberPicker mMitutePicker;
    private TextView swp_detailview;
    private TextView timerlater;
    private TextView content;

    public KeepTimeSetPopupWindow(AddTimerActivity helper, View.OnClickListener itemsOnClick) {
        super(helper);
        mAddTimerHelper = helper;
        LayoutInflater inflater = mAddTimerHelper.getLayoutInflater();
        mSetQuickView = inflater.inflate(R.layout.timer_popwindows_quick, null);
        initViewQuick();
        setview();
    }

    private void initViewQuick() {
        c = Calendar.getInstance();
        this.setContentView(mSetQuickView);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimationSetTimer);  //AnimationFade   PopupAnimation
        ColorDrawable dw = new ColorDrawable(/*0x00000000*/0xb0000000);
        this.setBackgroundDrawable(dw);

        mDayPicker = (NumberPicker)mSetQuickView.findViewById(R.id.day_picker);
        mHourPicker = (NumberPicker)mSetQuickView.findViewById(R.id.hour_picker);
        mMitutePicker = (NumberPicker)mSetQuickView.findViewById(R.id.minute_picker);
        detail = (TextView) mSetQuickView.findViewById(R.id.detail);
        detail.setVisibility(View.GONE);
        swp_detailview = (TextView) mSetQuickView.findViewById(R.id.swpdetialview);
        swp_detailview.setText(mAddTimerHelper.getString(R.string.keeptime));
        btn_cancel = (Button) mSetQuickView.findViewById(R.id.btn_cancel);
        btn_ok = (Button) mSetQuickView.findViewById(R.id.btn_ok);
        timerlater = (TextView)mSetQuickView.findViewById(R.id.tv_timelater);
        timerlater.setText("");
        content = (TextView)mSetQuickView.findViewById(R.id.tv_content);
        content.setText("");
    }

    private void setview() {
        mDayPicker.setMaxValue(366);
        mDayPicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        mHourPicker.setMaxValue(23);
        mHourPicker.setMinValue(0);
        mHourPicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        mMitutePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        mMitutePicker.setMaxValue(59);
        mMitutePicker.setMinValue(0);


        if (mAddTimerHelper.mQuikTimerPicker == null) {
            mMitutePicker.setValue(30);
        } else {
            mMitutePicker.setValue(mAddTimerHelper.mQuikTimerPicker.getValue());
        }

        if (mAddTimerHelper.mDayPicker == null) {
            mDayPicker.setValue(0);
        } else {
            mDayPicker.setValue(mAddTimerHelper.mDayPicker.getValue());
        }

        if (mAddTimerHelper.mHourPicker == null) {
            mHourPicker.setValue(0);
        } else {
            mHourPicker.setValue(mAddTimerHelper.mHourPicker.getValue());
        }


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();

            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mAddTimerHelper.mDayPicker=mDayPicker;
                mAddTimerHelper.mHourPicker=mHourPicker;
                mAddTimerHelper.mQuikTimerPicker = mMitutePicker;
                mAddTimerHelper.type = 3;
                mAddTimerHelper.mTimer.typ="duration";

                mAddTimerHelper.mKeepDay = mDayPicker.getValue();
                mAddTimerHelper.mKeepHour = mHourPicker.getValue();
                mAddTimerHelper.mKeepMinutes = mMitutePicker.getValue();



                Calendar cal = Calendar.getInstance();
                mAddTimerHelper.mKeepTimeDate = new Date(cal.getTime().getTime() + mDayPicker.getValue()* AlarmManager.INTERVAL_DAY+mHourPicker.getValue()*AlarmManager.INTERVAL_HOUR+ mMitutePicker.getValue() * AlarmManager.INTERVAL_FIFTEEN_MINUTES/15);
                mAddTimerHelper.showTVDate();
                mMitutePicker.setValue(30);
                mDayPicker.setValue(0);
                mHourPicker.setValue(0);
                dismiss();
            }
        });

    }
}

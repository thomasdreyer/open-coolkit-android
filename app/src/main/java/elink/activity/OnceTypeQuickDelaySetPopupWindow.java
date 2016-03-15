package elink.activity;

import android.app.AlarmManager;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import demo.demo.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hezhiyong on 9/17/15.
 */
public class OnceTypeQuickDelaySetPopupWindow extends PopupWindow {

    private static final String TAG = OnceTypeQuickDelaySetPopupWindow.class.getSimpleName();
    private View mSetQuickView;
    NumberPicker mMitutePicker;
    Button btn_cancel, btn_ok;
    private AddTimerActivity mAddTimerHelper;
    private TextView detail;
    private NumberPicker mDayPicker;
    private NumberPicker mHourPicker;
    private Calendar c;

    public OnceTypeQuickDelaySetPopupWindow(AddTimerActivity addTimerActivity, View.OnClickListener itemsOnClick) {
        super(addTimerActivity);
        mAddTimerHelper = addTimerActivity;
        LayoutInflater inflater = mAddTimerHelper.getLayoutInflater();
        mSetQuickView = inflater.inflate(R.layout.timer_popwindows_quick, null);
        initViewQuick();
        setView();

    }

    private void setView() {
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
                mAddTimerHelper.type = 1;
                mAddTimerHelper.mTimer.typ="once";
                Calendar cal = Calendar.getInstance();
                mAddTimerHelper.mDate = new Date(cal.getTime().getTime() + mDayPicker.getValue()* AlarmManager.INTERVAL_DAY+mHourPicker.getValue()*AlarmManager.INTERVAL_HOUR+ mMitutePicker.getValue() * AlarmManager.INTERVAL_FIFTEEN_MINUTES/15);
                mAddTimerHelper.showTVDate();
                mMitutePicker.setValue(30);
                mDayPicker.setValue(0);
                mHourPicker.setValue(0);
                dismiss();
            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
                mAddTimerHelper.type = AddTimerActivity.TYPE_SET_DETAIL;
                mAddTimerHelper.setTimerpop();
            }
        });
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
        btn_cancel = (Button) mSetQuickView.findViewById(R.id.btn_cancel);
        btn_ok = (Button) mSetQuickView.findViewById(R.id.btn_ok);
    }


}
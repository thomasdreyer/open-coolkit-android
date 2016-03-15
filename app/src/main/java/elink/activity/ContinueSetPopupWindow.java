package elink.activity;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import demo.demo.R;
import com.coolkit.common.HLog;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by yaoyi on 11/25/15.
 */
public class ContinueSetPopupWindow extends PopupWindow {
    private static final String TAG = ContinueSetPopupWindow.class.getSimpleName();
    private View mSetDetailView;
    TextView swp_quickview;
    TextView detail;
    DatePicker date_picker;
    TimePicker time_picker;
    Button btn_cancel, btn_ok;
    AddTimerActivity mAddTimerHelper;

    public ContinueSetPopupWindow(AddTimerActivity helper, View.OnClickListener itemsOnClick) {
        super(helper);
        mAddTimerHelper = helper;
        LayoutInflater inflater = mAddTimerHelper.getLayoutInflater();
        mSetDetailView = inflater.inflate(R.layout.timer_popwindows_detail, null);
        initViewdetail();
        setview();
    }

    private void setview() {
        if(date_picker!=null){
            HLog.i(TAG, "ViewGroup date_picker ");
            //((ViewGroup) ((ViewGroup) date_picker.getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.GONE);
            String lan = Locale.getDefault().getLanguage();
            if(lan.equals("zh")){
                ((ViewGroup) ((ViewGroup) date_picker.getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.GONE);
            }else{
                ( ((ViewGroup) ((ViewGroup) date_picker.getChildAt(0)).getChildAt(0)).getChildAt(2)).setVisibility(View.GONE);
            }

        }

        // UiHelper.resizePikcer(date_picker);
        // UiHelper.resizePikcer(mMitutePicker);
        time_picker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        date_picker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
//        mMitutePicker.setIs24HourView(true);
    }


    private void initViewdetail() {
//        date_picker = (DatePicker) mSetDetailView.findViewById(R.id.date_picker);
//        mMitutePicker = (TimePicker) mSetDetailView.findViewById(R.id.mMitutePicker);
        time_picker = findTimePicker((ViewGroup) mSetDetailView);
        time_picker.setIs24HourView(true);
        date_picker = findDatePicker((ViewGroup) mSetDetailView);
        swp_quickview = (TextView) mSetDetailView.findViewById(R.id.swp_quickview);
        swp_quickview.setVisibility(View.GONE);
        detail = (TextView) mSetDetailView.findViewById(R.id.detail);
        detail.setText(mAddTimerHelper.getString(R.string.begintime));
        btn_cancel = (Button) mSetDetailView.findViewById(R.id.btn_cancel);
        btn_ok = (Button) mSetDetailView.findViewById(R.id.btn_ok);

        Calendar cal = Calendar.getInstance();

        //用户编辑后的值,即用户点击进设置时间后的值
        if (null != mAddTimerHelper.mDetailDatePick) {
            date_picker.init(mAddTimerHelper.mDetailDatePick.getYear(), mAddTimerHelper.mDetailDatePick.getMonth(),
                    mAddTimerHelper.mDetailDatePick.getDayOfMonth(), null);
            time_picker.setCurrentHour(mAddTimerHelper.mDetailTimerPick.getCurrentHour());
            time_picker.setCurrentMinute(mAddTimerHelper.mDetailTimerPick.getCurrentMinute());

        } else {
            //用历史数据来初始化详细设置界面
            HLog.i(TAG,"mAddTimerHelper.mDate："+mAddTimerHelper.mContinueData);
            cal.setTime(mAddTimerHelper.mContinueData);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            date_picker.init(year, month, day, null);
            // date_picker.init(0, month, day, null);
            time_picker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            time_picker.setCurrentMinute(cal.get(Calendar.MINUTE));
            HLog.i(TAG,"year month day:"+year+ month+ day);
        }


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mAddTimerHelper.mDetailDatePick = date_picker;
                mAddTimerHelper.mDetailTimerPick = time_picker;
                mAddTimerHelper.type = 3;

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR,date_picker.getYear());
                cal.set(Calendar.MONTH,  date_picker.getMonth());
                cal.set(Calendar.DAY_OF_MONTH, date_picker.getDayOfMonth());
                cal.set(Calendar.HOUR_OF_DAY, time_picker.getCurrentHour());
                cal.set(Calendar.MINUTE, time_picker.getCurrentMinute());
                mAddTimerHelper.mTimer.typ="duration";
                mAddTimerHelper.mContinueData = cal.getTime();
                mAddTimerHelper.showTVDate();

                dismiss();
            }
        });


        this.setContentView(mSetDetailView);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);

        this.setAnimationStyle(R.style.AnimationSetTimer);  //AnimationFade   PopupAnimation
        ColorDrawable dw = new ColorDrawable(0xb0000000);//0xb0000000
        this.setBackgroundDrawable(dw);
    }

    private DatePicker findDatePicker(ViewGroup group) {
        if (group != null) {
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                View child = group.getChildAt(i);
                if (child instanceof DatePicker) {
                    return (DatePicker) child;
                } else if (child instanceof ViewGroup) {
                    DatePicker result = findDatePicker((ViewGroup) child);
                    if (result != null)
                        return result;
                }
            }
        }
        return null;
    }

    private TimePicker findTimePicker(ViewGroup group) {
        if (group != null) {
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                View child = group.getChildAt(i);
                if (child instanceof TimePicker) {
                    return (TimePicker) child;
                } else if (child instanceof ViewGroup) {
                    TimePicker result = findTimePicker((ViewGroup) child);
                    if (result != null)
                        return result;
                }
            }
        }
        return null;
    }
}

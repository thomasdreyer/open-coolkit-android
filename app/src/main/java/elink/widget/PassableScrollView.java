package elink.widget;
import android.content.Context;  
import android.util.AttributeSet;  
import android.view.MotionEvent;  
import android.widget.ScrollView;  
  
public class PassableScrollView extends ScrollView  
{  
      
    public PassableScrollView(Context context)  
    {  
        super(context);  
  
    }  
  
    public PassableScrollView(Context context, AttributeSet attrs)  
    {  
        super(context, attrs);  
          
    }  
      
    public PassableScrollView(Context context, AttributeSet attrs, int defStyle)  
    {  
        super(context, attrs, defStyle);  
    }  
      
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent event)   //这个方法如果返回 true 的话 两个手指移动，启动一个按下的手指的移动不能被传播出去。  
    {  
        super.onInterceptTouchEvent(event);  
        return false;  
    }  
      
    @Override  
    public boolean onTouchEvent(MotionEvent event)     
    {  
        super.onTouchEvent(event);  
        return false;       
    }  
          
}  
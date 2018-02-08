package demo.sunrise.com.floatwindowdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by sunrise on 2/6/18.
 */

public class FloatWindowSmallView extends LinearLayout {

    public static int sViewWidth;

    public static int sViewHeight;

    public static int sStatusBarHeight;

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mParams;

    private float xInScreen;

    private float yInScreen;

    private float xDownInScreen;

    private float yDownInScreen;

    private float xInView;

    private float yInView;

    public FloatWindowSmallView(Context context) {
        super(context);

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small,this);
        View view = findViewById(R.id.small_window_layout);
        sViewWidth = view.getLayoutParams().width;
        sViewHeight = view.getLayoutParams().height;
        TextView percentView = findViewById(R.id.percent);
        percentView.setText(MyWindowManager.getUsedPercentValue(getContext()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch ( event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                updateViewPostion();
                break;
            case MotionEvent.ACTION_UP:
                if (((xDownInScreen >= (xInScreen-2)) && ((xInScreen+2) >= xDownInScreen))
                    && ((yDownInScreen >= (yInScreen-2)) && ((yInScreen+2) >= yDownInScreen))) {
                    openBigWindow();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void openBigWindow() {
        MyWindowManager.createBigWindow(getContext());
        MyWindowManager.removeSmallWidow(getContext());
    }

    public void setmParams(WindowManager.LayoutParams params){
        mParams = params;
    }

    private void updateViewPostion() {
        mParams.x = (int)(xInScreen - xInView);
        mParams.y = (int)(yInScreen - yInView);
        mWindowManager.updateViewLayout(this,mParams);
    }

    private int getStatusBarHeight() {
        if (sStatusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                sStatusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sStatusBarHeight;
    }
}

package demo.sunrise.com.floatwindowdemo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.text.Layout;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by sunrise on 2/6/18.
 */

public class MyWindowManager {

    private static FloatWindowSmallView sSmallWindow;

    private static FloatWindowBigView sBigWindow;

    private static WindowManager.LayoutParams sSmallWindowParams;

    private static WindowManager.LayoutParams sBigWindowParams;

    private static WindowManager sWindowManager;

    private static ActivityManager sActivityManager;

    public  static void createSmallWindow(Context context){
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        if(sSmallWindow == null){
            sSmallWindow = new FloatWindowSmallView(context);
            if (sSmallWindowParams == null){
                sSmallWindowParams = new WindowManager.LayoutParams();
                sSmallWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                sSmallWindowParams.format = PixelFormat.RGB_888;
                sSmallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                sSmallWindowParams.gravity = Gravity.START | Gravity.TOP;
                sSmallWindowParams.width = FloatWindowSmallView.sViewWidth;
                sSmallWindowParams.height = FloatWindowSmallView.sViewHeight;
                sSmallWindowParams.x = screenWidth;
                sSmallWindowParams.y = screenHeight/2;
            }
            sSmallWindow.setmParams(sSmallWindowParams);
            windowManager.addView(sSmallWindow,sSmallWindowParams);
        }
    }

    public static void removeSmallWidow(Context context){
        if(sSmallWindow != null){
            getWindowManager(context).removeView(sSmallWindow);
            sSmallWindow = null;
        }
    }

    public static void createBigWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (sBigWindow == null) {
            sBigWindow = new FloatWindowBigView(context);
            if (sBigWindowParams == null) {
                sBigWindowParams = new WindowManager.LayoutParams();
                sBigWindowParams.x = screenWidth / 2 - FloatWindowBigView.sViewWidth / 2;
                sBigWindowParams.y = screenHeight / 2 - FloatWindowBigView.sViewHeight / 2;
                sBigWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                sBigWindowParams.format = PixelFormat.RGBA_8888;
                sBigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                sBigWindowParams.width = FloatWindowBigView.sViewWidth;
                sBigWindowParams.height = FloatWindowBigView.sViewHeight;
            }
            windowManager.addView(sBigWindow, sBigWindowParams);
        }
    }

    public static void removeBigWidow(Context context){
        if(sBigWindow != null){
            getWindowManager(context).removeView(sBigWindow);
            sBigWindow = null;
        }
    }

    public static void updateUsedPercent(Context context){
        if(sSmallWindow != null){
            ((TextView)(sSmallWindow.findViewById(R.id.percent)))
                    .setText(getUsedPercentValue(context));
        }
    }

    public static boolean isWindowShowing(){
        return sSmallWindow != null || sBigWindow != null;
    }


    private static WindowManager getWindowManager(Context context) {
        if(sWindowManager == null){
            sWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return sWindowManager;
    }

    private static ActivityManager getsActivityManager(Context context){
        if (sActivityManager == null){
            sActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return sActivityManager;
    }

    public static String getUsedPercentValue(Context context) {
        String dir = "/proc/meminfo";

        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr,2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+",""));
            long availableSize = getAvailableMemory(context)/1024;
            int percent = (int) ((totalMemorySize - availableSize)/(float)totalMemorySize * 100);
            return percent+"%";
        }catch (IOException e){
            e.printStackTrace();
        }

        return "FloatWindow";
    }

    private static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getsActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }
}

package demo.sunrise.com.floatwindowdemo;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class FloatWindowService extends Service {

    private static final String TAG = "FloatWindowService";

    private Handler handler = new Handler();

    private Timer timer;

    public FloatWindowService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (timer == null){
            timer = new Timer();
            timer.schedule(new RefreshTask(),0,500);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
    }

    class RefreshTask extends TimerTask {

        @Override
        public void run() {
            // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗
            if (isHome() && !MyWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.createSmallWindow(getApplicationContext());
                    }
                });
            }
            // 当前界面不是桌面，且有悬浮窗显示，则移除悬浮窗
            else if (!isHome() && MyWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.removeSmallWidow(getApplicationContext());
                        MyWindowManager.removeBigWidow(getApplicationContext());
                    }
                });
            }
            // 当前界面是桌面，且有悬浮窗显示，则更新内存数据
            else if (isHome() && MyWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.updateUsedPercent(getApplicationContext());
                    }
                });
            }
        }
    }

    /**
     * @return whether it is lanucher.
     */
    private boolean isHome() {

        // getRunningTasks在Android5.0后已被隐藏。
        /*ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        Log.d(TAG, "rti: "+rti);
        if (rti.isEmpty()){
            return true;
        }
        Log.d(TAG, "topactivity: "+rti.get(0).topActivity.getPackageName());
        return getHomes().contains(rti.get(0).topActivity.getPackageName());*/

        return getHomes().contains(getTopPackage());
    }

    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent
                ,PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo ri:resolveInfos){
            names.add(ri.activityInfo.processName);
        }
        Log.d(TAG, "getHomes: names:"+names.toString());
        return names;
    }


    static class RecentUseComparator implements Comparator<UsageStats> {

        @Override

        public int compare(UsageStats lhs,UsageStats rhs) {

            return (lhs.getLastTimeUsed()> rhs.getLastTimeUsed()) ? -1 : (lhs

                    .getLastTimeUsed()== rhs.getLastTimeUsed()) ? 0 : 1;

        }
    }

    /**
     * 使用UsageStatsManager
     * @return
     */
    private String getTopPackage() {

        Log.d(TAG,"===getTopPackage=");

        long ts = System.currentTimeMillis();

        RecentUseComparator mRecentComp = new RecentUseComparator();

        UsageStatsManager mUsageStatsManager =(UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        List<UsageStats> usageStats =mUsageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,ts - 10000, ts);  //查询ts-10000 到ts这段时间内的UsageStats，由于要设定时间限制，所以有可能获取不到

        if (usageStats == null)
            return "";
        if (usageStats.size() == 0)
            return "";

        Collections.sort(usageStats,mRecentComp);

        Log.d(TAG,"====usageStats.get(0).getPackageName()"+ usageStats.get(0).getPackageName()+" size:"+usageStats.size());

        return usageStats.get(0).getPackageName();

    }
}

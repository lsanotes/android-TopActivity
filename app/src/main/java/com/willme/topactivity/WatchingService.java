package com.willme.topactivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WatchingService extends Service {

    private final int NOTIF_ID = 1;

    private Handler mHandler = new Handler();

    private ActivityManager mActivityManager;

    private String text = null;

    private Timer timer;

    private String TAG = WatchingService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new RefreshTask(), 0, 500);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e(TAG, ServiceInfo.FLAG_STOP_WITH_TASK + "");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmService != null) {
            alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 500, restartServicePendingIntent);
        }
        super.onTaskRemoved(rootIntent);
    }

    class RefreshTask extends TimerTask {

        @Override
        public void run() {
            List<RunningTaskInfo> rtis = mActivityManager.getRunningTasks(1);
            String act = null;
            if (rtis.get(0).topActivity != null) {
                act = rtis.get(0).topActivity.getPackageName() + "\n" + rtis.get(0).topActivity.getClassName();
            }

            if (act != null && !act.equals(text)) {
                text = act;
                if (SPHelper.isShowWindow(WatchingService.this)) {
                    mHandler.post(() -> TasksWindow.show(WatchingService.this, text));
                }
            }
        }
    }
}

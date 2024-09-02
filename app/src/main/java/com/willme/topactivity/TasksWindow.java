package com.willme.topactivity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class TasksWindow {

    private static final String TAG = TasksWindow.class.getSimpleName();

    private static WindowManager.LayoutParams sWindowParams;

    private static WindowManager sWindowManager;

    private static View sView;

    public static void init(final Context context) {
        sWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
            WindowManager.LayoutParams.TYPE_TOAST;
        sWindowParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, type, 0x18,
            PixelFormat.TRANSLUCENT);
        sWindowParams.gravity = Gravity.START + Gravity.TOP;
        sView = LayoutInflater.from(context).inflate(R.layout.window_tasks, null);
    }

    public static void show(Context context, final String text) {
        if (sWindowManager == null) {
            init(context);
        }
        TextView textView = sView.findViewById(R.id.text);
        textView.setText(text);
        try {
            sWindowManager.addView(sView, sWindowParams);
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        QuickSettingTileService.updateTile(context);
    }

    public static void dismiss(Context context) {
        try {
            sWindowManager.removeView(sView);
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        QuickSettingTileService.updateTile(context);
    }
}

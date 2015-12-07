package pro.fren.adb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pro.fren.adb.utils.ADB;
import pro.fren.adb.utils.ScreenLock;

public class ADBReceiver extends BroadcastReceiver {
    public ADBReceiver() {
    }

    public static final String ACTION_CANCEL = "action_cancel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(ACTION_CANCEL)) {
            if (ADB.isEnabled(context))
                ADB.stop(context);
            stop(context);
        } else {
            if (ADB.isEnabled(context))
                start(context);
            else
                stop(context);
        }
    }

    public static void start(Context context) {
        ScreenLock.start(context);
    }

    public static void stop(Context context) {
        ScreenLock.stop(context);
    }
}
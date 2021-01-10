package com.android.cellbroadcastreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.cellbroadcastreceiver.module.R;

public class CellBroadcastAlertReminder extends Service {
    public static final String ACTION_PLAY_ALERT_REMINDER = "ACTION_PLAY_ALERT_REMINDER";
    public static final String ALERT_REMINDER_VIBRATE_EXTRA = "alert_reminder_vibrate_extra";
    private static PendingIntent sPlayReminderIntent;
    private static Ringtone sPlayReminderRingtone;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null || !ACTION_PLAY_ALERT_REMINDER.equals(intent.getAction())) {
            stopSelf();
            return 2;
        }
        log("playing alert reminder");
        playAlertReminderSound(intent.getBooleanExtra(ALERT_REMINDER_VIBRATE_EXTRA, true));
        if (queueAlertReminder(this, intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", Integer.MAX_VALUE), false)) {
            return 1;
        }
        log("no reminders queued");
        stopSelf();
        return 2;
    }

    private void playAlertReminderSound(boolean z) {
        Uri defaultUri = RingtoneManager.getDefaultUri(2);
        if (defaultUri == null) {
            loge("Can't get URI for alert reminder sound");
            return;
        }
        Ringtone ringtone = RingtoneManager.getRingtone(this, defaultUri);
        if (ringtone != null) {
            ringtone.setStreamType(5);
            log("playing alert reminder sound");
            ringtone.play();
        } else {
            loge("can't get Ringtone for alert reminder sound");
        }
        if (z) {
            ((Vibrator) getSystemService("vibrator")).vibrate(VibrationEffect.createOneShot(500, -1));
        }
    }

    public static boolean queueAlertReminder(Context context, int i, boolean z) {
        cancelAlertReminder();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String string = defaultSharedPreferences.getString("alert_reminder_interval", (String) null);
        if (string == null) {
            log("no preference value for alert reminder");
            return false;
        }
        try {
            int intValue = Integer.valueOf(string).intValue();
            if (intValue == 0) {
                log("Reminder is turned off.");
                return false;
            } else if (intValue != 1 || z) {
                if (z) {
                    int integer = CellBroadcastSettings.getResources(context, i).getInteger(R.integer.first_reminder_interval_in_min);
                    if (integer != 0) {
                        intValue = integer;
                    } else if (intValue == 1) {
                        intValue = 2;
                    }
                }
                log("queueAlertReminder() in " + intValue + " minutes");
                Intent intent = new Intent(context, CellBroadcastAlertReminder.class);
                intent.setAction(ACTION_PLAY_ALERT_REMINDER);
                intent.putExtra(ALERT_REMINDER_VIBRATE_EXTRA, defaultSharedPreferences.getBoolean("enable_alert_vibrate", true));
                intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", i);
                sPlayReminderIntent = PendingIntent.getService(context, 0, intent, 134217728);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
                if (alarmManager == null) {
                    loge("can't get Alarm Service");
                    return false;
                }
                alarmManager.setExact(2, SystemClock.elapsedRealtime() + ((long) (60000 * intValue)), sPlayReminderIntent);
                log("Set reminder in " + intValue + " minutes");
                return true;
            } else {
                log("Not scheduling reminder. Done for now.");
                return false;
            }
        } catch (NumberFormatException unused) {
            loge("invalid alert reminder interval preference: " + string);
            return false;
        }
    }

    static void cancelAlertReminder() {
        log("cancelAlertReminder()");
        if (sPlayReminderRingtone != null) {
            log("stopping play reminder ringtone");
            sPlayReminderRingtone.stop();
            sPlayReminderRingtone = null;
        }
        if (sPlayReminderIntent != null) {
            log("canceling pending play reminder intent");
            sPlayReminderIntent.cancel();
            sPlayReminderIntent = null;
        }
    }

    private static void log(String str) {
        Log.d("CellBroadcastAlertReminder", str);
    }

    private static void loge(String str) {
        Log.e("CellBroadcastAlertReminder", str);
    }
}

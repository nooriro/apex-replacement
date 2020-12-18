package com.android.cellbroadcastreceiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.SystemProperties;
import android.os.UserManager;
import android.preference.PreferenceManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaSmsCbProgramData;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.android.cellbroadcastreceiver.CellBroadcastContentProvider;
import com.android.cellbroadcastreceiver.module.R;
import com.android.cellbroadcastservice.CellBroadcastStatsLog;
import java.util.ArrayList;
import java.util.Iterator;

public class CellBroadcastReceiver extends BroadcastReceiver {
    public static final String CURRENT_INTERVAL_DEFAULT = "current_interval_default";
    public static final String TESTING_MODE = "testing_mode";
    private Context mContext;

    public void getCellBroadcastTask(final long j) {
        new CellBroadcastContentProvider.AsyncCellBroadcastTask(this.mContext.getContentResolver()).execute(new CellBroadcastContentProvider.CellBroadcastOperation[]{new CellBroadcastContentProvider.CellBroadcastOperation(this) {
            public boolean execute(CellBroadcastContentProvider cellBroadcastContentProvider) {
                return cellBroadcastContentProvider.markBroadcastRead("date", j);
            }
        }});
    }

    public Resources getResourcesMethod() {
        return CellBroadcastSettings.getResourcesForDefaultSubId(this.mContext);
    }

    public void onReceive(Context context, Intent intent) {
        log("onReceive " + intent);
        this.mContext = context.getApplicationContext();
        String action = intent.getAction();
        Resources resourcesMethod = getResourcesMethod();
        if ("com.android.cellbroadcastreceiver.intent.action.MARK_AS_READ".equals(action)) {
            getCellBroadcastTask(intent.getLongExtra("com.android.cellbroadcastreceiver.intent.extra.ID", -1));
        } else if ("android.telephony.action.CARRIER_CONFIG_CHANGED".equals(action)) {
            if (!intent.getBooleanExtra("android.telephony.extra.REBROADCAST_ON_UNLOCK", false)) {
                initializeSharedPreference(context, intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1));
                enableLauncher();
                startConfigServiceToEnableChannels();
                boolean z = PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("legacy_data_migration", false);
                if (resourcesMethod.getBoolean(R.bool.retry_message_history_data_migration) && !z) {
                    new CellBroadcastContentProvider.AsyncCellBroadcastTask(this.mContext.getContentResolver()).execute(new CellBroadcastContentProvider.CellBroadcastOperation[]{$$Lambda$CellBroadcastReceiver$nFIGQiGCpLEVTH4UCaDVjnMrmk4.INSTANCE});
                }
            }
        } else if ("android.intent.action.SERVICE_STATE".equals(action)) {
            int intExtra = intent.getIntExtra("voiceRegState", 0);
            if (intExtra != 3 && getServiceState(context) == 3) {
                startConfigServiceToEnableChannels();
            }
            setServiceState(intExtra);
        } else if ("com.android.cellbroadcastreceiver.intent.START_CONFIG".equals(action) || "android.telephony.action.DEFAULT_SMS_SUBSCRIPTION_CHANGED".equals(action)) {
            startConfigServiceToEnableChannels();
        } else if ("android.provider.action.SMS_EMERGENCY_CB_RECEIVED".equals(action) || "android.provider.Telephony.SMS_CB_RECEIVED".equals(action)) {
            intent.setClass(this.mContext, CellBroadcastAlertService.class);
            this.mContext.startService(intent);
        } else if ("android.provider.Telephony.SMS_SERVICE_CATEGORY_PROGRAM_DATA_RECEIVED".equals(action)) {
            ArrayList parcelableArrayListExtra = intent.getParcelableArrayListExtra("program_data");
            CellBroadcastStatsLog.write(249, 3, 3);
            if (parcelableArrayListExtra != null) {
                handleCdmaSmsCbProgramData(parcelableArrayListExtra);
            } else {
                loge("SCPD intent received with no program_data");
            }
        } else if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
            CellBroadcastAlertService.createNotificationChannels(this.mContext);
        } else if (!"android.telephony.action.SECRET_CODE".equals(action)) {
            Log.w("CellBroadcastReceiver", "onReceive() unexpected action " + action);
        } else if (SystemProperties.getInt("ro.debuggable", 0) == 1 || resourcesMethod.getBoolean(R.bool.allow_testing_mode_on_user_build)) {
            setTestingMode(!isTestingMode(this.mContext));
            String string = resourcesMethod.getString(isTestingMode(this.mContext) ? R.string.testing_mode_enabled : R.string.testing_mode_disabled);
            Toast.makeText(this.mContext, string, 0).show();
            LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(new Intent("com.android.cellbroadcastreceiver.intent.ACTION_TESTING_MODE_CHANGED"));
            log(string);
        }
    }

    private void resetSettingsIfCarrierChanged(Context context, int i) {
        if (i == -1) {
            if (getPreviousCarrierIdForDefaultSub() == -2) {
                saveCarrierIdForDefaultSub(-1);
            }
            Log.d("CellBroadcastReceiver", "ignoring carrier config broadcast because subId=-1");
            return;
        }
        int defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
        Log.d("CellBroadcastReceiver", "subId=" + i + " defaultSubId=" + defaultSubscriptionId);
        if (defaultSubscriptionId == -1) {
            Log.d("CellBroadcastReceiver", "ignoring carrier config broadcast because defaultSubId=-1");
        } else if (i != defaultSubscriptionId) {
            Log.d("CellBroadcastReceiver", "ignoring carrier config broadcast for subId=" + i + " because it does not match defaultSubId=" + defaultSubscriptionId);
        } else {
            int simCarrierId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).getSimCarrierId();
            if (simCarrierId == -1) {
                Log.e("CellBroadcastReceiver", "ignoring unknown carrier ID");
                return;
            }
            int previousCarrierIdForDefaultSub = getPreviousCarrierIdForDefaultSub();
            if (previousCarrierIdForDefaultSub == -2) {
                Log.d("CellBroadcastReceiver", "ignoring carrier config broadcast for subId=" + i + " for first boot");
                saveCarrierIdForDefaultSub(simCarrierId);
            } else if (simCarrierId != previousCarrierIdForDefaultSub) {
                saveCarrierIdForDefaultSub(simCarrierId);
                startConfigService(context, "UPDATE_SETTINGS_FOR_CARRIER");
            } else {
                Log.d("CellBroadcastReceiver", "ignoring carrier config broadcast for subId=" + i + " because carrier has not changed. carrierId=" + simCarrierId);
            }
        }
    }

    private int getPreviousCarrierIdForDefaultSub() {
        return getDefaultSharedPreferences().getInt("carrier_id_for_default_sub", -2);
    }

    private void saveCarrierIdForDefaultSub(int i) {
        getDefaultSharedPreferences().edit().putInt("carrier_id_for_default_sub", i).apply();
    }

    public void setTestingMode(boolean z) {
        PreferenceManager.getDefaultSharedPreferences(this.mContext).edit().putBoolean(TESTING_MODE, z).commit();
    }

    public static boolean isTestingMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TESTING_MODE, false);
    }

    private void setServiceState(int i) {
        PreferenceManager.getDefaultSharedPreferences(this.mContext).edit().putInt("service_state", i).commit();
    }

    private static int getServiceState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("service_state", 0);
    }

    public void adjustReminderInterval() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        String string = defaultSharedPreferences.getString(CURRENT_INTERVAL_DEFAULT, "0");
        String string2 = CellBroadcastSettings.getResourcesForDefaultSubId(this.mContext).getString(R.string.alert_reminder_interval_in_min_default);
        if (!string2.equals(string)) {
            Log.d("CellBroadcastReceiver", "Default interval changed from " + string + " to " + string2);
            SharedPreferences.Editor edit = defaultSharedPreferences.edit();
            edit.putString("alert_reminder_interval", string2);
            edit.putString(CURRENT_INTERVAL_DEFAULT, string2);
            edit.commit();
            return;
        }
        Log.d("CellBroadcastReceiver", "Default interval " + string + " did not change.");
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext);
    }

    public Boolean sharedPrefsHaveDefaultValues() {
        return Boolean.valueOf(this.mContext.getSharedPreferences("_has_set_default_values", 0).getBoolean("_has_set_default_values", false));
    }

    public void initializeSharedPreference(Context context, int i) {
        if (isSystemUser()) {
            Log.d("CellBroadcastReceiver", "initializeSharedPreference");
            resetSettingsIfCarrierChanged(context, i);
            SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences();
            if (!sharedPrefsHaveDefaultValues().booleanValue()) {
                PreferenceManager.setDefaultValues(this.mContext, R.xml.preferences, false);
                defaultSharedPreferences.edit().putBoolean("override_dnd_settings_changed", false).apply();
                migrateSharedPreferenceFromLegacy();
                if (ActivityManager.isRunningInUserTestHarness()) {
                    Log.d("CellBroadcastReceiver", "In test harness mode. Turn off emergency alert by default.");
                    defaultSharedPreferences.edit().putBoolean("enable_alerts_master_toggle", false).apply();
                }
            } else {
                Log.d("CellBroadcastReceiver", "Skip setting default values of shared preference.");
            }
            adjustReminderInterval();
            return;
        }
        Log.e("CellBroadcastReceiver", "initializeSharedPreference: Not system user.");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0094, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        android.util.Log.e("CellBroadcastReceiver", "fails to get shared preference " + r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00b5, code lost:
        r13 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00b6, code lost:
        if (r2 != null) goto L_0x00b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00c0, code lost:
        throw r13;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:6:0x002e, B:17:0x004b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void migrateSharedPreferenceFromLegacy() {
        /*
            r13 = this;
            java.lang.String r0 = "cellbroadcast-legacy"
            java.lang.String r1 = "enable_cmas_amber_alerts"
            java.lang.String r2 = "enable_area_update_info_alerts"
            java.lang.String r3 = "enable_test_alerts"
            java.lang.String r4 = "enable_state_local_test_alerts"
            java.lang.String r5 = "enable_public_safety_messages"
            java.lang.String r6 = "enable_cmas_severe_threat_alerts"
            java.lang.String r7 = "enable_cmas_extreme_threat_alerts"
            java.lang.String r8 = "enable_cmas_presidential_alerts"
            java.lang.String r9 = "enable_emergency_alerts"
            java.lang.String r10 = "enable_alert_vibrate"
            java.lang.String r11 = "receive_cmas_in_second_language"
            java.lang.String r12 = "enable_alerts_master_toggle"
            java.lang.String[] r1 = new java.lang.String[]{r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12}
            android.content.Context r2 = r13.mContext     // Catch:{ Exception -> 0x00c1 }
            android.content.ContentResolver r2 = r2.getContentResolver()     // Catch:{ Exception -> 0x00c1 }
            android.content.ContentProviderClient r2 = r2.acquireContentProviderClient(r0)     // Catch:{ Exception -> 0x00c1 }
            java.lang.String r3 = "CellBroadcastReceiver"
            if (r2 != 0) goto L_0x0037
            java.lang.String r13 = "No legacy provider available for sharedpreference migration"
            android.util.Log.d(r3, r13)     // Catch:{ all -> 0x00b5 }
            if (r2 == 0) goto L_0x0036
            r2.close()     // Catch:{ Exception -> 0x00c1 }
        L_0x0036:
            return
        L_0x0037:
            android.content.Context r13 = r13.mContext     // Catch:{ all -> 0x00b5 }
            android.content.SharedPreferences r13 = android.preference.PreferenceManager.getDefaultSharedPreferences(r13)     // Catch:{ all -> 0x00b5 }
            android.content.SharedPreferences$Editor r13 = r13.edit()     // Catch:{ all -> 0x00b5 }
            r4 = 0
        L_0x0042:
            r5 = 12
            if (r4 >= r5) goto L_0x00ac
            r5 = r1[r4]     // Catch:{ all -> 0x00b5 }
            java.lang.String r6 = "get_preference"
            r7 = 0
            android.os.Bundle r6 = r2.call(r0, r6, r5, r7)     // Catch:{ RemoteException -> 0x0094 }
            if (r6 == 0) goto L_0x007f
            boolean r7 = r6.containsKey(r5)     // Catch:{ RemoteException -> 0x0094 }
            if (r7 == 0) goto L_0x007f
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0094 }
            r7.<init>()     // Catch:{ RemoteException -> 0x0094 }
            java.lang.String r8 = "migrateSharedPreferenceFromLegacy: "
            r7.append(r8)     // Catch:{ RemoteException -> 0x0094 }
            r7.append(r5)     // Catch:{ RemoteException -> 0x0094 }
            java.lang.String r8 = "val: "
            r7.append(r8)     // Catch:{ RemoteException -> 0x0094 }
            boolean r8 = r6.getBoolean(r5)     // Catch:{ RemoteException -> 0x0094 }
            r7.append(r8)     // Catch:{ RemoteException -> 0x0094 }
            java.lang.String r7 = r7.toString()     // Catch:{ RemoteException -> 0x0094 }
            android.util.Log.d(r3, r7)     // Catch:{ RemoteException -> 0x0094 }
            boolean r6 = r6.getBoolean(r5)     // Catch:{ RemoteException -> 0x0094 }
            r13.putBoolean(r5, r6)     // Catch:{ RemoteException -> 0x0094 }
            goto L_0x00a9
        L_0x007f:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0094 }
            r6.<init>()     // Catch:{ RemoteException -> 0x0094 }
            java.lang.String r7 = "migrateSharedPreferenceFromLegacy: unsupported key: "
            r6.append(r7)     // Catch:{ RemoteException -> 0x0094 }
            r6.append(r5)     // Catch:{ RemoteException -> 0x0094 }
            java.lang.String r5 = r6.toString()     // Catch:{ RemoteException -> 0x0094 }
            android.util.Log.d(r3, r5)     // Catch:{ RemoteException -> 0x0094 }
            goto L_0x00a9
        L_0x0094:
            r5 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b5 }
            r6.<init>()     // Catch:{ all -> 0x00b5 }
            java.lang.String r7 = "fails to get shared preference "
            r6.append(r7)     // Catch:{ all -> 0x00b5 }
            r6.append(r5)     // Catch:{ all -> 0x00b5 }
            java.lang.String r5 = r6.toString()     // Catch:{ all -> 0x00b5 }
            android.util.Log.e(r3, r5)     // Catch:{ all -> 0x00b5 }
        L_0x00a9:
            int r4 = r4 + 1
            goto L_0x0042
        L_0x00ac:
            r13.apply()     // Catch:{ all -> 0x00b5 }
            if (r2 == 0) goto L_0x00d6
            r2.close()     // Catch:{ Exception -> 0x00c1 }
            goto L_0x00d6
        L_0x00b5:
            r13 = move-exception
            if (r2 == 0) goto L_0x00c0
            r2.close()     // Catch:{ all -> 0x00bc }
            goto L_0x00c0
        L_0x00bc:
            r0 = move-exception
            r13.addSuppressed(r0)     // Catch:{ Exception -> 0x00c1 }
        L_0x00c0:
            throw r13     // Catch:{ Exception -> 0x00c1 }
        L_0x00c1:
            r13 = move-exception
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Failed migration from legacy provider: "
            r0.append(r1)
            r0.append(r13)
            java.lang.String r13 = r0.toString()
            loge(r13)
        L_0x00d6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastreceiver.CellBroadcastReceiver.migrateSharedPreferenceFromLegacy():void");
    }

    public void handleCdmaSmsCbProgramData(ArrayList<CdmaSmsCbProgramData> arrayList) {
        Iterator<CdmaSmsCbProgramData> it = arrayList.iterator();
        while (it.hasNext()) {
            CdmaSmsCbProgramData next = it.next();
            int operation = next.getOperation();
            if (operation == 0) {
                tryCdmaSetCategory(this.mContext, next.getCategory(), false);
            } else if (operation == 1) {
                tryCdmaSetCategory(this.mContext, next.getCategory(), true);
            } else if (operation != 2) {
                loge("Ignoring unknown SCPD operation " + next.getOperation());
            } else {
                tryCdmaSetCategory(this.mContext, 4097, false);
                tryCdmaSetCategory(this.mContext, 4098, false);
                tryCdmaSetCategory(this.mContext, 4099, false);
                tryCdmaSetCategory(this.mContext, 4100, false);
            }
        }
    }

    public void tryCdmaSetCategory(Context context, int i, boolean z) {
        String str;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (i) {
            case 4097:
                defaultSharedPreferences.edit().putBoolean("enable_cmas_extreme_threat_alerts", z).apply();
                return;
            case 4098:
                defaultSharedPreferences.edit().putBoolean("enable_cmas_severe_threat_alerts", z).apply();
                return;
            case 4099:
                defaultSharedPreferences.edit().putBoolean("enable_cmas_amber_alerts", z).apply();
                return;
            case 4100:
                defaultSharedPreferences.edit().putBoolean("enable_test_alerts", z).apply();
                return;
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("Ignoring SCPD command to ");
                if (z) {
                    str = "enable";
                } else {
                    str = "disable";
                }
                sb.append(str);
                sb.append(" alerts in category ");
                sb.append(i);
                Log.w("CellBroadcastReceiver", sb.toString());
                return;
        }
    }

    public boolean isSystemUser() {
        return isSystemUser(this.mContext);
    }

    public void startConfigServiceToEnableChannels() {
        startConfigService(this.mContext, CellBroadcastConfigService.ACTION_ENABLE_CHANNELS);
    }

    private static boolean isSystemUser(Context context) {
        return ((UserManager) context.getSystemService("user")).isSystemUser();
    }

    static void startConfigService(Context context, String str) {
        if (isSystemUser(context)) {
            Log.d("CellBroadcastReceiver", "Start Cell Broadcast configuration for intent=" + str);
            context.startService(new Intent(str, (Uri) null, context, CellBroadcastConfigService.class));
            return;
        }
        Log.e("CellBroadcastReceiver", "startConfigService: Not system user.");
    }

    public void enableLauncher() {
        boolean z = getResourcesMethod().getBoolean(R.bool.show_message_history_in_launcher);
        PackageManager packageManager = this.mContext.getPackageManager();
        String str = null;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(this.mContext.getPackageName(), 513);
            if (packageInfo != null) {
                ActivityInfo[] activityInfoArr = packageInfo.activities;
                int length = activityInfoArr.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    ActivityInfo activityInfo = activityInfoArr[i];
                    if (CellBroadcastListActivity.class.getName().equals(activityInfo.targetActivity)) {
                        str = activityInfo.name;
                        break;
                    }
                    i++;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("CellBroadcastReceiver", e.toString());
        }
        if (TextUtils.isEmpty(str)) {
            Log.e("CellBroadcastReceiver", "cannot find launcher activity");
        } else if (z) {
            Log.d("CellBroadcastReceiver", "enable launcher activity: " + str);
            packageManager.setComponentEnabledSetting(new ComponentName(this.mContext, str), 1, 1);
        } else {
            Log.d("CellBroadcastReceiver", "disable launcher activity: " + str);
            packageManager.setComponentEnabledSetting(new ComponentName(this.mContext, str), 2, 1);
        }
    }

    private static void log(String str) {
        Log.d("CellBroadcastReceiver", str);
    }

    private static void loge(String str) {
        Log.e("CellBroadcastReceiver", str);
    }
}

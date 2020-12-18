package com.android.cellbroadcastreceiver;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.android.cellbroadcastreceiver.CellBroadcastChannelManager;
import com.android.cellbroadcastreceiver.module.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CellBroadcastConfigService extends IntentService {
    public static final String ACTION_ENABLE_CHANNELS = "ACTION_ENABLE_CHANNELS";

    public CellBroadcastConfigService() {
        super("CellBroadcastConfigService");
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        if (ACTION_ENABLE_CHANNELS.equals(intent.getAction())) {
            try {
                SubscriptionManager subscriptionManager = (SubscriptionManager) getApplicationContext().getSystemService("telephony_subscription_service");
                if (subscriptionManager != null) {
                    int[] activeSubIdList = getActiveSubIdList(subscriptionManager);
                    if (activeSubIdList.length != 0) {
                        for (int i : activeSubIdList) {
                            log("Enable CellBroadcast on sub " + i);
                            enableCellBroadcastChannels(i);
                        }
                        return;
                    }
                    enableCellBroadcastChannels(Integer.MAX_VALUE);
                }
            } catch (Exception e) {
                Log.e("CellBroadcastConfigService", "exception enabling cell broadcast channels", e);
            }
        }
    }

    private int[] getActiveSubIdList(SubscriptionManager subscriptionManager) {
        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        int size = activeSubscriptionInfoList != null ? activeSubscriptionInfoList.size() : 0;
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = activeSubscriptionInfoList.get(i).getSubscriptionId();
        }
        return iArr;
    }

    private void resetCellBroadcastChannels(int i) {
        SmsManager smsManager;
        if (i != Integer.MAX_VALUE) {
            smsManager = SmsManager.getSmsManagerForSubscriptionId(i);
        } else {
            smsManager = SmsManager.getDefault();
        }
        try {
            SmsManager.class.getDeclaredMethod("resetAllCellBroadcastRanges", new Class[0]).invoke(smsManager, new Object[0]);
        } catch (Exception e) {
            log("Can't reset cell broadcast ranges. e=" + e);
        }
    }

    public void enableCellBroadcastChannels(int i) {
        resetCellBroadcastChannels(i);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Resources resources = CellBroadcastSettings.getResources(this, i);
        boolean z = defaultSharedPreferences.getBoolean("enable_alerts_master_toggle", true);
        boolean z2 = z && defaultSharedPreferences.getBoolean("enable_cmas_extreme_threat_alerts", true);
        boolean z3 = z && defaultSharedPreferences.getBoolean("enable_cmas_severe_threat_alerts", true);
        boolean z4 = z && defaultSharedPreferences.getBoolean("enable_cmas_amber_alerts", true);
        boolean z5 = z && CellBroadcastSettings.isTestAlertsToggleVisible(getApplicationContext()) && defaultSharedPreferences.getBoolean("enable_test_alerts", false);
        boolean z6 = resources.getBoolean(R.bool.config_showAreaUpdateInfoSettings) && defaultSharedPreferences.getBoolean("enable_area_update_info_alerts", false);
        boolean z7 = z && defaultSharedPreferences.getBoolean("enable_public_safety_messages", true);
        boolean z8 = z && defaultSharedPreferences.getBoolean("enable_state_local_test_alerts", false);
        boolean z9 = z && defaultSharedPreferences.getBoolean("enable_emergency_alerts", true);
        CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(getApplicationContext(), i);
        setCellBroadcastRange(i, true, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.cmas_presidential_alerts_channels_range_strings));
        setCellBroadcastRange(i, z2, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.cmas_alert_extreme_channels_range_strings));
        setCellBroadcastRange(i, z3, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.cmas_alerts_severe_range_strings));
        setCellBroadcastRange(i, z4, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.cmas_amber_alerts_channels_range_strings));
        setCellBroadcastRange(i, z5, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.required_monthly_test_range_strings));
        setCellBroadcastRange(i, z5 || resources.getBoolean(R.bool.always_enable_exercise_alert), cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.exercise_alert_range_strings));
        setCellBroadcastRange(i, z5, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.operator_defined_alert_range_strings));
        setCellBroadcastRange(i, z, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.etws_alerts_range_strings));
        setCellBroadcastRange(i, z5, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.etws_test_alerts_range_strings));
        setCellBroadcastRange(i, z7, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.public_safety_messages_channels_range_strings));
        setCellBroadcastRange(i, z8, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.state_local_test_alert_range_strings));
        setCellBroadcastRange(i, true, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.geo_fencing_trigger_messages_range_strings));
        setCellBroadcastRange(i, z9, cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.emergency_alerts_channels_range_strings));
        Iterator<CellBroadcastChannelManager.CellBroadcastChannelRange> it = cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.additional_cbs_channels_strings).iterator();
        while (it.hasNext()) {
            CellBroadcastChannelManager.CellBroadcastChannelRange next = it.next();
            int i2 = C02771.f12x97d16d5c[next.mAlertType.ordinal()];
            setCellBroadcastRange(i, i2 != 1 ? i2 != 2 ? z : z5 : z6, new ArrayList(Arrays.asList(new CellBroadcastChannelManager.CellBroadcastChannelRange[]{next})));
        }
    }

    /* renamed from: com.android.cellbroadcastreceiver.CellBroadcastConfigService$1 */
    static /* synthetic */ class C02771 {

        /* renamed from: $SwitchMap$com$android$cellbroadcastreceiver$CellBroadcastAlertService$AlertType */
        static final /* synthetic */ int[] f12x97d16d5c;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        static {
            /*
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType[] r0 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f12x97d16d5c = r0
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.AREA     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f12x97d16d5c     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.TEST     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastreceiver.CellBroadcastConfigService.C02771.<clinit>():void");
        }
    }

    private void setCellBroadcastRange(int i, boolean z, List<CellBroadcastChannelManager.CellBroadcastChannelRange> list) {
        SmsManager smsManager;
        if (i != Integer.MAX_VALUE) {
            smsManager = SmsManager.getSmsManagerForSubscriptionId(i);
        } else {
            smsManager = SmsManager.getDefault();
        }
        if (list != null) {
            for (CellBroadcastChannelManager.CellBroadcastChannelRange next : list) {
                if (z) {
                    smsManager.enableCellBroadcastRange(next.mStartId, next.mEndId, next.mRanType);
                } else {
                    smsManager.disableCellBroadcastRange(next.mStartId, next.mEndId, next.mRanType);
                }
            }
        }
    }

    private static void log(String str) {
        Log.d("CellBroadcastConfigService", str);
    }
}

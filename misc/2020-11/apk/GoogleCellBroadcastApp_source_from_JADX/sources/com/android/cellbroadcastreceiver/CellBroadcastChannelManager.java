package com.android.cellbroadcastreceiver;

import android.content.Context;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.SmsCbMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.cellbroadcastreceiver.CellBroadcastAlertService;
import com.android.cellbroadcastreceiver.module.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CellBroadcastChannelManager {
    private static ArrayList<CellBroadcastChannelRange> sAllCellBroadcastChannelRanges = null;
    private static List<Integer> sCellBroadcastRangeResourceKeys = new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.array.additional_cbs_channels_strings), Integer.valueOf(R.array.emergency_alerts_channels_range_strings), Integer.valueOf(R.array.cmas_presidential_alerts_channels_range_strings), Integer.valueOf(R.array.cmas_alert_extreme_channels_range_strings), Integer.valueOf(R.array.cmas_alerts_severe_range_strings), Integer.valueOf(R.array.cmas_amber_alerts_channels_range_strings), Integer.valueOf(R.array.required_monthly_test_range_strings), Integer.valueOf(R.array.exercise_alert_range_strings), Integer.valueOf(R.array.operator_defined_alert_range_strings), Integer.valueOf(R.array.etws_alerts_range_strings), Integer.valueOf(R.array.etws_test_alerts_range_strings), Integer.valueOf(R.array.public_safety_messages_channels_range_strings), Integer.valueOf(R.array.state_local_test_alert_range_strings)}));
    private final Context mContext;
    private final int mSubId;

    public static class CellBroadcastChannelRange {
        public int mAlertDuration = -1;
        public CellBroadcastAlertService.AlertType mAlertType = CellBroadcastAlertService.AlertType.DEFAULT;
        public int mEmergencyLevel = 0;
        public int mEndId;
        public boolean mFilterLanguage;
        public boolean mOverrideDnd = false;
        public int mRanType = 1;
        public int mScope = 0;
        public int mStartId;
        public int[] mVibrationPattern;
        public boolean mWriteToSmsInbox = true;

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public CellBroadcastChannelRange(android.content.Context r11, int r12, java.lang.String r13) {
            /*
                r10 = this;
                r10.<init>()
                r0 = -1
                r10.mAlertDuration = r0
                r1 = 0
                r10.mOverrideDnd = r1
                r2 = 1
                r10.mWriteToSmsInbox = r2
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r3 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.DEFAULT
                r10.mAlertType = r3
                r10.mEmergencyLevel = r1
                r10.mRanType = r2
                r10.mScope = r1
                android.content.res.Resources r11 = com.android.cellbroadcastreceiver.CellBroadcastSettings.getResources(r11, r12)
                r12 = 2130837512(0x7f020008, float:1.727998E38)
                int[] r11 = r11.getIntArray(r12)
                r10.mVibrationPattern = r11
                r10.mFilterLanguage = r1
                r11 = 58
                int r11 = r13.indexOf(r11)
                if (r11 == r0) goto L_0x0167
                int r12 = r11 + 1
                java.lang.String r12 = r13.substring(r12)
                java.lang.String r12 = r12.trim()
                java.lang.String r3 = ","
                java.lang.String[] r12 = r12.split(r3)
                int r3 = r12.length
                r4 = r1
            L_0x003f:
                if (r4 >= r3) goto L_0x015f
                r5 = r12[r4]
                java.lang.String r5 = r5.trim()
                java.lang.String r6 = "="
                java.lang.String[] r5 = r5.split(r6)
                int r6 = r5.length
                r7 = 2
                if (r6 != r7) goto L_0x015b
                r6 = r5[r1]
                java.lang.String r6 = r6.trim()
                r5 = r5[r2]
                java.lang.String r5 = r5.trim()
                int r8 = r6.hashCode()
                r9 = 3
                switch(r8) {
                    case -1185914817: goto L_0x00b8;
                    case -886129385: goto L_0x00ae;
                    case -630324528: goto L_0x00a3;
                    case -81857902: goto L_0x0099;
                    case 112677: goto L_0x008f;
                    case 3575610: goto L_0x0085;
                    case 66652711: goto L_0x007b;
                    case 109264468: goto L_0x0071;
                    case 1629013393: goto L_0x0067;
                    default: goto L_0x0065;
                }
            L_0x0065:
                goto L_0x00c2
            L_0x0067:
                java.lang.String r8 = "emergency"
                boolean r6 = r6.equals(r8)
                if (r6 == 0) goto L_0x00c2
                r6 = r2
                goto L_0x00c3
            L_0x0071:
                java.lang.String r8 = "scope"
                boolean r6 = r6.equals(r8)
                if (r6 == 0) goto L_0x00c2
                r6 = r9
                goto L_0x00c3
            L_0x007b:
                java.lang.String r8 = "override_dnd"
                boolean r6 = r6.equals(r8)
                if (r6 == 0) goto L_0x00c2
                r6 = 7
                goto L_0x00c3
            L_0x0085:
                java.lang.String r8 = "type"
                boolean r6 = r6.equals(r8)
                if (r6 == 0) goto L_0x00c2
                r6 = r1
                goto L_0x00c3
            L_0x008f:
                java.lang.String r8 = "rat"
                boolean r6 = r6.equals(r8)
                if (r6 == 0) goto L_0x00c2
                r6 = r7
                goto L_0x00c3
            L_0x0099:
                java.lang.String r8 = "vibration"
                boolean r6 = r6.equals(r8)
                if (r6 == 0) goto L_0x00c2
                r6 = 4
                goto L_0x00c3
            L_0x00a3:
                java.lang.String r8 = "exclude_from_sms_inbox"
                boolean r6 = r6.equals(r8)
                if (r6 == 0) goto L_0x00c2
                r6 = 8
                goto L_0x00c3
            L_0x00ae:
                java.lang.String r8 = "alert_duration"
                boolean r6 = r6.equals(r8)
                if (r6 == 0) goto L_0x00c2
                r6 = 6
                goto L_0x00c3
            L_0x00b8:
                java.lang.String r8 = "filter_language"
                boolean r6 = r6.equals(r8)
                if (r6 == 0) goto L_0x00c2
                r6 = 5
                goto L_0x00c3
            L_0x00c2:
                r6 = r0
            L_0x00c3:
                java.lang.String r8 = "true"
                switch(r6) {
                    case 0: goto L_0x0151;
                    case 1: goto L_0x013d;
                    case 2: goto L_0x0130;
                    case 3: goto L_0x010f;
                    case 4: goto L_0x00f0;
                    case 5: goto L_0x00e6;
                    case 6: goto L_0x00de;
                    case 7: goto L_0x00d4;
                    case 8: goto L_0x00ca;
                    default: goto L_0x00c8;
                }
            L_0x00c8:
                goto L_0x015b
            L_0x00ca:
                boolean r5 = r5.equalsIgnoreCase(r8)
                if (r5 == 0) goto L_0x015b
                r10.mWriteToSmsInbox = r1
                goto L_0x015b
            L_0x00d4:
                boolean r5 = r5.equalsIgnoreCase(r8)
                if (r5 == 0) goto L_0x015b
                r10.mOverrideDnd = r2
                goto L_0x015b
            L_0x00de:
                int r5 = java.lang.Integer.parseInt(r5)
                r10.mAlertDuration = r5
                goto L_0x015b
            L_0x00e6:
                boolean r5 = r5.equalsIgnoreCase(r8)
                if (r5 == 0) goto L_0x015b
                r10.mFilterLanguage = r2
                goto L_0x015b
            L_0x00f0:
                java.lang.String r6 = "\\|"
                java.lang.String[] r5 = r5.split(r6)
                int r6 = r5.length
                if (r6 <= 0) goto L_0x015b
                int r6 = r5.length
                int[] r6 = new int[r6]
                r10.mVibrationPattern = r6
                r6 = r1
            L_0x00ff:
                int r7 = r5.length
                if (r6 >= r7) goto L_0x015b
                int[] r7 = r10.mVibrationPattern
                r8 = r5[r6]
                int r8 = java.lang.Integer.parseInt(r8)
                r7[r6] = r8
                int r6 = r6 + 1
                goto L_0x00ff
            L_0x010f:
                java.lang.String r6 = "carrier"
                boolean r6 = r5.equalsIgnoreCase(r6)
                if (r6 == 0) goto L_0x011a
                r10.mScope = r2
                goto L_0x015b
            L_0x011a:
                java.lang.String r6 = "domestic"
                boolean r6 = r5.equalsIgnoreCase(r6)
                if (r6 == 0) goto L_0x0125
                r10.mScope = r7
                goto L_0x015b
            L_0x0125:
                java.lang.String r6 = "international"
                boolean r5 = r5.equalsIgnoreCase(r6)
                if (r5 == 0) goto L_0x015b
                r10.mScope = r9
                goto L_0x015b
            L_0x0130:
                java.lang.String r6 = "cdma"
                boolean r5 = r5.equalsIgnoreCase(r6)
                if (r5 == 0) goto L_0x0139
                goto L_0x013a
            L_0x0139:
                r7 = r2
            L_0x013a:
                r10.mRanType = r7
                goto L_0x015b
            L_0x013d:
                boolean r6 = r5.equalsIgnoreCase(r8)
                if (r6 == 0) goto L_0x0146
                r10.mEmergencyLevel = r7
                goto L_0x015b
            L_0x0146:
                java.lang.String r6 = "false"
                boolean r5 = r5.equalsIgnoreCase(r6)
                if (r5 == 0) goto L_0x015b
                r10.mEmergencyLevel = r2
                goto L_0x015b
            L_0x0151:
                java.lang.String r5 = r5.toUpperCase()
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r5 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.valueOf(r5)
                r10.mAlertType = r5
            L_0x015b:
                int r4 = r4 + 1
                goto L_0x003f
            L_0x015f:
                java.lang.String r11 = r13.substring(r1, r11)
                java.lang.String r13 = r11.trim()
            L_0x0167:
                r11 = 45
                int r11 = r13.indexOf(r11)
                if (r11 == r0) goto L_0x0195
                java.lang.String r12 = r13.substring(r1, r11)
                java.lang.String r12 = r12.trim()
                java.lang.Integer r12 = java.lang.Integer.decode(r12)
                int r12 = r12.intValue()
                r10.mStartId = r12
                int r11 = r11 + r2
                java.lang.String r11 = r13.substring(r11)
                java.lang.String r11 = r11.trim()
                java.lang.Integer r11 = java.lang.Integer.decode(r11)
                int r11 = r11.intValue()
                r10.mEndId = r11
                goto L_0x01a1
            L_0x0195:
                java.lang.Integer r11 = java.lang.Integer.decode(r13)
                int r11 = r11.intValue()
                r10.mEndId = r11
                r10.mStartId = r11
            L_0x01a1:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastreceiver.CellBroadcastChannelManager.CellBroadcastChannelRange.<init>(android.content.Context, int, java.lang.String):void");
        }

        public String toString() {
            return "Range:[channels=" + this.mStartId + "-" + this.mEndId + ",emergency level=" + this.mEmergencyLevel + ",type=" + this.mAlertType + ",scope=" + this.mScope + ",vibration=" + Arrays.toString(this.mVibrationPattern) + ",alertDuration=" + this.mAlertDuration + ",filter_language=" + this.mFilterLanguage + ",override_dnd=" + this.mOverrideDnd + "]";
        }
    }

    public CellBroadcastChannelManager(Context context, int i) {
        this.mContext = context;
        this.mSubId = i;
    }

    public ArrayList<CellBroadcastChannelRange> getCellBroadcastChannelRanges(int i) {
        ArrayList<CellBroadcastChannelRange> arrayList = new ArrayList<>();
        for (String str : CellBroadcastSettings.getResources(this.mContext, this.mSubId).getStringArray(i)) {
            try {
                arrayList.add(new CellBroadcastChannelRange(this.mContext, this.mSubId, str));
            } catch (Exception e) {
                loge("Failed to parse \"" + str + "\". e=" + e);
            }
        }
        return arrayList;
    }

    public ArrayList<CellBroadcastChannelRange> getAllCellBroadcastChannelRanges() {
        ArrayList<CellBroadcastChannelRange> arrayList = sAllCellBroadcastChannelRanges;
        if (arrayList != null) {
            return arrayList;
        }
        ArrayList<CellBroadcastChannelRange> arrayList2 = new ArrayList<>();
        for (Integer intValue : sCellBroadcastRangeResourceKeys) {
            arrayList2.addAll(getCellBroadcastChannelRanges(intValue.intValue()));
        }
        sAllCellBroadcastChannelRanges = arrayList2;
        return arrayList2;
    }

    public boolean checkCellBroadcastChannelRange(int i, int i2) {
        Iterator<CellBroadcastChannelRange> it = getCellBroadcastChannelRanges(i2).iterator();
        while (it.hasNext()) {
            CellBroadcastChannelRange next = it.next();
            if (i >= next.mStartId && i <= next.mEndId) {
                return checkScope(next.mScope);
            }
        }
        return false;
    }

    public boolean checkScope(int i) {
        ServiceState serviceState;
        NetworkRegistrationInfo networkRegistrationInfo;
        int roamingType;
        if (i == 0 || (serviceState = ((TelephonyManager) this.mContext.getSystemService("phone")).createForSubscriptionId(this.mSubId).getServiceState()) == null || (networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(1, 1)) == null || ((networkRegistrationInfo.getRegistrationState() != 1 && networkRegistrationInfo.getRegistrationState() != 5 && !networkRegistrationInfo.isEmergencyEnabled()) || (roamingType = networkRegistrationInfo.getRoamingType()) == 0)) {
            return true;
        }
        if (roamingType == 2 && i == 2) {
            return true;
        }
        if (roamingType == 3 && i == 3) {
            return true;
        }
        return false;
    }

    public CellBroadcastChannelRange getCellBroadcastChannelRangeFromMessage(SmsCbMessage smsCbMessage) {
        ArrayList<CellBroadcastChannelRange> arrayList;
        if (this.mSubId != smsCbMessage.getSubscriptionId()) {
            Log.e("CBChannelManager", "getCellBroadcastChannelRangeFromMessage: This manager is created for sub " + this.mSubId + ", should not be used for message from sub " + smsCbMessage.getSubscriptionId());
        }
        int serviceCategory = smsCbMessage.getServiceCategory();
        Iterator<Integer> it = sCellBroadcastRangeResourceKeys.iterator();
        while (true) {
            if (!it.hasNext()) {
                arrayList = null;
                break;
            }
            int intValue = it.next().intValue();
            if (checkCellBroadcastChannelRange(serviceCategory, intValue)) {
                arrayList = getCellBroadcastChannelRanges(intValue);
                break;
            }
        }
        if (arrayList != null) {
            Iterator<CellBroadcastChannelRange> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                CellBroadcastChannelRange next = it2.next();
                if (next.mStartId <= smsCbMessage.getServiceCategory() && next.mEndId >= smsCbMessage.getServiceCategory()) {
                    return next;
                }
            }
        }
        return null;
    }

    public boolean isEmergencyMessage(SmsCbMessage smsCbMessage) {
        if (smsCbMessage == null) {
            return false;
        }
        if (this.mSubId != smsCbMessage.getSubscriptionId()) {
            Log.e("CBChannelManager", "This manager is created for sub " + this.mSubId + ", should not be used for message from sub " + smsCbMessage.getSubscriptionId());
        }
        int serviceCategory = smsCbMessage.getServiceCategory();
        for (Integer intValue : sCellBroadcastRangeResourceKeys) {
            Iterator<CellBroadcastChannelRange> it = getCellBroadcastChannelRanges(intValue.intValue()).iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                CellBroadcastChannelRange next = it.next();
                if (next.mStartId <= serviceCategory && next.mEndId >= serviceCategory) {
                    int i = next.mEmergencyLevel;
                    if (i == 1) {
                        Log.d("CBChannelManager", "isEmergencyMessage: false, message id = " + serviceCategory);
                        return false;
                    } else if (i == 2) {
                        Log.d("CBChannelManager", "isEmergencyMessage: true, message id = " + serviceCategory);
                        return true;
                    }
                }
            }
        }
        Log.d("CBChannelManager", "isEmergencyMessage: " + smsCbMessage.isEmergencyMessage() + ", message id = " + serviceCategory);
        return smsCbMessage.isEmergencyMessage();
    }

    private static void loge(String str) {
        Log.e("CBChannelManager", str);
    }
}

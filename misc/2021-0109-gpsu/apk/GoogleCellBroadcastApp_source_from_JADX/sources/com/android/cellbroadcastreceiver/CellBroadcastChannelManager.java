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
        public boolean mAlwaysOn = false;
        public boolean mDismissOnOutsideTouch = false;
        public boolean mDisplay;
        public boolean mDisplayIcon = true;
        public int mEmergencyLevel = 0;
        public int mEndId;
        public boolean mFilterLanguage;
        public String mLanguageCode;
        public boolean mOverrideDnd = false;
        public int mRanType = 1;
        public int mScope = 0;
        public int mScreenOnDuration = 60000;
        public int mStartId;
        public boolean mTestMode;
        public int[] mVibrationPattern;
        public boolean mWriteToSmsInbox = true;

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public CellBroadcastChannelRange(android.content.Context r17, int r18, java.lang.String r19) {
            /*
                r16 = this;
                r0 = r16
                r1 = r19
                r16.<init>()
                r2 = -1
                r0.mAlertDuration = r2
                r3 = 0
                r0.mOverrideDnd = r3
                r4 = 1
                r0.mWriteToSmsInbox = r4
                r0.mAlwaysOn = r3
                r5 = 60000(0xea60, float:8.4078E-41)
                r0.mScreenOnDuration = r5
                r0.mDisplayIcon = r4
                r0.mDismissOnOutsideTouch = r3
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r5 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.DEFAULT
                r0.mAlertType = r5
                r0.mEmergencyLevel = r3
                r0.mRanType = r4
                r0.mScope = r3
                android.content.res.Resources r5 = com.android.cellbroadcastreceiver.CellBroadcastSettings.getResources(r17, r18)
                r6 = 2130837513(0x7f020009, float:1.7279982E38)
                int[] r5 = r5.getIntArray(r6)
                r0.mVibrationPattern = r5
                r0.mFilterLanguage = r3
                r0.mDisplay = r4
                r0.mTestMode = r3
                r5 = 58
                int r5 = r1.indexOf(r5)
                if (r5 == r2) goto L_0x0210
                int r6 = r5 + 1
                java.lang.String r6 = r1.substring(r6)
                java.lang.String r6 = r6.trim()
                java.lang.String r7 = ","
                java.lang.String[] r6 = r6.split(r7)
                int r7 = r6.length
                r8 = r3
                r9 = r8
            L_0x0053:
                if (r8 >= r7) goto L_0x0207
                r10 = r6[r8]
                java.lang.String r10 = r10.trim()
                java.lang.String r11 = "="
                java.lang.String[] r10 = r10.split(r11)
                int r11 = r10.length
                r12 = 2
                if (r11 != r12) goto L_0x0203
                r11 = r10[r3]
                java.lang.String r11 = r11.trim()
                r10 = r10[r4]
                java.lang.String r10 = r10.trim()
                int r13 = r11.hashCode()
                r14 = 3
                switch(r13) {
                    case -1767413030: goto L_0x011f;
                    case -1613589672: goto L_0x0114;
                    case -1185914817: goto L_0x010a;
                    case -886129385: goto L_0x0100;
                    case -630324528: goto L_0x00f5;
                    case -287672159: goto L_0x00ea;
                    case -81857902: goto L_0x00e0;
                    case 112677: goto L_0x00d6;
                    case 3575610: goto L_0x00cc;
                    case 66652711: goto L_0x00c1;
                    case 109264468: goto L_0x00b6;
                    case 842238578: goto L_0x00aa;
                    case 1146581519: goto L_0x009e;
                    case 1614939606: goto L_0x0092;
                    case 1629013393: goto L_0x0087;
                    case 1671764162: goto L_0x007b;
                    default: goto L_0x0079;
                }
            L_0x0079:
                goto L_0x012a
            L_0x007b:
                java.lang.String r13 = "display"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 9
                goto L_0x012b
            L_0x0087:
                java.lang.String r13 = "emergency"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = r4
                goto L_0x012b
            L_0x0092:
                java.lang.String r13 = "display_icon"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 13
                goto L_0x012b
            L_0x009e:
                java.lang.String r13 = "always_on"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 11
                goto L_0x012b
            L_0x00aa:
                java.lang.String r13 = "testing_mode"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 10
                goto L_0x012b
            L_0x00b6:
                java.lang.String r13 = "scope"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = r14
                goto L_0x012b
            L_0x00c1:
                java.lang.String r13 = "override_dnd"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 7
                goto L_0x012b
            L_0x00cc:
                java.lang.String r13 = "type"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = r3
                goto L_0x012b
            L_0x00d6:
                java.lang.String r13 = "rat"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = r12
                goto L_0x012b
            L_0x00e0:
                java.lang.String r13 = "vibration"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 4
                goto L_0x012b
            L_0x00ea:
                java.lang.String r13 = "screen_on_duration"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 12
                goto L_0x012b
            L_0x00f5:
                java.lang.String r13 = "exclude_from_sms_inbox"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 8
                goto L_0x012b
            L_0x0100:
                java.lang.String r13 = "alert_duration"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 6
                goto L_0x012b
            L_0x010a:
                java.lang.String r13 = "filter_language"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 5
                goto L_0x012b
            L_0x0114:
                java.lang.String r13 = "language"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 15
                goto L_0x012b
            L_0x011f:
                java.lang.String r13 = "dismiss_on_outside_touch"
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x012a
                r11 = 14
                goto L_0x012b
            L_0x012a:
                r11 = r2
            L_0x012b:
                java.lang.String r13 = "false"
                java.lang.String r15 = "true"
                switch(r11) {
                    case 0: goto L_0x01f9;
                    case 1: goto L_0x01e7;
                    case 2: goto L_0x01da;
                    case 3: goto L_0x01b9;
                    case 4: goto L_0x0198;
                    case 5: goto L_0x018e;
                    case 6: goto L_0x0186;
                    case 7: goto L_0x017c;
                    case 8: goto L_0x0172;
                    case 9: goto L_0x0168;
                    case 10: goto L_0x015e;
                    case 11: goto L_0x0154;
                    case 12: goto L_0x014c;
                    case 13: goto L_0x0142;
                    case 14: goto L_0x0138;
                    case 15: goto L_0x0134;
                    default: goto L_0x0132;
                }
            L_0x0132:
                goto L_0x0203
            L_0x0134:
                r0.mLanguageCode = r10
                goto L_0x0203
            L_0x0138:
                boolean r10 = r10.equalsIgnoreCase(r15)
                if (r10 == 0) goto L_0x0203
                r0.mDismissOnOutsideTouch = r4
                goto L_0x0203
            L_0x0142:
                boolean r10 = r10.equalsIgnoreCase(r13)
                if (r10 == 0) goto L_0x0203
                r0.mDisplayIcon = r3
                goto L_0x0203
            L_0x014c:
                int r10 = java.lang.Integer.parseInt(r10)
                r0.mScreenOnDuration = r10
                goto L_0x0203
            L_0x0154:
                boolean r10 = r10.equalsIgnoreCase(r15)
                if (r10 == 0) goto L_0x0203
                r0.mAlwaysOn = r4
                goto L_0x0203
            L_0x015e:
                boolean r10 = r10.equalsIgnoreCase(r15)
                if (r10 == 0) goto L_0x0203
                r0.mTestMode = r4
                goto L_0x0203
            L_0x0168:
                boolean r10 = r10.equalsIgnoreCase(r13)
                if (r10 == 0) goto L_0x0203
                r0.mDisplay = r3
                goto L_0x0203
            L_0x0172:
                boolean r10 = r10.equalsIgnoreCase(r15)
                if (r10 == 0) goto L_0x0203
                r0.mWriteToSmsInbox = r3
                goto L_0x0203
            L_0x017c:
                boolean r10 = r10.equalsIgnoreCase(r15)
                if (r10 == 0) goto L_0x0203
                r0.mOverrideDnd = r4
                goto L_0x0203
            L_0x0186:
                int r10 = java.lang.Integer.parseInt(r10)
                r0.mAlertDuration = r10
                goto L_0x0203
            L_0x018e:
                boolean r10 = r10.equalsIgnoreCase(r15)
                if (r10 == 0) goto L_0x0203
                r0.mFilterLanguage = r4
                goto L_0x0203
            L_0x0198:
                java.lang.String r11 = "\\|"
                java.lang.String[] r10 = r10.split(r11)
                int r11 = r10.length
                if (r11 <= 0) goto L_0x0203
                int r9 = r10.length
                int[] r9 = new int[r9]
                r0.mVibrationPattern = r9
                r9 = r3
            L_0x01a7:
                int r11 = r10.length
                if (r9 >= r11) goto L_0x01b7
                int[] r11 = r0.mVibrationPattern
                r12 = r10[r9]
                int r12 = java.lang.Integer.parseInt(r12)
                r11[r9] = r12
                int r9 = r9 + 1
                goto L_0x01a7
            L_0x01b7:
                r9 = r4
                goto L_0x0203
            L_0x01b9:
                java.lang.String r11 = "carrier"
                boolean r11 = r10.equalsIgnoreCase(r11)
                if (r11 == 0) goto L_0x01c4
                r0.mScope = r4
                goto L_0x0203
            L_0x01c4:
                java.lang.String r11 = "domestic"
                boolean r11 = r10.equalsIgnoreCase(r11)
                if (r11 == 0) goto L_0x01cf
                r0.mScope = r12
                goto L_0x0203
            L_0x01cf:
                java.lang.String r11 = "international"
                boolean r10 = r10.equalsIgnoreCase(r11)
                if (r10 == 0) goto L_0x0203
                r0.mScope = r14
                goto L_0x0203
            L_0x01da:
                java.lang.String r11 = "cdma"
                boolean r10 = r10.equalsIgnoreCase(r11)
                if (r10 == 0) goto L_0x01e3
                goto L_0x01e4
            L_0x01e3:
                r12 = r4
            L_0x01e4:
                r0.mRanType = r12
                goto L_0x0203
            L_0x01e7:
                boolean r11 = r10.equalsIgnoreCase(r15)
                if (r11 == 0) goto L_0x01f0
                r0.mEmergencyLevel = r12
                goto L_0x0203
            L_0x01f0:
                boolean r10 = r10.equalsIgnoreCase(r13)
                if (r10 == 0) goto L_0x0203
                r0.mEmergencyLevel = r4
                goto L_0x0203
            L_0x01f9:
                java.lang.String r10 = r10.toUpperCase()
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r10 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.valueOf(r10)
                r0.mAlertType = r10
            L_0x0203:
                int r8 = r8 + 1
                goto L_0x0053
            L_0x0207:
                java.lang.String r1 = r1.substring(r3, r5)
                java.lang.String r1 = r1.trim()
                goto L_0x0211
            L_0x0210:
                r9 = r3
            L_0x0211:
                if (r9 != 0) goto L_0x022a
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r5 = r0.mAlertType
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r6 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.INFO
                boolean r5 = r5.equals(r6)
                if (r5 == 0) goto L_0x022a
                android.content.res.Resources r5 = com.android.cellbroadcastreceiver.CellBroadcastSettings.getResources(r17, r18)
                r6 = 2130837512(0x7f020008, float:1.727998E38)
                int[] r5 = r5.getIntArray(r6)
                r0.mVibrationPattern = r5
            L_0x022a:
                r5 = 45
                int r5 = r1.indexOf(r5)
                if (r5 == r2) goto L_0x0258
                java.lang.String r2 = r1.substring(r3, r5)
                java.lang.String r2 = r2.trim()
                java.lang.Integer r2 = java.lang.Integer.decode(r2)
                int r2 = r2.intValue()
                r0.mStartId = r2
                int r5 = r5 + r4
                java.lang.String r1 = r1.substring(r5)
                java.lang.String r1 = r1.trim()
                java.lang.Integer r1 = java.lang.Integer.decode(r1)
                int r1 = r1.intValue()
                r0.mEndId = r1
                goto L_0x0264
            L_0x0258:
                java.lang.Integer r1 = java.lang.Integer.decode(r1)
                int r1 = r1.intValue()
                r0.mEndId = r1
                r0.mStartId = r1
            L_0x0264:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastreceiver.CellBroadcastChannelManager.CellBroadcastChannelRange.<init>(android.content.Context, int, java.lang.String):void");
        }

        public String toString() {
            return "Range:[channels=" + this.mStartId + "-" + this.mEndId + ",emergency level=" + this.mEmergencyLevel + ",type=" + this.mAlertType + ",scope=" + this.mScope + ",vibration=" + Arrays.toString(this.mVibrationPattern) + ",alertDuration=" + this.mAlertDuration + ",filter_language=" + this.mFilterLanguage + ",override_dnd=" + this.mOverrideDnd + ",display=" + this.mDisplay + ",testMode=" + this.mTestMode + ",mAlwaysOn=" + this.mAlwaysOn + ",ScreenOnDuration=" + this.mScreenOnDuration + ", displayIcon=" + this.mDisplayIcon + "dismissOnOutsideTouch=" + this.mDismissOnOutsideTouch + ", languageCode=" + this.mLanguageCode + "]";
        }
    }

    public CellBroadcastChannelManager(Context context, int i) {
        this.mContext = context;
        this.mSubId = i;
    }

    public ArrayList<CellBroadcastChannelRange> getCellBroadcastChannelRanges(int i) {
        ArrayList<CellBroadcastChannelRange> arrayList = new ArrayList<>();
        String[] stringArray = CellBroadcastSettings.getResources(this.mContext, this.mSubId).getStringArray(i);
        if (stringArray != null) {
            for (String str : stringArray) {
                try {
                    arrayList.add(new CellBroadcastChannelRange(this.mContext, this.mSubId, str));
                } catch (Exception e) {
                    loge("Failed to parse \"" + str + "\". e=" + e);
                }
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

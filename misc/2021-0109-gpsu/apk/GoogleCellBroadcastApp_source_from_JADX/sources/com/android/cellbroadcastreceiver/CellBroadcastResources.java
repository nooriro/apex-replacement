package com.android.cellbroadcastreceiver;

import android.content.Context;
import android.content.res.Configuration;
import android.telephony.SmsCbCmasInfo;
import android.telephony.SmsCbEtwsInfo;
import android.telephony.SmsCbMessage;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import com.android.cellbroadcastreceiver.CellBroadcastChannelManager;
import com.android.cellbroadcastreceiver.module.R;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class CellBroadcastResources {
    public static CharSequence getMessageDetails(Context context, boolean z, SmsCbMessage smsCbMessage, long j, boolean z2, String str) {
        String str2;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        appendMessageDetail(context, spannableStringBuilder, R.string.delivery_time_heading, DateFormat.getDateTimeInstance().format(Long.valueOf(smsCbMessage.getReceivedTime())));
        if (z) {
            appendMessageDetail(context, spannableStringBuilder, R.string.message_identifier, Integer.toString(smsCbMessage.getServiceCategory()));
            appendMessageDetail(context, spannableStringBuilder, R.string.message_serial_number, Integer.toString(smsCbMessage.getSerialNumber()));
        }
        if (smsCbMessage.isCmasMessage()) {
            appendCmasAlertDetails(context, spannableStringBuilder, smsCbMessage.getCmasWarningInfo());
        }
        if (z) {
            appendMessageDetail(context, spannableStringBuilder, R.string.data_coding_scheme, Integer.toString(smsCbMessage.getDataCodingScheme()));
            appendMessageDetail(context, spannableStringBuilder, R.string.message_content, smsCbMessage.getMessageBody());
            if (j == -1) {
                str2 = "N/A";
            } else {
                str2 = DateFormat.getDateTimeInstance().format(Long.valueOf(j));
            }
            appendMessageDetail(context, spannableStringBuilder, R.string.location_check_time, str2);
            appendMessageDetail(context, spannableStringBuilder, R.string.maximum_waiting_time, smsCbMessage.getMaximumWaitingDuration() + " " + context.getString(R.string.seconds));
            appendMessageDetail(context, spannableStringBuilder, R.string.message_displayed, Boolean.toString(z2));
            if (TextUtils.isEmpty(str)) {
                str = "N/A";
            }
            appendMessageDetail(context, spannableStringBuilder, R.string.message_coordinates, str);
        }
        return spannableStringBuilder;
    }

    private static void appendCmasAlertDetails(Context context, SpannableStringBuilder spannableStringBuilder, SmsCbCmasInfo smsCbCmasInfo) {
        int cmasCategoryResId = getCmasCategoryResId(smsCbCmasInfo);
        if (cmasCategoryResId != 0) {
            appendMessageDetail(context, spannableStringBuilder, R.string.cmas_category_heading, context.getString(cmasCategoryResId));
        }
        int cmasResponseResId = getCmasResponseResId(smsCbCmasInfo);
        if (cmasResponseResId != 0) {
            appendMessageDetail(context, spannableStringBuilder, R.string.cmas_response_heading, context.getString(cmasResponseResId));
        }
        int cmasSeverityResId = getCmasSeverityResId(smsCbCmasInfo);
        if (cmasSeverityResId != 0) {
            appendMessageDetail(context, spannableStringBuilder, R.string.cmas_severity_heading, context.getString(cmasSeverityResId));
        }
        int cmasUrgencyResId = getCmasUrgencyResId(smsCbCmasInfo);
        if (cmasUrgencyResId != 0) {
            appendMessageDetail(context, spannableStringBuilder, R.string.cmas_urgency_heading, context.getString(cmasUrgencyResId));
        }
        int cmasCertaintyResId = getCmasCertaintyResId(smsCbCmasInfo);
        if (cmasCertaintyResId != 0) {
            appendMessageDetail(context, spannableStringBuilder, R.string.cmas_certainty_heading, context.getString(cmasCertaintyResId));
        }
    }

    private static void appendMessageDetail(Context context, SpannableStringBuilder spannableStringBuilder, int i, String str) {
        if (spannableStringBuilder.length() != 0) {
            spannableStringBuilder.append("\n");
        }
        int length = spannableStringBuilder.length();
        spannableStringBuilder.append(context.getString(i));
        spannableStringBuilder.setSpan(new StyleSpan(1), length, spannableStringBuilder.length(), 33);
        spannableStringBuilder.append(" ");
        spannableStringBuilder.append(str);
    }

    private static int getCmasCategoryResId(SmsCbCmasInfo smsCbCmasInfo) {
        switch (smsCbCmasInfo.getCategory()) {
            case 0:
                return R.string.cmas_category_geo;
            case 1:
                return R.string.cmas_category_met;
            case 2:
                return R.string.cmas_category_safety;
            case 3:
                return R.string.cmas_category_security;
            case 4:
                return R.string.cmas_category_rescue;
            case 5:
                return R.string.cmas_category_fire;
            case 6:
                return R.string.cmas_category_health;
            case 7:
                return R.string.cmas_category_env;
            case 8:
                return R.string.cmas_category_transport;
            case 9:
                return R.string.cmas_category_infra;
            case 10:
                return R.string.cmas_category_cbrne;
            case 11:
                return R.string.cmas_category_other;
            default:
                return 0;
        }
    }

    private static int getCmasResponseResId(SmsCbCmasInfo smsCbCmasInfo) {
        switch (smsCbCmasInfo.getResponseType()) {
            case 0:
                return R.string.cmas_response_shelter;
            case 1:
                return R.string.cmas_response_evacuate;
            case 2:
                return R.string.cmas_response_prepare;
            case 3:
                return R.string.cmas_response_execute;
            case 4:
                return R.string.cmas_response_monitor;
            case 5:
                return R.string.cmas_response_avoid;
            case 6:
                return R.string.cmas_response_assess;
            case 7:
                return R.string.cmas_response_none;
            default:
                return 0;
        }
    }

    private static int getCmasSeverityResId(SmsCbCmasInfo smsCbCmasInfo) {
        int severity = smsCbCmasInfo.getSeverity();
        if (severity == 0) {
            return R.string.cmas_severity_extreme;
        }
        if (severity != 1) {
            return 0;
        }
        return R.string.cmas_severity_severe;
    }

    private static int getCmasUrgencyResId(SmsCbCmasInfo smsCbCmasInfo) {
        int urgency = smsCbCmasInfo.getUrgency();
        if (urgency == 0) {
            return R.string.cmas_urgency_immediate;
        }
        if (urgency != 1) {
            return 0;
        }
        return R.string.cmas_urgency_expected;
    }

    private static int getCmasCertaintyResId(SmsCbCmasInfo smsCbCmasInfo) {
        int certainty = smsCbCmasInfo.getCertainty();
        if (certainty == 0) {
            return R.string.cmas_certainty_observed;
        }
        if (certainty != 1) {
            return 0;
        }
        return R.string.cmas_certainty_likely;
    }

    public static String getSmsSenderAddressResourceEnglishString(Context context, SmsCbMessage smsCbMessage) {
        int smsSenderAddressResource = getSmsSenderAddressResource(context, smsCbMessage);
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(Locale.ENGLISH);
        return context.createConfigurationContext(configuration).getResources().getText(smsSenderAddressResource).toString();
    }

    public static int getSmsSenderAddressResource(Context context, SmsCbMessage smsCbMessage) {
        CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(context, smsCbMessage.getSubscriptionId());
        int serviceCategory = smsCbMessage.getServiceCategory();
        if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.cmas_presidential_alerts_channels_range_strings)) {
            return R.string.sms_cb_sender_name_presidential;
        }
        if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.emergency_alerts_channels_range_strings)) {
            return R.string.sms_cb_sender_name_emergency;
        }
        return cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.public_safety_messages_channels_range_strings) ? R.string.sms_cb_sender_name_public_safety : R.string.sms_cb_sender_name_default;
    }

    static int getDialogTitleResource(Context context, SmsCbMessage smsCbMessage) {
        SmsCbEtwsInfo etwsWarningInfo = smsCbMessage.getEtwsWarningInfo();
        if (etwsWarningInfo != null) {
            int warningType = etwsWarningInfo.getWarningType();
            if (warningType == 0) {
                return R.string.etws_earthquake_warning;
            }
            if (warningType == 1) {
                return R.string.etws_tsunami_warning;
            }
            if (warningType != 2) {
                return warningType != 3 ? R.string.etws_other_emergency_type : R.string.etws_test_message;
            }
            return R.string.etws_earthquake_and_tsunami_warning;
        }
        SmsCbCmasInfo cmasWarningInfo = smsCbMessage.getCmasWarningInfo();
        CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(context, smsCbMessage.getSubscriptionId());
        int serviceCategory = smsCbMessage.getServiceCategory();
        if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.emergency_alerts_channels_range_strings)) {
            return R.string.pws_other_message_identifiers;
        }
        if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.cmas_presidential_alerts_channels_range_strings)) {
            return R.string.cmas_presidential_level_alert;
        }
        if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.cmas_alert_extreme_channels_range_strings)) {
            if (!smsCbMessage.isCmasMessage() || cmasWarningInfo.getSeverity() != 0 || cmasWarningInfo.getUrgency() != 0) {
                return R.string.cmas_extreme_alert;
            }
            if (cmasWarningInfo.getCertainty() == 0) {
                return R.string.cmas_extreme_immediate_observed_alert;
            }
            return cmasWarningInfo.getCertainty() == 1 ? R.string.cmas_extreme_immediate_likely_alert : R.string.cmas_extreme_alert;
        } else if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.cmas_alerts_severe_range_strings)) {
            return R.string.cmas_severe_alert;
        } else {
            if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.cmas_amber_alerts_channels_range_strings)) {
                return R.string.cmas_amber_alert;
            }
            if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.required_monthly_test_range_strings)) {
                return R.string.cmas_required_monthly_test;
            }
            if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.exercise_alert_range_strings)) {
                return R.string.cmas_exercise_alert;
            }
            if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.operator_defined_alert_range_strings)) {
                return R.string.cmas_operator_defined_alert;
            }
            if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.public_safety_messages_channels_range_strings)) {
                return R.string.public_safety_message;
            }
            if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.state_local_test_alert_range_strings)) {
                return R.string.state_local_test_alert;
            }
            if (!cellBroadcastChannelManager.isEmergencyMessage(smsCbMessage)) {
                return R.string.cb_other_message_identifiers;
            }
            ArrayList<CellBroadcastChannelManager.CellBroadcastChannelRange> cellBroadcastChannelRanges = cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.additional_cbs_channels_strings);
            if (cellBroadcastChannelRanges != null) {
                Iterator<CellBroadcastChannelManager.CellBroadcastChannelRange> it = cellBroadcastChannelRanges.iterator();
                while (it.hasNext()) {
                    CellBroadcastChannelManager.CellBroadcastChannelRange next = it.next();
                    if (serviceCategory >= next.mStartId && serviceCategory <= next.mEndId) {
                        switch (C02931.f15x97d16d5c[next.mAlertType.ordinal()]) {
                            case 1:
                                break;
                            case 2:
                                return R.string.etws_earthquake_warning;
                            case 3:
                                return R.string.etws_tsunami_warning;
                            case 4:
                                return R.string.etws_test_message;
                            case 5:
                            case 6:
                                return R.string.etws_other_emergency_type;
                        }
                    }
                }
            }
            return R.string.pws_other_message_identifiers;
        }
    }

    /* renamed from: com.android.cellbroadcastreceiver.CellBroadcastResources$1 */
    static /* synthetic */ class C02931 {

        /* renamed from: $SwitchMap$com$android$cellbroadcastreceiver$CellBroadcastAlertService$AlertType */
        static final /* synthetic */ int[] f15x97d16d5c;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|14) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType[] r0 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f15x97d16d5c = r0
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.DEFAULT     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f15x97d16d5c     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.ETWS_EARTHQUAKE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f15x97d16d5c     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.ETWS_TSUNAMI     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f15x97d16d5c     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.TEST     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = f15x97d16d5c     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.ETWS_DEFAULT     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = f15x97d16d5c     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r1 = com.android.cellbroadcastreceiver.CellBroadcastAlertService.AlertType.OTHER     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastreceiver.CellBroadcastResources.C02931.<clinit>():void");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0016, code lost:
        if (r0 != 2) goto L_0x001b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static int getDialogPictogramResource(android.content.Context r6, android.telephony.SmsCbMessage r7) {
        /*
            android.telephony.SmsCbEtwsInfo r0 = r7.getEtwsWarningInfo()
            r1 = 2131165288(0x7f070068, float:1.7944789E38)
            r2 = 2131165287(0x7f070067, float:1.7944787E38)
            r3 = 2
            if (r0 == 0) goto L_0x001b
            int r0 = r0.getWarningType()
            if (r0 == 0) goto L_0x001a
            r4 = 1
            if (r0 == r4) goto L_0x0019
            if (r0 == r3) goto L_0x001a
            goto L_0x001b
        L_0x0019:
            return r1
        L_0x001a:
            return r2
        L_0x001b:
            int r0 = r7.getServiceCategory()
            int r4 = r7.getSubscriptionId()
            com.android.cellbroadcastreceiver.CellBroadcastChannelManager r5 = new com.android.cellbroadcastreceiver.CellBroadcastChannelManager
            r5.<init>(r6, r4)
            boolean r6 = r5.isEmergencyMessage(r7)
            r7 = -1
            if (r6 == 0) goto L_0x005f
            r6 = 2130837504(0x7f020000, float:1.7279964E38)
            java.util.ArrayList r6 = r5.getCellBroadcastChannelRanges(r6)
            java.util.Iterator r6 = r6.iterator()
        L_0x0039:
            boolean r4 = r6.hasNext()
            if (r4 == 0) goto L_0x005f
            java.lang.Object r4 = r6.next()
            com.android.cellbroadcastreceiver.CellBroadcastChannelManager$CellBroadcastChannelRange r4 = (com.android.cellbroadcastreceiver.CellBroadcastChannelManager.CellBroadcastChannelRange) r4
            int r5 = r4.mStartId
            if (r0 < r5) goto L_0x0039
            int r5 = r4.mEndId
            if (r0 > r5) goto L_0x0039
            int[] r5 = com.android.cellbroadcastreceiver.CellBroadcastResources.C02931.f15x97d16d5c
            com.android.cellbroadcastreceiver.CellBroadcastAlertService$AlertType r4 = r4.mAlertType
            int r4 = r4.ordinal()
            r4 = r5[r4]
            if (r4 == r3) goto L_0x005e
            r5 = 3
            if (r4 == r5) goto L_0x005d
            goto L_0x0039
        L_0x005d:
            return r1
        L_0x005e:
            return r2
        L_0x005f:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastreceiver.CellBroadcastResources.getDialogPictogramResource(android.content.Context, android.telephony.SmsCbMessage):int");
    }
}

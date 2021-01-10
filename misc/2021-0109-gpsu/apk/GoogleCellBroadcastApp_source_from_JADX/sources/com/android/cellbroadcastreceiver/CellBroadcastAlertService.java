package com.android.cellbroadcastreceiver;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneStateListener;
import android.telephony.SmsCbEtwsInfo;
import android.telephony.SmsCbMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.cellbroadcastreceiver.CellBroadcastChannelManager;
import com.android.cellbroadcastreceiver.CellBroadcastContentProvider;
import com.android.cellbroadcastreceiver.module.R;
import com.android.cellbroadcastservice.CellBroadcastStatsLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.Executor;

public class CellBroadcastAlertService extends Service {
    public static final int NOTIFICATION_ID = 1;
    public static final String SHOW_NEW_ALERT_ACTION = "cellbroadcastreceiver.SHOW_NEW_ALERT";
    private static boolean sRemindAfterCallFinish = false;
    private Context mContext;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener(new Executor(new Handler(Looper.getMainLooper())) {
        public final /* synthetic */ Handler f$0;

        {
            this.f$0 = r1;
        }

        public final void execute(Runnable runnable) {
            this.f$0.post(runnable);
        }
    }) {
        public void onCallStateChanged(int i, String str) {
            if (i != 0) {
                Log.d("CBAlertService", "onCallStateChanged: other state = " + i);
                return;
            }
            Log.d("CBAlertService", "onCallStateChanged: CALL_STATE_IDLE");
            CellBroadcastAlertService.this.playPendingAlert();
        }
    };
    private TelephonyManager mTelephonyManager;

    public enum AlertType {
        DEFAULT,
        ETWS_DEFAULT,
        ETWS_EARTHQUAKE,
        ETWS_TSUNAMI,
        TEST,
        AREA,
        INFO,
        OTHER
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        this.mContext = getApplicationContext();
        String action = intent.getAction();
        Log.d("CBAlertService", "onStartCommand: " + action);
        if ("android.provider.action.SMS_EMERGENCY_CB_RECEIVED".equals(action) || "android.provider.Telephony.SMS_CB_RECEIVED".equals(action)) {
            handleCellBroadcastIntent(intent);
            return 2;
        } else if (SHOW_NEW_ALERT_ACTION.equals(action)) {
            int myUserId = UserHandle.myUserId();
            ActivityManager activityManager = (ActivityManager) getSystemService("activity");
            if (myUserId == ActivityManager.getCurrentUser()) {
                showNewAlert(intent);
                return 2;
            }
            Log.d("CBAlertService", "Not active user, ignore the alert display");
            return 2;
        } else {
            Log.e("CBAlertService", "Unrecognized intent action: " + action);
            return 2;
        }
    }

    public void onCreate() {
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService("phone");
        this.mTelephonyManager = telephonyManager;
        telephonyManager.listen(this.mPhoneStateListener, 32);
    }

    public void onDestroy() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }

    public boolean shouldDisplayMessage(SmsCbMessage smsCbMessage) {
        if (((TelephonyManager) this.mContext.getSystemService("phone")).createForSubscriptionId(smsCbMessage.getSubscriptionId()).getEmergencyCallbackMode() && CellBroadcastSettings.getResources(this.mContext, smsCbMessage.getSubscriptionId()).getBoolean(R.bool.ignore_messages_in_ecbm)) {
            Log.d("CBAlertService", "ignoring alert of type " + smsCbMessage.getServiceCategory() + " in ECBM");
            return false;
        } else if (!isChannelEnabled(smsCbMessage)) {
            Log.d("CBAlertService", "ignoring alert of type " + smsCbMessage.getServiceCategory() + " by user preference");
            return false;
        } else {
            String messageBody = smsCbMessage.getMessageBody();
            if (messageBody == null || messageBody.length() == 0) {
                Log.e("CBAlertService", "Empty content or Unsupported charset");
                return false;
            }
            CellBroadcastChannelManager.CellBroadcastChannelRange cellBroadcastChannelRangeFromMessage = new CellBroadcastChannelManager(this.mContext, smsCbMessage.getSubscriptionId()).getCellBroadcastChannelRangeFromMessage(smsCbMessage);
            String languageCode = smsCbMessage.getLanguageCode();
            if (cellBroadcastChannelRangeFromMessage != null && cellBroadcastChannelRangeFromMessage.mFilterLanguage) {
                String string = CellBroadcastSettings.getResources(this.mContext, smsCbMessage.getSubscriptionId()).getString(R.string.emergency_alert_second_language_code);
                if (!string.isEmpty()) {
                    boolean z = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("receive_cmas_in_second_language", false);
                    if (!TextUtils.isEmpty(languageCode) && !string.equalsIgnoreCase(languageCode)) {
                        Log.w("CBAlertService", "Ignoring message in the unspecified second language:" + languageCode);
                        return false;
                    } else if (!z) {
                        Log.d("CBAlertService", "Ignoring message in second language because setting is off");
                        return false;
                    }
                } else {
                    String language = Locale.getDefault().getLanguage();
                    if (!TextUtils.isEmpty(languageCode) && !languageCode.equalsIgnoreCase(language)) {
                        Log.d("CBAlertService", "ignoring the alert due to language mismatch. Message lang=" + languageCode + ", device lang=" + language);
                        return false;
                    }
                }
            }
            if (cellBroadcastChannelRangeFromMessage == null || !cellBroadcastChannelRangeFromMessage.mTestMode || CellBroadcastReceiver.isTestingMode(this.mContext)) {
                String str = SystemProperties.get("persist.cellbroadcast.message_filter", "");
                if (TextUtils.isEmpty(str)) {
                    return true;
                }
                String[] split = str.split(",");
                int length = split.length;
                int i = 0;
                while (i < length) {
                    String str2 = split[i];
                    if (TextUtils.isEmpty(str2) || !smsCbMessage.getMessageBody().toLowerCase().contains(str2)) {
                        i++;
                    } else {
                        Log.i("CBAlertService", "Skipped message due to filter: " + str2);
                        return false;
                    }
                }
                return true;
            }
            Log.d("CBAlertService", "ignoring the alert due to not in testing mode");
            return false;
        }
    }

    private void handleCellBroadcastIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.e("CBAlertService", "received SMS_CB_RECEIVED_ACTION with no extras!");
            return;
        }
        SmsCbMessage smsCbMessage = (SmsCbMessage) extras.get("message");
        if (smsCbMessage == null) {
            Log.e("CBAlertService", "received SMS_CB_RECEIVED_ACTION with no message extra");
            return;
        }
        if (smsCbMessage.getMessageFormat() == 1) {
            CellBroadcastStatsLog.write(249, 1, 3);
        } else if (smsCbMessage.getMessageFormat() == 2) {
            CellBroadcastStatsLog.write(249, 2, 3);
        }
        if (shouldDisplayMessage(smsCbMessage)) {
            Intent intent2 = new Intent(SHOW_NEW_ALERT_ACTION);
            intent2.setClass(this, CellBroadcastAlertService.class);
            intent2.putExtra("message", smsCbMessage);
            new CellBroadcastContentProvider.AsyncCellBroadcastTask(getContentResolver()).execute(new CellBroadcastContentProvider.CellBroadcastOperation[]{new CellBroadcastContentProvider.CellBroadcastOperation(smsCbMessage, intent2) {
                public final /* synthetic */ SmsCbMessage f$1;
                public final /* synthetic */ Intent f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final boolean execute(CellBroadcastContentProvider cellBroadcastContentProvider) {
                    return CellBroadcastAlertService.this.lambda$handleCellBroadcastIntent$0$CellBroadcastAlertService(this.f$1, this.f$2, cellBroadcastContentProvider);
                }
            }});
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleCellBroadcastIntent$0 */
    public /* synthetic */ boolean lambda$handleCellBroadcastIntent$0$CellBroadcastAlertService(SmsCbMessage smsCbMessage, Intent intent, CellBroadcastContentProvider cellBroadcastContentProvider) {
        CellBroadcastChannelManager.CellBroadcastChannelRange cellBroadcastChannelRangeFromMessage = new CellBroadcastChannelManager(this.mContext, smsCbMessage.getSubscriptionId()).getCellBroadcastChannelRangeFromMessage(smsCbMessage);
        boolean z = true;
        if (cellBroadcastChannelRangeFromMessage == null || !cellBroadcastChannelRangeFromMessage.mDisplay) {
            Log.d("CBAlertService", "ignoring the alert due to configured channels was marked as do not display");
        } else if (cellBroadcastContentProvider.insertNewBroadcast(smsCbMessage)) {
            startService(intent);
            markMessageDisplayed(smsCbMessage);
            if (CellBroadcastSettings.getResources(this.mContext, smsCbMessage.getSubscriptionId()).getBoolean(R.bool.enable_write_alerts_to_sms_inbox) && (CellBroadcastReceiver.isTestingMode(getApplicationContext()) || (cellBroadcastChannelRangeFromMessage != null && cellBroadcastChannelRangeFromMessage.mWriteToSmsInbox))) {
                cellBroadcastContentProvider.writeMessageToSmsInbox(smsCbMessage, this.mContext);
            }
            return z;
        }
        z = false;
        cellBroadcastContentProvider.writeMessageToSmsInbox(smsCbMessage, this.mContext);
        return z;
    }

    private void markMessageDisplayed(SmsCbMessage smsCbMessage) {
        this.mContext.getContentResolver().update(Uri.withAppendedPath(Telephony.CellBroadcasts.CONTENT_URI, "displayed"), new ContentValues(), "received_time=?", new String[]{Long.toString(smsCbMessage.getReceivedTime())});
    }

    private void showNewAlert(Intent intent) {
        if (intent.getExtras() == null) {
            Log.e("CBAlertService", "received SHOW_NEW_ALERT_ACTION with no extras!");
            return;
        }
        SmsCbMessage parcelableExtra = intent.getParcelableExtra("message");
        if (parcelableExtra == null) {
            Log.e("CBAlertService", "received SHOW_NEW_ALERT_ACTION with no message extra");
            return;
        }
        if (this.mTelephonyManager.getCallState() != 0 && CellBroadcastSettings.getResources(this.mContext, parcelableExtra.getSubscriptionId()).getBoolean(R.bool.enable_alert_handling_during_call)) {
            Log.d("CBAlertService", "CMAS received in dialing/during voicecall.");
            sRemindAfterCallFinish = true;
        }
        if (!new CellBroadcastChannelManager(this.mContext, parcelableExtra.getSubscriptionId()).isEmergencyMessage(parcelableExtra) || sRemindAfterCallFinish) {
            addToNotificationBar(parcelableExtra, CellBroadcastReceiverApp.addNewMessageToList(parcelableExtra), this, false, true, false);
            return;
        }
        openEmergencyAlertNotification(parcelableExtra);
        if (CellBroadcastSettings.getResources(this.mContext, parcelableExtra.getSubscriptionId()).getBoolean(R.bool.show_alert_dialog_with_notification)) {
            addToNotificationBar(parcelableExtra, CellBroadcastReceiverApp.addNewMessageToList(parcelableExtra), this, false, true, true);
        }
    }

    private boolean isChannelEnabled(SmsCbMessage smsCbMessage) {
        CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(this.mContext, smsCbMessage.getSubscriptionId());
        CellBroadcastChannelManager.CellBroadcastChannelRange cellBroadcastChannelRangeFromMessage = cellBroadcastChannelManager.getCellBroadcastChannelRangeFromMessage(smsCbMessage);
        Resources resources = CellBroadcastSettings.getResources(this.mContext, smsCbMessage.getSubscriptionId());
        if (cellBroadcastChannelRangeFromMessage == null || !cellBroadcastChannelRangeFromMessage.mAlwaysOn) {
            boolean z = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_alerts_master_toggle", true);
            SmsCbEtwsInfo etwsWarningInfo = smsCbMessage.getEtwsWarningInfo();
            if (etwsWarningInfo == null || etwsWarningInfo.getWarningType() != 3) {
                int serviceCategory = smsCbMessage.getServiceCategory();
                if (smsCbMessage.isEtwsMessage() || cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.etws_alerts_range_strings)) {
                    return z;
                }
                Iterator<CellBroadcastChannelManager.CellBroadcastChannelRange> it = cellBroadcastChannelManager.getCellBroadcastChannelRanges(R.array.additional_cbs_channels_strings).iterator();
                while (it.hasNext()) {
                    CellBroadcastChannelManager.CellBroadcastChannelRange next = it.next();
                    if (next.mStartId <= serviceCategory && next.mEndId >= serviceCategory) {
                        if (!cellBroadcastChannelManager.checkScope(next.mScope)) {
                            Log.d("CBAlertService", "The range [" + next.mStartId + "-" + next.mEndId + "] is not within the scope. mScope = " + next.mScope);
                            return false;
                        }
                        AlertType alertType = next.mAlertType;
                        if (alertType == AlertType.TEST) {
                            if (!z || !CellBroadcastSettings.isTestAlertsToggleVisible(getApplicationContext()) || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_test_alerts", false)) {
                                return false;
                            }
                            return true;
                        } else if (alertType != AlertType.AREA) {
                            return z;
                        } else {
                            if (!z || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_area_update_info_alerts", false)) {
                                return false;
                            }
                            return true;
                        }
                    }
                }
                if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.emergency_alerts_channels_range_strings)) {
                    if (!z || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_emergency_alerts", true)) {
                        return false;
                    }
                    return true;
                } else if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.cmas_presidential_alerts_channels_range_strings)) {
                    return true;
                } else {
                    if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.cmas_alert_extreme_channels_range_strings)) {
                        if (!z || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_cmas_extreme_threat_alerts", true)) {
                            return false;
                        }
                        return true;
                    } else if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.cmas_alerts_severe_range_strings)) {
                        if (!z || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_cmas_severe_threat_alerts", true)) {
                            return false;
                        }
                        return true;
                    } else if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.cmas_amber_alerts_channels_range_strings)) {
                        if (!z || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_cmas_amber_alerts", true)) {
                            return false;
                        }
                        return true;
                    } else if (!cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.exercise_alert_range_strings) || !resources.getBoolean(R.bool.show_separate_exercise_settings)) {
                        if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.required_monthly_test_range_strings) || cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.exercise_alert_range_strings) || cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.operator_defined_alert_range_strings)) {
                            if (!z || !CellBroadcastSettings.isTestAlertsToggleVisible(getApplicationContext()) || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_test_alerts", false)) {
                                return false;
                            }
                            return true;
                        } else if (cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.public_safety_messages_channels_range_strings)) {
                            if (!z || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_public_safety_messages", true)) {
                                return false;
                            }
                            return true;
                        } else if (!cellBroadcastChannelManager.checkCellBroadcastChannelRange(serviceCategory, R.array.state_local_test_alert_range_strings)) {
                            Log.e("CBAlertService", "received undefined channels: " + serviceCategory);
                            return false;
                        } else if (!z || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_state_local_test_alerts", false)) {
                            return false;
                        } else {
                            return true;
                        }
                    } else if (!z || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_exercise_alerts", false)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            } else if (!z || !CellBroadcastSettings.isTestAlertsToggleVisible(getApplicationContext()) || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("enable_test_alerts", false)) {
                return false;
            } else {
                return true;
            }
        } else {
            Log.d("CBAlertService", "channel is enabled due to always-on, ignoring preference check");
            return true;
        }
    }

    private void openEmergencyAlertNotification(SmsCbMessage smsCbMessage) {
        int[] iArr;
        sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        Intent intent = new Intent(this, CellBroadcastAlertAudio.class);
        intent.setAction(CellBroadcastAlertAudio.ACTION_START_ALERT_AUDIO);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(this.mContext, smsCbMessage.getSubscriptionId());
        AlertType alertType = AlertType.DEFAULT;
        if (!smsCbMessage.isEtwsMessage()) {
            int serviceCategory = smsCbMessage.getServiceCategory();
            Iterator<CellBroadcastChannelManager.CellBroadcastChannelRange> it = cellBroadcastChannelManager.getAllCellBroadcastChannelRanges().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                CellBroadcastChannelManager.CellBroadcastChannelRange next = it.next();
                if (serviceCategory >= next.mStartId && serviceCategory <= next.mEndId) {
                    alertType = next.mAlertType;
                    break;
                }
            }
        } else {
            alertType = AlertType.ETWS_DEFAULT;
            if (smsCbMessage.getEtwsWarningInfo() != null) {
                int warningType = smsCbMessage.getEtwsWarningInfo().getWarningType();
                if (warningType != 0) {
                    if (warningType == 1) {
                        alertType = AlertType.ETWS_TSUNAMI;
                    } else if (warningType != 2) {
                        if (warningType == 3) {
                            alertType = AlertType.TEST;
                        } else if (warningType == 4) {
                            alertType = AlertType.OTHER;
                        }
                    }
                }
                alertType = AlertType.ETWS_EARTHQUAKE;
            }
        }
        CellBroadcastChannelManager.CellBroadcastChannelRange cellBroadcastChannelRangeFromMessage = cellBroadcastChannelManager.getCellBroadcastChannelRangeFromMessage(smsCbMessage);
        intent.putExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_TONE_TYPE", alertType);
        if (cellBroadcastChannelRangeFromMessage != null) {
            iArr = cellBroadcastChannelRangeFromMessage.mVibrationPattern;
        } else {
            iArr = CellBroadcastSettings.getResources(this.mContext, smsCbMessage.getSubscriptionId()).getIntArray(R.array.default_vibration_pattern);
        }
        intent.putExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_VIBRATION_PATTERN", iArr);
        Resources resources = CellBroadcastSettings.getResources(this.mContext, smsCbMessage.getSubscriptionId());
        if ((resources.getBoolean(R.bool.show_override_dnd_settings) && defaultSharedPreferences.getBoolean("override_dnd", false)) || ((cellBroadcastChannelRangeFromMessage != null && cellBroadcastChannelRangeFromMessage.mOverrideDnd) || resources.getBoolean(R.bool.override_dnd))) {
            intent.putExtra("com.android.cellbroadcastreceiver.ALERT_OVERRIDE_DND_EXTRA", true);
        }
        String messageBody = smsCbMessage.getMessageBody();
        if (!CellBroadcastSettings.getResourcesForDefaultSubId(this.mContext).getBoolean(R.bool.show_alert_speech_setting) || defaultSharedPreferences.getBoolean("enable_alert_speech", true)) {
            intent.putExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_MESSAGE_BODY", messageBody);
            String languageCode = smsCbMessage.getLanguageCode();
            Log.d("CBAlertService", "Message language = " + languageCode);
            intent.putExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_MESSAGE_LANGUAGE", languageCode);
        }
        intent.putExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_SUB_INDEX", smsCbMessage.getSubscriptionId());
        intent.putExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_DURATION", cellBroadcastChannelRangeFromMessage != null ? cellBroadcastChannelRangeFromMessage.mAlertDuration : -1);
        startService(intent);
        ArrayList arrayList = new ArrayList();
        arrayList.add(smsCbMessage);
        if (getPackageManager().hasSystemFeature("android.hardware.type.watch")) {
            addToNotificationBar(smsCbMessage, arrayList, this, false, true, false);
            return;
        }
        Intent createDisplayMessageIntent = createDisplayMessageIntent(this, CellBroadcastAlertDialog.class, arrayList);
        createDisplayMessageIntent.addFlags(268435456);
        startActivity(createDisplayMessageIntent);
    }

    static void addToNotificationBar(SmsCbMessage smsCbMessage, ArrayList<SmsCbMessage> arrayList, Context context, boolean z, boolean z2, boolean z3) {
        Intent intent;
        PendingIntent pendingIntent;
        SmsCbMessage smsCbMessage2 = smsCbMessage;
        Context context2 = context;
        Resources resources = CellBroadcastSettings.getResources(context2, smsCbMessage.getSubscriptionId());
        CharSequence text = context2.getText(CellBroadcastResources.getDialogTitleResource(context2, smsCbMessage2));
        String messageBody = smsCbMessage.getMessageBody();
        NotificationManager notificationManager = (NotificationManager) context2.getSystemService("notification");
        createNotificationChannels(context);
        boolean hasSystemFeature = context.getPackageManager().hasSystemFeature("android.hardware.type.watch");
        if (hasSystemFeature) {
            intent = createMarkAsReadIntent(context2, smsCbMessage.getReceivedTime());
            ArrayList<SmsCbMessage> arrayList2 = arrayList;
        } else {
            intent = createDisplayMessageIntent(context2, CellBroadcastAlertDialog.class, arrayList);
        }
        intent.putExtra(CellBroadcastAlertDialog.FROM_NOTIFICATION_EXTRA, true);
        intent.putExtra("from_save_state_notification", z);
        if (hasSystemFeature) {
            pendingIntent = PendingIntent.getBroadcast(context2, 0, intent, 0);
        } else {
            pendingIntent = PendingIntent.getActivity(context2, 1, intent, 1207959552);
        }
        CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(context2, smsCbMessage.getSubscriptionId());
        String str = cellBroadcastChannelManager.isEmergencyMessage(smsCbMessage2) ? "broadcastMessages" : "broadcastMessagesNonEmergency";
        if (str == "broadcastMessages" && sRemindAfterCallFinish) {
            str = "broadcastMessagesInVoiceCall";
        }
        PendingIntent pendingIntent2 = pendingIntent;
        Notification.Builder onlyAlertOnce = new Notification.Builder(context2, str).setSmallIcon(R.drawable.ic_warning_googred).setTicker(text).setWhen(System.currentTimeMillis()).setCategory("sys").setPriority(1).setColor(resources.getColor(R.color.notification_color)).setVisibility(1).setOngoing((smsCbMessage.isEmergencyMessage() && CellBroadcastSettings.getResources(context2, smsCbMessage.getSubscriptionId()).getBoolean(R.bool.non_swipeable_notification)) || sRemindAfterCallFinish).setOnlyAlertOnce(!z2);
        if (hasSystemFeature) {
            onlyAlertOnce.setDeleteIntent(pendingIntent2);
            onlyAlertOnce.setVibrate(new long[]{0});
        } else {
            PendingIntent pendingIntent3 = pendingIntent2;
            if (z3) {
                Intent intent2 = new Intent(intent);
                intent2.putExtra("com.android.cellbroadcastreceiver.DIMISS_DIALOG", true);
                onlyAlertOnce.setDeleteIntent(PendingIntent.getActivity(context2, 1, intent2, 1207959552));
            } else {
                onlyAlertOnce.setContentIntent(pendingIntent3);
            }
            onlyAlertOnce.setDefaults(-1);
        }
        int size = arrayList.size();
        if (size > 1) {
            onlyAlertOnce.setContentTitle(context2.getString(R.string.notification_multiple_title));
            onlyAlertOnce.setContentText(context2.getString(R.string.notification_multiple, new Object[]{Integer.valueOf(size)}));
        } else {
            onlyAlertOnce.setContentTitle(text).setContentText(messageBody).setStyle(new Notification.BigTextStyle().bigText(messageBody));
        }
        notificationManager.notify(1, onlyAlertOnce.build());
        if (hasSystemFeature && !cellBroadcastChannelManager.isEmergencyMessage(smsCbMessage2) && resources.getBoolean(R.bool.watch_enable_non_emergency_audio)) {
            Intent intent3 = new Intent(context2, CellBroadcastAlertAudio.class);
            intent3.setAction(CellBroadcastAlertAudio.ACTION_START_ALERT_AUDIO);
            intent3.putExtra("com.android.cellbroadcastreceiver.ALERT_AUDIO_TONE_TYPE", AlertType.OTHER);
            context2.startService(intent3);
        }
    }

    static void createNotificationChannels(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        notificationManager.createNotificationChannel(new NotificationChannel("broadcastMessages", context.getString(R.string.notification_channel_emergency_alerts), 2));
        NotificationChannel notificationChannel = new NotificationChannel("broadcastMessagesNonEmergency", context.getString(R.string.notification_channel_broadcast_messages), 3);
        notificationChannel.enableVibration(true);
        notificationManager.createNotificationChannel(notificationChannel);
        NotificationChannel notificationChannel2 = new NotificationChannel("broadcastMessagesInVoiceCall", context.getString(R.string.notification_channel_broadcast_messages_in_voicecall), 4);
        notificationChannel2.enableVibration(true);
        notificationManager.createNotificationChannel(notificationChannel2);
    }

    private static Intent createDisplayMessageIntent(Context context, Class cls, ArrayList<SmsCbMessage> arrayList) {
        Intent intent = new Intent(context, cls);
        intent.putParcelableArrayListExtra("com.android.cellbroadcastreceiver.SMS_CB_MESSAGE", arrayList);
        return intent;
    }

    static Intent createMarkAsReadIntent(Context context, long j) {
        Intent intent = new Intent(context, CellBroadcastInternalReceiver.class);
        intent.setAction("com.android.cellbroadcastreceiver.intent.action.MARK_AS_READ");
        intent.putExtra("com.android.cellbroadcastreceiver.intent.extra.ID", j);
        return intent;
    }

    public IBinder onBind(Intent intent) {
        return new LocalBinder(this);
    }

    class LocalBinder extends Binder {
        LocalBinder(CellBroadcastAlertService cellBroadcastAlertService) {
        }
    }

    /* access modifiers changed from: private */
    public void playPendingAlert() {
        if (sRemindAfterCallFinish) {
            sRemindAfterCallFinish = false;
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService("notification");
            StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
            if (activeNotifications != null && activeNotifications.length > 0) {
                notificationManager.cancel(1);
                ArrayList<SmsCbMessage> newMessageList = CellBroadcastReceiverApp.getNewMessageList();
                for (int i = 0; i < newMessageList.size(); i++) {
                    openEmergencyAlertNotification(newMessageList.get(i));
                }
            }
            CellBroadcastReceiverApp.clearNewMessageList();
        }
    }
}

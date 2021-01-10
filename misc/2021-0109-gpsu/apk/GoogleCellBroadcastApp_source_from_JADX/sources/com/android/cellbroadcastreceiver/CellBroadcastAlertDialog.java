package com.android.cellbroadcastreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.StatusBarManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.telephony.SmsCbMessage;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.textclassifier.TextClassifier;
import android.view.textclassifier.TextLinks;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.cellbroadcastreceiver.CellBroadcastChannelManager;
import com.android.cellbroadcastreceiver.CellBroadcastContentProvider;
import com.android.cellbroadcastreceiver.module.R;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

public class CellBroadcastAlertDialog extends Activity {
    public static final String FROM_NOTIFICATION_EXTRA = "from_notification";
    public AnimationHandler mAnimationHandler = new AnimationHandler();
    protected ArrayList<SmsCbMessage> mMessageList;
    private AlertDialog mOptOutDialog;
    private final ScreenOffHandler mScreenOffHandler = new ScreenOffHandler();
    private boolean mShowOptOutDialog;

    public void onBackPressed() {
    }

    public class AnimationHandler extends Handler {
        public final AtomicInteger mCount = new AtomicInteger();
        private Drawable mWarningIcon;
        private ImageView mWarningIconView;
        public boolean mWarningIconVisible;

        AnimationHandler() {
        }

        public void startIconAnimation(int i) {
            if (initDrawableAndImageView(i)) {
                this.mWarningIconVisible = true;
                this.mWarningIconView.setVisibility(0);
                updateIconState();
                queueAnimateMessage();
            }
        }

        public void stopIconAnimation() {
            this.mCount.incrementAndGet();
            ImageView imageView = this.mWarningIconView;
            if (imageView != null) {
                imageView.setVisibility(8);
            }
        }

        private void updateIconState() {
            this.mWarningIconView.setImageAlpha(this.mWarningIconVisible ? 255 : 0);
            this.mWarningIconView.invalidateDrawable(this.mWarningIcon);
        }

        private void queueAnimateMessage() {
            int incrementAndGet = this.mCount.incrementAndGet();
            boolean z = this.mWarningIconVisible;
            sendEmptyMessageDelayed(incrementAndGet, 800);
        }

        public void handleMessage(Message message) {
            if (message.what == this.mCount.get()) {
                this.mWarningIconVisible = !this.mWarningIconVisible;
                updateIconState();
                queueAnimateMessage();
            }
        }

        private boolean initDrawableAndImageView(int i) {
            if (this.mWarningIcon == null) {
                try {
                    this.mWarningIcon = CellBroadcastSettings.getResources(CellBroadcastAlertDialog.this.getApplicationContext(), i).getDrawable(R.drawable.ic_warning_googred);
                } catch (Resources.NotFoundException e) {
                    Log.e("CellBroadcastAlertDialog", "warning icon resource not found", e);
                    return false;
                }
            }
            if (this.mWarningIconView != null) {
                return true;
            }
            ImageView imageView = (ImageView) CellBroadcastAlertDialog.this.findViewById(R.id.icon);
            this.mWarningIconView = imageView;
            if (imageView != null) {
                imageView.setImageDrawable(this.mWarningIcon);
                return true;
            }
            Log.e("CellBroadcastAlertDialog", "failed to get ImageView for warning icon");
            return false;
        }
    }

    private class ScreenOffHandler extends Handler {
        private final AtomicInteger mCount = new AtomicInteger();

        ScreenOffHandler() {
        }

        /* access modifiers changed from: package-private */
        public void startScreenOnTimer(SmsCbMessage smsCbMessage) {
            CellBroadcastChannelManager.CellBroadcastChannelRange cellBroadcastChannelRangeFromMessage = new CellBroadcastChannelManager(CellBroadcastAlertDialog.this.getApplicationContext(), smsCbMessage.getSubscriptionId()).getCellBroadcastChannelRangeFromMessage(smsCbMessage);
            int i = cellBroadcastChannelRangeFromMessage != null ? cellBroadcastChannelRangeFromMessage.mScreenOnDuration : 60000;
            if (i == 0) {
                Log.d("CellBroadcastAlertDialog", "screenOnDuration set to 0, do not turn screen on");
                return;
            }
            addWindowFlags();
            int incrementAndGet = this.mCount.incrementAndGet();
            removeMessages(incrementAndGet - 1);
            sendEmptyMessageDelayed(incrementAndGet, (long) i);
            Log.d("CellBroadcastAlertDialog", "added FLAG_KEEP_SCREEN_ON, queued screen off message id " + incrementAndGet);
        }

        /* access modifiers changed from: package-private */
        public void stopScreenOnTimer() {
            removeMessages(this.mCount.get());
            clearWindowFlags();
        }

        private void addWindowFlags() {
            CellBroadcastAlertDialog.this.getWindow().addFlags(2097280);
        }

        private void clearWindowFlags() {
            CellBroadcastAlertDialog.this.getWindow().clearFlags(128);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == this.mCount.get()) {
                clearWindowFlags();
                Log.d("CellBroadcastAlertDialog", "removed FLAG_KEEP_SCREEN_ON with id " + i);
                return;
            }
            Log.e("CellBroadcastAlertDialog", "discarding screen off message with id " + i);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        TextView textView;
        super.onCreate(bundle);
        boolean z = false;
        if (getIntent().getBooleanExtra("com.android.cellbroadcastreceiver.DIMISS_DIALOG", false)) {
            dismissAllFromNotification(getIntent());
            return;
        }
        Window window = getWindow();
        window.requestFeature(1);
        window.addFlags(4719616);
        setContentView(LayoutInflater.from(this).inflate(R.layout.cell_broadcast_alert, (ViewGroup) null));
        findViewById(R.id.dismissButton).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CellBroadcastAlertDialog.this.lambda$onCreate$0$CellBroadcastAlertDialog(view);
            }
        });
        if (bundle != null) {
            Log.d("CellBroadcastAlertDialog", "onCreate getting message list from saved instance state");
            this.mMessageList = bundle.getParcelableArrayList("com.android.cellbroadcastreceiver.SMS_CB_MESSAGE");
        } else {
            Log.d("CellBroadcastAlertDialog", "onCreate getting message list from intent");
            Intent intent = getIntent();
            this.mMessageList = intent.getParcelableArrayListExtra("com.android.cellbroadcastreceiver.SMS_CB_MESSAGE");
            clearNotification(intent);
        }
        ArrayList<SmsCbMessage> arrayList = this.mMessageList;
        if (arrayList == null || arrayList.size() == 0) {
            Log.e("CellBroadcastAlertDialog", "onCreate failed as message list is null or empty");
            finish();
            return;
        }
        Log.d("CellBroadcastAlertDialog", "onCreate loaded message list of size " + this.mMessageList.size());
        SmsCbMessage latestMessage = getLatestMessage();
        if (latestMessage == null) {
            Log.e("CellBroadcastAlertDialog", "message is null");
            finish();
            return;
        }
        CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(this, latestMessage.getSubscriptionId());
        if (cellBroadcastChannelManager.isEmergencyMessage(latestMessage)) {
            Log.d("CellBroadcastAlertDialog", "onCreate setting screen on timer for emergency alert for sub " + latestMessage.getSubscriptionId());
            this.mScreenOffHandler.startScreenOnTimer(latestMessage);
        }
        CellBroadcastChannelManager.CellBroadcastChannelRange cellBroadcastChannelRangeFromMessage = cellBroadcastChannelManager.getCellBroadcastChannelRangeFromMessage(latestMessage);
        if (cellBroadcastChannelRangeFromMessage != null && cellBroadcastChannelRangeFromMessage.mDismissOnOutsideTouch) {
            z = true;
        }
        setFinishOnTouchOutside(z);
        updateAlertText(latestMessage);
        if (CellBroadcastSettings.getResources(getApplicationContext(), latestMessage.getSubscriptionId()).getBoolean(R.bool.enable_text_copy) && (textView = (TextView) findViewById(R.id.message)) != null) {
            textView.setOnLongClickListener(new View.OnLongClickListener(latestMessage) {
                public final /* synthetic */ SmsCbMessage f$1;

                {
                    this.f$1 = r2;
                }

                public final boolean onLongClick(View view) {
                    return CellBroadcastAlertDialog.this.lambda$onCreate$1$CellBroadcastAlertDialog(this.f$1, view);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$CellBroadcastAlertDialog(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$1 */
    public /* synthetic */ boolean lambda$onCreate$1$CellBroadcastAlertDialog(SmsCbMessage smsCbMessage, View view) {
        return copyMessageToClipboard(smsCbMessage, getApplicationContext());
    }

    public void onStart() {
        super.onStart();
        getWindow().addSystemFlags(524288);
    }

    public void onResume() {
        super.onResume();
        SmsCbMessage latestMessage = getLatestMessage();
        if (latestMessage != null) {
            int subscriptionId = latestMessage.getSubscriptionId();
            CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(this, subscriptionId);
            CellBroadcastChannelManager.CellBroadcastChannelRange cellBroadcastChannelRangeFromMessage = cellBroadcastChannelManager.getCellBroadcastChannelRangeFromMessage(latestMessage);
            if (cellBroadcastChannelManager.isEmergencyMessage(latestMessage) && cellBroadcastChannelRangeFromMessage != null && cellBroadcastChannelRangeFromMessage.mDisplayIcon) {
                this.mAnimationHandler.startIconAnimation(subscriptionId);
            }
        }
        setStatusBarDisabledIfNeeded(true);
    }

    public void onPause() {
        Log.d("CellBroadcastAlertDialog", "onPause called");
        this.mAnimationHandler.stopIconAnimation();
        setStatusBarDisabledIfNeeded(false);
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        Log.d("CellBroadcastAlertDialog", "onStop called");
        PowerManager powerManager = (PowerManager) getSystemService("power");
        if (!isChangingConfigurations() && getLatestMessage() != null && powerManager.isScreenOn()) {
            CellBroadcastAlertService.addToNotificationBar(getLatestMessage(), this.mMessageList, getApplicationContext(), true, true, false);
            stopService(new Intent(this, CellBroadcastAlertAudio.class));
        }
        super.onStop();
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            setPictogramAreaLayout(getResources().getConfiguration().orientation);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        setPictogramAreaLayout(configuration.orientation);
    }

    /* access modifiers changed from: package-private */
    public SmsCbMessage getLatestMessage() {
        int size = this.mMessageList.size() - 1;
        if (size >= 0) {
            return this.mMessageList.get(size);
        }
        Log.d("CellBroadcastAlertDialog", "getLatestMessage returns null");
        return null;
    }

    private SmsCbMessage removeLatestMessage() {
        int size = this.mMessageList.size() - 1;
        if (size >= 0) {
            return this.mMessageList.remove(size);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelableArrayList("com.android.cellbroadcastreceiver.SMS_CB_MESSAGE", this.mMessageList);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x004b A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getLinkMethod(int r5) {
        /*
            r4 = this;
            android.content.Context r4 = r4.getApplicationContext()
            android.content.res.Resources r4 = com.android.cellbroadcastreceiver.CellBroadcastSettings.getResources(r4, r5)
            r5 = 2131624064(0x7f0e0080, float:1.8875297E38)
            java.lang.String r4 = r4.getString(r5)
            int r5 = r4.hashCode()
            r0 = 3387192(0x33af38, float:4.746467E-39)
            r1 = 0
            r2 = 2
            r3 = 1
            if (r5 == r0) goto L_0x003a
            r0 = 172027468(0xa40ee4c, float:9.2892824E-33)
            if (r5 == r0) goto L_0x0030
            r0 = 1694208076(0x64fb904c, float:3.7124244E22)
            if (r5 == r0) goto L_0x0026
            goto L_0x0044
        L_0x0026:
            java.lang.String r5 = "smart_linkify"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0044
            r4 = r2
            goto L_0x0045
        L_0x0030:
            java.lang.String r5 = "legacy_linkify"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0044
            r4 = r3
            goto L_0x0045
        L_0x003a:
            java.lang.String r5 = "none"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0044
            r4 = r1
            goto L_0x0045
        L_0x0044:
            r4 = -1
        L_0x0045:
            if (r4 == r3) goto L_0x004b
            if (r4 == r2) goto L_0x004a
            return r1
        L_0x004a:
            return r2
        L_0x004b:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastreceiver.CellBroadcastAlertDialog.getLinkMethod(int):int");
    }

    private void addLinks(TextView textView, String str, int i) {
        SpannableString spannableString = new SpannableString(str);
        if (i == 1) {
            Linkify.addLinks(spannableString, 15);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(spannableString);
        } else if (i == 2) {
            new Thread(new Runnable(textView, spannableString) {
                public final /* synthetic */ TextView f$1;
                public final /* synthetic */ Spannable f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    CellBroadcastAlertDialog.this.lambda$addLinks$3$CellBroadcastAlertDialog(this.f$1, this.f$2);
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addLinks$3 */
    public /* synthetic */ void lambda$addLinks$3$CellBroadcastAlertDialog(TextView textView, Spannable spannable) {
        textView.getTextClassifier().generateLinks(new TextLinks.Request.Builder(spannable).setEntityConfig(new TextClassifier.EntityConfig.Builder().setIncludedTypes(Arrays.asList(new String[]{"url", "email", "phone", "address", "flight"})).setExcludedTypes(Arrays.asList(new String[]{"date", "datetime"})).build()).build()).apply(spannable, 1, (Function) null);
        runOnUiThread(new Runnable(textView, spannable) {
            public final /* synthetic */ TextView f$0;
            public final /* synthetic */ Spannable f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                CellBroadcastAlertDialog.lambda$addLinks$2(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$addLinks$2(TextView textView, Spannable spannable) {
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannable);
    }

    private String overrideTranslation(int i, Resources resources, String str) {
        if (TextUtils.isEmpty(str) || !resources.getBoolean(R.bool.override_alert_title_language_to_match_message_locale)) {
            return resources.getText(i).toString();
        }
        Configuration configuration = new Configuration(resources.getConfiguration());
        configuration.setLocale(new Locale(str));
        return getApplicationContext().createConfigurationContext(configuration).getResources().getText(i).toString();
    }

    private void updateAlertText(SmsCbMessage smsCbMessage) {
        String str;
        Context applicationContext = getApplicationContext();
        int dialogTitleResource = CellBroadcastResources.getDialogTitleResource(applicationContext, smsCbMessage);
        Resources resources = CellBroadcastSettings.getResources(applicationContext, smsCbMessage.getSubscriptionId());
        CellBroadcastChannelManager.CellBroadcastChannelRange cellBroadcastChannelRangeFromMessage = new CellBroadcastChannelManager(this, smsCbMessage.getSubscriptionId()).getCellBroadcastChannelRangeFromMessage(smsCbMessage);
        if (cellBroadcastChannelRangeFromMessage == null || TextUtils.isEmpty(cellBroadcastChannelRangeFromMessage.mLanguageCode)) {
            str = smsCbMessage.getLanguageCode();
        } else {
            str = cellBroadcastChannelRangeFromMessage.mLanguageCode;
        }
        String overrideTranslation = overrideTranslation(dialogTitleResource, resources, str);
        TextView textView = (TextView) findViewById(R.id.alertTitle);
        if (textView != null) {
            if (resources.getBoolean(R.bool.show_date_time_title)) {
                textView.setSingleLine(false);
                int i = 527121;
                if (resources.getBoolean(R.bool.show_date_time_with_year_title)) {
                    i = 527125;
                }
                if (resources.getBoolean(R.bool.show_date_in_numeric_format)) {
                    i |= 131072;
                }
                overrideTranslation = overrideTranslation + "\n" + DateUtils.formatDateTime(applicationContext, smsCbMessage.getReceivedTime(), i);
            }
            setTitle(overrideTranslation);
            textView.setText(overrideTranslation);
        }
        TextView textView2 = (TextView) findViewById(R.id.message);
        String messageBody = smsCbMessage.getMessageBody();
        if (!(textView2 == null || messageBody == null)) {
            int linkMethod = getLinkMethod(smsCbMessage.getSubscriptionId());
            if (linkMethod != 0) {
                addLinks(textView2, messageBody, linkMethod);
            } else {
                textView2.setText(messageBody);
            }
        }
        String string = getString(R.string.button_dismiss);
        if (this.mMessageList.size() > 1) {
            string = string + "  (1/" + this.mMessageList.size() + ")";
        }
        ((TextView) findViewById(R.id.dismissButton)).setText(string);
        setPictogram(applicationContext, smsCbMessage);
    }

    private void setPictogram(Context context, SmsCbMessage smsCbMessage) {
        int dialogPictogramResource = CellBroadcastResources.getDialogPictogramResource(context, smsCbMessage);
        ImageView imageView = (ImageView) findViewById(R.id.pictogramImage);
        if (dialogPictogramResource != -1) {
            imageView.setImageResource(dialogPictogramResource);
            imageView.setVisibility(0);
            return;
        }
        imageView.setVisibility(8);
    }

    private void setPictogramAreaLayout(int i) {
        ImageView imageView = (ImageView) findViewById(R.id.pictogramImage);
        if (imageView.getVisibility() == 0) {
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            if (i == 2) {
                Display defaultDisplay = getWindowManager().getDefaultDisplay();
                Point point = new Point();
                defaultDisplay.getSize(point);
                layoutParams.width = (int) (((double) point.x) * 0.3d);
                layoutParams.height = (int) (((double) point.y) * 0.3d);
            } else {
                layoutParams.width = -2;
                layoutParams.height = -2;
            }
            imageView.setLayoutParams(layoutParams);
        }
    }

    public void onNewIntent(Intent intent) {
        if (intent.getBooleanExtra("com.android.cellbroadcastreceiver.DIMISS_DIALOG", false)) {
            dismissAllFromNotification(intent);
            return;
        }
        ArrayList<SmsCbMessage> parcelableArrayListExtra = intent.getParcelableArrayListExtra("com.android.cellbroadcastreceiver.SMS_CB_MESSAGE");
        if (parcelableArrayListExtra != null) {
            if (intent.getBooleanExtra("from_save_state_notification", false)) {
                this.mMessageList = parcelableArrayListExtra;
            } else {
                Iterator<SmsCbMessage> it = parcelableArrayListExtra.iterator();
                while (it.hasNext()) {
                    this.mMessageList.removeIf(new Predicate(it.next()) {
                        public final /* synthetic */ SmsCbMessage f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final boolean test(Object obj) {
                            return CellBroadcastAlertDialog.lambda$onNewIntent$4(this.f$0, (SmsCbMessage) obj);
                        }
                    });
                }
                this.mMessageList.addAll(parcelableArrayListExtra);
                if (CellBroadcastSettings.getResourcesForDefaultSubId(getApplicationContext()).getBoolean(R.bool.show_cmas_messages_in_priority_order)) {
                    Collections.sort(this.mMessageList, $$Lambda$CellBroadcastAlertDialog$JGJJN_P2liUQ0VGOm3kNjhcXiA.INSTANCE);
                }
            }
            Log.d("CellBroadcastAlertDialog", "onNewIntent called with message list of size " + parcelableArrayListExtra.size());
            SmsCbMessage latestMessage = getLatestMessage();
            if (latestMessage != null && new CellBroadcastChannelManager(this, latestMessage.getSubscriptionId()).isEmergencyMessage(latestMessage)) {
                Log.d("CellBroadcastAlertDialog", "onCreate setting screen on timer for emergency alert for sub " + latestMessage.getSubscriptionId());
                this.mScreenOffHandler.startScreenOnTimer(latestMessage);
            }
            hideOptOutDialog();
            updateAlertText(getLatestMessage());
            clearNotification(intent);
            return;
        }
        Log.e("CellBroadcastAlertDialog", "onNewIntent called without SMS_CB_MESSAGE_EXTRA, ignoring");
    }

    static /* synthetic */ boolean lambda$onNewIntent$4(SmsCbMessage smsCbMessage, SmsCbMessage smsCbMessage2) {
        return smsCbMessage2.getReceivedTime() == smsCbMessage.getReceivedTime();
    }

    static /* synthetic */ int lambda$onNewIntent$5(Object obj, Object obj2) {
        SmsCbMessage smsCbMessage = (SmsCbMessage) obj;
        boolean z = false;
        boolean z2 = smsCbMessage.isCmasMessage() && smsCbMessage.getCmasWarningInfo().getMessageClass() == 0;
        SmsCbMessage smsCbMessage2 = (SmsCbMessage) obj2;
        if (smsCbMessage2.isCmasMessage() && smsCbMessage2.getCmasWarningInfo().getMessageClass() == 0) {
            z = true;
        }
        if (!(z ^ z2)) {
            return new Long(smsCbMessage2.getReceivedTime()).compareTo(new Long(smsCbMessage.getReceivedTime()));
        } else if (z2) {
            return 1;
        } else {
            return -1;
        }
    }

    private void clearNotification(Intent intent) {
        if (intent.getBooleanExtra(FROM_NOTIFICATION_EXTRA, false)) {
            ((NotificationManager) getSystemService("notification")).cancel(1);
            CellBroadcastReceiverApp.clearNewMessageList();
        }
    }

    public void dismissAllFromNotification(Intent intent) {
        Log.d("CellBroadcastAlertDialog", "dismissAllFromNotification");
        stopService(new Intent(this, CellBroadcastAlertAudio.class));
        CellBroadcastAlertReminder.cancelAlertReminder();
        ArrayList<SmsCbMessage> arrayList = this.mMessageList;
        if (arrayList != null) {
            arrayList.clear();
        }
        clearNotification(intent);
        this.mScreenOffHandler.stopScreenOnTimer();
        finish();
    }

    public void dismiss() {
        Log.d("CellBroadcastAlertDialog", "dismiss");
        stopService(new Intent(this, CellBroadcastAlertAudio.class));
        CellBroadcastAlertReminder.cancelAlertReminder();
        SmsCbMessage removeLatestMessage = removeLatestMessage();
        if (removeLatestMessage == null) {
            Log.e("CellBroadcastAlertDialog", "dismiss() called with empty message list!");
            finish();
            return;
        }
        removeReadMessageFromNotificationBar(removeLatestMessage, getApplicationContext());
        long receivedTime = removeLatestMessage.getReceivedTime();
        new CellBroadcastContentProvider.AsyncCellBroadcastTask(getContentResolver()).execute(new CellBroadcastContentProvider.CellBroadcastOperation[]{new CellBroadcastContentProvider.CellBroadcastOperation(receivedTime) {
            public final /* synthetic */ long f$0;

            {
                this.f$0 = r1;
            }

            public final boolean execute(CellBroadcastContentProvider cellBroadcastContentProvider) {
                return cellBroadcastContentProvider.markBroadcastRead("date", this.f$0);
            }
        }});
        CellBroadcastChannelManager cellBroadcastChannelManager = new CellBroadcastChannelManager(getApplicationContext(), removeLatestMessage.getSubscriptionId());
        CellBroadcastChannelManager.CellBroadcastChannelRange cellBroadcastChannelRangeFromMessage = cellBroadcastChannelManager.getCellBroadcastChannelRangeFromMessage(removeLatestMessage);
        if (!neverShowOptOutDialog(removeLatestMessage.getSubscriptionId()) && cellBroadcastChannelRangeFromMessage != null && !cellBroadcastChannelRangeFromMessage.mAlwaysOn) {
            this.mShowOptOutDialog = true;
        }
        SmsCbMessage latestMessage = getLatestMessage();
        if (latestMessage != null) {
            updateAlertText(latestMessage);
            int subscriptionId = latestMessage.getSubscriptionId();
            if (!cellBroadcastChannelManager.isEmergencyMessage(latestMessage) || cellBroadcastChannelRangeFromMessage == null || !cellBroadcastChannelRangeFromMessage.mDisplayIcon) {
                this.mAnimationHandler.stopIconAnimation();
            } else {
                this.mAnimationHandler.startIconAnimation(subscriptionId);
            }
        } else {
            this.mScreenOffHandler.stopScreenOnTimer();
            if (this.mShowOptOutDialog) {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (defaultSharedPreferences.getBoolean("show_cmas_opt_out_dialog", true)) {
                    defaultSharedPreferences.edit().putBoolean("show_cmas_opt_out_dialog", false).apply();
                    if (((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
                        Log.d("CellBroadcastAlertDialog", "Showing opt-out dialog in new activity (secure keyguard)");
                        startActivity(new Intent(this, CellBroadcastOptOutActivity.class));
                    } else {
                        Log.d("CellBroadcastAlertDialog", "Showing opt-out dialog in current activity");
                        this.mOptOutDialog = CellBroadcastOptOutActivity.showOptOutDialog(this);
                        return;
                    }
                }
            }
            finish();
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Log.d("CellBroadcastAlertDialog", "onKeyDown: " + keyEvent);
        SmsCbMessage latestMessage = getLatestMessage();
        if (latestMessage == null || !CellBroadcastSettings.getResources(getApplicationContext(), latestMessage.getSubscriptionId()).getBoolean(R.bool.mute_by_physical_button)) {
            keyEvent.getKeyCode();
            return true;
        }
        int keyCode = keyEvent.getKeyCode();
        if (keyCode != 24 && keyCode != 25 && keyCode != 27 && keyCode != 80 && keyCode != 164) {
            return super.onKeyDown(i, keyEvent);
        }
        stopService(new Intent(this, CellBroadcastAlertAudio.class));
        return true;
    }

    private void hideOptOutDialog() {
        AlertDialog alertDialog = this.mOptOutDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("show_cmas_opt_out_dialog", true).apply();
            this.mOptOutDialog.dismiss();
        }
    }

    private boolean neverShowOptOutDialog(int i) {
        return CellBroadcastSettings.getResources(getApplicationContext(), i).getBoolean(R.bool.disable_opt_out_dialog);
    }

    public static boolean copyMessageToClipboard(SmsCbMessage smsCbMessage, Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService("clipboard");
        if (clipboardManager == null) {
            return false;
        }
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Alert Message", smsCbMessage.getMessageBody()));
        Toast.makeText(context, CellBroadcastSettings.getResources(context, smsCbMessage.getSubscriptionId()).getString(R.string.message_copied), 0).show();
        return true;
    }

    private void removeReadMessageFromNotificationBar(SmsCbMessage smsCbMessage, Context context) {
        Log.d("CellBroadcastAlertDialog", "removeReadMessageFromNotificationBar, msg: " + smsCbMessage.toString());
        ArrayList<SmsCbMessage> removeReadMessage = CellBroadcastReceiverApp.removeReadMessage(smsCbMessage);
        if (removeReadMessage.isEmpty()) {
            Log.d("CellBroadcastAlertDialog", "removeReadMessageFromNotificationBar, cancel notification");
            ((NotificationManager) getSystemService(NotificationManager.class)).cancel(1);
            return;
        }
        Log.d("CellBroadcastAlertDialog", "removeReadMessageFromNotificationBar, update count to " + removeReadMessage.size());
        CellBroadcastAlertService.addToNotificationBar(CellBroadcastReceiverApp.getLatestMessage(), removeReadMessage, context, false, false, false);
    }

    private void setStatusBarDisabledIfNeeded(boolean z) {
        if (CellBroadcastSettings.getResourcesForDefaultSubId(getApplicationContext()).getBoolean(R.bool.disable_status_bar)) {
            try {
                StatusBarManager statusBarManager = (StatusBarManager) getSystemService(StatusBarManager.class);
                Method declaredMethod = StatusBarManager.class.getDeclaredMethod("disable", new Class[]{Integer.TYPE});
                Method declaredMethod2 = StatusBarManager.class.getDeclaredMethod("disable2", new Class[]{Integer.TYPE});
                if (z) {
                    int i = StatusBarManager.class.getDeclaredField("DISABLE_HOME").getInt((Object) null);
                    int i2 = StatusBarManager.class.getDeclaredField("DISABLE_RECENT").getInt((Object) null);
                    int i3 = StatusBarManager.class.getDeclaredField("DISABLE_BACK").getInt((Object) null);
                    int i4 = StatusBarManager.class.getDeclaredField("DISABLE2_QUICK_SETTINGS").getInt((Object) null);
                    int i5 = StatusBarManager.class.getDeclaredField("DISABLE2_NOTIFICATION_SHADE").getInt((Object) null);
                    declaredMethod.invoke(statusBarManager, new Object[]{Integer.valueOf(i | i3 | i2)});
                    declaredMethod2.invoke(statusBarManager, new Object[]{Integer.valueOf(i4 | i5)});
                    return;
                }
                int i6 = StatusBarManager.class.getDeclaredField("DISABLE_NONE").getInt((Object) null);
                declaredMethod.invoke(statusBarManager, new Object[]{Integer.valueOf(i6)});
                declaredMethod2.invoke(statusBarManager, new Object[]{Integer.valueOf(i6)});
            } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
                Log.e("CellBroadcastAlertDialog", "Failed to disable navigation when showing alert: " + e);
            }
        }
    }
}

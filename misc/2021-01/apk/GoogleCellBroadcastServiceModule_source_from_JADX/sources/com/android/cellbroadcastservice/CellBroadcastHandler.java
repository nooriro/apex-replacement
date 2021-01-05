package com.android.cellbroadcastservice;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Telephony;
import android.telephony.CbGeoUtils;
import android.telephony.CellBroadcastIntents;
import android.telephony.SmsCbMessage;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.cellbroadcastservice.CellBroadcastHandler;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CellBroadcastHandler extends WakeLockStateMachine {
    private static final String CB_APEX_PATH = new File("/apex", "com.android.cellbroadcast").getAbsolutePath();
    private static final boolean IS_DEBUGGABLE;
    private final CbSendMessageCalculatorFactory mCbSendMessageCalculatorFactory;
    /* access modifiers changed from: private */
    public boolean mEnableDuplicateDetection;
    protected long mLastAirplaneModeTime;
    private final LocalLog mLocalLog;
    private final LocationRequester mLocationRequester;
    private BroadcastReceiver mReceiver;
    private final Map<Integer, Resources> mResourcesCache;
    private final Map<Integer, Integer> mServiceCategoryCrossRATMap;

    public interface LocationUpdateCallback {
        void onLocationUpdate(CbGeoUtils.LatLng latLng, double d);
    }

    static {
        boolean z = false;
        if (SystemProperties.getInt("ro.debuggable", 0) == 1) {
            z = true;
        }
        IS_DEBUGGABLE = z;
    }

    private CellBroadcastHandler(Context context) {
        this(CellBroadcastHandler.class.getSimpleName(), context, Looper.myLooper(), new CbSendMessageCalculatorFactory());
    }

    public static class CbSendMessageCalculatorFactory {
        public CbSendMessageCalculator createNew(Context context, List<CbGeoUtils.Geometry> list) {
            return new CbSendMessageCalculator(context, list);
        }
    }

    public CellBroadcastHandler(String str, Context context, Looper looper, CbSendMessageCalculatorFactory cbSendMessageCalculatorFactory) {
        super(str, context, looper);
        this.mLocalLog = new LocalLog(100);
        this.mLastAirplaneModeTime = 0;
        this.mResourcesCache = new HashMap();
        this.mEnableDuplicateDetection = true;
        this.mReceiver = new BroadcastReceiver() {
            /* JADX WARNING: Removed duplicated region for block: B:12:0x002c  */
            /* JADX WARNING: Removed duplicated region for block: B:19:0x0078  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onReceive(android.content.Context r5, android.content.Intent r6) {
                /*
                    r4 = this;
                    java.lang.String r5 = r6.getAction()
                    int r0 = r5.hashCode()
                    r1 = -1076576821(0xffffffffbfd4bdcb, float:-1.662042)
                    r2 = 0
                    r3 = 1
                    if (r0 == r1) goto L_0x001f
                    r1 = 158494245(0x9726e25, float:2.918148E-33)
                    if (r0 == r1) goto L_0x0015
                    goto L_0x0029
                L_0x0015:
                    java.lang.String r0 = "com.android.cellbroadcastservice.action.DUPLICATE_DETECTION"
                    boolean r5 = r5.equals(r0)
                    if (r5 == 0) goto L_0x0029
                    r5 = r3
                    goto L_0x002a
                L_0x001f:
                    java.lang.String r0 = "android.intent.action.AIRPLANE_MODE"
                    boolean r5 = r5.equals(r0)
                    if (r5 == 0) goto L_0x0029
                    r5 = r2
                    goto L_0x002a
                L_0x0029:
                    r5 = -1
                L_0x002a:
                    if (r5 == 0) goto L_0x0078
                    if (r5 == r3) goto L_0x0049
                    com.android.cellbroadcastservice.CellBroadcastHandler r4 = com.android.cellbroadcastservice.CellBroadcastHandler.this
                    java.lang.StringBuilder r5 = new java.lang.StringBuilder
                    r5.<init>()
                    java.lang.String r0 = "Unhandled broadcast "
                    r5.append(r0)
                    java.lang.String r6 = r6.getAction()
                    r5.append(r6)
                    java.lang.String r5 = r5.toString()
                    r4.log(r5)
                    goto L_0x008f
                L_0x0049:
                    com.android.cellbroadcastservice.CellBroadcastHandler r5 = com.android.cellbroadcastservice.CellBroadcastHandler.this
                    java.lang.String r0 = "enable"
                    boolean r6 = r6.getBooleanExtra(r0, r3)
                    boolean unused = r5.mEnableDuplicateDetection = r6
                    com.android.cellbroadcastservice.CellBroadcastHandler r5 = com.android.cellbroadcastservice.CellBroadcastHandler.this
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder
                    r6.<init>()
                    java.lang.String r0 = "Duplicate detection "
                    r6.append(r0)
                    com.android.cellbroadcastservice.CellBroadcastHandler r4 = com.android.cellbroadcastservice.CellBroadcastHandler.this
                    boolean r4 = r4.mEnableDuplicateDetection
                    if (r4 == 0) goto L_0x006b
                    java.lang.String r4 = "enabled"
                    goto L_0x006d
                L_0x006b:
                    java.lang.String r4 = "disabled"
                L_0x006d:
                    r6.append(r4)
                    java.lang.String r4 = r6.toString()
                    r5.log(r4)
                    goto L_0x008f
                L_0x0078:
                    java.lang.String r5 = "state"
                    boolean r5 = r6.getBooleanExtra(r5, r2)
                    if (r5 == 0) goto L_0x008f
                    com.android.cellbroadcastservice.CellBroadcastHandler r5 = com.android.cellbroadcastservice.CellBroadcastHandler.this
                    long r0 = java.lang.System.currentTimeMillis()
                    r5.mLastAirplaneModeTime = r0
                    com.android.cellbroadcastservice.CellBroadcastHandler r4 = com.android.cellbroadcastservice.CellBroadcastHandler.this
                    java.lang.String r5 = "Airplane mode on."
                    r4.log(r5)
                L_0x008f:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastservice.CellBroadcastHandler.C00051.onReceive(android.content.Context, android.content.Intent):void");
            }
        };
        this.mCbSendMessageCalculatorFactory = cbSendMessageCalculatorFactory;
        this.mLocationRequester = new LocationRequester(context, (LocationManager) this.mContext.getSystemService("location"), getHandler());
        this.mServiceCategoryCrossRATMap = (Map) Stream.of(new Integer[][]{new Integer[]{4370, 4096}, new Integer[]{4383, 4096}, new Integer[]{4371, 4097}, new Integer[]{4384, 4097}, new Integer[]{4372, 4097}, new Integer[]{4385, 4097}, new Integer[]{4373, 4098}, new Integer[]{4386, 4098}, new Integer[]{4374, 4098}, new Integer[]{4387, 4098}, new Integer[]{4375, 4098}, new Integer[]{4388, 4098}, new Integer[]{4376, 4098}, new Integer[]{4389, 4098}, new Integer[]{4377, 4098}, new Integer[]{4390, 4098}, new Integer[]{4378, 4098}, new Integer[]{4391, 4098}, new Integer[]{4379, 4099}, new Integer[]{4392, 4099}, new Integer[]{4380, 4100}, new Integer[]{4393, 4100}}).collect(Collectors.toMap($$Lambda$CellBroadcastHandler$HAXZLMNISaTHgUty2wVKP2q8Xok.INSTANCE, $$Lambda$CellBroadcastHandler$VA_K5xd17DVVzDWJNRwmkGA6h3k.INSTANCE));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        if (IS_DEBUGGABLE) {
            intentFilter.addAction("com.android.cellbroadcastservice.action.DUPLICATE_DETECTION");
        }
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
    }

    static /* synthetic */ Integer lambda$new$0(Integer[] numArr) {
        return numArr[0];
    }

    static /* synthetic */ Integer lambda$new$1(Integer[] numArr) {
        return numArr[1];
    }

    public void cleanup() {
        if (WakeLockStateMachine.DBG) {
            log("CellBroadcastHandler cleanup");
        }
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    public static CellBroadcastHandler makeCellBroadcastHandler(Context context) {
        CellBroadcastHandler cellBroadcastHandler = new CellBroadcastHandler(context);
        cellBroadcastHandler.start();
        return cellBroadcastHandler;
    }

    public boolean handleSmsMessage(Message message) {
        Object obj = message.obj;
        if (!(obj instanceof SmsCbMessage)) {
            String str = "handleSmsMessage got object of type: " + message.obj.getClass().getName();
            loge(str);
            CellBroadcastStatsLog.write(250, 13, str);
            return false;
        } else if (!isDuplicate((SmsCbMessage) obj)) {
            handleBroadcastSms((SmsCbMessage) message.obj);
            return true;
        } else {
            CellBroadcastStatsLog.write(278, 2, 1);
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public int getMaxLocationWaitingTime(SmsCbMessage smsCbMessage) {
        int maximumWaitingDuration = smsCbMessage.getMaximumWaitingDuration();
        return maximumWaitingDuration == 255 ? getResources(smsCbMessage.getSubscriptionId()).getInteger(R.integer.max_location_waiting_time) : maximumWaitingDuration;
    }

    /* access modifiers changed from: protected */
    public void handleBroadcastSms(SmsCbMessage smsCbMessage) {
        int slotIndex = smsCbMessage.getSlotIndex();
        Uri insert = this.mContext.getContentResolver().insert(Telephony.CellBroadcasts.CONTENT_URI, smsCbMessage.getContentValues());
        if (smsCbMessage.needGeoFencingCheck()) {
            int maxLocationWaitingTime = getMaxLocationWaitingTime(smsCbMessage);
            if (WakeLockStateMachine.DBG) {
                log("Requesting location for geo-fencing. serialNumber = " + smsCbMessage.getSerialNumber() + ", maximumWaitingTime = " + maxLocationWaitingTime);
            }
            requestLocationUpdate(new LocationUpdateCallback(smsCbMessage, insert, slotIndex) {
                public final /* synthetic */ SmsCbMessage f$1;
                public final /* synthetic */ Uri f$2;
                public final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onLocationUpdate(CbGeoUtils.LatLng latLng, double d) {
                    CellBroadcastHandler.this.lambda$handleBroadcastSms$2$CellBroadcastHandler(this.f$1, this.f$2, this.f$3, latLng, d);
                }
            }, maxLocationWaitingTime);
            return;
        }
        if (WakeLockStateMachine.DBG) {
            log("Broadcast the message directly because no geo-fencing required, serialNumber = " + smsCbMessage.getSerialNumber() + " needGeoFencing = " + smsCbMessage.needGeoFencingCheck());
        }
        broadcastMessage(smsCbMessage, insert, slotIndex);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleBroadcastSms$2 */
    public /* synthetic */ void lambda$handleBroadcastSms$2$CellBroadcastHandler(SmsCbMessage smsCbMessage, Uri uri, int i, CbGeoUtils.LatLng latLng, double d) {
        if (latLng == null) {
            broadcastMessage(smsCbMessage, uri, i);
            return;
        }
        performGeoFencing(smsCbMessage, uri, smsCbMessage.getGeometries(), latLng, i, d);
    }

    private boolean isSameLocation(SmsCbMessage smsCbMessage, SmsCbMessage smsCbMessage2) {
        if (smsCbMessage.getGeographicalScope() != smsCbMessage2.getGeographicalScope()) {
            return false;
        }
        if (smsCbMessage.getGeographicalScope() == 0 || smsCbMessage.getGeographicalScope() == 3) {
            return smsCbMessage.getLocation().isInLocationArea(smsCbMessage2.getLocation());
        }
        if (smsCbMessage.getGeographicalScope() == 2) {
            if (smsCbMessage.getLocation().getPlmn().equals(smsCbMessage2.getLocation().getPlmn()) && smsCbMessage.getLocation().getLac() != -1 && smsCbMessage.getLocation().getLac() == smsCbMessage2.getLocation().getLac()) {
                return true;
            }
            return false;
        } else if (smsCbMessage.getGeographicalScope() != 1 || TextUtils.isEmpty(smsCbMessage.getLocation().getPlmn()) || !smsCbMessage.getLocation().getPlmn().equals(smsCbMessage2.getLocation().getPlmn())) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isDuplicate(SmsCbMessage smsCbMessage) {
        if (!this.mEnableDuplicateDetection) {
            log("Duplicate detection was disabled for debugging purposes.");
            return false;
        }
        Resources resources = getResources(smsCbMessage.getSubscriptionId());
        long currentTimeMillis = System.currentTimeMillis() - ((long) resources.getInteger(R.integer.message_expiration_time));
        if (resources.getBoolean(R.bool.reset_on_power_cycle_or_airplane_mode)) {
            currentTimeMillis = Long.max(Long.max(currentTimeMillis, this.mLastAirplaneModeTime), System.currentTimeMillis() - SystemClock.elapsedRealtime());
        }
        ArrayList<SmsCbMessage> arrayList = new ArrayList<>();
        Cursor query = this.mContext.getContentResolver().query(Telephony.CellBroadcasts.CONTENT_URI, CellBroadcastProvider.QUERY_COLUMNS, "received_time>?", new String[]{Long.toString(currentTimeMillis)}, (String) null);
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    arrayList.add(SmsCbMessage.createFromCursor(query));
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            }
        }
        if (query != null) {
            query.close();
        }
        boolean z = resources.getBoolean(R.bool.duplicate_compare_body);
        log("Found " + arrayList.size() + " messages since " + DateFormat.getDateTimeInstance().format(Long.valueOf(currentTimeMillis)));
        for (SmsCbMessage smsCbMessage2 : arrayList) {
            if (smsCbMessage.getSlotIndex() != smsCbMessage2.getSlotIndex()) {
                if (TextUtils.equals(smsCbMessage.getMessageBody(), smsCbMessage2.getMessageBody())) {
                    log("Duplicate message detected from different slot. " + smsCbMessage);
                    return true;
                }
            } else if (smsCbMessage.getSerialNumber() == smsCbMessage2.getSerialNumber() && ((!smsCbMessage.isEtwsMessage() || !smsCbMessage2.isEtwsMessage() || smsCbMessage.getEtwsWarningInfo().isPrimary() == smsCbMessage2.getEtwsWarningInfo().isPrimary()) && ((smsCbMessage.getServiceCategory() == smsCbMessage2.getServiceCategory() || Objects.equals(this.mServiceCategoryCrossRATMap.get(Integer.valueOf(smsCbMessage.getServiceCategory())), Integer.valueOf(smsCbMessage2.getServiceCategory())) || Objects.equals(this.mServiceCategoryCrossRATMap.get(Integer.valueOf(smsCbMessage2.getServiceCategory())), Integer.valueOf(smsCbMessage.getServiceCategory()))) && isSameLocation(smsCbMessage, smsCbMessage2)))) {
                if (!z || TextUtils.equals(smsCbMessage.getMessageBody(), smsCbMessage2.getMessageBody())) {
                    log("Duplicate message detected. " + smsCbMessage);
                    return true;
                }
            }
        }
        log("Not a duplicate message. " + smsCbMessage);
        return false;
        throw th;
    }

    public void performGeoFencing(SmsCbMessage smsCbMessage, Uri uri, List<CbGeoUtils.Geometry> list, CbGeoUtils.LatLng latLng, int i, double d) {
        if (WakeLockStateMachine.DBG) {
            logd("Perform geo-fencing check for message identifier = " + smsCbMessage.getServiceCategory() + " serialNumber = " + smsCbMessage.getSerialNumber());
        }
        if (uri != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("location_check_time", Long.valueOf(System.currentTimeMillis()));
            this.mContext.getContentResolver().update(Telephony.CellBroadcasts.CONTENT_URI, contentValues, "_id=?", new String[]{uri.getLastPathSegment()});
        }
        CbSendMessageCalculator createNew = this.mCbSendMessageCalculatorFactory.createNew(this.mContext, list);
        createNew.addCoordinate(latLng, d);
        if (createNew.getAction() == 1 || createNew.getAction() == 3 || createNew.getAction() == 0) {
            broadcastMessage(smsCbMessage, uri, i);
            if (WakeLockStateMachine.DBG) {
                Log.d("CellBroadcastHandler", "performGeoFencing: SENT.  action=" + createNew.getActionString() + ", loc=" + latLng.toString() + ", acc=" + d);
                createNew.getAction();
                return;
            }
            return;
        }
        if (WakeLockStateMachine.DBG) {
            logd("Device location is outside the broadcast area " + CbGeoUtils.encodeGeometriesToString(list));
            Log.d("CellBroadcastHandler", "performGeoFencing: OUTSIDE.  action=" + createNew.getAction() + ", loc=" + latLng.toString() + ", acc=" + d);
        }
        if (smsCbMessage.getMessageFormat() == 1) {
            CellBroadcastStatsLog.write(278, 1, 2);
        } else if (smsCbMessage.getMessageFormat() == 2) {
            CellBroadcastStatsLog.write(278, 2, 2);
        }
        sendMessage(4);
    }

    /* access modifiers changed from: protected */
    public void requestLocationUpdate(LocationUpdateCallback locationUpdateCallback, int i) {
        this.mLocationRequester.requestLocationUpdate(locationUpdateCallback, i);
    }

    protected static int getSubIdForPhone(Context context, int i) {
        int[] subscriptionIds = ((SubscriptionManager) context.getSystemService("telephony_subscription_service")).getSubscriptionIds(i);
        if (subscriptionIds != null) {
            return subscriptionIds[0];
        }
        return -1;
    }

    public static void putPhoneIdAndSubIdExtra(Context context, Intent intent, int i) {
        int subIdForPhone = getSubIdForPhone(context, i);
        if (subIdForPhone != -1) {
            intent.putExtra("subscription", subIdForPhone);
            intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", subIdForPhone);
        }
        intent.putExtra("phone", i);
        intent.putExtra("android.telephony.extra.SLOT_INDEX", i);
    }

    /* access modifiers changed from: protected */
    public void broadcastMessage(SmsCbMessage smsCbMessage, Uri uri, int i) {
        String[] stringArray;
        SmsCbMessage smsCbMessage2 = smsCbMessage;
        if (smsCbMessage.isEmergencyMessage()) {
            String str = "Dispatching emergency SMS CB, SmsCbMessage is: " + smsCbMessage2;
            log(str);
            this.mLocalLog.log(str);
            Intent intent = new Intent("android.provider.action.SMS_EMERGENCY_CB_RECEIVED");
            intent.addFlags(268435456);
            intent.putExtra("message", smsCbMessage2);
            putPhoneIdAndSubIdExtra(this.mContext, intent, i);
            if (IS_DEBUGGABLE && (stringArray = this.mContext.getResources().getStringArray(R.array.test_cell_broadcast_receiver_packages)) != null) {
                Intent intent2 = new Intent(intent);
                for (String str2 : stringArray) {
                    intent2.setPackage(str2);
                    this.mContext.createContextAsUser(UserHandle.ALL, 0).sendOrderedBroadcast(intent, (String) null, (Bundle) null, (BroadcastReceiver) null, getHandler(), -1, (String) null, (Bundle) null);
                }
            }
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(getDefaultCBRPackageName(this.mContext, intent));
            arrayList.addAll(Arrays.asList(this.mContext.getResources().getStringArray(R.array.additional_cell_broadcast_receiver_packages)));
            this.mReceiverCount.addAndGet(arrayList.size());
            for (String str3 : arrayList) {
                intent.setPackage(str3);
                this.mContext.createContextAsUser(UserHandle.ALL, 0).sendOrderedBroadcast(intent, (String) null, (Bundle) null, this.mOrderedBroadcastReceiver, getHandler(), -1, (String) null, (Bundle) null);
            }
        } else {
            String str4 = "Dispatching SMS CB, SmsCbMessage is: " + smsCbMessage2;
            log(str4);
            this.mLocalLog.log(str4);
            this.mReceiverCount.incrementAndGet();
            CellBroadcastIntents.sendSmsCbReceivedBroadcast(this.mContext, UserHandle.ALL, smsCbMessage, this.mOrderedBroadcastReceiver, getHandler(), -1, i);
        }
        if (uri != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("message_broadcasted", 1);
            this.mContext.getContentResolver().update(Telephony.CellBroadcasts.CONTENT_URI, contentValues, "_id=?", new String[]{uri.getLastPathSegment()});
        }
    }

    private static boolean isAppInCBApex(ApplicationInfo applicationInfo) {
        return applicationInfo.sourceDir.startsWith(CB_APEX_PATH);
    }

    static String getDefaultCBRPackageName(Context context, Intent intent) {
        List<ResolveInfo> queryBroadcastReceivers = context.getPackageManager().queryBroadcastReceivers(intent, 0);
        queryBroadcastReceivers.removeIf($$Lambda$CellBroadcastHandler$TQ1yvK6SB02ETRxGo4qxdxXroU.INSTANCE);
        if (queryBroadcastReceivers.isEmpty()) {
            Log.e("CellBroadcastHandler", "getCBRPackageNames: no package found");
            return null;
        }
        if (queryBroadcastReceivers.size() > 1) {
            Log.e("CellBroadcastHandler", "Found > 1 APK in CB apex that can resolve " + intent.getAction() + ": " + ((String) queryBroadcastReceivers.stream().map($$Lambda$CellBroadcastHandler$iEUYFMNHBXxaj1WY_E6q6q7hkw.INSTANCE).collect(Collectors.joining(", "))));
        }
        return queryBroadcastReceivers.get(0).activityInfo.applicationInfo.packageName;
    }

    static /* synthetic */ boolean lambda$getDefaultCBRPackageName$3(ResolveInfo resolveInfo) {
        return !isAppInCBApex(resolveInfo.activityInfo.applicationInfo);
    }

    public Resources getResources(int i) {
        if (i == Integer.MAX_VALUE || !SubscriptionManager.isValidSubscriptionId(i)) {
            return this.mContext.getResources();
        }
        if (this.mResourcesCache.containsKey(Integer.valueOf(i))) {
            return this.mResourcesCache.get(Integer.valueOf(i));
        }
        Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(this.mContext, i);
        this.mResourcesCache.put(Integer.valueOf(i), resourcesForSubId);
        return resourcesForSubId;
    }

    private static final class LocationRequester {
        private static final String TAG = CellBroadcastHandler.class.getSimpleName();
        private final List<LocationUpdateCallback> mCallbacks = new ArrayList();
        private CancellationSignal mCancellationSignal;
        private final Context mContext;
        private final Handler mLocationHandler;
        private final LocationManager mLocationManager;
        private boolean mLocationUpdateInProgress;
        private final Runnable mTimeoutCallback;

        LocationRequester(Context context, LocationManager locationManager, Handler handler) {
            this.mLocationManager = locationManager;
            this.mContext = context;
            this.mLocationHandler = handler;
            this.mLocationUpdateInProgress = false;
            this.mTimeoutCallback = new Runnable() {
                public final void run() {
                    CellBroadcastHandler.LocationRequester.this.onLocationTimeout();
                }
            };
        }

        /* access modifiers changed from: package-private */
        public void requestLocationUpdate(LocationUpdateCallback locationUpdateCallback, int i) {
            this.mLocationHandler.post(new Runnable(locationUpdateCallback, i) {
                public final /* synthetic */ CellBroadcastHandler.LocationUpdateCallback f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    CellBroadcastHandler.LocationRequester.this.mo49x35c69096(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        public void onLocationTimeout() {
            Log.e(TAG, "Location request timeout");
            CancellationSignal cancellationSignal = this.mCancellationSignal;
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
            }
            onLocationUpdate((Location) null);
        }

        /* access modifiers changed from: private */
        public void onLocationUpdate(Location location) {
            float f;
            CbGeoUtils.LatLng latLng;
            String str = TAG;
            this.mLocationUpdateInProgress = false;
            this.mLocationHandler.removeCallbacks(this.mTimeoutCallback);
            if (location != null) {
                Log.d(str, "Got location update");
                latLng = new CbGeoUtils.LatLng(location.getLatitude(), location.getLongitude());
                f = location.getAccuracy();
            } else {
                Log.e(str, "Location is not available.");
                latLng = null;
                f = 0.0f;
            }
            for (LocationUpdateCallback onLocationUpdate : this.mCallbacks) {
                onLocationUpdate.onLocationUpdate(latLng, (double) f);
            }
            this.mCallbacks.clear();
        }

        /* access modifiers changed from: private */
        /* renamed from: requestLocationUpdateInternal */
        public void lambda$requestLocationUpdate$0(LocationUpdateCallback locationUpdateCallback, int i) {
            String str = TAG;
            if (WakeLockStateMachine.DBG) {
                Log.d(str, "requestLocationUpdate");
            }
            if (hasPermission("android.permission.ACCESS_FINE_LOCATION") || hasPermission("android.permission.ACCESS_COARSE_LOCATION")) {
                if (!this.mLocationUpdateInProgress) {
                    long j = (long) i;
                    LocationRequest expireIn = LocationRequest.create().setProvider("fused").setQuality(100).setInterval(0).setFastestInterval(0).setSmallestDisplacement(0.0f).setNumUpdates(1).setExpireIn(TimeUnit.SECONDS.toMillis(j));
                    if (WakeLockStateMachine.DBG) {
                        Log.d(str, "Location request=" + expireIn);
                    }
                    try {
                        CancellationSignal cancellationSignal = new CancellationSignal();
                        this.mCancellationSignal = cancellationSignal;
                        this.mLocationManager.getCurrentLocation(expireIn, cancellationSignal, new HandlerExecutor(this.mLocationHandler), new Consumer() {
                            public final void accept(Object obj) {
                                CellBroadcastHandler.LocationRequester.this.onLocationUpdate((Location) obj);
                            }
                        });
                        this.mLocationHandler.postDelayed(this.mTimeoutCallback, TimeUnit.SECONDS.toMillis(j));
                        this.mLocationUpdateInProgress = true;
                    } catch (IllegalArgumentException e) {
                        Log.e(str, "Cannot get current location. e=" + e);
                        locationUpdateCallback.onLocationUpdate((CbGeoUtils.LatLng) null, 0.0d);
                        return;
                    }
                }
                this.mCallbacks.add(locationUpdateCallback);
                return;
            }
            if (WakeLockStateMachine.DBG) {
                Log.e(str, "Can't request location update because of no location permission");
            }
            locationUpdateCallback.onLocationUpdate((CbGeoUtils.LatLng) null, Double.NaN);
        }

        private boolean hasPermission(String str) {
            return this.mContext.checkPermission(str, Process.myPid(), Process.myUid()) == 0;
        }
    }
}

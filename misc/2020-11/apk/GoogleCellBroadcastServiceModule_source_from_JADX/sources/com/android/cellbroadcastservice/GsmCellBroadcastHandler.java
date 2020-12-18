package com.android.cellbroadcastservice;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Telephony;
import android.telephony.CbGeoUtils;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityTdscdma;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.SmsCbLocation;
import android.telephony.SmsCbMessage;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Pair;
import android.util.SparseArray;
import com.android.cellbroadcastservice.CellBroadcastHandler;
import com.android.cellbroadcastservice.GsmSmsCbMessage;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GsmCellBroadcastHandler extends CellBroadcastHandler {
    private final SparseArray<String> mAreaInfos = new SparseArray<>();
    private final HashMap<SmsCbConcatInfo, byte[][]> mSmsCbPageMap = new HashMap<>(4);

    public GsmCellBroadcastHandler(Context context, Looper looper) {
        super("GsmCellBroadcastHandler", context, looper);
    }

    /* access modifiers changed from: protected */
    public void onQuitting() {
        super.onQuitting();
    }

    public void onGsmCellBroadcastSms(int i, byte[] bArr) {
        sendMessage(1, i, -1, bArr);
    }

    public String getCellBroadcastAreaInfo(int i) {
        String str;
        synchronized (this.mAreaInfos) {
            str = this.mAreaInfos.get(i);
        }
        return str == null ? "" : str;
    }

    public static GsmCellBroadcastHandler makeGsmCellBroadcastHandler(Context context) {
        GsmCellBroadcastHandler gsmCellBroadcastHandler = new GsmCellBroadcastHandler(context, Looper.myLooper());
        gsmCellBroadcastHandler.start();
        return gsmCellBroadcastHandler;
    }

    private boolean handleGeoFencingTriggerMessage(GsmSmsCbMessage.GeoFencingTriggerMessage geoFencingTriggerMessage, int i) {
        Resources resources;
        Throwable th;
        GsmSmsCbMessage.GeoFencingTriggerMessage geoFencingTriggerMessage2 = geoFencingTriggerMessage;
        ArrayList<SmsCbMessage> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        int[] subscriptionIds = ((SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service")).getSubscriptionIds(i);
        if (subscriptionIds != null) {
            resources = getResources(subscriptionIds[0]);
        } else {
            resources = getResources(Integer.MAX_VALUE);
        }
        long currentTimeMillis = System.currentTimeMillis() - 86400000;
        if (resources.getBoolean(R.bool.reset_on_power_cycle_or_airplane_mode)) {
            currentTimeMillis = Long.max(Long.max(currentTimeMillis, this.mLastAirplaneModeTime), System.currentTimeMillis() - SystemClock.elapsedRealtime());
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        for (GsmSmsCbMessage.GeoFencingTriggerMessage.CellBroadcastIdentity next : geoFencingTriggerMessage2.cbIdentifiers) {
            Cursor query = contentResolver.query(Telephony.CellBroadcasts.CONTENT_URI, CellBroadcastProvider.QUERY_COLUMNS, "service_category=? AND serial_number=? AND message_displayed=? AND received_time>?", new String[]{Integer.toString(next.messageIdentifier), Integer.toString(next.serialNumber), "0", Long.toString(currentTimeMillis)}, (String) null);
            if (query != null) {
                while (query.moveToNext()) {
                    try {
                        arrayList.add(SmsCbMessage.createFromCursor(query));
                        arrayList2.add(ContentUris.withAppendedId(Telephony.CellBroadcasts.CONTENT_URI, (long) query.getInt(query.getColumnIndex("_id"))));
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
            }
            if (query != null) {
                query.close();
            }
        }
        log("Found " + arrayList.size() + " not broadcasted messages since " + DateFormat.getDateTimeInstance().format(Long.valueOf(currentTimeMillis)));
        ArrayList arrayList3 = new ArrayList();
        if (geoFencingTriggerMessage.shouldShareBroadcastArea()) {
            for (SmsCbMessage smsCbMessage : arrayList) {
                if (smsCbMessage.getGeometries() != null) {
                    arrayList3.addAll(smsCbMessage.getGeometries());
                }
            }
        }
        int i2 = 0;
        for (SmsCbMessage maxLocationWaitingTime : arrayList) {
            i2 = Math.max(i2, getMaxLocationWaitingTime(maxLocationWaitingTime));
        }
        if (WakeLockStateMachine.DBG) {
            logd("Geo-fencing trigger message = " + geoFencingTriggerMessage2);
            for (SmsCbMessage smsCbMessage2 : arrayList) {
                logd(smsCbMessage2.toString());
            }
        }
        if (arrayList.isEmpty()) {
            if (WakeLockStateMachine.DBG) {
                logd("No CellBroadcast message need to be broadcasted");
            }
            return false;
        }
        requestLocationUpdate(new CellBroadcastHandler.LocationUpdateCallback(arrayList, arrayList2, i, arrayList3) {
            public final /* synthetic */ List f$1;
            public final /* synthetic */ List f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ List f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onLocationUpdate(CbGeoUtils.LatLng latLng) {
                GsmCellBroadcastHandler.this.lambda$handleGeoFencingTriggerMessage$0$GsmCellBroadcastHandler(this.f$1, this.f$2, this.f$3, this.f$4, latLng);
            }
        }, i2);
        return true;
        throw th;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleGeoFencingTriggerMessage$0 */
    public /* synthetic */ void lambda$handleGeoFencingTriggerMessage$0$GsmCellBroadcastHandler(List list, List list2, int i, List list3, CbGeoUtils.LatLng latLng) {
        List list4;
        int i2 = 0;
        if (latLng == null) {
            while (i2 < list.size()) {
                broadcastMessage((SmsCbMessage) list.get(i2), (Uri) list2.get(i2), i);
                i2++;
            }
            return;
        }
        while (i2 < list.size()) {
            if (!list3.isEmpty()) {
                list4 = list3;
            } else {
                list4 = ((SmsCbMessage) list.get(i2)).getGeometries();
            }
            if (list4 == null || list4.isEmpty()) {
                broadcastMessage((SmsCbMessage) list.get(i2), (Uri) list2.get(i2), i);
            } else {
                performGeoFencing((SmsCbMessage) list.get(i2), (Uri) list2.get(i2), list4, latLng, i);
            }
            i2++;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0041, code lost:
        r9 = r7.mContext.getResources().getStringArray(com.android.cellbroadcastservice.R.array.config_area_info_receiver_packages);
        r0 = r9.length;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x004f, code lost:
        if (r1 >= r0) goto L_0x006e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0051, code lost:
        r2 = r9[r1];
        r4 = new android.content.Intent("android.telephony.action.AREA_INFO_UPDATED");
        r4.putExtra("android.telephony.extra.SLOT_INDEX", r8);
        r4.setPackage(r2);
        r7.mContext.sendBroadcastAsUser(r4, android.os.UserHandle.ALL, "android.permission.READ_PRIVILEGED_PHONE_STATE");
        r1 = r1 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x006e, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleAreaInfoMessage(int r8, android.telephony.SmsCbMessage r9) {
        /*
            r7 = this;
            int r0 = r9.getSubscriptionId()
            android.content.res.Resources r0 = r7.getResources(r0)
            r1 = 2130771969(0x7f010001, float:1.7147043E38)
            int[] r0 = r0.getIntArray(r1)
            java.util.stream.IntStream r0 = java.util.stream.IntStream.of(r0)
            com.android.cellbroadcastservice.-$$Lambda$GsmCellBroadcastHandler$K1ZBMJtOdICkNz47j1RBRovxFh8 r1 = new com.android.cellbroadcastservice.-$$Lambda$GsmCellBroadcastHandler$K1ZBMJtOdICkNz47j1RBRovxFh8
            r1.<init>(r9)
            boolean r0 = r0.anyMatch(r1)
            r1 = 0
            if (r0 == 0) goto L_0x0072
            android.util.SparseArray<java.lang.String> r0 = r7.mAreaInfos
            monitor-enter(r0)
            android.util.SparseArray<java.lang.String> r2 = r7.mAreaInfos     // Catch:{ all -> 0x006f }
            java.lang.Object r2 = r2.get(r8)     // Catch:{ all -> 0x006f }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x006f }
            java.lang.String r3 = r9.getMessageBody()     // Catch:{ all -> 0x006f }
            boolean r2 = android.text.TextUtils.equals(r2, r3)     // Catch:{ all -> 0x006f }
            r3 = 1
            if (r2 == 0) goto L_0x0037
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            return r3
        L_0x0037:
            android.util.SparseArray<java.lang.String> r2 = r7.mAreaInfos     // Catch:{ all -> 0x006f }
            java.lang.String r9 = r9.getMessageBody()     // Catch:{ all -> 0x006f }
            r2.put(r8, r9)     // Catch:{ all -> 0x006f }
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            android.content.Context r9 = r7.mContext
            android.content.res.Resources r9 = r9.getResources()
            r0 = 2130771970(0x7f010002, float:1.7147045E38)
            java.lang.String[] r9 = r9.getStringArray(r0)
            int r0 = r9.length
        L_0x004f:
            if (r1 >= r0) goto L_0x006e
            r2 = r9[r1]
            android.content.Intent r4 = new android.content.Intent
            java.lang.String r5 = "android.telephony.action.AREA_INFO_UPDATED"
            r4.<init>(r5)
            java.lang.String r5 = "android.telephony.extra.SLOT_INDEX"
            r4.putExtra(r5, r8)
            r4.setPackage(r2)
            android.content.Context r2 = r7.mContext
            android.os.UserHandle r5 = android.os.UserHandle.ALL
            java.lang.String r6 = "android.permission.READ_PRIVILEGED_PHONE_STATE"
            r2.sendBroadcastAsUser(r4, r5, r6)
            int r1 = r1 + 1
            goto L_0x004f
        L_0x006e:
            return r3
        L_0x006f:
            r7 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            throw r7
        L_0x0072:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastservice.GsmCellBroadcastHandler.handleAreaInfoMessage(int, android.telephony.SmsCbMessage):boolean");
    }

    static /* synthetic */ boolean lambda$handleAreaInfoMessage$1(SmsCbMessage smsCbMessage, int i) {
        return i == smsCbMessage.getServiceCategory();
    }

    /* access modifiers changed from: protected */
    public boolean handleSmsMessage(Message message) {
        int i = message.arg1;
        Object obj = message.obj;
        if (obj instanceof byte[]) {
            byte[] bArr = (byte[]) obj;
            SmsCbHeader createSmsCbHeader = createSmsCbHeader(bArr);
            if (createSmsCbHeader == null) {
                return false;
            }
            log("header=" + createSmsCbHeader);
            if (createSmsCbHeader.getServiceCategory() == 4400) {
                GsmSmsCbMessage.GeoFencingTriggerMessage createGeoFencingTriggerMessage = GsmSmsCbMessage.createGeoFencingTriggerMessage(bArr);
                if (createGeoFencingTriggerMessage != null) {
                    return handleGeoFencingTriggerMessage(createGeoFencingTriggerMessage, i);
                }
            } else {
                SmsCbMessage handleGsmBroadcastSms = handleGsmBroadcastSms(createSmsCbHeader, bArr, i);
                if (handleGsmBroadcastSms != null) {
                    if (isDuplicate(handleGsmBroadcastSms)) {
                        CellBroadcastStatsLog.write(278, 1, 1);
                        return false;
                    } else if (handleAreaInfoMessage(i, handleGsmBroadcastSms)) {
                        log("Channel " + handleGsmBroadcastSms.getServiceCategory() + " message processed");
                        CellBroadcastStatsLog.write(278, 1, 3);
                        return false;
                    } else {
                        handleBroadcastSms(handleGsmBroadcastSms);
                        return true;
                    }
                }
            }
        } else {
            String str = "handleSmsMessage for GSM got object of type: " + message.obj.getClass().getName();
            loge(str);
            CellBroadcastStatsLog.write(250, 12, str);
        }
        if (message.obj instanceof SmsCbMessage) {
            return super.handleSmsMessage(message);
        }
        return false;
    }

    /* access modifiers changed from: private */
    public Pair<Integer, Integer> getLacAndCid(CellIdentity cellIdentity) {
        int i;
        int i2;
        if (cellIdentity == null) {
            return null;
        }
        if (cellIdentity instanceof CellIdentityGsm) {
            CellIdentityGsm cellIdentityGsm = (CellIdentityGsm) cellIdentity;
            i2 = cellIdentityGsm.getLac();
            i = cellIdentityGsm.getCid();
        } else if (cellIdentity instanceof CellIdentityWcdma) {
            CellIdentityWcdma cellIdentityWcdma = (CellIdentityWcdma) cellIdentity;
            i2 = cellIdentityWcdma.getLac();
            i = cellIdentityWcdma.getCid();
        } else if (cellIdentity instanceof CellIdentityTdscdma) {
            CellIdentityTdscdma cellIdentityTdscdma = (CellIdentityTdscdma) cellIdentity;
            i2 = cellIdentityTdscdma.getLac();
            i = cellIdentityTdscdma.getCid();
        } else if (cellIdentity instanceof CellIdentityLte) {
            CellIdentityLte cellIdentityLte = (CellIdentityLte) cellIdentity;
            i2 = cellIdentityLte.getTac();
            i = cellIdentityLte.getCi();
        } else if (cellIdentity instanceof CellIdentityNr) {
            CellIdentityNr cellIdentityNr = (CellIdentityNr) cellIdentity;
            i2 = cellIdentityNr.getTac();
            i = cellIdentityNr.getPci();
        } else {
            i = Integer.MAX_VALUE;
            i2 = Integer.MAX_VALUE;
        }
        if (i2 == Integer.MAX_VALUE && i == Integer.MAX_VALUE) {
            return null;
        }
        return Pair.create(Integer.valueOf(i2), Integer.valueOf(i));
    }

    private Pair<Integer, Integer> getLacAndCid(int i) {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        telephonyManager.createForSubscriptionId(CellBroadcastHandler.getSubIdForPhone(this.mContext, i));
        ServiceState serviceState = telephonyManager.getServiceState();
        if (serviceState == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(1, 1);
        if (networkRegistrationInfo != null) {
            arrayList.add(networkRegistrationInfo.getCellIdentity());
        }
        NetworkRegistrationInfo networkRegistrationInfo2 = serviceState.getNetworkRegistrationInfo(2, 1);
        if (networkRegistrationInfo2 != null) {
            arrayList.add(networkRegistrationInfo2.getCellIdentity());
        }
        List<CellInfo> allCellInfo = telephonyManager.getAllCellInfo();
        if (allCellInfo != null) {
            arrayList.addAll((Collection) allCellInfo.stream().map($$Lambda$nL5gNcBw4HP0ZZKSqyzehOqju7M.INSTANCE).collect(Collectors.toList()));
        }
        return (Pair) arrayList.stream().map(new Function() {
            public final Object apply(Object obj) {
                return GsmCellBroadcastHandler.this.getLacAndCid((CellIdentity) obj);
            }
        }).filter($$Lambda$G2EHUJbGSxlOUsxs1bcY92HMtE.INSTANCE).findFirst().orElse((Object) null);
    }

    private SmsCbMessage handleGsmBroadcastSms(SmsCbHeader smsCbHeader, byte[] bArr, int i) {
        int i2;
        byte[][] bArr2;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
            telephonyManager.createForSubscriptionId(CellBroadcastHandler.getSubIdForPhone(this.mContext, i));
            String networkOperator = telephonyManager.getNetworkOperator();
            Pair<Integer, Integer> lacAndCid = getLacAndCid(i);
            int i3 = -1;
            if (lacAndCid != null) {
                i3 = ((Integer) lacAndCid.first).intValue();
                i2 = ((Integer) lacAndCid.second).intValue();
            } else {
                i2 = -1;
            }
            SmsCbLocation smsCbLocation = new SmsCbLocation(networkOperator, i3, i2);
            int numberOfPages = smsCbHeader.getNumberOfPages();
            if (numberOfPages > 1) {
                SmsCbConcatInfo smsCbConcatInfo = new SmsCbConcatInfo(smsCbHeader, smsCbLocation);
                bArr2 = this.mSmsCbPageMap.get(smsCbConcatInfo);
                if (bArr2 == null) {
                    bArr2 = new byte[numberOfPages][];
                    this.mSmsCbPageMap.put(smsCbConcatInfo, bArr2);
                }
                bArr2[smsCbHeader.getPageIndex() - 1] = bArr;
                for (byte[] bArr3 : bArr2) {
                    if (bArr3 == null) {
                        log("still missing pdu");
                        return null;
                    }
                }
                this.mSmsCbPageMap.remove(smsCbConcatInfo);
            } else {
                bArr2 = new byte[][]{bArr};
            }
            Iterator<SmsCbConcatInfo> it = this.mSmsCbPageMap.keySet().iterator();
            while (it.hasNext()) {
                if (!it.next().matchesLocation(networkOperator, i3, i2)) {
                    it.remove();
                }
            }
            return GsmSmsCbMessage.createSmsCbMessage(this.mContext, smsCbHeader, smsCbLocation, bArr2, i);
        } catch (RuntimeException e) {
            String str = "Error in decoding SMS CB pdu: " + e.toString();
            e.printStackTrace();
            loge(str);
            CellBroadcastStatsLog.write(250, 7, str);
            return null;
        }
    }

    private SmsCbHeader createSmsCbHeader(byte[] bArr) {
        try {
            return new SmsCbHeader(bArr);
        } catch (Exception e) {
            loge("Can't create SmsCbHeader, ex = " + e.toString());
            return null;
        }
    }

    private static final class SmsCbConcatInfo {
        private final SmsCbHeader mHeader;
        private final SmsCbLocation mLocation;

        SmsCbConcatInfo(SmsCbHeader smsCbHeader, SmsCbLocation smsCbLocation) {
            this.mHeader = smsCbHeader;
            this.mLocation = smsCbLocation;
        }

        public int hashCode() {
            return (this.mHeader.getSerialNumber() * 31) + this.mLocation.hashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof SmsCbConcatInfo)) {
                return false;
            }
            SmsCbConcatInfo smsCbConcatInfo = (SmsCbConcatInfo) obj;
            if (this.mHeader.getSerialNumber() != smsCbConcatInfo.mHeader.getSerialNumber() || !this.mLocation.equals(smsCbConcatInfo.mLocation)) {
                return false;
            }
            return true;
        }

        public boolean matchesLocation(String str, int i, int i2) {
            return this.mLocation.isInLocationArea(str, i, i2);
        }
    }
}

package com.android.cellbroadcastservice;

import android.content.Context;
import android.os.Bundle;
import android.telephony.CellBroadcastService;
import android.telephony.SmsCbEtwsInfo;
import android.telephony.SmsCbLocation;
import android.telephony.SmsCbMessage;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaSmsCbProgramData;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DefaultCellBroadcastService extends CellBroadcastService {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private CellBroadcastHandler mCdmaCellBroadcastHandler;
    private CdmaServiceCategoryProgramHandler mCdmaScpHandler;
    private GsmCellBroadcastHandler mGsmCellBroadcastHandler;

    public void onCreate() {
        DefaultCellBroadcastService.super.onCreate();
        this.mGsmCellBroadcastHandler = GsmCellBroadcastHandler.makeGsmCellBroadcastHandler(getApplicationContext());
        this.mCdmaCellBroadcastHandler = CellBroadcastHandler.makeCellBroadcastHandler(getApplicationContext());
        this.mCdmaScpHandler = CdmaServiceCategoryProgramHandler.makeScpHandler(getApplicationContext());
    }

    public void onDestroy() {
        this.mGsmCellBroadcastHandler.cleanup();
        this.mCdmaCellBroadcastHandler.cleanup();
        DefaultCellBroadcastService.super.onDestroy();
    }

    public void onGsmCellBroadcastSms(int i, byte[] bArr) {
        Log.d("DefaultCellBroadcastService", "onGsmCellBroadcastSms received message on slotId=" + i);
        CellBroadcastStatsLog.write(249, 1, 2);
        this.mGsmCellBroadcastHandler.onGsmCellBroadcastSms(i, bArr);
    }

    public void onCdmaCellBroadcastSms(int i, byte[] bArr, int i2) {
        String str;
        Log.d("DefaultCellBroadcastService", "onCdmaCellBroadcastSms received message on slotId=" + i);
        CellBroadcastStatsLog.write(249, 2, 2);
        int[] subscriptionIds = ((SubscriptionManager) getSystemService("telephony_subscription_service")).getSubscriptionIds(i);
        if (subscriptionIds == null || subscriptionIds.length <= 0) {
            str = "";
        } else {
            str = ((TelephonyManager) getSystemService("phone")).createForSubscriptionId(subscriptionIds[0]).getNetworkOperator();
        }
        SmsCbMessage parseCdmaBroadcastSms = parseCdmaBroadcastSms(getApplicationContext(), i, str, bArr, i2);
        if (parseCdmaBroadcastSms != null) {
            this.mCdmaCellBroadcastHandler.onCdmaCellBroadcastSms(parseCdmaBroadcastSms);
        }
    }

    public void onCdmaScpMessage(int i, List<CdmaSmsCbProgramData> list, String str, Consumer<Bundle> consumer) {
        Log.d("DefaultCellBroadcastService", "onCdmaScpMessage received message on slotId=" + i);
        CellBroadcastStatsLog.write(249, 3, 2);
        this.mCdmaScpHandler.onCdmaScpMessage(i, new ArrayList(list), str, consumer);
    }

    public String getCellBroadcastAreaInfo(int i) {
        Log.d("DefaultCellBroadcastService", "getCellBroadcastAreaInfo on slotId=" + i);
        return this.mGsmCellBroadcastHandler.getCellBroadcastAreaInfo(i);
    }

    public static SmsCbMessage parseCdmaBroadcastSms(Context context, int i, String str, byte[] bArr, int i2) {
        Context context2 = context;
        byte[] bArr2 = bArr;
        try {
            BearerData decode = BearerData.decode(context2, bArr2, i2);
            Log.d("DefaultCellBroadcastService", "MT raw BearerData = " + toHexString(bArr2, 0, bArr2.length));
            SmsCbLocation smsCbLocation = new SmsCbLocation(str, -1, -1);
            int[] subscriptionIds = ((SubscriptionManager) context2.getSystemService("telephony_subscription_service")).getSubscriptionIds(i);
            int i3 = (subscriptionIds == null || subscriptionIds.length <= 0) ? Integer.MAX_VALUE : subscriptionIds[0];
            int i4 = decode.messageId;
            String language = decode.getLanguage();
            UserData userData = decode.userData;
            return new SmsCbMessage(2, 1, i4, smsCbLocation, i2, language, userData.msgEncoding, userData.payloadStr, decode.priority, (SmsCbEtwsInfo) null, decode.cmasWarningInfo, 0, (List) null, System.currentTimeMillis(), i, i3);
        } catch (Exception e) {
            Exception exc = e;
            String str2 = "Error decoding bearer data e=" + exc.toString();
            Log.e("DefaultCellBroadcastService", str2);
            CellBroadcastStatsLog.write(250, 1, str2);
            return null;
        }
    }

    private static String toHexString(byte[] bArr, int i, int i2) {
        char[] cArr = new char[(i2 * 2)];
        int i3 = 0;
        for (int i4 = i; i4 < i + i2; i4++) {
            byte b = bArr[i4];
            int i5 = i3 + 1;
            char[] cArr2 = HEX_DIGITS;
            cArr[i3] = cArr2[(b >>> 4) & 15];
            i3 = i5 + 1;
            cArr[i5] = cArr2[b & 15];
        }
        return new String(cArr);
    }
}

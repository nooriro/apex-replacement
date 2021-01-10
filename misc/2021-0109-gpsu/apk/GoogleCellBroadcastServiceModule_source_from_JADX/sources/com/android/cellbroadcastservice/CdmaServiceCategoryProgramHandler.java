package com.android.cellbroadcastservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.telephony.cdma.CdmaSmsCbProgramData;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public final class CdmaServiceCategoryProgramHandler extends WakeLockStateMachine {
    /* access modifiers changed from: private */
    public ConcurrentLinkedQueue<Consumer<Bundle>> mScpCallback = new ConcurrentLinkedQueue<>();
    private final BroadcastReceiver mScpResultsReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int resultCode = getResultCode();
            if (resultCode == -1 || resultCode == 1) {
                ((Consumer) CdmaServiceCategoryProgramHandler.this.mScpCallback.poll()).accept(getResultExtras(false));
                if (WakeLockStateMachine.DBG) {
                    CdmaServiceCategoryProgramHandler.this.log("mScpResultsReceiver finished");
                }
                if (CdmaServiceCategoryProgramHandler.this.mReceiverCount.decrementAndGet() == 0) {
                    CdmaServiceCategoryProgramHandler.this.sendMessage(2);
                    return;
                }
                return;
            }
            String str = "SCP results error: result code = " + resultCode;
            CdmaServiceCategoryProgramHandler.this.loge(str);
            CellBroadcastStatsLog.write(250, 3, str);
        }
    };

    CdmaServiceCategoryProgramHandler(Context context) {
        super("CdmaServiceCategoryProgramHandler", context, Looper.myLooper());
        this.mContext = context;
    }

    static CdmaServiceCategoryProgramHandler makeScpHandler(Context context) {
        CdmaServiceCategoryProgramHandler cdmaServiceCategoryProgramHandler = new CdmaServiceCategoryProgramHandler(context);
        cdmaServiceCategoryProgramHandler.start();
        return cdmaServiceCategoryProgramHandler;
    }

    public void onCdmaScpMessage(int i, ArrayList<CdmaSmsCbProgramData> arrayList, String str, Consumer<Bundle> consumer) {
        onCdmaCellBroadcastSms(new CdmaScpMessage(this, i, arrayList, str, consumer));
    }

    private class CdmaScpMessage {
        Consumer<Bundle> mCallback;
        String mOriginatingAddress;
        ArrayList<CdmaSmsCbProgramData> mProgamData;
        int mSlotIndex;

        CdmaScpMessage(CdmaServiceCategoryProgramHandler cdmaServiceCategoryProgramHandler, int i, ArrayList<CdmaSmsCbProgramData> arrayList, String str, Consumer<Bundle> consumer) {
            this.mSlotIndex = i;
            this.mProgamData = arrayList;
            this.mOriginatingAddress = str;
            this.mCallback = consumer;
        }
    }

    /* access modifiers changed from: protected */
    public boolean handleSmsMessage(Message message) {
        Object obj = message.obj;
        if (obj instanceof CdmaScpMessage) {
            CdmaScpMessage cdmaScpMessage = (CdmaScpMessage) obj;
            return handleServiceCategoryProgramData(cdmaScpMessage.mProgamData, cdmaScpMessage.mOriginatingAddress, cdmaScpMessage.mSlotIndex, cdmaScpMessage.mCallback);
        }
        String str = "handleMessage got object of type: " + message.obj.getClass().getName();
        loge(str);
        CellBroadcastStatsLog.write(250, 14, str);
        return false;
    }

    private boolean handleServiceCategoryProgramData(ArrayList<CdmaSmsCbProgramData> arrayList, String str, int i, Consumer<Bundle> consumer) {
        if (arrayList == null) {
            loge("handleServiceCategoryProgramData: program data list is null!");
            CellBroadcastStatsLog.write(250, 2, "handleServiceCategoryProgramData: program data list is null!");
            return false;
        }
        Intent intent = new Intent("android.provider.Telephony.SMS_SERVICE_CATEGORY_PROGRAM_DATA_RECEIVED");
        intent.putExtra("sender", str);
        intent.putParcelableArrayListExtra("program_data", arrayList);
        CellBroadcastHandler.putPhoneIdAndSubIdExtra(this.mContext, intent, i);
        String defaultCBRPackageName = CellBroadcastHandler.getDefaultCBRPackageName(this.mContext, intent);
        this.mReceiverCount.incrementAndGet();
        intent.setPackage(defaultCBRPackageName);
        this.mContext.sendOrderedBroadcast(intent, "android.permission.RECEIVE_SMS", "android:receive_sms", this.mScpResultsReceiver, getHandler(), -1, (String) null, (Bundle) null);
        this.mScpCallback.add(consumer);
        return true;
    }
}

package com.android.cellbroadcastreceiver;

import android.app.Application;
import android.telephony.SmsCbMessage;
import android.util.Log;
import java.util.ArrayList;
import java.util.function.Predicate;

public class CellBroadcastReceiverApp extends Application {
    private static final boolean VDBG = Log.isLoggable("CellBroadcastReceiverApp", 2);
    private static final ArrayList<SmsCbMessage> sNewMessageList = new ArrayList<>(4);

    static ArrayList<SmsCbMessage> addNewMessageToList(SmsCbMessage smsCbMessage) {
        if (VDBG) {
            Log.v("CellBroadcastReceiverApp", "addNewMessageToList: " + smsCbMessage);
        }
        sNewMessageList.add(smsCbMessage);
        return sNewMessageList;
    }

    static void clearNewMessageList() {
        if (VDBG) {
            Log.v("CellBroadcastReceiverApp", "clearNewMessageList");
        }
        sNewMessageList.clear();
    }

    static /* synthetic */ boolean lambda$removeReadMessage$0(SmsCbMessage smsCbMessage, SmsCbMessage smsCbMessage2) {
        return smsCbMessage2.getReceivedTime() == smsCbMessage.getReceivedTime();
    }

    static ArrayList<SmsCbMessage> removeReadMessage(SmsCbMessage smsCbMessage) {
        if (sNewMessageList.removeIf(new Predicate(smsCbMessage) {
            public final /* synthetic */ SmsCbMessage f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return CellBroadcastReceiverApp.lambda$removeReadMessage$0(this.f$0, (SmsCbMessage) obj);
            }
        })) {
            if (VDBG) {
                Log.v("CellBroadcastReceiverApp", "removeReadMessage succeed, msg: " + smsCbMessage);
            }
        } else if (VDBG) {
            Log.v("CellBroadcastReceiverApp", "removeReadMessage failed, no matching message: " + smsCbMessage);
        }
        return sNewMessageList;
    }

    static SmsCbMessage getLatestMessage() {
        if (sNewMessageList.isEmpty()) {
            return null;
        }
        ArrayList<SmsCbMessage> arrayList = sNewMessageList;
        return arrayList.get(arrayList.size() - 1);
    }

    static ArrayList<SmsCbMessage> getNewMessageList() {
        return sNewMessageList;
    }
}

package com.android.cellbroadcastreceiver;

import android.app.Application;
import android.telephony.SmsCbMessage;
import java.util.ArrayList;

public class CellBroadcastReceiverApp extends Application {
    private static final ArrayList<SmsCbMessage> sNewMessageList = new ArrayList<>(4);

    static ArrayList<SmsCbMessage> addNewMessageToList(SmsCbMessage smsCbMessage) {
        sNewMessageList.add(smsCbMessage);
        return sNewMessageList;
    }

    static void clearNewMessageList() {
        sNewMessageList.clear();
    }

    static ArrayList<SmsCbMessage> getNewMessageList() {
        return sNewMessageList;
    }
}

package com.android.cellbroadcastservice;

import android.util.StatsEvent;
import android.util.StatsLog;

public class CellBroadcastStatsLog {
    public static void write(int i, int i2, int i3) {
        StatsEvent.Builder newBuilder = StatsEvent.newBuilder();
        newBuilder.setAtomId(i);
        newBuilder.writeInt(i2);
        newBuilder.writeInt(i3);
        newBuilder.usePooledBuffer();
        StatsLog.write(newBuilder.build());
    }
}

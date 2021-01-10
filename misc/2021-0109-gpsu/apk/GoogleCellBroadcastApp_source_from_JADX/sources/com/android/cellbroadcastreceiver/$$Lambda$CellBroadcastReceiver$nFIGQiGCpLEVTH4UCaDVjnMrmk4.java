package com.android.cellbroadcastreceiver;

import android.os.Bundle;
import com.android.cellbroadcastreceiver.CellBroadcastContentProvider;

/* renamed from: com.android.cellbroadcastreceiver.-$$Lambda$CellBroadcastReceiver$nFIGQiGCpLEVTH4UCaDVjnMrmk4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$CellBroadcastReceiver$nFIGQiGCpLEVTH4UCaDVjnMrmk4 implements CellBroadcastContentProvider.CellBroadcastOperation {
    public static final /* synthetic */ $$Lambda$CellBroadcastReceiver$nFIGQiGCpLEVTH4UCaDVjnMrmk4 INSTANCE = new $$Lambda$CellBroadcastReceiver$nFIGQiGCpLEVTH4UCaDVjnMrmk4();

    private /* synthetic */ $$Lambda$CellBroadcastReceiver$nFIGQiGCpLEVTH4UCaDVjnMrmk4() {
    }

    public final boolean execute(CellBroadcastContentProvider cellBroadcastContentProvider) {
        return cellBroadcastContentProvider.call("migrate-legacy-data", (String) null, (Bundle) null);
    }
}

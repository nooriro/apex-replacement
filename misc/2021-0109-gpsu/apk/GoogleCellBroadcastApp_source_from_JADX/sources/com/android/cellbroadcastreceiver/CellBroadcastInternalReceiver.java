package com.android.cellbroadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.cellbroadcastreceiver.CellBroadcastContentProvider;

public class CellBroadcastInternalReceiver extends BroadcastReceiver {
    public void getCellBroadcastTask(Context context, final long j) {
        new CellBroadcastContentProvider.AsyncCellBroadcastTask(context.getContentResolver()).execute(new CellBroadcastContentProvider.CellBroadcastOperation[]{new CellBroadcastContentProvider.CellBroadcastOperation(this) {
            public boolean execute(CellBroadcastContentProvider cellBroadcastContentProvider) {
                return cellBroadcastContentProvider.markBroadcastRead("date", j);
            }
        }});
    }

    public void onReceive(Context context, Intent intent) {
        if ("com.android.cellbroadcastreceiver.intent.action.MARK_AS_READ".equals(intent.getAction())) {
            getCellBroadcastTask(context, intent.getLongExtra("com.android.cellbroadcastreceiver.intent.extra.ID", -1));
        }
    }
}

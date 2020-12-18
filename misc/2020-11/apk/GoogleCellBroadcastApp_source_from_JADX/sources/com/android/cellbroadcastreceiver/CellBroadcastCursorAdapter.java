package com.android.cellbroadcastreceiver;

import android.content.Context;
import android.database.Cursor;
import android.telephony.SmsCbCmasInfo;
import android.telephony.SmsCbEtwsInfo;
import android.telephony.SmsCbLocation;
import android.telephony.SmsCbMessage;
import android.telephony.SubscriptionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import com.android.cellbroadcastreceiver.module.R;
import java.util.List;

public class CellBroadcastCursorAdapter extends CursorAdapter {
    public CellBroadcastCursorAdapter(Context context) {
        super(context, (Cursor) null, 0);
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        SmsCbMessage createFromCursor = createFromCursor(context, cursor);
        CellBroadcastListItem cellBroadcastListItem = (CellBroadcastListItem) LayoutInflater.from(context).inflate(R.layout.cell_broadcast_list_item, viewGroup, false);
        cellBroadcastListItem.bind(createFromCursor);
        return cellBroadcastListItem;
    }

    static SmsCbMessage createFromCursor(Context context, Cursor cursor) {
        SmsCbCmasInfo smsCbCmasInfo;
        Cursor cursor2 = cursor;
        int i = cursor2.getInt(cursor2.getColumnIndexOrThrow("geo_scope"));
        int i2 = cursor2.getInt(cursor2.getColumnIndexOrThrow("serial_number"));
        int i3 = cursor2.getInt(cursor2.getColumnIndexOrThrow("service_category"));
        String string = cursor2.getString(cursor2.getColumnIndexOrThrow("language"));
        String string2 = cursor2.getString(cursor2.getColumnIndexOrThrow("body"));
        int i4 = cursor2.getInt(cursor2.getColumnIndexOrThrow("format"));
        int i5 = cursor2.getInt(cursor2.getColumnIndexOrThrow("priority"));
        int i6 = cursor2.getInt(cursor2.getColumnIndexOrThrow("slot_index"));
        int columnIndex = cursor2.getColumnIndex("plmn");
        String str = null;
        int i7 = -1;
        String string3 = (columnIndex == -1 || cursor2.isNull(columnIndex)) ? null : cursor2.getString(columnIndex);
        int columnIndex2 = cursor2.getColumnIndex("lac");
        int i8 = (columnIndex2 == -1 || cursor2.isNull(columnIndex2)) ? -1 : cursor2.getInt(columnIndex2);
        int columnIndex3 = cursor2.getColumnIndex("cid");
        SmsCbLocation smsCbLocation = new SmsCbLocation(string3, i8, (columnIndex3 == -1 || cursor2.isNull(columnIndex3)) ? -1 : cursor2.getInt(columnIndex3));
        int columnIndex4 = cursor2.getColumnIndex("etws_warning_type");
        SmsCbEtwsInfo smsCbEtwsInfo = (columnIndex4 == -1 || cursor2.isNull(columnIndex4)) ? null : new SmsCbEtwsInfo(cursor2.getInt(columnIndex4), false, false, false, (byte[]) null);
        int columnIndex5 = cursor2.getColumnIndex("cmas_message_class");
        if (columnIndex5 == -1 || cursor2.isNull(columnIndex5)) {
            smsCbCmasInfo = null;
        } else {
            int i9 = cursor2.getInt(columnIndex5);
            int columnIndex6 = cursor2.getColumnIndex("cmas_category");
            int i10 = (columnIndex6 == -1 || cursor2.isNull(columnIndex6)) ? -1 : cursor2.getInt(columnIndex6);
            int columnIndex7 = cursor2.getColumnIndex("cmas_response_type");
            int i11 = (columnIndex7 == -1 || cursor2.isNull(columnIndex7)) ? -1 : cursor2.getInt(columnIndex7);
            int columnIndex8 = cursor2.getColumnIndex("cmas_severity");
            int i12 = (columnIndex8 == -1 || cursor2.isNull(columnIndex8)) ? -1 : cursor2.getInt(columnIndex8);
            int columnIndex9 = cursor2.getColumnIndex("cmas_urgency");
            int i13 = (columnIndex9 == -1 || cursor2.isNull(columnIndex9)) ? -1 : cursor2.getInt(columnIndex9);
            int columnIndex10 = cursor2.getColumnIndex("cmas_certainty");
            if (columnIndex10 != -1 && !cursor2.isNull(columnIndex10)) {
                i7 = cursor2.getInt(columnIndex10);
            }
            smsCbCmasInfo = new SmsCbCmasInfo(i9, i10, i11, i12, i13, i7);
        }
        if (cursor2.getColumnIndex("date") >= 0) {
            str = "date";
        } else if (cursor2.getColumnIndex("received_time") >= 0) {
            str = "received_time";
        }
        long j = cursor2.getLong(cursor2.getColumnIndexOrThrow(str));
        int i14 = cursor2.getColumnIndex("dcs") >= 0 ? cursor2.getInt(cursor2.getColumnIndexOrThrow("dcs")) : 0;
        int[] subscriptionIds = ((SubscriptionManager) context.getSystemService("telephony_subscription_service")).getSubscriptionIds(i6);
        return new SmsCbMessage(i4, i, i2, smsCbLocation, i3, string, i14, string2, i5, smsCbEtwsInfo, smsCbCmasInfo, cursor2.getColumnIndex("maximum_wait_time") >= 0 ? cursor2.getInt(cursor2.getColumnIndexOrThrow("maximum_wait_time")) : 0, (List) null, j, i6, (subscriptionIds == null || subscriptionIds.length <= 0) ? Integer.MAX_VALUE : subscriptionIds[0]);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        ((CellBroadcastListItem) view).bind(createFromCursor(context, cursor));
    }
}

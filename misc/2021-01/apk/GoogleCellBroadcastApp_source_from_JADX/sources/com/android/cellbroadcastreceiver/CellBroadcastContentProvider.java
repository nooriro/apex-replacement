package com.android.cellbroadcastreceiver;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsCbCmasInfo;
import android.telephony.SmsCbEtwsInfo;
import android.telephony.SmsCbLocation;
import android.telephony.SmsCbMessage;
import android.text.TextUtils;
import android.util.Log;

public class CellBroadcastContentProvider extends ContentProvider {
    static final Uri CONTENT_URI = Uri.parse("content://cellbroadcasts-app/");
    private static final UriMatcher sUriMatcher = new UriMatcher(-1);
    public CellBroadcastDatabaseHelper mOpenHelper;

    interface CellBroadcastOperation {
        boolean execute(CellBroadcastContentProvider cellBroadcastContentProvider);
    }

    static {
        sUriMatcher.addURI("cellbroadcasts-app", (String) null, 0);
        sUriMatcher.addURI("cellbroadcasts-app", "#", 1);
    }

    public boolean onCreate() {
        CellBroadcastDatabaseHelper cellBroadcastDatabaseHelper = new CellBroadcastDatabaseHelper(getContext(), false);
        this.mOpenHelper = cellBroadcastDatabaseHelper;
        cellBroadcastDatabaseHelper.getReadableDatabase();
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables(CellBroadcastDatabaseHelper.TABLE_NAME);
        int match = sUriMatcher.match(uri);
        if (match != 0) {
            if (match == 1) {
                sQLiteQueryBuilder.appendWhere("(_id=" + uri.getPathSegments().get(0) + ')');
            } else {
                Log.e("CellBroadcastContentProvider", "Invalid query: " + uri);
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        if (TextUtils.isEmpty(str2)) {
            str2 = "date DESC";
        }
        Cursor query = sQLiteQueryBuilder.query(this.mOpenHelper.getReadableDatabase(), strArr, str, strArr2, (String) null, (String) null, str2);
        if (query != null) {
            query.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
        }
        return query;
    }

    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        if (match == 0) {
            return "vnd.android.cursor.dir/cellbroadcast";
        }
        if (match != 1) {
            return null;
        }
        return "vnd.android.cursor.item/cellbroadcast";
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("insert not supported");
    }

    public int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException("delete not supported");
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException("update not supported");
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        Log.d("CellBroadcastContentProvider", "call: method=" + str + " name=" + str2 + " args=" + bundle);
        if (!"migrate-legacy-data".equals(str)) {
            return null;
        }
        CellBroadcastDatabaseHelper cellBroadcastDatabaseHelper = this.mOpenHelper;
        cellBroadcastDatabaseHelper.migrateFromLegacyIfNeeded(cellBroadcastDatabaseHelper.getReadableDatabase());
        return null;
    }

    private ContentValues getContentValues(SmsCbMessage smsCbMessage) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("slot_index", Integer.valueOf(smsCbMessage.getSlotIndex()));
        contentValues.put("geo_scope", Integer.valueOf(smsCbMessage.getGeographicalScope()));
        SmsCbLocation location = smsCbMessage.getLocation();
        contentValues.put("plmn", location.getPlmn());
        if (location.getLac() != -1) {
            contentValues.put("lac", Integer.valueOf(location.getLac()));
        }
        if (location.getCid() != -1) {
            contentValues.put("cid", Integer.valueOf(location.getCid()));
        }
        contentValues.put("serial_number", Integer.valueOf(smsCbMessage.getSerialNumber()));
        contentValues.put("service_category", Integer.valueOf(smsCbMessage.getServiceCategory()));
        contentValues.put("language", smsCbMessage.getLanguageCode());
        contentValues.put("body", smsCbMessage.getMessageBody());
        contentValues.put("date", Long.valueOf(smsCbMessage.getReceivedTime()));
        contentValues.put("format", Integer.valueOf(smsCbMessage.getMessageFormat()));
        contentValues.put("priority", Integer.valueOf(smsCbMessage.getMessagePriority()));
        SmsCbEtwsInfo etwsWarningInfo = smsCbMessage.getEtwsWarningInfo();
        if (etwsWarningInfo != null) {
            contentValues.put("etws_warning_type", Integer.valueOf(etwsWarningInfo.getWarningType()));
        }
        SmsCbCmasInfo cmasWarningInfo = smsCbMessage.getCmasWarningInfo();
        if (cmasWarningInfo != null) {
            contentValues.put("cmas_message_class", Integer.valueOf(cmasWarningInfo.getMessageClass()));
            contentValues.put("cmas_category", Integer.valueOf(cmasWarningInfo.getCategory()));
            contentValues.put("cmas_response_type", Integer.valueOf(cmasWarningInfo.getResponseType()));
            contentValues.put("cmas_severity", Integer.valueOf(cmasWarningInfo.getSeverity()));
            contentValues.put("cmas_urgency", Integer.valueOf(cmasWarningInfo.getUrgency()));
            contentValues.put("cmas_certainty", Integer.valueOf(cmasWarningInfo.getCertainty()));
        }
        return contentValues;
    }

    public boolean insertNewBroadcast(SmsCbMessage smsCbMessage) {
        if (this.mOpenHelper.getWritableDatabase().insert(CellBroadcastDatabaseHelper.TABLE_NAME, (String) null, getContentValues(smsCbMessage)) != -1) {
            return true;
        }
        Log.e("CellBroadcastContentProvider", "failed to insert new broadcast into database");
        return true;
    }

    public boolean deleteBroadcast(long j) {
        if (this.mOpenHelper.getWritableDatabase().delete(CellBroadcastDatabaseHelper.TABLE_NAME, "_id=?", new String[]{Long.toString(j)}) != 0) {
            return true;
        }
        Log.e("CellBroadcastContentProvider", "failed to delete broadcast at row " + j);
        return false;
    }

    public boolean deleteAllBroadcasts() {
        if (this.mOpenHelper.getWritableDatabase().delete(CellBroadcastDatabaseHelper.TABLE_NAME, (String) null, (String[]) null) != 0) {
            return true;
        }
        Log.e("CellBroadcastContentProvider", "failed to delete all broadcasts");
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean markBroadcastRead(String str, long j) {
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("read", 1);
        if (writableDatabase.update(CellBroadcastDatabaseHelper.TABLE_NAME, contentValues, str + "=?", new String[]{Long.toString(j)}) != 0) {
            return true;
        }
        Log.e("CellBroadcastContentProvider", "failed to mark broadcast read: " + str + " = " + j);
        return false;
    }

    static class AsyncCellBroadcastTask extends AsyncTask<CellBroadcastOperation, Void, Void> {
        private ContentResolver mContentResolver;

        AsyncCellBroadcastTask(ContentResolver contentResolver) {
            this.mContentResolver = contentResolver;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(CellBroadcastOperation... cellBroadcastOperationArr) {
            ContentProviderClient acquireContentProviderClient = this.mContentResolver.acquireContentProviderClient("cellbroadcasts-app");
            CellBroadcastContentProvider cellBroadcastContentProvider = (CellBroadcastContentProvider) acquireContentProviderClient.getLocalContentProvider();
            if (cellBroadcastContentProvider != null) {
                try {
                    if (cellBroadcastOperationArr[0].execute(cellBroadcastContentProvider)) {
                        Log.d("CellBroadcastContentProvider", "database changed: notifying observers...");
                        this.mContentResolver.notifyChange(CellBroadcastContentProvider.CONTENT_URI, (ContentObserver) null, false);
                    }
                } finally {
                    acquireContentProviderClient.release();
                }
            } else {
                Log.e("CellBroadcastContentProvider", "getLocalContentProvider() returned null");
            }
            this.mContentResolver = null;
            return null;
        }
    }
}

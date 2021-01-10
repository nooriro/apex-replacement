package com.android.cellbroadcastreceiver;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Telephony;
import android.telephony.SmsCbCmasInfo;
import android.telephony.SmsCbEtwsInfo;
import android.telephony.SmsCbLocation;
import android.telephony.SmsCbMessage;
import android.text.TextUtils;
import android.util.Log;
import com.android.cellbroadcastreceiver.module.R;
import java.util.concurrent.CountDownLatch;

public class CellBroadcastContentProvider extends ContentProvider {
    public static final String CB_AUTHORITY = "cellbroadcasts-app";
    static final Uri CONTENT_URI = Uri.parse("content://cellbroadcasts-app/");
    private static final UriMatcher sUriMatcher = new UriMatcher(-1);
    private final CountDownLatch mInitializedLatch = new CountDownLatch(1);
    public CellBroadcastDatabaseHelper mOpenHelper;

    interface CellBroadcastOperation {
        boolean execute(CellBroadcastContentProvider cellBroadcastContentProvider);
    }

    static {
        sUriMatcher.addURI(CB_AUTHORITY, (String) null, 0);
        sUriMatcher.addURI(CB_AUTHORITY, "#", 1);
    }

    public boolean onCreate() {
        this.mOpenHelper = new CellBroadcastDatabaseHelper(getContext(), false);
        new Thread(new Runnable() {
            public final void run() {
                CellBroadcastContentProvider.this.lambda$onCreate$0$CellBroadcastContentProvider();
            }
        }).start();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$CellBroadcastContentProvider() {
        this.mOpenHelper.getReadableDatabase();
        this.mInitializedLatch.countDown();
    }

    /* access modifiers changed from: protected */
    public SQLiteDatabase awaitInitAndGetWritableDatabase() {
        while (this.mInitializedLatch.getCount() != 0) {
            try {
                this.mInitializedLatch.await();
            } catch (InterruptedException e) {
                Log.e("CellBroadcastContentProvider", "Interrupted while waiting for db initialization. e=" + e);
            }
        }
        return this.mOpenHelper.getWritableDatabase();
    }

    /* access modifiers changed from: protected */
    public SQLiteDatabase awaitInitAndGetReadableDatabase() {
        while (this.mInitializedLatch.getCount() != 0) {
            try {
                this.mInitializedLatch.await();
            } catch (InterruptedException e) {
                Log.e("CellBroadcastContentProvider", "Interrupted while waiting for db initialization. e=" + e);
            }
        }
        return this.mOpenHelper.getReadableDatabase();
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
        Cursor query = sQLiteQueryBuilder.query(awaitInitAndGetReadableDatabase(), strArr, str, strArr2, (String) null, (String) null, str2);
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
        this.mOpenHelper.migrateFromLegacyIfNeeded(awaitInitAndGetReadableDatabase());
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
        if (awaitInitAndGetWritableDatabase().insert(CellBroadcastDatabaseHelper.TABLE_NAME, (String) null, getContentValues(smsCbMessage)) != -1) {
            return true;
        }
        Log.e("CellBroadcastContentProvider", "failed to insert new broadcast into database");
        return true;
    }

    public boolean deleteBroadcast(long j) {
        if (awaitInitAndGetWritableDatabase().delete(CellBroadcastDatabaseHelper.TABLE_NAME, "_id=?", new String[]{Long.toString(j)}) != 0) {
            return true;
        }
        Log.e("CellBroadcastContentProvider", "failed to delete broadcast at row " + j);
        return false;
    }

    public boolean deleteAllBroadcasts() {
        if (awaitInitAndGetWritableDatabase().delete(CellBroadcastDatabaseHelper.TABLE_NAME, (String) null, (String[]) null) != 0) {
            return true;
        }
        Log.e("CellBroadcastContentProvider", "failed to delete all broadcasts");
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean markBroadcastRead(String str, long j) {
        SQLiteDatabase awaitInitAndGetWritableDatabase = awaitInitAndGetWritableDatabase();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("read", 1);
        if (awaitInitAndGetWritableDatabase.update(CellBroadcastDatabaseHelper.TABLE_NAME, contentValues, str + "=?", new String[]{Long.toString(j)}) != 0) {
            return true;
        }
        Log.e("CellBroadcastContentProvider", "failed to mark broadcast read: " + str + " = " + j);
        return false;
    }

    public boolean markBroadcastSmsSyncPending(String str, long j, boolean z) {
        SQLiteDatabase awaitInitAndGetWritableDatabase = awaitInitAndGetWritableDatabase();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("isSmsSyncPending", Integer.valueOf(z ? 1 : 0));
        if (awaitInitAndGetWritableDatabase.update(CellBroadcastDatabaseHelper.TABLE_NAME, contentValues, str + "=?", new String[]{Long.toString(j)}) != 0) {
            return true;
        }
        Log.e("CellBroadcastContentProvider", "failed to mark broadcast pending for sms inbox sync:  " + z + " where: " + str + " = " + j);
        return false;
    }

    public void resyncToSmsInbox(Context context) {
        Cursor query = query(CONTENT_URI, CellBroadcastDatabaseHelper.QUERY_COLUMNS, "isSmsSyncPending=1", (String[]) null, (String) null);
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    SmsCbMessage createFromCursor = CellBroadcastCursorAdapter.createFromCursor(context, query);
                    if (createFromCursor != null) {
                        Log.d("CellBroadcastContentProvider", "handling message received pending for sms sync: " + createFromCursor.toString());
                        writeMessageToSmsInbox(createFromCursor, context);
                        markBroadcastSmsSyncPending("date", createFromCursor.getReceivedTime(), false);
                    }
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            }
        }
        if (query != null) {
            query.close();
            return;
        }
        return;
        throw th;
    }

    public void writeMessageToSmsInbox(SmsCbMessage smsCbMessage, Context context) {
        UserManager userManager = (UserManager) context.getSystemService("user");
        if (!userManager.isSystemUser()) {
            Log.d("CellBroadcastContentProvider", "ignoring writeMessageToSmsInbox due to non-system user");
        } else if (!userManager.isUserUnlocked()) {
            Log.d("CellBroadcastContentProvider", "ignoring writeMessageToSmsInbox due to direct boot mode");
            markBroadcastSmsSyncPending("date", smsCbMessage.getReceivedTime(), true);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("body", smsCbMessage.getMessageBody());
            contentValues.put("date", Long.valueOf(smsCbMessage.getReceivedTime()));
            contentValues.put("sub_id", Integer.valueOf(smsCbMessage.getSubscriptionId()));
            contentValues.put("subject", context.getString(CellBroadcastResources.getDialogTitleResource(context, smsCbMessage)));
            contentValues.put("address", CellBroadcastResources.getSmsSenderAddressResourceEnglishString(context, smsCbMessage));
            contentValues.put("thread_id", Long.valueOf(Telephony.Threads.getOrCreateThreadId(context, CellBroadcastResources.getSmsSenderAddressResourceEnglishString(context, smsCbMessage))));
            if (CellBroadcastSettings.getResources(context, smsCbMessage.getSubscriptionId()).getBoolean(R.bool.always_mark_sms_read)) {
                contentValues.put("read", 1);
            }
            Uri insert = context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, contentValues);
            if (insert == null) {
                Log.e("CellBroadcastContentProvider", "writeMessageToSmsInbox: failed");
                return;
            }
            Log.d("CellBroadcastContentProvider", "writeMessageToSmsInbox: succeed uri = " + insert);
        }
    }

    static class AsyncCellBroadcastTask extends AsyncTask<CellBroadcastOperation, Void, Void> {
        private ContentResolver mContentResolver;

        AsyncCellBroadcastTask(ContentResolver contentResolver) {
            this.mContentResolver = contentResolver;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(CellBroadcastOperation... cellBroadcastOperationArr) {
            ContentProviderClient acquireContentProviderClient = this.mContentResolver.acquireContentProviderClient(CellBroadcastContentProvider.CB_AUTHORITY);
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

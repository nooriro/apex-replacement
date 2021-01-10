package com.android.cellbroadcastservice;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import java.util.Arrays;

public class CellBroadcastProvider extends ContentProvider {
    public static final String AUTHORITY = "cellbroadcasts";
    public static final String CELL_BROADCASTS_TABLE_NAME = "cell_broadcasts";
    public static final Uri CONTENT_URI = Uri.parse("content://cellbroadcasts");
    public static final int DATABASE_VERSION = 4;
    /* access modifiers changed from: private */
    public static final boolean DBG;
    public static final String[] QUERY_COLUMNS = {"_id", "slot_index", "sub_id", "geo_scope", "plmn", "lac", "cid", "serial_number", "service_category", "language", "dcs", "body", "format", "priority", "etws_warning_type", "etws_is_primary", "cmas_message_class", "cmas_category", "cmas_response_type", "cmas_severity", "cmas_urgency", "cmas_certainty", "received_time", "location_check_time", "message_broadcasted", "message_displayed", "geometries", "maximum_wait_time"};
    /* access modifiers changed from: private */
    public static final String TAG;
    private static final UriMatcher sUriMatcher = new UriMatcher(-1);
    public SQLiteOpenHelper mDbHelper;
    public CellBroadcastPermissionChecker mPermissionChecker;

    static {
        String simpleName = CellBroadcastProvider.class.getSimpleName();
        TAG = simpleName;
        DBG = Log.isLoggable(simpleName, 3);
        sUriMatcher.addURI(AUTHORITY, (String) null, 0);
        sUriMatcher.addURI(AUTHORITY, "history", 1);
        sUriMatcher.addURI(AUTHORITY, "displayed", 2);
    }

    public CellBroadcastProvider() {
    }

    public CellBroadcastProvider(CellBroadcastPermissionChecker cellBroadcastPermissionChecker) {
        this.mPermissionChecker = cellBroadcastPermissionChecker;
    }

    public boolean onCreate() {
        this.mDbHelper = new CellBroadcastDatabaseHelper(getContext());
        this.mPermissionChecker = new CellBroadcastPermissionChecker();
        return true;
    }

    public String getType(Uri uri) {
        if (sUriMatcher.match(uri) != 0) {
            return null;
        }
        return "vnd.android.cursor.dir/cellbroadcast";
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        String str3;
        Uri uri2 = uri;
        checkReadPermission(uri);
        if (DBG) {
            String str4 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("query: uri = ");
            sb.append(uri);
            sb.append(" projection = ");
            sb.append(Arrays.toString(strArr));
            sb.append(" selection = ");
            String str5 = str;
            sb.append(str);
            sb.append(" selectionArgs = ");
            sb.append(Arrays.toString(strArr2));
            sb.append(" sortOrder = ");
            str3 = str2;
            sb.append(str3);
            Log.d(str4, sb.toString());
        } else {
            String str6 = str;
            str3 = str2;
        }
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setStrict(true);
        sQLiteQueryBuilder.setTables(CELL_BROADCASTS_TABLE_NAME);
        if (TextUtils.isEmpty(str2)) {
            str3 = "received_time DESC";
        }
        String str7 = str3;
        int match = sUriMatcher.match(uri);
        if (match == 0) {
            return getReadableDatabase().query(CELL_BROADCASTS_TABLE_NAME, strArr, str, strArr2, (String) null, (String) null, str7);
        }
        if (match == 1) {
            sQLiteQueryBuilder.appendWhere("message_broadcasted=1");
            return sQLiteQueryBuilder.query(getReadableDatabase(), strArr, str, strArr2, (String) null, (String) null, str7);
        }
        throw new IllegalArgumentException("Query method doesn't support this uri = " + uri);
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        String str = TAG;
        checkWritePermission();
        if (DBG) {
            Log.d(str, "insert: uri = " + uri + " contentValue = " + contentValues);
        }
        if (sUriMatcher.match(uri) != 0) {
            String str2 = "Insert method doesn't support this uri=" + uri.toString() + " values=" + contentValues;
            if (str2.length() > 1000) {
                str2 = str2.substring(0, 1000);
            }
            CellBroadcastStatsLog.write(250, 10, str2);
            throw new IllegalArgumentException(str2);
        }
        long insertOrThrow = getWritableDatabase().insertOrThrow(CELL_BROADCASTS_TABLE_NAME, (String) null, contentValues);
        if (insertOrThrow > 0) {
            Uri withAppendedId = ContentUris.withAppendedId(CONTENT_URI, insertOrThrow);
            getContext().getContentResolver().notifyChange(CONTENT_URI, (ContentObserver) null);
            return withAppendedId;
        }
        String str3 = "uri=" + uri.toString() + " values=" + contentValues;
        if (str3.length() > 1000) {
            str3 = str3.substring(0, 1000);
        }
        CellBroadcastStatsLog.write(250, 10, str3);
        Log.e(str, "Insert record failed because of unknown reason. " + str3);
        return null;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        checkWritePermission();
        if (DBG) {
            String str2 = TAG;
            Log.d(str2, "delete: uri = " + uri + " selection = " + str + " selectionArgs = " + Arrays.toString(strArr));
        }
        if (sUriMatcher.match(uri) == 0) {
            return getWritableDatabase().delete(CELL_BROADCASTS_TABLE_NAME, str, strArr);
        }
        throw new IllegalArgumentException("Delete method doesn't support this uri = " + uri);
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        Cursor query;
        String str2 = TAG;
        checkWritePermission();
        if (DBG) {
            Log.d(str2, "update: uri = " + uri + " values = {" + contentValues + "} selection = " + str + " selectionArgs = " + Arrays.toString(strArr));
        }
        int match = sUriMatcher.match(uri);
        if (match == 0) {
            int update = getWritableDatabase().update(CELL_BROADCASTS_TABLE_NAME, contentValues, str, strArr);
            if (update > 0) {
                getContext().getContentResolver().notifyChange(uri, (ContentObserver) null, 3);
            }
            return update;
        } else if (match == 2) {
            contentValues.put("message_displayed", 1);
            int update2 = getWritableDatabase().update(CELL_BROADCASTS_TABLE_NAME, contentValues, str, strArr);
            if (update2 > 0) {
                try {
                    query = query(Telephony.CellBroadcasts.CONTENT_URI, new String[]{"_id"}, str, strArr, (String) null);
                    if (query != null) {
                        if (query.moveToFirst()) {
                            int i = query.getInt(query.getColumnIndex("_id"));
                            Log.d(str2, "notify contentObservers for the displayed message, row: " + i);
                            ContentResolver contentResolver = getContext().getContentResolver();
                            Uri uri2 = CONTENT_URI;
                            contentResolver.notifyChange(Uri.withAppendedPath(uri2, "displayed/" + i), (ContentObserver) null, true);
                        }
                    }
                    if (query != null) {
                        query.close();
                    }
                } catch (Exception e) {
                    Log.e(str2, "exception during update message displayed:  " + e.toString());
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            }
            return update2;
        } else {
            throw new IllegalArgumentException("Update method doesn't support this uri = " + uri);
        }
        throw th;
    }

    public static String getStringForCellBroadcastTableCreation(String str) {
        return "CREATE TABLE " + str + " (" + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT," + "sub_id" + " INTEGER," + "slot_index" + " INTEGER DEFAULT 0," + "geo_scope" + " INTEGER," + "plmn" + " TEXT," + "lac" + " INTEGER," + "cid" + " INTEGER," + "serial_number" + " INTEGER," + "service_category" + " INTEGER," + "language" + " TEXT," + "dcs" + " INTEGER DEFAULT 0," + "body" + " TEXT," + "format" + " INTEGER," + "priority" + " INTEGER," + "etws_warning_type" + " INTEGER,etws_is_primary BOOLEAN DEFAULT 0," + "cmas_message_class" + " INTEGER," + "cmas_category" + " INTEGER," + "cmas_response_type" + " INTEGER," + "cmas_severity" + " INTEGER," + "cmas_urgency" + " INTEGER," + "cmas_certainty" + " INTEGER," + "received_time" + " BIGINT," + "location_check_time" + " BIGINT DEFAULT -1," + "message_broadcasted" + " BOOLEAN DEFAULT 0," + "message_displayed" + " BOOLEAN DEFAULT 0," + "geometries" + " TEXT," + "maximum_wait_time" + " INTEGER);";
    }

    private SQLiteDatabase getWritableDatabase() {
        return this.mDbHelper.getWritableDatabase();
    }

    private SQLiteDatabase getReadableDatabase() {
        return this.mDbHelper.getReadableDatabase();
    }

    private void checkWritePermission() {
        if (!this.mPermissionChecker.hasFullAccessPermission()) {
            throw new SecurityException("No permission to write CellBroadcast provider");
        }
    }

    private void checkReadPermission(Uri uri) {
        if (sUriMatcher.match(uri) == 0 && !this.mPermissionChecker.hasFullAccessPermission()) {
            throw new SecurityException("No permission to read CellBroadcast provider");
        }
    }

    public static class CellBroadcastDatabaseHelper extends SQLiteOpenHelper {
        public CellBroadcastDatabaseHelper(Context context) {
            super(context, "cellbroadcasts.db", (SQLiteDatabase.CursorFactory) null, 4);
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(CellBroadcastProvider.getStringForCellBroadcastTableCreation(CellBroadcastProvider.CELL_BROADCASTS_TABLE_NAME));
        }

        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            if (CellBroadcastProvider.DBG) {
                String access$100 = CellBroadcastProvider.TAG;
                Log.d(access$100, "onUpgrade: oldV=" + i + " newV=" + i2);
            }
            if (i < 2) {
                sQLiteDatabase.execSQL("ALTER TABLE cell_broadcasts ADD COLUMN slot_index INTEGER DEFAULT 0;");
                Log.d(CellBroadcastProvider.TAG, "add slotIndex column");
            }
            if (i < 3) {
                sQLiteDatabase.execSQL("ALTER TABLE cell_broadcasts ADD COLUMN dcs INTEGER DEFAULT 0;");
                sQLiteDatabase.execSQL("ALTER TABLE cell_broadcasts ADD COLUMN location_check_time BIGINT DEFAULT -1;");
                sQLiteDatabase.execSQL("ALTER TABLE cell_broadcasts ADD COLUMN message_displayed BOOLEAN DEFAULT 1;");
                Log.d(CellBroadcastProvider.TAG, "add dcs, location check time, and message displayed column.");
            }
            if (i < 4) {
                sQLiteDatabase.execSQL("ALTER TABLE cell_broadcasts ADD COLUMN etws_is_primary BOOLEAN DEFAULT 0;");
                Log.d(CellBroadcastProvider.TAG, "add ETWS is_primary column.");
            }
        }
    }

    public class CellBroadcastPermissionChecker {
        public CellBroadcastPermissionChecker() {
        }

        public boolean hasFullAccessPermission() {
            return CellBroadcastProvider.this.getContext().checkCallingOrSelfPermission("com.android.cellbroadcastservice.FULL_ACCESS_CELL_BROADCAST_HISTORY") == 0;
        }
    }
}

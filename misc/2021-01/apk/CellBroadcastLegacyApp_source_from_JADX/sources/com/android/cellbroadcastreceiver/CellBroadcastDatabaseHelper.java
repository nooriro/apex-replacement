package com.android.cellbroadcastreceiver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CellBroadcastDatabaseHelper extends SQLiteOpenHelper {
    public static final String[] QUERY_COLUMNS = {"_id", "slot_index", "geo_scope", "plmn", "lac", "cid", "serial_number", "service_category", "language", "body", "date", "read", "format", "priority", "etws_warning_type", "cmas_message_class", "cmas_category", "cmas_response_type", "cmas_severity", "cmas_urgency", "cmas_certainty"};
    public static final String TABLE_NAME = "broadcasts";
    private final Context mContext;
    final boolean mLegacyProvider;

    public static String getStringForCellBroadcastTableCreation(String str) {
        return "CREATE TABLE " + str + " (" + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT," + "slot_index" + " INTEGER DEFAULT 0," + "geo_scope" + " INTEGER," + "plmn" + " TEXT," + "lac" + " INTEGER," + "cid" + " INTEGER," + "serial_number" + " INTEGER," + "service_category" + " INTEGER," + "language" + " TEXT," + "body" + " TEXT," + "date" + " INTEGER," + "read" + " INTEGER," + "format" + " INTEGER," + "priority" + " INTEGER," + "etws_warning_type" + " INTEGER," + "cmas_message_class" + " INTEGER," + "cmas_category" + " INTEGER," + "cmas_response_type" + " INTEGER," + "cmas_severity" + " INTEGER," + "cmas_urgency" + " INTEGER," + "cmas_certainty" + " INTEGER);";
    }

    public CellBroadcastDatabaseHelper(Context context, boolean z) {
        super(context, "cell_broadcasts.db", (SQLiteDatabase.CursorFactory) null, 12);
        this.mContext = context;
        this.mLegacyProvider = z;
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(getStringForCellBroadcastTableCreation(TABLE_NAME));
        sQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS deliveryTimeIndex ON broadcasts (date);");
        if (!this.mLegacyProvider) {
            migrateFromLegacy(sQLiteDatabase);
        }
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i != i2) {
            log("Upgrading DB from version " + i + " to " + i2);
            if (i < 12) {
                sQLiteDatabase.execSQL("ALTER TABLE broadcasts ADD COLUMN slot_index INTEGER DEFAULT 0;");
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0092, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
        r8.endTransaction();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x009e, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x009f, code lost:
        r8 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00a0, code lost:
        if (r7 != null) goto L_0x00a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:?, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00aa, code lost:
        throw r8;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:4:0x0010, B:42:0x0095] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void migrateFromLegacy(android.database.sqlite.SQLiteDatabase r8) {
        /*
            r7 = this;
            android.content.Context r7 = r7.mContext     // Catch:{ Exception -> 0x00ab }
            android.content.ContentResolver r7 = r7.getContentResolver()     // Catch:{ Exception -> 0x00ab }
            java.lang.String r0 = "cellbroadcast-legacy"
            android.content.ContentProviderClient r7 = r7.acquireContentProviderClient(r0)     // Catch:{ Exception -> 0x00ab }
            if (r7 != 0) goto L_0x0019
            java.lang.String r8 = "No legacy provider available for migration"
            log(r8)     // Catch:{ all -> 0x009f }
            if (r7 == 0) goto L_0x0018
            r7.close()     // Catch:{ Exception -> 0x00ab }
        L_0x0018:
            return
        L_0x0019:
            r8.beginTransaction()     // Catch:{ all -> 0x009f }
            java.lang.String r0 = "Starting migration from legacy provider"
            log(r0)     // Catch:{ all -> 0x009f }
            android.net.Uri r2 = android.provider.Telephony.CellBroadcasts.AUTHORITY_LEGACY_URI     // Catch:{ RemoteException -> 0x0094 }
            java.lang.String[] r3 = QUERY_COLUMNS     // Catch:{ RemoteException -> 0x0094 }
            r4 = 0
            r5 = 0
            r6 = 0
            r1 = r7
            android.database.Cursor r0 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ RemoteException -> 0x0094 }
            android.content.ContentValues r1 = new android.content.ContentValues     // Catch:{ all -> 0x0086 }
            r1.<init>()     // Catch:{ all -> 0x0086 }
        L_0x0032:
            boolean r2 = r0.moveToNext()     // Catch:{ all -> 0x0086 }
            if (r2 == 0) goto L_0x0070
            r1.clear()     // Catch:{ all -> 0x0086 }
            java.lang.String[] r2 = QUERY_COLUMNS     // Catch:{ all -> 0x0086 }
            int r3 = r2.length     // Catch:{ all -> 0x0086 }
            r4 = 0
        L_0x003f:
            if (r4 >= r3) goto L_0x0049
            r5 = r2[r4]     // Catch:{ all -> 0x0086 }
            copyFromCursorToContentValues(r5, r0, r1)     // Catch:{ all -> 0x0086 }
            int r4 = r4 + 1
            goto L_0x003f
        L_0x0049:
            java.lang.String r2 = "broadcasts"
            r3 = 0
            long r2 = r8.insert(r2, r3, r1)     // Catch:{ all -> 0x0086 }
            r4 = -1
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x0032
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0086 }
            r2.<init>()     // Catch:{ all -> 0x0086 }
            java.lang.String r3 = "Failed to insert "
            r2.append(r3)     // Catch:{ all -> 0x0086 }
            r2.append(r1)     // Catch:{ all -> 0x0086 }
            java.lang.String r3 = "; continuing"
            r2.append(r3)     // Catch:{ all -> 0x0086 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0086 }
            loge(r2)     // Catch:{ all -> 0x0086 }
            goto L_0x0032
        L_0x0070:
            r8.setTransactionSuccessful()     // Catch:{ all -> 0x0086 }
            java.lang.String r1 = "Finished migration from legacy provider"
            log(r1)     // Catch:{ all -> 0x0086 }
            if (r0 == 0) goto L_0x007d
            r0.close()     // Catch:{ RemoteException -> 0x0094 }
        L_0x007d:
            r8.endTransaction()     // Catch:{ all -> 0x009f }
            if (r7 == 0) goto L_0x00c0
            r7.close()     // Catch:{ Exception -> 0x00ab }
            goto L_0x00c0
        L_0x0086:
            r1 = move-exception
            if (r0 == 0) goto L_0x0091
            r0.close()     // Catch:{ all -> 0x008d }
            goto L_0x0091
        L_0x008d:
            r0 = move-exception
            r1.addSuppressed(r0)     // Catch:{ RemoteException -> 0x0094 }
        L_0x0091:
            throw r1     // Catch:{ RemoteException -> 0x0094 }
        L_0x0092:
            r0 = move-exception
            goto L_0x009b
        L_0x0094:
            r0 = move-exception
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0092 }
            r1.<init>(r0)     // Catch:{ all -> 0x0092 }
            throw r1     // Catch:{ all -> 0x0092 }
        L_0x009b:
            r8.endTransaction()     // Catch:{ all -> 0x009f }
            throw r0     // Catch:{ all -> 0x009f }
        L_0x009f:
            r8 = move-exception
            if (r7 == 0) goto L_0x00aa
            r7.close()     // Catch:{ all -> 0x00a6 }
            goto L_0x00aa
        L_0x00a6:
            r7 = move-exception
            r8.addSuppressed(r7)     // Catch:{ Exception -> 0x00ab }
        L_0x00aa:
            throw r8     // Catch:{ Exception -> 0x00ab }
        L_0x00ab:
            r7 = move-exception
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "Failed migration from legacy provider: "
            r8.append(r0)
            r8.append(r7)
            java.lang.String r7 = r8.toString()
            loge(r7)
        L_0x00c0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastreceiver.CellBroadcastDatabaseHelper.migrateFromLegacy(android.database.sqlite.SQLiteDatabase):void");
    }

    public static void copyFromCursorToContentValues(String str, Cursor cursor, ContentValues contentValues) {
        int columnIndex = cursor.getColumnIndex(str);
        if (columnIndex == -1) {
            return;
        }
        if (cursor.isNull(columnIndex)) {
            contentValues.putNull(str);
        } else {
            contentValues.put(str, cursor.getString(columnIndex));
        }
    }

    private static void log(String str) {
        Log.d("CellBroadcastDatabaseHelper", str);
    }

    private static void loge(String str) {
        Log.e("CellBroadcastDatabaseHelper", str);
    }
}

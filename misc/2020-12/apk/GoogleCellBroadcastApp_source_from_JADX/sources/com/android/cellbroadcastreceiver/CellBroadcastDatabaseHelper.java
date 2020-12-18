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

    public CellBroadcastDatabaseHelper(Context context, boolean z, String str) {
        super(context, str, (SQLiteDatabase.CursorFactory) null, 12);
        this.mContext = context;
        this.mLegacyProvider = z;
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(getStringForCellBroadcastTableCreation(TABLE_NAME));
        sQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS deliveryTimeIndex ON broadcasts (date);");
        if (!this.mLegacyProvider) {
            migrateFromLegacyIfNeeded(sQLiteDatabase);
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

    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00b8, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        r12.endTransaction();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00c4, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00c5, code lost:
        r12 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00c6, code lost:
        if (r11 != null) goto L_0x00c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        r11.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00d0, code lost:
        throw r12;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:9:0x0026, B:49:0x00bb] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void migrateFromLegacyIfNeeded(android.database.sqlite.SQLiteDatabase r12) {
        /*
            r11 = this;
            android.content.Context r0 = r11.mContext
            android.content.SharedPreferences r0 = android.preference.PreferenceManager.getDefaultSharedPreferences(r0)
            java.lang.String r1 = "legacy_data_migration"
            r2 = 0
            boolean r3 = r0.getBoolean(r1, r2)
            if (r3 == 0) goto L_0x0015
            java.lang.String r11 = "Data migration was complete already"
            log(r11)
            return
        L_0x0015:
            r3 = 1
            android.content.Context r11 = r11.mContext     // Catch:{ Exception -> 0x00d3 }
            android.content.ContentResolver r11 = r11.getContentResolver()     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r4 = "cellbroadcast-legacy"
            android.content.ContentProviderClient r11 = r11.acquireContentProviderClient(r4)     // Catch:{ Exception -> 0x00d3 }
            if (r11 != 0) goto L_0x003a
            java.lang.String r12 = "No legacy provider available for migration"
            log(r12)     // Catch:{ all -> 0x00c5 }
            if (r11 == 0) goto L_0x002e
            r11.close()     // Catch:{ Exception -> 0x00d3 }
        L_0x002e:
            android.content.SharedPreferences$Editor r11 = r0.edit()
            android.content.SharedPreferences$Editor r11 = r11.putBoolean(r1, r3)
            r11.commit()
            return
        L_0x003a:
            r12.beginTransaction()     // Catch:{ all -> 0x00c5 }
            java.lang.String r4 = "Starting migration from legacy provider"
            log(r4)     // Catch:{ all -> 0x00c5 }
            android.net.Uri r6 = android.provider.Telephony.CellBroadcasts.AUTHORITY_LEGACY_URI     // Catch:{ RemoteException -> 0x00ba }
            java.lang.String[] r7 = QUERY_COLUMNS     // Catch:{ RemoteException -> 0x00ba }
            r8 = 0
            r9 = 0
            r10 = 0
            r5 = r11
            android.database.Cursor r4 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ RemoteException -> 0x00ba }
            android.content.ContentValues r5 = new android.content.ContentValues     // Catch:{ all -> 0x00ac }
            r5.<init>()     // Catch:{ all -> 0x00ac }
        L_0x0053:
            boolean r6 = r4.moveToNext()     // Catch:{ all -> 0x00ac }
            if (r6 == 0) goto L_0x0096
            r5.clear()     // Catch:{ all -> 0x00ac }
            java.lang.String[] r6 = QUERY_COLUMNS     // Catch:{ all -> 0x00ac }
            int r7 = r6.length     // Catch:{ all -> 0x00ac }
            r8 = r2
        L_0x0060:
            if (r8 >= r7) goto L_0x006a
            r9 = r6[r8]     // Catch:{ all -> 0x00ac }
            copyFromCursorToContentValues(r9, r4, r5)     // Catch:{ all -> 0x00ac }
            int r8 = r8 + 1
            goto L_0x0060
        L_0x006a:
            java.lang.String r6 = "_id"
            r5.remove(r6)     // Catch:{ all -> 0x00ac }
            java.lang.String r6 = "broadcasts"
            r7 = 0
            long r6 = r12.insert(r6, r7, r5)     // Catch:{ all -> 0x00ac }
            r8 = -1
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 != 0) goto L_0x0053
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ac }
            r6.<init>()     // Catch:{ all -> 0x00ac }
            java.lang.String r7 = "Failed to insert "
            r6.append(r7)     // Catch:{ all -> 0x00ac }
            r6.append(r5)     // Catch:{ all -> 0x00ac }
            java.lang.String r7 = "; continuing"
            r6.append(r7)     // Catch:{ all -> 0x00ac }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00ac }
            loge(r6)     // Catch:{ all -> 0x00ac }
            goto L_0x0053
        L_0x0096:
            r12.setTransactionSuccessful()     // Catch:{ all -> 0x00ac }
            java.lang.String r2 = "Finished migration from legacy provider"
            log(r2)     // Catch:{ all -> 0x00ac }
            if (r4 == 0) goto L_0x00a3
            r4.close()     // Catch:{ RemoteException -> 0x00ba }
        L_0x00a3:
            r12.endTransaction()     // Catch:{ all -> 0x00c5 }
            if (r11 == 0) goto L_0x00e8
            r11.close()     // Catch:{ Exception -> 0x00d3 }
            goto L_0x00e8
        L_0x00ac:
            r2 = move-exception
            if (r4 == 0) goto L_0x00b7
            r4.close()     // Catch:{ all -> 0x00b3 }
            goto L_0x00b7
        L_0x00b3:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ RemoteException -> 0x00ba }
        L_0x00b7:
            throw r2     // Catch:{ RemoteException -> 0x00ba }
        L_0x00b8:
            r2 = move-exception
            goto L_0x00c1
        L_0x00ba:
            r2 = move-exception
            java.lang.IllegalStateException r4 = new java.lang.IllegalStateException     // Catch:{ all -> 0x00b8 }
            r4.<init>(r2)     // Catch:{ all -> 0x00b8 }
            throw r4     // Catch:{ all -> 0x00b8 }
        L_0x00c1:
            r12.endTransaction()     // Catch:{ all -> 0x00c5 }
            throw r2     // Catch:{ all -> 0x00c5 }
        L_0x00c5:
            r12 = move-exception
            if (r11 == 0) goto L_0x00d0
            r11.close()     // Catch:{ all -> 0x00cc }
            goto L_0x00d0
        L_0x00cc:
            r11 = move-exception
            r12.addSuppressed(r11)     // Catch:{ Exception -> 0x00d3 }
        L_0x00d0:
            throw r12     // Catch:{ Exception -> 0x00d3 }
        L_0x00d1:
            r11 = move-exception
            goto L_0x00f4
        L_0x00d3:
            r11 = move-exception
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d1 }
            r12.<init>()     // Catch:{ all -> 0x00d1 }
            java.lang.String r2 = "Failed migration from legacy provider: "
            r12.append(r2)     // Catch:{ all -> 0x00d1 }
            r12.append(r11)     // Catch:{ all -> 0x00d1 }
            java.lang.String r11 = r12.toString()     // Catch:{ all -> 0x00d1 }
            loge(r11)     // Catch:{ all -> 0x00d1 }
        L_0x00e8:
            android.content.SharedPreferences$Editor r11 = r0.edit()
            android.content.SharedPreferences$Editor r11 = r11.putBoolean(r1, r3)
            r11.commit()
            return
        L_0x00f4:
            android.content.SharedPreferences$Editor r12 = r0.edit()
            android.content.SharedPreferences$Editor r12 = r12.putBoolean(r1, r3)
            r12.commit()
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastreceiver.CellBroadcastDatabaseHelper.migrateFromLegacyIfNeeded(android.database.sqlite.SQLiteDatabase):void");
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

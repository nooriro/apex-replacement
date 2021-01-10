package com.android.cellbroadcastreceiver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;

public class CellBroadcastDatabaseHelper extends SQLiteOpenHelper {
    public static final String[] QUERY_COLUMNS = {"_id", "slot_index", "geo_scope", "plmn", "lac", "cid", "serial_number", "service_category", "language", "body", "date", "read", "format", "priority", "etws_warning_type", "cmas_message_class", "cmas_category", "cmas_response_type", "cmas_severity", "cmas_urgency", "cmas_certainty"};
    public static final String TABLE_NAME = "broadcasts";
    private final Context mContext;
    final boolean mLegacyProvider;

    public static String getStringForCellBroadcastTableCreation(String str) {
        return "CREATE TABLE " + str + " (" + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT," + "slot_index" + " INTEGER DEFAULT 0," + "geo_scope" + " INTEGER," + "plmn" + " TEXT," + "lac" + " INTEGER," + "cid" + " INTEGER," + "serial_number" + " INTEGER," + "service_category" + " INTEGER," + "language" + " TEXT," + "body" + " TEXT," + "date" + " INTEGER," + "read" + " INTEGER," + "format" + " INTEGER," + "priority" + " INTEGER," + "etws_warning_type" + " INTEGER," + "cmas_message_class" + " INTEGER," + "cmas_category" + " INTEGER," + "cmas_response_type" + " INTEGER," + "cmas_severity" + " INTEGER," + "cmas_urgency" + " INTEGER," + "cmas_certainty" + " INTEGER," + "isSmsSyncPending" + " BOOLEAN);";
    }

    public CellBroadcastDatabaseHelper(Context context, boolean z) {
        super(context, "cell_broadcasts_v13.db", (SQLiteDatabase.CursorFactory) null, 13);
        this.mContext = context;
        this.mLegacyProvider = z;
    }

    public CellBroadcastDatabaseHelper(Context context, boolean z, String str) {
        super(context, str, (SQLiteDatabase.CursorFactory) null, 13);
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
            if (i < 13) {
                sQLiteDatabase.execSQL("ALTER TABLE broadcasts ADD COLUMN isSmsSyncPending BOOLEAN DEFAULT 0;");
            }
        }
    }

    private synchronized void tryToMigrateV13() {
        File databasePath = this.mContext.getDatabasePath("cell_broadcasts.db");
        File databasePath2 = this.mContext.getDatabasePath("cell_broadcasts_v13.db");
        if (databasePath.exists()) {
            if (!databasePath2.exists() || databasePath.lastModified() > databasePath2.lastModified()) {
                try {
                    Log.d("CellBroadcastDatabaseHelper", "copying to v13 db");
                    if (databasePath2.exists()) {
                        databasePath2.delete();
                    }
                    Files.copy(databasePath.toPath(), databasePath2.toPath(), new CopyOption[0]);
                } catch (Exception e) {
                    this.mContext.deleteDatabase("cell_broadcasts_v13.db");
                    loge("could not copy DB to v13. e=" + e);
                }
            }
        } else {
            return;
        }
        return;
    }

    public SQLiteDatabase getReadableDatabase() {
        tryToMigrateV13();
        return super.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabase() {
        tryToMigrateV13();
        return super.getWritableDatabase();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00d4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:?, code lost:
        r13.setTransactionSuccessful();
        r13.endTransaction();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00e3, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00e4, code lost:
        r13 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00e5, code lost:
        if (r12 != null) goto L_0x00e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:?, code lost:
        r12.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x00ef, code lost:
        throw r13;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:9:0x0028, B:55:0x00d7] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void migrateFromLegacyIfNeeded(android.database.sqlite.SQLiteDatabase r13) {
        /*
            r12 = this;
            java.lang.String r0 = "Failed to insert "
            android.content.Context r1 = r12.mContext
            android.content.SharedPreferences r1 = android.preference.PreferenceManager.getDefaultSharedPreferences(r1)
            java.lang.String r2 = "legacy_data_migration"
            r3 = 0
            boolean r4 = r1.getBoolean(r2, r3)
            if (r4 == 0) goto L_0x0017
            java.lang.String r12 = "Data migration was complete already"
            log(r12)
            return
        L_0x0017:
            r4 = 1
            android.content.Context r12 = r12.mContext     // Catch:{ Exception -> 0x00f2 }
            android.content.ContentResolver r12 = r12.getContentResolver()     // Catch:{ Exception -> 0x00f2 }
            java.lang.String r5 = "cellbroadcast-legacy"
            android.content.ContentProviderClient r12 = r12.acquireContentProviderClient(r5)     // Catch:{ Exception -> 0x00f2 }
            if (r12 != 0) goto L_0x003c
            java.lang.String r13 = "No legacy provider available for migration"
            log(r13)     // Catch:{ all -> 0x00e4 }
            if (r12 == 0) goto L_0x0030
            r12.close()     // Catch:{ Exception -> 0x00f2 }
        L_0x0030:
            android.content.SharedPreferences$Editor r12 = r1.edit()
            android.content.SharedPreferences$Editor r12 = r12.putBoolean(r2, r4)
            r12.commit()
            return
        L_0x003c:
            r13.beginTransaction()     // Catch:{ all -> 0x00e4 }
            java.lang.String r5 = "Starting migration from legacy provider"
            log(r5)     // Catch:{ all -> 0x00e4 }
            android.net.Uri r7 = android.provider.Telephony.CellBroadcasts.AUTHORITY_LEGACY_URI     // Catch:{ RemoteException -> 0x00d6 }
            java.lang.String[] r8 = QUERY_COLUMNS     // Catch:{ RemoteException -> 0x00d6 }
            r9 = 0
            r10 = 0
            r11 = 0
            r6 = r12
            android.database.Cursor r5 = r6.query(r7, r8, r9, r10, r11)     // Catch:{ RemoteException -> 0x00d6 }
            android.content.ContentValues r6 = new android.content.ContentValues     // Catch:{ all -> 0x00c8 }
            r6.<init>()     // Catch:{ all -> 0x00c8 }
        L_0x0055:
            boolean r7 = r5.moveToNext()     // Catch:{ all -> 0x00c8 }
            if (r7 == 0) goto L_0x00b2
            r6.clear()     // Catch:{ all -> 0x00c8 }
            java.lang.String[] r7 = QUERY_COLUMNS     // Catch:{ all -> 0x00c8 }
            int r8 = r7.length     // Catch:{ all -> 0x00c8 }
            r9 = r3
        L_0x0062:
            if (r9 >= r8) goto L_0x006c
            r10 = r7[r9]     // Catch:{ all -> 0x00c8 }
            copyFromCursorToContentValues(r10, r5, r6)     // Catch:{ all -> 0x00c8 }
            int r9 = r9 + 1
            goto L_0x0062
        L_0x006c:
            java.lang.String r7 = "_id"
            r6.remove(r7)     // Catch:{ all -> 0x00c8 }
            java.lang.String r7 = "broadcasts"
            r8 = 0
            long r7 = r13.insert(r7, r8, r6)     // Catch:{ Exception -> 0x0096 }
            r9 = -1
            int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r7 != 0) goto L_0x0055
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0096 }
            r7.<init>()     // Catch:{ Exception -> 0x0096 }
            r7.append(r0)     // Catch:{ Exception -> 0x0096 }
            r7.append(r6)     // Catch:{ Exception -> 0x0096 }
            java.lang.String r8 = "; continuing"
            r7.append(r8)     // Catch:{ Exception -> 0x0096 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0096 }
            loge(r7)     // Catch:{ Exception -> 0x0096 }
            goto L_0x0055
        L_0x0096:
            r7 = move-exception
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c8 }
            r8.<init>()     // Catch:{ all -> 0x00c8 }
            r8.append(r0)     // Catch:{ all -> 0x00c8 }
            r8.append(r6)     // Catch:{ all -> 0x00c8 }
            java.lang.String r9 = " due to exception: "
            r8.append(r9)     // Catch:{ all -> 0x00c8 }
            r8.append(r7)     // Catch:{ all -> 0x00c8 }
            java.lang.String r7 = r8.toString()     // Catch:{ all -> 0x00c8 }
            loge(r7)     // Catch:{ all -> 0x00c8 }
            goto L_0x0055
        L_0x00b2:
            java.lang.String r0 = "Finished migration from legacy provider"
            log(r0)     // Catch:{ all -> 0x00c8 }
            if (r5 == 0) goto L_0x00bc
            r5.close()     // Catch:{ RemoteException -> 0x00d6 }
        L_0x00bc:
            r13.setTransactionSuccessful()     // Catch:{ all -> 0x00e4 }
            r13.endTransaction()     // Catch:{ all -> 0x00e4 }
            if (r12 == 0) goto L_0x0107
            r12.close()     // Catch:{ Exception -> 0x00f2 }
            goto L_0x0107
        L_0x00c8:
            r0 = move-exception
            if (r5 == 0) goto L_0x00d3
            r5.close()     // Catch:{ all -> 0x00cf }
            goto L_0x00d3
        L_0x00cf:
            r3 = move-exception
            r0.addSuppressed(r3)     // Catch:{ RemoteException -> 0x00d6 }
        L_0x00d3:
            throw r0     // Catch:{ RemoteException -> 0x00d6 }
        L_0x00d4:
            r0 = move-exception
            goto L_0x00dd
        L_0x00d6:
            r0 = move-exception
            java.lang.IllegalStateException r3 = new java.lang.IllegalStateException     // Catch:{ all -> 0x00d4 }
            r3.<init>(r0)     // Catch:{ all -> 0x00d4 }
            throw r3     // Catch:{ all -> 0x00d4 }
        L_0x00dd:
            r13.setTransactionSuccessful()     // Catch:{ all -> 0x00e4 }
            r13.endTransaction()     // Catch:{ all -> 0x00e4 }
            throw r0     // Catch:{ all -> 0x00e4 }
        L_0x00e4:
            r13 = move-exception
            if (r12 == 0) goto L_0x00ef
            r12.close()     // Catch:{ all -> 0x00eb }
            goto L_0x00ef
        L_0x00eb:
            r12 = move-exception
            r13.addSuppressed(r12)     // Catch:{ Exception -> 0x00f2 }
        L_0x00ef:
            throw r13     // Catch:{ Exception -> 0x00f2 }
        L_0x00f0:
            r12 = move-exception
            goto L_0x0113
        L_0x00f2:
            r12 = move-exception
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x00f0 }
            r13.<init>()     // Catch:{ all -> 0x00f0 }
            java.lang.String r0 = "Failed migration from legacy provider: "
            r13.append(r0)     // Catch:{ all -> 0x00f0 }
            r13.append(r12)     // Catch:{ all -> 0x00f0 }
            java.lang.String r12 = r13.toString()     // Catch:{ all -> 0x00f0 }
            loge(r12)     // Catch:{ all -> 0x00f0 }
        L_0x0107:
            android.content.SharedPreferences$Editor r12 = r1.edit()
            android.content.SharedPreferences$Editor r12 = r12.putBoolean(r2, r4)
            r12.commit()
            return
        L_0x0113:
            android.content.SharedPreferences$Editor r13 = r1.edit()
            android.content.SharedPreferences$Editor r13 = r13.putBoolean(r2, r4)
            r13.commit()
            throw r12
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

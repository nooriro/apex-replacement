package com.android.cellbroadcastreceiver;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Arrays;
import java.util.List;

public class LegacyCellBroadcastContentProvider extends ContentProvider {
    private static final List<String> PREF_KEYS = Arrays.asList(new String[]{"enable_cmas_amber_alerts", "enable_area_update_info_alerts", "enable_test_alerts", "enable_state_local_test_alerts", "enable_public_safety_messages", "enable_cmas_severe_threat_alerts", "enable_cmas_extreme_threat_alerts", "enable_cmas_presidential_alerts", "enable_emergency_alerts", "enable_alert_vibrate", "receive_cmas_in_second_language", "enable_alerts_master_toggle"});
    private static final String TAG = LegacyCellBroadcastContentProvider.class.getSimpleName();
    private SQLiteOpenHelper mOpenHelper;

    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        this.mOpenHelper = new CellBroadcastDatabaseHelper(getContext(), true);
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        String str3 = TAG;
        Log.d(str3, "query: uri=" + uri + " values=" + Arrays.toString(strArr) + " selection=" + str + " selectionArgs=" + Arrays.toString(strArr2));
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables(CellBroadcastDatabaseHelper.TABLE_NAME);
        Cursor query = sQLiteQueryBuilder.query(this.mOpenHelper.getReadableDatabase(), strArr, str, strArr2, (String) null, (String) null, str2);
        StringBuilder sb = new StringBuilder();
        sb.append("query from legacy cellbroadcast, returned ");
        sb.append(query.getCount());
        sb.append(" messages");
        Log.d(str3, sb.toString());
        return query;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        String str3 = TAG;
        Log.d(str3, "call: method=" + str + " name=" + str2 + " args=" + bundle);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!"get_preference".equals(str)) {
            Log.e(str3, "unsuppprted call method: " + str);
            return null;
        } else if (!PREF_KEYS.contains(str2)) {
            Log.e(str3, "unsupported preference name" + str2);
            return null;
        } else if (defaultSharedPreferences == null || !defaultSharedPreferences.contains(str2)) {
            return null;
        } else {
            Bundle bundle2 = new Bundle();
            bundle2.putBoolean(str2, defaultSharedPreferences.getBoolean(str2, true));
            Log.d(str3, "migrate sharedpreference: " + str2 + " val: " + bundle2.get(str2));
            return bundle2;
        }
    }

    public String getType(Uri uri) {
        Log.d(TAG, "getType");
        return null;
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
}

package com.android.cellbroadcastreceiver;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;

public class CellBroadcastBackupAgent extends BackupAgentHelper {
    public void onCreate() {
        Log.d("CBBackupAgent", "onCreate");
        addHelper("shared_pref", new SharedPreferencesBackupHelper(this, new String[]{"com.android.cellbroadcastreceiver_preferences"}));
    }

    public void onRestoreFinished() {
        Log.d("CBBackupAgent", "Restore finished.");
        sendBroadcastAsUser(new Intent("com.android.cellbroadcastreceiver.intent.START_CONFIG"), UserHandle.SYSTEM);
    }
}

package com.android.cellbroadcastreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.android.cellbroadcastreceiver.module.R;

public class CellBroadcastOptOutActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("CellBroadcastOptOutActivity", "created activity");
        showOptOutDialog(this);
    }

    static AlertDialog showOptOutDialog(final Activity activity) {
        AlertDialog create = new AlertDialog.Builder(activity).setMessage(R.string.cmas_opt_out_dialog_text).setPositiveButton(R.string.cmas_opt_out_button_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("CellBroadcastOptOutActivity", "User clicked Yes");
                activity.finish();
            }
        }).setNegativeButton(R.string.cmas_opt_out_button_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("CellBroadcastOptOutActivity", "User clicked No");
                activity.startActivity(new Intent(activity, CellBroadcastSettings.class));
                activity.finish();
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("CellBroadcastOptOutActivity", "User cancelled");
                activity.finish();
            }
        }).create();
        create.show();
        return create;
    }
}

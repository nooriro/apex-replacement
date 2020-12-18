package androidx.activity.result.contract;

import android.content.Intent;
import androidx.activity.result.ActivityResult;

public final class ActivityResultContracts$StartActivityForResult extends ActivityResultContract<Intent, ActivityResult> {
    public ActivityResult parseResult(int i, Intent intent) {
        return new ActivityResult(i, intent);
    }
}

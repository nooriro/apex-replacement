package androidx.core.view;

import android.os.Build;
import android.view.Gravity;

public final class GravityCompat {
    public static int getAbsoluteGravity(int i, int i2) {
        return Build.VERSION.SDK_INT >= 17 ? Gravity.getAbsoluteGravity(i, i2) : i & -8388609;
    }
}

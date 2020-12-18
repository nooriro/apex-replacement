package androidx.core.p002os;

import android.os.Build;

/* renamed from: androidx.core.os.BuildCompat */
public class BuildCompat {
    public static boolean isAtLeastR() {
        return Build.VERSION.SDK_INT >= 30 || Build.VERSION.CODENAME.equals("R");
    }
}

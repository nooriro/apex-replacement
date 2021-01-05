package com.android.internal.util;

import android.compat.annotation.UnsupportedAppUsage;

public class Preconditions {
    @UnsupportedAppUsage
    @Deprecated
    public static <T> T checkNotNull(T t) {
        if (t != null) {
            return t;
        }
        throw null;
    }
}

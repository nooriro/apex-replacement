package com.android.cellbroadcastservice;

import android.telephony.CbGeoUtils;
import java.util.Objects;
import java.util.function.Predicate;

/* renamed from: com.android.cellbroadcastservice.-$$Lambda$zVwfqhEUiMR6AhrXXOoYTRyCUlY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$zVwfqhEUiMR6AhrXXOoYTRyCUlY implements Predicate {
    public static final /* synthetic */ $$Lambda$zVwfqhEUiMR6AhrXXOoYTRyCUlY INSTANCE = new $$Lambda$zVwfqhEUiMR6AhrXXOoYTRyCUlY();

    private /* synthetic */ $$Lambda$zVwfqhEUiMR6AhrXXOoYTRyCUlY() {
    }

    public final boolean test(Object obj) {
        return Objects.nonNull((CbGeoUtils.Geometry) obj);
    }
}

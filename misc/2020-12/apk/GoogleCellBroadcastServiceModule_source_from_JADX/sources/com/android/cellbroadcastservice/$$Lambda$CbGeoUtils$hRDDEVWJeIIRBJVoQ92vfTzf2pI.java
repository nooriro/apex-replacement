package com.android.cellbroadcastservice;

import android.telephony.CbGeoUtils;
import java.util.function.Function;

/* renamed from: com.android.cellbroadcastservice.-$$Lambda$CbGeoUtils$hRDDEVWJeIIRBJVoQ92vfTzf2pI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$CbGeoUtils$hRDDEVWJeIIRBJVoQ92vfTzf2pI implements Function {
    public static final /* synthetic */ $$Lambda$CbGeoUtils$hRDDEVWJeIIRBJVoQ92vfTzf2pI INSTANCE = new $$Lambda$CbGeoUtils$hRDDEVWJeIIRBJVoQ92vfTzf2pI();

    private /* synthetic */ $$Lambda$CbGeoUtils$hRDDEVWJeIIRBJVoQ92vfTzf2pI() {
    }

    public final Object apply(Object obj) {
        return CbGeoUtils.encodeGeometryToString((CbGeoUtils.Geometry) obj);
    }
}

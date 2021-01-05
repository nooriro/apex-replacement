package com.android.cellbroadcastservice;

import com.android.cellbroadcastservice.GsmSmsCbMessage;
import java.util.function.Function;

/* renamed from: com.android.cellbroadcastservice.-$$Lambda$GsmSmsCbMessage$GeoFencingTriggerMessage$cCaceXF16nJkrpBSDyjqvNOyPLg */
/* compiled from: lambda */
public final /* synthetic */ class C0003x9743bc91 implements Function {
    public static final /* synthetic */ C0003x9743bc91 INSTANCE = new C0003x9743bc91();

    private /* synthetic */ C0003x9743bc91() {
    }

    public final Object apply(Object obj) {
        return String.format("(msgId = %d, serial = %d)", new Object[]{Integer.valueOf(((GsmSmsCbMessage.GeoFencingTriggerMessage.CellBroadcastIdentity) obj).messageIdentifier), Integer.valueOf(((GsmSmsCbMessage.GeoFencingTriggerMessage.CellBroadcastIdentity) obj).serialNumber)});
    }
}

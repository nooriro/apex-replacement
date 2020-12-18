package com.android.cellbroadcastservice;

import android.telephony.CellInfo;
import java.util.function.Function;

/* renamed from: com.android.cellbroadcastservice.-$$Lambda$nL5gNcBw4HP0ZZKSqyzehOqju7M  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$nL5gNcBw4HP0ZZKSqyzehOqju7M implements Function {
    public static final /* synthetic */ $$Lambda$nL5gNcBw4HP0ZZKSqyzehOqju7M INSTANCE = new $$Lambda$nL5gNcBw4HP0ZZKSqyzehOqju7M();

    private /* synthetic */ $$Lambda$nL5gNcBw4HP0ZZKSqyzehOqju7M() {
    }

    public final Object apply(Object obj) {
        return ((CellInfo) obj).getCellIdentity();
    }
}

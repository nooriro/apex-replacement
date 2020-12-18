package com.android.cellbroadcastservice;

import android.content.pm.ResolveInfo;
import java.util.function.Function;

/* renamed from: com.android.cellbroadcastservice.-$$Lambda$CellBroadcastHandler$iEUYFMNH-BXxaj1WY_E6q6q7hkw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$CellBroadcastHandler$iEUYFMNHBXxaj1WY_E6q6q7hkw implements Function {
    public static final /* synthetic */ $$Lambda$CellBroadcastHandler$iEUYFMNHBXxaj1WY_E6q6q7hkw INSTANCE = new $$Lambda$CellBroadcastHandler$iEUYFMNHBXxaj1WY_E6q6q7hkw();

    private /* synthetic */ $$Lambda$CellBroadcastHandler$iEUYFMNHBXxaj1WY_E6q6q7hkw() {
    }

    public final Object apply(Object obj) {
        return ((ResolveInfo) obj).activityInfo.applicationInfo.packageName;
    }
}

package com.android.cellbroadcastservice;

import android.telephony.CbGeoUtils;
import android.text.TextUtils;
import android.util.Log;
import java.util.List;
import java.util.stream.Collectors;

public class CbGeoUtils {
    public static String encodeGeometriesToString(List<CbGeoUtils.Geometry> list) {
        return (String) list.stream().map($$Lambda$CbGeoUtils$hRDDEVWJeIIRBJVoQ92vfTzf2pI.INSTANCE).filter($$Lambda$CbGeoUtils$EEEaqcdgTNuZk5SSlhlzkIlblw.INSTANCE).collect(Collectors.joining(";"));
    }

    static /* synthetic */ boolean lambda$encodeGeometriesToString$1(String str) {
        return !TextUtils.isEmpty(str);
    }

    /* access modifiers changed from: private */
    public static String encodeGeometryToString(CbGeoUtils.Geometry geometry) {
        StringBuilder sb = new StringBuilder();
        if (geometry instanceof CbGeoUtils.Polygon) {
            sb.append("polygon");
            for (CbGeoUtils.LatLng latLng : ((CbGeoUtils.Polygon) geometry).getVertices()) {
                sb.append("|");
                sb.append(latLng.lat);
                sb.append(",");
                sb.append(latLng.lng);
            }
        } else if (geometry instanceof CbGeoUtils.Circle) {
            sb.append("circle");
            CbGeoUtils.Circle circle = (CbGeoUtils.Circle) geometry;
            sb.append("|");
            sb.append(circle.getCenter().lat);
            sb.append(",");
            sb.append(circle.getCenter().lng);
            sb.append("|");
            sb.append(circle.getRadius());
        } else {
            Log.e("CbGeoUtils", "Unsupported geometry object " + geometry);
            return null;
        }
        return sb.toString();
    }
}

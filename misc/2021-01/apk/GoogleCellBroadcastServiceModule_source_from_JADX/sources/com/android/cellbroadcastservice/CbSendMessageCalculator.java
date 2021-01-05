package com.android.cellbroadcastservice;

import android.content.Context;
import android.telephony.CbGeoUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CbSendMessageCalculator {
    private int mAction;
    private final boolean mDoNewWay;
    private final List<CbGeoUtils.Geometry> mFences;
    private final double mThresholdMeters;

    public CbSendMessageCalculator(Context context, List<CbGeoUtils.Geometry> list) {
        this(context, list, (double) context.getResources().getInteger(R.integer.geo_fence_threshold));
    }

    public CbSendMessageCalculator(Context context, List<CbGeoUtils.Geometry> list, double d) {
        this.mAction = 0;
        this.mFences = (List) list.stream().filter($$Lambda$zVwfqhEUiMR6AhrXXOoYTRyCUlY.INSTANCE).collect(Collectors.toList());
        this.mThresholdMeters = d;
        this.mDoNewWay = context.getResources().getBoolean(R.bool.use_new_geo_fence_calculation);
    }

    public int getAction() {
        if (this.mFences.size() == 0) {
            return 1;
        }
        return this.mAction;
    }

    /* access modifiers changed from: package-private */
    public String getActionString() {
        int i = this.mAction;
        if (i == 1) {
            return "SEND";
        }
        if (i == 3) {
            return "AMBIGUOUS";
        }
        if (i == 2) {
            return "DONT_SEND";
        }
        return i == 0 ? "NO_COORDINATES" : "!BAD_VALUE!";
    }

    public void addCoordinate(CbGeoUtils.LatLng latLng, double d) {
        if (this.mFences.size() != 0) {
            calculatePersistentAction(latLng, d);
        }
    }

    private void calculatePersistentAction(CbGeoUtils.LatLng latLng, double d) {
        if (this.mAction != 1) {
            int calculateActionFromFences = calculateActionFromFences(latLng, d);
            if (calculateActionFromFences == 1) {
                this.mAction = calculateActionFromFences;
            } else if (this.mAction != 2) {
                this.mAction = calculateActionFromFences;
            }
        }
    }

    private int calculateActionFromFences(CbGeoUtils.LatLng latLng, double d) {
        int i = 2;
        for (int i2 = 0; i2 < this.mFences.size(); i2++) {
            int calculateSingleFence = calculateSingleFence(latLng, d, this.mFences.get(i2));
            if (calculateSingleFence == 1) {
                return calculateSingleFence;
            }
            if (calculateSingleFence == 3) {
                i = 3;
            }
        }
        return i;
    }

    private int calculateSingleFence(CbGeoUtils.LatLng latLng, double d, CbGeoUtils.Geometry geometry) {
        if (geometry.contains(latLng)) {
            return 1;
        }
        if (this.mDoNewWay) {
            return calculateSysSingleFence(latLng, d, geometry);
        }
        return 2;
    }

    private int calculateSysSingleFence(CbGeoUtils.LatLng latLng, double d, CbGeoUtils.Geometry geometry) {
        Optional<Double> distance = CbGeoUtils.distance(geometry, latLng);
        if (!distance.isPresent()) {
            return 2;
        }
        double doubleValue = distance.get().doubleValue();
        double d2 = this.mThresholdMeters;
        if (d <= d2 && doubleValue <= d2) {
            return 1;
        }
        if (doubleValue <= d) {
            return 3;
        }
        return 2;
    }
}

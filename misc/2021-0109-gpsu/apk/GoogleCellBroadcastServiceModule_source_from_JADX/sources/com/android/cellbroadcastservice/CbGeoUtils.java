package com.android.cellbroadcastservice;

import android.telephony.CbGeoUtils;
import android.text.TextUtils;
import android.util.Log;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    public static Optional<Double> distance(CbGeoUtils.Geometry geometry, CbGeoUtils.LatLng latLng) {
        if (geometry instanceof CbGeoUtils.Polygon) {
            return Optional.of(Double.valueOf(new DistancePolygon((CbGeoUtils.Polygon) geometry).distance(latLng)));
        }
        if (geometry instanceof CbGeoUtils.Circle) {
            return Optional.of(Double.valueOf(new DistanceCircle((CbGeoUtils.Circle) geometry).distance(latLng)));
        }
        return Optional.empty();
    }

    public static class DistanceCircle {
        private final CbGeoUtils.Circle mCircle;

        DistanceCircle(CbGeoUtils.Circle circle) {
            this.mCircle = circle;
        }

        public double distance(CbGeoUtils.LatLng latLng) {
            return latLng.distance(this.mCircle.getCenter()) - this.mCircle.getRadius();
        }
    }

    public static class DistancePolygon {
        private final CbGeoUtils.LatLng mOrigin;
        private final CbGeoUtils.Polygon mPolygon;

        public DistancePolygon(CbGeoUtils.Polygon polygon) {
            this.mPolygon = polygon;
            int i = 0;
            for (int i2 = 1; i2 < polygon.getVertices().size(); i2++) {
                if (((CbGeoUtils.LatLng) polygon.getVertices().get(i2)).lng < ((CbGeoUtils.LatLng) polygon.getVertices().get(i)).lng) {
                    i = i2;
                }
            }
            this.mOrigin = (CbGeoUtils.LatLng) polygon.getVertices().get(i);
        }

        public double distance(CbGeoUtils.LatLng latLng) {
            List vertices = this.mPolygon.getVertices();
            int size = this.mPolygon.getVertices().size();
            double d = Double.MAX_VALUE;
            int i = 0;
            while (i < size) {
                i++;
                Point convertToDistanceFromOrigin = convertToDistanceFromOrigin((CbGeoUtils.LatLng) vertices.get(i));
                Point convertToDistanceFromOrigin2 = convertToDistanceFromOrigin((CbGeoUtils.LatLng) vertices.get(i % size));
                d = Math.min(new LineSegment(convertToDistanceFromOrigin, convertToDistanceFromOrigin2).distance(convertToDistanceFromOrigin(latLng)), d);
            }
            return d;
        }

        private Point convertToDistanceFromOrigin(CbGeoUtils.LatLng latLng) {
            return CbGeoUtils.convertToDistanceFromOrigin(this.mOrigin, latLng);
        }
    }

    public static Point convertToDistanceFromOrigin(CbGeoUtils.LatLng latLng, CbGeoUtils.LatLng latLng2) {
        double distance = new CbGeoUtils.LatLng(latLng2.lat, latLng.lng).distance(new CbGeoUtils.LatLng(latLng.lat, latLng.lng));
        double distance2 = new CbGeoUtils.LatLng(latLng.lat, latLng2.lng).distance(new CbGeoUtils.LatLng(latLng.lat, latLng.lng));
        if (latLng2.lat <= latLng.lat) {
            distance = -distance;
        }
        if (latLng2.lng <= latLng.lng) {
            distance2 = -distance2;
        }
        return new Point(distance, distance2);
    }

    public static class Point {

        /* renamed from: x */
        public final double f2x;

        /* renamed from: y */
        public final double f3y;

        public Point(double d, double d2) {
            this.f2x = d;
            this.f3y = d2;
        }

        public Point subtract(Point point) {
            return new Point(this.f2x - point.f2x, this.f3y - point.f3y);
        }

        public double distance(Point point) {
            return Math.sqrt(Math.pow(this.f2x - point.f2x, 2.0d) + Math.pow(this.f3y - point.f3y, 2.0d));
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || Point.class != obj.getClass()) {
                return false;
            }
            Point point = (Point) obj;
            if (Double.compare(point.f2x, this.f2x) == 0 && Double.compare(point.f3y, this.f3y) == 0) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Double.valueOf(this.f2x), Double.valueOf(this.f3y)});
        }

        public String toString() {
            return "(" + this.f2x + ", " + this.f3y + ")";
        }
    }

    public static final class LineSegment {

        /* renamed from: a */
        final Point f0a;

        /* renamed from: b */
        final Point f1b;

        private static double calcProjCoordinate(double d, double d2, double d3) {
            return d + ((d2 - d) * d3);
        }

        public LineSegment(Point point, Point point2) {
            this.f0a = point;
            this.f1b = point2;
        }

        public double getLength() {
            return this.f0a.distance(this.f1b);
        }

        public double distance(Point point) {
            double length = getLength() * getLength();
            if (length == 0.0d) {
                return point.distance(this.f0a);
            }
            Point subtract = point.subtract(this.f0a);
            Point subtract2 = this.f1b.subtract(this.f0a);
            double d = ((subtract.f2x * subtract2.f2x) + (subtract.f3y * subtract2.f3y)) / length;
            double d2 = 1.0d;
            if (d <= 1.0d) {
                d2 = d < 0.0d ? 0.0d : d;
            }
            double d3 = d2;
            return new Point(calcProjCoordinate(this.f0a.f2x, this.f1b.f2x, d3), calcProjCoordinate(this.f0a.f3y, this.f1b.f3y, d3)).distance(point);
        }
    }
}

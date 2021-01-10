package com.android.cellbroadcastservice;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.CbGeoUtils;
import android.util.Log;
import android.util.Pair;
import com.android.cellbroadcastservice.SmsCbHeader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GsmSmsCbMessage {
    private static final String TAG = "GsmSmsCbMessage";

    public static String getEtwsPrimaryMessage(Context context, int i) {
        Resources resources = context.getResources();
        if (i == 0) {
            return resources.getString(R.string.etws_primary_default_message_earthquake);
        }
        if (i == 1) {
            return resources.getString(R.string.etws_primary_default_message_tsunami);
        }
        if (i == 2) {
            return resources.getString(R.string.etws_primary_default_message_earthquake_and_tsunami);
        }
        if (i != 3) {
            return i != 4 ? "" : resources.getString(R.string.etws_primary_default_message_others);
        }
        return resources.getString(R.string.etws_primary_default_message_test);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v2, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.telephony.SmsCbMessage createSmsCbMessage(android.content.Context r22, com.android.cellbroadcastservice.SmsCbHeader r23, android.telephony.SmsCbLocation r24, byte[][] r25, int r26) throws java.lang.IllegalArgumentException {
        /*
            r0 = r22
            r1 = r23
            r2 = r25
            java.lang.String r3 = "telephony_subscription_service"
            java.lang.Object r3 = r0.getSystemService(r3)
            android.telephony.SubscriptionManager r3 = (android.telephony.SubscriptionManager) r3
            r8 = r26
            int[] r3 = r3.getSubscriptionIds(r8)
            r4 = 0
            if (r3 == 0) goto L_0x001d
            int r5 = r3.length
            if (r5 <= 0) goto L_0x001d
            r3 = r3[r4]
            goto L_0x0020
        L_0x001d:
            r3 = 2147483647(0x7fffffff, float:NaN)
        L_0x0020:
            r21 = r3
            long r18 = java.lang.System.currentTimeMillis()
            boolean r3 = r23.isEtwsPrimaryNotification()
            if (r3 == 0) goto L_0x0062
            android.telephony.SmsCbMessage r2 = new android.telephony.SmsCbMessage
            r4 = r2
            r5 = 1
            int r6 = r23.getGeographicalScope()
            int r7 = r23.getSerialNumber()
            int r9 = r23.getServiceCategory()
            r10 = 0
            int r11 = r23.getDataCodingScheme()
            android.telephony.SmsCbEtwsInfo r3 = r23.getEtwsInfo()
            int r3 = r3.getWarningType()
            java.lang.String r12 = getEtwsPrimaryMessage(r0, r3)
            r13 = 3
            android.telephony.SmsCbEtwsInfo r14 = r23.getEtwsInfo()
            android.telephony.SmsCbCmasInfo r15 = r23.getCmasInfo()
            r16 = 0
            r17 = 0
            r8 = r24
            r20 = r26
            r4.<init>(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r20, r21)
            return r2
        L_0x0062:
            boolean r0 = r23.isUmtsFormat()
            r3 = 3
            r5 = 0
            if (r0 == 0) goto L_0x00e7
            r0 = r2[r4]
            android.util.Pair r2 = parseUmtsBody(r1, r0)
            java.lang.Object r6 = r2.first
            r10 = r6
            java.lang.String r10 = (java.lang.String) r10
            java.lang.Object r2 = r2.second
            r12 = r2
            java.lang.String r12 = (java.lang.String) r12
            boolean r2 = r23.isEmergencyMessage()
            if (r2 == 0) goto L_0x0082
            r13 = r3
            goto L_0x0083
        L_0x0082:
            r13 = r4
        L_0x0083:
            r2 = 6
            byte r2 = r0[r2]
            int r2 = r2 * 83
            int r2 = r2 + 7
            r3 = 255(0xff, float:3.57E-43)
            int r4 = r0.length
            if (r4 <= r2) goto L_0x00bf
            android.util.Pair r0 = parseWarningAreaCoordinates(r0, r2)     // Catch:{ Exception -> 0x00a4 }
            java.lang.Object r2 = r0.first     // Catch:{ Exception -> 0x00a4 }
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ Exception -> 0x00a4 }
            int r3 = r2.intValue()     // Catch:{ Exception -> 0x00a4 }
            java.lang.Object r0 = r0.second     // Catch:{ Exception -> 0x00a4 }
            java.util.List r0 = (java.util.List) r0     // Catch:{ Exception -> 0x00a4 }
            r17 = r0
            r16 = r3
            goto L_0x00c3
        L_0x00a4:
            r0 = move-exception
            java.lang.String r2 = TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "Can't parse warning area coordinates, ex = "
            r4.append(r6)
            java.lang.String r0 = r0.toString()
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            android.util.Log.e(r2, r0)
        L_0x00bf:
            r16 = r3
            r17 = r5
        L_0x00c3:
            android.telephony.SmsCbMessage r0 = new android.telephony.SmsCbMessage
            r4 = r0
            r5 = 1
            int r6 = r23.getGeographicalScope()
            int r7 = r23.getSerialNumber()
            int r9 = r23.getServiceCategory()
            int r11 = r23.getDataCodingScheme()
            android.telephony.SmsCbEtwsInfo r14 = r23.getEtwsInfo()
            android.telephony.SmsCbCmasInfo r15 = r23.getCmasInfo()
            r8 = r24
            r20 = r26
            r4.<init>(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r20, r21)
            return r0
        L_0x00e7:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r6 = r2.length
            r10 = r5
            r5 = r4
        L_0x00ef:
            if (r5 >= r6) goto L_0x0106
            r7 = r2[r5]
            android.util.Pair r7 = parseGsmBody(r1, r7)
            java.lang.Object r8 = r7.first
            r10 = r8
            java.lang.String r10 = (java.lang.String) r10
            java.lang.Object r7 = r7.second
            java.lang.String r7 = (java.lang.String) r7
            r0.append(r7)
            int r5 = r5 + 1
            goto L_0x00ef
        L_0x0106:
            boolean r2 = r23.isEmergencyMessage()
            if (r2 == 0) goto L_0x010e
            r13 = r3
            goto L_0x010f
        L_0x010e:
            r13 = r4
        L_0x010f:
            android.telephony.SmsCbMessage r2 = new android.telephony.SmsCbMessage
            r4 = r2
            r5 = 1
            int r6 = r23.getGeographicalScope()
            int r7 = r23.getSerialNumber()
            int r9 = r23.getServiceCategory()
            int r11 = r23.getDataCodingScheme()
            java.lang.String r12 = r0.toString()
            android.telephony.SmsCbEtwsInfo r14 = r23.getEtwsInfo()
            android.telephony.SmsCbCmasInfo r15 = r23.getCmasInfo()
            r16 = 0
            r17 = 0
            r8 = r24
            r20 = r26
            r4.<init>(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r20, r21)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastservice.GsmSmsCbMessage.createSmsCbMessage(android.content.Context, com.android.cellbroadcastservice.SmsCbHeader, android.telephony.SmsCbLocation, byte[][], int):android.telephony.SmsCbMessage");
    }

    public static GeoFencingTriggerMessage createGeoFencingTriggerMessage(byte[] bArr) {
        try {
            BitStreamReader bitStreamReader = new BitStreamReader(bArr, 7);
            int read = bitStreamReader.read(4);
            int read2 = bitStreamReader.read(7);
            bitStreamReader.skip();
            int i = ((read2 - 2) * 8) / 32;
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < i; i2++) {
                arrayList.add(new GeoFencingTriggerMessage.CellBroadcastIdentity(bitStreamReader.read(16), bitStreamReader.read(16)));
            }
            return new GeoFencingTriggerMessage(read, arrayList);
        } catch (Exception e) {
            String str = "create geo-fencing trigger failed, ex = " + e.toString();
            Log.e(TAG, str);
            CellBroadcastStatsLog.write(250, 8, str);
            return null;
        }
    }

    private static Pair<Integer, List<CbGeoUtils.Geometry>> parseWarningAreaCoordinates(byte[] bArr, int i) {
        int i2 = 255;
        int i3 = ((bArr[i + 1] & 255) << 8) | (bArr[i] & 255);
        int i4 = i + 2;
        int i5 = i4 + i3;
        if (i5 <= bArr.length) {
            BitStreamReader bitStreamReader = new BitStreamReader(bArr, i4);
            ArrayList arrayList = new ArrayList();
            while (i3 > 0) {
                int read = bitStreamReader.read(4);
                int read2 = bitStreamReader.read(10);
                i3 -= read2;
                bitStreamReader.skip();
                if (read == 1) {
                    i2 = bitStreamReader.read(8);
                } else if (read == 2) {
                    ArrayList arrayList2 = new ArrayList();
                    int i6 = ((read2 - 2) * 8) / 44;
                    for (int i7 = 0; i7 < i6; i7++) {
                        arrayList2.add(getLatLng(bitStreamReader));
                    }
                    bitStreamReader.skip();
                    arrayList.add(new CbGeoUtils.Polygon(arrayList2));
                } else if (read == 3) {
                    arrayList.add(new CbGeoUtils.Circle(getLatLng(bitStreamReader), ((((double) bitStreamReader.read(20)) * 1.0d) / 64.0d) * 1000.0d));
                } else {
                    IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Unsupported geoType = " + read);
                    CellBroadcastStatsLog.write(250, 9, illegalArgumentException.toString());
                    throw illegalArgumentException;
                }
            }
            return new Pair<>(Integer.valueOf(i2), arrayList);
        }
        IllegalArgumentException illegalArgumentException2 = new IllegalArgumentException("Invalid wac data, expected the length of pdu at least " + i5 + ", actual is " + bArr.length);
        CellBroadcastStatsLog.write(250, 9, illegalArgumentException2.toString());
        throw illegalArgumentException2;
    }

    private static CbGeoUtils.LatLng getLatLng(BitStreamReader bitStreamReader) {
        return new CbGeoUtils.LatLng(((((double) bitStreamReader.read(22)) * 180.0d) / 4194304.0d) - 90.0d, ((((double) bitStreamReader.read(22)) * 360.0d) / 4194304.0d) - 180.0d);
    }

    private static Pair<String, String> parseUmtsBody(SmsCbHeader smsCbHeader, byte[] bArr) {
        byte b = bArr[6];
        String str = smsCbHeader.getDataCodingSchemeStructedData().language;
        if (bArr.length >= (b * 83) + 7) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < b) {
                int i2 = (i * 83) + 7;
                byte b2 = bArr[i2 + 82];
                if (b2 <= 82) {
                    Pair<String, String> unpackBody = unpackBody(bArr, i2, b2, smsCbHeader.getDataCodingSchemeStructedData());
                    sb.append((String) unpackBody.second);
                    i++;
                    str = (String) unpackBody.first;
                } else {
                    throw new IllegalArgumentException("Page length " + b2 + " exceeds maximum value " + 82);
                }
            }
            return new Pair<>(str, sb.toString());
        }
        throw new IllegalArgumentException("Pdu length " + bArr.length + " does not match " + b + " pages");
    }

    private static Pair<String, String> parseGsmBody(SmsCbHeader smsCbHeader, byte[] bArr) {
        return unpackBody(bArr, 6, bArr.length - 6, smsCbHeader.getDataCodingSchemeStructedData());
    }

    private static Pair<String, String> unpackBody(byte[] bArr, int i, int i2, SmsCbHeader.DataCodingScheme dataCodingScheme) {
        String str;
        int i3;
        String str2 = dataCodingScheme.language;
        int i4 = dataCodingScheme.encoding;
        if (i4 == 1) {
            str = GsmAlphabet.gsm7BitPackedToString(bArr, i, (i2 * 8) / 7);
            if (dataCodingScheme.hasLanguageIndicator && str != null && str.length() > 2) {
                str2 = str.substring(0, 2);
                str = str.substring(3);
            }
        } else if (i4 == 2) {
            str = GsmAlphabet.gsm8BitUnpackedToString(bArr, i, i2);
        } else if (i4 != 3) {
            str = null;
        } else {
            if (dataCodingScheme.hasLanguageIndicator && bArr.length >= (i3 = i + 2)) {
                str2 = GsmAlphabet.gsm7BitPackedToString(bArr, i, 2);
                i2 -= 2;
                i = i3;
            }
            try {
                str = new String(bArr, i, i2 & 65534, "utf-16");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Error decoding UTF-16 message", e);
            }
        }
        if (str != null) {
            int length = str.length() - 1;
            while (true) {
                if (length < 0) {
                    break;
                } else if (str.charAt(length) != 13) {
                    str = str.substring(0, length + 1);
                    break;
                } else {
                    length--;
                }
            }
        } else {
            str = "";
        }
        return new Pair<>(str2, str);
    }

    private static final class BitStreamReader {
        private int mCurrentOffset;
        private final byte[] mData;
        private int mRemainedBit = 8;

        BitStreamReader(byte[] bArr, int i) {
            this.mData = bArr;
            this.mCurrentOffset = i;
        }

        public int read(int i) throws IndexOutOfBoundsException {
            byte b = 0;
            while (i > 0) {
                int i2 = this.mRemainedBit;
                if (i >= i2) {
                    byte[] bArr = this.mData;
                    int i3 = this.mCurrentOffset;
                    b = (b << i2) | (bArr[i3] & ((1 << i2) - 1));
                    i -= i2;
                    this.mRemainedBit = 8;
                    this.mCurrentOffset = i3 + 1;
                } else {
                    b = (b << i) | ((this.mData[this.mCurrentOffset] & ((1 << i2) - 1)) >> (i2 - i));
                    this.mRemainedBit = i2 - i;
                    i = 0;
                }
            }
            return b;
        }

        public void skip() {
            if (this.mRemainedBit < 8) {
                this.mRemainedBit = 8;
                this.mCurrentOffset++;
            }
        }
    }

    public static final class GeoFencingTriggerMessage {
        public final List<CellBroadcastIdentity> cbIdentifiers;
        public final int type;

        GeoFencingTriggerMessage(int i, List<CellBroadcastIdentity> list) {
            this.type = i;
            this.cbIdentifiers = list;
        }

        /* access modifiers changed from: package-private */
        public boolean shouldShareBroadcastArea() {
            return this.type == 2;
        }

        public static final class CellBroadcastIdentity {
            public final int messageIdentifier;
            public final int serialNumber;

            CellBroadcastIdentity(int i, int i2) {
                this.messageIdentifier = i;
                this.serialNumber = i2;
            }
        }

        public String toString() {
            return "triggerType=" + this.type + " identifiers=" + ((String) this.cbIdentifiers.stream().map(C0003x9743bc91.INSTANCE).collect(Collectors.joining(",")));
        }
    }
}

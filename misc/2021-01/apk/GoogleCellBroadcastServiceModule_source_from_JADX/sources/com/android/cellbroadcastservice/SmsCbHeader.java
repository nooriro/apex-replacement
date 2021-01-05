package com.android.cellbroadcastservice;

import android.telephony.SmsCbCmasInfo;
import android.telephony.SmsCbEtwsInfo;
import java.util.Arrays;
import java.util.Locale;

public class SmsCbHeader {
    /* access modifiers changed from: private */
    public static final String[] LANGUAGE_CODES_GROUP_0 = {Locale.GERMAN.getLanguage(), Locale.ENGLISH.getLanguage(), Locale.ITALIAN.getLanguage(), Locale.FRENCH.getLanguage(), new Locale("es").getLanguage(), new Locale("nl").getLanguage(), new Locale("sv").getLanguage(), new Locale("da").getLanguage(), new Locale("pt").getLanguage(), new Locale("fi").getLanguage(), new Locale("nb").getLanguage(), new Locale("el").getLanguage(), new Locale("tr").getLanguage(), new Locale("hu").getLanguage(), new Locale("pl").getLanguage(), null};
    /* access modifiers changed from: private */
    public static final String[] LANGUAGE_CODES_GROUP_2 = {new Locale("cs").getLanguage(), new Locale("he").getLanguage(), new Locale("ar").getLanguage(), new Locale("ru").getLanguage(), new Locale("is").getLanguage(), null, null, null, null, null, null, null, null, null, null, null};
    private final SmsCbCmasInfo mCmasInfo;
    private final int mDataCodingScheme;
    private DataCodingScheme mDataCodingSchemeStructedData;
    private final SmsCbEtwsInfo mEtwsInfo;
    private final int mFormat;
    private final int mGeographicalScope;
    private final int mMessageIdentifier;
    private final int mNrOfPages;
    private final int mPageIndex;
    private final int mSerialNumber;

    public SmsCbHeader(byte[] bArr) throws IllegalArgumentException {
        byte[] bArr2 = bArr;
        if (bArr2 == null || bArr2.length < 6) {
            CellBroadcastStatsLog.write(250, 4, "Illegal PDU");
            throw new IllegalArgumentException("Illegal PDU");
        }
        int i = 1;
        if (bArr2.length <= 88) {
            this.mGeographicalScope = (bArr2[0] & 192) >>> 6;
            this.mSerialNumber = ((bArr2[0] & 255) << 8) | (bArr2[1] & 255);
            this.mMessageIdentifier = ((bArr2[2] & 255) << 8) | (bArr2[3] & 255);
            if (!isEtwsMessage() || bArr2.length > 56) {
                this.mFormat = 1;
                this.mDataCodingScheme = bArr2[4] & 255;
                int i2 = (bArr2[5] & 240) >>> 4;
                byte b = bArr2[5] & 15;
                if (i2 == 0 || b == 0 || i2 > b) {
                    b = 1;
                } else {
                    i = i2;
                }
                this.mPageIndex = i;
                this.mNrOfPages = b;
            } else {
                this.mFormat = 3;
                this.mDataCodingScheme = -1;
                this.mPageIndex = -1;
                this.mNrOfPages = -1;
                this.mEtwsInfo = new SmsCbEtwsInfo((bArr2[4] & 254) >>> 1, (bArr2[4] & 1) != 0, (bArr2[5] & 128) != 0, true, bArr2.length > 6 ? Arrays.copyOfRange(bArr2, 6, bArr2.length) : null);
                this.mCmasInfo = null;
                return;
            }
        } else {
            this.mFormat = 2;
            byte b2 = bArr2[0];
            if (b2 == 1) {
                this.mMessageIdentifier = ((bArr2[1] & 255) << 8) | (bArr2[2] & 255);
                this.mGeographicalScope = (bArr2[3] & 192) >>> 6;
                this.mSerialNumber = ((bArr2[3] & 255) << 8) | (bArr2[4] & 255);
                this.mDataCodingScheme = bArr2[5] & 255;
                this.mPageIndex = 1;
                this.mNrOfPages = 1;
            } else {
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Unsupported message type " + b2);
                CellBroadcastStatsLog.write(250, 5, illegalArgumentException.toString());
                throw illegalArgumentException;
            }
        }
        int i3 = this.mDataCodingScheme;
        if (i3 != -1) {
            this.mDataCodingSchemeStructedData = new DataCodingScheme(i3);
        }
        if (isEtwsMessage()) {
            this.mEtwsInfo = new SmsCbEtwsInfo(getEtwsWarningType(), isEtwsEmergencyUserAlert(), isEtwsPopupAlert(), false, (byte[]) null);
            this.mCmasInfo = null;
        } else if (isCmasMessage()) {
            int cmasMessageClass = getCmasMessageClass();
            int cmasSeverity = getCmasSeverity();
            int cmasUrgency = getCmasUrgency();
            int cmasCertainty = getCmasCertainty();
            this.mEtwsInfo = null;
            this.mCmasInfo = new SmsCbCmasInfo(cmasMessageClass, -1, -1, cmasSeverity, cmasUrgency, cmasCertainty);
        } else {
            this.mEtwsInfo = null;
            this.mCmasInfo = null;
        }
    }

    public int getGeographicalScope() {
        return this.mGeographicalScope;
    }

    public int getSerialNumber() {
        return this.mSerialNumber;
    }

    public int getServiceCategory() {
        return this.mMessageIdentifier;
    }

    public int getDataCodingScheme() {
        return this.mDataCodingScheme;
    }

    public DataCodingScheme getDataCodingSchemeStructedData() {
        return this.mDataCodingSchemeStructedData;
    }

    public int getPageIndex() {
        return this.mPageIndex;
    }

    public int getNumberOfPages() {
        return this.mNrOfPages;
    }

    public SmsCbEtwsInfo getEtwsInfo() {
        return this.mEtwsInfo;
    }

    public SmsCbCmasInfo getCmasInfo() {
        return this.mCmasInfo;
    }

    public boolean isEmergencyMessage() {
        int i = this.mMessageIdentifier;
        return i >= 4352 && i <= 6399;
    }

    public boolean isEtwsMessage() {
        return (this.mMessageIdentifier & 65528) == 4352;
    }

    public boolean isEtwsPrimaryNotification() {
        return this.mFormat == 3;
    }

    public boolean isUmtsFormat() {
        return this.mFormat == 2;
    }

    private boolean isCmasMessage() {
        int i = this.mMessageIdentifier;
        return i >= 4370 && i <= 4400;
    }

    private boolean isEtwsPopupAlert() {
        return (this.mSerialNumber & 4096) != 0;
    }

    private boolean isEtwsEmergencyUserAlert() {
        return (this.mSerialNumber & 8192) != 0;
    }

    private int getEtwsWarningType() {
        return this.mMessageIdentifier - 4352;
    }

    private int getCmasMessageClass() {
        switch (this.mMessageIdentifier) {
            case 4370:
            case 4383:
                return 0;
            case 4371:
            case 4372:
            case 4384:
            case 4385:
                return 1;
            case 4373:
            case 4374:
            case 4375:
            case 4376:
            case 4377:
            case 4378:
            case 4386:
            case 4387:
            case 4388:
            case 4389:
            case 4390:
            case 4391:
                return 2;
            case 4379:
            case 4392:
                return 3;
            case 4380:
            case 4393:
                return 4;
            case 4381:
            case 4394:
                return 5;
            case 4382:
            case 4395:
                return 6;
            default:
                return -1;
        }
    }

    private int getCmasSeverity() {
        int i = this.mMessageIdentifier;
        switch (i) {
            case 4371:
            case 4372:
            case 4373:
            case 4374:
                return 0;
            case 4375:
            case 4376:
            case 4377:
            case 4378:
                return 1;
            default:
                switch (i) {
                    case 4384:
                    case 4385:
                    case 4386:
                    case 4387:
                        return 0;
                    case 4388:
                    case 4389:
                    case 4390:
                    case 4391:
                        return 1;
                    default:
                        return -1;
                }
        }
    }

    private int getCmasUrgency() {
        int i = this.mMessageIdentifier;
        switch (i) {
            case 4371:
            case 4372:
            case 4375:
            case 4376:
                return 0;
            case 4373:
            case 4374:
            case 4377:
            case 4378:
                return 1;
            default:
                switch (i) {
                    case 4384:
                    case 4385:
                    case 4388:
                    case 4389:
                        return 0;
                    case 4386:
                    case 4387:
                    case 4390:
                    case 4391:
                        return 1;
                    default:
                        return -1;
                }
        }
    }

    private int getCmasCertainty() {
        int i = this.mMessageIdentifier;
        switch (i) {
            case 4371:
            case 4373:
            case 4375:
            case 4377:
                return 0;
            case 4372:
            case 4374:
            case 4376:
            case 4378:
                return 1;
            default:
                switch (i) {
                    case 4384:
                    case 4386:
                    case 4388:
                    case 4390:
                        return 0;
                    case 4385:
                    case 4387:
                    case 4389:
                    case 4391:
                        return 1;
                    default:
                        return -1;
                }
        }
    }

    public String toString() {
        return "SmsCbHeader{GS=" + this.mGeographicalScope + ", serialNumber=0x" + Integer.toHexString(this.mSerialNumber) + ", messageIdentifier=0x" + Integer.toHexString(this.mMessageIdentifier) + ", format=" + this.mFormat + ", DCS=0x" + Integer.toHexString(this.mDataCodingScheme) + ", page " + this.mPageIndex + " of " + this.mNrOfPages + '}';
    }

    public static final class DataCodingScheme {
        public final int encoding;
        public final boolean hasLanguageIndicator;
        public final String language;

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x003b, code lost:
            r1 = 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0041, code lost:
            if (((r8 & 4) >> 2) == 1) goto L_0x0024;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0021, code lost:
            if (r8 != 2) goto L_0x003b;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public DataCodingScheme(int r8) {
            /*
                r7 = this;
                r7.<init>()
                r0 = r8 & 240(0xf0, float:3.36E-43)
                int r0 = r0 >> 4
                r1 = 9
                if (r0 == r1) goto L_0x004b
                r1 = 14
                if (r0 == r1) goto L_0x004b
                r1 = 3
                r2 = 15
                r3 = 2
                r4 = 1
                r5 = 0
                r6 = 0
                if (r0 == r2) goto L_0x003e
                switch(r0) {
                    case 0: goto L_0x0034;
                    case 1: goto L_0x002e;
                    case 2: goto L_0x0026;
                    case 3: goto L_0x003b;
                    case 4: goto L_0x001c;
                    case 5: goto L_0x001c;
                    case 6: goto L_0x004b;
                    case 7: goto L_0x004b;
                    default: goto L_0x001b;
                }
            L_0x001b:
                goto L_0x003b
            L_0x001c:
                r8 = r8 & 12
                int r8 = r8 >> r3
                if (r8 == r4) goto L_0x0024
                if (r8 == r3) goto L_0x003c
                goto L_0x003b
            L_0x0024:
                r1 = r3
                goto L_0x003c
            L_0x0026:
                java.lang.String[] r0 = com.android.cellbroadcastservice.SmsCbHeader.LANGUAGE_CODES_GROUP_2
                r8 = r8 & r2
                r5 = r0[r8]
                goto L_0x003b
            L_0x002e:
                r8 = r8 & r2
                if (r8 != r4) goto L_0x0032
                goto L_0x0044
            L_0x0032:
                r1 = r4
                goto L_0x0044
            L_0x0034:
                java.lang.String[] r0 = com.android.cellbroadcastservice.SmsCbHeader.LANGUAGE_CODES_GROUP_0
                r8 = r8 & r2
                r5 = r0[r8]
            L_0x003b:
                r1 = r4
            L_0x003c:
                r4 = r6
                goto L_0x0044
            L_0x003e:
                r8 = r8 & 4
                int r8 = r8 >> r3
                if (r8 != r4) goto L_0x003b
                goto L_0x0024
            L_0x0044:
                r7.encoding = r1
                r7.language = r5
                r7.hasLanguageIndicator = r4
                return
            L_0x004b:
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                java.lang.String r0 = "Unsupported GSM dataCodingScheme "
                r7.append(r0)
                r7.append(r8)
                java.lang.String r7 = r7.toString()
                r8 = 250(0xfa, float:3.5E-43)
                r0 = 6
                com.android.cellbroadcastservice.CellBroadcastStatsLog.write((int) r8, (int) r0, (java.lang.String) r7)
                java.lang.IllegalArgumentException r8 = new java.lang.IllegalArgumentException
                r8.<init>(r7)
                throw r8
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.cellbroadcastservice.SmsCbHeader.DataCodingScheme.<init>(int):void");
        }
    }
}

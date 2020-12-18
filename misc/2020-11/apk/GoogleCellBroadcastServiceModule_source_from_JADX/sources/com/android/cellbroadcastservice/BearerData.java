package com.android.cellbroadcastservice;

import android.content.Context;
import android.telephony.SmsCbCmasInfo;
import android.util.Log;
import com.android.cellbroadcastservice.BitwiseInputStream;
import java.io.UnsupportedEncodingException;

public final class BearerData {
    public SmsCbCmasInfo cmasWarningInfo;
    public boolean hasUserDataHeader;
    public int language = 0;
    public int messageId;
    public int priority = 0;
    public UserData userData;

    private static String getLanguageCodeForValue(int i) {
        switch (i) {
            case 1:
                return "en";
            case 2:
                return "fr";
            case 3:
                return "es";
            case CellBroadcastProvider.DATABASE_VERSION /*4*/:
                return "ja";
            case 5:
                return "ko";
            case 6:
                return "zh";
            case 7:
                return "he";
            default:
                return null;
        }
    }

    private static boolean isCmasAlertCategory(int i) {
        return i >= 4096 && i <= 4351;
    }

    private static int serviceCategoryToCmasMessageClass(int i) {
        switch (i) {
            case 4096:
                return 0;
            case 4097:
                return 1;
            case 4098:
                return 2;
            case 4099:
                return 3;
            case 4100:
                return 4;
            default:
                return -1;
        }
    }

    private BearerData() {
    }

    private static class CodingException extends Exception {
        public CodingException(String str) {
            super(str);
        }
    }

    public String getLanguage() {
        return getLanguageCodeForValue(this.language);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BearerData ");
        sb.append(", messageId=" + this.messageId);
        sb.append(", hasUserDataHeader=" + this.hasUserDataHeader);
        sb.append(", userData=" + this.userData);
        sb.append(" }");
        return sb.toString();
    }

    private static boolean decodeMessageId(BearerData bearerData, BitwiseInputStream bitwiseInputStream) throws BitwiseInputStream.AccessException {
        int read = bitwiseInputStream.read(8) * 8;
        boolean z = false;
        if (read >= 24) {
            read -= 24;
            bitwiseInputStream.skip(4);
            int read2 = bitwiseInputStream.read(8) << 8;
            bearerData.messageId = read2;
            bearerData.messageId = bitwiseInputStream.read(8) | read2;
            if (bitwiseInputStream.read(1) == 1) {
                z = true;
            }
            bearerData.hasUserDataHeader = z;
            bitwiseInputStream.skip(3);
            z = true;
        }
        if (!z || read > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("MESSAGE_IDENTIFIER decode ");
            sb.append(z ? "succeeded" : "failed");
            sb.append(" (extra bits = ");
            sb.append(read);
            sb.append(")");
            Log.d("BearerData", sb.toString());
        }
        bitwiseInputStream.skip(read);
        return z;
    }

    private static boolean decodeReserved(BitwiseInputStream bitwiseInputStream, int i) throws BitwiseInputStream.AccessException, CodingException {
        boolean z;
        int read = bitwiseInputStream.read(8);
        int i2 = read * 8;
        if (i2 <= bitwiseInputStream.available()) {
            z = true;
            bitwiseInputStream.skip(i2);
        } else {
            z = false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("RESERVED bearer data subparameter ");
        sb.append(i);
        sb.append(" decode ");
        sb.append(z ? "succeeded" : "failed");
        sb.append(" (param bits = ");
        sb.append(i2);
        sb.append(")");
        Log.d("BearerData", sb.toString());
        if (z) {
            return z;
        }
        throw new CodingException("RESERVED bearer data subparameter " + i + " had invalid SUBPARAM_LEN " + read);
    }

    private static boolean decodeUserData(BearerData bearerData, BitwiseInputStream bitwiseInputStream) throws BitwiseInputStream.AccessException {
        int read = bitwiseInputStream.read(8) * 8;
        UserData userData2 = new UserData();
        bearerData.userData = userData2;
        int i = 5;
        userData2.msgEncoding = bitwiseInputStream.read(5);
        UserData userData3 = bearerData.userData;
        userData3.msgEncodingSet = true;
        userData3.msgType = 0;
        int i2 = userData3.msgEncoding;
        if (i2 == 1 || i2 == 10) {
            bearerData.userData.msgType = bitwiseInputStream.read(8);
            i = 13;
        }
        bearerData.userData.numFields = bitwiseInputStream.read(8);
        bearerData.userData.payload = bitwiseInputStream.readByteArray(read - (i + 8));
        return true;
    }

    private static String decodeUtf8(byte[] bArr, int i, int i2) throws CodingException {
        return decodeCharset(bArr, i, i2, 1, "UTF-8");
    }

    private static String decodeUtf16(byte[] bArr, int i, int i2) throws CodingException {
        return decodeCharset(bArr, i, i2 - (((i % 2) + i) / 2), 2, "utf-16be");
    }

    private static String decodeCharset(byte[] bArr, int i, int i2, int i3, String str) throws CodingException {
        if (i2 < 0 || (i2 * i3) + i > bArr.length) {
            int length = ((bArr.length - i) - (i % i3)) / i3;
            if (length >= 0) {
                Log.e("BearerData", str + " decode error: offset = " + i + " numFields = " + i2 + " data.length = " + bArr.length + " maxNumFields = " + length);
                i2 = length;
            } else {
                throw new CodingException(str + " decode failed: offset out of range");
            }
        }
        try {
            return new String(bArr, i, i2 * i3, str);
        } catch (UnsupportedEncodingException e) {
            throw new CodingException(str + " decode failed: " + e);
        }
    }

    private static String decode7bitAscii(byte[] bArr, int i, int i2) throws CodingException {
        try {
            int i3 = ((i * 8) + 6) / 7;
            int i4 = i2 - i3;
            StringBuffer stringBuffer = new StringBuffer(i4);
            BitwiseInputStream bitwiseInputStream = new BitwiseInputStream(bArr);
            int i5 = i3 * 7;
            int i6 = (i4 * 7) + i5;
            if (bitwiseInputStream.available() >= i6) {
                bitwiseInputStream.skip(i5);
                for (int i7 = 0; i7 < i4; i7++) {
                    int read = bitwiseInputStream.read(7);
                    if (read >= 32 && read <= UserData.ASCII_MAP_MAX_INDEX) {
                        stringBuffer.append(UserData.ASCII_MAP[read - 32]);
                    } else if (read == 10) {
                        stringBuffer.append(10);
                    } else if (read == 13) {
                        stringBuffer.append(13);
                    } else {
                        stringBuffer.append(' ');
                    }
                }
                return stringBuffer.toString();
            }
            throw new CodingException("insufficient data (wanted " + i6 + " bits, but only have " + bitwiseInputStream.available() + ")");
        } catch (BitwiseInputStream.AccessException e) {
            throw new CodingException("7bit ASCII decode failed: " + e);
        }
    }

    private static String decode7bitGsm(byte[] bArr, int i, int i2) throws CodingException {
        int i3 = i * 8;
        int i4 = (i3 + 6) / 7;
        String gsm7BitPackedToString = GsmAlphabet.gsm7BitPackedToString(bArr, i, i2 - i4, (i4 * 7) - i3, 0, 0);
        if (gsm7BitPackedToString != null) {
            return gsm7BitPackedToString;
        }
        throw new CodingException("7bit GSM decoding failed");
    }

    private static String decodeLatin(byte[] bArr, int i, int i2) throws CodingException {
        return decodeCharset(bArr, i, i2, 1, "ISO-8859-1");
    }

    private static String decodeShiftJis(byte[] bArr, int i, int i2) throws CodingException {
        return decodeCharset(bArr, i, i2, 1, "Shift_JIS");
    }

    private static String decodeGsmDcs(byte[] bArr, int i, int i2, int i3) throws CodingException {
        if ((i3 & 192) == 0) {
            int i4 = (i3 >> 2) & 3;
            if (i4 == 0) {
                return decode7bitGsm(bArr, i, i2);
            }
            if (i4 == 1) {
                return decodeUtf8(bArr, i, i2);
            }
            if (i4 == 2) {
                return decodeUtf16(bArr, i, i2);
            }
            throw new CodingException("unsupported user msgType encoding (" + i3 + ")");
        }
        throw new CodingException("unsupported coding group (" + i3 + ")");
    }

    private static void decodeUserDataPayload(Context context, UserData userData2, boolean z) throws CodingException {
        int i;
        if (z) {
            byte[] bArr = userData2.payload;
            int i2 = bArr[0] & 255;
            i = i2 + 1 + 0;
            byte[] bArr2 = new byte[i2];
            System.arraycopy(bArr, 1, bArr2, 0, i2);
            userData2.userDataHeader = SmsHeader.fromByteArray(bArr2);
        } else {
            i = 0;
        }
        int i3 = userData2.msgEncoding;
        if (i3 == 0) {
            boolean z2 = context.getResources().getBoolean(R.bool.config_sms_utf8_support);
            int i4 = userData2.numFields;
            byte[] bArr3 = new byte[i4];
            byte[] bArr4 = userData2.payload;
            if (i4 >= bArr4.length) {
                i4 = bArr4.length;
            }
            System.arraycopy(userData2.payload, 0, bArr3, 0, i4);
            userData2.payload = bArr3;
            if (!z2) {
                userData2.payloadStr = decodeLatin(bArr3, i, userData2.numFields);
            } else {
                userData2.payloadStr = decodeUtf8(bArr3, i, userData2.numFields);
            }
        } else if (i3 == 2 || i3 == 3) {
            userData2.payloadStr = decode7bitAscii(userData2.payload, i, userData2.numFields);
        } else if (i3 == 4) {
            userData2.payloadStr = decodeUtf16(userData2.payload, i, userData2.numFields);
        } else if (i3 != 5) {
            switch (i3) {
                case 8:
                    userData2.payloadStr = decodeLatin(userData2.payload, i, userData2.numFields);
                    return;
                case 9:
                    userData2.payloadStr = decode7bitGsm(userData2.payload, i, userData2.numFields);
                    return;
                case 10:
                    userData2.payloadStr = decodeGsmDcs(userData2.payload, i, userData2.numFields, userData2.msgType);
                    return;
                default:
                    throw new CodingException("unsupported user data encoding (" + userData2.msgEncoding + ")");
            }
        } else {
            userData2.payloadStr = decodeShiftJis(userData2.payload, i, userData2.numFields);
        }
    }

    private static boolean decodeLanguageIndicator(BearerData bearerData, BitwiseInputStream bitwiseInputStream) throws BitwiseInputStream.AccessException {
        boolean z;
        int read = bitwiseInputStream.read(8) * 8;
        if (read >= 8) {
            read -= 8;
            z = true;
            bearerData.language = bitwiseInputStream.read(8);
        } else {
            z = false;
        }
        if (!z || read > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("LANGUAGE_INDICATOR decode ");
            sb.append(z ? "succeeded" : "failed");
            sb.append(" (extra bits = ");
            sb.append(read);
            sb.append(")");
            Log.d("BearerData", sb.toString());
        }
        bitwiseInputStream.skip(read);
        return z;
    }

    private static boolean decodePriorityIndicator(BearerData bearerData, BitwiseInputStream bitwiseInputStream) throws BitwiseInputStream.AccessException {
        boolean z;
        int read = bitwiseInputStream.read(8) * 8;
        if (read >= 8) {
            read -= 8;
            z = true;
            bearerData.priority = bitwiseInputStream.read(2);
            bitwiseInputStream.skip(6);
        } else {
            z = false;
        }
        if (!z || read > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("PRIORITY_INDICATOR decode ");
            sb.append(z ? "succeeded" : "failed");
            sb.append(" (extra bits = ");
            sb.append(read);
            sb.append(")");
            Log.d("BearerData", sb.toString());
        }
        bitwiseInputStream.skip(read);
        return z;
    }

    private static void decodeCmasUserData(Context context, BearerData bearerData, int i) throws BitwiseInputStream.AccessException, CodingException {
        int i2;
        BearerData bearerData2 = bearerData;
        BitwiseInputStream bitwiseInputStream = new BitwiseInputStream(bearerData2.userData.payload);
        if (bitwiseInputStream.available() >= 8) {
            int read = bitwiseInputStream.read(8);
            if (read == 0) {
                int serviceCategoryToCmasMessageClass = serviceCategoryToCmasMessageClass(i);
                int i3 = -1;
                int i4 = -1;
                int i5 = -1;
                int i6 = -1;
                int i7 = -1;
                while (bitwiseInputStream.available() >= 16) {
                    int read2 = bitwiseInputStream.read(8);
                    int read3 = bitwiseInputStream.read(8);
                    if (read2 != 0) {
                        if (read2 != 1) {
                            Log.w("BearerData", "skipping unsupported CMAS record type " + read2);
                            bitwiseInputStream.skip(read3 * 8);
                        } else {
                            i3 = bitwiseInputStream.read(8);
                            i4 = bitwiseInputStream.read(8);
                            i5 = bitwiseInputStream.read(4);
                            i6 = bitwiseInputStream.read(4);
                            i7 = bitwiseInputStream.read(4);
                            bitwiseInputStream.skip((read3 * 8) - 28);
                        }
                        Context context2 = context;
                    } else {
                        UserData userData2 = new UserData();
                        int read4 = bitwiseInputStream.read(5);
                        userData2.msgEncoding = read4;
                        userData2.msgEncodingSet = true;
                        userData2.msgType = 0;
                        if (read4 != 0) {
                            if (!(read4 == 2 || read4 == 3)) {
                                if (read4 == 4) {
                                    i2 = (read3 - 1) / 2;
                                } else if (read4 != 8) {
                                    if (read4 != 9) {
                                        i2 = 0;
                                    }
                                }
                                userData2.numFields = i2;
                                userData2.payload = bitwiseInputStream.readByteArray((read3 * 8) - 5);
                                decodeUserDataPayload(context, userData2, false);
                                bearerData2.userData = userData2;
                            }
                            i2 = ((read3 * 8) - 5) / 7;
                            userData2.numFields = i2;
                            userData2.payload = bitwiseInputStream.readByteArray((read3 * 8) - 5);
                            decodeUserDataPayload(context, userData2, false);
                            bearerData2.userData = userData2;
                        }
                        i2 = read3 - 1;
                        userData2.numFields = i2;
                        userData2.payload = bitwiseInputStream.readByteArray((read3 * 8) - 5);
                        decodeUserDataPayload(context, userData2, false);
                        bearerData2.userData = userData2;
                    }
                }
                bearerData2.cmasWarningInfo = new SmsCbCmasInfo(serviceCategoryToCmasMessageClass, i3, i4, i5, i6, i7);
                return;
            }
            throw new CodingException("unsupported CMAE_protocol_version " + read);
        }
        throw new CodingException("emergency CB with no CMAE_protocol_version");
    }

    public static BearerData decode(Context context, byte[] bArr, int i) throws CodingException, BitwiseInputStream.AccessException {
        boolean z;
        BitwiseInputStream bitwiseInputStream = new BitwiseInputStream(bArr);
        BearerData bearerData = new BearerData();
        int i2 = 0;
        while (bitwiseInputStream.available() > 0) {
            int read = bitwiseInputStream.read(8);
            int i3 = 1 << read;
            if ((i2 & i3) == 0 || read < 0 || read > 23) {
                if (read == 0) {
                    z = decodeMessageId(bearerData, bitwiseInputStream);
                } else if (read == 1) {
                    z = decodeUserData(bearerData, bitwiseInputStream);
                } else if (read == 8) {
                    z = decodePriorityIndicator(bearerData, bitwiseInputStream);
                } else if (read != 13) {
                    z = decodeReserved(bitwiseInputStream, read);
                } else {
                    z = decodeLanguageIndicator(bearerData, bitwiseInputStream);
                }
                if (z && read >= 0 && read <= 23) {
                    i2 |= i3;
                }
            } else {
                throw new CodingException("illegal duplicate subparameter (" + read + ")");
            }
        }
        if ((i2 & 1) != 0) {
            if (bearerData.userData != null) {
                if (isCmasAlertCategory(i)) {
                    decodeCmasUserData(context, bearerData, i);
                } else {
                    decodeUserDataPayload(context, bearerData.userData, bearerData.hasUserDataHeader);
                }
            }
            return bearerData;
        }
        throw new CodingException("missing MESSAGE_IDENTIFIER subparam");
    }
}

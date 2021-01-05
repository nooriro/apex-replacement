package com.android.cellbroadcastservice;

import android.util.SparseIntArray;

public class UserData {
    public static final char[] ASCII_MAP = {' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~'};
    public static final int ASCII_MAP_MAX_INDEX = ((ASCII_MAP.length + 32) - 1);
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final char[] HEX_LOWER_CASE_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static final SparseIntArray charToAscii = new SparseIntArray();
    public int msgEncoding;
    public boolean msgEncodingSet = false;
    public int msgType;
    public int numFields;
    public int paddingBits;
    public byte[] payload;
    public String payloadStr;
    public SmsHeader userDataHeader;

    static {
        int i = 0;
        while (true) {
            char[] cArr = ASCII_MAP;
            if (i < cArr.length) {
                charToAscii.put(cArr[i], i + 32);
                i++;
            } else {
                charToAscii.put(10, 10);
                charToAscii.put(13, 13);
                return;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UserData ");
        StringBuilder sb2 = new StringBuilder();
        sb2.append("{ msgEncoding=");
        sb2.append(this.msgEncodingSet ? Integer.valueOf(this.msgEncoding) : "unset");
        sb.append(sb2.toString());
        sb.append(", msgType=" + this.msgType);
        sb.append(", paddingBits=" + this.paddingBits);
        sb.append(", numFields=" + this.numFields);
        sb.append(", userDataHeader=" + this.userDataHeader);
        sb.append(", payload='" + toHexString(this.payload) + "'");
        sb.append(", payloadStr='" + this.payloadStr + "'");
        sb.append(" }");
        return sb.toString();
    }

    private static String toHexString(byte[] bArr) {
        return toHexString(bArr, 0, bArr.length, true);
    }

    private static String toHexString(byte[] bArr, int i, int i2, boolean z) {
        char[] cArr = z ? HEX_DIGITS : HEX_LOWER_CASE_DIGITS;
        char[] cArr2 = new char[(i2 * 2)];
        int i3 = 0;
        for (int i4 = i; i4 < i + i2; i4++) {
            byte b = bArr[i4];
            int i5 = i3 + 1;
            cArr2[i3] = cArr[(b >>> 4) & 15];
            i3 = i5 + 1;
            cArr2[i5] = cArr[b & 15];
        }
        return new String(cArr2);
    }
}

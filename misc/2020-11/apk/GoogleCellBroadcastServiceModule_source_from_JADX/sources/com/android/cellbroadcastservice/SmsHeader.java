package com.android.cellbroadcastservice;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SmsHeader {
    public ConcatRef concatRef;
    public int languageShiftTable;
    public int languageTable;
    public ArrayList<MiscElt> miscEltList = new ArrayList<>();
    public PortAddrs portAddrs;
    public ArrayList<SpecialSmsMsg> specialSmsMsgList = new ArrayList<>();

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || SmsHeader.class != obj.getClass()) {
            return false;
        }
        SmsHeader smsHeader = (SmsHeader) obj;
        if (this.languageTable != smsHeader.languageTable || this.languageShiftTable != smsHeader.languageShiftTable || !Objects.equals(this.portAddrs, smsHeader.portAddrs) || !Objects.equals(this.concatRef, smsHeader.concatRef) || !Objects.equals(this.specialSmsMsgList, smsHeader.specialSmsMsgList) || !Objects.equals(this.miscEltList, smsHeader.miscEltList)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.portAddrs, this.concatRef, this.specialSmsMsgList, this.miscEltList, Integer.valueOf(this.languageTable), Integer.valueOf(this.languageShiftTable)});
    }

    public static class PortAddrs {
        public boolean areEightBits;
        public int destPort;
        public int origPort;

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || PortAddrs.class != obj.getClass()) {
                return false;
            }
            PortAddrs portAddrs = (PortAddrs) obj;
            if (this.destPort == portAddrs.destPort && this.origPort == portAddrs.origPort && this.areEightBits == portAddrs.areEightBits) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Integer.valueOf(this.destPort), Integer.valueOf(this.origPort), Boolean.valueOf(this.areEightBits)});
        }
    }

    public static class ConcatRef {
        public boolean isEightBits;
        public int msgCount;
        public int refNumber;
        public int seqNumber;

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || ConcatRef.class != obj.getClass()) {
                return false;
            }
            ConcatRef concatRef = (ConcatRef) obj;
            if (this.refNumber == concatRef.refNumber && this.seqNumber == concatRef.seqNumber && this.msgCount == concatRef.msgCount && this.isEightBits == concatRef.isEightBits) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Integer.valueOf(this.refNumber), Integer.valueOf(this.seqNumber), Integer.valueOf(this.msgCount), Boolean.valueOf(this.isEightBits)});
        }
    }

    public static class SpecialSmsMsg {
        public int msgCount;
        public int msgIndType;

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || SpecialSmsMsg.class != obj.getClass()) {
                return false;
            }
            SpecialSmsMsg specialSmsMsg = (SpecialSmsMsg) obj;
            if (this.msgIndType == specialSmsMsg.msgIndType && this.msgCount == specialSmsMsg.msgCount) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Integer.valueOf(this.msgIndType), Integer.valueOf(this.msgCount)});
        }
    }

    public static class MiscElt {
        public byte[] data;

        /* renamed from: id */
        public int f0id;

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || MiscElt.class != obj.getClass()) {
                return false;
            }
            MiscElt miscElt = (MiscElt) obj;
            if (this.f0id != miscElt.f0id || !Arrays.equals(this.data, miscElt.data)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (Objects.hash(new Object[]{Integer.valueOf(this.f0id)}) * 31) + Arrays.hashCode(this.data);
        }
    }

    public static SmsHeader fromByteArray(byte[] bArr) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        SmsHeader smsHeader = new SmsHeader();
        while (byteArrayInputStream.available() > 0) {
            int read = byteArrayInputStream.read();
            int read2 = byteArrayInputStream.read();
            if (read == 0) {
                ConcatRef concatRef2 = new ConcatRef();
                concatRef2.refNumber = byteArrayInputStream.read();
                concatRef2.msgCount = byteArrayInputStream.read();
                int read3 = byteArrayInputStream.read();
                concatRef2.seqNumber = read3;
                concatRef2.isEightBits = true;
                int i = concatRef2.msgCount;
                if (!(i == 0 || read3 == 0 || read3 > i)) {
                    smsHeader.concatRef = concatRef2;
                }
            } else if (read == 1) {
                SpecialSmsMsg specialSmsMsg = new SpecialSmsMsg();
                specialSmsMsg.msgIndType = byteArrayInputStream.read();
                specialSmsMsg.msgCount = byteArrayInputStream.read();
                smsHeader.specialSmsMsgList.add(specialSmsMsg);
            } else if (read == 4) {
                PortAddrs portAddrs2 = new PortAddrs();
                portAddrs2.destPort = byteArrayInputStream.read();
                portAddrs2.origPort = byteArrayInputStream.read();
                portAddrs2.areEightBits = true;
                smsHeader.portAddrs = portAddrs2;
            } else if (read == 5) {
                PortAddrs portAddrs3 = new PortAddrs();
                portAddrs3.destPort = (byteArrayInputStream.read() << 8) | byteArrayInputStream.read();
                portAddrs3.origPort = (byteArrayInputStream.read() << 8) | byteArrayInputStream.read();
                portAddrs3.areEightBits = false;
                smsHeader.portAddrs = portAddrs3;
            } else if (read == 8) {
                ConcatRef concatRef3 = new ConcatRef();
                concatRef3.refNumber = (byteArrayInputStream.read() << 8) | byteArrayInputStream.read();
                concatRef3.msgCount = byteArrayInputStream.read();
                int read4 = byteArrayInputStream.read();
                concatRef3.seqNumber = read4;
                concatRef3.isEightBits = false;
                int i2 = concatRef3.msgCount;
                if (!(i2 == 0 || read4 == 0 || read4 > i2)) {
                    smsHeader.concatRef = concatRef3;
                }
            } else if (read == 36) {
                smsHeader.languageShiftTable = byteArrayInputStream.read();
            } else if (read != 37) {
                MiscElt miscElt = new MiscElt();
                miscElt.f0id = read;
                byte[] bArr2 = new byte[read2];
                miscElt.data = bArr2;
                byteArrayInputStream.read(bArr2, 0, read2);
                smsHeader.miscEltList.add(miscElt);
            } else {
                smsHeader.languageTable = byteArrayInputStream.read();
            }
        }
        return smsHeader;
    }
}

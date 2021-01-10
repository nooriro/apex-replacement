package com.android.cellbroadcastservice;

import android.os.SystemClock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;

public final class LocalLog {
    private final Deque<String> mLog;
    private final int mMaxLines;
    private final boolean mUseLocalTimestamps;

    public LocalLog(int i) {
        this(i, true);
    }

    public LocalLog(int i, boolean z) {
        this.mMaxLines = Math.max(0, i);
        this.mLog = new ArrayDeque(this.mMaxLines);
        this.mUseLocalTimestamps = z;
    }

    public void log(String str) {
        String str2;
        if (this.mMaxLines > 0) {
            if (this.mUseLocalTimestamps) {
                str2 = String.format("%s - %s", new Object[]{LocalDateTime.now(), str});
            } else {
                str2 = String.format("%s / %s - %s", new Object[]{Long.valueOf(SystemClock.elapsedRealtime()), Instant.now(), str});
            }
            append(str2);
        }
    }

    private synchronized void append(String str) {
        while (this.mLog.size() >= this.mMaxLines) {
            this.mLog.remove();
        }
        this.mLog.add(str);
    }
}

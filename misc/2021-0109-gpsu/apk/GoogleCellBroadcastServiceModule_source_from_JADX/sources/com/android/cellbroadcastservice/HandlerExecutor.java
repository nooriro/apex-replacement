package com.android.cellbroadcastservice;

import android.os.Handler;
import com.android.internal.util.Preconditions;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

public class HandlerExecutor implements Executor {
    private final Handler mHandler;

    public HandlerExecutor(Handler handler) {
        Preconditions.checkNotNull(handler);
        this.mHandler = handler;
    }

    public void execute(Runnable runnable) {
        if (!this.mHandler.post(runnable)) {
            throw new RejectedExecutionException(this.mHandler + " is shutting down");
        }
    }
}

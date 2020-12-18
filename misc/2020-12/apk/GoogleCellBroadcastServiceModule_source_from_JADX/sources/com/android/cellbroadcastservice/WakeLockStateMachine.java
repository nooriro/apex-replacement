package com.android.cellbroadcastservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.Log;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class WakeLockStateMachine extends StateMachine {
    protected static final boolean DBG;
    protected Context mContext;
    private final DefaultState mDefaultState = new DefaultState();
    /* access modifiers changed from: private */
    public final IdleState mIdleState = new IdleState();
    protected final BroadcastReceiver mOrderedBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (WakeLockStateMachine.this.mReceiverCount.decrementAndGet() == 0) {
                WakeLockStateMachine.this.sendMessage(2);
            }
        }
    };
    protected AtomicInteger mReceiverCount = new AtomicInteger(0);
    /* access modifiers changed from: private */
    public final WaitingState mWaitingState = new WaitingState();
    /* access modifiers changed from: private */
    public final PowerManager.WakeLock mWakeLock;

    /* access modifiers changed from: protected */
    public abstract boolean handleSmsMessage(Message message);

    static {
        boolean z = false;
        if (SystemProperties.getInt("ro.debuggable", 0) == 1) {
            z = true;
        }
        DBG = z;
    }

    protected WakeLockStateMachine(String str, Context context, Looper looper) {
        super(str, looper);
        this.mContext = context;
        PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, str);
        this.mWakeLock = newWakeLock;
        newWakeLock.acquire();
        addState(this.mDefaultState);
        addState(this.mIdleState, this.mDefaultState);
        addState(this.mWaitingState, this.mDefaultState);
        setInitialState(this.mIdleState);
    }

    /* access modifiers changed from: private */
    public void releaseWakeLock() {
        if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
        if (this.mWakeLock.isHeld()) {
            loge("Wait lock is held after release.");
        }
    }

    /* access modifiers changed from: protected */
    public void onQuitting() {
        while (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
    }

    public final void onCdmaCellBroadcastSms(Object obj) {
        sendMessage(1, obj);
    }

    class DefaultState extends State {
        DefaultState() {
        }

        public boolean processMessage(Message message) {
            int i = message.what;
            String str = "processMessage: unhandled message type " + message.what;
            if (!WakeLockStateMachine.DBG) {
                WakeLockStateMachine.this.loge(str);
                return true;
            }
            throw new RuntimeException(str);
        }
    }

    class IdleState extends State {
        IdleState() {
        }

        public void enter() {
            WakeLockStateMachine.this.sendMessageDelayed(3, 3000);
        }

        public void exit() {
            WakeLockStateMachine.this.mWakeLock.acquire();
            if (WakeLockStateMachine.DBG) {
                WakeLockStateMachine.this.log("Idle: acquired wakelock, leaving Idle state");
            }
        }

        public boolean processMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                WakeLockStateMachine.this.log("Idle: new cell broadcast message");
                if (WakeLockStateMachine.this.handleSmsMessage(message)) {
                    WakeLockStateMachine wakeLockStateMachine = WakeLockStateMachine.this;
                    wakeLockStateMachine.transitionTo(wakeLockStateMachine.mWaitingState);
                }
                return true;
            } else if (i == 3) {
                WakeLockStateMachine.this.log("Idle: release wakelock");
                WakeLockStateMachine.this.releaseWakeLock();
                return true;
            } else if (i != 4) {
                return false;
            } else {
                WakeLockStateMachine.this.log("Idle: broadcast not required");
                return true;
            }
        }
    }

    class WaitingState extends State {
        WaitingState() {
        }

        public boolean processMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                WakeLockStateMachine.this.log("Waiting: deferring message until return to idle");
                WakeLockStateMachine.this.deferMessage(message);
                return true;
            } else if (i == 2) {
                WakeLockStateMachine.this.log("Waiting: broadcast complete, returning to idle");
                WakeLockStateMachine wakeLockStateMachine = WakeLockStateMachine.this;
                wakeLockStateMachine.transitionTo(wakeLockStateMachine.mIdleState);
                return true;
            } else if (i == 3) {
                WakeLockStateMachine.this.log("Waiting: release wakelock");
                WakeLockStateMachine.this.releaseWakeLock();
                return true;
            } else if (i != 4) {
                return false;
            } else {
                WakeLockStateMachine.this.log("Waiting: broadcast not required");
                if (WakeLockStateMachine.this.mReceiverCount.get() == 0) {
                    WakeLockStateMachine wakeLockStateMachine2 = WakeLockStateMachine.this;
                    wakeLockStateMachine2.transitionTo(wakeLockStateMachine2.mIdleState);
                }
                return true;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void log(String str) {
        Log.d(getName(), str);
    }

    /* access modifiers changed from: protected */
    public void loge(String str) {
        Log.e(getName(), str);
    }
}

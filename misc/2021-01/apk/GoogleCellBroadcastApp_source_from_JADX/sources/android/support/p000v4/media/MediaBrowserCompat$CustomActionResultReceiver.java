package android.support.p000v4.media;

import android.os.Bundle;
import android.support.p000v4.media.session.MediaSessionCompat;
import android.support.p000v4.p001os.ResultReceiver;
import android.util.Log;

/* renamed from: android.support.v4.media.MediaBrowserCompat$CustomActionResultReceiver */
class MediaBrowserCompat$CustomActionResultReceiver extends ResultReceiver {
    private final String mAction;
    private final MediaBrowserCompat$CustomActionCallback mCallback;
    private final Bundle mExtras;

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        if (this.mCallback != null) {
            MediaSessionCompat.ensureClassLoader(bundle);
            if (i == -1) {
                this.mCallback.onError(this.mAction, this.mExtras, bundle);
            } else if (i == 0) {
                this.mCallback.onResult(this.mAction, this.mExtras, bundle);
            } else if (i != 1) {
                Log.w("MediaBrowserCompat", "Unknown result code: " + i + " (extras=" + this.mExtras + ", resultData=" + bundle + ")");
            } else {
                this.mCallback.onProgressUpdate(this.mAction, this.mExtras, bundle);
            }
        }
    }
}

package android.support.p000v4.media.session;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.p000v4.media.MediaMetadataCompat;
import android.support.p000v4.media.session.IMediaSession;
import android.support.p000v4.media.session.MediaControllerCompat$Callback;
import android.support.p000v4.media.session.MediaSessionCompat;
import android.util.Log;
import androidx.core.app.BundleCompat;
import androidx.versionedparcelable.ParcelUtils;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/* renamed from: android.support.v4.media.session.MediaControllerCompat$MediaControllerImplApi21 */
class MediaControllerCompat$MediaControllerImplApi21 {
    private HashMap<MediaControllerCompat$Callback, ExtraCallback> mCallbackMap;
    final Object mLock;
    private final List<MediaControllerCompat$Callback> mPendingCallbacks;
    final MediaSessionCompat.Token mSessionToken;

    /* access modifiers changed from: package-private */
    public void processPendingCallbacksLocked() {
        if (this.mSessionToken.getExtraBinder() != null) {
            for (MediaControllerCompat$Callback next : this.mPendingCallbacks) {
                ExtraCallback extraCallback = new ExtraCallback(next);
                this.mCallbackMap.put(next, extraCallback);
                next.mIControllerCallback = extraCallback;
                try {
                    this.mSessionToken.getExtraBinder().registerCallbackListener(extraCallback);
                    next.postToHandler(13, (Object) null, (Bundle) null);
                } catch (RemoteException e) {
                    Log.e("MediaControllerCompat", "Dead object in registerCallback.", e);
                }
            }
            this.mPendingCallbacks.clear();
        }
    }

    /* renamed from: android.support.v4.media.session.MediaControllerCompat$MediaControllerImplApi21$ExtraBinderRequestResultReceiver */
    private static class ExtraBinderRequestResultReceiver extends ResultReceiver {
        private WeakReference<MediaControllerCompat$MediaControllerImplApi21> mMediaControllerImpl;

        /* access modifiers changed from: protected */
        public void onReceiveResult(int i, Bundle bundle) {
            MediaControllerCompat$MediaControllerImplApi21 mediaControllerCompat$MediaControllerImplApi21 = (MediaControllerCompat$MediaControllerImplApi21) this.mMediaControllerImpl.get();
            if (mediaControllerCompat$MediaControllerImplApi21 != null && bundle != null) {
                synchronized (mediaControllerCompat$MediaControllerImplApi21.mLock) {
                    mediaControllerCompat$MediaControllerImplApi21.mSessionToken.setExtraBinder(IMediaSession.Stub.asInterface(BundleCompat.getBinder(bundle, "android.support.v4.media.session.EXTRA_BINDER")));
                    mediaControllerCompat$MediaControllerImplApi21.mSessionToken.setSession2Token(ParcelUtils.getVersionedParcelable(bundle, "android.support.v4.media.session.SESSION_TOKEN2"));
                    mediaControllerCompat$MediaControllerImplApi21.processPendingCallbacksLocked();
                }
            }
        }
    }

    /* renamed from: android.support.v4.media.session.MediaControllerCompat$MediaControllerImplApi21$ExtraCallback */
    private static class ExtraCallback extends MediaControllerCompat$Callback.StubCompat {
        ExtraCallback(MediaControllerCompat$Callback mediaControllerCompat$Callback) {
            super(mediaControllerCompat$Callback);
        }

        public void onSessionDestroyed() throws RemoteException {
            throw new AssertionError();
        }

        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) throws RemoteException {
            throw new AssertionError();
        }

        public void onQueueChanged(List<MediaSessionCompat.QueueItem> list) throws RemoteException {
            throw new AssertionError();
        }

        public void onQueueTitleChanged(CharSequence charSequence) throws RemoteException {
            throw new AssertionError();
        }

        public void onExtrasChanged(Bundle bundle) throws RemoteException {
            throw new AssertionError();
        }

        public void onVolumeInfoChanged(ParcelableVolumeInfo parcelableVolumeInfo) throws RemoteException {
            throw new AssertionError();
        }
    }
}

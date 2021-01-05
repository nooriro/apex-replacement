package android.support.p000v4.media.session;

import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.p000v4.media.MediaMetadataCompat;
import android.support.p000v4.media.session.IMediaControllerCallback;
import android.support.p000v4.media.session.MediaSessionCompat;
import androidx.media.AudioAttributesCompat;
import java.lang.ref.WeakReference;
import java.util.List;

/* renamed from: android.support.v4.media.session.MediaControllerCompat$Callback */
public abstract class MediaControllerCompat$Callback implements IBinder.DeathRecipient {
    MessageHandler mHandler;
    IMediaControllerCallback mIControllerCallback;

    /* renamed from: android.support.v4.media.session.MediaControllerCompat$Callback$MessageHandler */
    private class MessageHandler extends Handler {
    }

    public void onAudioInfoChanged(MediaControllerCompat$PlaybackInfo mediaControllerCompat$PlaybackInfo) {
    }

    public void onExtrasChanged(Bundle bundle) {
    }

    public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) {
    }

    public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) {
    }

    public void onQueueChanged(List<MediaSessionCompat.QueueItem> list) {
    }

    public void onQueueTitleChanged(CharSequence charSequence) {
    }

    public void onSessionDestroyed() {
    }

    public void onSessionEvent(String str, Bundle bundle) {
    }

    public MediaControllerCompat$Callback() {
        if (Build.VERSION.SDK_INT >= 21) {
            new MediaControllerCallbackApi21(this);
        } else {
            this.mIControllerCallback = new StubCompat(this);
        }
    }

    public void binderDied() {
        postToHandler(8, (Object) null, (Bundle) null);
    }

    /* access modifiers changed from: package-private */
    public void postToHandler(int i, Object obj, Bundle bundle) {
        MessageHandler messageHandler = this.mHandler;
        if (messageHandler != null) {
            Message obtainMessage = messageHandler.obtainMessage(i, obj);
            obtainMessage.setData(bundle);
            obtainMessage.sendToTarget();
        }
    }

    /* renamed from: android.support.v4.media.session.MediaControllerCompat$Callback$MediaControllerCallbackApi21 */
    private static class MediaControllerCallbackApi21 extends MediaController.Callback {
        private final WeakReference<MediaControllerCompat$Callback> mCallback;

        MediaControllerCallbackApi21(MediaControllerCompat$Callback mediaControllerCompat$Callback) {
            this.mCallback = new WeakReference<>(mediaControllerCompat$Callback);
        }

        public void onSessionDestroyed() {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onSessionDestroyed();
            }
        }

        public void onSessionEvent(String str, Bundle bundle) {
            MediaSessionCompat.ensureClassLoader(bundle);
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback == null) {
                return;
            }
            if (mediaControllerCompat$Callback.mIControllerCallback == null || Build.VERSION.SDK_INT >= 23) {
                mediaControllerCompat$Callback.onSessionEvent(str, bundle);
            }
        }

        public void onPlaybackStateChanged(PlaybackState playbackState) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null && mediaControllerCompat$Callback.mIControllerCallback == null) {
                mediaControllerCompat$Callback.onPlaybackStateChanged(PlaybackStateCompat.fromPlaybackState(playbackState));
            }
        }

        public void onMetadataChanged(MediaMetadata mediaMetadata) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onMetadataChanged(MediaMetadataCompat.fromMediaMetadata(mediaMetadata));
            }
        }

        public void onQueueChanged(List<MediaSession.QueueItem> list) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onQueueChanged(MediaSessionCompat.QueueItem.fromQueueItemList(list));
            }
        }

        public void onQueueTitleChanged(CharSequence charSequence) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onQueueTitleChanged(charSequence);
            }
        }

        public void onExtrasChanged(Bundle bundle) {
            MediaSessionCompat.ensureClassLoader(bundle);
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onExtrasChanged(bundle);
            }
        }

        public void onAudioInfoChanged(MediaController.PlaybackInfo playbackInfo) {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.onAudioInfoChanged(new MediaControllerCompat$PlaybackInfo(playbackInfo.getPlaybackType(), AudioAttributesCompat.wrap(playbackInfo.getAudioAttributes()), playbackInfo.getVolumeControl(), playbackInfo.getMaxVolume(), playbackInfo.getCurrentVolume()));
            }
        }
    }

    /* renamed from: android.support.v4.media.session.MediaControllerCompat$Callback$StubCompat */
    private static class StubCompat extends IMediaControllerCallback.Stub {
        private final WeakReference<MediaControllerCompat$Callback> mCallback;

        public void onShuffleModeChangedRemoved(boolean z) throws RemoteException {
        }

        StubCompat(MediaControllerCompat$Callback mediaControllerCompat$Callback) {
            this.mCallback = new WeakReference<>(mediaControllerCompat$Callback);
        }

        public void onEvent(String str, Bundle bundle) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(1, str, bundle);
            }
        }

        public void onSessionDestroyed() throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(8, (Object) null, (Bundle) null);
            }
        }

        public void onPlaybackStateChanged(PlaybackStateCompat playbackStateCompat) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(2, playbackStateCompat, (Bundle) null);
            }
        }

        public void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(3, mediaMetadataCompat, (Bundle) null);
            }
        }

        public void onQueueChanged(List<MediaSessionCompat.QueueItem> list) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(5, list, (Bundle) null);
            }
        }

        public void onQueueTitleChanged(CharSequence charSequence) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(6, charSequence, (Bundle) null);
            }
        }

        public void onCaptioningEnabledChanged(boolean z) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(11, Boolean.valueOf(z), (Bundle) null);
            }
        }

        public void onRepeatModeChanged(int i) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(9, Integer.valueOf(i), (Bundle) null);
            }
        }

        public void onShuffleModeChanged(int i) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(12, Integer.valueOf(i), (Bundle) null);
            }
        }

        public void onExtrasChanged(Bundle bundle) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(7, bundle, (Bundle) null);
            }
        }

        public void onVolumeInfoChanged(ParcelableVolumeInfo parcelableVolumeInfo) throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(4, parcelableVolumeInfo != null ? new MediaControllerCompat$PlaybackInfo(parcelableVolumeInfo.volumeType, parcelableVolumeInfo.audioStream, parcelableVolumeInfo.controlType, parcelableVolumeInfo.maxVolume, parcelableVolumeInfo.currentVolume) : null, (Bundle) null);
            }
        }

        public void onSessionReady() throws RemoteException {
            MediaControllerCompat$Callback mediaControllerCompat$Callback = (MediaControllerCompat$Callback) this.mCallback.get();
            if (mediaControllerCompat$Callback != null) {
                mediaControllerCompat$Callback.postToHandler(13, (Object) null, (Bundle) null);
            }
        }
    }
}

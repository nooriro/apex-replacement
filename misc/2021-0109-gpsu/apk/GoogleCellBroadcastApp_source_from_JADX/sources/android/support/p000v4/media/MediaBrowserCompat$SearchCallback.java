package android.support.p000v4.media;

import android.os.Bundle;
import java.util.List;

/* renamed from: android.support.v4.media.MediaBrowserCompat$SearchCallback */
public abstract class MediaBrowserCompat$SearchCallback {
    public abstract void onError(String str, Bundle bundle);

    public abstract void onSearchResult(String str, Bundle bundle, List<MediaBrowserCompat$MediaItem> list);
}

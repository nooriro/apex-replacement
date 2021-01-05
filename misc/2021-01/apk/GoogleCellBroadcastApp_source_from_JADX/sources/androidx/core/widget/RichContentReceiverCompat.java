package androidx.core.widget;

import android.content.ClipData;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public abstract class RichContentReceiverCompat<T extends View> {
    public abstract boolean onReceive(T t, ClipData clipData, int i, int i2);

    public final void populateEditorInfoContentMimeTypes(InputConnection inputConnection, EditorInfo editorInfo) {
        throw null;
    }
}

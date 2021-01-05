package androidx.preference;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PreferenceViewHolder extends RecyclerView.ViewHolder {
    private Drawable mBackground;
    private final SparseArray<View> mCachedViews = new SparseArray<>(4);
    private boolean mDividerAllowedAbove;
    private boolean mDividerAllowedBelow;
    private ColorStateList mTitleTextColors;

    PreferenceViewHolder(View view) {
        super(view);
        TextView textView = (TextView) view.findViewById(16908310);
        this.mCachedViews.put(16908310, textView);
        this.mCachedViews.put(16908304, view.findViewById(16908304));
        this.mCachedViews.put(16908294, view.findViewById(16908294));
        SparseArray<View> sparseArray = this.mCachedViews;
        int i = R$id.icon_frame;
        sparseArray.put(i, view.findViewById(i));
        this.mCachedViews.put(16908350, view.findViewById(16908350));
        this.mBackground = view.getBackground();
        if (textView != null) {
            this.mTitleTextColors = textView.getTextColors();
        }
    }

    public View findViewById(int i) {
        View view = this.mCachedViews.get(i);
        if (view != null) {
            return view;
        }
        View findViewById = this.itemView.findViewById(i);
        if (findViewById != null) {
            this.mCachedViews.put(i, findViewById);
        }
        return findViewById;
    }

    public boolean isDividerAllowedAbove() {
        return this.mDividerAllowedAbove;
    }

    public void setDividerAllowedAbove(boolean z) {
        this.mDividerAllowedAbove = z;
    }

    public boolean isDividerAllowedBelow() {
        return this.mDividerAllowedBelow;
    }

    public void setDividerAllowedBelow(boolean z) {
        this.mDividerAllowedBelow = z;
    }

    /* access modifiers changed from: package-private */
    public void resetState() {
        Drawable background = this.itemView.getBackground();
        Drawable drawable = this.mBackground;
        if (background != drawable) {
            ViewCompat.setBackground(this.itemView, drawable);
        }
        TextView textView = (TextView) findViewById(16908310);
        if (textView != null && this.mTitleTextColors != null && !textView.getTextColors().equals(this.mTitleTextColors)) {
            textView.setTextColor(this.mTitleTextColors);
        }
    }
}

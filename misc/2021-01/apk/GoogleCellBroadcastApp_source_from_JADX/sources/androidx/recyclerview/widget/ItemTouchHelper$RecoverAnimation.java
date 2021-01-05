package androidx.recyclerview.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import androidx.recyclerview.widget.RecyclerView;

class ItemTouchHelper$RecoverAnimation implements Animator.AnimatorListener {
    boolean mEnded;
    final ValueAnimator mValueAnimator;
    final RecyclerView.ViewHolder mViewHolder;

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
    }

    public void setFraction(float f) {
    }

    public void onAnimationEnd(Animator animator) {
        if (!this.mEnded) {
            this.mViewHolder.setIsRecyclable(true);
        }
        this.mEnded = true;
    }

    public void onAnimationCancel(Animator animator) {
        setFraction(1.0f);
    }
}

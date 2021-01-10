package androidx.core.view;

import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import androidx.core.graphics.Insets;
import androidx.core.util.ObjectsCompat;
import androidx.core.util.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;

public class WindowInsetsCompat {
    public static final WindowInsetsCompat CONSUMED;
    private final Impl mImpl;

    static {
        if (Build.VERSION.SDK_INT >= 30) {
            CONSUMED = Impl30.CONSUMED;
        } else {
            CONSUMED = Impl.CONSUMED;
        }
    }

    private WindowInsetsCompat(WindowInsets windowInsets) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 30) {
            this.mImpl = new Impl30(this, windowInsets);
        } else if (i >= 29) {
            this.mImpl = new Impl29(this, windowInsets);
        } else if (i >= 28) {
            this.mImpl = new Impl28(this, windowInsets);
        } else if (i >= 21) {
            this.mImpl = new Impl21(this, windowInsets);
        } else if (i >= 20) {
            this.mImpl = new Impl20(this, windowInsets);
        } else {
            this.mImpl = new Impl(this);
        }
    }

    public WindowInsetsCompat(WindowInsetsCompat windowInsetsCompat) {
        int i = Build.VERSION.SDK_INT;
        if (windowInsetsCompat != null) {
            Impl impl = windowInsetsCompat.mImpl;
            if (i >= 30 && (impl instanceof Impl30)) {
                this.mImpl = new Impl30(this, (Impl30) impl);
            } else if (i >= 29 && (impl instanceof Impl29)) {
                this.mImpl = new Impl29(this, (Impl29) impl);
            } else if (i >= 28 && (impl instanceof Impl28)) {
                this.mImpl = new Impl28(this, (Impl28) impl);
            } else if (i >= 21 && (impl instanceof Impl21)) {
                this.mImpl = new Impl21(this, (Impl21) impl);
            } else if (i < 20 || !(impl instanceof Impl20)) {
                this.mImpl = new Impl(this);
            } else {
                this.mImpl = new Impl20(this, (Impl20) impl);
            }
            impl.copyWindowDataInto(this);
            return;
        }
        this.mImpl = new Impl(this);
    }

    public static WindowInsetsCompat toWindowInsetsCompat(WindowInsets windowInsets) {
        return toWindowInsetsCompat(windowInsets, (View) null);
    }

    public static WindowInsetsCompat toWindowInsetsCompat(WindowInsets windowInsets, View view) {
        Preconditions.checkNotNull(windowInsets);
        WindowInsetsCompat windowInsetsCompat = new WindowInsetsCompat(windowInsets);
        if (view != null && view.isAttachedToWindow()) {
            windowInsetsCompat.setRootWindowInsets(ViewCompat.getRootWindowInsets(view));
            windowInsetsCompat.copyRootViewBounds(view.getRootView());
        }
        return windowInsetsCompat;
    }

    @Deprecated
    public int getSystemWindowInsetLeft() {
        return this.mImpl.getSystemWindowInsets().left;
    }

    @Deprecated
    public int getSystemWindowInsetTop() {
        return this.mImpl.getSystemWindowInsets().top;
    }

    @Deprecated
    public int getSystemWindowInsetRight() {
        return this.mImpl.getSystemWindowInsets().right;
    }

    @Deprecated
    public int getSystemWindowInsetBottom() {
        return this.mImpl.getSystemWindowInsets().bottom;
    }

    public boolean isConsumed() {
        return this.mImpl.isConsumed();
    }

    @Deprecated
    public WindowInsetsCompat consumeSystemWindowInsets() {
        return this.mImpl.consumeSystemWindowInsets();
    }

    @Deprecated
    public WindowInsetsCompat replaceSystemWindowInsets(int i, int i2, int i3, int i4) {
        Builder builder = new Builder(this);
        builder.setSystemWindowInsets(Insets.m2of(i, i2, i3, i4));
        return builder.build();
    }

    @Deprecated
    public WindowInsetsCompat consumeStableInsets() {
        return this.mImpl.consumeStableInsets();
    }

    @Deprecated
    public WindowInsetsCompat consumeDisplayCutout() {
        return this.mImpl.consumeDisplayCutout();
    }

    @Deprecated
    public Insets getSystemWindowInsets() {
        return this.mImpl.getSystemWindowInsets();
    }

    @Deprecated
    public Insets getSystemGestureInsets() {
        return this.mImpl.getSystemGestureInsets();
    }

    public WindowInsetsCompat inset(int i, int i2, int i3, int i4) {
        return this.mImpl.inset(i, i2, i3, i4);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WindowInsetsCompat)) {
            return false;
        }
        return ObjectsCompat.equals(this.mImpl, ((WindowInsetsCompat) obj).mImpl);
    }

    public int hashCode() {
        Impl impl = this.mImpl;
        if (impl == null) {
            return 0;
        }
        return impl.hashCode();
    }

    public WindowInsets toWindowInsets() {
        Impl impl = this.mImpl;
        if (impl instanceof Impl20) {
            return ((Impl20) impl).mPlatformInsets;
        }
        return null;
    }

    private static class Impl {
        static final WindowInsetsCompat CONSUMED = new Builder().build().consumeDisplayCutout().consumeStableInsets().consumeSystemWindowInsets();
        final WindowInsetsCompat mHost;

        /* access modifiers changed from: package-private */
        public void copyWindowDataInto(WindowInsetsCompat windowInsetsCompat) {
        }

        /* access modifiers changed from: package-private */
        public DisplayCutoutCompat getDisplayCutout() {
            return null;
        }

        /* access modifiers changed from: package-private */
        public boolean isConsumed() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean isRound() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public void setRootViewData(Rect rect, int i) {
        }

        /* access modifiers changed from: package-private */
        public void setRootWindowInsets(WindowInsetsCompat windowInsetsCompat) {
        }

        Impl(WindowInsetsCompat windowInsetsCompat) {
            this.mHost = windowInsetsCompat;
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeSystemWindowInsets() {
            return this.mHost;
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeStableInsets() {
            return this.mHost;
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeDisplayCutout() {
            return this.mHost;
        }

        /* access modifiers changed from: package-private */
        public Insets getSystemWindowInsets() {
            return Insets.NONE;
        }

        /* access modifiers changed from: package-private */
        public Insets getStableInsets() {
            return Insets.NONE;
        }

        /* access modifiers changed from: package-private */
        public Insets getSystemGestureInsets() {
            return getSystemWindowInsets();
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat inset(int i, int i2, int i3, int i4) {
            return CONSUMED;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Impl)) {
                return false;
            }
            Impl impl = (Impl) obj;
            if (isRound() != impl.isRound() || isConsumed() != impl.isConsumed() || !ObjectsCompat.equals(getSystemWindowInsets(), impl.getSystemWindowInsets()) || !ObjectsCompat.equals(getStableInsets(), impl.getStableInsets()) || !ObjectsCompat.equals(getDisplayCutout(), impl.getDisplayCutout())) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ObjectsCompat.hash(Boolean.valueOf(isRound()), Boolean.valueOf(isConsumed()), getSystemWindowInsets(), getStableInsets(), getDisplayCutout());
        }
    }

    private static class Impl20 extends Impl {
        final WindowInsets mPlatformInsets;
        private int mRootViewHeight;
        private Rect mRootViewVisibleFrame;
        private WindowInsetsCompat mRootWindowInsets;
        private Insets mSystemWindowInsets;

        Impl20(WindowInsetsCompat windowInsetsCompat, WindowInsets windowInsets) {
            super(windowInsetsCompat);
            this.mSystemWindowInsets = null;
            this.mPlatformInsets = windowInsets;
        }

        Impl20(WindowInsetsCompat windowInsetsCompat, Impl20 impl20) {
            this(windowInsetsCompat, new WindowInsets(impl20.mPlatformInsets));
        }

        /* access modifiers changed from: package-private */
        public boolean isRound() {
            return this.mPlatformInsets.isRound();
        }

        /* access modifiers changed from: package-private */
        public final Insets getSystemWindowInsets() {
            if (this.mSystemWindowInsets == null) {
                this.mSystemWindowInsets = Insets.m2of(this.mPlatformInsets.getSystemWindowInsetLeft(), this.mPlatformInsets.getSystemWindowInsetTop(), this.mPlatformInsets.getSystemWindowInsetRight(), this.mPlatformInsets.getSystemWindowInsetBottom());
            }
            return this.mSystemWindowInsets;
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat inset(int i, int i2, int i3, int i4) {
            Builder builder = new Builder(WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets));
            builder.setSystemWindowInsets(WindowInsetsCompat.insetInsets(getSystemWindowInsets(), i, i2, i3, i4));
            builder.setStableInsets(WindowInsetsCompat.insetInsets(getStableInsets(), i, i2, i3, i4));
            return builder.build();
        }

        /* access modifiers changed from: package-private */
        public void copyWindowDataInto(WindowInsetsCompat windowInsetsCompat) {
            windowInsetsCompat.setRootWindowInsets(this.mRootWindowInsets);
            windowInsetsCompat.setRootViewData(this.mRootViewVisibleFrame, this.mRootViewHeight);
        }

        /* access modifiers changed from: package-private */
        public void setRootWindowInsets(WindowInsetsCompat windowInsetsCompat) {
            this.mRootWindowInsets = windowInsetsCompat;
        }

        /* access modifiers changed from: package-private */
        public void setRootViewData(Rect rect, int i) {
            this.mRootViewVisibleFrame = rect;
            this.mRootViewHeight = i;
        }
    }

    private static class Impl21 extends Impl20 {
        private Insets mStableInsets = null;

        Impl21(WindowInsetsCompat windowInsetsCompat, WindowInsets windowInsets) {
            super(windowInsetsCompat, windowInsets);
        }

        Impl21(WindowInsetsCompat windowInsetsCompat, Impl21 impl21) {
            super(windowInsetsCompat, (Impl20) impl21);
        }

        /* access modifiers changed from: package-private */
        public boolean isConsumed() {
            return this.mPlatformInsets.isConsumed();
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeStableInsets() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets.consumeStableInsets());
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeSystemWindowInsets() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets.consumeSystemWindowInsets());
        }

        /* access modifiers changed from: package-private */
        public final Insets getStableInsets() {
            if (this.mStableInsets == null) {
                this.mStableInsets = Insets.m2of(this.mPlatformInsets.getStableInsetLeft(), this.mPlatformInsets.getStableInsetTop(), this.mPlatformInsets.getStableInsetRight(), this.mPlatformInsets.getStableInsetBottom());
            }
            return this.mStableInsets;
        }
    }

    private static class Impl28 extends Impl21 {
        Impl28(WindowInsetsCompat windowInsetsCompat, WindowInsets windowInsets) {
            super(windowInsetsCompat, windowInsets);
        }

        Impl28(WindowInsetsCompat windowInsetsCompat, Impl28 impl28) {
            super(windowInsetsCompat, (Impl21) impl28);
        }

        /* access modifiers changed from: package-private */
        public DisplayCutoutCompat getDisplayCutout() {
            return DisplayCutoutCompat.wrap(this.mPlatformInsets.getDisplayCutout());
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeDisplayCutout() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets.consumeDisplayCutout());
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Impl28)) {
                return false;
            }
            return Objects.equals(this.mPlatformInsets, ((Impl28) obj).mPlatformInsets);
        }

        public int hashCode() {
            return this.mPlatformInsets.hashCode();
        }
    }

    private static class Impl29 extends Impl28 {
        private Insets mSystemGestureInsets = null;

        Impl29(WindowInsetsCompat windowInsetsCompat, WindowInsets windowInsets) {
            super(windowInsetsCompat, windowInsets);
        }

        Impl29(WindowInsetsCompat windowInsetsCompat, Impl29 impl29) {
            super(windowInsetsCompat, (Impl28) impl29);
        }

        /* access modifiers changed from: package-private */
        public Insets getSystemGestureInsets() {
            if (this.mSystemGestureInsets == null) {
                this.mSystemGestureInsets = Insets.toCompatInsets(this.mPlatformInsets.getSystemGestureInsets());
            }
            return this.mSystemGestureInsets;
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat inset(int i, int i2, int i3, int i4) {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets.inset(i, i2, i3, i4));
        }
    }

    static Insets insetInsets(Insets insets, int i, int i2, int i3, int i4) {
        int max = Math.max(0, insets.left - i);
        int max2 = Math.max(0, insets.top - i2);
        int max3 = Math.max(0, insets.right - i3);
        int max4 = Math.max(0, insets.bottom - i4);
        if (max == i && max2 == i2 && max3 == i3 && max4 == i4) {
            return insets;
        }
        return Insets.m2of(max, max2, max3, max4);
    }

    private static class Impl30 extends Impl29 {
        static final WindowInsetsCompat CONSUMED = WindowInsetsCompat.toWindowInsetsCompat(WindowInsets.CONSUMED);

        Impl30(WindowInsetsCompat windowInsetsCompat, WindowInsets windowInsets) {
            super(windowInsetsCompat, windowInsets);
        }

        Impl30(WindowInsetsCompat windowInsetsCompat, Impl30 impl30) {
            super(windowInsetsCompat, (Impl29) impl30);
        }
    }

    public static final class Builder {
        private final BuilderImpl mImpl;

        public Builder() {
            int i = Build.VERSION.SDK_INT;
            if (i >= 30) {
                this.mImpl = new BuilderImpl30();
            } else if (i >= 29) {
                this.mImpl = new BuilderImpl29();
            } else if (i >= 20) {
                this.mImpl = new BuilderImpl20();
            } else {
                this.mImpl = new BuilderImpl();
            }
        }

        public Builder(WindowInsetsCompat windowInsetsCompat) {
            int i = Build.VERSION.SDK_INT;
            if (i >= 30) {
                this.mImpl = new BuilderImpl30(windowInsetsCompat);
            } else if (i >= 29) {
                this.mImpl = new BuilderImpl29(windowInsetsCompat);
            } else if (i >= 20) {
                this.mImpl = new BuilderImpl20(windowInsetsCompat);
            } else {
                this.mImpl = new BuilderImpl(windowInsetsCompat);
            }
        }

        @Deprecated
        public Builder setSystemWindowInsets(Insets insets) {
            this.mImpl.setSystemWindowInsets(insets);
            return this;
        }

        @Deprecated
        public Builder setStableInsets(Insets insets) {
            this.mImpl.setStableInsets(insets);
            return this;
        }

        public WindowInsetsCompat build() {
            return this.mImpl.build();
        }
    }

    private static class BuilderImpl {
        private final WindowInsetsCompat mInsets;
        private Insets[] mInsetsTypeMask;

        /* access modifiers changed from: package-private */
        public void setMandatorySystemGestureInsets(Insets insets) {
        }

        /* access modifiers changed from: package-private */
        public void setStableInsets(Insets insets) {
        }

        /* access modifiers changed from: package-private */
        public void setSystemGestureInsets(Insets insets) {
        }

        /* access modifiers changed from: package-private */
        public void setSystemWindowInsets(Insets insets) {
        }

        /* access modifiers changed from: package-private */
        public void setTappableElementInsets(Insets insets) {
        }

        BuilderImpl() {
            this(new WindowInsetsCompat((WindowInsetsCompat) null));
        }

        BuilderImpl(WindowInsetsCompat windowInsetsCompat) {
            this.mInsets = windowInsetsCompat;
        }

        /* access modifiers changed from: protected */
        public final void applyInsetTypes() {
            Insets[] insetsArr = this.mInsetsTypeMask;
            if (insetsArr != null) {
                Insets insets = insetsArr[Type.indexOf(1)];
                Insets insets2 = this.mInsetsTypeMask[Type.indexOf(2)];
                if (insets != null && insets2 != null) {
                    setSystemWindowInsets(Insets.max(insets, insets2));
                } else if (insets != null) {
                    setSystemWindowInsets(insets);
                } else if (insets2 != null) {
                    setSystemWindowInsets(insets2);
                }
                Insets insets3 = this.mInsetsTypeMask[Type.indexOf(16)];
                if (insets3 != null) {
                    setSystemGestureInsets(insets3);
                }
                Insets insets4 = this.mInsetsTypeMask[Type.indexOf(32)];
                if (insets4 != null) {
                    setMandatorySystemGestureInsets(insets4);
                }
                Insets insets5 = this.mInsetsTypeMask[Type.indexOf(64)];
                if (insets5 != null) {
                    setTappableElementInsets(insets5);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat build() {
            applyInsetTypes();
            return this.mInsets;
        }
    }

    private static class BuilderImpl20 extends BuilderImpl {
        private static Constructor<WindowInsets> sConstructor = null;
        private static boolean sConstructorFetched = false;
        private static Field sConsumedField = null;
        private static boolean sConsumedFieldFetched = false;
        private WindowInsets mInsets;

        BuilderImpl20() {
            this.mInsets = createWindowInsetsInstance();
        }

        BuilderImpl20(WindowInsetsCompat windowInsetsCompat) {
            this.mInsets = windowInsetsCompat.toWindowInsets();
        }

        /* access modifiers changed from: package-private */
        public void setSystemWindowInsets(Insets insets) {
            WindowInsets windowInsets = this.mInsets;
            if (windowInsets != null) {
                this.mInsets = windowInsets.replaceSystemWindowInsets(insets.left, insets.top, insets.right, insets.bottom);
            }
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat build() {
            applyInsetTypes();
            return WindowInsetsCompat.toWindowInsetsCompat(this.mInsets);
        }

        private static WindowInsets createWindowInsetsInstance() {
            if (!sConsumedFieldFetched) {
                try {
                    sConsumedField = WindowInsets.class.getDeclaredField("CONSUMED");
                } catch (ReflectiveOperationException e) {
                    Log.i("WindowInsetsCompat", "Could not retrieve WindowInsets.CONSUMED field", e);
                }
                sConsumedFieldFetched = true;
            }
            Field field = sConsumedField;
            if (field != null) {
                try {
                    WindowInsets windowInsets = (WindowInsets) field.get((Object) null);
                    if (windowInsets != null) {
                        return new WindowInsets(windowInsets);
                    }
                } catch (ReflectiveOperationException e2) {
                    Log.i("WindowInsetsCompat", "Could not get value from WindowInsets.CONSUMED field", e2);
                }
            }
            if (!sConstructorFetched) {
                try {
                    sConstructor = WindowInsets.class.getConstructor(new Class[]{Rect.class});
                } catch (ReflectiveOperationException e3) {
                    Log.i("WindowInsetsCompat", "Could not retrieve WindowInsets(Rect) constructor", e3);
                }
                sConstructorFetched = true;
            }
            Constructor<WindowInsets> constructor = sConstructor;
            if (constructor != null) {
                try {
                    return constructor.newInstance(new Object[]{new Rect()});
                } catch (ReflectiveOperationException e4) {
                    Log.i("WindowInsetsCompat", "Could not invoke WindowInsets(Rect) constructor", e4);
                }
            }
            return null;
        }
    }

    private static class BuilderImpl29 extends BuilderImpl {
        final WindowInsets.Builder mPlatBuilder;

        BuilderImpl29() {
            this.mPlatBuilder = new WindowInsets.Builder();
        }

        BuilderImpl29(WindowInsetsCompat windowInsetsCompat) {
            WindowInsets.Builder builder;
            WindowInsets windowInsets = windowInsetsCompat.toWindowInsets();
            if (windowInsets != null) {
                builder = new WindowInsets.Builder(windowInsets);
            } else {
                builder = new WindowInsets.Builder();
            }
            this.mPlatBuilder = builder;
        }

        /* access modifiers changed from: package-private */
        public void setSystemWindowInsets(Insets insets) {
            this.mPlatBuilder.setSystemWindowInsets(insets.toPlatformInsets());
        }

        /* access modifiers changed from: package-private */
        public void setSystemGestureInsets(Insets insets) {
            this.mPlatBuilder.setSystemGestureInsets(insets.toPlatformInsets());
        }

        /* access modifiers changed from: package-private */
        public void setMandatorySystemGestureInsets(Insets insets) {
            this.mPlatBuilder.setMandatorySystemGestureInsets(insets.toPlatformInsets());
        }

        /* access modifiers changed from: package-private */
        public void setTappableElementInsets(Insets insets) {
            this.mPlatBuilder.setTappableElementInsets(insets.toPlatformInsets());
        }

        /* access modifiers changed from: package-private */
        public void setStableInsets(Insets insets) {
            this.mPlatBuilder.setStableInsets(insets.toPlatformInsets());
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat build() {
            applyInsetTypes();
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatBuilder.build());
        }
    }

    private static class BuilderImpl30 extends BuilderImpl29 {
        BuilderImpl30() {
        }

        BuilderImpl30(WindowInsetsCompat windowInsetsCompat) {
            super(windowInsetsCompat);
        }
    }

    public static final class Type {
        static int indexOf(int i) {
            if (i == 1) {
                return 0;
            }
            if (i == 2) {
                return 1;
            }
            if (i == 4) {
                return 2;
            }
            if (i == 8) {
                return 3;
            }
            if (i == 16) {
                return 4;
            }
            if (i == 32) {
                return 5;
            }
            if (i == 64) {
                return 6;
            }
            if (i == 128) {
                return 7;
            }
            if (i == 256) {
                return 8;
            }
            throw new IllegalArgumentException("type needs to be >= FIRST and <= LAST, type=" + i);
        }
    }

    /* access modifiers changed from: package-private */
    public void setRootWindowInsets(WindowInsetsCompat windowInsetsCompat) {
        this.mImpl.setRootWindowInsets(windowInsetsCompat);
    }

    /* access modifiers changed from: package-private */
    public void setRootViewData(Rect rect, int i) {
        this.mImpl.setRootViewData(rect, i);
    }

    /* access modifiers changed from: package-private */
    public void copyRootViewBounds(View view) {
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        setRootViewData(rect, view.getHeight());
    }
}

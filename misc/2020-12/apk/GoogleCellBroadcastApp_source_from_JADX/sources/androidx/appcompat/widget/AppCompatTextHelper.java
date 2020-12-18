package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.TextView;
import androidx.appcompat.R$styleable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.AutoSizeableTextView;
import java.lang.ref.WeakReference;

class AppCompatTextHelper {
    private boolean mAsyncFontPending;
    private final AppCompatTextViewAutoSizeHelper mAutoSizeTextHelper;
    private TintInfo mDrawableBottomTint;
    private TintInfo mDrawableEndTint;
    private TintInfo mDrawableLeftTint;
    private TintInfo mDrawableRightTint;
    private TintInfo mDrawableStartTint;
    private TintInfo mDrawableTint;
    private TintInfo mDrawableTopTint;
    private Typeface mFontTypeface;
    private int mFontWeight = -1;
    private int mStyle = 0;
    private final TextView mView;

    AppCompatTextHelper(TextView textView) {
        this.mView = textView;
        this.mAutoSizeTextHelper = new AppCompatTextViewAutoSizeHelper(this.mView);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0133  */
    @android.annotation.SuppressLint({"NewApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadFromAttributes(android.util.AttributeSet r19, int r20) {
        /*
            r18 = this;
            r7 = r18
            r8 = r19
            r9 = r20
            int r10 = android.os.Build.VERSION.SDK_INT
            android.widget.TextView r0 = r7.mView
            android.content.Context r11 = r0.getContext()
            androidx.appcompat.widget.AppCompatDrawableManager r12 = androidx.appcompat.widget.AppCompatDrawableManager.get()
            int[] r0 = androidx.appcompat.R$styleable.AppCompatTextHelper
            r13 = 0
            androidx.appcompat.widget.TintTypedArray r14 = androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes(r11, r8, r0, r9, r13)
            android.widget.TextView r0 = r7.mView
            android.content.Context r1 = r0.getContext()
            int[] r2 = androidx.appcompat.R$styleable.AppCompatTextHelper
            android.content.res.TypedArray r4 = r14.getWrappedTypeArray()
            r6 = 0
            r3 = r19
            r5 = r20
            androidx.core.view.ViewCompat.saveAttributeDataForStyleable(r0, r1, r2, r3, r4, r5, r6)
            int r0 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_textAppearance
            r15 = -1
            int r0 = r14.getResourceId(r0, r15)
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableLeft
            boolean r1 = r14.hasValue(r1)
            if (r1 == 0) goto L_0x0048
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableLeft
            int r1 = r14.getResourceId(r1, r13)
            androidx.appcompat.widget.TintInfo r1 = createTintInfo(r11, r12, r1)
            r7.mDrawableLeftTint = r1
        L_0x0048:
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableTop
            boolean r1 = r14.hasValue(r1)
            if (r1 == 0) goto L_0x005c
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableTop
            int r1 = r14.getResourceId(r1, r13)
            androidx.appcompat.widget.TintInfo r1 = createTintInfo(r11, r12, r1)
            r7.mDrawableTopTint = r1
        L_0x005c:
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableRight
            boolean r1 = r14.hasValue(r1)
            if (r1 == 0) goto L_0x0070
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableRight
            int r1 = r14.getResourceId(r1, r13)
            androidx.appcompat.widget.TintInfo r1 = createTintInfo(r11, r12, r1)
            r7.mDrawableRightTint = r1
        L_0x0070:
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableBottom
            boolean r1 = r14.hasValue(r1)
            if (r1 == 0) goto L_0x0084
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableBottom
            int r1 = r14.getResourceId(r1, r13)
            androidx.appcompat.widget.TintInfo r1 = createTintInfo(r11, r12, r1)
            r7.mDrawableBottomTint = r1
        L_0x0084:
            r1 = 17
            if (r10 < r1) goto L_0x00b0
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableStart
            boolean r1 = r14.hasValue(r1)
            if (r1 == 0) goto L_0x009c
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableStart
            int r1 = r14.getResourceId(r1, r13)
            androidx.appcompat.widget.TintInfo r1 = createTintInfo(r11, r12, r1)
            r7.mDrawableStartTint = r1
        L_0x009c:
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableEnd
            boolean r1 = r14.hasValue(r1)
            if (r1 == 0) goto L_0x00b0
            int r1 = androidx.appcompat.R$styleable.AppCompatTextHelper_android_drawableEnd
            int r1 = r14.getResourceId(r1, r13)
            androidx.appcompat.widget.TintInfo r1 = createTintInfo(r11, r12, r1)
            r7.mDrawableEndTint = r1
        L_0x00b0:
            r14.recycle()
            android.widget.TextView r1 = r7.mView
            android.text.method.TransformationMethod r1 = r1.getTransformationMethod()
            boolean r1 = r1 instanceof android.text.method.PasswordTransformationMethod
            r2 = 26
            r4 = 23
            if (r0 == r15) goto L_0x0138
            int[] r5 = androidx.appcompat.R$styleable.TextAppearance
            androidx.appcompat.widget.TintTypedArray r0 = androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes((android.content.Context) r11, (int) r0, (int[]) r5)
            if (r1 != 0) goto L_0x00d9
            int r5 = androidx.appcompat.R$styleable.TextAppearance_textAllCaps
            boolean r5 = r0.hasValue(r5)
            if (r5 == 0) goto L_0x00d9
            int r5 = androidx.appcompat.R$styleable.TextAppearance_textAllCaps
            boolean r5 = r0.getBoolean(r5, r13)
            r6 = 1
            goto L_0x00db
        L_0x00d9:
            r5 = r13
            r6 = r5
        L_0x00db:
            r7.updateTypefaceAndStyle(r11, r0)
            if (r10 >= r4) goto L_0x010f
            int r3 = androidx.appcompat.R$styleable.TextAppearance_android_textColor
            boolean r3 = r0.hasValue(r3)
            if (r3 == 0) goto L_0x00ef
            int r3 = androidx.appcompat.R$styleable.TextAppearance_android_textColor
            android.content.res.ColorStateList r3 = r0.getColorStateList(r3)
            goto L_0x00f0
        L_0x00ef:
            r3 = 0
        L_0x00f0:
            int r14 = androidx.appcompat.R$styleable.TextAppearance_android_textColorHint
            boolean r14 = r0.hasValue(r14)
            if (r14 == 0) goto L_0x00ff
            int r14 = androidx.appcompat.R$styleable.TextAppearance_android_textColorHint
            android.content.res.ColorStateList r14 = r0.getColorStateList(r14)
            goto L_0x0100
        L_0x00ff:
            r14 = 0
        L_0x0100:
            int r15 = androidx.appcompat.R$styleable.TextAppearance_android_textColorLink
            boolean r15 = r0.hasValue(r15)
            if (r15 == 0) goto L_0x0111
            int r15 = androidx.appcompat.R$styleable.TextAppearance_android_textColorLink
            android.content.res.ColorStateList r15 = r0.getColorStateList(r15)
            goto L_0x0112
        L_0x010f:
            r3 = 0
            r14 = 0
        L_0x0111:
            r15 = 0
        L_0x0112:
            int r4 = androidx.appcompat.R$styleable.TextAppearance_textLocale
            boolean r4 = r0.hasValue(r4)
            if (r4 == 0) goto L_0x0121
            int r4 = androidx.appcompat.R$styleable.TextAppearance_textLocale
            java.lang.String r4 = r0.getString(r4)
            goto L_0x0122
        L_0x0121:
            r4 = 0
        L_0x0122:
            if (r10 < r2) goto L_0x0133
            int r2 = androidx.appcompat.R$styleable.TextAppearance_fontVariationSettings
            boolean r2 = r0.hasValue(r2)
            if (r2 == 0) goto L_0x0133
            int r2 = androidx.appcompat.R$styleable.TextAppearance_fontVariationSettings
            java.lang.String r2 = r0.getString(r2)
            goto L_0x0134
        L_0x0133:
            r2 = 0
        L_0x0134:
            r0.recycle()
            goto L_0x013f
        L_0x0138:
            r5 = r13
            r6 = r5
            r2 = 0
            r3 = 0
            r4 = 0
            r14 = 0
            r15 = 0
        L_0x013f:
            int[] r0 = androidx.appcompat.R$styleable.TextAppearance
            androidx.appcompat.widget.TintTypedArray r0 = androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes(r11, r8, r0, r9, r13)
            if (r1 != 0) goto L_0x015b
            int r13 = androidx.appcompat.R$styleable.TextAppearance_textAllCaps
            boolean r13 = r0.hasValue(r13)
            if (r13 == 0) goto L_0x015b
            int r5 = androidx.appcompat.R$styleable.TextAppearance_textAllCaps
            r6 = 0
            boolean r5 = r0.getBoolean(r5, r6)
            r6 = 23
            r16 = 1
            goto L_0x015f
        L_0x015b:
            r16 = r6
            r6 = 23
        L_0x015f:
            if (r10 >= r6) goto L_0x018b
            int r6 = androidx.appcompat.R$styleable.TextAppearance_android_textColor
            boolean r6 = r0.hasValue(r6)
            if (r6 == 0) goto L_0x016f
            int r3 = androidx.appcompat.R$styleable.TextAppearance_android_textColor
            android.content.res.ColorStateList r3 = r0.getColorStateList(r3)
        L_0x016f:
            int r6 = androidx.appcompat.R$styleable.TextAppearance_android_textColorHint
            boolean r6 = r0.hasValue(r6)
            if (r6 == 0) goto L_0x017d
            int r6 = androidx.appcompat.R$styleable.TextAppearance_android_textColorHint
            android.content.res.ColorStateList r14 = r0.getColorStateList(r6)
        L_0x017d:
            int r6 = androidx.appcompat.R$styleable.TextAppearance_android_textColorLink
            boolean r6 = r0.hasValue(r6)
            if (r6 == 0) goto L_0x018b
            int r6 = androidx.appcompat.R$styleable.TextAppearance_android_textColorLink
            android.content.res.ColorStateList r15 = r0.getColorStateList(r6)
        L_0x018b:
            int r6 = androidx.appcompat.R$styleable.TextAppearance_textLocale
            boolean r6 = r0.hasValue(r6)
            if (r6 == 0) goto L_0x0199
            int r4 = androidx.appcompat.R$styleable.TextAppearance_textLocale
            java.lang.String r4 = r0.getString(r4)
        L_0x0199:
            r6 = 26
            if (r10 < r6) goto L_0x01ab
            int r6 = androidx.appcompat.R$styleable.TextAppearance_fontVariationSettings
            boolean r6 = r0.hasValue(r6)
            if (r6 == 0) goto L_0x01ab
            int r2 = androidx.appcompat.R$styleable.TextAppearance_fontVariationSettings
            java.lang.String r2 = r0.getString(r2)
        L_0x01ab:
            r6 = 28
            if (r10 < r6) goto L_0x01ca
            int r6 = androidx.appcompat.R$styleable.TextAppearance_android_textSize
            boolean r6 = r0.hasValue(r6)
            if (r6 == 0) goto L_0x01ca
            int r6 = androidx.appcompat.R$styleable.TextAppearance_android_textSize
            r13 = -1
            int r6 = r0.getDimensionPixelSize(r6, r13)
            if (r6 != 0) goto L_0x01ca
            android.widget.TextView r6 = r7.mView
            r13 = 0
            r17 = r12
            r12 = 0
            r6.setTextSize(r12, r13)
            goto L_0x01cc
        L_0x01ca:
            r17 = r12
        L_0x01cc:
            r7.updateTypefaceAndStyle(r11, r0)
            r0.recycle()
            if (r3 == 0) goto L_0x01d9
            android.widget.TextView r0 = r7.mView
            r0.setTextColor(r3)
        L_0x01d9:
            if (r14 == 0) goto L_0x01e0
            android.widget.TextView r0 = r7.mView
            r0.setHintTextColor(r14)
        L_0x01e0:
            if (r15 == 0) goto L_0x01e7
            android.widget.TextView r0 = r7.mView
            r0.setLinkTextColor(r15)
        L_0x01e7:
            if (r1 != 0) goto L_0x01ee
            if (r16 == 0) goto L_0x01ee
            r7.setAllCaps(r5)
        L_0x01ee:
            android.graphics.Typeface r0 = r7.mFontTypeface
            if (r0 == 0) goto L_0x0204
            int r1 = r7.mFontWeight
            r3 = -1
            if (r1 != r3) goto L_0x01ff
            android.widget.TextView r1 = r7.mView
            int r3 = r7.mStyle
            r1.setTypeface(r0, r3)
            goto L_0x0204
        L_0x01ff:
            android.widget.TextView r1 = r7.mView
            r1.setTypeface(r0)
        L_0x0204:
            if (r2 == 0) goto L_0x020b
            android.widget.TextView r0 = r7.mView
            r0.setFontVariationSettings(r2)
        L_0x020b:
            if (r4 == 0) goto L_0x0233
            r0 = 24
            if (r10 < r0) goto L_0x021b
            android.widget.TextView r0 = r7.mView
            android.os.LocaleList r1 = android.os.LocaleList.forLanguageTags(r4)
            r0.setTextLocales(r1)
            goto L_0x0233
        L_0x021b:
            r0 = 21
            if (r10 < r0) goto L_0x0233
            r0 = 44
            int r0 = r4.indexOf(r0)
            r1 = 0
            java.lang.String r0 = r4.substring(r1, r0)
            android.widget.TextView r1 = r7.mView
            java.util.Locale r0 = java.util.Locale.forLanguageTag(r0)
            r1.setTextLocale(r0)
        L_0x0233:
            androidx.appcompat.widget.AppCompatTextViewAutoSizeHelper r0 = r7.mAutoSizeTextHelper
            r0.loadFromAttributes(r8, r9)
            boolean r0 = androidx.core.widget.AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE
            if (r0 == 0) goto L_0x0279
            androidx.appcompat.widget.AppCompatTextViewAutoSizeHelper r0 = r7.mAutoSizeTextHelper
            int r0 = r0.getAutoSizeTextType()
            if (r0 == 0) goto L_0x0279
            androidx.appcompat.widget.AppCompatTextViewAutoSizeHelper r0 = r7.mAutoSizeTextHelper
            int[] r0 = r0.getAutoSizeTextAvailableSizes()
            int r1 = r0.length
            if (r1 <= 0) goto L_0x0279
            android.widget.TextView r1 = r7.mView
            int r1 = r1.getAutoSizeStepGranularity()
            float r1 = (float) r1
            r2 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0273
            android.widget.TextView r0 = r7.mView
            androidx.appcompat.widget.AppCompatTextViewAutoSizeHelper r1 = r7.mAutoSizeTextHelper
            int r1 = r1.getAutoSizeMinTextSize()
            androidx.appcompat.widget.AppCompatTextViewAutoSizeHelper r2 = r7.mAutoSizeTextHelper
            int r2 = r2.getAutoSizeMaxTextSize()
            androidx.appcompat.widget.AppCompatTextViewAutoSizeHelper r3 = r7.mAutoSizeTextHelper
            int r3 = r3.getAutoSizeStepGranularity()
            r4 = 0
            r0.setAutoSizeTextTypeUniformWithConfiguration(r1, r2, r3, r4)
            goto L_0x0279
        L_0x0273:
            r4 = 0
            android.widget.TextView r1 = r7.mView
            r1.setAutoSizeTextTypeUniformWithPresetSizes(r0, r4)
        L_0x0279:
            int[] r0 = androidx.appcompat.R$styleable.AppCompatTextView
            androidx.appcompat.widget.TintTypedArray r8 = androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes((android.content.Context) r11, (android.util.AttributeSet) r8, (int[]) r0)
            int r0 = androidx.appcompat.R$styleable.AppCompatTextView_drawableLeftCompat
            r1 = -1
            int r0 = r8.getResourceId(r0, r1)
            r2 = r17
            if (r0 == r1) goto L_0x0290
            android.graphics.drawable.Drawable r0 = r2.getDrawable(r11, r0)
            r3 = r0
            goto L_0x0291
        L_0x0290:
            r3 = 0
        L_0x0291:
            int r0 = androidx.appcompat.R$styleable.AppCompatTextView_drawableTopCompat
            int r0 = r8.getResourceId(r0, r1)
            if (r0 == r1) goto L_0x029f
            android.graphics.drawable.Drawable r0 = r2.getDrawable(r11, r0)
            r4 = r0
            goto L_0x02a0
        L_0x029f:
            r4 = 0
        L_0x02a0:
            int r0 = androidx.appcompat.R$styleable.AppCompatTextView_drawableRightCompat
            int r0 = r8.getResourceId(r0, r1)
            if (r0 == r1) goto L_0x02ae
            android.graphics.drawable.Drawable r0 = r2.getDrawable(r11, r0)
            r5 = r0
            goto L_0x02af
        L_0x02ae:
            r5 = 0
        L_0x02af:
            int r0 = androidx.appcompat.R$styleable.AppCompatTextView_drawableBottomCompat
            int r0 = r8.getResourceId(r0, r1)
            if (r0 == r1) goto L_0x02bd
            android.graphics.drawable.Drawable r0 = r2.getDrawable(r11, r0)
            r6 = r0
            goto L_0x02be
        L_0x02bd:
            r6 = 0
        L_0x02be:
            int r0 = androidx.appcompat.R$styleable.AppCompatTextView_drawableStartCompat
            int r0 = r8.getResourceId(r0, r1)
            if (r0 == r1) goto L_0x02cc
            android.graphics.drawable.Drawable r0 = r2.getDrawable(r11, r0)
            r9 = r0
            goto L_0x02cd
        L_0x02cc:
            r9 = 0
        L_0x02cd:
            int r0 = androidx.appcompat.R$styleable.AppCompatTextView_drawableEndCompat
            int r0 = r8.getResourceId(r0, r1)
            if (r0 == r1) goto L_0x02db
            android.graphics.drawable.Drawable r0 = r2.getDrawable(r11, r0)
            r10 = r0
            goto L_0x02dc
        L_0x02db:
            r10 = 0
        L_0x02dc:
            r0 = r18
            r1 = r3
            r2 = r4
            r3 = r5
            r4 = r6
            r5 = r9
            r6 = r10
            r0.setCompoundDrawables(r1, r2, r3, r4, r5, r6)
            int r0 = androidx.appcompat.R$styleable.AppCompatTextView_drawableTint
            boolean r0 = r8.hasValue(r0)
            if (r0 == 0) goto L_0x02fa
            int r0 = androidx.appcompat.R$styleable.AppCompatTextView_drawableTint
            android.content.res.ColorStateList r0 = r8.getColorStateList(r0)
            android.widget.TextView r1 = r7.mView
            androidx.core.widget.TextViewCompat.setCompoundDrawableTintList(r1, r0)
        L_0x02fa:
            int r0 = androidx.appcompat.R$styleable.AppCompatTextView_drawableTintMode
            boolean r0 = r8.hasValue(r0)
            if (r0 == 0) goto L_0x0314
            int r0 = androidx.appcompat.R$styleable.AppCompatTextView_drawableTintMode
            r1 = -1
            int r0 = r8.getInt(r0, r1)
            r2 = 0
            android.graphics.PorterDuff$Mode r0 = androidx.appcompat.widget.DrawableUtils.parseTintMode(r0, r2)
            android.widget.TextView r2 = r7.mView
            androidx.core.widget.TextViewCompat.setCompoundDrawableTintMode(r2, r0)
            goto L_0x0315
        L_0x0314:
            r1 = -1
        L_0x0315:
            int r0 = androidx.appcompat.R$styleable.AppCompatTextView_firstBaselineToTopHeight
            int r0 = r8.getDimensionPixelSize(r0, r1)
            int r2 = androidx.appcompat.R$styleable.AppCompatTextView_lastBaselineToBottomHeight
            int r2 = r8.getDimensionPixelSize(r2, r1)
            int r3 = androidx.appcompat.R$styleable.AppCompatTextView_lineHeight
            int r3 = r8.getDimensionPixelSize(r3, r1)
            r8.recycle()
            if (r0 == r1) goto L_0x0331
            android.widget.TextView r4 = r7.mView
            androidx.core.widget.TextViewCompat.setFirstBaselineToTopHeight(r4, r0)
        L_0x0331:
            if (r2 == r1) goto L_0x0338
            android.widget.TextView r0 = r7.mView
            androidx.core.widget.TextViewCompat.setLastBaselineToBottomHeight(r0, r2)
        L_0x0338:
            if (r3 == r1) goto L_0x033f
            android.widget.TextView r0 = r7.mView
            androidx.core.widget.TextViewCompat.setLineHeight(r0, r3)
        L_0x033f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.AppCompatTextHelper.loadFromAttributes(android.util.AttributeSet, int):void");
    }

    private void updateTypefaceAndStyle(Context context, TintTypedArray tintTypedArray) {
        int i;
        String string;
        int i2 = Build.VERSION.SDK_INT;
        this.mStyle = tintTypedArray.getInt(R$styleable.TextAppearance_android_textStyle, this.mStyle);
        boolean z = false;
        if (i2 >= 28) {
            int i3 = tintTypedArray.getInt(R$styleable.TextAppearance_android_textFontWeight, -1);
            this.mFontWeight = i3;
            if (i3 != -1) {
                this.mStyle = (this.mStyle & 2) | 0;
            }
        }
        if (tintTypedArray.hasValue(R$styleable.TextAppearance_android_fontFamily) || tintTypedArray.hasValue(R$styleable.TextAppearance_fontFamily)) {
            this.mFontTypeface = null;
            if (tintTypedArray.hasValue(R$styleable.TextAppearance_fontFamily)) {
                i = R$styleable.TextAppearance_fontFamily;
            } else {
                i = R$styleable.TextAppearance_android_fontFamily;
            }
            final int i4 = this.mFontWeight;
            final int i5 = this.mStyle;
            if (!context.isRestricted()) {
                final WeakReference weakReference = new WeakReference(this.mView);
                try {
                    Typeface font = tintTypedArray.getFont(i, this.mStyle, new ResourcesCompat.FontCallback() {
                        public void onFontRetrievalFailed(int i) {
                        }

                        public void onFontRetrieved(Typeface typeface) {
                            int i;
                            if (Build.VERSION.SDK_INT >= 28 && (i = i4) != -1) {
                                typeface = Typeface.create(typeface, i, (i5 & 2) != 0);
                            }
                            AppCompatTextHelper.this.onAsyncTypefaceReceived(weakReference, typeface);
                        }
                    });
                    if (font != null) {
                        if (i2 < 28 || this.mFontWeight == -1) {
                            this.mFontTypeface = font;
                        } else {
                            this.mFontTypeface = Typeface.create(Typeface.create(font, 0), this.mFontWeight, (this.mStyle & 2) != 0);
                        }
                    }
                    this.mAsyncFontPending = this.mFontTypeface == null;
                } catch (Resources.NotFoundException | UnsupportedOperationException unused) {
                }
            }
            if (this.mFontTypeface == null && (string = tintTypedArray.getString(i)) != null) {
                if (i2 < 28 || this.mFontWeight == -1) {
                    this.mFontTypeface = Typeface.create(string, this.mStyle);
                    return;
                }
                Typeface create = Typeface.create(string, 0);
                int i6 = this.mFontWeight;
                if ((this.mStyle & 2) != 0) {
                    z = true;
                }
                this.mFontTypeface = Typeface.create(create, i6, z);
            }
        } else if (tintTypedArray.hasValue(R$styleable.TextAppearance_android_typeface)) {
            this.mAsyncFontPending = false;
            int i7 = tintTypedArray.getInt(R$styleable.TextAppearance_android_typeface, 1);
            if (i7 == 1) {
                this.mFontTypeface = Typeface.SANS_SERIF;
            } else if (i7 == 2) {
                this.mFontTypeface = Typeface.SERIF;
            } else if (i7 == 3) {
                this.mFontTypeface = Typeface.MONOSPACE;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onAsyncTypefaceReceived(WeakReference<TextView> weakReference, Typeface typeface) {
        if (this.mAsyncFontPending) {
            this.mFontTypeface = typeface;
            TextView textView = (TextView) weakReference.get();
            if (textView != null) {
                textView.setTypeface(typeface, this.mStyle);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onSetTextAppearance(Context context, int i) {
        String string;
        ColorStateList colorStateList;
        ColorStateList colorStateList2;
        ColorStateList colorStateList3;
        int i2 = Build.VERSION.SDK_INT;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, i, R$styleable.TextAppearance);
        if (obtainStyledAttributes.hasValue(R$styleable.TextAppearance_textAllCaps)) {
            setAllCaps(obtainStyledAttributes.getBoolean(R$styleable.TextAppearance_textAllCaps, false));
        }
        if (i2 < 23) {
            if (obtainStyledAttributes.hasValue(R$styleable.TextAppearance_android_textColor) && (colorStateList3 = obtainStyledAttributes.getColorStateList(R$styleable.TextAppearance_android_textColor)) != null) {
                this.mView.setTextColor(colorStateList3);
            }
            if (obtainStyledAttributes.hasValue(R$styleable.TextAppearance_android_textColorLink) && (colorStateList2 = obtainStyledAttributes.getColorStateList(R$styleable.TextAppearance_android_textColorLink)) != null) {
                this.mView.setLinkTextColor(colorStateList2);
            }
            if (obtainStyledAttributes.hasValue(R$styleable.TextAppearance_android_textColorHint) && (colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.TextAppearance_android_textColorHint)) != null) {
                this.mView.setHintTextColor(colorStateList);
            }
        }
        if (obtainStyledAttributes.hasValue(R$styleable.TextAppearance_android_textSize) && obtainStyledAttributes.getDimensionPixelSize(R$styleable.TextAppearance_android_textSize, -1) == 0) {
            this.mView.setTextSize(0, 0.0f);
        }
        updateTypefaceAndStyle(context, obtainStyledAttributes);
        if (i2 >= 26 && obtainStyledAttributes.hasValue(R$styleable.TextAppearance_fontVariationSettings) && (string = obtainStyledAttributes.getString(R$styleable.TextAppearance_fontVariationSettings)) != null) {
            this.mView.setFontVariationSettings(string);
        }
        obtainStyledAttributes.recycle();
        Typeface typeface = this.mFontTypeface;
        if (typeface != null) {
            this.mView.setTypeface(typeface, this.mStyle);
        }
    }

    /* access modifiers changed from: package-private */
    public void setAllCaps(boolean z) {
        this.mView.setAllCaps(z);
    }

    /* access modifiers changed from: package-private */
    public void onSetCompoundDrawables() {
        applyCompoundDrawablesTints();
    }

    /* access modifiers changed from: package-private */
    public void applyCompoundDrawablesTints() {
        if (!(this.mDrawableLeftTint == null && this.mDrawableTopTint == null && this.mDrawableRightTint == null && this.mDrawableBottomTint == null)) {
            Drawable[] compoundDrawables = this.mView.getCompoundDrawables();
            applyCompoundDrawableTint(compoundDrawables[0], this.mDrawableLeftTint);
            applyCompoundDrawableTint(compoundDrawables[1], this.mDrawableTopTint);
            applyCompoundDrawableTint(compoundDrawables[2], this.mDrawableRightTint);
            applyCompoundDrawableTint(compoundDrawables[3], this.mDrawableBottomTint);
        }
        if (Build.VERSION.SDK_INT < 17) {
            return;
        }
        if (this.mDrawableStartTint != null || this.mDrawableEndTint != null) {
            Drawable[] compoundDrawablesRelative = this.mView.getCompoundDrawablesRelative();
            applyCompoundDrawableTint(compoundDrawablesRelative[0], this.mDrawableStartTint);
            applyCompoundDrawableTint(compoundDrawablesRelative[2], this.mDrawableEndTint);
        }
    }

    private void applyCompoundDrawableTint(Drawable drawable, TintInfo tintInfo) {
        if (drawable != null && tintInfo != null) {
            AppCompatDrawableManager.tintDrawable(drawable, tintInfo, this.mView.getDrawableState());
        }
    }

    private static TintInfo createTintInfo(Context context, AppCompatDrawableManager appCompatDrawableManager, int i) {
        ColorStateList tintList = appCompatDrawableManager.getTintList(context, i);
        if (tintList == null) {
            return null;
        }
        TintInfo tintInfo = new TintInfo();
        tintInfo.mHasTintList = true;
        tintInfo.mTintList = tintList;
        return tintInfo;
    }

    /* access modifiers changed from: package-private */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (!AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
            autoSizeText();
        }
    }

    /* access modifiers changed from: package-private */
    public void setTextSize(int i, float f) {
        if (!AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE && !isAutoSizeEnabled()) {
            setTextSizeInternal(i, f);
        }
    }

    /* access modifiers changed from: package-private */
    public void autoSizeText() {
        this.mAutoSizeTextHelper.autoSizeText();
    }

    /* access modifiers changed from: package-private */
    public boolean isAutoSizeEnabled() {
        return this.mAutoSizeTextHelper.isAutoSizeEnabled();
    }

    private void setTextSizeInternal(int i, float f) {
        this.mAutoSizeTextHelper.setTextSizeInternal(i, f);
    }

    /* access modifiers changed from: package-private */
    public void setAutoSizeTextTypeWithDefaults(int i) {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeWithDefaults(i);
    }

    /* access modifiers changed from: package-private */
    public void setAutoSizeTextTypeUniformWithConfiguration(int i, int i2, int i3, int i4) throws IllegalArgumentException {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithConfiguration(i, i2, i3, i4);
    }

    /* access modifiers changed from: package-private */
    public void setAutoSizeTextTypeUniformWithPresetSizes(int[] iArr, int i) throws IllegalArgumentException {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithPresetSizes(iArr, i);
    }

    /* access modifiers changed from: package-private */
    public int getAutoSizeTextType() {
        return this.mAutoSizeTextHelper.getAutoSizeTextType();
    }

    /* access modifiers changed from: package-private */
    public int getAutoSizeStepGranularity() {
        return this.mAutoSizeTextHelper.getAutoSizeStepGranularity();
    }

    /* access modifiers changed from: package-private */
    public int getAutoSizeMinTextSize() {
        return this.mAutoSizeTextHelper.getAutoSizeMinTextSize();
    }

    /* access modifiers changed from: package-private */
    public int getAutoSizeMaxTextSize() {
        return this.mAutoSizeTextHelper.getAutoSizeMaxTextSize();
    }

    /* access modifiers changed from: package-private */
    public int[] getAutoSizeTextAvailableSizes() {
        return this.mAutoSizeTextHelper.getAutoSizeTextAvailableSizes();
    }

    /* access modifiers changed from: package-private */
    public ColorStateList getCompoundDrawableTintList() {
        TintInfo tintInfo = this.mDrawableTint;
        if (tintInfo != null) {
            return tintInfo.mTintList;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void setCompoundDrawableTintList(ColorStateList colorStateList) {
        if (this.mDrawableTint == null) {
            this.mDrawableTint = new TintInfo();
        }
        TintInfo tintInfo = this.mDrawableTint;
        tintInfo.mTintList = colorStateList;
        tintInfo.mHasTintList = colorStateList != null;
        setCompoundTints();
    }

    /* access modifiers changed from: package-private */
    public PorterDuff.Mode getCompoundDrawableTintMode() {
        TintInfo tintInfo = this.mDrawableTint;
        if (tintInfo != null) {
            return tintInfo.mTintMode;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void setCompoundDrawableTintMode(PorterDuff.Mode mode) {
        if (this.mDrawableTint == null) {
            this.mDrawableTint = new TintInfo();
        }
        TintInfo tintInfo = this.mDrawableTint;
        tintInfo.mTintMode = mode;
        tintInfo.mHasTintMode = mode != null;
        setCompoundTints();
    }

    private void setCompoundTints() {
        TintInfo tintInfo = this.mDrawableTint;
        this.mDrawableLeftTint = tintInfo;
        this.mDrawableTopTint = tintInfo;
        this.mDrawableRightTint = tintInfo;
        this.mDrawableBottomTint = tintInfo;
        this.mDrawableStartTint = tintInfo;
        this.mDrawableEndTint = tintInfo;
    }

    private void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4, Drawable drawable5, Drawable drawable6) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 17 && (drawable5 != null || drawable6 != null)) {
            Drawable[] compoundDrawablesRelative = this.mView.getCompoundDrawablesRelative();
            TextView textView = this.mView;
            if (drawable5 == null) {
                drawable5 = compoundDrawablesRelative[0];
            }
            if (drawable2 == null) {
                drawable2 = compoundDrawablesRelative[1];
            }
            if (drawable6 == null) {
                drawable6 = compoundDrawablesRelative[2];
            }
            if (drawable4 == null) {
                drawable4 = compoundDrawablesRelative[3];
            }
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable5, drawable2, drawable6, drawable4);
        } else if (drawable != null || drawable2 != null || drawable3 != null || drawable4 != null) {
            if (i >= 17) {
                Drawable[] compoundDrawablesRelative2 = this.mView.getCompoundDrawablesRelative();
                if (!(compoundDrawablesRelative2[0] == null && compoundDrawablesRelative2[2] == null)) {
                    TextView textView2 = this.mView;
                    Drawable drawable7 = compoundDrawablesRelative2[0];
                    if (drawable2 == null) {
                        drawable2 = compoundDrawablesRelative2[1];
                    }
                    Drawable drawable8 = compoundDrawablesRelative2[2];
                    if (drawable4 == null) {
                        drawable4 = compoundDrawablesRelative2[3];
                    }
                    textView2.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable7, drawable2, drawable8, drawable4);
                    return;
                }
            }
            Drawable[] compoundDrawables = this.mView.getCompoundDrawables();
            TextView textView3 = this.mView;
            if (drawable == null) {
                drawable = compoundDrawables[0];
            }
            if (drawable2 == null) {
                drawable2 = compoundDrawables[1];
            }
            if (drawable3 == null) {
                drawable3 = compoundDrawables[2];
            }
            if (drawable4 == null) {
                drawable4 = compoundDrawables[3];
            }
            textView3.setCompoundDrawablesWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        }
    }
}

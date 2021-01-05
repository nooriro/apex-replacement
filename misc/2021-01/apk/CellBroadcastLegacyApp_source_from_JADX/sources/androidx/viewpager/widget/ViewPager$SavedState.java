package androidx.viewpager.widget;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.customview.view.AbsSavedState;

public class ViewPager$SavedState extends AbsSavedState {
    public static final Parcelable.Creator<ViewPager$SavedState> CREATOR = new Parcelable.ClassLoaderCreator<ViewPager$SavedState>() {
        public ViewPager$SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new ViewPager$SavedState(parcel, classLoader);
        }

        public ViewPager$SavedState createFromParcel(Parcel parcel) {
            return new ViewPager$SavedState(parcel, (ClassLoader) null);
        }

        public ViewPager$SavedState[] newArray(int i) {
            return new ViewPager$SavedState[i];
        }
    };
    Parcelable adapterState;
    int position;

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.position);
        parcel.writeParcelable(this.adapterState, i);
    }

    public String toString() {
        return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
    }

    ViewPager$SavedState(Parcel parcel, ClassLoader classLoader) {
        super(parcel, classLoader);
        classLoader = classLoader == null ? ViewPager$SavedState.class.getClassLoader() : classLoader;
        this.position = parcel.readInt();
        this.adapterState = parcel.readParcelable(classLoader);
    }
}

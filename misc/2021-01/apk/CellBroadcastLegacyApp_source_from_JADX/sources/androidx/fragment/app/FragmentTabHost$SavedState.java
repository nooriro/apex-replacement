package androidx.fragment.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

class FragmentTabHost$SavedState extends View.BaseSavedState {
    public static final Parcelable.Creator<FragmentTabHost$SavedState> CREATOR = new Parcelable.Creator<FragmentTabHost$SavedState>() {
        public FragmentTabHost$SavedState createFromParcel(Parcel parcel) {
            return new FragmentTabHost$SavedState(parcel);
        }

        public FragmentTabHost$SavedState[] newArray(int i) {
            return new FragmentTabHost$SavedState[i];
        }
    };
    String curTab;

    FragmentTabHost$SavedState(Parcel parcel) {
        super(parcel);
        this.curTab = parcel.readString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.curTab);
    }

    public String toString() {
        return "FragmentTabHost.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " curTab=" + this.curTab + "}";
    }
}

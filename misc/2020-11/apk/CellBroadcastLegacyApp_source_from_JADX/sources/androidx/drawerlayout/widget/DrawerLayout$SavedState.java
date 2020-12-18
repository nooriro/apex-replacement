package androidx.drawerlayout.widget;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.customview.view.AbsSavedState;

public class DrawerLayout$SavedState extends AbsSavedState {
    public static final Parcelable.Creator<DrawerLayout$SavedState> CREATOR = new Parcelable.ClassLoaderCreator<DrawerLayout$SavedState>() {
        public DrawerLayout$SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new DrawerLayout$SavedState(parcel, classLoader);
        }

        public DrawerLayout$SavedState createFromParcel(Parcel parcel) {
            return new DrawerLayout$SavedState(parcel, (ClassLoader) null);
        }

        public DrawerLayout$SavedState[] newArray(int i) {
            return new DrawerLayout$SavedState[i];
        }
    };
    int lockModeEnd;
    int lockModeLeft;
    int lockModeRight;
    int lockModeStart;
    int openDrawerGravity = 0;

    public DrawerLayout$SavedState(Parcel parcel, ClassLoader classLoader) {
        super(parcel, classLoader);
        this.openDrawerGravity = parcel.readInt();
        this.lockModeLeft = parcel.readInt();
        this.lockModeRight = parcel.readInt();
        this.lockModeStart = parcel.readInt();
        this.lockModeEnd = parcel.readInt();
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.openDrawerGravity);
        parcel.writeInt(this.lockModeLeft);
        parcel.writeInt(this.lockModeRight);
        parcel.writeInt(this.lockModeStart);
        parcel.writeInt(this.lockModeEnd);
    }
}

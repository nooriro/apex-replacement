package androidx.fragment.app;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.p002os.CancellationSignal;
import androidx.core.view.ViewCompat;
import androidx.fragment.R$id;
import androidx.fragment.app.SpecialEffectsController;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelStoreOwner;

class FragmentStateManager {
    private final FragmentLifecycleCallbacksDispatcher mDispatcher;
    private CancellationSignal mEnterAnimationCancellationSignal;
    private CancellationSignal mExitAnimationCancellationSignal;
    private final Fragment mFragment;
    private int mFragmentManagerState = -1;
    private final FragmentStore mFragmentStore;
    private CancellationSignal mHiddenAnimationCancellationSignal;
    private boolean mMovingToState = false;

    FragmentStateManager(FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher, FragmentStore fragmentStore, Fragment fragment) {
        this.mDispatcher = fragmentLifecycleCallbacksDispatcher;
        this.mFragmentStore = fragmentStore;
        this.mFragment = fragment;
    }

    FragmentStateManager(FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher, FragmentStore fragmentStore, ClassLoader classLoader, FragmentFactory fragmentFactory, FragmentState fragmentState) {
        this.mDispatcher = fragmentLifecycleCallbacksDispatcher;
        this.mFragmentStore = fragmentStore;
        this.mFragment = fragmentFactory.instantiate(classLoader, fragmentState.mClassName);
        Bundle bundle = fragmentState.mArguments;
        if (bundle != null) {
            bundle.setClassLoader(classLoader);
        }
        this.mFragment.setArguments(fragmentState.mArguments);
        Fragment fragment = this.mFragment;
        fragment.mWho = fragmentState.mWho;
        fragment.mFromLayout = fragmentState.mFromLayout;
        fragment.mRestored = true;
        fragment.mFragmentId = fragmentState.mFragmentId;
        fragment.mContainerId = fragmentState.mContainerId;
        fragment.mTag = fragmentState.mTag;
        fragment.mRetainInstance = fragmentState.mRetainInstance;
        fragment.mRemoving = fragmentState.mRemoving;
        fragment.mDetached = fragmentState.mDetached;
        fragment.mHidden = fragmentState.mHidden;
        fragment.mMaxState = Lifecycle.State.values()[fragmentState.mMaxLifecycleState];
        Bundle bundle2 = fragmentState.mSavedFragmentState;
        if (bundle2 != null) {
            this.mFragment.mSavedFragmentState = bundle2;
        } else {
            this.mFragment.mSavedFragmentState = new Bundle();
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            Log.v("FragmentManager", "Instantiated fragment " + this.mFragment);
        }
    }

    FragmentStateManager(FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher, FragmentStore fragmentStore, Fragment fragment, FragmentState fragmentState) {
        this.mDispatcher = fragmentLifecycleCallbacksDispatcher;
        this.mFragmentStore = fragmentStore;
        this.mFragment = fragment;
        fragment.mSavedViewState = null;
        fragment.mSavedViewRegistryState = null;
        fragment.mBackStackNesting = 0;
        fragment.mInLayout = false;
        fragment.mAdded = false;
        Fragment fragment2 = fragment.mTarget;
        fragment.mTargetWho = fragment2 != null ? fragment2.mWho : null;
        Fragment fragment3 = this.mFragment;
        fragment3.mTarget = null;
        Bundle bundle = fragmentState.mSavedFragmentState;
        if (bundle != null) {
            fragment3.mSavedFragmentState = bundle;
        } else {
            fragment3.mSavedFragmentState = new Bundle();
        }
    }

    /* access modifiers changed from: package-private */
    public Fragment getFragment() {
        return this.mFragment;
    }

    /* access modifiers changed from: package-private */
    public void setFragmentManagerState(int i) {
        this.mFragmentManagerState = i;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0037, code lost:
        r2 = r9.mFragment;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int computeExpectedState() {
        /*
            r9 = this;
            androidx.fragment.app.Fragment r0 = r9.mFragment
            androidx.fragment.app.FragmentManager r1 = r0.mFragmentManager
            if (r1 != 0) goto L_0x0009
            int r9 = r0.mState
            return r9
        L_0x0009:
            int r1 = r9.mFragmentManagerState
            boolean r2 = r0.mFromLayout
            r3 = 2
            r4 = 4
            r5 = 1
            if (r2 == 0) goto L_0x0028
            boolean r2 = r0.mInLayout
            if (r2 == 0) goto L_0x001b
            int r1 = java.lang.Math.max(r1, r3)
            goto L_0x0028
        L_0x001b:
            if (r1 >= r4) goto L_0x0024
            int r0 = r0.mState
            int r1 = java.lang.Math.min(r1, r0)
            goto L_0x0028
        L_0x0024:
            int r1 = java.lang.Math.min(r1, r5)
        L_0x0028:
            androidx.fragment.app.Fragment r0 = r9.mFragment
            boolean r0 = r0.mAdded
            if (r0 != 0) goto L_0x0032
            int r1 = java.lang.Math.min(r1, r5)
        L_0x0032:
            r0 = 0
            boolean r2 = androidx.fragment.app.FragmentManager.USE_STATE_MANAGER
            if (r2 == 0) goto L_0x0049
            androidx.fragment.app.Fragment r2 = r9.mFragment
            android.view.ViewGroup r6 = r2.mContainer
            if (r6 == 0) goto L_0x0049
            androidx.fragment.app.FragmentManager r0 = r2.getParentFragmentManager()
            androidx.fragment.app.SpecialEffectsController r0 = androidx.fragment.app.SpecialEffectsController.getOrCreateController((android.view.ViewGroup) r6, (androidx.fragment.app.FragmentManager) r0)
            androidx.fragment.app.SpecialEffectsController$Operation$LifecycleImpact r0 = r0.getAwaitingCompletionLifecycleImpact(r9)
        L_0x0049:
            androidx.fragment.app.SpecialEffectsController$Operation$LifecycleImpact r2 = androidx.fragment.app.SpecialEffectsController.Operation.LifecycleImpact.ADDING
            r6 = -1
            r7 = 3
            if (r0 != r2) goto L_0x0055
            r0 = 6
            int r1 = java.lang.Math.min(r1, r0)
            goto L_0x0073
        L_0x0055:
            androidx.fragment.app.SpecialEffectsController$Operation$LifecycleImpact r2 = androidx.fragment.app.SpecialEffectsController.Operation.LifecycleImpact.REMOVING
            if (r0 != r2) goto L_0x005e
            int r1 = java.lang.Math.max(r1, r7)
            goto L_0x0073
        L_0x005e:
            androidx.fragment.app.Fragment r0 = r9.mFragment
            boolean r2 = r0.mRemoving
            if (r2 == 0) goto L_0x0073
            boolean r0 = r0.isInBackStack()
            if (r0 == 0) goto L_0x006f
            int r1 = java.lang.Math.min(r1, r5)
            goto L_0x0073
        L_0x006f:
            int r1 = java.lang.Math.min(r1, r6)
        L_0x0073:
            androidx.fragment.app.Fragment r0 = r9.mFragment
            boolean r2 = r0.mDeferStart
            r8 = 5
            if (r2 == 0) goto L_0x0082
            int r0 = r0.mState
            if (r0 >= r8) goto L_0x0082
            int r1 = java.lang.Math.min(r1, r4)
        L_0x0082:
            int[] r0 = androidx.fragment.app.FragmentStateManager.C01752.$SwitchMap$androidx$lifecycle$Lifecycle$State
            androidx.fragment.app.Fragment r9 = r9.mFragment
            androidx.lifecycle.Lifecycle$State r9 = r9.mMaxState
            int r9 = r9.ordinal()
            r9 = r0[r9]
            if (r9 == r5) goto L_0x00a2
            if (r9 == r3) goto L_0x009e
            if (r9 == r7) goto L_0x0099
            int r1 = java.lang.Math.min(r1, r6)
            goto L_0x00a2
        L_0x0099:
            int r1 = java.lang.Math.min(r1, r5)
            goto L_0x00a2
        L_0x009e:
            int r1 = java.lang.Math.min(r1, r8)
        L_0x00a2:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.FragmentStateManager.computeExpectedState():int");
    }

    /* renamed from: androidx.fragment.app.FragmentStateManager$2 */
    static /* synthetic */ class C01752 {
        static final /* synthetic */ int[] $SwitchMap$androidx$lifecycle$Lifecycle$State;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                androidx.lifecycle.Lifecycle$State[] r0 = androidx.lifecycle.Lifecycle.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$androidx$lifecycle$Lifecycle$State = r0
                androidx.lifecycle.Lifecycle$State r1 = androidx.lifecycle.Lifecycle.State.RESUMED     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$androidx$lifecycle$Lifecycle$State     // Catch:{ NoSuchFieldError -> 0x001d }
                androidx.lifecycle.Lifecycle$State r1 = androidx.lifecycle.Lifecycle.State.STARTED     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$androidx$lifecycle$Lifecycle$State     // Catch:{ NoSuchFieldError -> 0x0028 }
                androidx.lifecycle.Lifecycle$State r1 = androidx.lifecycle.Lifecycle.State.CREATED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.FragmentStateManager.C01752.<clinit>():void");
        }
    }

    /* access modifiers changed from: package-private */
    public void moveToExpectedState() {
        if (!this.mMovingToState) {
            boolean z = false;
            z = true;
            try {
                while (true) {
                    int computeExpectedState = computeExpectedState();
                    if (computeExpectedState != this.mFragment.mState) {
                        if (computeExpectedState <= this.mFragment.mState) {
                            int i = this.mFragment.mState - (z ? 1 : 0);
                            if (this.mEnterAnimationCancellationSignal != null) {
                                this.mEnterAnimationCancellationSignal.cancel();
                            }
                            switch (i) {
                                case -1:
                                    detach();
                                    break;
                                case 0:
                                    destroy();
                                    break;
                                case 1:
                                    this.mFragment.mState = z;
                                    break;
                                case 2:
                                    destroyFragmentView();
                                    this.mFragment.mState = 2;
                                    break;
                                case 3:
                                    if (FragmentManager.isLoggingEnabled(3)) {
                                        Log.d("FragmentManager", "movefrom ACTIVITY_CREATED: " + this.mFragment);
                                    }
                                    if (this.mFragment.mView != null && this.mFragment.mSavedViewState == null) {
                                        saveViewState();
                                    }
                                    if (!(this.mFragment.mView == null || this.mFragment.mContainer == null || this.mFragmentManagerState <= -1)) {
                                        SpecialEffectsController orCreateController = SpecialEffectsController.getOrCreateController(this.mFragment.mContainer, this.mFragment.getParentFragmentManager());
                                        if (this.mHiddenAnimationCancellationSignal != null) {
                                            this.mHiddenAnimationCancellationSignal.cancel();
                                        }
                                        CancellationSignal cancellationSignal = new CancellationSignal();
                                        this.mExitAnimationCancellationSignal = cancellationSignal;
                                        orCreateController.enqueueRemove(this, cancellationSignal);
                                    }
                                    this.mFragment.mState = 3;
                                    break;
                                case 4:
                                    stop();
                                    break;
                                case 5:
                                    this.mFragment.mState = 5;
                                    break;
                                case 6:
                                    pause();
                                    break;
                            }
                        } else {
                            int i2 = this.mFragment.mState + z;
                            if (this.mExitAnimationCancellationSignal != null) {
                                this.mExitAnimationCancellationSignal.cancel();
                            }
                            switch (i2) {
                                case 0:
                                    attach();
                                    break;
                                case 1:
                                    create();
                                    break;
                                case 2:
                                    ensureInflatedView();
                                    createView();
                                    break;
                                case 3:
                                    activityCreated();
                                    break;
                                case 4:
                                    if (!(this.mFragment.mView == null || this.mFragment.mContainer == null)) {
                                        if (this.mFragment.mView.getParent() == null) {
                                            this.mFragment.mContainer.addView(this.mFragment.mView, this.mFragmentStore.findFragmentIndexInContainer(this.mFragment));
                                        }
                                        SpecialEffectsController orCreateController2 = SpecialEffectsController.getOrCreateController(this.mFragment.mContainer, this.mFragment.getParentFragmentManager());
                                        if (this.mHiddenAnimationCancellationSignal != null) {
                                            this.mHiddenAnimationCancellationSignal.cancel();
                                        }
                                        this.mEnterAnimationCancellationSignal = new CancellationSignal();
                                        orCreateController2.enqueueAdd(SpecialEffectsController.Operation.State.from(this.mFragment.getPostOnViewCreatedVisibility()), this, this.mEnterAnimationCancellationSignal);
                                    }
                                    this.mFragment.mState = 4;
                                    break;
                                case 5:
                                    start();
                                    break;
                                case 6:
                                    this.mFragment.mState = 6;
                                    break;
                                case 7:
                                    resume();
                                    break;
                            }
                        }
                    } else {
                        if (FragmentManager.USE_STATE_MANAGER && this.mFragment.mHiddenChanged) {
                            if (!(this.mFragment.mView == null || this.mFragment.mContainer == null)) {
                                if (this.mHiddenAnimationCancellationSignal != null) {
                                    this.mHiddenAnimationCancellationSignal.cancel();
                                }
                                SpecialEffectsController orCreateController3 = SpecialEffectsController.getOrCreateController(this.mFragment.mContainer, this.mFragment.getParentFragmentManager());
                                CancellationSignal cancellationSignal2 = new CancellationSignal();
                                this.mHiddenAnimationCancellationSignal = cancellationSignal2;
                                if (this.mFragment.mHidden) {
                                    orCreateController3.enqueueHide(this, cancellationSignal2);
                                } else {
                                    orCreateController3.enqueueShow(this, cancellationSignal2);
                                }
                            }
                            this.mFragment.mHiddenChanged = z;
                            this.mFragment.onHiddenChanged(this.mFragment.mHidden);
                        }
                        this.mMovingToState = z;
                        return;
                    }
                }
            } finally {
                this.mMovingToState = z;
            }
        } else if (FragmentManager.isLoggingEnabled(2)) {
            Log.v("FragmentManager", "Ignoring re-entrant call to moveToExpectedState() for " + getFragment());
        }
    }

    /* access modifiers changed from: package-private */
    public void ensureInflatedView() {
        Fragment fragment = this.mFragment;
        if (fragment.mFromLayout && fragment.mInLayout && !fragment.mPerformedCreateView) {
            if (FragmentManager.isLoggingEnabled(3)) {
                Log.d("FragmentManager", "moveto CREATE_VIEW: " + this.mFragment);
            }
            Fragment fragment2 = this.mFragment;
            fragment2.performCreateView(fragment2.performGetLayoutInflater(fragment2.mSavedFragmentState), (ViewGroup) null, this.mFragment.mSavedFragmentState);
            View view = this.mFragment.mView;
            if (view != null) {
                view.setSaveFromParentEnabled(false);
                Fragment fragment3 = this.mFragment;
                fragment3.mView.setTag(R$id.fragment_container_view_tag, fragment3);
                Fragment fragment4 = this.mFragment;
                if (fragment4.mHidden) {
                    fragment4.mView.setVisibility(8);
                }
                this.mFragment.performViewCreated();
                FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
                Fragment fragment5 = this.mFragment;
                fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentViewCreated(fragment5, fragment5.mView, fragment5.mSavedFragmentState, false);
                this.mFragment.mState = 2;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void restoreState(ClassLoader classLoader) {
        Bundle bundle = this.mFragment.mSavedFragmentState;
        if (bundle != null) {
            bundle.setClassLoader(classLoader);
            Fragment fragment = this.mFragment;
            fragment.mSavedViewState = fragment.mSavedFragmentState.getSparseParcelableArray("android:view_state");
            Fragment fragment2 = this.mFragment;
            fragment2.mSavedViewRegistryState = fragment2.mSavedFragmentState.getBundle("android:view_registry_state");
            Fragment fragment3 = this.mFragment;
            fragment3.mTargetWho = fragment3.mSavedFragmentState.getString("android:target_state");
            Fragment fragment4 = this.mFragment;
            if (fragment4.mTargetWho != null) {
                fragment4.mTargetRequestCode = fragment4.mSavedFragmentState.getInt("android:target_req_state", 0);
            }
            Fragment fragment5 = this.mFragment;
            Boolean bool = fragment5.mSavedUserVisibleHint;
            if (bool != null) {
                fragment5.mUserVisibleHint = bool.booleanValue();
                this.mFragment.mSavedUserVisibleHint = null;
            } else {
                fragment5.mUserVisibleHint = fragment5.mSavedFragmentState.getBoolean("android:user_visible_hint", true);
            }
            Fragment fragment6 = this.mFragment;
            if (!fragment6.mUserVisibleHint) {
                fragment6.mDeferStart = true;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void attach() {
        if (FragmentManager.isLoggingEnabled(3)) {
            Log.d("FragmentManager", "moveto ATTACHED: " + this.mFragment);
        }
        Fragment fragment = this.mFragment;
        Fragment fragment2 = fragment.mTarget;
        FragmentStateManager fragmentStateManager = null;
        if (fragment2 != null) {
            FragmentStateManager fragmentStateManager2 = this.mFragmentStore.getFragmentStateManager(fragment2.mWho);
            if (fragmentStateManager2 != null) {
                Fragment fragment3 = this.mFragment;
                fragment3.mTargetWho = fragment3.mTarget.mWho;
                fragment3.mTarget = null;
                fragmentStateManager = fragmentStateManager2;
            } else {
                throw new IllegalStateException("Fragment " + this.mFragment + " declared target fragment " + this.mFragment.mTarget + " that does not belong to this FragmentManager!");
            }
        } else {
            String str = fragment.mTargetWho;
            if (str != null && (fragmentStateManager = this.mFragmentStore.getFragmentStateManager(str)) == null) {
                throw new IllegalStateException("Fragment " + this.mFragment + " declared target fragment " + this.mFragment.mTargetWho + " that does not belong to this FragmentManager!");
            }
        }
        if (fragmentStateManager != null && (FragmentManager.USE_STATE_MANAGER || fragmentStateManager.getFragment().mState < 1)) {
            fragmentStateManager.moveToExpectedState();
        }
        Fragment fragment4 = this.mFragment;
        fragment4.mHost = fragment4.mFragmentManager.getHost();
        Fragment fragment5 = this.mFragment;
        fragment5.mParentFragment = fragment5.mFragmentManager.getParent();
        this.mDispatcher.dispatchOnFragmentPreAttached(this.mFragment, false);
        this.mFragment.performAttach();
        this.mDispatcher.dispatchOnFragmentAttached(this.mFragment, false);
    }

    /* access modifiers changed from: package-private */
    public void create() {
        if (FragmentManager.isLoggingEnabled(3)) {
            Log.d("FragmentManager", "moveto CREATED: " + this.mFragment);
        }
        Fragment fragment = this.mFragment;
        if (!fragment.mIsCreated) {
            this.mDispatcher.dispatchOnFragmentPreCreated(fragment, fragment.mSavedFragmentState, false);
            Fragment fragment2 = this.mFragment;
            fragment2.performCreate(fragment2.mSavedFragmentState);
            FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
            Fragment fragment3 = this.mFragment;
            fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentCreated(fragment3, fragment3.mSavedFragmentState, false);
            return;
        }
        fragment.restoreChildFragmentState(fragment.mSavedFragmentState);
        this.mFragment.mState = 1;
    }

    /* access modifiers changed from: package-private */
    public void createView() {
        String str;
        if (!this.mFragment.mFromLayout) {
            if (FragmentManager.isLoggingEnabled(3)) {
                Log.d("FragmentManager", "moveto CREATE_VIEW: " + this.mFragment);
            }
            Fragment fragment = this.mFragment;
            LayoutInflater performGetLayoutInflater = fragment.performGetLayoutInflater(fragment.mSavedFragmentState);
            ViewGroup viewGroup = null;
            Fragment fragment2 = this.mFragment;
            ViewGroup viewGroup2 = fragment2.mContainer;
            if (viewGroup2 != null) {
                viewGroup = viewGroup2;
            } else {
                int i = fragment2.mContainerId;
                if (i != 0) {
                    if (i != -1) {
                        viewGroup = (ViewGroup) fragment2.mFragmentManager.getContainer().onFindViewById(this.mFragment.mContainerId);
                        if (viewGroup == null) {
                            Fragment fragment3 = this.mFragment;
                            if (!fragment3.mRestored) {
                                try {
                                    str = fragment3.getResources().getResourceName(this.mFragment.mContainerId);
                                } catch (Resources.NotFoundException unused) {
                                    str = "unknown";
                                }
                                throw new IllegalArgumentException("No view found for id 0x" + Integer.toHexString(this.mFragment.mContainerId) + " (" + str + ") for fragment " + this.mFragment);
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Cannot create fragment " + this.mFragment + " for a container view with no id");
                    }
                }
            }
            Fragment fragment4 = this.mFragment;
            fragment4.mContainer = viewGroup;
            fragment4.performCreateView(performGetLayoutInflater, viewGroup, fragment4.mSavedFragmentState);
            View view = this.mFragment.mView;
            if (view != null) {
                boolean z = false;
                view.setSaveFromParentEnabled(false);
                Fragment fragment5 = this.mFragment;
                fragment5.mView.setTag(R$id.fragment_container_view_tag, fragment5);
                if (viewGroup != null) {
                    viewGroup.addView(this.mFragment.mView, this.mFragmentStore.findFragmentIndexInContainer(this.mFragment));
                }
                Fragment fragment6 = this.mFragment;
                if (fragment6.mHidden) {
                    fragment6.mView.setVisibility(8);
                }
                if (ViewCompat.isAttachedToWindow(this.mFragment.mView)) {
                    ViewCompat.requestApplyInsets(this.mFragment.mView);
                } else {
                    final View view2 = this.mFragment.mView;
                    view2.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(this) {
                        public void onViewDetachedFromWindow(View view) {
                        }

                        public void onViewAttachedToWindow(View view) {
                            view2.removeOnAttachStateChangeListener(this);
                            ViewCompat.requestApplyInsets(view2);
                        }
                    });
                }
                this.mFragment.performViewCreated();
                FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
                Fragment fragment7 = this.mFragment;
                fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentViewCreated(fragment7, fragment7.mView, fragment7.mSavedFragmentState, false);
                int visibility = this.mFragment.mView.getVisibility();
                if (FragmentManager.USE_STATE_MANAGER) {
                    this.mFragment.setPostOnViewCreatedVisibility(visibility);
                    Fragment fragment8 = this.mFragment;
                    if (fragment8.mContainer != null && visibility == 0) {
                        fragment8.setFocusedView(fragment8.mView.findFocus());
                        this.mFragment.mView.setVisibility(4);
                    }
                } else {
                    Fragment fragment9 = this.mFragment;
                    if (visibility == 0 && fragment9.mContainer != null) {
                        z = true;
                    }
                    fragment9.mIsNewlyAdded = z;
                }
            }
            this.mFragment.mState = 2;
        }
    }

    /* access modifiers changed from: package-private */
    public void activityCreated() {
        if (FragmentManager.isLoggingEnabled(3)) {
            Log.d("FragmentManager", "moveto ACTIVITY_CREATED: " + this.mFragment);
        }
        Fragment fragment = this.mFragment;
        fragment.performActivityCreated(fragment.mSavedFragmentState);
        FragmentLifecycleCallbacksDispatcher fragmentLifecycleCallbacksDispatcher = this.mDispatcher;
        Fragment fragment2 = this.mFragment;
        fragmentLifecycleCallbacksDispatcher.dispatchOnFragmentActivityCreated(fragment2, fragment2.mSavedFragmentState, false);
    }

    /* access modifiers changed from: package-private */
    public void start() {
        if (FragmentManager.isLoggingEnabled(3)) {
            Log.d("FragmentManager", "moveto STARTED: " + this.mFragment);
        }
        this.mFragment.performStart();
        this.mDispatcher.dispatchOnFragmentStarted(this.mFragment, false);
    }

    /* access modifiers changed from: package-private */
    public void resume() {
        if (FragmentManager.isLoggingEnabled(3)) {
            Log.d("FragmentManager", "moveto RESUMED: " + this.mFragment);
        }
        this.mFragment.performResume();
        this.mDispatcher.dispatchOnFragmentResumed(this.mFragment, false);
        Fragment fragment = this.mFragment;
        fragment.mSavedFragmentState = null;
        fragment.mSavedViewState = null;
        fragment.mSavedViewRegistryState = null;
    }

    /* access modifiers changed from: package-private */
    public void pause() {
        if (FragmentManager.isLoggingEnabled(3)) {
            Log.d("FragmentManager", "movefrom RESUMED: " + this.mFragment);
        }
        this.mFragment.performPause();
        this.mDispatcher.dispatchOnFragmentPaused(this.mFragment, false);
    }

    /* access modifiers changed from: package-private */
    public void stop() {
        if (FragmentManager.isLoggingEnabled(3)) {
            Log.d("FragmentManager", "movefrom STARTED: " + this.mFragment);
        }
        this.mFragment.performStop();
        this.mDispatcher.dispatchOnFragmentStopped(this.mFragment, false);
    }

    /* access modifiers changed from: package-private */
    public FragmentState saveState() {
        FragmentState fragmentState = new FragmentState(this.mFragment);
        if (this.mFragment.mState <= -1 || fragmentState.mSavedFragmentState != null) {
            fragmentState.mSavedFragmentState = this.mFragment.mSavedFragmentState;
        } else {
            Bundle saveBasicState = saveBasicState();
            fragmentState.mSavedFragmentState = saveBasicState;
            if (this.mFragment.mTargetWho != null) {
                if (saveBasicState == null) {
                    fragmentState.mSavedFragmentState = new Bundle();
                }
                fragmentState.mSavedFragmentState.putString("android:target_state", this.mFragment.mTargetWho);
                int i = this.mFragment.mTargetRequestCode;
                if (i != 0) {
                    fragmentState.mSavedFragmentState.putInt("android:target_req_state", i);
                }
            }
        }
        return fragmentState;
    }

    private Bundle saveBasicState() {
        Bundle bundle = new Bundle();
        this.mFragment.performSaveInstanceState(bundle);
        this.mDispatcher.dispatchOnFragmentSaveInstanceState(this.mFragment, bundle, false);
        if (bundle.isEmpty()) {
            bundle = null;
        }
        if (this.mFragment.mView != null) {
            saveViewState();
        }
        if (this.mFragment.mSavedViewState != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putSparseParcelableArray("android:view_state", this.mFragment.mSavedViewState);
        }
        if (this.mFragment.mSavedViewRegistryState != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBundle("android:view_registry_state", this.mFragment.mSavedViewRegistryState);
        }
        if (!this.mFragment.mUserVisibleHint) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBoolean("android:user_visible_hint", this.mFragment.mUserVisibleHint);
        }
        return bundle;
    }

    /* access modifiers changed from: package-private */
    public void saveViewState() {
        if (this.mFragment.mView != null) {
            SparseArray<Parcelable> sparseArray = new SparseArray<>();
            this.mFragment.mView.saveHierarchyState(sparseArray);
            if (sparseArray.size() > 0) {
                this.mFragment.mSavedViewState = sparseArray;
            }
            Bundle bundle = new Bundle();
            this.mFragment.mViewLifecycleOwner.performSave(bundle);
            if (!bundle.isEmpty()) {
                this.mFragment.mSavedViewRegistryState = bundle;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void destroyFragmentView() {
        this.mFragment.performDestroyView();
        this.mDispatcher.dispatchOnFragmentViewDestroyed(this.mFragment, false);
        Fragment fragment = this.mFragment;
        fragment.mContainer = null;
        fragment.mView = null;
        fragment.mViewLifecycleOwner = null;
        fragment.mViewLifecycleOwnerLiveData.setValue(null);
        this.mFragment.mInLayout = false;
    }

    /* access modifiers changed from: package-private */
    public void destroy() {
        Fragment findActiveFragment;
        if (FragmentManager.isLoggingEnabled(3)) {
            Log.d("FragmentManager", "movefrom CREATED: " + this.mFragment);
        }
        Fragment fragment = this.mFragment;
        boolean z = true;
        boolean z2 = fragment.mRemoving && !fragment.isInBackStack();
        if (z2 || this.mFragmentStore.getNonConfig().shouldDestroy(this.mFragment)) {
            FragmentHostCallback<?> fragmentHostCallback = this.mFragment.mHost;
            if (fragmentHostCallback instanceof ViewModelStoreOwner) {
                z = this.mFragmentStore.getNonConfig().isCleared();
            } else if (fragmentHostCallback.getContext() instanceof Activity) {
                z = true ^ ((Activity) fragmentHostCallback.getContext()).isChangingConfigurations();
            }
            if (z2 || z) {
                this.mFragmentStore.getNonConfig().clearNonConfigState(this.mFragment);
            }
            this.mFragment.performDestroy();
            this.mDispatcher.dispatchOnFragmentDestroyed(this.mFragment, false);
            for (FragmentStateManager next : this.mFragmentStore.getActiveFragmentStateManagers()) {
                if (next != null) {
                    Fragment fragment2 = next.getFragment();
                    if (this.mFragment.mWho.equals(fragment2.mTargetWho)) {
                        fragment2.mTarget = this.mFragment;
                        fragment2.mTargetWho = null;
                    }
                }
            }
            Fragment fragment3 = this.mFragment;
            String str = fragment3.mTargetWho;
            if (str != null) {
                fragment3.mTarget = this.mFragmentStore.findActiveFragment(str);
            }
            this.mFragmentStore.makeInactive(this);
            return;
        }
        String str2 = this.mFragment.mTargetWho;
        if (!(str2 == null || (findActiveFragment = this.mFragmentStore.findActiveFragment(str2)) == null || !findActiveFragment.mRetainInstance)) {
            this.mFragment.mTarget = findActiveFragment;
        }
        this.mFragment.mState = 0;
    }

    /* access modifiers changed from: package-private */
    public void detach() {
        if (FragmentManager.isLoggingEnabled(3)) {
            Log.d("FragmentManager", "movefrom ATTACHED: " + this.mFragment);
        }
        this.mFragment.performDetach();
        boolean z = false;
        this.mDispatcher.dispatchOnFragmentDetached(this.mFragment, false);
        Fragment fragment = this.mFragment;
        fragment.mState = -1;
        fragment.mHost = null;
        fragment.mParentFragment = null;
        fragment.mFragmentManager = null;
        if (fragment.mRemoving && !fragment.isInBackStack()) {
            z = true;
        }
        if (z || this.mFragmentStore.getNonConfig().shouldDestroy(this.mFragment)) {
            if (FragmentManager.isLoggingEnabled(3)) {
                Log.d("FragmentManager", "initState called for fragment: " + this.mFragment);
            }
            this.mFragment.initState();
        }
    }
}

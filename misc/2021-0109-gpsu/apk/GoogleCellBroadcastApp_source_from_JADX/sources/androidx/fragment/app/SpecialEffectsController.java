package androidx.fragment.app;

import android.view.View;
import android.view.ViewGroup;
import androidx.core.p002os.CancellationSignal;
import androidx.fragment.R$id;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract class SpecialEffectsController {
    final HashMap<Fragment, Operation> mAwaitingCompletionOperations = new HashMap<>();
    private final ViewGroup mContainer;
    boolean mIsContainerPostponed = false;
    boolean mOperationDirectionIsPop = false;
    final ArrayList<Operation> mPendingOperations = new ArrayList<>();

    /* access modifiers changed from: package-private */
    public abstract void executeOperations(List<Operation> list, boolean z);

    static SpecialEffectsController getOrCreateController(ViewGroup viewGroup, FragmentManager fragmentManager) {
        return getOrCreateController(viewGroup, fragmentManager.getSpecialEffectsControllerFactory());
    }

    static SpecialEffectsController getOrCreateController(ViewGroup viewGroup, SpecialEffectsControllerFactory specialEffectsControllerFactory) {
        Object tag = viewGroup.getTag(R$id.special_effects_controller_view_tag);
        if (tag instanceof SpecialEffectsController) {
            return (SpecialEffectsController) tag;
        }
        SpecialEffectsController createController = specialEffectsControllerFactory.createController(viewGroup);
        viewGroup.setTag(R$id.special_effects_controller_view_tag, createController);
        return createController;
    }

    SpecialEffectsController(ViewGroup viewGroup) {
        this.mContainer = viewGroup;
    }

    public ViewGroup getContainer() {
        return this.mContainer;
    }

    /* access modifiers changed from: package-private */
    public Operation.LifecycleImpact getAwaitingCompletionLifecycleImpact(FragmentStateManager fragmentStateManager) {
        Operation operation = this.mAwaitingCompletionOperations.get(fragmentStateManager.getFragment());
        if (operation == null || operation.getCancellationSignal().isCanceled()) {
            return null;
        }
        return operation.getLifecycleImpact();
    }

    /* access modifiers changed from: package-private */
    public void enqueueAdd(Operation.State state, FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        enqueue(state, Operation.LifecycleImpact.ADDING, fragmentStateManager, cancellationSignal);
    }

    /* access modifiers changed from: package-private */
    public void enqueueShow(FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        enqueue(Operation.State.VISIBLE, Operation.LifecycleImpact.NONE, fragmentStateManager, cancellationSignal);
    }

    /* access modifiers changed from: package-private */
    public void enqueueHide(FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        enqueue(Operation.State.GONE, Operation.LifecycleImpact.NONE, fragmentStateManager, cancellationSignal);
    }

    /* access modifiers changed from: package-private */
    public void enqueueRemove(FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        enqueue(Operation.State.REMOVED, Operation.LifecycleImpact.REMOVING, fragmentStateManager, cancellationSignal);
    }

    private void enqueue(Operation.State state, Operation.LifecycleImpact lifecycleImpact, FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        if (!cancellationSignal.isCanceled()) {
            synchronized (this.mPendingOperations) {
                final CancellationSignal cancellationSignal2 = new CancellationSignal();
                Operation operation = this.mAwaitingCompletionOperations.get(fragmentStateManager.getFragment());
                if (operation != null) {
                    operation.mergeWith(state, lifecycleImpact, cancellationSignal);
                    return;
                }
                final FragmentStateManagerOperation fragmentStateManagerOperation = new FragmentStateManagerOperation(state, lifecycleImpact, fragmentStateManager, cancellationSignal2);
                this.mPendingOperations.add(fragmentStateManagerOperation);
                this.mAwaitingCompletionOperations.put(fragmentStateManagerOperation.getFragment(), fragmentStateManagerOperation);
                cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
                    public void onCancel() {
                        synchronized (SpecialEffectsController.this.mPendingOperations) {
                            SpecialEffectsController.this.mPendingOperations.remove(fragmentStateManagerOperation);
                            SpecialEffectsController.this.mAwaitingCompletionOperations.remove(fragmentStateManagerOperation.getFragment());
                            cancellationSignal2.cancel();
                        }
                    }
                });
                fragmentStateManagerOperation.addCompletionListener(new Runnable() {
                    public void run() {
                        if (!fragmentStateManagerOperation.getCancellationSignal().isCanceled()) {
                            SpecialEffectsController.this.mAwaitingCompletionOperations.remove(fragmentStateManagerOperation.getFragment());
                        }
                    }
                });
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateOperationDirection(boolean z) {
        this.mOperationDirectionIsPop = z;
    }

    /* access modifiers changed from: package-private */
    public void markPostponedState() {
        synchronized (this.mPendingOperations) {
            this.mIsContainerPostponed = false;
            int size = this.mPendingOperations.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                Operation operation = this.mPendingOperations.get(size);
                Operation.State from = Operation.State.from(operation.getFragment().mView);
                if (operation.getFinalState() == Operation.State.VISIBLE && from != Operation.State.VISIBLE) {
                    this.mIsContainerPostponed = operation.getFragment().isPostponed();
                    break;
                }
                size--;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void forcePostponedExecutePendingOperations() {
        if (this.mIsContainerPostponed) {
            this.mIsContainerPostponed = false;
            executePendingOperations();
        }
    }

    /* access modifiers changed from: package-private */
    public void executePendingOperations() {
        if (!this.mIsContainerPostponed) {
            synchronized (this.mPendingOperations) {
                if (!this.mPendingOperations.isEmpty()) {
                    executeOperations(new ArrayList(this.mPendingOperations), this.mOperationDirectionIsPop);
                    this.mPendingOperations.clear();
                    this.mOperationDirectionIsPop = false;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void forceCompleteAllOperations() {
        synchronized (this.mPendingOperations) {
            for (Operation next : this.mAwaitingCompletionOperations.values()) {
                next.getCancellationSignal().cancel();
                next.getFinalState().applyState(next.getFragment().mView);
                next.complete();
            }
            this.mAwaitingCompletionOperations.clear();
            this.mPendingOperations.clear();
        }
    }

    static class Operation {
        final CancellationSignal mCancellationSignal = new CancellationSignal();
        private final List<Runnable> mCompletionListeners = new ArrayList();
        private State mFinalState;
        private final Fragment mFragment;
        private LifecycleImpact mLifecycleImpact;

        enum LifecycleImpact {
            NONE,
            ADDING,
            REMOVING
        }

        enum State {
            REMOVED,
            VISIBLE,
            GONE,
            INVISIBLE;

            static State from(View view) {
                return from(view.getVisibility());
            }

            static State from(int i) {
                if (i == 0) {
                    return VISIBLE;
                }
                if (i == 4) {
                    return INVISIBLE;
                }
                if (i == 8) {
                    return GONE;
                }
                throw new IllegalArgumentException("Unknown visibility " + i);
            }

            /* access modifiers changed from: package-private */
            public void applyState(View view) {
                int i = C01943.f6xe493b431[ordinal()];
                if (i == 1) {
                    ViewGroup viewGroup = (ViewGroup) view.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(view);
                    }
                } else if (i == 2) {
                    view.setVisibility(0);
                } else if (i == 3) {
                    view.setVisibility(8);
                } else if (i == 4) {
                    view.setVisibility(4);
                }
            }
        }

        Operation(State state, LifecycleImpact lifecycleImpact, Fragment fragment, CancellationSignal cancellationSignal) {
            this.mFinalState = state;
            this.mLifecycleImpact = lifecycleImpact;
            this.mFragment = fragment;
            cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
                public void onCancel() {
                    Operation.this.mCancellationSignal.cancel();
                }
            });
        }

        public State getFinalState() {
            return this.mFinalState;
        }

        /* access modifiers changed from: package-private */
        public LifecycleImpact getLifecycleImpact() {
            return this.mLifecycleImpact;
        }

        public final Fragment getFragment() {
            return this.mFragment;
        }

        public final CancellationSignal getCancellationSignal() {
            return this.mCancellationSignal;
        }

        /* access modifiers changed from: package-private */
        public final void mergeWith(State state, LifecycleImpact lifecycleImpact, CancellationSignal cancellationSignal) {
            int i = C01943.f5xb9e640f0[lifecycleImpact.ordinal()];
            if (i != 1) {
                if (i == 2) {
                    this.mFinalState = State.REMOVED;
                    this.mLifecycleImpact = LifecycleImpact.REMOVING;
                } else if (i == 3 && this.mFinalState != State.REMOVED) {
                    this.mFinalState = state;
                }
            } else if (this.mFinalState == State.REMOVED) {
                this.mFinalState = State.VISIBLE;
                this.mLifecycleImpact = LifecycleImpact.ADDING;
            }
            cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
                public void onCancel() {
                    Operation.this.mCancellationSignal.cancel();
                }
            });
        }

        /* access modifiers changed from: package-private */
        public final void addCompletionListener(Runnable runnable) {
            this.mCompletionListeners.add(runnable);
        }

        public void complete() {
            for (Runnable run : this.mCompletionListeners) {
                run.run();
            }
        }
    }

    /* renamed from: androidx.fragment.app.SpecialEffectsController$3 */
    static /* synthetic */ class C01943 {

        /* renamed from: $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$LifecycleImpact */
        static final /* synthetic */ int[] f5xb9e640f0;

        /* renamed from: $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State */
        static final /* synthetic */ int[] f6xe493b431;

        /* JADX WARNING: Can't wrap try/catch for region: R(17:0|(2:1|2)|3|(2:5|6)|7|9|10|11|13|14|15|16|17|18|19|20|22) */
        /* JADX WARNING: Can't wrap try/catch for region: R(19:0|1|2|3|5|6|7|9|10|11|13|14|15|16|17|18|19|20|22) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0039 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0043 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x004d */
        static {
            /*
                androidx.fragment.app.SpecialEffectsController$Operation$LifecycleImpact[] r0 = androidx.fragment.app.SpecialEffectsController.Operation.LifecycleImpact.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f5xb9e640f0 = r0
                r1 = 1
                androidx.fragment.app.SpecialEffectsController$Operation$LifecycleImpact r2 = androidx.fragment.app.SpecialEffectsController.Operation.LifecycleImpact.ADDING     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r0[r2] = r1     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                r0 = 2
                int[] r2 = f5xb9e640f0     // Catch:{ NoSuchFieldError -> 0x001d }
                androidx.fragment.app.SpecialEffectsController$Operation$LifecycleImpact r3 = androidx.fragment.app.SpecialEffectsController.Operation.LifecycleImpact.REMOVING     // Catch:{ NoSuchFieldError -> 0x001d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                r2 = 3
                int[] r3 = f5xb9e640f0     // Catch:{ NoSuchFieldError -> 0x0028 }
                androidx.fragment.app.SpecialEffectsController$Operation$LifecycleImpact r4 = androidx.fragment.app.SpecialEffectsController.Operation.LifecycleImpact.NONE     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                androidx.fragment.app.SpecialEffectsController$Operation$State[] r3 = androidx.fragment.app.SpecialEffectsController.Operation.State.values()
                int r3 = r3.length
                int[] r3 = new int[r3]
                f6xe493b431 = r3
                androidx.fragment.app.SpecialEffectsController$Operation$State r4 = androidx.fragment.app.SpecialEffectsController.Operation.State.REMOVED     // Catch:{ NoSuchFieldError -> 0x0039 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0039 }
                r3[r4] = r1     // Catch:{ NoSuchFieldError -> 0x0039 }
            L_0x0039:
                int[] r1 = f6xe493b431     // Catch:{ NoSuchFieldError -> 0x0043 }
                androidx.fragment.app.SpecialEffectsController$Operation$State r3 = androidx.fragment.app.SpecialEffectsController.Operation.State.VISIBLE     // Catch:{ NoSuchFieldError -> 0x0043 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x0043 }
                r1[r3] = r0     // Catch:{ NoSuchFieldError -> 0x0043 }
            L_0x0043:
                int[] r0 = f6xe493b431     // Catch:{ NoSuchFieldError -> 0x004d }
                androidx.fragment.app.SpecialEffectsController$Operation$State r1 = androidx.fragment.app.SpecialEffectsController.Operation.State.GONE     // Catch:{ NoSuchFieldError -> 0x004d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004d }
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004d }
            L_0x004d:
                int[] r0 = f6xe493b431     // Catch:{ NoSuchFieldError -> 0x0058 }
                androidx.fragment.app.SpecialEffectsController$Operation$State r1 = androidx.fragment.app.SpecialEffectsController.Operation.State.INVISIBLE     // Catch:{ NoSuchFieldError -> 0x0058 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0058 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0058 }
            L_0x0058:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.SpecialEffectsController.C01943.<clinit>():void");
        }
    }

    private static class FragmentStateManagerOperation extends Operation {
        private final FragmentStateManager mFragmentStateManager;

        FragmentStateManagerOperation(Operation.State state, Operation.LifecycleImpact lifecycleImpact, FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
            super(state, lifecycleImpact, fragmentStateManager.getFragment(), cancellationSignal);
            this.mFragmentStateManager = fragmentStateManager;
        }

        public void complete() {
            super.complete();
            this.mFragmentStateManager.moveToExpectedState();
        }
    }
}

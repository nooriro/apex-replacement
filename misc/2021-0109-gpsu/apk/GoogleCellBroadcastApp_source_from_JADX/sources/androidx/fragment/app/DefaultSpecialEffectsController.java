package androidx.fragment.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import androidx.collection.ArrayMap;
import androidx.core.p002os.CancellationSignal;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.fragment.app.FragmentAnim;
import androidx.fragment.app.SpecialEffectsController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class DefaultSpecialEffectsController extends SpecialEffectsController {
    private final HashMap<SpecialEffectsController.Operation, HashSet<CancellationSignal>> mRunningOperations = new HashMap<>();

    DefaultSpecialEffectsController(ViewGroup viewGroup) {
        super(viewGroup);
    }

    private void addCancellationSignal(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal) {
        if (this.mRunningOperations.get(operation) == null) {
            this.mRunningOperations.put(operation, new HashSet());
        }
        this.mRunningOperations.get(operation).add(cancellationSignal);
    }

    /* access modifiers changed from: package-private */
    public void removeCancellationSignal(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal) {
        HashSet hashSet = this.mRunningOperations.get(operation);
        if (hashSet != null && hashSet.remove(cancellationSignal) && hashSet.isEmpty()) {
            this.mRunningOperations.remove(operation);
            operation.complete();
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelAllSpecialEffects(SpecialEffectsController.Operation operation) {
        HashSet remove = this.mRunningOperations.remove(operation);
        if (remove != null) {
            Iterator it = remove.iterator();
            while (it.hasNext()) {
                ((CancellationSignal) it.next()).cancel();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void executeOperations(List<SpecialEffectsController.Operation> list, boolean z) {
        SpecialEffectsController.Operation operation = null;
        SpecialEffectsController.Operation operation2 = null;
        for (SpecialEffectsController.Operation next : list) {
            SpecialEffectsController.Operation.State from = SpecialEffectsController.Operation.State.from(next.getFragment().mView);
            int i = C013811.f4xe493b431[next.getFinalState().ordinal()];
            if (i == 1 || i == 2 || i == 3) {
                if (from == SpecialEffectsController.Operation.State.VISIBLE && operation == null) {
                    operation = next;
                }
            } else if (i == 4 && from != SpecialEffectsController.Operation.State.VISIBLE) {
                operation2 = next;
            }
        }
        ArrayList<AnimationInfo> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        final ArrayList<SpecialEffectsController.Operation> arrayList3 = new ArrayList<>(list);
        Iterator<SpecialEffectsController.Operation> it = list.iterator();
        while (true) {
            boolean z2 = false;
            if (!it.hasNext()) {
                break;
            }
            final SpecialEffectsController.Operation next2 = it.next();
            CancellationSignal cancellationSignal = new CancellationSignal();
            addCancellationSignal(next2, cancellationSignal);
            arrayList.add(new AnimationInfo(next2, cancellationSignal));
            CancellationSignal cancellationSignal2 = new CancellationSignal();
            addCancellationSignal(next2, cancellationSignal2);
            if (z) {
                if (next2 != operation) {
                    arrayList2.add(new TransitionInfo(next2, cancellationSignal2, z, z2));
                    next2.addCompletionListener(new Runnable(this) {
                        public void run() {
                            View focusedView;
                            if (next2.getFinalState() == SpecialEffectsController.Operation.State.VISIBLE && (focusedView = next2.getFragment().getFocusedView()) != null) {
                                focusedView.requestFocus();
                                next2.getFragment().setFocusedView((View) null);
                            }
                        }
                    });
                    next2.addCompletionListener(new Runnable() {
                        public void run() {
                            if (arrayList3.contains(next2)) {
                                arrayList3.remove(next2);
                                DefaultSpecialEffectsController.this.applyContainerChanges(next2);
                            }
                        }
                    });
                    next2.getCancellationSignal().setOnCancelListener(new CancellationSignal.OnCancelListener() {
                        public void onCancel() {
                            DefaultSpecialEffectsController.this.cancelAllSpecialEffects(next2);
                        }
                    });
                }
            } else if (next2 != operation2) {
                arrayList2.add(new TransitionInfo(next2, cancellationSignal2, z, z2));
                next2.addCompletionListener(new Runnable(this) {
                    public void run() {
                        View focusedView;
                        if (next2.getFinalState() == SpecialEffectsController.Operation.State.VISIBLE && (focusedView = next2.getFragment().getFocusedView()) != null) {
                            focusedView.requestFocus();
                            next2.getFragment().setFocusedView((View) null);
                        }
                    }
                });
                next2.addCompletionListener(new Runnable() {
                    public void run() {
                        if (arrayList3.contains(next2)) {
                            arrayList3.remove(next2);
                            DefaultSpecialEffectsController.this.applyContainerChanges(next2);
                        }
                    }
                });
                next2.getCancellationSignal().setOnCancelListener(new CancellationSignal.OnCancelListener() {
                    public void onCancel() {
                        DefaultSpecialEffectsController.this.cancelAllSpecialEffects(next2);
                    }
                });
            }
            z2 = true;
            arrayList2.add(new TransitionInfo(next2, cancellationSignal2, z, z2));
            next2.addCompletionListener(new Runnable(this) {
                public void run() {
                    View focusedView;
                    if (next2.getFinalState() == SpecialEffectsController.Operation.State.VISIBLE && (focusedView = next2.getFragment().getFocusedView()) != null) {
                        focusedView.requestFocus();
                        next2.getFragment().setFocusedView((View) null);
                    }
                }
            });
            next2.addCompletionListener(new Runnable() {
                public void run() {
                    if (arrayList3.contains(next2)) {
                        arrayList3.remove(next2);
                        DefaultSpecialEffectsController.this.applyContainerChanges(next2);
                    }
                }
            });
            next2.getCancellationSignal().setOnCancelListener(new CancellationSignal.OnCancelListener() {
                public void onCancel() {
                    DefaultSpecialEffectsController.this.cancelAllSpecialEffects(next2);
                }
            });
        }
        Map<SpecialEffectsController.Operation, Boolean> startTransitions = startTransitions(arrayList2, z, operation, operation2);
        boolean containsValue = startTransitions.containsValue(Boolean.TRUE);
        for (AnimationInfo animationInfo : arrayList) {
            SpecialEffectsController.Operation operation3 = animationInfo.getOperation();
            startAnimation(operation3, animationInfo.getSignal(), containsValue, startTransitions.containsKey(operation3) ? startTransitions.get(operation3).booleanValue() : false);
        }
        for (SpecialEffectsController.Operation applyContainerChanges : arrayList3) {
            applyContainerChanges(applyContainerChanges);
        }
        arrayList3.clear();
    }

    /* renamed from: androidx.fragment.app.DefaultSpecialEffectsController$11 */
    static /* synthetic */ class C013811 {

        /* renamed from: $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State */
        static final /* synthetic */ int[] f4xe493b431;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                androidx.fragment.app.SpecialEffectsController$Operation$State[] r0 = androidx.fragment.app.SpecialEffectsController.Operation.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f4xe493b431 = r0
                androidx.fragment.app.SpecialEffectsController$Operation$State r1 = androidx.fragment.app.SpecialEffectsController.Operation.State.GONE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f4xe493b431     // Catch:{ NoSuchFieldError -> 0x001d }
                androidx.fragment.app.SpecialEffectsController$Operation$State r1 = androidx.fragment.app.SpecialEffectsController.Operation.State.INVISIBLE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f4xe493b431     // Catch:{ NoSuchFieldError -> 0x0028 }
                androidx.fragment.app.SpecialEffectsController$Operation$State r1 = androidx.fragment.app.SpecialEffectsController.Operation.State.REMOVED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f4xe493b431     // Catch:{ NoSuchFieldError -> 0x0033 }
                androidx.fragment.app.SpecialEffectsController$Operation$State r1 = androidx.fragment.app.SpecialEffectsController.Operation.State.VISIBLE     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.DefaultSpecialEffectsController.C013811.<clinit>():void");
        }
    }

    private void startAnimation(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal, boolean z, boolean z2) {
        SpecialEffectsController.Operation.State state;
        Animation animation;
        final ViewGroup container = getContainer();
        Context context = container.getContext();
        Fragment fragment = operation.getFragment();
        final View view = fragment.mView;
        SpecialEffectsController.Operation.State from = SpecialEffectsController.Operation.State.from(view);
        SpecialEffectsController.Operation.State finalState = operation.getFinalState();
        if (from == finalState || !(from == (state = SpecialEffectsController.Operation.State.VISIBLE) || finalState == state)) {
            removeCancellationSignal(operation, cancellationSignal);
            return;
        }
        FragmentAnim.AnimationOrAnimator loadAnimation = FragmentAnim.loadAnimation(context, fragment, finalState == SpecialEffectsController.Operation.State.VISIBLE);
        if (loadAnimation == null) {
            removeCancellationSignal(operation, cancellationSignal);
        } else if (z && loadAnimation.animation != null) {
            if (FragmentManager.isLoggingEnabled(2)) {
                Log.v("FragmentManager", "Ignoring Animation set on " + fragment + " as Animations cannot run alongside Transitions.");
            }
            removeCancellationSignal(operation, cancellationSignal);
        } else if (z2) {
            if (FragmentManager.isLoggingEnabled(2)) {
                Log.v("FragmentManager", "Ignoring Animator set on " + fragment + " as this Fragment was involved in a Transition.");
            }
            removeCancellationSignal(operation, cancellationSignal);
        } else {
            container.startViewTransition(view);
            if (loadAnimation.animation != null) {
                if (operation.getFinalState() == SpecialEffectsController.Operation.State.VISIBLE) {
                    animation = new FragmentAnim.EnterViewTransitionAnimation(loadAnimation.animation);
                } else {
                    animation = new FragmentAnim.EndViewTransitionAnimation(loadAnimation.animation, container, view);
                }
                final View view2 = view;
                final SpecialEffectsController.Operation operation2 = operation;
                final CancellationSignal cancellationSignal2 = cancellationSignal;
                animation.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        container.post(new Runnable() {
                            public void run() {
                                C01414 r0 = C01414.this;
                                container.endViewTransition(view2);
                                C01414 r2 = C01414.this;
                                DefaultSpecialEffectsController.this.removeCancellationSignal(operation2, cancellationSignal2);
                            }
                        });
                    }
                });
                view.startAnimation(animation);
            } else {
                final View view3 = view;
                final SpecialEffectsController.Operation operation3 = operation;
                final CancellationSignal cancellationSignal3 = cancellationSignal;
                loadAnimation.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        container.endViewTransition(view3);
                        DefaultSpecialEffectsController.this.removeCancellationSignal(operation3, cancellationSignal3);
                    }
                });
                loadAnimation.animator.setTarget(view);
                loadAnimation.animator.start();
            }
            cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener(this) {
                public void onCancel() {
                    view.clearAnimation();
                }
            });
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:85:0x02cb, code lost:
        r0 = (android.view.View) r9.get(r16.get(0));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Map<androidx.fragment.app.SpecialEffectsController.Operation, java.lang.Boolean> startTransitions(java.util.List<androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo> r32, boolean r33, androidx.fragment.app.SpecialEffectsController.Operation r34, androidx.fragment.app.SpecialEffectsController.Operation r35) {
        /*
            r31 = this;
            r6 = r31
            r7 = r33
            r8 = r34
            r9 = r35
            java.lang.Boolean r10 = java.lang.Boolean.TRUE
            java.lang.Boolean r11 = java.lang.Boolean.FALSE
            java.util.HashMap r12 = new java.util.HashMap
            r12.<init>()
            java.util.Iterator r0 = r32.iterator()
            r15 = 0
        L_0x0016:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0066
            java.lang.Object r1 = r0.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r1 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r1
            boolean r2 = r1.isVisibilityUnchanged()
            if (r2 == 0) goto L_0x0029
            goto L_0x0016
        L_0x0029:
            androidx.fragment.app.FragmentTransitionImpl r2 = r1.getHandlingImpl()
            if (r15 != 0) goto L_0x0031
            r15 = r2
            goto L_0x0016
        L_0x0031:
            if (r2 == 0) goto L_0x0016
            if (r15 != r2) goto L_0x0036
            goto L_0x0016
        L_0x0036:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Mixing framework transitions and AndroidX transitions is not allowed. Fragment "
            r2.append(r3)
            androidx.fragment.app.SpecialEffectsController$Operation r3 = r1.getOperation()
            androidx.fragment.app.Fragment r3 = r3.getFragment()
            r2.append(r3)
            java.lang.String r3 = " returned Transition "
            r2.append(r3)
            java.lang.Object r1 = r1.getTransition()
            r2.append(r1)
            java.lang.String r1 = " which uses a different Transition  type than other Fragments."
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            r0.<init>(r1)
            throw r0
        L_0x0066:
            if (r15 != 0) goto L_0x008c
            java.util.Iterator r0 = r32.iterator()
        L_0x006c:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x008b
            java.lang.Object r1 = r0.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r1 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r1
            androidx.fragment.app.SpecialEffectsController$Operation r2 = r1.getOperation()
            r12.put(r2, r11)
            androidx.fragment.app.SpecialEffectsController$Operation r2 = r1.getOperation()
            androidx.core.os.CancellationSignal r1 = r1.getSignal()
            r6.removeCancellationSignal(r2, r1)
            goto L_0x006c
        L_0x008b:
            return r12
        L_0x008c:
            android.view.View r14 = new android.view.View
            android.view.ViewGroup r0 = r31.getContainer()
            android.content.Context r0 = r0.getContext()
            r14.<init>(r0)
            android.graphics.Rect r5 = new android.graphics.Rect
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            androidx.collection.ArrayMap r2 = new androidx.collection.ArrayMap
            r2.<init>()
            java.util.Iterator r22 = r32.iterator()
            r0 = 0
            r13 = 0
            r23 = 0
        L_0x00b5:
            boolean r16 = r22.hasNext()
            r24 = r13
            if (r16 == 0) goto L_0x0340
            java.lang.Object r16 = r22.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r16 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r16
            boolean r17 = r16.hasSharedElementTransition()
            if (r17 == 0) goto L_0x031a
            if (r8 == 0) goto L_0x031a
            if (r9 == 0) goto L_0x031a
            java.lang.Object r0 = r16.getSharedElementTransition()
            java.lang.Object r0 = r15.cloneTransition(r0)
            java.lang.Object r0 = r15.wrapTransitionInSet(r0)
            androidx.fragment.app.Fragment r16 = r35.getFragment()
            java.util.ArrayList r13 = r16.getSharedElementSourceNames()
            androidx.fragment.app.Fragment r16 = r34.getFragment()
            java.util.ArrayList r1 = r16.getSharedElementSourceNames()
            androidx.fragment.app.Fragment r16 = r34.getFragment()
            r18 = r0
            java.util.ArrayList r0 = r16.getSharedElementTargetNames()
            r16 = r5
            r25 = r11
            r5 = 0
        L_0x00f8:
            int r11 = r0.size()
            if (r5 >= r11) goto L_0x0117
            java.lang.Object r11 = r0.get(r5)
            int r11 = r13.indexOf(r11)
            r19 = r0
            r0 = -1
            if (r11 == r0) goto L_0x0112
            java.lang.Object r0 = r1.get(r5)
            r13.set(r11, r0)
        L_0x0112:
            int r5 = r5 + 1
            r0 = r19
            goto L_0x00f8
        L_0x0117:
            androidx.fragment.app.Fragment r0 = r35.getFragment()
            java.util.ArrayList r11 = r0.getSharedElementTargetNames()
            if (r7 != 0) goto L_0x0132
            androidx.fragment.app.Fragment r0 = r34.getFragment()
            androidx.core.app.SharedElementCallback r0 = r0.getExitTransitionCallback()
            androidx.fragment.app.Fragment r1 = r35.getFragment()
            androidx.core.app.SharedElementCallback r1 = r1.getEnterTransitionCallback()
            goto L_0x0142
        L_0x0132:
            androidx.fragment.app.Fragment r0 = r34.getFragment()
            androidx.core.app.SharedElementCallback r0 = r0.getEnterTransitionCallback()
            androidx.fragment.app.Fragment r1 = r35.getFragment()
            androidx.core.app.SharedElementCallback r1 = r1.getExitTransitionCallback()
        L_0x0142:
            int r5 = r13.size()
            r9 = 0
        L_0x0147:
            if (r9 >= r5) goto L_0x0165
            java.lang.Object r19 = r13.get(r9)
            r20 = r5
            r5 = r19
            java.lang.String r5 = (java.lang.String) r5
            java.lang.Object r19 = r11.get(r9)
            r8 = r19
            java.lang.String r8 = (java.lang.String) r8
            r2.put(r5, r8)
            int r9 = r9 + 1
            r8 = r34
            r5 = r20
            goto L_0x0147
        L_0x0165:
            androidx.collection.ArrayMap r8 = new androidx.collection.ArrayMap
            r8.<init>()
            androidx.fragment.app.Fragment r5 = r34.getFragment()
            android.view.View r5 = r5.mView
            r6.findNamedViews(r8, r5)
            r8.retainAll(r13)
            if (r0 == 0) goto L_0x01b8
            r0.onMapSharedElements(r13, r8)
            int r0 = r13.size()
            r5 = 1
            int r0 = r0 - r5
        L_0x0181:
            if (r0 < 0) goto L_0x01b5
            java.lang.Object r5 = r13.get(r0)
            java.lang.String r5 = (java.lang.String) r5
            java.lang.Object r9 = r8.get(r5)
            android.view.View r9 = (android.view.View) r9
            if (r9 != 0) goto L_0x0197
            r2.remove(r5)
            r26 = r10
            goto L_0x01b0
        L_0x0197:
            r26 = r10
            java.lang.String r10 = androidx.core.view.ViewCompat.getTransitionName(r9)
            boolean r10 = r5.equals(r10)
            if (r10 != 0) goto L_0x01b0
            java.lang.Object r5 = r2.remove(r5)
            java.lang.String r5 = (java.lang.String) r5
            java.lang.String r9 = androidx.core.view.ViewCompat.getTransitionName(r9)
            r2.put(r9, r5)
        L_0x01b0:
            int r0 = r0 + -1
            r10 = r26
            goto L_0x0181
        L_0x01b5:
            r26 = r10
            goto L_0x01c1
        L_0x01b8:
            r26 = r10
            java.util.Set r0 = r8.keySet()
            r2.retainAll(r0)
        L_0x01c1:
            androidx.collection.ArrayMap r9 = new androidx.collection.ArrayMap
            r9.<init>()
            androidx.fragment.app.Fragment r0 = r35.getFragment()
            android.view.View r0 = r0.mView
            r6.findNamedViews(r9, r0)
            r9.retainAll(r11)
            java.util.Collection r0 = r2.values()
            r9.retainAll(r0)
            if (r1 == 0) goto L_0x0218
            r1.onMapSharedElements(r11, r9)
            int r0 = r11.size()
            r1 = 1
            int r0 = r0 - r1
        L_0x01e4:
            if (r0 < 0) goto L_0x021b
            java.lang.Object r1 = r11.get(r0)
            java.lang.String r1 = (java.lang.String) r1
            java.lang.Object r5 = r9.get(r1)
            android.view.View r5 = (android.view.View) r5
            if (r5 != 0) goto L_0x01fe
            java.lang.String r1 = androidx.fragment.app.FragmentTransition.findKeyForValue(r2, r1)
            if (r1 == 0) goto L_0x0215
            r2.remove(r1)
            goto L_0x0215
        L_0x01fe:
            java.lang.String r10 = androidx.core.view.ViewCompat.getTransitionName(r5)
            boolean r10 = r1.equals(r10)
            if (r10 != 0) goto L_0x0215
            java.lang.String r1 = androidx.fragment.app.FragmentTransition.findKeyForValue(r2, r1)
            if (r1 == 0) goto L_0x0215
            java.lang.String r5 = androidx.core.view.ViewCompat.getTransitionName(r5)
            r2.put(r1, r5)
        L_0x0215:
            int r0 = r0 + -1
            goto L_0x01e4
        L_0x0218:
            androidx.fragment.app.FragmentTransition.retainValues(r2, r9)
        L_0x021b:
            java.util.Set r0 = r2.keySet()
            r6.retainMatchingViews(r8, r0)
            java.util.Collection r0 = r2.values()
            r6.retainMatchingViews(r9, r0)
            boolean r0 = r2.isEmpty()
            if (r0 == 0) goto L_0x0249
            r4.clear()
            r3.clear()
            r5 = r35
            r27 = r2
            r7 = r4
            r9 = r12
            r1 = r14
            r8 = r15
            r13 = r24
            r4 = r26
            r0 = 0
            r2 = r34
            r12 = r3
            r3 = r16
            goto L_0x032c
        L_0x0249:
            androidx.fragment.app.Fragment r0 = r35.getFragment()
            androidx.fragment.app.Fragment r1 = r34.getFragment()
            r10 = 1
            androidx.fragment.app.FragmentTransition.callSharedElementStartEnd(r0, r1, r7, r8, r10)
            android.view.ViewGroup r5 = r31.getContainer()
            androidx.fragment.app.DefaultSpecialEffectsController$7 r1 = new androidx.fragment.app.DefaultSpecialEffectsController$7
            r10 = r18
            r0 = r1
            r7 = r1
            r1 = r31
            r27 = r2
            r2 = r35
            r28 = r12
            r12 = r3
            r3 = r34
            r17 = r14
            r14 = r4
            r4 = r33
            r29 = r16
            r16 = r11
            r11 = r5
            r5 = r9
            r0.<init>(r1, r2, r3, r4, r5)
            androidx.core.view.OneShotPreDrawListener.add(r11, r7)
            java.util.Collection r0 = r8.values()
            java.util.Iterator r0 = r0.iterator()
        L_0x0283:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0293
            java.lang.Object r1 = r0.next()
            android.view.View r1 = (android.view.View) r1
            r6.captureTransitioningViews(r14, r1)
            goto L_0x0283
        L_0x0293:
            boolean r0 = r13.isEmpty()
            if (r0 != 0) goto L_0x02ab
            r0 = 0
            java.lang.Object r1 = r13.get(r0)
            java.lang.String r1 = (java.lang.String) r1
            java.lang.Object r0 = r8.get(r1)
            android.view.View r0 = (android.view.View) r0
            r15.setEpicenter((java.lang.Object) r10, (android.view.View) r0)
            r13 = r0
            goto L_0x02ad
        L_0x02ab:
            r13 = r24
        L_0x02ad:
            java.util.Collection r0 = r9.values()
            java.util.Iterator r0 = r0.iterator()
        L_0x02b5:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x02c5
            java.lang.Object r1 = r0.next()
            android.view.View r1 = (android.view.View) r1
            r6.captureTransitioningViews(r12, r1)
            goto L_0x02b5
        L_0x02c5:
            boolean r0 = r16.isEmpty()
            if (r0 != 0) goto L_0x02ef
            r0 = r16
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            java.lang.Object r0 = r9.get(r0)
            android.view.View r0 = (android.view.View) r0
            if (r0 == 0) goto L_0x02ef
            android.view.ViewGroup r1 = r31.getContainer()
            androidx.fragment.app.DefaultSpecialEffectsController$8 r2 = new androidx.fragment.app.DefaultSpecialEffectsController$8
            r3 = r29
            r2.<init>(r6, r15, r0, r3)
            androidx.core.view.OneShotPreDrawListener.add(r1, r2)
            r0 = r17
            r23 = 1
            goto L_0x02f3
        L_0x02ef:
            r3 = r29
            r0 = r17
        L_0x02f3:
            r15.setSharedElementTargets(r10, r0, r14)
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r1 = r0
            r7 = r14
            r14 = r15
            r8 = r15
            r15 = r10
            r20 = r10
            r21 = r12
            r14.scheduleRemoveTargets(r15, r16, r17, r18, r19, r20, r21)
            r2 = r34
            r4 = r26
            r9 = r28
            r9.put(r2, r4)
            r5 = r35
            r9.put(r5, r4)
            r0 = r10
            goto L_0x032c
        L_0x031a:
            r27 = r2
            r7 = r4
            r2 = r8
            r4 = r10
            r25 = r11
            r1 = r14
            r8 = r15
            r30 = r12
            r12 = r3
            r3 = r5
            r5 = r9
            r9 = r30
            r13 = r24
        L_0x032c:
            r14 = r1
            r10 = r4
            r4 = r7
            r15 = r8
            r11 = r25
            r7 = r33
            r8 = r2
            r2 = r27
            r30 = r5
            r5 = r3
            r3 = r12
            r12 = r9
            r9 = r30
            goto L_0x00b5
        L_0x0340:
            r27 = r2
            r7 = r4
            r2 = r8
            r4 = r10
            r25 = r11
            r1 = r14
            r8 = r15
            r30 = r12
            r12 = r3
            r3 = r5
            r5 = r9
            r9 = r30
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            java.util.Iterator r11 = r32.iterator()
            r13 = 0
            r15 = 0
        L_0x035b:
            boolean r14 = r11.hasNext()
            if (r14 == 0) goto L_0x0463
            java.lang.Object r14 = r11.next()
            r22 = r14
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r22 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r22
            boolean r14 = r22.isVisibilityUnchanged()
            if (r14 == 0) goto L_0x038c
            androidx.fragment.app.SpecialEffectsController$Operation r14 = r22.getOperation()
            r33 = r11
            r11 = r25
            r9.put(r14, r11)
            androidx.fragment.app.SpecialEffectsController$Operation r14 = r22.getOperation()
            r16 = r15
            androidx.core.os.CancellationSignal r15 = r22.getSignal()
            r6.removeCancellationSignal(r14, r15)
            r15 = r16
            r11 = r33
            goto L_0x035b
        L_0x038c:
            r33 = r11
            r16 = r15
            r11 = r25
            java.lang.Object r14 = r22.getTransition()
            java.lang.Object r15 = r8.cloneTransition(r14)
            androidx.fragment.app.SpecialEffectsController$Operation r14 = r22.getOperation()
            if (r0 == 0) goto L_0x03a7
            if (r14 == r2) goto L_0x03a4
            if (r14 != r5) goto L_0x03a7
        L_0x03a4:
            r17 = 1
            goto L_0x03a9
        L_0x03a7:
            r17 = 0
        L_0x03a9:
            if (r15 != 0) goto L_0x03c2
            if (r17 != 0) goto L_0x03b7
            r9.put(r14, r11)
            androidx.core.os.CancellationSignal r15 = r22.getSignal()
            r6.removeCancellationSignal(r14, r15)
        L_0x03b7:
            r26 = r1
            r25 = r11
            r15 = r16
            r2 = r24
            r5 = 0
            goto L_0x0457
        L_0x03c2:
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r25 = r11
            androidx.fragment.app.Fragment r11 = r14.getFragment()
            android.view.View r11 = r11.mView
            r6.captureTransitioningViews(r5, r11)
            if (r17 == 0) goto L_0x03dd
            if (r14 != r2) goto L_0x03da
            r5.removeAll(r7)
            goto L_0x03dd
        L_0x03da:
            r5.removeAll(r12)
        L_0x03dd:
            boolean r11 = r5.isEmpty()
            if (r11 == 0) goto L_0x03ec
            r8.addTarget(r15, r1)
            r26 = r1
            r11 = r14
            r1 = r16
            goto L_0x0428
        L_0x03ec:
            r8.addTargets(r15, r5)
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r11 = r14
            r14 = r8
            r26 = r1
            r28 = r15
            r1 = r16
            r16 = r28
            r17 = r5
            r14.scheduleRemoveTargets(r15, r16, r17, r18, r19, r20, r21)
            androidx.fragment.app.SpecialEffectsController$Operation$State r14 = r11.getFinalState()
            androidx.fragment.app.SpecialEffectsController$Operation$State r15 = androidx.fragment.app.SpecialEffectsController.Operation.State.GONE
            if (r14 != r15) goto L_0x0426
            androidx.fragment.app.Fragment r14 = r11.getFragment()
            android.view.View r14 = r14.mView
            r15 = r28
            r8.scheduleHideFragmentView(r15, r14, r5)
            android.view.ViewGroup r14 = r31.getContainer()
            androidx.fragment.app.DefaultSpecialEffectsController$9 r2 = new androidx.fragment.app.DefaultSpecialEffectsController$9
            r2.<init>(r6, r5)
            androidx.core.view.OneShotPreDrawListener.add(r14, r2)
            goto L_0x0428
        L_0x0426:
            r15 = r28
        L_0x0428:
            androidx.fragment.app.SpecialEffectsController$Operation$State r2 = r11.getFinalState()
            androidx.fragment.app.SpecialEffectsController$Operation$State r14 = androidx.fragment.app.SpecialEffectsController.Operation.State.VISIBLE
            if (r2 != r14) goto L_0x043b
            r10.addAll(r5)
            if (r23 == 0) goto L_0x0438
            r8.setEpicenter((java.lang.Object) r15, (android.graphics.Rect) r3)
        L_0x0438:
            r2 = r24
            goto L_0x0440
        L_0x043b:
            r2 = r24
            r8.setEpicenter((java.lang.Object) r15, (android.view.View) r2)
        L_0x0440:
            r9.put(r11, r4)
            boolean r5 = r22.isOverlapAllowed()
            if (r5 == 0) goto L_0x0451
            r5 = 0
            java.lang.Object r11 = r8.mergeTransitionsTogether(r13, r15, r5)
            r15 = r1
            r13 = r11
            goto L_0x0457
        L_0x0451:
            r5 = 0
            java.lang.Object r1 = r8.mergeTransitionsTogether(r1, r15, r5)
            r15 = r1
        L_0x0457:
            r11 = r33
            r5 = r35
            r24 = r2
            r1 = r26
            r2 = r34
            goto L_0x035b
        L_0x0463:
            r1 = r15
            java.lang.Object r1 = r8.mergeTransitionsInSequence(r13, r1, r0)
            java.util.Iterator r2 = r32.iterator()
        L_0x046c:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x049a
            java.lang.Object r3 = r2.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r3 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r3
            boolean r4 = r3.isVisibilityUnchanged()
            if (r4 == 0) goto L_0x047f
            goto L_0x046c
        L_0x047f:
            java.lang.Object r4 = r3.getTransition()
            if (r4 == 0) goto L_0x046c
            androidx.fragment.app.SpecialEffectsController$Operation r4 = r3.getOperation()
            androidx.fragment.app.Fragment r4 = r4.getFragment()
            androidx.core.os.CancellationSignal r5 = r3.getSignal()
            androidx.fragment.app.DefaultSpecialEffectsController$10 r11 = new androidx.fragment.app.DefaultSpecialEffectsController$10
            r11.<init>(r3)
            r8.setListenerForTransitionEnd(r4, r1, r5, r11)
            goto L_0x046c
        L_0x049a:
            r2 = 4
            androidx.fragment.app.FragmentTransition.setViewVisibility(r10, r2)
            java.util.ArrayList r5 = r8.prepareSetNameOverridesReordered(r12)
            android.view.ViewGroup r2 = r31.getContainer()
            r8.beginDelayedTransition(r2, r1)
            android.view.ViewGroup r2 = r31.getContainer()
            r1 = r8
            r3 = r7
            r4 = r12
            r6 = r27
            r1.setNameOverridesReordered(r2, r3, r4, r5, r6)
            r1 = 0
            androidx.fragment.app.FragmentTransition.setViewVisibility(r10, r1)
            r8.swapSharedElementTargets(r0, r7, r12)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.DefaultSpecialEffectsController.startTransitions(java.util.List, boolean, androidx.fragment.app.SpecialEffectsController$Operation, androidx.fragment.app.SpecialEffectsController$Operation):java.util.Map");
    }

    /* access modifiers changed from: package-private */
    public void retainMatchingViews(ArrayMap<String, View> arrayMap, Collection<String> collection) {
        Iterator<Map.Entry<String, View>> it = arrayMap.entrySet().iterator();
        while (it.hasNext()) {
            if (!collection.contains(ViewCompat.getTransitionName((View) it.next().getValue()))) {
                it.remove();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void captureTransitioningViews(ArrayList<View> arrayList, View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (ViewGroupCompat.isTransitionGroup(viewGroup)) {
                arrayList.add(viewGroup);
                return;
            }
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt.getVisibility() == 0) {
                    captureTransitioningViews(arrayList, childAt);
                }
            }
            return;
        }
        arrayList.add(view);
    }

    /* access modifiers changed from: package-private */
    public void findNamedViews(Map<String, View> map, View view) {
        String transitionName = ViewCompat.getTransitionName(view);
        if (transitionName != null) {
            map.put(transitionName, view);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt.getVisibility() == 0) {
                    findNamedViews(map, childAt);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void applyContainerChanges(SpecialEffectsController.Operation operation) {
        operation.getFinalState().applyState(operation.getFragment().mView);
    }

    private static class AnimationInfo {
        private final SpecialEffectsController.Operation mOperation;
        private final CancellationSignal mSignal;

        AnimationInfo(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal) {
            this.mOperation = operation;
            this.mSignal = cancellationSignal;
        }

        /* access modifiers changed from: package-private */
        public SpecialEffectsController.Operation getOperation() {
            return this.mOperation;
        }

        /* access modifiers changed from: package-private */
        public CancellationSignal getSignal() {
            return this.mSignal;
        }
    }

    private static class TransitionInfo {
        private final SpecialEffectsController.Operation mOperation;
        private final boolean mOverlapAllowed;
        private final Object mSharedElementTransition;
        private final CancellationSignal mSignal;
        private final Object mTransition;

        TransitionInfo(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal, boolean z, boolean z2) {
            Object obj;
            Object obj2;
            boolean z3;
            this.mOperation = operation;
            this.mSignal = cancellationSignal;
            if (operation.getFinalState() == SpecialEffectsController.Operation.State.VISIBLE) {
                if (z) {
                    obj2 = operation.getFragment().getReenterTransition();
                } else {
                    obj2 = operation.getFragment().getEnterTransition();
                }
                this.mTransition = obj2;
                if (z) {
                    z3 = operation.getFragment().getAllowReturnTransitionOverlap();
                } else {
                    z3 = operation.getFragment().getAllowEnterTransitionOverlap();
                }
                this.mOverlapAllowed = z3;
            } else {
                if (z) {
                    obj = operation.getFragment().getReturnTransition();
                } else {
                    obj = operation.getFragment().getExitTransition();
                }
                this.mTransition = obj;
                this.mOverlapAllowed = true;
            }
            if (!z2) {
                this.mSharedElementTransition = null;
            } else if (z) {
                this.mSharedElementTransition = operation.getFragment().getSharedElementReturnTransition();
            } else {
                this.mSharedElementTransition = operation.getFragment().getSharedElementEnterTransition();
            }
        }

        /* access modifiers changed from: package-private */
        public SpecialEffectsController.Operation getOperation() {
            return this.mOperation;
        }

        /* access modifiers changed from: package-private */
        public CancellationSignal getSignal() {
            return this.mSignal;
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:2:0x0014, code lost:
            r1 = androidx.fragment.app.SpecialEffectsController.Operation.State.VISIBLE;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isVisibilityUnchanged() {
            /*
                r2 = this;
                androidx.fragment.app.SpecialEffectsController$Operation r0 = r2.mOperation
                androidx.fragment.app.Fragment r0 = r0.getFragment()
                android.view.View r0 = r0.mView
                androidx.fragment.app.SpecialEffectsController$Operation$State r0 = androidx.fragment.app.SpecialEffectsController.Operation.State.from((android.view.View) r0)
                androidx.fragment.app.SpecialEffectsController$Operation r2 = r2.mOperation
                androidx.fragment.app.SpecialEffectsController$Operation$State r2 = r2.getFinalState()
                if (r0 == r2) goto L_0x001d
                androidx.fragment.app.SpecialEffectsController$Operation$State r1 = androidx.fragment.app.SpecialEffectsController.Operation.State.VISIBLE
                if (r0 == r1) goto L_0x001b
                if (r2 == r1) goto L_0x001b
                goto L_0x001d
            L_0x001b:
                r2 = 0
                goto L_0x001e
            L_0x001d:
                r2 = 1
            L_0x001e:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo.isVisibilityUnchanged():boolean");
        }

        /* access modifiers changed from: package-private */
        public Object getTransition() {
            return this.mTransition;
        }

        /* access modifiers changed from: package-private */
        public boolean isOverlapAllowed() {
            return this.mOverlapAllowed;
        }

        public boolean hasSharedElementTransition() {
            return this.mSharedElementTransition != null;
        }

        public Object getSharedElementTransition() {
            return this.mSharedElementTransition;
        }

        /* access modifiers changed from: package-private */
        public FragmentTransitionImpl getHandlingImpl() {
            FragmentTransitionImpl handlingImpl = getHandlingImpl(this.mTransition);
            FragmentTransitionImpl handlingImpl2 = getHandlingImpl(this.mSharedElementTransition);
            if (handlingImpl == null || handlingImpl2 == null || handlingImpl == handlingImpl2) {
                return handlingImpl != null ? handlingImpl : handlingImpl2;
            }
            throw new IllegalArgumentException("Mixing framework transitions and AndroidX transitions is not allowed. Fragment " + this.mOperation.getFragment() + " returned Transition " + this.mTransition + " which uses a different Transition  type than its shared element transition " + this.mSharedElementTransition);
        }

        private FragmentTransitionImpl getHandlingImpl(Object obj) {
            if (obj == null) {
                return null;
            }
            FragmentTransitionImpl fragmentTransitionImpl = FragmentTransition.PLATFORM_IMPL;
            if (fragmentTransitionImpl != null && fragmentTransitionImpl.canHandle(obj)) {
                return FragmentTransition.PLATFORM_IMPL;
            }
            FragmentTransitionImpl fragmentTransitionImpl2 = FragmentTransition.SUPPORT_IMPL;
            if (fragmentTransitionImpl2 != null && fragmentTransitionImpl2.canHandle(obj)) {
                return FragmentTransition.SUPPORT_IMPL;
            }
            throw new IllegalArgumentException("Transition " + obj + " for fragment " + this.mOperation.getFragment() + " is not a valid framework Transition or AndroidX Transition");
        }
    }
}

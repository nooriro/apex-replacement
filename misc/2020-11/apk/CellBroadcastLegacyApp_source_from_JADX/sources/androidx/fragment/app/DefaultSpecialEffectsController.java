package androidx.fragment.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import androidx.core.p002os.CancellationSignal;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.fragment.app.FragmentAnim;
import androidx.fragment.app.SpecialEffectsController;
import java.util.ArrayList;
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

    /* renamed from: androidx.fragment.app.DefaultSpecialEffectsController$8 */
    static /* synthetic */ class C01178 {

        /* renamed from: $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$Type */
        static final /* synthetic */ int[] f4x8b812b9a;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                androidx.fragment.app.SpecialEffectsController$Operation$Type[] r0 = androidx.fragment.app.SpecialEffectsController.Operation.Type.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f4x8b812b9a = r0
                androidx.fragment.app.SpecialEffectsController$Operation$Type r1 = androidx.fragment.app.SpecialEffectsController.Operation.Type.HIDE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f4x8b812b9a     // Catch:{ NoSuchFieldError -> 0x001d }
                androidx.fragment.app.SpecialEffectsController$Operation$Type r1 = androidx.fragment.app.SpecialEffectsController.Operation.Type.REMOVE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f4x8b812b9a     // Catch:{ NoSuchFieldError -> 0x0028 }
                androidx.fragment.app.SpecialEffectsController$Operation$Type r1 = androidx.fragment.app.SpecialEffectsController.Operation.Type.SHOW     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f4x8b812b9a     // Catch:{ NoSuchFieldError -> 0x0033 }
                androidx.fragment.app.SpecialEffectsController$Operation$Type r1 = androidx.fragment.app.SpecialEffectsController.Operation.Type.ADD     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.DefaultSpecialEffectsController.C01178.<clinit>():void");
        }
    }

    /* access modifiers changed from: package-private */
    public void executeOperations(List<SpecialEffectsController.Operation> list, boolean z) {
        SpecialEffectsController.Operation operation = null;
        SpecialEffectsController.Operation operation2 = null;
        for (SpecialEffectsController.Operation next : list) {
            int i = C01178.f4x8b812b9a[next.getType().ordinal()];
            if (i == 1 || i == 2) {
                if (operation == null) {
                    operation = next;
                }
            } else if (i == 3 || i == 4) {
                operation2 = next;
            }
        }
        ArrayList<AnimationInfo> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        final ArrayList<SpecialEffectsController.Operation> arrayList3 = new ArrayList<>(list);
        for (final SpecialEffectsController.Operation next2 : list) {
            CancellationSignal cancellationSignal = new CancellationSignal();
            addCancellationSignal(next2, cancellationSignal);
            arrayList.add(new AnimationInfo(next2, cancellationSignal));
            CancellationSignal cancellationSignal2 = new CancellationSignal();
            addCancellationSignal(next2, cancellationSignal2);
            boolean z2 = false;
            if (z) {
                if (next2 != operation) {
                    arrayList2.add(new TransitionInfo(next2, cancellationSignal2, z, z2));
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
        startTransitions(arrayList2, z, operation, operation2);
        for (AnimationInfo animationInfo : arrayList) {
            startAnimation(animationInfo.getOperation(), animationInfo.getSignal());
        }
        for (SpecialEffectsController.Operation applyContainerChanges : arrayList3) {
            applyContainerChanges(applyContainerChanges);
        }
        arrayList3.clear();
    }

    private void startAnimation(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal) {
        Animation animation;
        final ViewGroup container = getContainer();
        Context context = container.getContext();
        Fragment fragment = operation.getFragment();
        final View view = fragment.mView;
        FragmentAnim.AnimationOrAnimator loadAnimation = FragmentAnim.loadAnimation(context, fragment, operation.getType() == SpecialEffectsController.Operation.Type.ADD || operation.getType() == SpecialEffectsController.Operation.Type.SHOW);
        if (loadAnimation == null) {
            removeCancellationSignal(operation, cancellationSignal);
            return;
        }
        container.startViewTransition(view);
        if (loadAnimation.animation != null) {
            if (operation.getType() == SpecialEffectsController.Operation.Type.ADD || operation.getType() == SpecialEffectsController.Operation.Type.SHOW) {
                animation = new FragmentAnim.EnterViewTransitionAnimation(loadAnimation.animation);
            } else {
                animation = new FragmentAnim.EndViewTransitionAnimation(loadAnimation.animation, container, view);
            }
            Animation animation2 = animation;
            final View view2 = view;
            final SpecialEffectsController.Operation operation2 = operation;
            final CancellationSignal cancellationSignal2 = cancellationSignal;
            animation2.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    container.post(new Runnable() {
                        public void run() {
                            C01113 r0 = C01113.this;
                            container.endViewTransition(view2);
                            C01113 r2 = C01113.this;
                            DefaultSpecialEffectsController.this.removeCancellationSignal(operation2, cancellationSignal2);
                        }
                    });
                }
            });
            view.startAnimation(animation2);
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

    /* JADX WARNING: Code restructure failed: missing block: B:47:0x01ad, code lost:
        r6 = (android.view.View) r7.get(r9.get(0));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startTransitions(java.util.List<androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo> r23, boolean r24, androidx.fragment.app.SpecialEffectsController.Operation r25, androidx.fragment.app.SpecialEffectsController.Operation r26) {
        /*
            r22 = this;
            r0 = r22
            r1 = r25
            r2 = r26
            java.util.Iterator r3 = r23.iterator()
            r11 = 0
        L_0x000b:
            boolean r5 = r3.hasNext()
            if (r5 == 0) goto L_0x0054
            java.lang.Object r5 = r3.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r5 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r5
            androidx.fragment.app.FragmentTransitionImpl r6 = r5.getHandlingImpl()
            if (r11 != 0) goto L_0x001f
            r11 = r6
            goto L_0x000b
        L_0x001f:
            if (r6 == 0) goto L_0x000b
            if (r11 != r6) goto L_0x0024
            goto L_0x000b
        L_0x0024:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Mixing framework transitions and AndroidX transitions is not allowed. Fragment "
            r1.append(r2)
            androidx.fragment.app.SpecialEffectsController$Operation r2 = r5.getOperation()
            androidx.fragment.app.Fragment r2 = r2.getFragment()
            r1.append(r2)
            java.lang.String r2 = " returned Transition "
            r1.append(r2)
            java.lang.Object r2 = r5.getTransition()
            r1.append(r2)
            java.lang.String r2 = " which uses a different Transition  type than other Fragments."
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0054:
            if (r11 != 0) goto L_0x0073
            java.util.Iterator r1 = r23.iterator()
        L_0x005a:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0072
            java.lang.Object r2 = r1.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r2 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r2
            androidx.fragment.app.SpecialEffectsController$Operation r3 = r2.getOperation()
            androidx.core.os.CancellationSignal r2 = r2.getSignal()
            r0.removeCancellationSignal(r3, r2)
            goto L_0x005a
        L_0x0072:
            return
        L_0x0073:
            android.view.View r3 = new android.view.View
            android.view.ViewGroup r5 = r22.getContainer()
            android.content.Context r5 = r5.getContext()
            r3.<init>(r5)
            android.graphics.Rect r5 = new android.graphics.Rect
            r5.<init>()
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            androidx.collection.ArrayMap r10 = new androidx.collection.ArrayMap
            r10.<init>()
            java.util.Iterator r6 = r23.iterator()
            r7 = 0
            r8 = 0
            r15 = 0
        L_0x009b:
            boolean r9 = r6.hasNext()
            r16 = 1
            if (r9 == 0) goto L_0x01d9
            java.lang.Object r9 = r6.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r9 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r9
            boolean r17 = r9.hasSharedElementTransition()
            if (r17 == 0) goto L_0x01cf
            if (r1 == 0) goto L_0x01cf
            if (r2 == 0) goto L_0x01cf
            java.lang.Object r15 = r9.getSharedElementTransition()
            java.lang.Object r15 = r11.cloneTransition(r15)
            java.lang.Object r15 = r11.wrapTransitionInSet(r15)
            androidx.fragment.app.SpecialEffectsController$Operation r9 = r9.getOperation()
            androidx.fragment.app.Fragment r9 = r9.getFragment()
            if (r24 != 0) goto L_0x00d2
            java.util.ArrayList r17 = r9.getSharedElementSourceNames()
            java.util.ArrayList r9 = r9.getSharedElementTargetNames()
            goto L_0x00da
        L_0x00d2:
            java.util.ArrayList r17 = r9.getSharedElementTargetNames()
            java.util.ArrayList r9 = r9.getSharedElementSourceNames()
        L_0x00da:
            int r4 = r17.size()
            r14 = 0
        L_0x00df:
            if (r14 >= r4) goto L_0x0107
            r18 = r4
            r4 = r17
            java.lang.Object r17 = r4.get(r14)
            r19 = r6
            r6 = r17
            java.lang.String r6 = (java.lang.String) r6
            java.lang.Object r17 = r9.get(r14)
            r20 = r7
            r7 = r17
            java.lang.String r7 = (java.lang.String) r7
            r10.put(r6, r7)
            int r14 = r14 + 1
            r17 = r4
            r4 = r18
            r6 = r19
            r7 = r20
            goto L_0x00df
        L_0x0107:
            r19 = r6
            r20 = r7
            r4 = r17
            androidx.collection.ArrayMap r6 = new androidx.collection.ArrayMap
            r6.<init>()
            androidx.fragment.app.Fragment r7 = r25.getFragment()
            android.view.View r7 = r7.mView
            r0.findNamedViews(r6, r7)
            r6.retainAll(r4)
            java.util.Set r7 = r6.keySet()
            r10.retainAll(r7)
            androidx.collection.ArrayMap r7 = new androidx.collection.ArrayMap
            r7.<init>()
            androidx.fragment.app.Fragment r14 = r26.getFragment()
            android.view.View r14 = r14.mView
            r0.findNamedViews(r7, r14)
            r7.retainAll(r9)
            androidx.fragment.app.FragmentTransition.retainValues(r10, r7)
            java.util.Set r14 = r10.keySet()
            r6.retainAll(r14)
            java.util.Collection r14 = r10.values()
            r7.retainAll(r14)
            boolean r14 = r10.isEmpty()
            if (r14 == 0) goto L_0x0158
            r12.clear()
            r13.clear()
            r7 = r20
            r15 = 0
            goto L_0x01d5
        L_0x0158:
            java.util.Collection r14 = r6.values()
            java.util.Iterator r14 = r14.iterator()
        L_0x0160:
            boolean r17 = r14.hasNext()
            if (r17 == 0) goto L_0x0176
            java.lang.Object r17 = r14.next()
            r18 = r14
            r14 = r17
            android.view.View r14 = (android.view.View) r14
            r0.captureTransitioningViews(r12, r14)
            r14 = r18
            goto L_0x0160
        L_0x0176:
            boolean r14 = r4.isEmpty()
            if (r14 != 0) goto L_0x018d
            r14 = 0
            java.lang.Object r4 = r4.get(r14)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.Object r4 = r6.get(r4)
            android.view.View r4 = (android.view.View) r4
            r11.setEpicenter((java.lang.Object) r15, (android.view.View) r4)
            goto L_0x018f
        L_0x018d:
            r4 = r20
        L_0x018f:
            java.util.Collection r6 = r7.values()
            java.util.Iterator r6 = r6.iterator()
        L_0x0197:
            boolean r14 = r6.hasNext()
            if (r14 == 0) goto L_0x01a7
            java.lang.Object r14 = r6.next()
            android.view.View r14 = (android.view.View) r14
            r0.captureTransitioningViews(r13, r14)
            goto L_0x0197
        L_0x01a7:
            boolean r6 = r9.isEmpty()
            if (r6 != 0) goto L_0x01ca
            r6 = 0
            java.lang.Object r9 = r9.get(r6)
            java.lang.String r9 = (java.lang.String) r9
            java.lang.Object r6 = r7.get(r9)
            android.view.View r6 = (android.view.View) r6
            if (r6 == 0) goto L_0x01ca
            android.view.ViewGroup r7 = r22.getContainer()
            androidx.fragment.app.DefaultSpecialEffectsController$6 r8 = new androidx.fragment.app.DefaultSpecialEffectsController$6
            r8.<init>(r0, r11, r6, r5)
            androidx.core.view.OneShotPreDrawListener.add(r7, r8)
            r8 = r16
        L_0x01ca:
            r11.addTargets(r15, r12)
            r7 = r4
            goto L_0x01d5
        L_0x01cf:
            r19 = r6
            r20 = r7
            r7 = r20
        L_0x01d5:
            r6 = r19
            goto L_0x009b
        L_0x01d9:
            r20 = r7
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.Iterator r6 = r23.iterator()
            r7 = 0
            r9 = 0
        L_0x01e6:
            boolean r14 = r6.hasNext()
            if (r14 == 0) goto L_0x028b
            java.lang.Object r14 = r6.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r14 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r14
            r24 = r6
            java.lang.Object r6 = r14.getTransition()
            java.lang.Object r6 = r11.cloneTransition(r6)
            r17 = r10
            androidx.fragment.app.SpecialEffectsController$Operation r10 = r14.getOperation()
            if (r15 == 0) goto L_0x020b
            if (r10 == r1) goto L_0x0208
            if (r10 != r2) goto L_0x020b
        L_0x0208:
            r18 = r16
            goto L_0x020d
        L_0x020b:
            r18 = 0
        L_0x020d:
            if (r6 != 0) goto L_0x0222
            if (r18 != 0) goto L_0x021c
            androidx.fragment.app.SpecialEffectsController$Operation r6 = r14.getOperation()
            androidx.core.os.CancellationSignal r10 = r14.getSignal()
            r0.removeCancellationSignal(r6, r10)
        L_0x021c:
            r21 = r15
            r2 = r20
            r10 = 0
            goto L_0x027f
        L_0x0222:
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            androidx.fragment.app.SpecialEffectsController$Operation r19 = r14.getOperation()
            r21 = r15
            androidx.fragment.app.Fragment r15 = r19.getFragment()
            android.view.View r15 = r15.mView
            r0.captureTransitioningViews(r2, r15)
            if (r18 == 0) goto L_0x0241
            if (r10 != r1) goto L_0x023e
            r2.removeAll(r12)
            goto L_0x0241
        L_0x023e:
            r2.removeAll(r13)
        L_0x0241:
            boolean r10 = r2.isEmpty()
            if (r10 == 0) goto L_0x024b
            r11.addTarget(r6, r3)
            goto L_0x024e
        L_0x024b:
            r11.addTargets(r6, r2)
        L_0x024e:
            androidx.fragment.app.SpecialEffectsController$Operation r10 = r14.getOperation()
            androidx.fragment.app.SpecialEffectsController$Operation$Type r10 = r10.getType()
            androidx.fragment.app.SpecialEffectsController$Operation$Type r15 = androidx.fragment.app.SpecialEffectsController.Operation.Type.ADD
            boolean r10 = r10.equals(r15)
            if (r10 == 0) goto L_0x0269
            r4.addAll(r2)
            if (r8 == 0) goto L_0x0266
            r11.setEpicenter((java.lang.Object) r6, (android.graphics.Rect) r5)
        L_0x0266:
            r2 = r20
            goto L_0x026e
        L_0x0269:
            r2 = r20
            r11.setEpicenter((java.lang.Object) r6, (android.view.View) r2)
        L_0x026e:
            boolean r10 = r14.isOverlapAllowed()
            if (r10 == 0) goto L_0x027a
            r10 = 0
            java.lang.Object r7 = r11.mergeTransitionsTogether(r7, r6, r10)
            goto L_0x027f
        L_0x027a:
            r10 = 0
            java.lang.Object r9 = r11.mergeTransitionsTogether(r9, r6, r10)
        L_0x027f:
            r6 = r24
            r20 = r2
            r10 = r17
            r15 = r21
            r2 = r26
            goto L_0x01e6
        L_0x028b:
            r17 = r10
            java.lang.Object r1 = r11.mergeTransitionsInSequence(r7, r9, r15)
            java.util.Iterator r2 = r23.iterator()
        L_0x0295:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x02bc
            java.lang.Object r3 = r2.next()
            androidx.fragment.app.DefaultSpecialEffectsController$TransitionInfo r3 = (androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo) r3
            java.lang.Object r5 = r3.getTransition()
            if (r5 == 0) goto L_0x0295
            androidx.fragment.app.SpecialEffectsController$Operation r5 = r3.getOperation()
            androidx.fragment.app.Fragment r5 = r5.getFragment()
            androidx.core.os.CancellationSignal r6 = r3.getSignal()
            androidx.fragment.app.DefaultSpecialEffectsController$7 r7 = new androidx.fragment.app.DefaultSpecialEffectsController$7
            r7.<init>(r3)
            r11.setListenerForTransitionEnd(r5, r1, r6, r7)
            goto L_0x0295
        L_0x02bc:
            r2 = 4
            androidx.fragment.app.FragmentTransition.setViewVisibility(r4, r2)
            java.util.ArrayList r9 = r11.prepareSetNameOverridesReordered(r13)
            android.view.ViewGroup r2 = r22.getContainer()
            r11.beginDelayedTransition(r2, r1)
            android.view.ViewGroup r6 = r22.getContainer()
            r5 = r11
            r7 = r12
            r8 = r13
            r10 = r17
            r5.setNameOverridesReordered(r6, r7, r8, r9, r10)
            r0 = 0
            androidx.fragment.app.FragmentTransition.setViewVisibility(r4, r0)
            r11.swapSharedElementTargets(r15, r12, r13)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.DefaultSpecialEffectsController.startTransitions(java.util.List, boolean, androidx.fragment.app.SpecialEffectsController$Operation, androidx.fragment.app.SpecialEffectsController$Operation):void");
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
        View view = operation.getFragment().mView;
        int i = C01178.f4x8b812b9a[operation.getType().ordinal()];
        if (i == 1) {
            view.setVisibility(8);
        } else if (i == 2) {
            getContainer().removeView(view);
        } else if (i == 3 || i == 4) {
            view.setVisibility(0);
        }
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
            boolean z3;
            Object obj2;
            this.mOperation = operation;
            this.mSignal = cancellationSignal;
            if (operation.getType() == SpecialEffectsController.Operation.Type.ADD || operation.getType() == SpecialEffectsController.Operation.Type.SHOW) {
                if (z) {
                    obj = operation.getFragment().getReenterTransition();
                } else {
                    obj = operation.getFragment().getEnterTransition();
                }
                this.mTransition = obj;
                if (z) {
                    z3 = operation.getFragment().getAllowEnterTransitionOverlap();
                } else {
                    z3 = operation.getFragment().getAllowReturnTransitionOverlap();
                }
                this.mOverlapAllowed = z3;
            } else {
                if (z) {
                    obj2 = operation.getFragment().getReturnTransition();
                } else {
                    obj2 = operation.getFragment().getExitTransition();
                }
                this.mTransition = obj2;
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

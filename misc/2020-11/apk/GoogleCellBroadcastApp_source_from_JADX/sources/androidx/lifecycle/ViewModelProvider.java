package androidx.lifecycle;

public class ViewModelProvider {
    private final Factory mFactory;
    private final ViewModelStore mViewModelStore;

    public interface Factory {
        <T extends ViewModel> T create(Class<T> cls);
    }

    static class OnRequeryFactory {
        /* access modifiers changed from: package-private */
        public void onRequery(ViewModel viewModel) {
        }

        OnRequeryFactory() {
        }
    }

    static abstract class KeyedFactory extends OnRequeryFactory implements Factory {
        public abstract <T extends ViewModel> T create(String str, Class<T> cls);

        KeyedFactory() {
        }

        public <T extends ViewModel> T create(Class<T> cls) {
            throw new UnsupportedOperationException("create(String, Class<?>) must be called on implementaions of KeyedFactory");
        }
    }

    public ViewModelProvider(ViewModelStore viewModelStore, Factory factory) {
        this.mFactory = factory;
        this.mViewModelStore = viewModelStore;
    }

    public <T extends ViewModel> T get(Class<T> cls) {
        String canonicalName = cls.getCanonicalName();
        if (canonicalName != null) {
            return get("androidx.lifecycle.ViewModelProvider.DefaultKey:" + canonicalName, cls);
        }
        throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
    }

    public <T extends ViewModel> T get(String str, Class<T> cls) {
        T t;
        T t2 = this.mViewModelStore.get(str);
        if (cls.isInstance(t2)) {
            Factory factory = this.mFactory;
            if (factory instanceof OnRequeryFactory) {
                ((OnRequeryFactory) factory).onRequery(t2);
            }
            return t2;
        }
        Factory factory2 = this.mFactory;
        if (factory2 instanceof KeyedFactory) {
            t = ((KeyedFactory) factory2).create(str, cls);
        } else {
            t = factory2.create(cls);
        }
        this.mViewModelStore.put(str, t);
        return t;
    }
}

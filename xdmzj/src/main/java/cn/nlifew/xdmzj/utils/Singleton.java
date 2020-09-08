package cn.nlifew.xdmzj.utils;

public abstract class Singleton<T> {
    private T mInstance;
    private boolean mShouldCreate = true;

    public final T get() {
        if (mShouldCreate) {
            synchronized (this) {
                if (mShouldCreate) {
                    mInstance = create();
                    mShouldCreate = false;
                }
            }
        }
        return mInstance;
    }

    protected abstract T create();

    @Deprecated
    public final void set(T t) {
        synchronized (this) {
            mShouldCreate = false;
            mInstance = t;
        }
    }
}

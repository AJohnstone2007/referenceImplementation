package com.sun.util.reentrant;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
public abstract class ReentrantContextProvider<K extends ReentrantContext>
{
static final byte USAGE_TL_INACTIVE = 0;
static final byte USAGE_TL_IN_USE = 1;
static final byte USAGE_CLQ = 2;
public static final int REF_HARD = 0;
public static final int REF_SOFT = 1;
public static final int REF_WEAK = 2;
private final int refType;
protected ReentrantContextProvider(final int refType) {
this.refType = refType;
}
protected abstract K newContext();
public abstract K acquire();
public abstract void release(K ctx);
@SuppressWarnings("unchecked")
protected final Reference<K> getOrCreateReference(final K ctx) {
if (ctx.reference == null) {
switch (refType) {
case REF_HARD:
ctx.reference = new HardReference<K>(ctx);
break;
case REF_SOFT:
ctx.reference = new SoftReference<K>(ctx);
break;
default:
case REF_WEAK:
ctx.reference = new WeakReference<K>(ctx);
break;
}
}
return (Reference<K>) ctx.reference;
}
static final class HardReference<V> extends WeakReference<V> {
private final V strongRef;
HardReference(final V referent) {
super(null);
this.strongRef = referent;
}
@Override
public V get() {
return strongRef;
}
}
}

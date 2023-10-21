package com.sun.util.reentrant;
import java.lang.ref.Reference;
public abstract class ReentrantContextProviderTL<K extends ReentrantContext>
extends ReentrantContextProvider<K>
{
private final ThreadLocal<Reference<K>> ctxTL
= new ThreadLocal<Reference<K>>();
private final ReentrantContextProviderCLQ<K> ctxProviderCLQ;
public ReentrantContextProviderTL(final int refType) {
this(refType, REF_WEAK);
}
public ReentrantContextProviderTL(final int refTypeTL, final int refTypeCLQ)
{
super(refTypeTL);
final ReentrantContextProviderTL<K> parent = this;
this.ctxProviderCLQ = new ReentrantContextProviderCLQ<K>(refTypeCLQ) {
@Override
protected K newContext() {
return parent.newContext();
}
};
}
@Override
public final K acquire() {
K ctx = null;
final Reference<K> ref = ctxTL.get();
if (ref != null) {
ctx = ref.get();
}
if (ctx == null) {
ctx = newContext();
ctxTL.set(getOrCreateReference(ctx));
}
if (ctx.usage == USAGE_TL_INACTIVE) {
ctx.usage = USAGE_TL_IN_USE;
} else {
ctx = ctxProviderCLQ.acquire();
}
return ctx;
}
@Override
public final void release(final K ctx) {
if (ctx.usage == USAGE_TL_IN_USE) {
ctx.usage = USAGE_TL_INACTIVE;
} else {
ctxProviderCLQ.release(ctx);
}
}
}

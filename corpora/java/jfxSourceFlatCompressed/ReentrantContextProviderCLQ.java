package com.sun.util.reentrant;
import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentLinkedQueue;
public abstract class ReentrantContextProviderCLQ<K extends ReentrantContext>
extends ReentrantContextProvider<K>
{
private final ConcurrentLinkedQueue<Reference<K>> ctxQueue
= new ConcurrentLinkedQueue<Reference<K>>();
public ReentrantContextProviderCLQ(final int refType) {
super(refType);
}
@Override
public final K acquire() {
K ctx = null;
Reference<K> ref = null;
while ((ctx == null) && ((ref = ctxQueue.poll()) != null)) {
ctx = ref.get();
}
if (ctx == null) {
ctx = newContext();
ctx.usage = USAGE_CLQ;
}
return ctx;
}
@Override
public final void release(final K ctx) {
if (ctx.usage == USAGE_CLQ) {
ctxQueue.offer(getOrCreateReference(ctx));
}
}
}

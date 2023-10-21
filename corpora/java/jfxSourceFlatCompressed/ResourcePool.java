package com.sun.prism.impl;
public interface ResourcePool<T> {
public void freeDisposalRequestedAndCheckResources(boolean forgiveStaleLocks);
public boolean isManagerThread();
public long used();
public long managed();
public long max();
public long target();
public long origTarget();
public void setTarget(long newTarget);
public long size(T resource);
public void recordAllocated(long size);
public void recordFree(long size);
public void resourceManaged(ManagedResource<T> resource);
public void resourceFreed(ManagedResource<T> resource);
public boolean prepareForAllocation(long size);
}

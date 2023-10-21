package com.sun.scenario.effect;
public interface LockableResource {
public void lock();
public void unlock();
public boolean isLost();
}

package com.sun.javafx.runtime.async;
public interface AsyncOperationListener<V> {
public void onProgress(int progressValue, int progressMax);
public void onCompletion(V value);
public void onCancel();
public void onException(Exception e);
}

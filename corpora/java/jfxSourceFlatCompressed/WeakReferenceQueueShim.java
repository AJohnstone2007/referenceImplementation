package com.sun.javafx.util;
public class WeakReferenceQueueShim {
public static int size(WeakReferenceQueue q) {
return q.size;
}
}

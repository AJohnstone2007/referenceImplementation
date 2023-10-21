package com.sun.webkit;
public final class PageCache {
private PageCache() {
throw new AssertionError();
}
public static int getCapacity() {
return twkGetCapacity();
}
public static void setCapacity(int capacity) {
if (capacity < 0) {
throw new IllegalArgumentException(
"capacity is negative:" + capacity);
}
twkSetCapacity(capacity);
}
native private static int twkGetCapacity();
native private static void twkSetCapacity(int capacity);
}

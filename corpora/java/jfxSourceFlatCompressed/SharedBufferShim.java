package com.sun.webkit;
public class SharedBufferShim {
public static SharedBuffer createSharedBuffer() {
return new SharedBuffer();
}
public static void dispose(SharedBuffer sb) {
sb.dispose();
}
public static long size(SharedBuffer sb) {
return sb.size();
}
public static void append(SharedBuffer sb, byte[] buffer, int offset, int length) {
sb.append(buffer, offset, length);
}
public static int getSomeData(SharedBuffer sb, long position, byte[] buffer, int offset, int length) {
return sb.getSomeData(position, buffer, offset, length);
}
}

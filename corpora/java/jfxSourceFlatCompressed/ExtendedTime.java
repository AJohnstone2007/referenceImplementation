package com.sun.webkit.network;
final class ExtendedTime implements Comparable<ExtendedTime> {
private final long baseTime;
private final int subtime;
ExtendedTime(long baseTime, int subtime) {
this.baseTime = baseTime;
this.subtime = subtime;
}
static ExtendedTime currentTime() {
return new ExtendedTime(System.currentTimeMillis(), 0);
}
long baseTime() {
return baseTime;
}
int subtime() {
return subtime;
}
ExtendedTime incrementSubtime() {
return new ExtendedTime(baseTime, subtime + 1);
}
@Override
public int compareTo(ExtendedTime otherExtendedTime) {
int d = (int) (baseTime - otherExtendedTime.baseTime);
if (d != 0) {
return d;
}
return subtime - otherExtendedTime.subtime;
}
@Override
public String toString() {
return "[baseTime=" + baseTime + ", subtime=" + subtime + "]";
}
}

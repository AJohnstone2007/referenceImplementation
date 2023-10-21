package com.sun.webkit.network;
public class ExtendedTimeShim {
ExtendedTime time;
ExtendedTimeShim(ExtendedTime t) {
time = t;
}
public ExtendedTimeShim(long baseTime, int subtime) {
this(new ExtendedTime(baseTime, subtime));
}
ExtendedTime getExtendedTime() {
return time;
}
public boolean equals(ExtendedTimeShim s) {
if (s == null)
return time.equals(null);
else
return time.equals(s.time);
}
public static ExtendedTimeShim currentTime() {
return new ExtendedTimeShim(ExtendedTime.currentTime());
}
public long baseTime() {
return time.baseTime();
}
}

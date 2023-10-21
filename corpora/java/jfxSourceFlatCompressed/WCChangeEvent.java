package com.sun.webkit.event;
public final class WCChangeEvent {
private final Object source;
public WCChangeEvent(Object source) {
if (source == null) {
throw new IllegalArgumentException("null source");
}
this.source = source;
}
public Object getSource() {
return source;
}
@Override
public String toString() {
return getClass().getName() + "[source=" + source + "]";
}
}

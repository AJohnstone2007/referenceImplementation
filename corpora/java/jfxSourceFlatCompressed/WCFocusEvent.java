package com.sun.webkit.event;
import java.lang.annotation.Native;
public final class WCFocusEvent {
@Native public final static int WINDOW_ACTIVATED = 0;
@Native public final static int WINDOW_DEACTIVATED = 1;
@Native public final static int FOCUS_GAINED = 2;
@Native public final static int FOCUS_LOST = 3;
@Native public final static int UNKNOWN = -1;
@Native public final static int FORWARD = 0;
@Native public final static int BACKWARD = 1;
private final int id;
private final int direction;
public WCFocusEvent(int id, int direction) {
this.id = id;
this.direction = direction;
}
public int getID() { return id; }
public int getDirection() { return direction; }
@Override
public String toString() {
return "WCFocusEvent(" + id + ", " + direction + ")";
}
}

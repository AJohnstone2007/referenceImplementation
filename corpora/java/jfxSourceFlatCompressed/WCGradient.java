package com.sun.webkit.graphics;
import com.sun.prism.paint.Color;
public abstract class WCGradient<G> {
public static final int PAD = 1;
public static final int REFLECT = 2;
public static final int REPEAT = 3;
private int spreadMethod = PAD;
private boolean proportional;
void setSpreadMethod(int spreadMethod) {
if (spreadMethod != REFLECT && spreadMethod != REPEAT) {
spreadMethod = PAD;
}
this.spreadMethod = spreadMethod;
}
public int getSpreadMethod() {
return this.spreadMethod;
}
void setProportional(boolean proportional) {
this.proportional = proportional;
}
public boolean isProportional() {
return this.proportional;
}
protected abstract void addStop(Color color, float offset);
public abstract G getPlatformGradient();
}

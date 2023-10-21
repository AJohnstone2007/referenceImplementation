package com.sun.scenario.effect.impl.prism;
import java.util.Map;
import java.util.WeakHashMap;
import com.sun.glass.ui.Screen;
import com.sun.scenario.effect.FilterContext;
public class PrFilterContext extends FilterContext {
private static Screen defaultScreen;
private static final Map<Screen, PrFilterContext> ctxMap =
new WeakHashMap<Screen, PrFilterContext>();
private static PrFilterContext printerFilterContext = null;
public static PrFilterContext getPrinterContext(Object resourceFactory) {
if (printerFilterContext == null) {
printerFilterContext = new PrFilterContext(resourceFactory);
}
return printerFilterContext;
}
private PrFilterContext swinstance;
private boolean forceSW;
private PrFilterContext(Object screen) {
super(screen);
}
public static PrFilterContext getInstance(Screen screen) {
if (screen == null) {
throw new IllegalArgumentException("Screen must be non-null");
}
PrFilterContext fctx = ctxMap.get(screen);
if (fctx == null) {
fctx = new PrFilterContext(screen);
ctxMap.put(screen, fctx);
}
return fctx;
}
public static PrFilterContext getDefaultInstance() {
if (defaultScreen == null) {
defaultScreen = Screen.getMainScreen();
}
return getInstance(defaultScreen);
}
public PrFilterContext getSoftwareInstance() {
if (swinstance == null) {
if (forceSW) {
swinstance = this;
} else {
swinstance = new PrFilterContext(getReferent());
swinstance.forceSW = true;
}
}
return swinstance;
}
public boolean isForceSoftware() {
return forceSW;
}
private static int hashCode(boolean value) {
return value ? 1231 : 1237;
}
@Override
public int hashCode() {
return getReferent().hashCode() ^ hashCode(forceSW);
}
@Override
public boolean equals(Object o) {
if (!(o instanceof PrFilterContext)) {
return false;
}
PrFilterContext pfctx = (PrFilterContext) o;
return (this.getReferent().equals(pfctx.getReferent()) &&
this.forceSW == pfctx.forceSW);
}
}

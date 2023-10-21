package com.sun.scenario.effect.impl;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.javafx.PlatformUtil;
import com.sun.scenario.effect.FilterContext;
class RendererFactory {
private static String rootPkg = Renderer.rootPkg;
private static boolean tryRSL = true;
private static boolean trySIMD = false;
private static boolean tryJOGL = PlatformUtil.isMac();
private static boolean tryPrism = true;
static {
try {
if ("false".equals(System.getProperty("decora.rsl"))) {
tryRSL = false;
}
if ("false".equals(System.getProperty("decora.simd"))) {
trySIMD = false;
}
String tryJOGLProp = System.getProperty("decora.jogl");
if (tryJOGLProp != null) {
tryJOGL = Boolean.parseBoolean(tryJOGLProp);
}
if ("false".equals(System.getProperty("decora.prism"))) {
tryPrism = false;
}
} catch (SecurityException ignore) {
}
}
private static boolean isRSLFriendly(Class klass) {
if (klass.getName().equals("sun.java2d.pipe.hw.AccelGraphicsConfig")) {
return true;
}
boolean rsl = false;
for (Class iface : klass.getInterfaces()) {
if (isRSLFriendly(iface)) {
rsl = true;
break;
}
}
return rsl;
}
private static boolean isRSLAvailable(FilterContext fctx) {
return isRSLFriendly(fctx.getReferent().getClass());
}
private static Renderer createRSLRenderer(FilterContext fctx) {
try {
Class klass = Class.forName(rootPkg + ".impl.j2d.rsl.RSLRenderer");
Method m = klass.getMethod("createRenderer",
new Class[] { FilterContext.class });
return (Renderer)m.invoke(null, new Object[] { fctx });
} catch (Throwable e) {}
return null;
}
private static Renderer createJOGLRenderer(FilterContext fctx) {
if (tryJOGL) {
try {
Class klass = Class.forName(rootPkg + ".impl.j2d.jogl.JOGLRenderer");
Method m = klass.getMethod("createRenderer",
new Class[] { FilterContext.class });
return (Renderer)m.invoke(null, new Object[] { fctx });
} catch (Throwable e) {}
}
return null;
}
private static Renderer createPrismRenderer(FilterContext fctx) {
if (tryPrism) {
try {
Class klass = Class.forName(rootPkg + ".impl.prism.PrRenderer");
Method m = klass.getMethod("createRenderer",
new Class[] { FilterContext.class });
return (Renderer)m.invoke(null, new Object[] { fctx });
} catch (Throwable e) {
e.printStackTrace();
}
}
return null;
}
private static Renderer getSSERenderer() {
if (trySIMD) {
try {
Class klass = Class.forName(rootPkg + ".impl.j2d.J2DSWRenderer");
Method m = klass.getMethod("getSSEInstance", (Class[])null);
Renderer sseRenderer = (Renderer)m.invoke(null, (Object[])null);
if (sseRenderer != null) {
return sseRenderer;
}
} catch (Throwable e) {e.printStackTrace();}
trySIMD = false;
}
return null;
}
private static Renderer getJavaRenderer() {
try {
Class klass = Class.forName(rootPkg + ".impl.prism.sw.PSWRenderer");
Class screenClass = Class.forName("com.sun.glass.ui.Screen");
Method m = klass.getMethod("createJSWInstance",
new Class[] { screenClass });
Renderer jswRenderer =
(Renderer)m.invoke(null, new Object[] { null } );
if (jswRenderer != null) {
return jswRenderer;
}
} catch (Throwable e) {e.printStackTrace();}
return null;
}
private static Renderer getJavaRenderer(FilterContext fctx) {
try {
Class klass = Class.forName(rootPkg + ".impl.prism.sw.PSWRenderer");
Method m = klass.getMethod("createJSWInstance",
new Class[] { FilterContext.class });
Renderer jswRenderer =
(Renderer)m.invoke(null, new Object[] { fctx } );
if (jswRenderer != null) {
return jswRenderer;
}
} catch (Throwable e) {}
return null;
}
static Renderer getSoftwareRenderer() {
Renderer r = getSSERenderer();
if (r == null) {
r = getJavaRenderer();
}
return r;
}
@SuppressWarnings("removal")
static Renderer createRenderer(final FilterContext fctx) {
return AccessController.doPrivileged((PrivilegedAction<Renderer>) () -> {
Renderer r = null;
String klassName = fctx.getClass().getName();
String simpleName = klassName.substring(klassName.lastIndexOf(".") + 1);
if (simpleName.equals("PrFilterContext") && tryPrism) {
r = createPrismRenderer(fctx);
}
if (r == null && tryRSL && isRSLAvailable(fctx)) {
r = createRSLRenderer(fctx);
}
if (r == null && tryJOGL) {
r = createJOGLRenderer(fctx);
}
if (r == null && trySIMD) {
r = getSSERenderer();
}
if (r == null) {
r = getJavaRenderer(fctx);
}
return r;
});
}
}

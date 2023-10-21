package com.sun.prism.es2;
import com.sun.glass.ui.monocle.AcceleratedScreen;
import com.sun.prism.paint.Color;
import java.security.AccessController;
import java.security.PrivilegedAction;
class MonocleGLDrawable extends GLDrawable {
@SuppressWarnings("removal")
private static final boolean transparentFramebuffer =
AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> Boolean.getBoolean("com.sun.javafx.transparentFramebuffer"));
AcceleratedScreen accScreen;
MonocleGLDrawable(GLPixelFormat pixelFormat, AcceleratedScreen accScreen) {
super(0L, pixelFormat);
this.accScreen = accScreen;
}
MonocleGLDrawable(long nativeWindow, GLPixelFormat pixelFormat,
AcceleratedScreen accScreen) {
super(nativeWindow, pixelFormat);
this.accScreen = accScreen;
}
@Override
boolean swapBuffers(GLContext glCtx) {
boolean retval = accScreen.swapBuffers();
glCtx.clearBuffers(
transparentFramebuffer ? Color.TRANSPARENT : Color.BLACK,
true, true, true);
return retval;
}
}

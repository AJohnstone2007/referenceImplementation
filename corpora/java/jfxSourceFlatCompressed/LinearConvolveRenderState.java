package com.sun.scenario.effect.impl.state;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.geom.Rectangle;
import com.sun.scenario.effect.Color4f;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.EffectPeer;
import com.sun.scenario.effect.impl.Renderer;
import java.nio.FloatBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
public abstract class LinearConvolveRenderState implements RenderState {
public static final int MAX_COMPILED_KERNEL_SIZE = 128;
public static final int MAX_KERNEL_SIZE;
static final float MIN_EFFECT_RADIUS = 1.0f / 256.0f;
static final float[] BLACK_COMPONENTS =
Color4f.BLACK.getPremultipliedRGBComponents();
static {
final int defSize = PlatformUtil.isEmbedded() ? 64 : MAX_COMPILED_KERNEL_SIZE;
@SuppressWarnings("removal")
int size = AccessController.doPrivileged(
(PrivilegedAction<Integer>) () -> Integer.getInteger(
"decora.maxLinearConvolveKernelSize", defSize));
if (size > MAX_COMPILED_KERNEL_SIZE) {
System.out.println("Clamping maxLinearConvolveKernelSize to "
+ MAX_COMPILED_KERNEL_SIZE);
size = MAX_COMPILED_KERNEL_SIZE;
}
MAX_KERNEL_SIZE = size;
}
public enum PassType {
HORIZONTAL_CENTERED,
VERTICAL_CENTERED,
GENERAL_VECTOR,
};
public static int getPeerSize(int ksize) {
if (ksize < 32) return ((ksize + 3) & (~3));
if (ksize <= MAX_KERNEL_SIZE) return ((ksize + 31) & (~31));
throw new RuntimeException("No peer available for kernel size: "+ksize);
}
static boolean nearZero(float v, int size) {
return (Math.abs(v * size) < 1.0/512.0);
}
static boolean nearOne(float v, int size) {
return (Math.abs(v * size - size) < 1.0/512.0);
}
public abstract boolean isShadow();
public abstract Color4f getShadowColor();
public abstract int getInputKernelSize(int pass);
public abstract boolean isNop();
public abstract ImageData validatePassInput(ImageData src, int pass);
public abstract boolean isPassNop();
public EffectPeer<? extends LinearConvolveRenderState>
getPassPeer(Renderer r, FilterContext fctx)
{
if (isPassNop()) {
return null;
}
int ksize = getPassKernelSize();
int psize = getPeerSize(ksize);
String opname = isShadow() ? "LinearConvolveShadow" : "LinearConvolve";
return r.getPeerInstance(fctx, opname, psize);
}
public abstract Rectangle getPassResultBounds(Rectangle srcdimension,
Rectangle outputClip);
public PassType getPassType() {
return PassType.GENERAL_VECTOR;
}
public abstract FloatBuffer getPassWeights();
public abstract int getPassWeightsArrayLength();
public abstract float[] getPassVector();
public abstract float[] getPassShadowColorComponents();
public abstract int getPassKernelSize();
}

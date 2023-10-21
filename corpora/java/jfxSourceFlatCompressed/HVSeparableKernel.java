package com.sun.scenario.effect.impl.state;
import com.sun.javafx.geom.Rectangle;
public abstract class HVSeparableKernel extends LinearConvolveKernel {
@Override
public final Rectangle getResultBounds(Rectangle srcdimension, int pass) {
int ksize = getKernelSize(pass);
Rectangle ret = new Rectangle(srcdimension);
if (pass == 0) {
ret.grow(ksize/2, 0);
} else {
ret.grow(0, ksize/2);
}
return ret;
}
}

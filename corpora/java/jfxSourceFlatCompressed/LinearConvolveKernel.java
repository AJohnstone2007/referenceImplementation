package com.sun.scenario.effect.impl.state;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
public abstract class LinearConvolveKernel {
public boolean isShadow() {
return false;
}
public boolean isNop() {
return false;
}
public abstract Rectangle getResultBounds(Rectangle srcdimension, int pass);
public abstract int getKernelSize(int pass);
public abstract LinearConvolveRenderState getRenderState(BaseTransform filtertx);
}

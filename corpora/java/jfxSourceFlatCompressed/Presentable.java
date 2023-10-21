package com.sun.prism;
import com.sun.javafx.geom.Rectangle;
public interface Presentable extends RenderTarget {
public boolean lockResources(PresentableState pState);
public boolean prepare(Rectangle dirtyregion);
public boolean present();
public float getPixelScaleFactorX();
public float getPixelScaleFactorY();
}

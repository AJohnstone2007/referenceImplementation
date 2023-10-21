package com.sun.prism;
import com.sun.javafx.geom.Rectangle;
public interface ReadbackGraphics extends Graphics {
public boolean canReadBack();
public RTTexture readBack(Rectangle view);
public void releaseReadBackBuffer(RTTexture view);
}

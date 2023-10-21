package com.sun.prism;
import com.sun.glass.ui.Screen;
public interface RenderTarget extends Surface {
public Screen getAssociatedScreen();
public Graphics createGraphics();
public boolean isOpaque();
public void setOpaque(boolean opaque);
public boolean isMSAA();
}

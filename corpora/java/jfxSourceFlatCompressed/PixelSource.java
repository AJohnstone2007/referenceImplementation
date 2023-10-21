package com.sun.prism;
import com.sun.glass.ui.Pixels;
public interface PixelSource {
public Pixels getLatestPixels();
public void doneWithPixels(Pixels used);
public void skipLatestPixels();
}

package com.sun.marlin;
public interface MarlinRenderer extends DPathConsumer2D {
public MarlinRenderer init(final int pix_boundsX, final int pix_boundsY,
final int pix_boundsWidth, final int pix_boundsHeight,
final int windingRule);
public void dispose();
public int getOutpixMinX();
public int getOutpixMaxX();
public int getOutpixMinY();
public int getOutpixMaxY();
public void produceAlphas(MarlinAlphaConsumer ac);
public double getOffsetX();
public double getOffsetY();
}

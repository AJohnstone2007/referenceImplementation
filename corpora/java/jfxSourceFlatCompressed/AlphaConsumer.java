package com.sun.openpisces;
public interface AlphaConsumer {
public int getOriginX();
public int getOriginY();
public int getWidth();
public int getHeight();
public void setMaxAlpha(int maxalpha);
public void setAndClearRelativeAlphas(int alphaDeltas[], int pix_y,
int firstdelta, int lastdelta);
}

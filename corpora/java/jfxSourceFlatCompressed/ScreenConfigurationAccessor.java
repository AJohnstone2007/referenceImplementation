package com.sun.javafx.tk;
public interface ScreenConfigurationAccessor {
public int getMinX(Object obj);
public int getMinY(Object obj);
public int getWidth(Object obj);
public int getHeight(Object obj);
public int getVisualMinX(Object obj);
public int getVisualMinY(Object obj);
public int getVisualHeight(Object obj);
public int getVisualWidth(Object obj);
public float getDPI(Object obj);
public float getRecommendedOutputScaleX(Object obj);
public float getRecommendedOutputScaleY(Object obj);
}

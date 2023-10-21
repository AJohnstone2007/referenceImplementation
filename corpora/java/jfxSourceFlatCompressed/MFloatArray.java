package com.javafx.experiments.importers.maya.values;
public interface MFloatArray extends MData {
public void set(int index, float x);
public float[] get();
public float get(int index);
public int getSize();
}

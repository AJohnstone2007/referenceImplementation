package com.javafx.experiments.importers.maya.values;
public interface MFloat3Array extends MData {
public void set(int index, float x, float y, float z);
public float[] get();
public int getSize();
}

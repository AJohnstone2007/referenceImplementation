package com.javafx.experiments.shape3d.symbolic;
public abstract class SymbolicPointArray {
final public float[] data;
final public int numPoints;
static final int NUM_COMPONENTS_PER_POINT = 3;
protected SymbolicPointArray(float[] data) {
this.data = data;
this.numPoints = data.length / NUM_COMPONENTS_PER_POINT;
}
public abstract void update();
}

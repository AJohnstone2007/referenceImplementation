package com.javafx.experiments.importers.maya.values;
public interface MNurbsCurve extends MData {
public int getDegree();
public int getSpans();
public int getForm();
public boolean isRational();
public int getDimension();
public int getNumKnots();
public float[] getKnots();
public int getNumCVs();
public float[] getCVs();
}

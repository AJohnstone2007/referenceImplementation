package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.types.MNurbsCurveType;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.MNurbsCurve;
public class MNurbsCurveImpl extends MDataImpl implements MNurbsCurve {
int degree;
int spans;
int form;
boolean rational;
int dimension;
int numKnots;
float[] knots;
int numCvs;
float[] cvs;
public MNurbsCurveImpl(MNurbsCurveType type) {
super(type);
}
public MData getData(int start, int end) {
return this;
}
public int getDegree() {
return degree;
}
public int getSpans() {
return spans;
}
public int getForm() {
return form;
}
public boolean isRational() {
return rational;
}
public int getDimension() {
return dimension;
}
public int getNumKnots() {
return numKnots;
}
public float[] getKnots() {
return knots;
}
public int getNumCVs() {
return numCvs;
}
public float[] getCVs() {
return cvs;
}
public void parse(Iterator<String> values) {
degree = Integer.parseInt(values.next());
spans = Integer.parseInt(values.next());
form = Integer.parseInt(values.next());
String tok = values.next();
dimension = Integer.parseInt(values.next());
numKnots = Integer.parseInt(values.next());
knots = new float[numKnots];
for (int i = 0; i < numKnots; i++) {
knots[i] = Float.parseFloat(values.next());
}
numCvs = Integer.parseInt(values.next());
cvs = new float[numCvs * dimension];
for (int i = 0; i < cvs.length; i++) {
cvs[i] = Float.parseFloat(values.next());
}
}
}

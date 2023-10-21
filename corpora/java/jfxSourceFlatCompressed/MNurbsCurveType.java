package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MNurbsCurveImpl;
public class MNurbsCurveType extends MDataType {
public static final String NAME = "nurbsCurve";
public MNurbsCurveType(MEnv env) {
super(env, NAME);
}
public MData createData() {
return new MNurbsCurveImpl(this);
}
}

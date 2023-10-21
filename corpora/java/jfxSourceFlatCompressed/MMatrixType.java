package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.MFloatArray;
import com.javafx.experiments.importers.maya.values.impl.MFloatArrayImpl;
public class MMatrixType extends MFloatArrayType {
public static final String NAME = "matrix";
public MMatrixType(MEnv env) {
super(env, NAME);
}
public MData createData() {
MFloatArray array = new MFloatArrayImpl(this);
array.setSize(16);
array.set(0, 1);
array.set(5, 1);
array.set(10, 1);
array.set(15, 1);
return array;
}
}

package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MFloatArrayImpl;
public class MFloatArrayType extends MDataType {
public static final String NAME = "float[]";
public MFloatArrayType(MEnv env) {
this(env, NAME);
}
public MFloatArrayType(MEnv env, String name) {
super(env, name);
}
public MData createData() {
return new MFloatArrayImpl(this);
}
}

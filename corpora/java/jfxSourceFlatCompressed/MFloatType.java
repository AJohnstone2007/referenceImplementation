package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MFloatImpl;
public class MFloatType extends MDataType {
public static final String NAME = "float";
public MFloatType(MEnv env) {
super(env, NAME);
}
public MData createData() {
return new MFloatImpl(this);
}
}

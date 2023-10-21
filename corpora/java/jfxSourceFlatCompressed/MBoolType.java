package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MBoolImpl;
public class MBoolType extends MDataType {
public static final String NAME = "bool";
public MBoolType(MEnv env) {
super(env, NAME);
}
public MData createData() {
return new MBoolImpl(this);
}
}

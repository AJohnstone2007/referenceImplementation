package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MInt3ArrayImpl;
public class MInt3ArrayType extends MDataType {
public static final String NAME = "int3[]";
public MInt3ArrayType(MEnv env) {
super(env, NAME);
}
public MData createData() {
return new MInt3ArrayImpl(this);
}
}

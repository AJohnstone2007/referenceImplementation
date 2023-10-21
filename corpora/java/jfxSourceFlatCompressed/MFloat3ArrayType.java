package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MFloat3ArrayImpl;
public class MFloat3ArrayType extends MDataType {
public static final String NAME = "float3[]";
public MFloat3ArrayType(MEnv env) {
super(env, NAME);
}
public MData createData() {
return new MFloat3ArrayImpl(this);
}
}

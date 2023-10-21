package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MFloat3Impl;
public class MFloat3Type extends MDataType {
public static final String NAME = "float3";
public MFloat3Type(MEnv env) {
super(env, NAME);
}
public MData createData() {
return new MFloat3Impl(this);
}
}

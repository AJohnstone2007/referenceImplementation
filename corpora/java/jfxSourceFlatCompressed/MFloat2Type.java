package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MFloat2Impl;
public class MFloat2Type extends MDataType {
public static final String NAME = "float2";
public MFloat2Type(MEnv env) {
super(env, NAME);
}
public MData createData() {
return new MFloat2Impl(this);
}
}

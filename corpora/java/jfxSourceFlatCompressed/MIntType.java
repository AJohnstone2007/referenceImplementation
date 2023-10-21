package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MIntImpl;
public class MIntType extends MDataType {
public static final String NAME = "int";
public MIntType(MEnv env) {
super(env, NAME);
}
public MData createData() {
return new MIntImpl(this);
}
}

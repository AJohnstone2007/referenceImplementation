package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MPointerImpl;
public class MPointerType extends MDataType {
public static final String NAME = "Message";
public MPointerType(MEnv env) {
super(env, NAME);
}
public MData createData() {
return new MPointerImpl(this);
}
}

package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MComponentListImpl;
public class MComponentListType extends MDataType {
public static final String NAME = "componentList";
public MComponentListType(MEnv env) {
super(env, NAME);
}
public MData createData() {
return new MComponentListImpl(this);
}
}

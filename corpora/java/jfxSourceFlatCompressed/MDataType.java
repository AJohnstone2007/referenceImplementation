package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.MObject;
import com.javafx.experiments.importers.maya.values.MData;
public abstract class MDataType extends MObject {
public MDataType(MEnv env, String name) {
super(env, name);
}
public abstract MData createData();
public void accept(MEnv.Visitor visitor) {
visitor.visitDataType(this);
}
}

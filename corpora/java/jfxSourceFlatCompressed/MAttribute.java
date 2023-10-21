package com.javafx.experiments.importers.maya;
import com.javafx.experiments.importers.maya.types.MDataType;
public class MAttribute extends MObject {
String shortName;
MDataType dataType;
MNodeType declaringNodeType;
int childIndex = -1;
public MAttribute(
MEnv env, String name,
String shortName, MDataType type) {
super(env, name);
this.shortName = shortName;
this.dataType = type;
}
public MNodeType getContext() {
return declaringNodeType;
}
public void accept(MEnv.Visitor visitor) {
visitor.visitAttribute(this);
}
public String getShortName() {
return shortName;
}
public MDataType getType() {
return dataType;
}
public int addChild() {
return ++childIndex;
}
}

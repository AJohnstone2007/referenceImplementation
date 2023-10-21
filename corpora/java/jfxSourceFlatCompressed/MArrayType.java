package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MArrayImpl;
public class MArrayType extends MDataType {
MDataType elementType;
public MArrayType(MEnv env, MDataType elementType) {
super(env, elementType.getName() + "[]");
this.elementType = elementType;
}
public MDataType getElementType() {
return elementType;
}
public MData createData() {
return new MArrayImpl(this);
}
}

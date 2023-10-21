package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MAttributeAliasImpl;
public class MAttributeAliasType extends MDataType {
public static final String NAME = "attributeAlias";
public MAttributeAliasType(MEnv env) {
this(env, NAME);
}
public MAttributeAliasType(MEnv env, String name) {
super(env, name);
}
public MData createData() {
return new MAttributeAliasImpl(this);
}
}

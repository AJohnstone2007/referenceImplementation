package com.javafx.experiments.importers.maya.types;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.impl.MCharacterMappingImpl;
public class MCharacterMappingType extends MDataType {
public static final String NAME = "characterMapping";
public MCharacterMappingType(MEnv env) {
this(env, NAME);
}
public MCharacterMappingType(MEnv env, String name) {
super(env, name);
}
public MData createData() {
return new MCharacterMappingImpl(this);
}
}

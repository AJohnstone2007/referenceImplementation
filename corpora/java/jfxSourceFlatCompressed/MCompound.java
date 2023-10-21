package com.javafx.experiments.importers.maya.values;
import com.javafx.experiments.importers.maya.types.MCompoundType;
public interface MCompound extends MData {
public MCompoundType getCompoundType();
public MData getFieldData(int fieldIndex);
public MData getFieldData(String fieldName);
public void set(int fieldIndex, MData value);
public void set(String fieldName, MData data);
}

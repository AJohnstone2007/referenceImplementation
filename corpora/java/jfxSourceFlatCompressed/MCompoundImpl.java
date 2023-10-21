package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.types.MCompoundType;
import com.javafx.experiments.importers.maya.types.MDataType;
import com.javafx.experiments.importers.maya.values.MCompound;
import com.javafx.experiments.importers.maya.values.MData;
public class MCompoundImpl extends MDataImpl implements MCompound {
private MData[] fieldData;
public MCompoundImpl(MCompoundType type) {
super(type);
fieldData = new MData[type.getNumFields()];
for (int i = 0; i < fieldData.length; i++) {
MDataType dt = getCompoundType().getField(i).getType();
if (dt != null) {
fieldData[i] = dt.createData();
} else {
}
}
}
public MCompoundType getCompoundType() {
return (MCompoundType) getType();
}
public MData getFieldData(String fieldName) {
return getFieldData(getCompoundType().getFieldIndex(fieldName));
}
public MData getFieldData(int fieldIndex) {
if (fieldIndex < 0) {
return null;
}
return fieldData[fieldIndex];
}
public void set(int fieldIndex, MData value) {
fieldData[fieldIndex] = value;
}
public void set(String fieldName, MData data) {
set(getCompoundType().getFieldIndex(fieldName), data);
}
public void parse(Iterator<String> data) {
for (int i = 0; i < getCompoundType().getNumFields(); i++) {
MData fdata = getFieldData(i);
if (fdata != null) {
fdata.parse(data);
}
}
}
public String toString() {
String result = "";
for (int i = 0; i < fieldData.length; i++) {
result += getCompoundType().getField(i).getName() + ":\t" + fieldData[i] + "\n";
}
return result;
}
}

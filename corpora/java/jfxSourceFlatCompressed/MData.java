package com.javafx.experiments.importers.maya.values;
import java.util.Iterator;
import java.util.List;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.types.MDataType;
public interface MData {
public MEnv getEnv();
public MDataType getType();
public void setSize(int size);
public void parse(String field, List<String> values);
public void parse(List<String> values);
public void parse(Iterator<String> iter);
public MData getData(String path);
public MData getFieldData(String name);
public MData getData(int index);
public MData getData(int start, int end);
}

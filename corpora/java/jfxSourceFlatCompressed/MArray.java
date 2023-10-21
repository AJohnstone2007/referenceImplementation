package com.javafx.experiments.importers.maya.values;
import java.util.List;
public interface MArray extends MData {
public void set(int index, MData data);
public List<MData> get();
public int getSize();
}

package com.javafx.experiments.importers.maya.values;
public interface MCharacterMapping extends MData {
public interface Entry {
public String getKey();
public int getSourceIndex();
public int getTargetIndex();
}
public Entry[] getMapping();
}

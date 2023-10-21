package com.javafx.experiments.importers.maya.values;
import com.javafx.experiments.importers.maya.MNode;
import com.javafx.experiments.importers.maya.MPath;
public interface MPointer extends MData {
public void setTarget(MPath path);
public MPath getTarget();
public MNode getTargetNode();
}

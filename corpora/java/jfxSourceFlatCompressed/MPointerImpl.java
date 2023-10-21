package com.javafx.experiments.importers.maya.values.impl;
import java.util.Iterator;
import com.javafx.experiments.importers.maya.MNode;
import com.javafx.experiments.importers.maya.MPath;
import com.javafx.experiments.importers.maya.types.MPointerType;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.MPointer;
public class MPointerImpl extends MDataImpl implements MPointer {
private MPath target;
public MPointerImpl(MPointerType type) {
super(type);
}
public void setTarget(MPath path) {
target = path;
}
public MPath getTarget() {
return target;
}
public void set(MData data) {
}
public MData get() {
return target.apply();
}
public void parse(Iterator<String> iter) {
}
public String toString() {
if (target != null) {
return target.toString();
} else {
return "Null Pointer";
}
}
public MNode getTargetNode() {
return target.getTargetNode();
}
}

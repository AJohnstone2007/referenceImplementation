package com.javafx.experiments.importers.maya;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.javafx.experiments.importers.maya.types.MArrayType;
import com.javafx.experiments.importers.maya.types.MDataType;
import com.javafx.experiments.importers.maya.values.MData;
public class MNode extends MObject {
MNodeType nodeType;
boolean hasLocalType = false;
Map<String, MData> values = new HashMap();
public void createInstance(String instanceName) {
}
public MNodeType getLocalType() {
if (!hasLocalType) {
hasLocalType = true;
final MNodeType superType = nodeType;
MNodeType localType = new MNodeType(getEnv(), getFullName()) {
{
addSuperType(superType);
}
};
nodeType = localType;
}
return nodeType;
}
List<MNode> parentNodes = new ArrayList();
List<MNode> childNodes = new ArrayList();
public void addAttr(
String longName,
String shortName,
String dataType,
boolean isArray) {
addAttr(longName, shortName, dataType, isArray, null);
}
public void addAttr(
String longName,
String shortName,
String dataType,
boolean isArray,
String parentAttr) {
MDataType t = getEnv().findDataType(dataType);
if (t == null) {
}
if (isArray) {
t = new MArrayType(getEnv(), t);
}
if (parentAttr != null) {
MAttribute parent = getLocalType().getAttribute(parentAttr);
int index = parent.addChild();
getLocalType().addAlias(shortName, parent.getShortName() + "[" + index + "]");
return;
}
getLocalType().addAttribute(new MAttribute(getEnv(), longName, shortName, t));
}
public void setParentNode(MNode n) {
parentNodes.add(n);
n.childNodes.add(this);
}
public List<MNode> getParentNodes() {
return parentNodes;
}
public List<MNode> getChildNodes() {
return childNodes;
}
public MNode getChildNode(String name) {
for (MNode node : childNodes) {
if (name.equals(node.getName())) {
return node;
}
}
return null;
}
public MNode(MEnv env, MNodeType type, String name) {
super(env, name);
this.nodeType = type;
}
public boolean isInstanceOf(MNodeType t) {
return t.isAssignableFrom(getNodeType());
}
public void accept(MEnv.Visitor visitor) {
visitor.visitNode(this);
for (Map.Entry<String, MData> e : values.entrySet()) {
visitor.visitNodeAttribute(
this,
getNodeType().getAttribute(e.getKey()),
e.getValue());
}
}
public MNodeType getNodeType() {
return nodeType;
}
public String getFullName() {
String result = "";
LinkedList<MNode> path = new LinkedList();
MNode n = this;
while (true) {
path.addFirst(n);
List<MNode> p = n.getParentNodes();
if (p.size() == 0) {
break;
}
n = p.get(0);
}
for (MNode x : path) {
result += "|";
result += x.getName();
}
return result;
}
public List<MConnection> getConnectionsFrom(String attr) {
Set<MConnection> c = getEnv().getConnectionsFrom(new MPath(this, "." + attr));
List<MConnection> result = new ArrayList();
result.addAll(c);
Collections.sort(result, MConnection.SOURCE_PATH_COMPARATOR);
return result;
}
public List<MPath> getPathsConnectingFrom(String attr) {
Set<MPath> c = getEnv().getPathsConnectingFrom(new MPath(this, "." + attr));
List<MPath> result = new ArrayList();
result.addAll(c);
return result;
}
public void getConnectionsTo(String attr, Set<MConnection> result) {
getEnv().getConnectionsTo(new MPath(this, "." + attr), true, result);
}
public List<MConnection> getConnectionsTo(String attr) {
Set<MConnection> c = getEnv().getConnectionsTo(new MPath(this, "." + attr));
List<MConnection> result = new ArrayList();
result.addAll(c);
Collections.sort(result, MConnection.TARGET_PATH_COMPARATOR);
return result;
}
public List<MPath> getPathsConnectingTo(String attr) {
Set<MPath> c = getEnv().getPathsConnectingTo(new MPath(this, "." + attr));
List<MPath> result = new ArrayList();
result.addAll(c);
return result;
}
public MNode getIncomingConnectionToType(String fromAttr, String nodeType) {
return getIncomingConnectionToType(fromAttr, nodeType, true);
}
public MNode getIncomingConnectionToType(String fromAttr, String nodeType, boolean checkSubPaths) {
MNodeType t = getEnv().findNodeType(nodeType);
MPath path = new MPath(this, "." + fromAttr);
Set<MPath> c = getEnv().getPathsConnectingTo(path, checkSubPaths);
for (MPath p : c) {
if (p.getTargetNode().isInstanceOf(t)) {
return p.getTargetNode();
}
}
return null;
}
public MNode getOutgoingConnectionToType(String fromAttr, String nodeType) {
return getOutgoingConnectionToType(fromAttr, nodeType, true);
}
public MNode getOutgoingConnectionToType(String fromAttr, String nodeType, boolean checkSubPaths) {
MNodeType t = getEnv().findNodeType(nodeType);
MPath path = new MPath(this, "." + fromAttr);
Set<MPath> c = getEnv().getPathsConnectingFrom(path, checkSubPaths);
for (MPath p : c) {
if (p.getTargetNode().isInstanceOf(t)) {
return p.getTargetNode();
}
}
return null;
}
public MData getAttr(MAttribute attribute) {
MData data = values.get(attribute.getShortName());
if (data == null) {
if (data == null) {
if (attribute.getType() == null) {
return null;
}
data = attribute.getType().createData();
}
values.put(attribute.getShortName(), data);
}
return data;
}
public MData getAttrDirect(String attrName) {
MAttribute attr = getNodeType().getAttribute(attrName);
if (attr == null) {
return null;
}
return getAttr(attr);
}
public String getCanonicalName(String name) {
return getNodeType().getCanonicalName(name);
}
public MData getAttr(String name) {
MPath path = new MPath(this, name);
return path.apply();
}
public void setAttr(MAttribute attr, MData value) {
if (getNodeType().getAttribute(attr.getName()) != attr) {
}
setAttr(attr.getShortName(), value);
}
public void setAttr(String name, MData value) {
if (getNodeType().getAttribute(name) == null) {
}
values.put(name, value);
}
public Set<MConnection> getIncomingConnections() {
return getEnv().getIncomingConnections(this);
}
public Set<MConnection> getOutgoingConnections() {
return getEnv().getOutgoingConnections(this);
}
public void parent(MNode parent) {
parentNodes.add(parent);
}
}

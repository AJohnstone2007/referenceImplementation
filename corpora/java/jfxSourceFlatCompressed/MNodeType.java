package com.javafx.experiments.importers.maya;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public abstract class MNodeType extends MObject {
Map<String, MAttribute> attributes = new HashMap();
Map<String, MAttribute> attributesByShortName = new HashMap();
List<MNodeType> superTypes = new ArrayList();
Map<String, String> canonicalNames = new HashMap();
public Collection<MAttribute> getAttributes() {
return attributes.values();
}
public void addAlias(String alias, String target) {
canonicalNames.put(alias, target);
}
public String getAlias(String name) {
for (Map.Entry<String, String> e : canonicalNames.entrySet()) {
if (e.getValue().equals(name)) {
return e.getKey();
}
}
for (MNodeType type : superTypes) {
String alias = type.getAlias(name);
if (alias != null) {
return alias;
}
}
return name;
}
public String getCanonicalName(String name) {
String tmpName = toShortName(name);
String result = getCanonicalNameRecursive(name);
if (result != null) {
return result;
}
return tmpName;
}
private String getCanonicalNameRecursive(String name) {
String tmpName = toShortName(name);
String result = canonicalNames.get(tmpName);
if (result == null) {
for (MNodeType t : superTypes) {
if (t == null) {
System.out.println("missing supertype for " + getName());
}
result = t.getCanonicalNameRecursive(name);
if (result != null) {
return result;
}
}
}
return result;
}
private String toShortName(String name) {
MAttribute attr = attributes.get(name);
if (attr != null) {
return attr.getShortName();
}
return name;
}
public boolean isAssignableFrom(MNodeType other) {
if (other == this) {
return true;
}
for (MNodeType t : other.getSuperTypes()) {
if (isAssignableFrom(t)) {
return true;
}
}
return false;
}
public void accept(MEnv.Visitor visitor) {
visitor.visitNodeType(this);
for (MAttribute attr : attributes.values()) {
attr.accept(visitor);
}
}
public MNodeType(MEnv env, String name) {
super(env, name);
}
public List<MNodeType> getSuperTypes() {
return superTypes;
}
public MAttribute getAttribute(String name) {
MAttribute attr = attributes.get(name);
if (attr == null) {
attr = attributesByShortName.get(name);
}
if (attr == null) {
for (MNodeType t : superTypes) {
attr = t.getAttribute(name);
if (attr != null) {
break;
}
}
}
if (attr == null) {
String canonicalName = getCanonicalName(name);
if (!canonicalName.equals(name)) {
attr = getAttribute(canonicalName);
}
}
return attr;
}
public MNode createNode(String name) {
MNode n = doCreateNode(name);
getEnv().addNode(n);
doInitNode(n);
return n;
}
protected void initNode(MNode node) {
}
void doInitNode(MNode node) {
for (MNodeType i : superTypes) {
i.doInitNode(node);
}
initNode(node);
}
public void addAttribute(MAttribute attribute) {
attribute.declaringNodeType = this;
attributes.put(attribute.getName(), attribute);
attributesByShortName.put(attribute.getShortName(), attribute);
}
protected MNode doCreateNode(String name) {
return new MNode(getEnv(), this, name);
}
public void addSuperType(MNodeType superType) {
superTypes.add(superType);
}
}

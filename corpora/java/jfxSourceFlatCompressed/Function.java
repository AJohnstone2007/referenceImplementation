package com.sun.scenario.effect.compiler.model;
import java.util.Collections;
import java.util.List;
public class Function {
private final String name;
private final Type returnType;
private final List<Param> params;
Function(String name, Type returnType, List<Param> params) {
this.name = name;
this.returnType = returnType;
if (params != null) {
this.params = params;
} else {
this.params = Collections.emptyList();
}
}
public String getName() {
return name;
}
public Type getReturnType() {
return returnType;
}
public List<Param> getParams() {
return params;
}
@Override
public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
final Function other = (Function) obj;
if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
return false;
}
if (this.returnType != other.returnType) {
return false;
}
if (this.params != other.params && (this.params == null || !this.params.equals(other.params))) {
return false;
}
return true;
}
@Override
public int hashCode() {
int hash = 7;
hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
hash = 71 * hash + (this.returnType != null ? this.returnType.hashCode() : 0);
hash = 71 * hash + (this.params != null ? this.params.hashCode() : 0);
return hash;
}
}

package com.sun.scenario.effect.compiler.model;
public class Param {
private final String name;
private final Type type;
public Param(String name, Type type) {
if (name == null) {
throw new IllegalArgumentException("Name must be non-null");
}
if (type == null) {
throw new IllegalArgumentException("Type must be non-null");
}
this.name = name;
this.type = type;
}
public String getName() {
return name;
}
public Type getType() {
return type;
}
@Override
public boolean equals(Object obj) {
if (this == obj) {
return true;
}
if ((obj == null) || (obj.getClass() != this.getClass())) {
return false;
}
Param that = (Param)obj;
return this.name.equals(that.name) && this.type.equals(that.type);
}
@Override
public int hashCode() {
int hash = 7;
hash = 31 * hash + name.hashCode();
hash = 31 * hash + type.hashCode();
return hash;
}
}

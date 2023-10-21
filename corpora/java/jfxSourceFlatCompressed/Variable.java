package com.sun.scenario.effect.compiler.model;
import java.util.Locale;
public class Variable {
private final String name;
private final Type type;
private final Qualifier qual;
private final Precision precision;
private final int reg;
private final int arraySize;
private final Object constValue;
private final boolean isParam;
private int refCount;
Variable(String name, Type type) {
this(name, type, null, null, -1, -1, null, false);
}
Variable(String name, Type type, Qualifier qual, Precision precision,
int reg, int arraySize, Object constValue, boolean isParam)
{
if (name == null) {
throw new IllegalArgumentException("Name must be non-null");
}
if (type == null) {
throw new IllegalArgumentException("Type must be non-null");
}
this.name = name;
this.type = type;
this.qual = qual;
this.precision = precision;
this.reg = reg;
this.arraySize = arraySize;
this.constValue = constValue;
this.isParam = isParam;
}
public String getName() {
return name;
}
public Type getType() {
return type;
}
public Qualifier getQualifier() {
return qual;
}
public Precision getPrecision() {
return precision;
}
public int getReg() {
return reg;
}
public boolean isArray() {
return arraySize > 0;
}
public int getArraySize() {
return arraySize;
}
public Object getConstValue() {
return constValue;
}
public boolean isParam() {
return isParam;
}
public String getAccessorName() {
return "get" + name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
}
public void incrementRefCount() {
refCount++;
}
public boolean isReferenced() {
return refCount > 0;
}
}

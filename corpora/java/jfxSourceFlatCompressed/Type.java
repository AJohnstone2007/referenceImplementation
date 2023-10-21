package com.sun.scenario.effect.compiler.model;
import java.util.Locale;
public enum Type {
VOID (BaseType.VOID, 1),
FLOAT (BaseType.FLOAT, 1),
FLOAT2 (BaseType.FLOAT, 2),
FLOAT3 (BaseType.FLOAT, 3),
FLOAT4 (BaseType.FLOAT, 4),
INT (BaseType.INT, 1),
INT2 (BaseType.INT, 2),
INT3 (BaseType.INT, 3),
INT4 (BaseType.INT, 4),
BOOL (BaseType.BOOL, 1),
BOOL2 (BaseType.BOOL, 2),
BOOL3 (BaseType.BOOL, 3),
BOOL4 (BaseType.BOOL, 4),
SAMPLER(BaseType.SAMPLER, 1),
LSAMPLER(BaseType.SAMPLER, 1),
FSAMPLER(BaseType.SAMPLER, 1);
private final BaseType baseType;
private final int numFields;
private Type(BaseType baseType, int numFields) {
this.baseType = baseType;
this.numFields = numFields;
}
public BaseType getBaseType() {
return baseType;
}
public int getNumFields() {
return numFields;
}
public boolean isVector() {
return numFields > 1;
}
public static Type fromToken(String s) {
return valueOf(s.toUpperCase(Locale.ENGLISH));
}
@Override
public String toString() {
return name().toLowerCase(Locale.ENGLISH);
}
}

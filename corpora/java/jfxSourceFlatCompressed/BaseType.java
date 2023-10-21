package com.sun.scenario.effect.compiler.model;
import java.util.Locale;
public enum BaseType {
VOID,
FLOAT,
INT,
BOOL,
SAMPLER;
@Override
public String toString() {
return name().toLowerCase(Locale.ENGLISH);
}
}

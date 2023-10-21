package com.sun.scenario.effect.compiler.model;
import java.util.Locale;
public enum Qualifier {
CONST,
PARAM;
public static Qualifier fromToken(String s) {
return valueOf(s.toUpperCase(Locale.ENGLISH));
}
@Override
public String toString() {
return name().toLowerCase(Locale.ENGLISH);
}
}

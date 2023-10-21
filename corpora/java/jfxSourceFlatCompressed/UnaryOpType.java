package com.sun.scenario.effect.compiler.model;
public enum UnaryOpType {
INC ("++"),
DEC ("--"),
PLUS ("+"),
MINUS ("-"),
NOT ("!");
private String symbol;
private UnaryOpType(String symbol) {
this.symbol = symbol;
}
public static UnaryOpType forSymbol(String symbol) {
for (UnaryOpType ot : UnaryOpType.values()) {
if (ot.getSymbol().equals(symbol)) {
return ot;
}
}
return null;
}
public String getSymbol() {
return symbol;
}
@Override
public String toString() {
return symbol;
}
}

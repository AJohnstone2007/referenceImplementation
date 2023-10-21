package com.sun.scenario.effect.compiler.model;
public enum BinaryOpType {
ADD ("+", Op.MATH),
SUB ("-", Op.MATH),
MUL ("*", Op.MATH),
DIV ("/", Op.MATH),
EQ ("=", Op.ASSIGN),
ADDEQ("+=", Op.ASSIGN),
SUBEQ("-=", Op.ASSIGN),
MULEQ("*=", Op.ASSIGN),
DIVEQ("/=", Op.ASSIGN),
OR ("||", Op.REL),
XOR ("^^", Op.REL),
AND ("&&", Op.REL),
EQEQ ("==", Op.REL),
NEQ ("!=", Op.REL),
LTEQ ("<=", Op.REL),
GTEQ (">=", Op.REL),
LT ("<", Op.REL),
GT (">", Op.REL);
private enum Op { MATH, ASSIGN, REL }
private String symbol;
private Op op;
private BinaryOpType(String symbol, Op op) {
this.symbol = symbol;
this.op = op;
}
public static BinaryOpType forSymbol(String symbol) {
for (BinaryOpType ot : BinaryOpType.values()) {
if (ot.getSymbol().equals(symbol)) {
return ot;
}
}
return null;
}
public String getSymbol() {
return symbol;
}
public boolean isRelational() {
return (op == Op.REL);
}
public boolean isAssignment() {
return (op == Op.ASSIGN);
}
@Override
public String toString() {
return symbol;
}
}

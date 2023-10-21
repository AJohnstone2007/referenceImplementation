package com.sun.scenario.effect.compiler.tree;
public class ArrayAccessExpr extends Expr {
private final Expr expr;
private final Expr index;
public ArrayAccessExpr(Expr expr, Expr index) {
super(expr.getResultType());
this.expr = expr;
this.index = index;
}
public Expr getExpr() {
return expr;
}
public Expr getIndex() {
return index;
}
public void accept(TreeVisitor tv) {
tv.visitArrayAccessExpr(this);
}
}

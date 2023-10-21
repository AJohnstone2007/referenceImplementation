package com.sun.scenario.effect.compiler.tree;
public class ParenExpr extends Expr {
private final Expr expr;
ParenExpr(Expr expr) {
super(expr.getResultType());
this.expr = expr;
}
public Expr getExpr() {
return expr;
}
public void accept(TreeVisitor tv) {
tv.visitParenExpr(this);
}
}

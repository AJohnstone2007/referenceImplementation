package com.sun.scenario.effect.compiler.tree;
public class ExprStmt extends Stmt {
private final Expr expr;
ExprStmt(Expr expr) {
this.expr = expr;
}
public Expr getExpr() {
return expr;
}
public void accept(TreeVisitor tv) {
tv.visitExprStmt(this);
}
}

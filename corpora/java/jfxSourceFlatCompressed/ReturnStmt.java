package com.sun.scenario.effect.compiler.tree;
public class ReturnStmt extends Stmt {
private final Expr expr;
ReturnStmt(Expr expr) {
this.expr = expr;
}
public Expr getExpr() {
return expr;
}
public void accept(TreeVisitor tv) {
tv.visitReturnStmt(this);
}
}

package com.sun.scenario.effect.compiler.tree;
public class DoWhileStmt extends Stmt {
private final Stmt stmt;
private final Expr expr;
DoWhileStmt(Stmt stmt, Expr expr) {
this.stmt = stmt;
this.expr = expr;
}
public Stmt getStmt() {
return stmt;
}
public Expr getExpr() {
return expr;
}
public void accept(TreeVisitor tv) {
tv.visitDoWhileStmt(this);
}
}

package com.sun.scenario.effect.compiler.tree;
public class SelectStmt extends Stmt {
private final Expr ifExpr;
private final Stmt thenStmt;
private final Stmt elseStmt;
SelectStmt(Expr ifExpr, Stmt thenStmt, Stmt elseStmt) {
this.ifExpr = ifExpr;
this.thenStmt = thenStmt;
this.elseStmt = elseStmt;
}
public Expr getIfExpr() {
return ifExpr;
}
public Stmt getThenStmt() {
return thenStmt;
}
public Stmt getElseStmt() {
return elseStmt;
}
public void accept(TreeVisitor tv) {
tv.visitSelectStmt(this);
}
}

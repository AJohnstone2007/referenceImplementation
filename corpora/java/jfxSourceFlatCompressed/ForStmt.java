package com.sun.scenario.effect.compiler.tree;
public class ForStmt extends Stmt {
private final Stmt init;
private final Expr cond;
private final Expr expr;
private final Stmt stmt;
private final int unrollMax;
private final int unrollCheck;
ForStmt(Stmt init, Expr cond, Expr expr, Stmt stmt,
int unrollMax, int unrollCheck)
{
this.init = init;
this.cond = cond;
this.expr = expr;
this.stmt = stmt;
this.unrollMax = unrollMax;
this.unrollCheck = unrollCheck;
}
public Stmt getInit() {
return init;
}
public Expr getCondition() {
return cond;
}
public Expr getExpr() {
return expr;
}
public Stmt getStmt() {
return stmt;
}
public int getUnrollMax() {
return unrollMax;
}
public int getUnrollCheck() {
return unrollCheck;
}
public void accept(TreeVisitor tv) {
tv.visitForStmt(this);
}
}

package com.sun.scenario.effect.compiler.tree;
public class WhileStmt extends Stmt {
private final Expr cond;
private final Stmt stmt;
WhileStmt(Expr cond, Stmt stmt) {
this.cond = cond;
this.stmt = stmt;
}
public Expr getCondition() {
return cond;
}
public Stmt getStmt() {
return stmt;
}
public void accept(TreeVisitor tv) {
tv.visitWhileStmt(this);
}
}

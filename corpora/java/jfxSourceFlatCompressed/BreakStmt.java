package com.sun.scenario.effect.compiler.tree;
public class BreakStmt extends Stmt {
BreakStmt() {
}
public void accept(TreeVisitor tv) {
tv.visitBreakStmt(this);
}
}

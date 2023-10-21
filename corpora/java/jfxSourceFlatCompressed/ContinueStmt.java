package com.sun.scenario.effect.compiler.tree;
public class ContinueStmt extends Stmt {
ContinueStmt() {
}
public void accept(TreeVisitor tv) {
tv.visitContinueStmt(this);
}
}

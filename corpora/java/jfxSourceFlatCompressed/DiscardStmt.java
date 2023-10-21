package com.sun.scenario.effect.compiler.tree;
public class DiscardStmt extends Stmt {
DiscardStmt() {
}
public void accept(TreeVisitor tv) {
tv.visitDiscardStmt(this);
}
}

package com.sun.scenario.effect.compiler.tree;
import java.util.List;
public class CompoundStmt extends Stmt {
private final List<Stmt> stmts;
CompoundStmt(List<Stmt> stmts) {
this.stmts = stmts;
}
public List<Stmt> getStmts() {
return stmts;
}
public void accept(TreeVisitor tv) {
tv.visitCompoundStmt(this);
}
}

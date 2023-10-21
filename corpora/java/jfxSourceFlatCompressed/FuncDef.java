package com.sun.scenario.effect.compiler.tree;
import com.sun.scenario.effect.compiler.model.Function;
public class FuncDef extends ExtDecl {
private final Function func;
private final Stmt stmt;
FuncDef(Function func, Stmt stmt) {
this.func = func;
this.stmt = stmt;
}
public Function getFunction() {
return func;
}
public Stmt getStmt() {
return stmt;
}
public void accept(TreeVisitor tv) {
tv.visitFuncDef(this);
}
}

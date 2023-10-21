package com.sun.scenario.effect.compiler.tree;
import com.sun.scenario.effect.compiler.model.Variable;
public class VarDecl extends ExtDecl {
private final Variable var;
private final Expr init;
VarDecl(Variable var, Expr init) {
this.var = var;
this.init = init;
}
public Variable getVariable() {
return var;
}
public Expr getInit() {
return init;
}
public void accept(TreeVisitor tv) {
tv.visitVarDecl(this);
}
}

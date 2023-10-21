package com.sun.scenario.effect.compiler.tree;
import com.sun.scenario.effect.compiler.model.Variable;
public class VariableExpr extends Expr {
private final Variable var;
VariableExpr(Variable var) {
super(var.getType());
this.var = var;
}
public Variable getVariable() {
return var;
}
public void accept(TreeVisitor tv) {
tv.visitVariableExpr(this);
}
}

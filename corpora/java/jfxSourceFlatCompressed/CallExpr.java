package com.sun.scenario.effect.compiler.tree;
import java.util.List;
import com.sun.scenario.effect.compiler.model.Function;
public class CallExpr extends Expr {
private final Function func;
private final List<Expr> params;
CallExpr(Function func, List<Expr> params) {
super(func != null ? func.getReturnType() : null);
this.func = func;
this.params = params;
}
public Function getFunction() {
return func;
}
public List<Expr> getParams() {
return params;
}
public void accept(TreeVisitor tv) {
tv.visitCallExpr(this);
}
}

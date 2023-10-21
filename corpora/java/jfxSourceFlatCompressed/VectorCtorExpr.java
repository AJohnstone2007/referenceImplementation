package com.sun.scenario.effect.compiler.tree;
import java.util.List;
import com.sun.scenario.effect.compiler.model.Type;
public class VectorCtorExpr extends Expr {
private final Type type;
private final List<Expr> params;
VectorCtorExpr(Type type, List<Expr> params) {
super(type);
this.type = type;
this.params = params;
}
public Type getType() {
return type;
}
public List<Expr> getParams() {
return params;
}
public void accept(TreeVisitor tv) {
tv.visitVectorCtorExpr(this);
}
}

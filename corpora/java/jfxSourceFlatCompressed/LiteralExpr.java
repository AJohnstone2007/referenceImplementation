package com.sun.scenario.effect.compiler.tree;
import com.sun.scenario.effect.compiler.model.Type;
public class LiteralExpr extends Expr {
private final Object value;
LiteralExpr(Type type, Object value) {
super(type);
this.value = value;
}
public Object getValue() {
return value;
}
public void accept(TreeVisitor tv) {
tv.visitLiteralExpr(this);
}
}

package com.sun.scenario.effect.compiler.tree;
import com.sun.scenario.effect.compiler.model.UnaryOpType;
public class UnaryExpr extends Expr {
private final UnaryOpType op;
private final Expr expr;
UnaryExpr(UnaryOpType op, Expr expr) {
super(expr.getResultType());
this.op = op;
this.expr = expr;
}
public UnaryOpType getOp() {
return op;
}
public Expr getExpr() {
return expr;
}
public void accept(TreeVisitor tv) {
tv.visitUnaryExpr(this);
}
}

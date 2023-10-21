package com.sun.scenario.effect.compiler.tree;
import com.sun.scenario.effect.compiler.model.BaseType;
import com.sun.scenario.effect.compiler.model.BinaryOpType;
import com.sun.scenario.effect.compiler.model.Type;
public class BinaryExpr extends Expr {
private final BinaryOpType op;
private final Expr left, right;
BinaryExpr(BinaryOpType op, Expr left, Expr right) {
super(getType(op, left, right));
this.op = op;
this.left = left;
this.right = right;
}
private static Type getType(BinaryOpType op, Expr left, Expr right) {
if (op.isRelational()) {
return Type.BOOL;
} else {
Type ltype = left.getResultType();
Type rtype = right.getResultType();
BaseType lbase = ltype.getBaseType();
BaseType rbase = rtype.getBaseType();
if (ltype == rtype) {
return ltype;
} else if (lbase == rbase &&
((ltype.isVector() && !rtype.isVector()) ||
(!ltype.isVector() && rtype.isVector())))
{
if (ltype.isVector()) {
return ltype;
} else {
return rtype;
}
} else if (lbase != rbase &&
!ltype.isVector() &&
!rtype.isVector() &&
((lbase == BaseType.FLOAT && rbase == BaseType.INT) ||
(lbase == BaseType.INT && rbase == BaseType.FLOAT)))
{
return Type.FLOAT;
} else {
throw new RuntimeException("Expressions must have compatible result types" +
" (lhs=" + ltype +
" rhs=" + rtype +
" op=" + op + ")");
}
}
}
public BinaryOpType getOp() {
return op;
}
public Expr getLeft() {
return left;
}
public Expr getRight() {
return right;
}
public void accept(TreeVisitor tv) {
tv.visitBinaryExpr(this);
}
}

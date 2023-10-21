package com.sun.scenario.effect.compiler.tree;
import com.sun.scenario.effect.compiler.model.BaseType;
import com.sun.scenario.effect.compiler.model.Type;
public class FieldSelectExpr extends Expr {
private final Expr expr;
private final String fields;
FieldSelectExpr(Expr expr, String fields) {
super(getType(expr.getResultType(), fields));
this.expr = expr;
this.fields = fields;
}
private static Type getType(Type orig, String fields) {
BaseType base = orig.getBaseType();
int len = fields.length();
for (Type type : Type.values()) {
if (type.getBaseType() == base && type.getNumFields() == len) {
return type;
}
}
throw new RuntimeException("Invalid type for field selection");
}
public Expr getExpr() {
return expr;
}
public String getFields() {
return fields;
}
public void accept(TreeVisitor tv) {
tv.visitFieldSelectExpr(this);
}
}

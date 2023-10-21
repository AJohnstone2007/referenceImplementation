package com.sun.scenario.effect.compiler.backend.sw.me;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.sun.scenario.effect.compiler.model.BaseType;
import com.sun.scenario.effect.compiler.model.FuncImpl;
import com.sun.scenario.effect.compiler.model.Function;
import com.sun.scenario.effect.compiler.model.Param;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.model.Variable;
import com.sun.scenario.effect.compiler.tree.ArrayAccessExpr;
import com.sun.scenario.effect.compiler.tree.BinaryExpr;
import com.sun.scenario.effect.compiler.tree.CallExpr;
import com.sun.scenario.effect.compiler.tree.Expr;
import com.sun.scenario.effect.compiler.tree.FieldSelectExpr;
import com.sun.scenario.effect.compiler.tree.LiteralExpr;
import com.sun.scenario.effect.compiler.tree.ParenExpr;
import com.sun.scenario.effect.compiler.tree.TreeScanner;
import com.sun.scenario.effect.compiler.tree.UnaryExpr;
import com.sun.scenario.effect.compiler.tree.VariableExpr;
import com.sun.scenario.effect.compiler.tree.VectorCtorExpr;
import static com.sun.scenario.effect.compiler.backend.sw.me.MEBackend.*;
class MECallScanner extends TreeScanner {
private StringBuilder sb;
private boolean inCallExpr = false;
private Set<Integer> selectedFields = null;
private boolean inFieldSelect = false;
private char selectedField = 'x';
private boolean inVectorOp = false;
private int vectorIndex = 0;
private void output(String s) {
if (sb == null) {
sb = new StringBuilder();
}
sb.append(s);
}
String getResult() {
return (sb != null) ? sb.toString() : null;
}
@Override
public void visitCallExpr(CallExpr e) {
if (inCallExpr) {
throw new InternalError("Nested function calls not yet supported");
}
Function func = e.getFunction();
Type t = func.getReturnType();
String vtype = t.getBaseType().toString();
String vname = func.getName();
Set<Integer> fields = selectedFields;
if (t.isVector()) {
if (fields == null) {
fields = new HashSet<Integer>();
for (int i = 0; i < t.getNumFields(); i++) {
fields.add(i);
}
}
}
if (!MEBackend.isResultVarDeclared(vname)) {
MEBackend.declareResultVar(vname);
if (t.isVector()) {
output(vtype + " ");
boolean first = true;
for (Integer f : fields) {
if (first) {
first = false;
} else {
output(", ");
}
output(vname + "_res" + getSuffix(f));
}
output(";\n");
} else {
output(vtype + " " + vname + "_res;\n");
}
}
inCallExpr = true;
output("{\n");
List<Param> params = func.getParams();
List<Expr> argExprs = e.getParams();
for (int i = 0; i < params.size(); i++) {
Param param = params.get(i);
String pname = param.getName();
Type ptype = param.getType();
BaseType pbasetype = ptype.getBaseType();
if (pbasetype == BaseType.SAMPLER) {
continue;
}
if (ptype.isVector()) {
inVectorOp = true;
for (int j = 0; j < ptype.getNumFields(); j++) {
vectorIndex = j;
output(pbasetype.toString());
output(" ");
output(pname + "_tmp" + getSuffix(j) + " = ");
scan(argExprs.get(i));
output(";\n");
}
inVectorOp = false;
} else {
output(pbasetype.toString());
output(" ");
output(pname + "_tmp = ");
scan(argExprs.get(i));
output(";\n");
}
}
FuncImpl impl = MEFuncImpls.get(func);
if (impl != null) {
String preamble = impl.getPreamble(argExprs);
if (preamble != null) {
output(preamble);
}
if (t.isVector()) {
for (Integer f : fields) {
output(vname + "_res" + getSuffix(f) + " = ");
output(impl.toString(f, argExprs));
output(";\n");
}
} else {
output(vname + "_res = ");
output(impl.toString(0, argExprs));
output(";\n");
}
} else {
METreeScanner scanner = new METreeScanner(func.getName());
scanner.scan(MEBackend.getFuncDef(func.getName()).getStmt());
output(scanner.getResult());
}
output("\n}\n");
inCallExpr = false;
}
@Override
public void visitArrayAccessExpr(ArrayAccessExpr e) {
if (inCallExpr) {
if (e.getExpr() instanceof VariableExpr &&
e.getIndex() instanceof VariableExpr)
{
VariableExpr ve = (VariableExpr)e.getExpr();
VariableExpr ie = (VariableExpr)e.getIndex();
output(ve.getVariable().getName());
output("_arr[" + ie.getVariable().getName());
output(" * " + ve.getVariable().getType().getNumFields());
output(" + " + getFieldIndex(selectedField) + "]");
} else {
throw new InternalError("Array access only supports variable expr/index (for now)");
}
} else {
super.visitArrayAccessExpr(e);
}
}
@Override
public void visitBinaryExpr(BinaryExpr e) {
if (inCallExpr) {
scan(e.getLeft());
output(" " + e.getOp() + " ");
scan(e.getRight());
} else {
super.visitBinaryExpr(e);
}
}
@Override
public void visitFieldSelectExpr(FieldSelectExpr e) {
if (inCallExpr) {
if (e.getFields().length() == 1) {
selectedField = e.getFields().charAt(0);
} else {
int index = inVectorOp ? vectorIndex : 0;
selectedField = e.getFields().charAt(index);
}
inFieldSelect = true;
scan(e.getExpr());
inFieldSelect = false;
} else {
selectedFields = getFieldSet(e.getFields());
super.visitFieldSelectExpr(e);
selectedFields = null;
}
}
private static Set<Integer> getFieldSet(String fields) {
Set<Integer> fieldSet = new HashSet<Integer>();
for (int i = 0; i < fields.length(); i++) {
fieldSet.add(getFieldIndex(fields.charAt(i)));
}
return fieldSet;
}
@Override
public void visitLiteralExpr(LiteralExpr e) {
if (inCallExpr) {
output(e.getValue().toString());
if (e.getValue() instanceof Float) {
output("f");
}
} else {
super.visitLiteralExpr(e);
}
}
@Override
public void visitParenExpr(ParenExpr e) {
if (inCallExpr) {
output("(");
scan(e.getExpr());
output(")");
} else {
super.visitParenExpr(e);
}
}
@Override
public void visitUnaryExpr(UnaryExpr e) {
if (inCallExpr) {
output(e.getOp().toString());
scan(e.getExpr());
} else {
super.visitUnaryExpr(e);
}
}
@Override
public void visitVariableExpr(VariableExpr e) {
if (inCallExpr) {
Variable var = e.getVariable();
output(var.getName());
if (var.isParam()) {
output("_tmp");
}
if (var.getType().isVector()) {
if (inFieldSelect) {
output(getSuffix(getFieldIndex(selectedField)));
} else if (inVectorOp) {
output(getSuffix(vectorIndex));
} else {
throw new InternalError("TBD");
}
}
} else {
super.visitVariableExpr(e);
}
}
@Override
public void visitVectorCtorExpr(VectorCtorExpr e) {
scan(e.getParams().get(vectorIndex));
}
}

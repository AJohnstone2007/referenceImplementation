package com.sun.scenario.effect.compiler.backend.sw.me;
import com.sun.scenario.effect.compiler.model.Function;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.model.Variable;
import com.sun.scenario.effect.compiler.tree.*;
import static com.sun.scenario.effect.compiler.backend.sw.me.MEBackend.getFieldIndex;
import static com.sun.scenario.effect.compiler.backend.sw.me.MEBackend.getSuffix;
class METreeScanner extends TreeScanner {
private final String funcName;
private final StringBuilder sb = new StringBuilder();
private boolean inVectorOp = false;
private int vectorIndex = 0;
private boolean inFieldSelect = false;
private char selectedField = 'x';
METreeScanner() {
this(null);
}
METreeScanner(String funcName) {
this.funcName = funcName;
}
private void output(String s) {
sb.append(s);
}
String getResult() {
return (sb != null) ? sb.toString() : null;
}
@Override
public void visitArrayAccessExpr(ArrayAccessExpr e) {
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
}
@Override
public void visitBinaryExpr(BinaryExpr e) {
scan(e.getLeft());
output(" " + e.getOp() + " ");
scan(e.getRight());
}
@Override
public void visitBreakStmt(BreakStmt s) {
output("break;");
}
@Override
public void visitCallExpr(CallExpr e) {
Function func = e.getFunction();
output(func.getName() + "_res");
if (func.getReturnType().isVector()) {
if (inFieldSelect) {
output(getSuffix(getFieldIndex(selectedField)));
} else if (inVectorOp) {
output(getSuffix(vectorIndex));
} else {
throw new InternalError("TBD");
}
}
}
@Override
public void visitCompoundStmt(CompoundStmt s) {
output("{\n");
super.visitCompoundStmt(s);
output("}\n");
}
@Override
public void visitContinueStmt(ContinueStmt s) {
output("continue;");
}
@Override
public void visitDeclStmt(DeclStmt s) {
super.visitDeclStmt(s);
}
@Override
public void visitDiscardStmt(DiscardStmt s) {
}
@Override
public void visitDoWhileStmt(DoWhileStmt s) {
output("do ");
scan(s.getStmt());
output(" while (");
scan(s.getExpr());
output(");");
}
@Override
public void visitExprStmt(ExprStmt s) {
Expr expr = s.getExpr();
outputPreambles(expr);
Type t = expr.getResultType();
if (t.isVector()) {
inVectorOp = true;
for (int i = 0; i < t.getNumFields(); i++) {
vectorIndex = i;
scan(s.getExpr());
output(";\n");
}
inVectorOp = false;
} else {
scan(s.getExpr());
output(";\n");
}
}
@Override
public void visitFieldSelectExpr(FieldSelectExpr e) {
if (e.getFields().length() == 1) {
selectedField = e.getFields().charAt(0);
} else {
int index = inVectorOp ? vectorIndex : 0;
selectedField = e.getFields().charAt(index);
}
inFieldSelect = true;
scan(e.getExpr());
inFieldSelect = false;
}
@Override
public void visitForStmt(ForStmt s) {
output("for (");
scan(s.getInit());
scan(s.getCondition());
output(";");
scan(s.getExpr());
output(")");
scan(s.getStmt());
}
@Override
public void visitFuncDef(FuncDef d) {
if (d.getFunction().getName().equals("main")) {
scan(d.getStmt());
} else {
MEBackend.putFuncDef(d);
}
}
@Override
public void visitGlueBlock(GlueBlock b) {
MEBackend.addGlueBlock(b.getText());
}
@Override
public void visitLiteralExpr(LiteralExpr e) {
output(e.getValue().toString());
if (e.getValue() instanceof Float) {
output("f");
}
}
@Override
public void visitParenExpr(ParenExpr e) {
output("(");
scan(e.getExpr());
output(")");
}
@Override
public void visitProgramUnit(ProgramUnit p) {
super.visitProgramUnit(p);
}
@Override
public void visitReturnStmt(ReturnStmt s) {
Expr expr = s.getExpr();
if (expr == null) {
throw new InternalError("Empty return not yet implemented");
}
if (funcName == null) {
throw new RuntimeException("Return statement not expected");
}
Type t = expr.getResultType();
if (t.isVector()) {
inVectorOp = true;
for (int i = 0; i < t.getNumFields(); i++) {
vectorIndex = i;
output(funcName + "_res" + getSuffix(i) + " = ");
scan(s.getExpr());
output(";\n");
}
inVectorOp = false;
} else {
output(funcName + "_res = ");
scan(s.getExpr());
output(";\n");
}
}
@Override
public void visitSelectStmt(SelectStmt s) {
output("if (");
scan(s.getIfExpr());
output(")");
scan(s.getThenStmt());
Stmt e = s.getElseStmt();
if (e != null) {
output(" else ");
scan(e);
}
}
@Override
public void visitUnaryExpr(UnaryExpr e) {
output(e.getOp().toString());
scan(e.getExpr());
}
@Override
public void visitVarDecl(VarDecl d) {
Variable var = d.getVariable();
if (var.getQualifier() != null) {
return;
}
outputPreambles(d);
Type t = var.getType();
if (t.isVector()) {
inVectorOp = true;
for (int i = 0; i < t.getNumFields(); i++) {
output(t.getBaseType().toString() + " ");
output(var.getName() + getSuffix(i));
Expr init = d.getInit();
if (init != null) {
output(" = ");
vectorIndex = i;
scan(init);
}
output(";\n");
}
inVectorOp = false;
} else {
output(t.toString() + " " + var.getName());
Expr init = d.getInit();
if (init != null) {
output(" = ");
scan(init);
}
output(";\n");
}
}
@Override
public void visitVariableExpr(VariableExpr e) {
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
}
@Override
public void visitVectorCtorExpr(VectorCtorExpr e) {
scan(e.getParams().get(vectorIndex));
}
@Override
public void visitWhileStmt(WhileStmt s) {
output("while (");
scan(s.getCondition());
output(")");
scan(s.getStmt());
}
private void outputPreambles(Tree tree) {
MECallScanner scanner = new MECallScanner();
scanner.scan(tree);
String res = scanner.getResult();
if (res != null) {
output(scanner.getResult());
}
}
}

package com.sun.scenario.effect.compiler.tree;
public class TreeScanner extends TreeVisitor {
public final void scan(Tree node) {
if (node != null) {
node.accept(this);
}
}
@Override
public void visitArrayAccessExpr(ArrayAccessExpr e) {
scan(e.getExpr());
scan(e.getIndex());
}
@Override
public void visitBinaryExpr(BinaryExpr e) {
scan(e.getLeft());
scan(e.getRight());
}
@Override
public void visitBreakStmt(BreakStmt s) {
}
@Override
public void visitCallExpr(CallExpr e) {
for (Expr param : e.getParams()) {
scan(param);
}
}
@Override
public void visitCompoundStmt(CompoundStmt s) {
for (Stmt stmt : s.getStmts()) {
scan(stmt);
}
}
@Override
public void visitContinueStmt(ContinueStmt s) {
}
@Override
public void visitDeclStmt(DeclStmt s) {
for (VarDecl decl : s.getDecls()) {
scan(decl);
}
}
@Override
public void visitDiscardStmt(DiscardStmt s) {
}
@Override
public void visitDoWhileStmt(DoWhileStmt s) {
scan(s.getStmt());
scan(s.getExpr());
}
@Override
public void visitExprStmt(ExprStmt s) {
scan(s.getExpr());
}
@Override
public void visitFieldSelectExpr(FieldSelectExpr e) {
scan(e.getExpr());
}
@Override
public void visitForStmt(ForStmt s) {
scan(s.getInit());
scan(s.getCondition());
scan(s.getExpr());
scan(s.getStmt());
}
@Override
public void visitFuncDef(FuncDef d) {
scan(d.getStmt());
}
@Override
public void visitGlueBlock(GlueBlock b) {
}
@Override
public void visitLiteralExpr(LiteralExpr e) {
}
@Override
public void visitParenExpr(ParenExpr e) {
scan(e.getExpr());
}
@Override
public void visitProgramUnit(ProgramUnit p) {
for (ExtDecl decl : p.getDecls()) {
scan(decl);
}
}
@Override
public void visitReturnStmt(ReturnStmt s) {
scan(s.getExpr());
}
@Override
public void visitSelectStmt(SelectStmt s) {
scan(s.getIfExpr());
scan(s.getThenStmt());
scan(s.getElseStmt());
}
@Override
public void visitUnaryExpr(UnaryExpr e) {
scan(e.getExpr());
}
@Override
public void visitVarDecl(VarDecl d) {
scan(d.getInit());
}
@Override
public void visitVariableExpr(VariableExpr e) {
}
@Override
public void visitVectorCtorExpr(VectorCtorExpr e) {
for (Expr expr : e.getParams()) {
scan(expr);
}
}
@Override
public void visitWhileStmt(WhileStmt s) {
scan(s.getCondition());
scan(s.getStmt());
}
}

package com.sun.scenario.effect.compiler.tree;
public class TreeVisitor {
public final void visit(Tree node) {
if (node != null) {
node.accept(this);
}
}
public void visitBinaryExpr(BinaryExpr e) {
}
public void visitUnaryExpr(UnaryExpr e) {
}
public void visitLiteralExpr(LiteralExpr e) {
}
public void visitVariableExpr(VariableExpr e) {
}
public void visitVectorCtorExpr(VectorCtorExpr e) {
}
public void visitParenExpr(ParenExpr e) {
}
public void visitFieldSelectExpr(FieldSelectExpr e) {
}
public void visitArrayAccessExpr(ArrayAccessExpr e) {
}
public void visitCallExpr(CallExpr e) {
}
public void visitContinueStmt(ContinueStmt s) {
}
public void visitBreakStmt(BreakStmt s) {
}
public void visitDiscardStmt(DiscardStmt s) {
}
public void visitReturnStmt(ReturnStmt s) {
}
public void visitSelectStmt(SelectStmt s) {
}
public void visitWhileStmt(WhileStmt s) {
}
public void visitDoWhileStmt(DoWhileStmt s) {
}
public void visitForStmt(ForStmt s) {
}
public void visitExprStmt(ExprStmt s) {
}
public void visitDeclStmt(DeclStmt s) {
}
public void visitCompoundStmt(CompoundStmt s) {
}
public void visitFuncDef(FuncDef d) {
}
public void visitVarDecl(VarDecl d) {
}
public void visitGlueBlock(GlueBlock b) {
}
public void visitProgramUnit(ProgramUnit p) {
}
}

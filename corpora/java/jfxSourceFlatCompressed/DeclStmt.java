package com.sun.scenario.effect.compiler.tree;
import java.util.List;
public class DeclStmt extends Stmt {
private final List<VarDecl> decls;
DeclStmt(List<VarDecl> decls) {
this.decls = decls;
}
public List<VarDecl> getDecls() {
return decls;
}
public void accept(TreeVisitor tv) {
tv.visitDeclStmt(this);
}
}

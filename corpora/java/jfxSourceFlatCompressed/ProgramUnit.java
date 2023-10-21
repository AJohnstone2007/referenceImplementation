package com.sun.scenario.effect.compiler.tree;
import java.util.List;
public class ProgramUnit implements Tree {
private final List<ExtDecl> declList;
ProgramUnit(List<ExtDecl> declList) {
this.declList = declList;
}
public List<ExtDecl> getDecls() {
return declList;
}
public void accept(TreeVisitor tv) {
tv.visitProgramUnit(this);
}
}

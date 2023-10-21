package com.sun.scenario.effect.compiler.tree;
public class GlueBlock extends ExtDecl {
private final String text;
GlueBlock(String text) {
this.text = text;
}
public String getText() {
return text;
}
public void accept(TreeVisitor tv) {
tv.visitGlueBlock(this);
}
}

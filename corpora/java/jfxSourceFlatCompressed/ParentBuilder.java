package com.sun.javafx.fxml.builder.web;
@Deprecated
public abstract class ParentBuilder<B extends ParentBuilder<B>> extends NodeBuilder<B> {
protected ParentBuilder() {
}
private int __set;
public void applyTo(javafx.scene.Parent x) {
super.applyTo(x);
int set = __set;
if ((set & (1 << 1)) != 0) x.getStylesheets().addAll(this.stylesheets);
}
private java.util.Collection<? extends java.lang.String> stylesheets;
@SuppressWarnings("unchecked")
public B stylesheets(java.util.Collection<? extends java.lang.String> x) {
this.stylesheets = x;
__set |= 1 << 1;
return (B) this;
}
public B stylesheets(java.lang.String... x) {
return stylesheets(java.util.Arrays.asList(x));
}
}

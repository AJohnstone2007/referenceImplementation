package com.sun.javafx.css;
import javafx.css.PseudoClass;
final class PseudoClassImpl extends PseudoClass {
PseudoClassImpl(String pseudoClassName, int index) {
this.pseudoClassName = pseudoClassName;
this.index = index;
}
@Override
public String getPseudoClassName() {
return pseudoClassName;
}
@Override public String toString() {
return pseudoClassName;
}
public int getIndex() {
return index;
}
private final String pseudoClassName;
private final int index;
}

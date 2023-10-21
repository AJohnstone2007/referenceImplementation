package com.sun.javafx.css;
import javafx.css.Declaration;
import javafx.css.Match;
import javafx.css.ParsedValue;
import javafx.css.PseudoClass;
import javafx.css.Rule;
import javafx.css.Selector;
import javafx.css.Style;
import javafx.css.StyleOrigin;
import java.util.Set;
public class CascadingStyle implements Comparable<CascadingStyle> {
private final Style style;
public Style getStyle() {
return style;
}
private final Set<PseudoClass> pseudoClasses;
private final int specificity;
private final int ordinal;
private final boolean skinProp;
public CascadingStyle(final Style style, Set<PseudoClass> pseudoClasses,
final int specificity, final int ordinal) {
this.style = style;
this.pseudoClasses = pseudoClasses;
this.specificity = specificity;
this.ordinal = ordinal;
this.skinProp = "-fx-skin".equals(style.getDeclaration().getProperty());
}
public CascadingStyle(final Declaration decl, final Match match, final int ordinal) {
this(new Style(match.getSelector(), decl),
match.getPseudoClasses(),
match.getSpecificity(),
ordinal);
}
public String getProperty() {
return style.getDeclaration().getProperty();
}
public Selector getSelector() {
return style.getSelector();
}
public Rule getRule() {
return style.getDeclaration().getRule();
}
public StyleOrigin getOrigin() {
return getRule().getOrigin();
}
public ParsedValue getParsedValue() {
return style.getDeclaration().getParsedValue();
}
@Override public String toString() { return getProperty(); }
@Override public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
CascadingStyle other = (CascadingStyle)obj;
final String property = getProperty();
final String otherProperty = other.getProperty();
if (property == null ? otherProperty != null : !property.equals(otherProperty)) {
return false;
}
if (pseudoClasses == null ? other.pseudoClasses != null : !pseudoClasses.containsAll(other.pseudoClasses)) {
return false;
}
return true;
}
@Override public int hashCode() {
int hash = 7;
final String property = getProperty();
hash = 47 * hash + (property != null ? property.hashCode() : 0);
hash = 47 * hash + (pseudoClasses != null ? pseudoClasses.hashCode() : 0);
return hash;
}
@Override public int compareTo(CascadingStyle other) {
final Declaration decl = style.getDeclaration();
final boolean important = decl != null ? decl.isImportant() : false;
final Rule rule = decl != null ? decl.getRule() : null;
final StyleOrigin source = rule != null ? rule.getOrigin() : null;
final Declaration otherDecl = other.style.getDeclaration();
final boolean otherImportant = otherDecl != null ? otherDecl.isImportant() : false;
final Rule otherRule = otherDecl != null ? otherDecl.getRule() : null;
final StyleOrigin otherSource = otherRule != null ? otherRule.getOrigin() : null;
int c = 0;
if (this.skinProp && !other.skinProp) {
c = 1;
} else if (important != otherImportant) {
c = important ? -1 : 1;
} else if (source != otherSource) {
if (source == null) c = -1;
else if (otherSource == null) c = 1;
else c = otherSource.compareTo(source);
} else {
c = other.specificity - this.specificity;
};
if (c == 0) c = other.ordinal - this.ordinal;
return c;
}
}

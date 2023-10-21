package javafx.css;
import com.sun.javafx.css.Combinator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
abstract public class Selector {
Selector() {
}
private static class UniversalSelector {
private static final Selector INSTANCE =
new SimpleSelector("*", null, null, null);
}
static Selector getUniversalSelector() {
return UniversalSelector.INSTANCE;
}
private Rule rule;
void setRule(Rule rule) {
this.rule = rule;
}
public Rule getRule() {
return rule;
}
private int ordinal = -1;
public void setOrdinal(int ordinal) {
this.ordinal = ordinal;
}
public int getOrdinal() {
return ordinal;
}
public abstract Match createMatch();
public abstract boolean applies(Styleable styleable);
public abstract boolean applies(Styleable styleable, Set<PseudoClass>[] triggerStates, int depth);
public abstract boolean stateMatches(Styleable styleable, Set<PseudoClass> state);
private static final int TYPE_SIMPLE = 1;
private static final int TYPE_COMPOUND = 2;
protected void writeBinary(DataOutputStream os, StyleConverter.StringStore stringStore)
throws IOException {
if (this instanceof SimpleSelector) {
os.writeByte(TYPE_SIMPLE);
} else {
os.writeByte(TYPE_COMPOUND);
}
}
static Selector readBinary(int bssVersion, DataInputStream is, String[] strings)
throws IOException {
final int type = is.readByte();
if (type == TYPE_SIMPLE)
return SimpleSelector.readBinary(bssVersion, is,strings);
else
return CompoundSelector.readBinary(bssVersion, is,strings);
}
public static Selector createSelector(final String cssSelector) {
if (cssSelector == null || cssSelector.length() == 0) {
return null;
}
List<SimpleSelector> selectors = new ArrayList<SimpleSelector>();
List<Combinator> combinators = new ArrayList<Combinator>();
List<String> parts = new ArrayList<String>();
int start = 0;
int end = -1;
char combinator = '\0';
for (int i=0; i<cssSelector.length(); i++) {
char ch = cssSelector.charAt(i);
if (ch == ' ') {
if (combinator == '\0') {
combinator = ch;
end = i;
}
} else if (ch == '>') {
if (combinator == '\0') end = i;
combinator = ch;
} else if (combinator != '\0'){
parts.add(cssSelector.substring(start, end));
start = i;
combinators.add(combinator == ' ' ? Combinator.DESCENDANT : Combinator.CHILD);
combinator = '\0';
}
}
parts.add(cssSelector.substring(start));
for (int i=0; i<parts.size(); i++) {
final String part = parts.get(i);
if (part != null && !part.equals("")) {
String[] pseudoClassParts = part.split(":");
List<String> pseudoClasses = new ArrayList<String>();
for (int j=1; j<pseudoClassParts.length; j++) {
if (pseudoClassParts[j] != null && !pseudoClassParts[j].equals("")) {
pseudoClasses.add(pseudoClassParts[j].trim());
}
}
final String selector = pseudoClassParts[0].trim();
String[] styleClassParts = selector.split("\\.");
List<String> styleClasses = new ArrayList<String>();
for (int j=1; j<styleClassParts.length; j++) {
if (styleClassParts[j] != null && !styleClassParts[j].equals("")) {
styleClasses.add(styleClassParts[j].trim());
}
}
String name = null, id = null;
if (styleClassParts[0].equals("")) {
} else if (styleClassParts[0].charAt(0) == '#') {
id = styleClassParts[0].substring(1).trim();
} else {
name = styleClassParts[0].trim();
}
selectors.add(new SimpleSelector(name, styleClasses, pseudoClasses, id));
}
}
if (selectors.size() == 1) {
return selectors.get(0);
} else {
return new CompoundSelector(selectors, combinators);
}
}
}

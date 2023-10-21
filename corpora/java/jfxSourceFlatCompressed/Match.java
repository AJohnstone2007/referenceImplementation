package javafx.css;
import com.sun.javafx.css.PseudoClassState;
import static javafx.geometry.NodeOrientation.INHERIT;
public final class Match implements Comparable<Match> {
final Selector selector;
final PseudoClassState pseudoClasses;
final int idCount;
final int styleClassCount;
final int specificity;
Match(final Selector selector, PseudoClassState pseudoClasses, int idCount, int styleClassCount) {
assert selector != null;
this.selector = selector;
this.idCount = idCount;
this.styleClassCount = styleClassCount;
this.pseudoClasses = pseudoClasses;
int nPseudoClasses = pseudoClasses != null ? pseudoClasses.size() : 0;
if (selector instanceof SimpleSelector) {
final SimpleSelector simple = (SimpleSelector)selector;
if (simple.getNodeOrientation() != INHERIT) {
nPseudoClasses += 1;
}
}
specificity = (idCount << 8) | (styleClassCount << 4) | nPseudoClasses;
}
public Selector getSelector() {
return selector;
}
public PseudoClassState getPseudoClasses() {
return pseudoClasses;
}
public int getSpecificity() {
return specificity;
}
@Override public int compareTo(Match o) {
return specificity - o.specificity;
}
}

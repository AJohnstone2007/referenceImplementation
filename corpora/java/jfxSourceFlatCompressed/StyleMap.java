package com.sun.javafx.css;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.css.Declaration;
import javafx.css.Match;
import javafx.css.Rule;
import javafx.css.Selector;
public final class StyleMap {
public static final StyleMap EMPTY_MAP =
new StyleMap(-1, Collections.<Selector>emptyList());
public StyleMap(int id, List<Selector> selectors) {
this.id = id;
this.selectors = selectors;
}
public int getId() {
return id;
}
public boolean isEmpty() {
if (selectors != null) return selectors.isEmpty();
else if (cascadingStyles != null) return cascadingStyles.isEmpty();
else return true;
}
public Map<String, List<CascadingStyle>> getCascadingStyles() {
if (cascadingStyles == null) {
if (selectors == null || selectors.isEmpty()) {
cascadingStyles = Collections.emptyMap();
return cascadingStyles;
}
List<CascadingStyle> cascadingStyleList = new ArrayList<>();
int ordinal = 0;
for (int i=0, iMax=selectors.size(); i<iMax; i++) {
final Selector selector = selectors.get(i);
final Match match = selector.createMatch();
final Rule rule = selector.getRule();
for (int d = 0, dmax = rule.getDeclarations().size(); d < dmax; d++) {
final Declaration decl = rule.getDeclarations().get(d);
final CascadingStyle s = new CascadingStyle(decl, match, ordinal++);
cascadingStyleList.add(s);
}
}
if (cascadingStyleList.isEmpty()) {
cascadingStyles = Collections.emptyMap();
return cascadingStyles;
}
Collections.sort(cascadingStyleList, cascadingStyleComparator);
final int nCascadingStyles = cascadingStyleList.size();
cascadingStyles = new HashMap<>(nCascadingStyles);
CascadingStyle cascadingStyle = cascadingStyleList.get(0);
String property = cascadingStyle.getProperty();
for (int fromIndex=0; fromIndex<nCascadingStyles; ) {
List<CascadingStyle> value = cascadingStyles.get(property);
if (value == null) {
int toIndex = fromIndex;
final String currentProperty = property;
while (++toIndex < nCascadingStyles) {
cascadingStyle = cascadingStyleList.get(toIndex);
property = cascadingStyle.getProperty();
if (property.equals(currentProperty) == false) break;
}
cascadingStyles.put(currentProperty, cascadingStyleList.subList(fromIndex, toIndex));
fromIndex = toIndex;
} else {
assert(false);
}
}
selectors.clear();
selectors = null;
}
return cascadingStyles;
}
private static final Comparator<CascadingStyle> cascadingStyleComparator =
(o1, o2) -> {
String thisProperty = o1.getProperty();
String otherProperty = o2.getProperty();
int c = thisProperty.compareTo(otherProperty);
if (c != 0) return c;
return o1.compareTo(o2);
};
private final int id;
private List<Selector> selectors;
private Map<String, List<CascadingStyle>> cascadingStyles;
}

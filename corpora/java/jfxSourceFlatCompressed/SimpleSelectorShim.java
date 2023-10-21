package javafx.css;
import java.util.List;
import java.util.Set;
public class SimpleSelectorShim {
public static SimpleSelector getSimpleSelector(
final String name, final List<String> styleClasses,
final List<String> pseudoClasses, final String id) {
return new SimpleSelector(name, styleClasses, pseudoClasses, id);
}
public static Set<PseudoClass> getPseudoClassStates(SimpleSelector ss) {
return ss.getPseudoClassStates();
}
}

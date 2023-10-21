package javafx.css;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javafx.scene.Node;
public class RuleShim {
public static List<Declaration> getUnobservedDeclarationList(Rule r) {
return r.getUnobservedDeclarationList();
}
public static List<Selector> getUnobservedSelectorList(Rule r) {
return r.getUnobservedSelectorList();
}
public static long applies(
Rule r,
Node node, Set<PseudoClass>[] triggerStates) {
return r.applies(node, triggerStates);
}
public static Rule readBinary(
int bssVersion, DataInputStream is, String[] strings)
throws IOException {
return Rule.readBinary(bssVersion, is, strings);
}
public static void writeBinary(
Rule r,
DataOutputStream os, StyleConverter.StringStore stringStore)
throws IOException {
r.writeBinary(os, stringStore);
}
public static Rule getRule(List<Selector> selectors, List<Declaration> declarations) {
return new Rule(selectors, declarations);
}
}

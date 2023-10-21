package com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.traversal.Direction;
import javafx.event.EventTarget;
import javafx.scene.Node;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import javafx.scene.input.KeyEvent;
import java.util.List;
import static com.sun.javafx.scene.control.inputmap.InputMap.*;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.LEFT;
import static javafx.scene.input.KeyCode.RIGHT;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
public class FocusTraversalInputMap<N extends Node> {
private static final List<InputMap.Mapping<?>> MAPPINGS = List.of(
new KeyMapping(UP, e -> traverseUp(e)),
new KeyMapping(DOWN, e -> traverseDown(e)),
new KeyMapping(LEFT, e -> traverseLeft(e)),
new KeyMapping(RIGHT, e -> traverseRight(e)),
new KeyMapping(TAB, e -> traverseNext(e)),
new KeyMapping(new KeyBinding(TAB).shift(), e -> traversePrevious(e)),
new KeyMapping(new KeyBinding(UP).shift().alt().ctrl(), e -> traverseUp(e)),
new KeyMapping(new KeyBinding(DOWN).shift().alt().ctrl(), e -> traverseDown(e)),
new KeyMapping(new KeyBinding(LEFT).shift().alt().ctrl(), e -> traverseLeft(e)),
new KeyMapping(new KeyBinding(RIGHT).shift().alt().ctrl(), e -> traverseRight(e)),
new KeyMapping(new KeyBinding(TAB).shift().alt().ctrl(), e -> traverseNext(e)),
new KeyMapping(new KeyBinding(TAB).alt().ctrl(), e -> traversePrevious(e)));
private FocusTraversalInputMap() {
}
public static InputMap.Mapping<?>[] getFocusTraversalMappings() {
return MAPPINGS.toArray(new InputMap.Mapping[MAPPINGS.size()]);
}
public static <N extends Node> InputMap<N> createInputMap(N node) {
InputMap<N> inputMap = new InputMap<>(node);
inputMap.getMappings().addAll(getFocusTraversalMappings());
return inputMap;
}
public static void traverse(final Node node, final Direction dir) {
if (node == null) {
throw new IllegalArgumentException("Attempting to traverse on a null Node. " +
"Most probably a KeyEvent has been fired with a null target specified.");
}
NodeHelper.traverse(node, dir);
}
public static final void traverseUp(KeyEvent e) {
traverse(getNode(e), com.sun.javafx.scene.traversal.Direction.UP);
}
public static final void traverseDown(KeyEvent e) {
traverse(getNode(e), com.sun.javafx.scene.traversal.Direction.DOWN);
}
public static final void traverseLeft(KeyEvent e) {
traverse(getNode(e), com.sun.javafx.scene.traversal.Direction.LEFT);
}
public static final void traverseRight(KeyEvent e) {
traverse(getNode(e), com.sun.javafx.scene.traversal.Direction.RIGHT);
}
public static final void traverseNext(KeyEvent e) {
traverse(getNode(e), com.sun.javafx.scene.traversal.Direction.NEXT);
}
public static final void traversePrevious(KeyEvent e) {
traverse(getNode(e), com.sun.javafx.scene.traversal.Direction.PREVIOUS);
}
private static Node getNode(KeyEvent e) {
EventTarget target = e.getTarget();
if (target instanceof Node) {
return (Node) target;
}
return null;
}
}

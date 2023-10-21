package javafx.scene.input;
import javafx.beans.NamedArg;
import javafx.event.ActionEvent;
import javafx.scene.Node;
public class Mnemonic {
public Mnemonic(@NamedArg("node") Node node, @NamedArg("keyCombination") KeyCombination keyCombination) {
this.node = node;
this.keyCombination = keyCombination;
}
private KeyCombination keyCombination;
public KeyCombination getKeyCombination() { return keyCombination; }
public void setKeyCombination(KeyCombination keyCombination) {
this.keyCombination = keyCombination;
}
private Node node;
public Node getNode() { return node; }
public void setNode(Node node) {
this.node = node;
}
public void fire() {
if (node != null)
node.fireEvent(new ActionEvent());
}
}

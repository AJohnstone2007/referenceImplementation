package javafx.scene.control;
import javafx.scene.Node;
import javafx.scene.control.Tab;
public class TabShim extends Tab {
public TabShim() {
super();
}
public TabShim(String text) {
super(text);
}
public TabShim(String text, Node content) {
super(text, content);
}
public void shim_setSelected(boolean value) {
super.setSelected(value);
}
public void shim_setTabPane(TabPane value) {
super.setTabPane(value);
}
}

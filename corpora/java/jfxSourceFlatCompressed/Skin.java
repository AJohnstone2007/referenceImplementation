package javafx.scene.control;
import javafx.scene.Node;
public interface Skin<C extends Skinnable> {
public C getSkinnable();
public Node getNode();
public void dispose();
}

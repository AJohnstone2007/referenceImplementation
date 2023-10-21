package test.javafx.scene.layout;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.ParentShim;
import javafx.scene.shape.Rectangle;
public class MockParent extends ParentShim {
public MockParent() {
Rectangle r = new Rectangle(-10,-20,100,200);
ParentShim.getChildren(this).add(r);
MockResizable tr = new MockResizable(100,200);
ParentShim.getChildren(this).add(tr);
}
}

package test.javafx.scene.control;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.shape.Rectangle;
public class SkinStub<C extends Control> implements Skin<C> {
Rectangle r = new Rectangle();
Node n;
C c;
public SkinStub(C c) {
r.setWidth(20);
r.setHeight(20);
r.setStrokeWidth(0);
n = r;
this.c = c;
}
public void setNode(Node n) {
this.n = n;
}
@Override
public C getSkinnable() {
return c;
}
@Override
public Node getNode() {
return n;
}
@Override
public void dispose() {
}
}

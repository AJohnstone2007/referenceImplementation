package test.javafx.scene.shape;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
public class StubShape extends Shape {
static {
StubShapeHelper.setStubShapeAccessor(new StubShapeHelper.StubShapeAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((StubShape) node).doCreatePeer();
}
@Override
public com.sun.javafx.geom.Shape doConfigShape(Shape shape) {
return ((StubShape) shape).doConfigShape();
}
});
}
{
StubShapeHelper.initHelper(this);
}
public StubShape() {
setStroke(Color.BLACK);
}
private NGNode doCreatePeer() {
return new StubNGShape();
}
private com.sun.javafx.geom.Shape doConfigShape() {
return new com.sun.javafx.geom.RoundRectangle2D(0, 0, 10, 10, 4, 4);
}
}

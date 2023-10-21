package javafx.scene.input;
import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
public class PickResult {
public static final int FACE_UNDEFINED = -1;
private Node node;
private Point3D point;
private double distance = Double.POSITIVE_INFINITY;
private int face = -1;
private Point3D normal;
private Point2D texCoord;
public PickResult(@NamedArg("node") Node node, @NamedArg("point") Point3D point, @NamedArg("distance") double distance, @NamedArg("face") int face, @NamedArg("texCoord") Point2D texCoord) {
this.node = node;
this.point = point;
this.distance = distance;
this.face = face;
this.normal = null;
this.texCoord = texCoord;
}
public PickResult(@NamedArg("node") Node node, @NamedArg("point") Point3D point,
@NamedArg("distance") double distance, @NamedArg("face") int face,
@NamedArg("normal") Point3D normal, @NamedArg("texCoord") Point2D texCoord) {
this.node = node;
this.point = point;
this.distance = distance;
this.face = face;
this.normal = normal;
this.texCoord = texCoord;
}
public PickResult(@NamedArg("node") Node node, @NamedArg("point") Point3D point, @NamedArg("distance") double distance) {
this.node = node;
this.point = point;
this.distance = distance;
this.face = FACE_UNDEFINED;
this.normal = null;
this.texCoord = null;
}
public PickResult(@NamedArg("target") EventTarget target, @NamedArg("sceneX") double sceneX, @NamedArg("sceneY") double sceneY) {
this(target instanceof Node ? (Node) target : null,
target instanceof Node ? ((Node) target).sceneToLocal(sceneX, sceneY, 0) : new Point3D(sceneX, sceneY, 0),
1.0);
}
public final Node getIntersectedNode() {
return node;
}
public final Point3D getIntersectedPoint() {
return point;
}
public final double getIntersectedDistance() {
return distance;
}
public final int getIntersectedFace() {
return face;
}
public final Point3D getIntersectedNormal() {
return normal;
}
public final Point2D getIntersectedTexCoord() {
return texCoord;
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder("PickResult [");
sb.append("node = ").append(getIntersectedNode())
.append(", point = ").append(getIntersectedPoint())
.append(", distance = ").append(getIntersectedDistance());
if (getIntersectedFace() != FACE_UNDEFINED) {
sb.append(", face = ").append(getIntersectedFace());
}
if (getIntersectedNormal() != null) {
sb.append(", normal = ").append(getIntersectedNormal());
}
if (getIntersectedTexCoord() != null) {
sb.append(", texCoord = ").append(getIntersectedTexCoord());
}
sb.append("]");
return sb.toString();
}
}

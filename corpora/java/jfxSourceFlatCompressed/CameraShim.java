package javafx.scene;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.GeneralTransform3D;
public class CameraShim {
public static PickRay computePickRay(Camera c,
double x, double y, PickRay pickRay) {
return c.computePickRay(x, y, pickRay);
}
public static Vec3d computePosition(Camera c, Vec3d position) {
return c.computePosition(position);
}
public static double getFarClipInScene(Camera c) {
return c.getFarClipInScene();
}
public static double getNearClipInScene(Camera c) {
return c.getNearClipInScene();
}
public static GeneralTransform3D getProjViewTransform(Camera c) {
return c.getProjViewTransform();
}
public static Affine3D getSceneToLocalTransform(Camera c) {
return c.getSceneToLocalTransform();
}
public static double getViewHeight(Camera c) {
return c.getViewHeight();
}
public static double getViewWidth(Camera c) {
return c.getViewWidth();
}
}

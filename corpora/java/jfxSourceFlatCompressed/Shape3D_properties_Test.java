package test.javafx.scene.shape;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
@RunWith(Parameterized.class)
public final class Shape3D_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
Shape3D testShape = createTestBox();
PhongMaterial DEFAULT_MATERIAL = new PhongMaterial();
array.add(config(testShape, "cullFace", CullFace.BACK, CullFace.FRONT));
array.add(config(testShape, "drawMode", DrawMode.FILL, DrawMode.LINE));
array.add(config(testShape, "material", DEFAULT_MATERIAL, null));
return array;
}
public Shape3D_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Box createTestBox() {
Box b = new Box(10, 10, 10);
return b;
}
}

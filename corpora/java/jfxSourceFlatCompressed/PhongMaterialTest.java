package test.javafx.scene.paint;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import static org.junit.Assert.*;
import org.junit.Test;
public class PhongMaterialTest {
@Test
public void testDefaultToString() {
String mat = new PhongMaterial().toString();
assertNotNull(mat);
}
@Test
public void testSetSpecularMap() {
PhongMaterial mat = new PhongMaterial();
Image img = new Image("file:javafx.png");
mat.setSpecularMap(img);
assertEquals(img, mat.getSpecularMap());
}
}

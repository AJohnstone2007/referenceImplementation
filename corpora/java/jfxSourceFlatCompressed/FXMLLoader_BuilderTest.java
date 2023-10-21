package test.javafx.fxml;
import javafx.scene.shape.TriangleMesh;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
public class FXMLLoader_BuilderTest {
@Test
public void testTriangleMeshBuilder() throws IOException {
TriangleMesh mesh = FXMLLoader.load(getClass().getResource("builders_trianglemesh.fxml"));
float[] refFloatArray = {0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f};
int[] refIntArray = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
assertArrayEquals(refIntArray, mesh.getFaces().toArray(new int[0]));
assertArrayEquals(refIntArray, mesh.getFaceSmoothingGroups().toArray(new int[0]));
assertArrayEquals(refFloatArray, mesh.getPoints().toArray(new float[0]), 1e-10f);
assertArrayEquals(refFloatArray, mesh.getTexCoords().toArray(new float[0]), 1e-10f);
}
}

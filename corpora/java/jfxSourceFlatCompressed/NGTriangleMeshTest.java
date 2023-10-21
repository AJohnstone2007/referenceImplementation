package test.com.sun.javafx.sg.prism;
import com.sun.javafx.collections.FloatArraySyncer;
import com.sun.javafx.collections.IntegerArraySyncer;
import com.sun.javafx.sg.prism.NGTriangleMeshShim;
import java.util.Arrays;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
public class NGTriangleMeshTest {
private static final float EPSILON_FLOAT = 1e-5f;
@Test
public void testSyncFaceSmoothingGroups() {
final int[] faceSmoothingGroups = new int[]{0, 1, 2, 3, 4, 5};
NGTriangleMeshShim instance = new NGTriangleMeshShim();
instance.syncFaceSmoothingGroups((array, fromAndLengthIndices) -> faceSmoothingGroups);
int[] actuals = instance.test_getFaceSmoothingGroups();
int[] expecteds = new int[]{0, 1, 2, 3, 4, 5};
assertArrayEquals(expecteds, actuals);
}
@Test
public void testSyncFaceSmoothingGroups2() {
final int[] faceSmoothingGroups = new int[]{0, 1, 2, 3, 4, 5};
NGTriangleMeshShim instance = new NGTriangleMeshShim();
instance.syncFaceSmoothingGroups((array, fromAndLengthIndices) -> faceSmoothingGroups);
instance.syncFaceSmoothingGroups((array, fromAndLengthIndices) -> {
Arrays.fill(array, 1, 1 + 4, 1);
return array;
});
int[] actuals = instance.test_getFaceSmoothingGroups();
int[] expecteds = new int[]{0, 1, 1, 1, 1, 5};
assertArrayEquals(expecteds, actuals);
}
@Test
public void testSyncPoints() {
final float[] points = new float[]{0, 1, 2, 3, 4, 5};
NGTriangleMeshShim instance = new NGTriangleMeshShim();
instance.syncPoints((array, fromAndLengthIndices) -> points);
float[] actuals = instance.test_getPoints();
float[] expecteds = new float[]{0, 1, 2, 3, 4, 5};
assertArrayEquals(expecteds, actuals, EPSILON_FLOAT);
}
@Test
public void testSyncPoints2() {
final float[] points = new float[]{0, 1, 2, 3, 4, 5};
NGTriangleMeshShim instance = new NGTriangleMeshShim();
instance.syncPoints((array, fromAndLengthIndices) -> points);
instance.syncPoints((array, fromAndLengthIndices) -> {
Arrays.fill(array, 1, 1 + 4, 1);
return array;
});
float[] actuals = instance.test_getPoints();
float[] expecteds = new float[]{0, 1, 1, 1, 1, 5};
assertArrayEquals(expecteds, actuals, EPSILON_FLOAT);
}
@Test
public void testSyncNormals() {
final float[] normals = new float[]{0, 1, 2, 3, 4, 5};
NGTriangleMeshShim instance = new NGTriangleMeshShim();
instance.syncNormals((array, fromAndLengthIndices) -> normals);
float[] actuals = instance.test_getNormals();
float[] expecteds = new float[]{0, 1, 2, 3, 4, 5};
assertArrayEquals(expecteds, actuals, EPSILON_FLOAT);
}
@Test
public void testSyncNormals2() {
final float[] normals = new float[]{0, 1, 2, 3, 4, 5};
NGTriangleMeshShim instance = new NGTriangleMeshShim();
instance.syncNormals((array, fromAndLengthIndices) -> normals);
instance.syncNormals((array, fromAndLengthIndices) -> {
Arrays.fill(array, 1, 1 + 4, 1);
return array;
});
float[] actuals = instance.test_getNormals();
float[] expecteds = new float[]{0, 1, 1, 1, 1, 5};
assertArrayEquals(expecteds, actuals, EPSILON_FLOAT);
}
@Test
public void testSyncTexCoords() {
final float[] texcoords = new float[]{0, 1, 2, 3, 4, 5};
NGTriangleMeshShim instance = new NGTriangleMeshShim();
instance.syncTexCoords((array, fromAndLengthIndices) -> texcoords);
float[] actuals = instance.test_getTexCoords();
float[] expecteds = new float[]{0, 1, 2, 3, 4, 5};
assertArrayEquals(expecteds, actuals, EPSILON_FLOAT);
}
@Test
public void testSyncTexCoords2() {
final float[] texcoords = new float[]{0, 1, 2, 3, 4, 5};
NGTriangleMeshShim instance = new NGTriangleMeshShim();
instance.syncTexCoords((array, fromAndLengthIndices) -> texcoords);
instance.syncTexCoords((array, fromAndLengthIndices) -> {
Arrays.fill(array, 1, 1 + 4, 1);
return array;
});
float[] actuals = instance.test_getTexCoords();
float[] expecteds = new float[]{0, 1, 1, 1, 1, 5};
assertArrayEquals(expecteds, actuals, EPSILON_FLOAT);
}
@Test
public void testSyncFaces() {
final int[] faces = new int[]{0, 1, 2, 3, 4, 5};
NGTriangleMeshShim instance = new NGTriangleMeshShim();
instance.syncFaces((array, fromAndLengthIndices) -> faces);
int[] actuals = instance.test_getFaces();
int[] expecteds = new int[]{0, 1, 2, 3, 4, 5};
assertArrayEquals(expecteds, actuals);
}
@Test
public void testSyncFaces2() {
final int[] faces = new int[]{0, 1, 2, 3, 4, 5};
NGTriangleMeshShim instance = new NGTriangleMeshShim();
instance.syncFaces((array, fromAndLengthIndices) -> faces);
instance.syncFaces((array, fromAndLengthIndices) -> {
Arrays.fill(array, 1, 1 + 4, 1);
return array;
});
int[] actuals = instance.test_getFaces();
int[] expecteds = new int[]{0, 1, 1, 1, 1, 5};
assertArrayEquals(expecteds, actuals);
}
}

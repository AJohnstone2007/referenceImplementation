package test.javafx.scene.shape;
import java.util.Arrays;
import javafx.scene.shape.TriangleMesh;
import static org.junit.Assert.*;
import org.junit.Test;
public class TriangleMeshTest {
@Test
public void testSetFaceSmoothingGroups_intArr() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
int[] faceSmoothingGroups = new int[divX * divY * 2];
Arrays.fill(faceSmoothingGroups, 1);
instance.getFaceSmoothingGroups().setAll(faceSmoothingGroups);
assertTrue(instance.getFaceSmoothingGroups().size() == faceSmoothingGroups.length);
assertArrayEquals(faceSmoothingGroups, instance.getFaceSmoothingGroups().toArray(null));
}
@Test
public void testSetFaceSmoothingGroups_4args() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
int[] faceSmoothingGroups = new int[divX * divY * 2];
Arrays.fill(faceSmoothingGroups, 1);
int[] setterArray = new int[]{2, 4, 8};
int[] expected = new int[setterArray.length];
int index = 1;
int start = 0;
int length = setterArray.length;
instance.getFaceSmoothingGroups().setAll(faceSmoothingGroups);
instance.getFaceSmoothingGroups().set(index, setterArray, start, length);
assertArrayEquals(setterArray, instance.getFaceSmoothingGroups().toArray(index, expected, length));
}
@Test (expected=ArrayIndexOutOfBoundsException.class)
public void testSetFaceSmoothingGroups_4argsValueOutOfRange() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
int[] faceSmoothingGroups = new int[divX * divY * 2];
Arrays.fill(faceSmoothingGroups, 1);
int[] setterArray = new int[]{2, 0, -1};
int index = 0;
int start = 0;
int length = setterArray.length;
instance.getFaceSmoothingGroups().set(index, setterArray, start, length);
assertArrayEquals(faceSmoothingGroups, instance.getFaceSmoothingGroups().toArray(null));
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testSetFaceSmoothingGroups_4argsIllegalArgument() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
int[] faceSmoothingGroups = new int[divX * divY * 2];
Arrays.fill(faceSmoothingGroups, 1);
instance.getFaceSmoothingGroups().setAll(faceSmoothingGroups);
int[] setterArray = new int[]{2, 0, 1};
int index = 0;
int start = 0;
instance.getFaceSmoothingGroups().set(index, setterArray, start, -1);
assertArrayEquals(faceSmoothingGroups, instance.getFaceSmoothingGroups().toArray(null));
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testSetFaceSmoothingGroups_4argsIndexOutOfRange() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
int[] faceSmoothingGroups = new int[divX * divY * 2];
Arrays.fill(faceSmoothingGroups, 1);
int[] setterArray = new int[]{2, 0, 1};
int start = 0;
int length = setterArray.length;
instance.getFaceSmoothingGroups().setAll(faceSmoothingGroups);
instance.getFaceSmoothingGroups().set(198, setterArray, start, length);
assertArrayEquals(faceSmoothingGroups, instance.getFaceSmoothingGroups().toArray(null));
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testSetFaceSmoothingGroups_4argsStartOutOfRange() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
int[] faceSmoothingGroups = new int[divX * divY * 2];
Arrays.fill(faceSmoothingGroups, 1);
int[] setterArray = new int[]{2, 0, 1};
int index = 0;
int length = setterArray.length;
instance.getFaceSmoothingGroups().setAll(faceSmoothingGroups);
instance.getFaceSmoothingGroups().set(index, setterArray, 2, length);
assertArrayEquals(faceSmoothingGroups, instance.getFaceSmoothingGroups().toArray(null));
}
@Test
public void testSetFaces_4args() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
int faces[] = {
0, 0, 2, 2, 1, 1,
2, 2, 3, 3, 1, 1,
4, 0, 5, 1, 6, 2,
6, 2, 5, 1, 7, 3,
0, 0, 1, 1, 4, 2,
4, 2, 1, 1, 5, 3,
2, 0, 6, 2, 3, 1,
3, 1, 6, 2, 7, 3,
0, 0, 4, 1, 2, 2,
2, 2, 4, 1, 6, 3,
1, 0, 3, 1, 5, 2,
5, 2, 3, 1, 7, 3,};
int index = 6;
int start = 0;
int length = faces.length;
instance.getFaces().set(index, faces, start, length);
int[] expected = new int[faces.length];
assertArrayEquals(instance.getFaces().toArray(index, expected, length), faces);
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testSetFaces_4argsIllegalArgument() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
int faces[] = {0, 0, 2, 2, 1, 1,};
int[] expecteds = instance.getFaces().toArray(null);
int length = faces.length;
instance.getFaces().set(-1, faces, -1, length);
assertArrayEquals(expecteds, instance.getFaces().toArray(null));
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testSetFaces_4argsIndexOutOfRange() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
int faces[] = {0, 0, 2, 2, 1, 1,};
int[] expecteds = instance.getFaces().toArray(null);
int start = 0;
int length = faces.length;
instance.getFaces().set(1200, faces, start, length);
assertArrayEquals(expecteds, instance.getFaces().toArray(null));
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testSetFaces_4argsStartOutOfRange() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
int faces[] = {
0, 0, 2, 2, 1, 1,
2, 2, 3, 3, 1, 1,};
int[] expecteds = instance.getFaces().toArray(null);
int index = 6;
int length = faces.length;
instance.getFaces().set(index, faces, 1, length);
assertArrayEquals(expecteds, instance.getFaces().toArray(null));
}
@Test
public void testsetTexCoords_4args() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
float texCoords[] = {0, 0,
0, 1,
1, 0,
1, 1};
float[] expecteds = new float[texCoords.length];
int index = 2;
int start = 0;
int length = texCoords.length;
instance.getTexCoords().set(index, texCoords, start, length);
assertArrayEquals(instance.getTexCoords().toArray(index, expecteds, length), texCoords, 1e-3f);
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testsetTexCoords_4argsIllegalArgument() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
float texCoords[] = {0, 0,
0, 1,
1, 0,
1, 1};
float[] expecteds = instance.getTexCoords().toArray(null);
int length = texCoords.length;
instance.getTexCoords().set(-1, texCoords, -1, length);
assertArrayEquals(instance.getTexCoords().toArray(null), expecteds, 1e-3f);
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testsetTexCoords_4argsIndexOutOfRange() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
float texCoords[] = {0, 0,
0, 1,
1, 0,
1, 1};
float[] expecteds = instance.getTexCoords().toArray(null);
int start = 0;
int length = texCoords.length;
instance.getTexCoords().set(240, texCoords, start, length);
assertArrayEquals(instance.getTexCoords().toArray(null), expecteds, 1e-3f);
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testsetTexCoords_4argsStartOutOfRange() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
float texCoords[] = {0, 0,
0, 1,
1, 0,
1, 1};
float[] expecteds = instance.getTexCoords().toArray(null);
int index = 2;
int length = texCoords.length;
instance.getTexCoords().set(index, texCoords, 1, length);
assertArrayEquals(instance.getTexCoords().toArray(null), expecteds, 1e-3f);
}
@Test
public void testSetPoints_4args() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
float points[] = {
1, 1, 1,
1, 1, -1,
1, -1, 1,
1, -1, -1,
-1, 1, 1,
-1, 1, -1,
-1, -1, 1,
-1, -1, -1,};
float[] expecteds = new float[points.length];
int index = 3;
int start = 0;
int length = points.length;
instance.getPoints().set(index, points, start, length);
assertArrayEquals(instance.getPoints().toArray(index, expecteds, length), points, 1e-3f);
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testSetPoints_4argsIllegalArgument() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
float points[] = {
1, 1, 1,
1, 1, -1,
1, -1, 1,
1, -1, -1,
-1, 1, 1,
-1, 1, -1,
-1, -1, 1,
-1, -1, -1,};
float[] expecteds = instance.getPoints().toArray(null);
int length = points.length;
instance.getPoints().set(-1, points, -1, length);
assertArrayEquals(instance.getPoints().toArray(null), expecteds, 1e-3f);
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testSetPoints_4argsIndexOutOfRange() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
float points[] = {
1, 1, 1,
1, 1, -1,
1, -1, 1,
1, -1, -1,
-1, 1, 1,
-1, 1, -1,
-1, -1, 1,
-1, -1, -1,};
float[] expecteds = instance.getPoints().toArray(null);
int start = 0;
int length = points.length;
instance.getPoints().set(120 * 3, points, start, length);
assertArrayEquals(instance.getPoints().toArray(null), expecteds, 1e-3f);
}
@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testSetPoints_4argsStartOutOfRange() {
int divX = 10;
int divY = 10;
TriangleMesh instance = buildTriangleMesh(divX, divY);
float points[] = {
1, 1, 1,
1, 1, -1,
1, -1, 1,
1, -1, -1,
-1, 1, 1,
-1, 1, -1,
-1, -1, 1,
-1, -1, -1,};
float[] expecteds = instance.getPoints().toArray(null);
int index = 3;
int length = points.length;
instance.getPoints().set(index, points, 1, length);
assertArrayEquals(instance.getPoints().toArray(null), expecteds, 1e-3f);
}
@Test
public void testVertexFormatOfDefaultTriangleMesh() {
TriangleMesh triMesh = new TriangleMesh();
assertEquals(3, triMesh.getPointElementSize());
assertEquals(2, triMesh.getTexCoordElementSize());
assertEquals(6, triMesh.getFaceElementSize());
}
TriangleMesh buildTriangleMesh(int subDivX, int subDivY) {
TriangleMesh triangleMesh = new TriangleMesh();
final int pointSize = triangleMesh.getPointElementSize();
final int texCoordSize = triangleMesh.getTexCoordElementSize();
final int faceSize = triangleMesh.getFaceElementSize();
int numDivX = subDivX + 1;
int numVerts = (subDivY + 1) * numDivX;
float points[] = new float[numVerts * pointSize];
float texCoords[] = new float[numVerts * texCoordSize];
int faceCount = subDivX * subDivY * 2;
int faces[] = new int[faceCount * faceSize];
triangleMesh.getPoints().setAll(points);
triangleMesh.getTexCoords().setAll(texCoords);
triangleMesh.getFaces().setAll(faces);
return triangleMesh;
}
}

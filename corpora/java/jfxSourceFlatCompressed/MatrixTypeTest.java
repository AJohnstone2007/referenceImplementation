package test.javafx.scene.transform;
import javafx.scene.transform.MatrixType;
import org.junit.Test;
import static org.junit.Assert.*;
public class MatrixTypeTest {
@Test
public void testIs2D() {
assertTrue(MatrixType.MT_2D_2x3.is2D());
assertTrue(MatrixType.MT_2D_3x3.is2D());
assertFalse(MatrixType.MT_3D_3x4.is2D());
assertFalse(MatrixType.MT_3D_4x4.is2D());
}
@Test
public void testRows() {
assertEquals(2, MatrixType.MT_2D_2x3.rows());
assertEquals(3, MatrixType.MT_2D_3x3.rows());
assertEquals(3, MatrixType.MT_3D_3x4.rows());
assertEquals(4, MatrixType.MT_3D_4x4.rows());
}
@Test
public void testColumns() {
assertEquals(3, MatrixType.MT_2D_2x3.columns());
assertEquals(3, MatrixType.MT_2D_3x3.columns());
assertEquals(4, MatrixType.MT_3D_3x4.columns());
assertEquals(4, MatrixType.MT_3D_4x4.columns());
}
@Test
public void testElements() {
assertEquals(6, MatrixType.MT_2D_2x3.elements());
assertEquals(9, MatrixType.MT_2D_3x3.elements());
assertEquals(12, MatrixType.MT_3D_3x4.elements());
assertEquals(16, MatrixType.MT_3D_4x4.elements());
}
}

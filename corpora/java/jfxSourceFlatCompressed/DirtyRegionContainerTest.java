package test.com.sun.javafx.geom;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.RectBounds;
import junit.framework.Assert;
import org.junit.Test;
public class DirtyRegionContainerTest {
static RectBounds[] nonIntersecting_3_Regions = new RectBounds[] {
new RectBounds(0, 0, 20, 20),
new RectBounds(25, 25, 50, 50),
new RectBounds(60, 60, 100, 100)
};
@Test
public void test_maxSpace() {
DirtyRegionContainer drc = new DirtyRegionContainer(10);
Assert.assertEquals(10, drc.maxSpace());
}
@Test
public void test_size() {
DirtyRegionContainer drc = new DirtyRegionContainer(5);
drc.deriveWithNewRegions(nonIntersecting_3_Regions);
Assert.assertEquals(3, drc.size());
}
@Test
public void test_deriveWithNewBounds() {
DirtyRegionContainer drc = new DirtyRegionContainer(5);
drc.deriveWithNewRegions(nonIntersecting_3_Regions);
for (int i = 0; i < drc.size(); i++) {
RectBounds rb = drc.getDirtyRegion(i);
Assert.assertEquals(nonIntersecting_3_Regions[i], rb);
}
}
@Test
public void test_deriveWithNewBounds_null() {
DirtyRegionContainer drc = getDRC_initialized();
drc.deriveWithNewRegions(null);
for (int i = 0; i < drc.size(); i++) {
RectBounds rb = drc.getDirtyRegion(i);
Assert.assertEquals(nonIntersecting_3_Regions[i], rb);
}
}
@Test
public void test_deriveWithNewBounds_zero_length () {
DirtyRegionContainer drc = getDRC_initialized();
drc.deriveWithNewRegions(new RectBounds[]{});
for (int i = 0; i < drc.size(); i++) {
RectBounds rb = drc.getDirtyRegion(i);
Assert.assertEquals(nonIntersecting_3_Regions[i], rb);
}
}
@Test
public void test_deriveWithNewBounds_biger_length () {
DirtyRegionContainer drc = getDRC_initialized();
RectBounds[] arry = new RectBounds[]{
new RectBounds(1, 1, 10, 10),
new RectBounds(15, 15, 50, 50),
new RectBounds(60, 60, 100, 100),
new RectBounds(110, 110, 200, 200)
};
drc.deriveWithNewRegions(arry);
for (int i = 0; i < drc.size(); i++) {
RectBounds rb = drc.getDirtyRegion(i);
Assert.assertEquals(arry[i], rb);
}
}
@Test
public void test_copy() {
DirtyRegionContainer drc = getDRC_initialized();
DirtyRegionContainer copyDrc = drc.copy();
Assert.assertTrue(copyDrc != drc);
Assert.assertEquals(copyDrc, drc);
}
@Test
public void test_getDirtyRegion() {
DirtyRegionContainer drc = getDRC_initialized();
RectBounds dr = drc.getDirtyRegion(1);
Assert.assertEquals(new RectBounds(25, 25, 50, 50), dr);
}
@Test (expected=ArrayIndexOutOfBoundsException.class)
public void test_getDirtyRegion_AIOOBE() {
DirtyRegionContainer drc = getDRC_initialized();
RectBounds dr = drc.getDirtyRegion(10);
Assert.fail("Expected AIOOBE");
}
@Test
public void test_addDirtyRegion_non_intersecting() {
DirtyRegionContainer drc = getDRC_initialized();
RectBounds newregion = new RectBounds(150, 150, 200, 200);
drc.addDirtyRegion(newregion);
Assert.assertEquals(4, drc.size());
for(int i = 0; i < drc.size() - 1; i++) {
Assert.assertEquals(nonIntersecting_3_Regions[i], (drc.getDirtyRegion(i)));
}
Assert.assertEquals(drc.getDirtyRegion(drc.size() - 1), newregion);
}
@Test
public void test_addDirtyRegion_has_space_intersect_once() {
DirtyRegionContainer drc = getDRC_initialized();
drc.addDirtyRegion(new RectBounds(10, 10, 22, 15));
Assert.assertEquals(3, drc.size());
Assert.assertEquals(new RectBounds(60, 60, 100, 100), drc.getDirtyRegion(0));
Assert.assertEquals(new RectBounds(25, 25, 50, 50), drc.getDirtyRegion(1));
Assert.assertEquals(new RectBounds(0, 0, 22, 20), drc.getDirtyRegion(2));
}
@Test
public void test_addDirtyRegion_has_space_intersect_twice() {
DirtyRegionContainer drc = getDRC_initialized();
drc.addDirtyRegion(new RectBounds(10, 10, 40, 40));
Assert.assertEquals(2, drc.size());
Assert.assertEquals(new RectBounds(60, 60, 100, 100), drc.getDirtyRegion(0));
Assert.assertEquals(new RectBounds(0, 0, 50, 50), drc.getDirtyRegion(1));
}
@Test
public void test_addDirtyRegion_has_space_intersect_all() {
DirtyRegionContainer drc = getDRC_initialized();
drc.addDirtyRegion(new RectBounds(10, 10, 80, 80));
Assert.assertEquals(1, drc.size());
Assert.assertEquals(new RectBounds(0, 0, 100, 100), drc.getDirtyRegion(0));
}
@Test
public void test_addDirtyRegion_no_space_intersect_once() {
DirtyRegionContainer drc = getDRC_initialized();
drc.addDirtyRegion(new RectBounds(120, 120, 150, 150));
drc.addDirtyRegion(new RectBounds(10, 10, 22, 15));
Assert.assertEquals(4, drc.size());
Assert.assertEquals(new RectBounds(120, 120, 150, 150), drc.getDirtyRegion(0));
Assert.assertEquals(new RectBounds(25, 25, 50, 50), drc.getDirtyRegion(1));
Assert.assertEquals(new RectBounds(60, 60, 100, 100), drc.getDirtyRegion(2));
Assert.assertEquals(new RectBounds(0, 0, 22, 20), drc.getDirtyRegion(3));
}
@Test
public void test_addDirtyRegion_no_space_intersect_twice() {
DirtyRegionContainer drc = getDRC_initialized();
drc.addDirtyRegion(new RectBounds(120, 120, 150, 150));
drc.addDirtyRegion(new RectBounds(10, 10, 40, 40));
Assert.assertEquals(3, drc.size());
Assert.assertEquals(new RectBounds(120, 120, 150, 150), drc.getDirtyRegion(0));
Assert.assertEquals(new RectBounds(60, 60, 100, 100), drc.getDirtyRegion(1));
Assert.assertEquals(new RectBounds(0, 0, 50, 50), drc.getDirtyRegion(2));
}
private DirtyRegionContainer getDRC_initialized() {
DirtyRegionContainer drc = new DirtyRegionContainer(4);
return drc.deriveWithNewRegions(nonIntersecting_3_Regions);
}
}

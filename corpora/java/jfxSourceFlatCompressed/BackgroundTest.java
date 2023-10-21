package test.javafx.scene.layout;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundShim;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.junit.Test;
import static javafx.scene.layout.BackgroundRepeat.*;
import static org.junit.Assert.*;
public class BackgroundTest {
private static final BackgroundFill[] FILLS_1 = new BackgroundFill[] {
new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY),
};
private static final BackgroundFill[] FILLS_2 = new BackgroundFill[] {
new BackgroundFill(Color.GREEN, new CornerRadii(3), new Insets(4)),
new BackgroundFill(Color.BLUE, new CornerRadii(6), new Insets(8)),
};
private static final Image IMAGE_1 = new Image("test/javafx/scene/layout/red.png");
private static final Image IMAGE_2 = new Image("test/javafx/scene/layout/blue.png");
private static final Image IMAGE_3 = new Image("test/javafx/scene/layout/green.png");
private static final Image IMAGE_4 = new Image("test/javafx/scene/layout/yellow.png");
private static final BackgroundImage[] IMAGES_1 = new BackgroundImage[] {
new BackgroundImage(IMAGE_1, null, null, null, null)
};
private static final BackgroundImage[] IMAGES_2 = new BackgroundImage[] {
new BackgroundImage(IMAGE_2, SPACE, SPACE, null, null),
new BackgroundImage(IMAGE_3, ROUND, ROUND, null, null),
new BackgroundImage(IMAGE_4, NO_REPEAT, NO_REPEAT, null, null)
};
@Test public void instanceCreation() {
Background b = new Background(FILLS_1, IMAGES_1);
assertEquals(FILLS_1.length, b.getFills().size(), 0);
assertEquals(FILLS_1[0], b.getFills().get(0));
assertEquals(IMAGES_1.length, b.getImages().size(), 0);
assertEquals(IMAGES_1[0], b.getImages().get(0));
}
@Test public void instanceCreation2() {
Background b = new Background(FILLS_2, IMAGES_2);
assertEquals(FILLS_2.length, b.getFills().size(), 0);
assertEquals(FILLS_2[0], b.getFills().get(0));
assertEquals(FILLS_2[1], b.getFills().get(1));
assertEquals(IMAGES_2.length, b.getImages().size(), 0);
assertEquals(IMAGES_2[0], b.getImages().get(0));
assertEquals(IMAGES_2[1], b.getImages().get(1));
assertEquals(IMAGES_2[2], b.getImages().get(2));
}
@Test public void instanceCreationNullFills() {
Background b = new Background(null, IMAGES_1);
assertEquals(0, b.getFills().size(), 0);
assertEquals(IMAGES_1.length, b.getImages().size(), 0);
assertEquals(IMAGES_1[0], b.getImages().get(0));
}
@Test public void instanceCreationEmptyFills() {
Background b = new Background(new BackgroundFill[0], IMAGES_1);
assertEquals(0, b.getFills().size(), 0);
assertEquals(IMAGES_1.length, b.getImages().size(), 0);
assertEquals(IMAGES_1[0], b.getImages().get(0));
}
@Test public void instanceCreationNullImages() {
Background b = new Background(FILLS_1, null);
assertEquals(FILLS_1.length, b.getFills().size(), 0);
assertEquals(FILLS_1[0], b.getFills().get(0));
assertEquals(0, b.getImages().size(), 0);
}
@Test public void instanceCreationEmptyImages() {
Background b = new Background(FILLS_1, new BackgroundImage[0]);
assertEquals(FILLS_1.length, b.getFills().size(), 0);
assertEquals(FILLS_1[0], b.getFills().get(0));
assertEquals(0, b.getImages().size(), 0);
}
@Test public void instanceCreationWithNullsInTheFillArray() {
final BackgroundFill[] fills = new BackgroundFill[] {
null,
new BackgroundFill(Color.GREEN, new CornerRadii(3), new Insets(4)),
new BackgroundFill(Color.BLUE, new CornerRadii(6), new Insets(8)),
};
Background b = new Background(fills, null);
assertEquals(FILLS_2.length, b.getFills().size(), 0);
assertEquals(FILLS_2[0], b.getFills().get(0));
assertEquals(FILLS_2[1], b.getFills().get(1));
}
@Test public void instanceCreationWithNullsInTheFillArray2() {
final BackgroundFill[] fills = new BackgroundFill[] {
new BackgroundFill(Color.GREEN, new CornerRadii(3), new Insets(4)),
null,
new BackgroundFill(Color.BLUE, new CornerRadii(6), new Insets(8)),
};
Background b = new Background(fills, null);
assertEquals(FILLS_2.length, b.getFills().size(), 0);
assertEquals(FILLS_2[0], b.getFills().get(0));
assertEquals(FILLS_2[1], b.getFills().get(1));
}
@Test public void instanceCreationWithNullsInTheFillArray3() {
final BackgroundFill[] fills = new BackgroundFill[] {
new BackgroundFill(Color.GREEN, new CornerRadii(3), new Insets(4)),
new BackgroundFill(Color.BLUE, new CornerRadii(6), new Insets(8)),
null
};
Background b = new Background(fills, null);
assertEquals(FILLS_2.length, b.getFills().size(), 0);
assertEquals(FILLS_2[0], b.getFills().get(0));
assertEquals(FILLS_2[1], b.getFills().get(1));
}
@Test public void instanceCreationWithNullsInTheFillArray4() {
final BackgroundFill[] fills = new BackgroundFill[] {
null
};
Background b = new Background(fills, null);
assertEquals(0, b.getFills().size(), 0);
}
@Test public void instanceCreationWithNullsInTheImageArray() {
final BackgroundImage[] images = new BackgroundImage[] {
null,
new BackgroundImage(IMAGE_2, SPACE, SPACE, null, null),
new BackgroundImage(IMAGE_3, ROUND, ROUND, null, null),
new BackgroundImage(IMAGE_4, NO_REPEAT, NO_REPEAT, null, null)
};
Background b = new Background(null, images);
assertEquals(IMAGES_2.length, b.getImages().size(), 0);
assertEquals(IMAGES_2[0], b.getImages().get(0));
assertEquals(IMAGES_2[1], b.getImages().get(1));
assertEquals(IMAGES_2[2], b.getImages().get(2));
}
@Test public void instanceCreationWithNullsInTheImageArray2() {
final BackgroundImage[] images = new BackgroundImage[] {
new BackgroundImage(IMAGE_2, SPACE, SPACE, null, null),
null,
new BackgroundImage(IMAGE_3, ROUND, ROUND, null, null),
new BackgroundImage(IMAGE_4, NO_REPEAT, NO_REPEAT, null, null)
};
Background b = new Background(null, images);
assertEquals(IMAGES_2.length, b.getImages().size(), 0);
assertEquals(IMAGES_2[0], b.getImages().get(0));
assertEquals(IMAGES_2[1], b.getImages().get(1));
assertEquals(IMAGES_2[2], b.getImages().get(2));
}
@Test public void instanceCreationWithNullsInTheImageArray3() {
final BackgroundImage[] images = new BackgroundImage[] {
new BackgroundImage(IMAGE_2, SPACE, SPACE, null, null),
new BackgroundImage(IMAGE_3, ROUND, ROUND, null, null),
new BackgroundImage(IMAGE_4, NO_REPEAT, NO_REPEAT, null, null),
null
};
Background b = new Background(null, images);
assertEquals(IMAGES_2.length, b.getImages().size(), 0);
assertEquals(IMAGES_2[0], b.getImages().get(0));
assertEquals(IMAGES_2[1], b.getImages().get(1));
assertEquals(IMAGES_2[2], b.getImages().get(2));
}
@Test public void instanceCreationWithNullsInTheImageArray4() {
final BackgroundImage[] images = new BackgroundImage[] {
null
};
Background b = new Background(null, images);
assertEquals(0, b.getImages().size(), 0);
}
@Test public void suppliedBackgroundFillsMutatedLaterDoNotChangeFills() {
final BackgroundFill fill = new BackgroundFill(Color.GREEN, new CornerRadii(3), new Insets(4));
final BackgroundFill[] fills = new BackgroundFill[] { fill };
Background b = new Background(fills, null);
Background b2 = new Background(fills);
fills[0] = null;
assertEquals(1, b.getFills().size());
assertEquals(1, b2.getFills().size());
assertSame(fill, b.getFills().get(0));
assertSame(fill, b2.getFills().get(0));
}
@Test public void suppliedBackgroundImagesMutatedLaterDoNotChangeImages() {
final BackgroundImage image = new BackgroundImage(IMAGE_2, SPACE, SPACE, null, null);
final BackgroundImage[] images = new BackgroundImage[] { image };
Background b = new Background(null, images);
Background b2 = new Background(images);
images[0] = null;
assertEquals(1, b.getImages().size());
assertEquals(1, b2.getImages().size());
assertSame(image, b.getImages().get(0));
assertSame(image, b2.getImages().get(0));
}
@Test(expected = UnsupportedOperationException.class)
public void fillsIsUnmodifiable() {
final BackgroundFill fill = new BackgroundFill(Color.GREEN, new CornerRadii(3), new Insets(4));
final BackgroundFill[] fills = new BackgroundFill[] { fill };
Background b = new Background(fills);
b.getFills().add(new BackgroundFill(Color.BLUE, new CornerRadii(6), new Insets(8)));
}
@Test(expected = UnsupportedOperationException.class)
public void imagesIsUnmodifiable() {
final BackgroundImage image = new BackgroundImage(IMAGE_2, SPACE, SPACE, null, null);
final BackgroundImage[] images = new BackgroundImage[] { image };
Background b = new Background(images);
b.getImages().add(new BackgroundImage(IMAGE_3, ROUND, ROUND, null, null));
}
@Test public void backgroundOutsetsAreDefinedByFills() {
final BackgroundFill[] fills = new BackgroundFill[] {
new BackgroundFill(Color.RED, new CornerRadii(3), new Insets(-1, 5, 5, 5)),
new BackgroundFill(Color.GREEN, new CornerRadii(6), new Insets(8)),
new BackgroundFill(Color.BLUE, new CornerRadii(6), new Insets(2, -1, 3, 5)),
new BackgroundFill(Color.MAGENTA, new CornerRadii(6), new Insets(-7, -2, 4, 4)),
new BackgroundFill(Color.CYAN, new CornerRadii(6), new Insets(0, 0, -8, 0)),
new BackgroundFill(Color.YELLOW, new CornerRadii(6), new Insets(4, -1, 3, 5)),
new BackgroundFill(Color.BLACK, new CornerRadii(6), new Insets(0, 0, 0, -8))
};
Background b = new Background(fills, null);
assertEquals(new Insets(7, 2, 8, 8), b.getOutsets());
}
@Test public void backgroundImagesDoNotContributeToOutsets() {
final BackgroundImage[] images = new BackgroundImage[] {
new BackgroundImage(IMAGE_1, null, null,
new BackgroundPosition(Side.LEFT, -10, false, Side.TOP, -10, false),
null)
};
Background b = new Background(null, images);
assertEquals(Insets.EMPTY, b.getOutsets());
}
@Test public void equivalent() {
Background a = new Background((BackgroundFill[])null, null);
Background b = new Background((BackgroundFill[])null, null);
assertEquals(a, b);
}
@Test public void equivalent2() {
Background a = new Background(FILLS_2, null);
Background b = new Background(FILLS_2, null);
assertEquals(a, b);
}
@Test public void equivalent3() {
Background a = new Background(null, IMAGES_2);
Background b = new Background(null, IMAGES_2);
assertEquals(a, b);
}
@Test public void equivalentHasSameHashCode() {
Background a = new Background((BackgroundFill[])null, null);
Background b = new Background((BackgroundFill[])null, null);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void equivalentHasSameHashCode2() {
Background a = new Background(FILLS_2, null);
Background b = new Background(FILLS_2, null);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void equivalentHasSameHashCode3() {
Background a = new Background(null, IMAGES_2);
Background b = new Background(null, IMAGES_2);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void notEqual() {
Background a = new Background(FILLS_1, null);
Background b = new Background((BackgroundFill[])null, null);
assertFalse(a.equals(b));
}
@Test public void notEqual2() {
Background a = new Background((BackgroundFill[])null, null);
Background b = new Background(FILLS_2, null);
assertFalse(a.equals(b));
}
@Test public void notEqual3() {
Background a = new Background(null, IMAGES_1);
Background b = new Background((BackgroundFill[])null, null);
assertFalse(a.equals(b));
}
@Test public void notEqual4() {
Background a = new Background((BackgroundFill[])null, null);
Background b = new Background(null, IMAGES_2);
assertFalse(a.equals(b));
}
@Test public void notEqualWithNull() {
Background a = new Background((BackgroundFill[])null, null);
assertFalse(a.equals(null));
}
@Test public void notEqualWithRandom() {
Background a = new Background((BackgroundFill[])null, null);
assertFalse(a.equals("Some random String"));
}
@Test public void opaqueInsets_nullFillsResultsInNaN() {
Background b = new Background((BackgroundFill[])null, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertTrue(Double.isNaN(trbl[0]));
assertTrue(Double.isNaN(trbl[1]));
assertTrue(Double.isNaN(trbl[2]));
assertTrue(Double.isNaN(trbl[3]));
}
@Test public void opaqueInsets_transparentFillsResultsInNaN() {
BackgroundFill f = new BackgroundFill(Color.TRANSPARENT, null, null);
Background b = new Background(new BackgroundFill[] { f }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertTrue(Double.isNaN(trbl[0]));
assertTrue(Double.isNaN(trbl[1]));
assertTrue(Double.isNaN(trbl[2]));
assertTrue(Double.isNaN(trbl[3]));
}
@Test public void opaqueInsets_transparentFillsResultsInNaN2() {
BackgroundFill f = new BackgroundFill(Color.rgb(255, 0, 0, 0), null, null);
Background b = new Background(new BackgroundFill[] { f }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertTrue(Double.isNaN(trbl[0]));
assertTrue(Double.isNaN(trbl[1]));
assertTrue(Double.isNaN(trbl[2]));
assertTrue(Double.isNaN(trbl[3]));
}
@Test public void opaqueInsets_transparentFillsResultsInNaN3() {
BackgroundFill f = new BackgroundFill(Color.TRANSPARENT, null, null);
BackgroundFill f2 = new BackgroundFill(Color.rgb(255, 0, 0, 0), null, null);
Background b = new Background(new BackgroundFill[] { f, f2 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertTrue(Double.isNaN(trbl[0]));
assertTrue(Double.isNaN(trbl[1]));
assertTrue(Double.isNaN(trbl[2]));
assertTrue(Double.isNaN(trbl[3]));
}
@Test public void opaqueInsets_transparentFillsMixedWithNonTransparentFills() {
BackgroundFill f = new BackgroundFill(Color.TRANSPARENT, null, null);
BackgroundFill f2 = new BackgroundFill(Color.RED, null, new Insets(1));
Background b = new Background(new BackgroundFill[] { f, f2 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(1, trbl[0], 0);
assertEquals(1, trbl[1], 0);
assertEquals(1, trbl[2], 0);
assertEquals(1, trbl[3], 0);
}
@Test public void opaqueInsets_transparentFillsMixedWithNonTransparentFills2() {
BackgroundFill f = new BackgroundFill(Color.TRANSPARENT, null, null);
BackgroundFill f2 = new BackgroundFill(Color.RED, null, new Insets(-1));
Background b = new Background(new BackgroundFill[] { f, f2 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(-1, trbl[0], 0);
assertEquals(-1, trbl[1], 0);
assertEquals(-1, trbl[2], 0);
assertEquals(-1, trbl[3], 0);
}
@Test public void opaqueInsets_nestedOpaqueRectangles_LargestRectangleUsed() {
BackgroundFill f = new BackgroundFill(Color.RED, null, new Insets(0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, null, new Insets(1));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, null, new Insets(2));
Background b = new Background(new BackgroundFill[] { f, f2, f3 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(0, trbl[0], 0);
assertEquals(0, trbl[1], 0);
assertEquals(0, trbl[2], 0);
assertEquals(0, trbl[3], 0);
}
@Test public void opaqueInsets_nestedOpaqueRectangles_LargestRectangleUsed2() {
BackgroundFill f = new BackgroundFill(Color.RED, null, new Insets(-1));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, null, new Insets(0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, null, new Insets(1));
Background b = new Background(new BackgroundFill[] { f, f2, f3 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(-1, trbl[0], 0);
assertEquals(-1, trbl[1], 0);
assertEquals(-1, trbl[2], 0);
assertEquals(-1, trbl[3], 0);
}
@Test public void opaqueInsets_nestedOpaqueRectangles_LargestRectangleUsed3() {
BackgroundFill f = new BackgroundFill(Color.RED, null, new Insets(10));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, null, new Insets(1));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, null, new Insets(2));
Background b = new Background(new BackgroundFill[] { f, f2, f3 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(1, trbl[0], 0);
assertEquals(1, trbl[1], 0);
assertEquals(1, trbl[2], 0);
assertEquals(1, trbl[3], 0);
}
@Test public void opaqueInsets_nestedOpaqueRectangles_LargestRectangleUsed4() {
BackgroundFill f = new BackgroundFill(Color.RED, null, new Insets(0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, null, new Insets(1));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, null, new Insets(-2));
Background b = new Background(new BackgroundFill[] { f, f2, f3 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(-2, trbl[0], 0);
assertEquals(-2, trbl[1], 0);
assertEquals(-2, trbl[2], 0);
assertEquals(-2, trbl[3], 0);
}
@Test public void opaqueInsets_offsetOpaqueRectangles_completelyContained_LargestRectangleUsed() {
BackgroundFill f = new BackgroundFill(Color.RED, null, new Insets(0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, null, new Insets(1, 0, 0, 0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, null, new Insets(0, 1, 0, 0));
BackgroundFill f4 = new BackgroundFill(Color.YELLOW, null, new Insets(0, 0, 1, 0));
BackgroundFill f5 = new BackgroundFill(Color.CYAN, null, new Insets(0, 0, 0, 1));
Background b = new Background(new BackgroundFill[] { f, f2, f3, f4, f5 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(0, trbl[0], 0);
assertEquals(0, trbl[1], 0);
assertEquals(0, trbl[2], 0);
assertEquals(0, trbl[3], 0);
}
@Test public void opaqueInsets_offsetOpaqueRectangles_completelyContained_LargestRectangleUsed2() {
BackgroundFill f = new BackgroundFill(Color.YELLOW, null, new Insets(0, 0, 1, 0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, null, new Insets(1, 0, 0, 0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, null, new Insets(0, 1, 0, 0));
BackgroundFill f4 = new BackgroundFill(Color.RED, null, new Insets(0));
BackgroundFill f5 = new BackgroundFill(Color.CYAN, null, new Insets(0, 0, 0, 1));
Background b = new Background(new BackgroundFill[] { f, f2, f3, f4, f5 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(0, trbl[0], 0);
assertEquals(0, trbl[1], 0);
assertEquals(0, trbl[2], 0);
assertEquals(0, trbl[3], 0);
}
@Test public void opaqueInsets_offsetOpaqueRectangles_UnionUsed() {
BackgroundFill f = new BackgroundFill(Color.RED, null, new Insets(10, 0, 0, 0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, null, new Insets(0, 10, 0, 0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, null, new Insets(0, 0, 10, 0));
BackgroundFill f4 = new BackgroundFill(Color.YELLOW, null, new Insets(0, 0, 0, 10));
Background b = new Background(new BackgroundFill[] { f, f2, f3, f4 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(0, trbl[0], 0);
assertEquals(0, trbl[1], 0);
assertEquals(0, trbl[2], 0);
assertEquals(0, trbl[3], 0);
}
@Test public void opaqueInsets_offsetOpaqueRectangles_UnionUsed2() {
BackgroundFill f = new BackgroundFill(Color.RED, null, new Insets(10, 0, 0, 0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, null, new Insets(10, 10, 0, 0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, null, new Insets(10, 10, 10, 0));
BackgroundFill f4 = new BackgroundFill(Color.YELLOW, null, new Insets(10, 10, 10, 10));
Background b = new Background(new BackgroundFill[] { f, f2, f3, f4 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(10, trbl[0], 0);
assertEquals(0, trbl[1], 0);
assertEquals(0, trbl[2], 0);
assertEquals(0, trbl[3], 0);
}
@Test public void opaqueInsets_offsetOpaqueRectangles_LargestUsed() {
BackgroundFill f = new BackgroundFill(Color.RED, null, new Insets(10));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, null, new Insets(20, 0, 0, 20));
Background b = new Background(new BackgroundFill[] { f, f2 }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(10, trbl[0], 0);
assertEquals(10, trbl[1], 0);
assertEquals(10, trbl[2], 0);
assertEquals(10, trbl[3], 0);
}
@Test public void opaqueInsets_uniformCornerRadii() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(3), null);
Background b = new Background(new BackgroundFill[] { f }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(1.5, trbl[0], 0);
assertEquals(1.5, trbl[1], 0);
assertEquals(1.5, trbl[2], 0);
assertEquals(1.5, trbl[3], 0);
}
@Test public void opaqueInsets_nonUniformCornerRadii() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(1, 2, 3, 4, false), null);
Background b = new Background(new BackgroundFill[] { f }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(1, trbl[0], 0);
assertEquals(1.5, trbl[1], 0);
assertEquals(2, trbl[2], 0);
assertEquals(2, trbl[3], 0);
}
@Test public void opaqueInsets_nonUniformCornerRadii2() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(1, 2, 3, 4, 5, 6, 7, 8,
false, false, false, false,
false, false, false, false), null);
Background b = new Background(new BackgroundFill[] { f }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(1.5, trbl[0], 0);
assertEquals(2.5, trbl[1], 0);
assertEquals(3.5, trbl[2], 0);
assertEquals(4, trbl[3], 0);
}
@Test public void opaqueInsetsPercent_uniformCornerRadii() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(.1, true), null);
Background b = new Background(new BackgroundFill[] { f }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(2.5, trbl[0], 0);
assertEquals(5, trbl[1], 0);
assertEquals(2.5, trbl[2], 0);
assertEquals(5, trbl[3], 0);
}
@Test public void opaqueInsetsPercent_nonUniformCornerRadii() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(.1, .2, .3, .4, true), null);
Background b = new Background(new BackgroundFill[] { f }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(5, trbl[0], 0);
assertEquals(15, trbl[1], 0);
assertEquals(10, trbl[2], 0);
assertEquals(20, trbl[3], 0);
}
@Test public void opaqueInsetsPercent_nonUniformCornerRadii2() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(.1, .15, .2, .25, .3, .35, .4, .45,
true, true, true, true,
true, true, true, true), null);
Background b = new Background(new BackgroundFill[] { f }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(5, trbl[0], 0);
assertEquals(15, trbl[1], 0);
assertEquals(10, trbl[2], 0);
assertEquals(22.5, trbl[3], 0);
}
@Test public void opaqueInsetsPercent_nonUniformCornerRadii3() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(.1, 15, .2, 25, .3, 35, .4, 45,
true, false, true, false,
true, false, true, false), null);
Background b = new Background(new BackgroundFill[] { f }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(7.5, trbl[0], 0);
assertEquals(15, trbl[1], 0);
assertEquals(17.5, trbl[2], 0);
assertEquals(22.5, trbl[3], 0);
}
@Test public void opaqueInsetsPercent_nonUniformCornerRadii4() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(10, .15, 20, .25, 30, .35, 40, .45,
false, true, false, true,
false, true, false, true), null);
Background b = new Background(new BackgroundFill[] { f }, null);
final double[] trbl = new double[4];
BackgroundShim.computeOpaqueInsets(b, 100, 50, trbl);
assertEquals(10, trbl[0], 0);
assertEquals(15, trbl[1], 0);
assertEquals(20, trbl[2], 0);
assertEquals(22.5, trbl[3], 0);
}
@Test public void backgroundFillsArePercentageBased_AllPercentageBased() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(.5, true), new Insets(10, 0, 0, 0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, new CornerRadii(.4, true), new Insets(0, 10, 0, 0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, new CornerRadii(.3, true), new Insets(0, 0, 10, 0));
BackgroundFill f4 = new BackgroundFill(Color.YELLOW, new CornerRadii(.2, true), new Insets(0, 0, 0, 10));
Background b = new Background(new BackgroundFill[] { f, f2, f3, f4 }, null);
assertTrue(b.isFillPercentageBased());
}
@Test public void backgroundFillsArePercentageBased_OnePercentageBased() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(5), new Insets(10, 0, 0, 0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, new CornerRadii(4), new Insets(0, 10, 0, 0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, new CornerRadii(.3, true), new Insets(0, 0, 10, 0));
BackgroundFill f4 = new BackgroundFill(Color.YELLOW, new CornerRadii(2), new Insets(0, 0, 0, 10));
Background b = new Background(new BackgroundFill[] { f, f2, f3, f4 }, null);
assertTrue(b.isFillPercentageBased());
}
@Test public void backgroundFillsArePercentageBased_OneCornerOfOne() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(5, 5, .5, 5, 5, 5, 5, 5,
false, false, true, false, false, false, false, false), new Insets(10, 0, 0, 0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, new CornerRadii(4), new Insets(0, 10, 0, 0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, new CornerRadii(3), new Insets(0, 0, 10, 0));
BackgroundFill f4 = new BackgroundFill(Color.YELLOW, new CornerRadii(2), new Insets(0, 0, 0, 10));
Background b = new Background(new BackgroundFill[] { f, f2, f3, f4 }, null);
assertTrue(b.isFillPercentageBased());
}
@Test public void backgroundFillsAreNotPercentageBased() {
BackgroundFill f = new BackgroundFill(Color.RED, new CornerRadii(5), new Insets(10, 0, 0, 0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, new CornerRadii(4), new Insets(0, 10, 0, 0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, new CornerRadii(3), new Insets(0, 0, 10, 0));
BackgroundFill f4 = new BackgroundFill(Color.YELLOW, new CornerRadii(2), new Insets(0, 0, 0, 10));
Background b = new Background(new BackgroundFill[] { f, f2, f3, f4 }, null);
assertFalse(b.isFillPercentageBased());
}
@Test public void backgroundFillsAreNotPercentageBased_NoFills() {
Background b = new Background(new BackgroundFill[0]);
assertFalse(b.isFillPercentageBased());
}
@Test public void backgroundFillsAreNotPercentageBased_NullRadii() {
BackgroundFill f = new BackgroundFill(Color.RED, null, new Insets(10, 0, 0, 0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, null, new Insets(0, 10, 0, 0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, null, new Insets(0, 0, 10, 0));
BackgroundFill f4 = new BackgroundFill(Color.YELLOW, null, new Insets(0, 0, 0, 10));
Background b = new Background(new BackgroundFill[] { f, f2, f3, f4 }, null);
assertFalse(b.isFillPercentageBased());
}
@Test public void backgroundFillsAreNotPercentageBased_OneEmpty() {
BackgroundFill f = new BackgroundFill(Color.RED, CornerRadii.EMPTY, new Insets(10, 0, 0, 0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, new CornerRadii(4), new Insets(0, 10, 0, 0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, new CornerRadii(3), new Insets(0, 0, 10, 0));
BackgroundFill f4 = new BackgroundFill(Color.YELLOW, new CornerRadii(2), new Insets(0, 0, 0, 10));
Background b = new Background(new BackgroundFill[] { f, f2, f3, f4 }, null);
assertFalse(b.isFillPercentageBased());
}
@Test public void backgroundFillsAreNotPercentageBased_AllEmpty() {
BackgroundFill f = new BackgroundFill(Color.RED, CornerRadii.EMPTY, new Insets(10, 0, 0, 0));
BackgroundFill f2 = new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, new Insets(0, 10, 0, 0));
BackgroundFill f3 = new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, new Insets(0, 0, 10, 0));
BackgroundFill f4 = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, new Insets(0, 0, 0, 10));
Background b = new Background(new BackgroundFill[] { f, f2, f3, f4 }, null);
assertFalse(b.isFillPercentageBased());
}
@Test
public void testSingleFill() {
var background1 = Background.fill(Color.BEIGE);
var background2 = new Background(new BackgroundFill(Color.BEIGE, null, null));
assertEquals("The factory method should give the same result as the constructor", background1, background2);
}
@Test
public void testSingleFillWithNullPaint() {
var background1 = Background.fill(null);
var background2 = new Background(new BackgroundFill(null, null, null));
assertEquals("The factory method should give the same result as the constructor", background1, background2);
}
}

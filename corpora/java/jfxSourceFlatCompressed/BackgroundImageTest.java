package test.javafx.scene.layout;
import javafx.geometry.Side;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;
import org.junit.Test;
import static javafx.scene.layout.BackgroundRepeat.*;
import static org.junit.Assert.*;
public class BackgroundImageTest {
private static final BackgroundPosition POS_1 = new BackgroundPosition(Side.LEFT, .5, true, Side.TOP, 10, false);
private static final BackgroundPosition POS_2 = BackgroundPosition.DEFAULT;
private static final BackgroundSize SIZE_1 = new BackgroundSize(1, 1, true, true, false, true);
private static final BackgroundSize SIZE_2 = BackgroundSize.DEFAULT;
private static final Image IMAGE_1 = new Image("test/javafx/scene/layout/red.png");
private static final Image IMAGE_2 = new Image("test/javafx/scene/layout/blue.png");
@Test public void instanceCreation() {
BackgroundImage image = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_1);
assertEquals(IMAGE_1, image.getImage());
assertEquals(REPEAT, image.getRepeatX());
assertEquals(REPEAT, image.getRepeatY());
assertEquals(POS_1, image.getPosition());
assertEquals(SIZE_1, image.getSize());
}
@Test public void instanceCreation2() {
BackgroundImage image = new BackgroundImage(IMAGE_2, NO_REPEAT, ROUND, POS_2, SIZE_2);
assertEquals(IMAGE_2, image.getImage());
assertEquals(NO_REPEAT, image.getRepeatX());
assertEquals(ROUND, image.getRepeatY());
assertEquals(POS_2, image.getPosition());
assertEquals(SIZE_2, image.getSize());
}
@Test(expected = NullPointerException.class)
public void instanceCreationNullImage() {
new BackgroundImage(null, NO_REPEAT, ROUND, POS_2, SIZE_2);
}
@Test public void instanceCreationNullRepeatXDefaultsToREPEAT() {
BackgroundImage image = new BackgroundImage(IMAGE_1, null, REPEAT, POS_1, SIZE_1);
assertEquals(IMAGE_1, image.getImage());
assertEquals(REPEAT, image.getRepeatX());
assertEquals(REPEAT, image.getRepeatY());
assertEquals(POS_1, image.getPosition());
assertEquals(SIZE_1, image.getSize());
}
@Test public void instanceCreationNullRepeatYDefaultsToREPEAT() {
BackgroundImage image = new BackgroundImage(IMAGE_1, REPEAT, null, POS_1, SIZE_1);
assertEquals(IMAGE_1, image.getImage());
assertEquals(REPEAT, image.getRepeatX());
assertEquals(REPEAT, image.getRepeatY());
assertEquals(POS_1, image.getPosition());
assertEquals(SIZE_1, image.getSize());
}
@Test public void instanceCreationNullPositionDefaultsToDEFAULT() {
BackgroundImage image = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, null, SIZE_1);
assertEquals(IMAGE_1, image.getImage());
assertEquals(REPEAT, image.getRepeatX());
assertEquals(REPEAT, image.getRepeatY());
assertEquals(BackgroundPosition.DEFAULT, image.getPosition());
assertEquals(SIZE_1, image.getSize());
}
@Test public void instanceCreationNullSizeDefaultsToDEFAULT() {
BackgroundImage image = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, null);
assertEquals(IMAGE_1, image.getImage());
assertEquals(REPEAT, image.getRepeatX());
assertEquals(REPEAT, image.getRepeatY());
assertEquals(POS_1, image.getPosition());
assertEquals(BackgroundSize.DEFAULT, image.getSize());
}
@Test public void equivalent() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_1);
assertEquals(a, b);
}
@Test public void equivalent2() {
BackgroundImage a = new BackgroundImage(IMAGE_1, SPACE, REPEAT, POS_1, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, SPACE, REPEAT, POS_1, SIZE_1);
assertEquals(a, b);
}
@Test public void equivalent3() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, ROUND, POS_1, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, ROUND, POS_1, SIZE_1);
assertEquals(a, b);
}
@Test public void equivalent4() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_2, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_2, SIZE_1);
assertEquals(a, b);
}
@Test public void equivalent5() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_2);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_2);
assertEquals(a, b);
}
@Test public void equivalent6() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_2, SIZE_2);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
assertEquals(a, b);
}
@Test public void equivalentHasSameHashCode() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_1);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void equivalentHasSameHashCode2() {
BackgroundImage a = new BackgroundImage(IMAGE_1, SPACE, REPEAT, POS_1, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, SPACE, REPEAT, POS_1, SIZE_1);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void equivalentHasSameHashCode3() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, ROUND, POS_1, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, ROUND, POS_1, SIZE_1);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void equivalentHasSameHashCode4() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_2, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_2, SIZE_1);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void equivalentHasSameHashCode5() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_2);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_2);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void equivalentHasSameHashCode6() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_2, SIZE_2);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
assertEquals(a.hashCode(), b.hashCode());
}
@Test public void notEquivalent() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_2, REPEAT, REPEAT, POS_1, SIZE_1);
assertFalse(a.equals(b));
}
@Test public void notEquivalent2() {
BackgroundImage a = new BackgroundImage(IMAGE_1, SPACE, REPEAT, POS_1, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, ROUND, REPEAT, POS_1, SIZE_1);
assertFalse(a.equals(b));
}
@Test public void notEquivalent3() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, ROUND, POS_1, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_1);
assertFalse(a.equals(b));
}
@Test public void notEquivalent4() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_2, SIZE_1);
assertFalse(a.equals(b));
}
@Test public void notEquivalent5() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_1);
BackgroundImage b = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_1, SIZE_2);
assertFalse(a.equals(b));
}
@Test public void notEquivalentWithNull() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_2, SIZE_2);
assertFalse(a.equals(null));
}
@Test public void notEquivalentWithRandom() {
BackgroundImage a = new BackgroundImage(IMAGE_1, REPEAT, REPEAT, POS_2, SIZE_2);
assertFalse(a.equals("Some random string"));
}
}

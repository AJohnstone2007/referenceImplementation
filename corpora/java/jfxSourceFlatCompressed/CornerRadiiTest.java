package test.javafx.scene.layout;
import javafx.scene.layout.CornerRadii;
import org.junit.Test;
import static org.junit.Assert.*;
public class CornerRadiiTest {
@Test public void instanceCreation_singleConstructor() {
CornerRadii r = new CornerRadii(1);
assertEquals(1, r.getTopLeftHorizontalRadius(), 0);
assertEquals(1, r.getTopLeftVerticalRadius(), 0);
assertEquals(1, r.getTopRightVerticalRadius(), 0);
assertEquals(1, r.getTopRightHorizontalRadius(), 0);
assertEquals(1, r.getBottomRightHorizontalRadius(), 0);
assertEquals(1, r.getBottomRightVerticalRadius(), 0);
assertEquals(1, r.getBottomLeftVerticalRadius(), 0);
assertEquals(1, r.getBottomLeftHorizontalRadius(), 0);
assertFalse(r.isTopLeftHorizontalRadiusAsPercentage());
assertFalse(r.isTopLeftVerticalRadiusAsPercentage());
assertFalse(r.isTopRightVerticalRadiusAsPercentage());
assertFalse(r.isTopRightHorizontalRadiusAsPercentage());
assertFalse(r.isBottomRightHorizontalRadiusAsPercentage());
assertFalse(r.isBottomRightVerticalRadiusAsPercentage());
assertFalse(r.isBottomLeftVerticalRadiusAsPercentage());
assertFalse(r.isBottomLeftHorizontalRadiusAsPercentage());
}
@Test(expected = IllegalArgumentException.class)
public void negativeRadiusNotAllowed_singleConstructor() {
new CornerRadii(-1);
}
}

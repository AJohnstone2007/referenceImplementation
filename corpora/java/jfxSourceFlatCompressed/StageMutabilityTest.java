package test.javafx.stage;
import com.sun.javafx.stage.StageHelper;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageShim;
import javafx.stage.StageStyle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
public class StageMutabilityTest {
@Test public void testStyleDefault() {
Stage stage = new Stage();
assertEquals(StageStyle.DECORATED, stage.getStyle());
}
@Test public void testStyleSet() {
Stage stage = new Stage();
assertEquals(StageStyle.DECORATED, stage.getStyle());
stage.initStyle(StageStyle.UNDECORATED);
assertEquals(StageStyle.UNDECORATED, stage.getStyle());
stage.initStyle(StageStyle.DECORATED);
assertEquals(StageStyle.DECORATED, stage.getStyle());
stage.initStyle(StageStyle.TRANSPARENT);
assertEquals(StageStyle.TRANSPARENT, stage.getStyle());
stage.initStyle(StageStyle.UTILITY);
assertEquals(StageStyle.UTILITY, stage.getStyle());
}
@Test public void testStyleSetPrimary() {
Stage stage = new Stage();
assertFalse(StageShim.isPrimary(stage));
StageHelper.setPrimary(stage, true);
assertTrue(StageShim.isPrimary(stage));
assertEquals(StageStyle.DECORATED, stage.getStyle());
stage.initStyle(StageStyle.UNDECORATED);
assertEquals(StageStyle.UNDECORATED, stage.getStyle());
stage.initStyle(StageStyle.DECORATED);
assertEquals(StageStyle.DECORATED, stage.getStyle());
stage.initStyle(StageStyle.TRANSPARENT);
assertEquals(StageStyle.TRANSPARENT, stage.getStyle());
stage.initStyle(StageStyle.UTILITY);
assertEquals(StageStyle.UTILITY, stage.getStyle());
}
@Test public void testStyleConstructor() {
Stage stage = new Stage(StageStyle.UNDECORATED);
assertEquals(StageStyle.UNDECORATED, stage.getStyle());
stage.initStyle(StageStyle.DECORATED);
assertEquals(StageStyle.DECORATED, stage.getStyle());
}
@Test public void testStyleSetWhileVisible() {
Stage stage = new Stage();
assertEquals(StageStyle.DECORATED, stage.getStyle());
stage.show();
try {
stage.initStyle(StageStyle.UNDECORATED);
assertTrue(false);
} catch (IllegalStateException ex) {
}
assertEquals(StageStyle.DECORATED, stage.getStyle());
}
@Test public void testStyleSetAfterVisible() {
Stage stage = new Stage(StageStyle.TRANSPARENT);
assertEquals(StageStyle.TRANSPARENT, stage.getStyle());
stage.initStyle(StageStyle.UNDECORATED);
assertEquals(StageStyle.UNDECORATED, stage.getStyle());
stage.show();
stage.hide();
try {
stage.initStyle(StageStyle.DECORATED);
assertTrue(false);
} catch (IllegalStateException ex) {
}
assertEquals(StageStyle.UNDECORATED, stage.getStyle());
}
@Test public void testModalityDefault() {
Stage stage = new Stage();
assertEquals(Modality.NONE, stage.getModality());
}
@Test public void testModalitySet() {
Stage stage = new Stage();
assertEquals(Modality.NONE, stage.getModality());
stage.initModality(Modality.WINDOW_MODAL);
assertEquals(Modality.WINDOW_MODAL, stage.getModality());
stage.initModality(Modality.APPLICATION_MODAL);
assertEquals(Modality.APPLICATION_MODAL, stage.getModality());
stage.initModality(Modality.NONE);
assertEquals(Modality.NONE, stage.getModality());
}
@Test public void testModalitySetPrimary() {
Stage stage = new Stage();
assertFalse(StageShim.isPrimary(stage));
StageHelper.setPrimary(stage, true);
assertTrue(StageShim.isPrimary(stage));
assertEquals(Modality.NONE, stage.getModality());
try {
stage.initModality(Modality.WINDOW_MODAL);
assertTrue(false);
} catch (IllegalStateException ex) {
}
assertEquals(Modality.NONE, stage.getModality());
}
}

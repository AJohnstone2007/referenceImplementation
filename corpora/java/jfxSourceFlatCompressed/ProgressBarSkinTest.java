package test.javafx.scene.control.skin;
import java.lang.ref.WeakReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.sun.javafx.tk.Toolkit;
import static org.junit.Assert.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.ProgressBarSkin;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import test.com.sun.javafx.pgstub.StubToolkit;
public class ProgressBarSkinTest {
private ProgressBar progressbar;
private ProgressBarSkinMock skin;
private Scene scene;
private Stage stage;
private StackPane root;
@Before public void setup() {
progressbar = new ProgressBar();
skin = new ProgressBarSkinMock(progressbar);
progressbar.setSkin(skin);
}
private void initStage() {
Toolkit tk = (StubToolkit)Toolkit.getToolkit();
root = new StackPane();
scene = new Scene(root);
stage = new Stage();
stage.setScene(scene);
}
@After
public void cleanup() {
if (stage != null) {
stage.hide();
}
}
@Test
public void testWidthListener() {
initStage();
double progress = .5;
progressbar.setProgress(progress);
progressbar.setMaxWidth(2000);
root.getChildren().setAll(progressbar);
double stageSize = 300;
stage.setWidth(stageSize);
stage.setHeight(stageSize);
stage.show();
Toolkit.getToolkit().firePulse();
assertEquals("progressbar fills root", root.getWidth(),
progressbar.getWidth(), 0.5);
Region innerBar = (Region) progressbar.lookup(".bar");
assertEquals("inner bar width updated",
progressbar.getWidth() * progress, innerBar.getWidth(), 0.5);
}
WeakReference<Skin<?>> weakSkinRef;
@Test
public void testWidthListenerGC() {
ProgressBar progressbar = new ProgressBar();
progressbar.setSkin(new ProgressBarSkin(progressbar));
weakSkinRef = new WeakReference<>(progressbar.getSkin());
progressbar.setSkin(null);
attemptGC(10);
assertNull("skin must be gc'ed", weakSkinRef.get());
}
private void attemptGC(int n) {
for (int i = 0; i < n; i++) {
System.gc();
if (weakSkinRef.get() == null) {
break;
}
try {
Thread.sleep(500);
} catch (InterruptedException e) {
System.err.println("InterruptedException occurred during Thread.sleep()");
}
}
}
@Test public void maxWidthTracksPreferred() {
progressbar.setPrefWidth(500);
assertEquals(500, progressbar.maxWidth(-1), 0);
}
@Test public void maxHeightTracksPreferred() {
progressbar.setPrefHeight(500);
assertEquals(500, progressbar.maxHeight(-1), 0);
}
public static final class ProgressBarSkinMock extends ProgressBarSkin {
boolean propertyChanged = false;
int propertyChangeCount = 0;
public ProgressBarSkinMock(ProgressBar progressbar) {
super(progressbar);
}
public void addWatchedProperty(ObservableValue<?> p) {
p.addListener(o -> {
propertyChanged = true;
propertyChangeCount++;
});
}
}
}

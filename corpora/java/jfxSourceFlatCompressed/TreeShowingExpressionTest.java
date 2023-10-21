package test.javafx.scene;
import com.sun.javafx.scene.TreeShowingExpression;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
@RunWith(Parameterized.class)
public class TreeShowingExpressionTest {
private final Parent root;
private final Node node;
private final TreeShowingExpression expression;
@Parameters
public static Collection<Object[]> parameters() {
Supplier<RootAndNodeToTest> supplier1 = () -> {
Node node = new StackPane();
return new RootAndNodeToTest(new StackPane(node), node);
};
Supplier<RootAndNodeToTest> supplier2 = () -> {
StackPane node = new StackPane();
return new RootAndNodeToTest(new StackPane(new SubScene(node, 100.0, 100.0)), node);
};
return Arrays.asList(new Object[][] { { supplier1 }, { supplier2 } });
}
static class RootAndNodeToTest {
RootAndNodeToTest(Parent root, Node nodeToTest) {
this.root = root;
this.nodeToTest = nodeToTest;
}
Parent root;
Node nodeToTest;
}
public TreeShowingExpressionTest(Supplier<RootAndNodeToTest> nodeSupplier) {
RootAndNodeToTest nodes = nodeSupplier.get();
this.root = nodes.root;
this.node = nodes.nodeToTest;
this.expression = new TreeShowingExpression(this.node);
}
@Test
public void nodeNotAttachedToSceneShouldNotBeShowing() {
assertFalse(expression.get());
}
@Test
public void getShouldTrackChangesInShowingStateForGivenNode() {
assertFalse(expression.get());
Scene scene = new Scene(root);
assertFalse(expression.get());
Stage stage = new Stage();
stage.setScene(scene);
assertFalse(expression.get());
stage.show();
assertTrue(expression.get());
stage.hide();
assertFalse(expression.get());
}
@Test
public void changeListenerShouldRegisterAndUnregisterCorrectly() {
AtomicReference<Boolean> state = new AtomicReference<>();
ChangeListener<Boolean> listener = (obs, old, current) -> state.set(current);
expression.addListener(listener);
assertNull(state.getAndSet(null));
Stage stage = new Stage();
stage.setScene(new Scene(root));
stage.show();
assertTrue(state.getAndSet(null));
expression.removeListener(listener);
stage.hide();
assertNull(state.getAndSet(null));
}
@Test
public void invalidationListenerShouldRegisterAndUnregisterCorrectly() {
AtomicReference<Boolean> state = new AtomicReference<>();
InvalidationListener listener = obs -> state.set(true);
expression.addListener(listener);
assertNull(state.getAndSet(null));
Stage stage = new Stage();
stage.setScene(new Scene(root));
stage.show();
assertTrue(state.getAndSet(null));
expression.get();
expression.removeListener(listener);
stage.hide();
assertNull(state.getAndSet(null));
}
@Test
public void changeListenerShouldTrackShowingState() {
AtomicReference<Boolean> state = new AtomicReference<>();
expression.addListener((obs, old, current) -> state.set(current));
assertNull(state.getAndSet(null));
Scene scene = new Scene(root);
assertNull(state.getAndSet(null));
Stage stage = new Stage();
stage.setWidth(100);
stage.setHeight(100);
stage.setScene(scene);
assertNull(state.getAndSet(null));
stage.show();
assertTrue(state.getAndSet(null));
stage.setScene(null);
assertFalse(state.getAndSet(null));
stage.setScene(scene);
assertTrue(state.getAndSet(null));
stage.hide();
assertFalse(state.getAndSet(null));
Stage stage2 = new Stage();
stage2.setWidth(100);
stage2.setHeight(100);
stage2.show();
stage2.setScene(scene);
assertTrue(state.getAndSet(null));
stage2.hide();
assertFalse(state.getAndSet(null));
stage.show();
assertNull(state.getAndSet(null));
scene.setRoot(new StackPane());
Scene scene2 = new Scene(root);
stage.setScene(scene2);
assertTrue(state.getAndSet(null));
}
@Test
public void invalidationListenerShouldNotifyOfChangesInShowingState() {
AtomicReference<Boolean> state = new AtomicReference<>();
expression.addListener(obs -> state.set(true));
assertNull(state.getAndSet(null));
Scene scene = new Scene(root);
assertNull(state.getAndSet(null));
Stage stage = new Stage();
stage.setWidth(100);
stage.setHeight(100);
stage.setScene(scene);
assertNull(state.getAndSet(null));
stage.show();
assertTrue(state.getAndSet(null));
expression.get();
stage.setScene(null);
assertTrue(state.getAndSet(null));
expression.get();
stage.setScene(scene);
assertTrue(state.getAndSet(null));
stage.hide();
assertNull(state.getAndSet(null));
stage.show();
expression.get();
stage.hide();
assertTrue(state.getAndSet(null));
Stage stage2 = new Stage();
stage2.setWidth(100);
stage2.setHeight(100);
stage2.show();
expression.get();
stage2.setScene(scene);
assertTrue(state.getAndSet(null));
expression.get();
stage2.hide();
assertTrue(state.getAndSet(null));
expression.get();
stage.show();
assertNull(state.getAndSet(null));
scene.setRoot(new StackPane());
Scene scene2 = new Scene(root);
expression.get();
stage.setScene(scene2);
assertTrue(state.getAndSet(null));
}
@Test
public void disposeShouldUnregisterListenersOnGivenNode() {
AtomicReference<Boolean> state = new AtomicReference<>();
expression.addListener((obs, old, current) -> state.set(current));
Stage stage = new Stage();
Scene scene = new Scene(root);
stage.setScene(scene);
stage.show();
assertTrue(state.getAndSet(null));
expression.dispose();
stage.hide();
assertNull(state.getAndSet(null));
Stage stage2 = new Stage();
stage2.setWidth(100);
stage2.setHeight(100);
stage2.show();
scene.setRoot(new StackPane());
stage2.setScene(new Scene(root));
assertNull(state.getAndSet(null));
}
}

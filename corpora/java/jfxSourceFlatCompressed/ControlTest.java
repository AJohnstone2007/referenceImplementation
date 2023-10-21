package test.javafx.scene.control;
import javafx.css.CssMetaData;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import com.sun.javafx.scene.control.Logging;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.ControlShim;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import static org.junit.Assert.*;
public class ControlTest {
private static final double MIN_WIDTH = 35;
private static final double MIN_HEIGHT = 45;
private static final double MAX_WIDTH = 2000;
private static final double MAX_HEIGHT = 2011;
private static final double PREF_WIDTH = 100;
private static final double PREF_HEIGHT = 130;
private static final double BASELINE_OFFSET = 10;
private ControlStub c;
private SkinStub<ControlStub> s;
private ResizableRectangle skinNode;
private Level originalLogLevel = null;
@Before public void setUp() {
c = new ControlStub();
s = new SkinStub<ControlStub>(c);
skinNode = new ResizableRectangle();
skinNode.resize(20, 20);
skinNode.minWidth = MIN_WIDTH;
skinNode.minHeight = MIN_HEIGHT;
skinNode.maxWidth = MAX_WIDTH;
skinNode.maxHeight = MAX_HEIGHT;
skinNode.prefWidth = PREF_WIDTH;
skinNode.prefHeight = PREF_HEIGHT;
skinNode.baselineOffset = BASELINE_OFFSET;
s.setNode(skinNode);
}
private void disableLogging() {
final PlatformLogger logger = Logging.getControlsLogger();
logger.disableLogging();
}
private void enableLogging() {
final PlatformLogger logger = Logging.getControlsLogger();
logger.enableLogging();
}
@Test public void focusTraversableIsTrueByDefault() {
assertTrue(c.isFocusTraversable());
}
@Test public void resizableIsTrueByDefault() {
assertTrue(c.isResizable());
}
@Test public void modifyingTheControlWidthUpdatesTheLayoutBounds() {
c.resize(173, c.getHeight());
assertEquals(173, c.getLayoutBounds().getWidth(), 0);
}
@Test public void modifyingTheControlWidthLeadsToRequestLayout() {
c.layout();
assertTrue(!c.isNeedsLayout());
c.resize(173, c.getHeight());
assertTrue(c.isNeedsLayout());
}
@Test public void modifyingTheControlHeightUpdatesTheLayoutBounds() {
c.resize(c.getWidth(), 173);
assertEquals(173, c.getLayoutBounds().getHeight(), 0);
}
@Test public void modifyingTheControlHeightLeadsToRequestLayout() {
c.layout();
assertTrue(!c.isNeedsLayout());
c.resize(c.getWidth(), 173);
assertTrue(c.isNeedsLayout());
}
@Test public void multipleModificationsToWidthAndHeightAreReflectedInLayoutBounds() {
c.resize(723, 234);
c.resize(992, 238);
assertEquals(992, c.getLayoutBounds().getWidth(), 0);
assertEquals(238, c.getLayoutBounds().getHeight(), 0);
}
@Test public void containsDelegatesToTheSkinWhenSet() {
c.setSkin(s);
skinNode.resize(100, 100);
assertTrue(c.getSkin().getNode() != null);
assertTrue(c.contains(50, 50));
}
@Test public void intersectsReturnsTrueWhenThereIsNoSkin() {
c.relocate(0, 0);
c.resize(100, 100);
assertTrue(c.intersects(50, 50, 100, 100));
}
@Test public void intersectsDelegatesToTheSkinWhenSet() {
c.setSkin(s);
skinNode.resize(100, 100);
assertTrue(c.intersects(50, 50, 100, 100));
}
@Test public void skinIsResizedToExactSizeOfControlOnLayout() {
c.setSkin(s);
c.resize(67, 998);
c.layout();
assertEquals(0, s.getNode().getLayoutBounds().getMinX(), 0);
assertEquals(0, s.getNode().getLayoutBounds().getMinY(), 0);
assertEquals(67, s.getNode().getLayoutBounds().getWidth(), 0);
assertEquals(998, s.getNode().getLayoutBounds().getHeight(), 0);
}
@Test public void skinWithNodeLargerThanControlDoesntAffectLayoutBounds() {
s.setNode(new Rectangle(0, 0, 1000, 1001));
c.setSkin(s);
c.resize(50, 40);
c.layout();
assertEquals(1000, c.getSkin().getNode().getLayoutBounds().getWidth(), 0);
assertEquals(1001, c.getSkin().getNode().getLayoutBounds().getHeight(), 0);
assertEquals(50, c.getLayoutBounds().getWidth(), 0);
assertEquals(40, c.getLayoutBounds().getHeight(), 0);
}
@Test public void callsToSetMinWidthResultInRequestLayoutBeingCalledOnParentNotOnControl() {
Group parent = new Group();
parent.getChildren().add(c);
parent.layout();
assertTrue(!parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
c.setMinWidth(123.45);
assertTrue(parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
}
@Test public void getMinWidthReturnsTheMinWidthOfTheSkinNode() {
c.setSkin(s);
assertEquals(MIN_WIDTH, c.minWidth(-1), 0.0);
}
@Test public void getMinWidthReturnsTheCustomSetMinWidthWhenSpecified() {
c.setSkin(s);
c.setMinWidth(123.45);
assertEquals(123.45, c.minWidth(-1), 0.0);
}
@Test public void getMinWidthReturnsTheCustomSetMinWidthWhenSpecifiedEvenWhenThereIsNoSkin() {
c.setMinWidth(123.45);
assertEquals(123.45, c.minWidth(-1), 0.0);
}
@Test public void minWidthWhenNoSkinIsSetIsZeroByDefault() {
assertEquals(0, c.minWidth(-1), 0.0);
}
@Test public void resettingMinWidthTo_USE_PREF_SIZE_ReturnsThePrefWidthOfTheSkinNodeWhenThereIsASkin() {
c.setSkin(s);
c.setMinWidth(123.45);
c.setMinWidth(Control.USE_PREF_SIZE);
assertEquals(PREF_WIDTH, c.minWidth(-1), 0.0);
}
@Test public void resettingMinWidthTo_USE_PREF_SIZE_ReturnsZeroWhenThereIsNoSkinAndPrefWidthIsNotSet() {
c.setMinWidth(123.45);
c.setMinWidth(Control.USE_PREF_SIZE);
assertEquals(0, c.minWidth(-1), 0.0);
}
@Test public void resettingMinWidthTo_USE_PREF_SIZE_ReturnsPrefWidthWhenThereIsNoSkinAndPrefWidthIsSet() {
c.setMinWidth(123.45);
c.setPrefWidth(98.6);
c.setMinWidth(Control.USE_PREF_SIZE);
assertEquals(98.6, c.minWidth(-1), 0.0);
}
@Test public void resettingMinWidthTo_USE_COMPUTED_SIZE_ReturnsTheMinWidthOfTheSkinNodeWhenThereIsASkin() {
c.setSkin(s);
c.setMinWidth(123.45);
c.setMinWidth(Control.USE_COMPUTED_SIZE);
assertEquals(MIN_WIDTH, c.minWidth(-1), 0.0);
}
@Test public void resettingMinWidthTo_USE_COMPUTED_SIZE_ReturnsZeroWhenThereIsNoSkinAndPrefWidthIsNotSet() {
c.setMinWidth(123.45);
c.setMinWidth(Control.USE_COMPUTED_SIZE);
assertEquals(0, c.minWidth(-1), 0.0);
}
@Test public void minWidthIs_USE_COMPUTED_SIZE_ByDefault() {
assertEquals(Control.USE_COMPUTED_SIZE, c.getMinWidth(), 0);
assertEquals(Control.USE_COMPUTED_SIZE, c.minWidthProperty().get(), 0);
}
@Test public void minWidthCanBeSet() {
c.setMinWidth(234);
assertEquals(234, c.getMinWidth(), 0);
assertEquals(234, c.minWidthProperty().get(), 0);
}
@Test public void minWidthCanBeBound() {
DoubleProperty other = new SimpleDoubleProperty(939);
c.minWidthProperty().bind(other);
assertEquals(939, c.getMinWidth(), 0);
other.set(332);
assertEquals(332, c.getMinWidth(), 0);
}
@Test public void minWidthPropertyHasBeanReference() {
assertSame(c, c.minWidthProperty().getBean());
}
@Test public void minWidthPropertyHasName() {
assertEquals("minWidth", c.minWidthProperty().getName());
}
@Test public void callsToSetMinHeightResultInRequestLayoutBeingCalledOnParentNotOnControl() {
Group parent = new Group();
parent.getChildren().add(c);
parent.layout();
assertTrue(!parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
c.setMinHeight(98.76);
assertTrue(parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
}
@Test public void getMinHeightReturnsTheMinHeightOfTheSkinNode() {
c.setSkin(s);
assertEquals(MIN_HEIGHT, c.minHeight(-1), 0.0);
}
@Test public void getMinHeightReturnsTheCustomSetMinHeightWhenSpecified() {
c.setSkin(s);
c.setMinHeight(98.76);
assertEquals(98.76, c.minHeight(-1), 0.0);
}
@Test public void getMinHeightReturnsTheCustomSetMinHeightWhenSpecifiedEvenWhenThereIsNoSkin() {
c.setMinHeight(98.76);
assertEquals(98.76, c.minHeight(-1), 0.0);
}
@Test public void minHeightWhenNoSkinIsSetIsZeroByDefault() {
assertEquals(0, c.minHeight(-1), 0.0);
}
@Test public void resettingMinHeightTo_USE_PREF_SIZE_ReturnsThePrefHeightOfTheSkinNodeWhenThereIsASkin() {
c.setSkin(s);
c.setMinHeight(98.76);
c.setMinHeight(Control.USE_PREF_SIZE);
assertEquals(PREF_HEIGHT, c.minHeight(-1), 0.0);
}
@Test public void resettingMinHeightTo_USE_PREF_SIZE_ReturnsZeroWhenThereIsNoSkinAndPrefHeightIsNotSet() {
c.setMinHeight(98.76);
c.setMinHeight(Control.USE_PREF_SIZE);
assertEquals(0, c.minHeight(-1), 0.0);
}
@Test public void resettingMinHeightTo_USE_PREF_SIZE_ReturnsPrefHeightWhenThereIsNoSkinAndPrefHeightIsSet() {
c.setMinHeight(98.76);
c.setPrefHeight(105.2);
c.setMinHeight(Control.USE_PREF_SIZE);
assertEquals(105.2, c.minHeight(-1), 0.0);
}
@Test public void resettingMinHeightTo_USE_COMPUTED_SIZE_ReturnsTheMinHeightOfTheSkinNodeWhenThereIsASkin() {
c.setSkin(s);
c.setMinHeight(98.76);
c.setMinHeight(Control.USE_COMPUTED_SIZE);
assertEquals(MIN_HEIGHT, c.minHeight(-1), 0.0);
}
@Test public void resettingMinHeightTo_USE_COMPUTED_SIZE_ReturnsZeroWhenThereIsNoSkinAndPrefHeightIsNotSet() {
c.setMinHeight(98.76);
c.setMinHeight(Control.USE_COMPUTED_SIZE);
assertEquals(0, c.minHeight(-1), 0.0);
}
@Test public void minHeightIs_USE_COMPUTED_SIZE_ByDefault() {
assertEquals(Control.USE_COMPUTED_SIZE, c.getMinHeight(), 0);
assertEquals(Control.USE_COMPUTED_SIZE, c.minHeightProperty().get(), 0);
}
@Test public void minHeightCanBeSet() {
c.setMinHeight(98.76);
assertEquals(98.76, c.getMinHeight(), 0);
assertEquals(98.76, c.minHeightProperty().get(), 0);
}
@Test public void minHeightCanBeBound() {
DoubleProperty other = new SimpleDoubleProperty(939);
c.minHeightProperty().bind(other);
assertEquals(939, c.getMinHeight(), 0);
other.set(332);
assertEquals(332, c.getMinHeight(), 0);
}
@Test public void minHeightPropertyHasBeanReference() {
assertSame(c, c.minHeightProperty().getBean());
}
@Test public void minHeightPropertyHasName() {
assertEquals("minHeight", c.minHeightProperty().getName());
}
@Test public void callsToSetMaxWidthResultInRequestLayoutBeingCalledOnParentNotOnControl() {
Group parent = new Group();
parent.getChildren().add(c);
parent.layout();
assertTrue(!parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
c.setMaxWidth(500);
assertTrue(parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
}
@Test public void getMaxWidthReturnsTheMaxWidthOfTheSkinNode() {
c.setSkin(s);
assertEquals(MAX_WIDTH, c.maxWidth(-1), 0.0);
}
@Test public void getMaxWidthReturnsTheCustomSetMaxWidthWhenSpecified() {
c.setSkin(s);
c.setMaxWidth(500);
assertEquals(500, c.maxWidth(-1), 0.0);
}
@Test public void getMaxWidthReturnsTheCustomSetMaxWidthWhenSpecifiedEvenWhenThereIsNoSkin() {
c.setMaxWidth(500);
assertEquals(500, c.maxWidth(-1), 0.0);
}
@Test public void maxWidthWhenNoSkinIsSetIsZeroByDefault() {
assertEquals(0, c.maxWidth(-1), 0.0);
}
@Test public void resettingMaxWidthTo_USE_PREF_SIZE_ReturnsThePrefWidthOfTheSkinNodeWhenThereIsASkin() {
c.setSkin(s);
c.setMaxWidth(500);
c.setMaxWidth(Control.USE_PREF_SIZE);
assertEquals(PREF_WIDTH, c.maxWidth(-1), 0.0);
}
@Test public void resettingMaxWidthTo_USE_PREF_SIZE_ReturnsZeroWhenThereIsNoSkinAndPrefWidthIsNotSet() {
c.setMaxWidth(500);
c.setMaxWidth(Control.USE_PREF_SIZE);
assertEquals(0, c.maxWidth(-1), 0.0);
}
@Test public void resettingMaxWidthTo_USE_PREF_SIZE_ReturnsPrefWidthWhenThereIsNoSkinAndPrefWidthIsSet() {
c.setMaxWidth(500);
c.setPrefWidth(98.6);
c.setMaxWidth(Control.USE_PREF_SIZE);
assertEquals(98.6, c.maxWidth(-1), 0.0);
}
@Test public void resettingMaxWidthTo_USE_COMPUTED_SIZE_ReturnsTheMaxWidthOfTheSkinNodeWhenThereIsASkin() {
c.setSkin(s);
c.setMaxWidth(500);
c.setMaxWidth(Control.USE_COMPUTED_SIZE);
assertEquals(MAX_WIDTH, c.maxWidth(-1), 0.0);
}
@Test public void resettingMaxWidthTo_USE_COMPUTED_SIZE_ReturnsZeroWhenThereIsNoSkinAndPrefWidthIsNotSet() {
c.setMaxWidth(500);
c.setMaxWidth(Control.USE_COMPUTED_SIZE);
assertEquals(0, c.maxWidth(-1), 0.0);
}
@Test public void maxWidthIs_USE_COMPUTED_SIZE_ByDefault() {
assertEquals(Control.USE_COMPUTED_SIZE, c.getMaxWidth(), 0);
assertEquals(Control.USE_COMPUTED_SIZE, c.maxWidthProperty().get(), 0);
}
@Test public void maxWidthCanBeSet() {
c.setMaxWidth(500);
assertEquals(500, c.getMaxWidth(), 0);
assertEquals(500, c.maxWidthProperty().get(), 0);
}
@Test public void maxWidthCanBeBound() {
DoubleProperty other = new SimpleDoubleProperty(939);
c.maxWidthProperty().bind(other);
assertEquals(939, c.getMaxWidth(), 0);
other.set(332);
assertEquals(332, c.getMaxWidth(), 0);
}
@Test public void maxWidthPropertyHasBeanReference() {
assertSame(c, c.maxWidthProperty().getBean());
}
@Test public void maxWidthPropertyHasName() {
assertEquals("maxWidth", c.maxWidthProperty().getName());
}
@Test public void callsToSetMaxHeightResultInRequestLayoutBeingCalledOnParentNotOnControl() {
Group parent = new Group();
parent.getChildren().add(c);
parent.layout();
assertTrue(!parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
c.setMaxHeight(450);
assertTrue(parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
}
@Test public void getMaxHeightReturnsTheMaxHeightOfTheSkinNode() {
c.setSkin(s);
assertEquals(MAX_HEIGHT, c.maxHeight(-1), 0.0);
}
@Test public void getMaxHeightReturnsTheCustomSetMaxHeightWhenSpecified() {
c.setSkin(s);
c.setMaxHeight(450);
assertEquals(450, c.maxHeight(-1), 0.0);
}
@Test public void getMaxHeightReturnsTheCustomSetMaxHeightWhenSpecifiedEvenWhenThereIsNoSkin() {
c.setMaxHeight(500);
assertEquals(500, c.maxHeight(-1), 0.0);
}
@Test public void maxHeightWhenNoSkinIsSetIsZeroByDefault() {
assertEquals(0, c.maxHeight(-1), 0.0);
}
@Test public void resettingMaxHeightTo_USE_PREF_SIZE_ReturnsThePrefHeightOfTheSkinNodeWhenThereIsASkin() {
c.setSkin(s);
c.setMaxHeight(500);
c.setMaxHeight(Control.USE_PREF_SIZE);
assertEquals(PREF_HEIGHT, c.maxHeight(-1), 0.0);
}
@Test public void resettingMaxHeightTo_USE_PREF_SIZE_ReturnsZeroWhenThereIsNoSkinAndPrefHeightIsNotSet() {
c.setMaxHeight(500);
c.setMaxHeight(Control.USE_PREF_SIZE);
assertEquals(0, c.maxHeight(-1), 0.0);
}
@Test public void resettingMaxHeightTo_USE_PREF_SIZE_ReturnsPrefHeightWhenThereIsNoSkinAndPrefHeightIsSet() {
c.setMaxHeight(500);
c.setPrefHeight(105.2);
c.setMaxHeight(Control.USE_PREF_SIZE);
assertEquals(105.2, c.maxHeight(-1), 0.0);
}
@Test public void resettingMaxHeightTo_USE_COMPUTED_SIZE_ReturnsTheMaxHeightOfTheSkinNodeWhenThereIsASkin() {
c.setSkin(s);
c.setMaxHeight(500);
c.setMaxHeight(Control.USE_COMPUTED_SIZE);
assertEquals(MAX_HEIGHT, c.maxHeight(-1), 0.0);
}
@Test public void resettingMaxHeightTo_USE_COMPUTED_SIZE_ReturnsZeroWhenThereIsNoSkinAndPrefHeightIsNotSet() {
c.setMaxHeight(500);
c.setMaxHeight(Control.USE_COMPUTED_SIZE);
assertEquals(0, c.maxHeight(-1), 0.0);
}
@Test public void maxHeightIs_USE_COMPUTED_SIZE_ByDefault() {
assertEquals(Control.USE_COMPUTED_SIZE, c.getMaxHeight(), 0);
assertEquals(Control.USE_COMPUTED_SIZE, c.maxHeightProperty().get(), 0);
}
@Test public void maxHeightCanBeSet() {
c.setMaxHeight(500);
assertEquals(500, c.getMaxHeight(), 0);
assertEquals(500, c.maxHeightProperty().get(), 0);
}
@Test public void maxHeightCanBeBound() {
DoubleProperty other = new SimpleDoubleProperty(939);
c.maxHeightProperty().bind(other);
assertEquals(939, c.getMaxHeight(), 0);
other.set(332);
assertEquals(332, c.getMaxHeight(), 0);
}
@Test public void maxHeightPropertyHasBeanReference() {
assertSame(c, c.maxHeightProperty().getBean());
}
@Test public void maxHeightPropertyHasName() {
assertEquals("maxHeight", c.maxHeightProperty().getName());
}
@Test public void callsToSetPrefWidthResultInRequestLayoutBeingCalledOnParentNotOnControl() {
Group parent = new Group();
parent.getChildren().add(c);
parent.layout();
assertTrue(!parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
c.setPrefWidth(80);
assertTrue(parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
}
@Test public void getPrefWidthReturnsThePrefWidthOfTheSkinNode() {
c.setSkin(s);
assertEquals(PREF_WIDTH, c.prefWidth(-1), 0.0);
}
@Test public void getPrefWidthReturnsTheCustomSetPrefWidthWhenSpecified() {
c.setSkin(s);
c.setPrefWidth(80);
assertEquals(80, c.prefWidth(-1), 0.0);
}
@Test public void getPrefWidthReturnsTheCustomSetPrefWidthWhenSpecifiedEvenWhenThereIsNoSkin() {
c.setPrefWidth(80);
assertEquals(80, c.prefWidth(-1), 0.0);
}
@Test public void prefWidthWhenNoSkinIsSetIsZeroByDefault() {
assertEquals(0, c.prefWidth(-1), 0.0);
}
@Ignore ("What should happen when the pref width is set to USE_PREF_SIZE? Seems it should be an exception")
@Test public void resettingPrefWidthTo_USE_PREF_SIZE_ThrowsExceptionWhenThereIsASkin() {
c.setSkin(s);
c.setPrefWidth(80);
c.setPrefWidth(Control.USE_PREF_SIZE);
assertEquals(PREF_WIDTH, c.prefWidth(-1), 0.0);
}
@Test public void resettingPrefWidthTo_USE_COMPUTED_SIZE_ReturnsThePrefWidthOfTheSkinNodeWhenThereIsASkin() {
c.setSkin(s);
c.setPrefWidth(80);
c.setPrefWidth(Control.USE_COMPUTED_SIZE);
assertEquals(PREF_WIDTH, c.prefWidth(-1), 0.0);
}
@Test public void resettingPrefWidthTo_USE_COMPUTED_SIZE_ReturnsZeroWhenThereIsNoSkinAndPrefWidthIsNotSet() {
c.setPrefWidth(80);
c.setPrefWidth(Control.USE_COMPUTED_SIZE);
assertEquals(0, c.prefWidth(-1), 0.0);
}
@Test public void prefWidthIs_USE_COMPUTED_SIZE_ByDefault() {
assertEquals(Control.USE_COMPUTED_SIZE, c.getPrefWidth(), 0);
assertEquals(Control.USE_COMPUTED_SIZE, c.prefWidthProperty().get(), 0);
}
@Test public void prefWidthCanBeSet() {
c.setPrefWidth(80);
assertEquals(80, c.getPrefWidth(), 0);
assertEquals(80, c.prefWidthProperty().get(), 0);
}
@Test public void prefWidthCanBeBound() {
DoubleProperty other = new SimpleDoubleProperty(939);
c.prefWidthProperty().bind(other);
assertEquals(939, c.getPrefWidth(), 0);
other.set(332);
assertEquals(332, c.getPrefWidth(), 0);
}
@Test public void prefWidthPropertyHasBeanReference() {
assertSame(c, c.prefWidthProperty().getBean());
}
@Test public void prefWidthPropertyHasName() {
assertEquals("prefWidth", c.prefWidthProperty().getName());
}
@Test public void callsToSetPrefHeightResultInRequestLayoutBeingCalledOnParentNotOnControl() {
Group parent = new Group();
parent.getChildren().add(c);
parent.layout();
assertTrue(!parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
c.setPrefHeight(92);
assertTrue(parent.isNeedsLayout());
assertTrue(!c.isNeedsLayout());
}
@Test public void getPrefHeightReturnsThePrefHeightOfTheSkinNode() {
c.setSkin(s);
assertEquals(PREF_HEIGHT, c.prefHeight(-1), 0.0);
}
@Test public void getPrefHeightReturnsTheCustomSetPrefHeightWhenSpecified() {
c.setSkin(s);
c.setPrefHeight(92);
assertEquals(92, c.prefHeight(-1), 0.0);
}
@Test public void getPrefHeightReturnsTheCustomSetPrefHeightWhenSpecifiedEvenWhenThereIsNoSkin() {
c.setPrefHeight(92);
assertEquals(92, c.prefHeight(-1), 0.0);
}
@Test public void prefHeightWhenNoSkinIsSetIsZeroByDefault() {
assertEquals(0, c.prefHeight(-1), 0.0);
}
@Ignore ("What should happen when the pref width is set to USE_PREF_SIZE? Seems it should be an exception")
@Test public void resettingPrefHeightTo_USE_PREF_SIZE_ThrowsExceptionWhenThereIsASkin() {
c.setSkin(s);
c.setPrefHeight(92);
c.setPrefHeight(Control.USE_PREF_SIZE);
assertEquals(PREF_HEIGHT, c.prefHeight(-1), 0.0);
}
@Test public void resettingPrefHeightTo_USE_COMPUTED_SIZE_ReturnsThePrefHeightOfTheSkinNodeWhenThereIsASkin() {
c.setSkin(s);
c.setPrefHeight(92);
c.setPrefHeight(Control.USE_COMPUTED_SIZE);
assertEquals(PREF_HEIGHT, c.prefHeight(-1), 0.0);
}
@Test public void resettingPrefHeightTo_USE_COMPUTED_SIZE_ReturnsZeroWhenThereIsNoSkinAndPrefHeightIsNotSet() {
c.setPrefHeight(92);
c.setPrefHeight(Control.USE_COMPUTED_SIZE);
assertEquals(0, c.prefHeight(-1), 0.0);
}
@Test public void prefHeightIs_USE_COMPUTED_SIZE_ByDefault() {
assertEquals(Control.USE_COMPUTED_SIZE, c.getPrefHeight(), 0);
assertEquals(Control.USE_COMPUTED_SIZE, c.prefHeightProperty().get(), 0);
}
@Test public void prefHeightCanBeSet() {
c.setPrefHeight(98.76);
assertEquals(98.76, c.getPrefHeight(), 0);
assertEquals(98.76, c.prefHeightProperty().get(), 0);
}
@Test public void prefHeightCanBeBound() {
DoubleProperty other = new SimpleDoubleProperty(939);
c.prefHeightProperty().bind(other);
assertEquals(939, c.getPrefHeight(), 0);
other.set(332);
assertEquals(332, c.getPrefHeight(), 0);
}
@Test public void prefHeightPropertyHasBeanReference() {
assertSame(c, c.prefHeightProperty().getBean());
}
@Test public void prefHeightPropertyHasName() {
assertEquals("prefHeight", c.prefHeightProperty().getName());
}
@Test public void skinIsNullByDefault() {
assertNull(c.getSkin());
assertNull(c.contextMenuProperty().get());
}
@Test public void skinCanBeSet() {
c.setSkin(s);
assertSame(s, c.getSkin());
assertSame(s, c.skinProperty().get());
}
@Test public void skinCanBeCleared() {
c.setSkin(s);
c.setSkin(null);
assertNull(c.getSkin());
assertNull(c.skinProperty().get());
}
@Test public void skinCanBeBound() {
ObjectProperty other = new SimpleObjectProperty(s);
c.skinProperty().bind(other);
assertSame(s, c.getSkin());
other.set(null);
assertNull(c.getSkin());
}
@Test public void skinPropertyHasBeanReference() {
assertSame(c, c.skinProperty().getBean());
}
@Test public void skinPropertyHasName() {
assertEquals("skin", c.skinProperty().getName());
}
@Test public void canSpecifySkinViaCSS() {
disableLogging();
try {
((StyleableProperty)ControlShim.skinClassNameProperty(c)).applyStyle(null, "test.javafx.scene.control.SkinStub");
assertNotNull(c.getSkin());
assertTrue(c.getSkin() instanceof SkinStub);
assertSame(c, c.getSkin().getSkinnable());
} finally {
enableLogging();
}
}
@Test public void specifyingSameSkinTwiceViaCSSDoesntSetTwice() {
disableLogging();
try {
SkinChangeListener listener = new SkinChangeListener();
c.skinProperty().addListener(listener);
((StyleableProperty)ControlShim.skinClassNameProperty(c)).applyStyle(null, "test.javafx.scene.control.SkinStub");
assertTrue(listener.changed);
listener.changed = false;
((StyleableProperty)ControlShim.skinClassNameProperty(c)).applyStyle(null, "test.javafx.scene.control.SkinStub");
assertFalse(listener.changed);
} finally {
enableLogging();
}
}
@Test public void specifyingNullSkinNameHasNoEffect() {
disableLogging();
try {
SkinChangeListener listener = new SkinChangeListener();
c.skinProperty().addListener(listener);
((StyleableProperty)ControlShim.skinClassNameProperty(c)).applyStyle(null, null);
assertFalse(listener.changed);
assertNull(c.getSkin());
} finally {
enableLogging();
}
}
@Test public void specifyingEmptyStringSkinNameHasNoEffect() {
disableLogging();
try {
SkinChangeListener listener = new SkinChangeListener();
c.skinProperty().addListener(listener);
((StyleableProperty)ControlShim.skinClassNameProperty(c)).applyStyle(null, "");
assertFalse(listener.changed);
assertNull(c.getSkin());
} finally {
enableLogging();
}
}
@Test public void loadingSkinWithNoAppropriateConstructorResultsInNoSkin() {
disableLogging();
try {
SkinChangeListener listener = new SkinChangeListener();
c.skinProperty().addListener(listener);
((StyleableProperty)ControlShim.skinClassNameProperty(c)).applyStyle(null, "test.javafx.scene.control.ControlTest$BadSkin");
assertFalse(listener.changed);
assertNull(c.getSkin());
} finally {
enableLogging();
}
}
@Test public void exceptionThrownDuringSkinConstructionResultsInNoSkin() {
disableLogging();
try {
SkinChangeListener listener = new SkinChangeListener();
c.skinProperty().addListener(listener);
((StyleableProperty)ControlShim.skinClassNameProperty(c)).applyStyle(null, "test.javafx.scene.control.ControlTest$ExceptionalSkin");
assertFalse(listener.changed);
assertNull(c.getSkin());
} finally {
enableLogging();
}
}
@Test public void tooltipIsNullByDefault() {
assertNull(c.getTooltip());
assertNull(c.tooltipProperty().get());
}
@Test public void tooltipCanBeSet() {
Tooltip tip = new Tooltip("Hello");
c.setTooltip(tip);
assertSame(tip, c.getTooltip());
assertSame(tip, c.tooltipProperty().get());
}
@Test public void tooltipCanBeCleared() {
Tooltip tip = new Tooltip("Hello");
c.setTooltip(tip);
c.setTooltip(null);
assertNull(c.getTooltip());
assertNull(c.tooltipProperty().get());
}
@Test public void tooltipCanBeBound() {
Tooltip tip = new Tooltip("Hello");
ObjectProperty<Tooltip> other = new SimpleObjectProperty<Tooltip>(tip);
c.tooltipProperty().bind(other);
assertSame(tip, c.getTooltip());
assertSame(tip, c.tooltipProperty().get());
other.set(null);
assertNull(c.getTooltip());
assertNull(c.tooltipProperty().get());
}
@Test public void tooltipPropertyHasBeanReference() {
assertSame(c, c.tooltipProperty().getBean());
}
@Test public void tooltipPropertyHasName() {
assertEquals("tooltip", c.tooltipProperty().getName());
}
@Test public void contextMenuIsNullByDefault() {
assertNull(c.getContextMenu());
assertNull(c.contextMenuProperty().get());
}
@Test public void contextMenuCanBeSet() {
ContextMenu menu = new ContextMenu();
c.setContextMenu(menu);
assertSame(menu, c.getContextMenu());
assertSame(menu, c.contextMenuProperty().get());
}
@Test public void contextMenuCanBeCleared() {
ContextMenu menu = new ContextMenu();
c.setContextMenu(menu);
c.setContextMenu(null);
assertNull(c.getContextMenu());
assertNull(c.getContextMenu());
}
@Test public void contextMenuCanBeBound() {
ContextMenu menu = new ContextMenu();
ObjectProperty<ContextMenu> other = new SimpleObjectProperty<ContextMenu>(menu);
c.contextMenuProperty().bind(other);
assertSame(menu, c.getContextMenu());
assertSame(menu, c.contextMenuProperty().get());
other.set(null);
assertNull(c.getContextMenu());
assertNull(c.contextMenuProperty().get());
}
@Test public void contextMenuPropertyHasBeanReference() {
assertSame(c, c.contextMenuProperty().getBean());
}
@Test public void contextMenuPropertyHasName() {
assertEquals("contextMenu", c.contextMenuProperty().getName());
}
@Test public void setMinSizeUpdatesBothMinWidthAndMinHeight() {
c.setMinSize(123.45, 98.6);
assertEquals(123.45, c.getMinWidth(), 0);
assertEquals(98.6, c.getMinHeight(), 0);
}
@Test public void setMaxSizeUpdatesBothMaxWidthAndMaxHeight() {
c.setMaxSize(658.9, 373.4);
assertEquals(658.9, c.getMaxWidth(), 0);
assertEquals(373.4, c.getMaxHeight(), 0);
}
@Test public void setPrefSizeUpdatesBothPrefWidthAndPrefHeight() {
c.setPrefSize(720, 540);
assertEquals(720, c.getPrefWidth(), 0);
assertEquals(540, c.getPrefHeight(), 0);
}
@Test public void baselineOffsetIsZeroWhenThereIsNoSkin() {
assertEquals(0, c.getBaselineOffset(), 0f);
}
@Test public void baselineOffsetUpdatedWhenTheSkinChanges() {
c.setSkin(s);
assertEquals(BASELINE_OFFSET, c.getBaselineOffset(), 0);
}
@Test
public void testRT18097() {
try {
File f = System.getProperties().containsKey("CSS_META_DATA_TEST_DIR") ?
new File(System.getProperties().get("CSS_META_DATA_TEST_DIR").toString()) :
null;
if (f == null) {
ClassLoader cl = Thread.currentThread().getContextClassLoader();
URL base = cl.getResource("test/javafx/../javafx");
f = new File(base.toURI());
}
assertTrue("" + f.getCanonicalPath() + " is not a directory", f.isDirectory());
recursiveCheck(f, f.getPath().length() - 7);
} catch (Exception ex) {
ex.printStackTrace(System.err);
fail(ex.getMessage());
}
}
private static void checkClass(Class someClass) {
if (someClass.getEnclosingClass() != null) return;
if (javafx.scene.control.Control.class.isAssignableFrom(someClass) &&
Modifier.isAbstract(someClass.getModifiers()) == false &&
Modifier.isPrivate(someClass.getModifiers()) == false) {
String what = someClass.getName();
try {
Method m = someClass.getMethod("getClassCssMetaData", (Class[]) null);
Node node = (Node)someClass.getDeclaredConstructor().newInstance();
for (CssMetaData styleable : (List<CssMetaData<? extends Styleable, ?>>) m.invoke(null)) {
what = someClass.getName() + " " + styleable.getProperty();
WritableValue writable = styleable.getStyleableProperty(node);
assertNotNull(what, writable);
Object defaultValue = writable.getValue();
Object initialValue = styleable.getInitialValue((Node) someClass.getDeclaredConstructor().newInstance());
if (defaultValue instanceof Number) {
assert(initialValue instanceof Number);
double d1 = ((Number)defaultValue).doubleValue();
double d2 = ((Number)initialValue).doubleValue();
assertEquals(what, d1, d2, .001);
} else if (defaultValue != null && defaultValue.getClass().isArray()) {
assertTrue(what, Arrays.equals((Object[])defaultValue, (Object[])initialValue));
} else {
assertEquals(what, defaultValue, initialValue);
}
}
} catch (NoSuchMethodException ex) {
fail("NoSuchMethodException: RT-18097 cannot be tested on " + what);
} catch (IllegalAccessException ex) {
System.err.println("IllegalAccessException:  RT-18097 cannot be tested on " + what);
} catch (IllegalArgumentException ex) {
fail("IllegalArgumentException:  RT-18097 cannot be tested on " + what);
} catch (InvocationTargetException ex) {
fail("InvocationTargetException:  RT-18097 cannot be tested on " + what);
} catch (InstantiationException ex) {
fail("InstantiationException:  RT-18097 cannot be tested on " + what);
}
}
}
private static void checkDirectory(File directory, final int pathLength) {
if (directory.isDirectory()) {
for (File file : directory.listFiles()) {
if (file.isFile() && file.getName().endsWith(".class")) {
final String filePath = file.getPath();
final int len = file.getPath().length() - ".class".length();
final String clName =
file.getPath().substring(pathLength+1, len).replace(File.separatorChar,'.');
if (clName.startsWith("javafx.scene") == false) continue;
try {
final Class cl = Class.forName(clName);
if (cl != null) checkClass(cl);
} catch(ClassNotFoundException ex) {
System.err.println(ex.toString() + " " + clName);
}
}
}
}
}
private static void recursiveCheck(File directory, int pathLength) {
if (directory.isDirectory()) {
checkDirectory(directory, pathLength);
for (File subFile : directory.listFiles()) {
recursiveCheck(subFile, pathLength);
}
}
}
public class ResizableRectangle extends Rectangle {
private double minWidth;
private double maxWidth;
private double minHeight;
private double maxHeight;
private double prefWidth;
private double prefHeight;
private double baselineOffset;
@Override public boolean isResizable() { return true; }
@Override public double minWidth(double h) { return minWidth; }
@Override public double minHeight(double w) { return minHeight; }
@Override public double prefWidth(double height) { return prefWidth; }
@Override public double prefHeight(double width) { return prefHeight; }
@Override public double maxWidth(double h) { return maxWidth; }
@Override public double maxHeight(double w) { return maxHeight; }
@Override public double getBaselineOffset() { return baselineOffset; }
@Override public void resize(double width, double height) {
setWidth(width);
setHeight(height);
}
}
public static final class BadSkin<C extends Control> extends SkinStub<C> {
public BadSkin() {
super(null);
}
}
public static final class ExceptionalSkin<C extends Control> extends SkinStub<C> {
public ExceptionalSkin(C control) {
super(control);
throw new NullPointerException("I am EXCEPTIONAL!");
}
}
public class SkinChangeListener implements ChangeListener<Skin> {
boolean changed = false;
@Override public void changed(ObservableValue<? extends Skin> observable, Skin oldValue, Skin newValue) {
changed = true;
}
}
}

package test.javafx.scene.control.skin;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.sun.javafx.scene.control.TabObservableList;
import com.sun.javafx.tk.Toolkit;
import static javafx.scene.control.skin.TabPaneSkinShim.*;
import static org.junit.Assert.*;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.skin.TabPaneSkin;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class TabPaneHeaderScrollTest {
private static final int TAB_COUNT = 30;
private static final int THIRD_OF = TAB_COUNT / 3;
private static final int FITTING = 7;
private Scene scene;
private Stage stage;
private Pane root;
private TabPane tabPane;
@Test
public void testMoveBySetAll() {
showTabPane();
int last = tabPane.getTabs().size() - 1;
tabPane.getSelectionModel().select(last);
Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
Toolkit.getToolkit().firePulse();
List<Tab> tabs = new ArrayList<>(tabPane.getTabs());
tabs.remove(selectedTab);
tabs.add(0, selectedTab);
tabPane.getTabs().setAll(tabs);
Toolkit.getToolkit().firePulse();
assertEquals("scrolled to leading edge", 0, getHeaderAreaScrollOffset(tabPane), 1);
}
@Test
public void testMoveByTabObservableList() {
showTabPane();
int last = tabPane.getTabs().size() - 1;
tabPane.getSelectionModel().select(last);
Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
Toolkit.getToolkit().firePulse();
((TabObservableList<Tab>) tabPane.getTabs()).reorder(selectedTab, tabPane.getTabs().get(0));
Toolkit.getToolkit().firePulse();
assertEquals("scrolled to leading edge", 0, getHeaderAreaScrollOffset(tabPane), 1);
}
@Test
public void testRemoveSelectedAsLast() {
showTabPane();
int last = tabPane.getTabs().size() - 1;
Tab secondLastTab = tabPane.getTabs().get(last - 1);
Tab lastTab = tabPane.getTabs().get(last);
tabPane.getSelectionModel().select(last);
Toolkit.getToolkit().firePulse();
double scrollOffset = getHeaderAreaScrollOffset(tabPane);
double lastTabOffset = getTabHeaderOffset(tabPane, lastTab);
double secondLastTabOffset = getTabHeaderOffset(tabPane, secondLastTab);
double expectedDelta = lastTabOffset - secondLastTabOffset;
tabPane.getTabs().remove(last);
Toolkit.getToolkit().firePulse();
assertEquals("scrollOffset adjusted: ", scrollOffset + expectedDelta, getHeaderAreaScrollOffset(tabPane), 1);
}
@Test
public void testRemoveLastIfSelectedIsSecondLast() {
showTabPane();
int last = tabPane.getTabs().size() - 1;
Tab lastTab = tabPane.getTabs().get(last);
int secondLast = last - 1;
Tab secondLastTab = tabPane.getTabs().get(secondLast);
tabPane.getSelectionModel().select(last);
Toolkit.getToolkit().firePulse();
double scrollOffset = getHeaderAreaScrollOffset(tabPane);
double lastTabOffest = getTabHeaderOffset(tabPane, lastTab);
double secondeLastTabOffset = getTabHeaderOffset(tabPane, secondLastTab);
double expectedDelta = lastTabOffest - secondeLastTabOffset;
tabPane.getSelectionModel().select(secondLast);
Toolkit.getToolkit().firePulse();
tabPane.getTabs().remove(last);
Toolkit.getToolkit().firePulse();
assertEquals("scrollOffset adjusted: ", scrollOffset + expectedDelta, getHeaderAreaScrollOffset(tabPane), 1);
}
@Test
public void testRemoveBefore() {
showTabPane();
int selected = 4;
tabPane.getSelectionModel().select(selected);
Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
Toolkit.getToolkit().firePulse();
double selectedTabOffset = getTabHeaderOffset(tabPane, selectedTab);
double scrollOffset = getHeaderAreaScrollOffset(tabPane);
assertEquals("sanity: tab visible but not scrolled", 0, scrollOffset, 1);
setHeaderAreaScrollOffset(tabPane, - selectedTabOffset);
Toolkit.getToolkit().firePulse();
assertEquals("sanity: really scrolled", - selectedTabOffset, getHeaderAreaScrollOffset(tabPane), 1);
tabPane.getTabs().remove(0);
Toolkit.getToolkit().firePulse();
assertEquals("scroll offset", - getTabHeaderOffset(tabPane, selectedTab), getHeaderAreaScrollOffset(tabPane), 1);
}
@Test
public void testAddBefore() {
showTabPane();
int last = tabPane.getTabs().size() - 1;
tabPane.getSelectionModel().select(last);
Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
Toolkit.getToolkit().firePulse();
double selectedTabOffset = getTabHeaderOffset(tabPane, selectedTab);
double scrollOffset = getHeaderAreaScrollOffset(tabPane);
Tab added = new Tab("added", new Label("added"));
tabPane.getTabs().add(0, added);
Toolkit.getToolkit().firePulse();
Node addedHeader = getTabHeaderFor(tabPane, added);
double addedWidth = addedHeader.prefWidth(-1);
assertEquals("sanity", selectedTabOffset + addedWidth, getTabHeaderOffset(tabPane, selectedTab), 1);
assertEquals("scroll offset", scrollOffset - addedWidth, getHeaderAreaScrollOffset(tabPane), 1);
}
@Test
public void testDecreaseWidth() {
assertScrollOnDecreaseSize(Side.TOP);
}
@Test
public void testDecreaseHeight() {
assertScrollOnDecreaseSize(Side.RIGHT);
}
private void assertScrollOnDecreaseSize(Side side) {
TabPane tabPane = createTabPane(FITTING);
tabPane.setSide(side);
showTabPane(tabPane);
tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
Toolkit.getToolkit().firePulse();
Node header = getSelectedTabHeader(tabPane);
double tabOffset = getTabHeaderOffset(tabPane, tabPane.getSelectionModel().getSelectedItem());
double noScrollOffset = getHeaderAreaScrollOffset(tabPane);
assertEquals("scrollOffset for fitting tabs", 0, noScrollOffset, 1);
assertEquals("bounds minX", tabOffset, header.getBoundsInParent().getMinX(), 1);
if (side.isHorizontal()) {
tabPane.setMaxWidth(stage.getWidth()/2);
} else {
tabPane.setMaxHeight(stage.getHeight()/2);
}
Toolkit.getToolkit().firePulse();
double scrollOffset = getHeaderAreaScrollOffset(tabPane);
assertFalse("sanity: not fitting after resize", isTabsFit(tabPane));
assertTrue("header must be scrolled", scrollOffset < 0);
assertEquals("bounds minX", tabOffset, - scrollOffset + header.getBoundsInParent().getMinX(), 0);
}
@Test
public void testTabsFitHorizontal() {
assertTabsFit(Side.TOP);
}
@Test
public void testTabsFitVertical() {
assertTabsFit(Side.RIGHT);
}
private void assertTabsFit(Side side) {
TabPane tabPane = createTabPane(FITTING);
tabPane.setSide(side);
showTabPane(tabPane);
assertTrue(isTabsFit(tabPane));
tabPane.getTabs().add(new Tab("tab + x"));
Toolkit.getToolkit().firePulse();
assertFalse(isTabsFit(tabPane));
}
@Test
public void testChangeSide() {
showTabPane();
tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
Toolkit.getToolkit().firePulse();
tabPane.setSide(Side.BOTTOM);
Toolkit.getToolkit().firePulse();
assertScrolledToLastAndBack();
}
@Test
public void testInitialSelect() {
tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
showTabPane();
assertScrolledToLastAndBack();
}
@Test
public void testSelect() {
showTabPane();
assertEquals(0, getHeaderAreaScrollOffset(tabPane), 1);
tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
Toolkit.getToolkit().firePulse();
assertScrolledToLastAndBack();
}
private void assertScrolledToLastAndBack() {
Node firstHeader = getTabHeaderFor(tabPane, tabPane.getTabs().get(0));
double scrollPerTab = firstHeader.prefWidth(-1);
double scrollPerThirdOfTabs = THIRD_OF * scrollPerTab;
double scrollOffset = getHeaderAreaScrollOffset(tabPane);
assertTrue("scrollOffset must be negative", scrollOffset < 0);
assertTrue("scrollOffset " + scrollOffset + "must be much greater than multiple tab widths " + scrollPerThirdOfTabs ,
scrollPerThirdOfTabs < - scrollOffset);
tabPane.getSelectionModel().select(1);
Toolkit.getToolkit().firePulse();
assertEquals("scrollOffset", scrollPerTab, - getHeaderAreaScrollOffset(tabPane), 1);
}
protected void showTabPane() {
showTabPane(tabPane);
}
protected void showTabPane(TabPane tabPane) {
if (root == null) {
root = new VBox();
scene = new Scene(root, 600, 600);
stage = new Stage();
stage.setScene(scene);
stage.show();
}
root.getChildren().setAll(tabPane);
Toolkit.getToolkit().firePulse();
disableAnimations((TabPaneSkin) tabPane.getSkin());
}
@Test
public void testShowAlternativeTabPane() {
showTabPane();
List<Node> expected = List.of(tabPane);
assertEquals(expected, root.getChildren());
TabPane alternativeTabPane = createTabPane();
showTabPane(alternativeTabPane);
List<Node> alternative = List.of(alternativeTabPane);
assertEquals(alternative, root.getChildren());
}
@Test
public void testShowTabPane() {
assertNotNull(tabPane);
assertSame(Side.TOP, tabPane.getSide());
showTabPane();
List<Node> expected = List.of(tabPane);
assertEquals(expected, root.getChildren());
}
protected TabPane createTabPane() {
return createTabPane(TAB_COUNT);
}
protected TabPane createTabPane(int max) {
TabPane tabPane = new TabPane();
for (int i = 0; i < max; i++) {
Tab tab = new Tab("Tab " + i, new Label("Content for " + i));
tabPane.getTabs().add(tab);
}
return tabPane;
}
@Before
public void setup() {
Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
if (throwable instanceof RuntimeException) {
throw (RuntimeException)throwable;
} else {
Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
}
});
tabPane = createTabPane();
}
@After
public void cleanup() {
if (stage != null) stage.hide();
Thread.currentThread().setUncaughtExceptionHandler(null);
}
public static double getTabHeaderOffset(TabPane tabPane, Tab tab) {
Objects.requireNonNull(tabPane, "tabPane must not be null");
Objects.requireNonNull(tab, "tab must not be null");
if (!tabPane.getTabs().contains(tab)) throw new IllegalStateException("tab must be contained");
List<Node> headers = getTabHeaders(tabPane);
double offset = 0;
for (Node node : headers) {
if (getTabFor(node) == tab) break;
offset += node.prefWidth(-1);
}
return offset;
}
public static Node getSelectedTabHeader(TabPane tabPane) {
Objects.requireNonNull(tabPane, "tabPane must not be null");
if (tabPane.getTabs().isEmpty()) throw new IllegalStateException("tabs must not be empty");
Tab tab = tabPane.getSelectionModel().getSelectedItem();
return getTabHeaderFor(tabPane, tab);
}
public static Node getTabHeaderFor(TabPane tabPane, Tab tab) {
Objects.requireNonNull(tabPane, "tabPane must not be null");
Objects.requireNonNull(tab, "tab must not be null");
if (!tabPane.getTabs().contains(tab)) throw new IllegalStateException("tab must be contained");
List<Node> headers = getTabHeaders(tabPane);
Optional<Node> tabHeader = headers.stream()
.filter(node -> getTabFor(node) == tab)
.findFirst();
return tabHeader.get();
}
public static Tab getTabFor(Node tabHeader) {
Objects.requireNonNull(tabHeader, "tabHeader must not be null");
Object tab = tabHeader.getProperties().get(Tab.class);
if (tab instanceof Tab) return (Tab) tab;
throw new IllegalStateException("node is not a tabHeader " + tabHeader);
}
}

package test.javafx.scene.control;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import static org.junit.Assert.assertEquals;
public class DialogPaneTest {
private StageLoader sl;
private DialogPane dialogPane;
@Before
public void setup() {
dialogPane = new DialogPane();
sl = new StageLoader(dialogPane);
}
@After
public void after() {
sl.dispose();
}
@Test
public void test_graphic_padding_noHeader() {
dialogPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("no-header"), true);
final ImageView graphic = new ImageView(new Image(ContextMenuTest.class.getResource("icon.png").toExternalForm()));
dialogPane.setGraphic(graphic);
dialogPane.applyCss();
final StackPane graphicContainer = (StackPane) graphic.getParent();
final Insets padding = graphicContainer.getPadding();
final double fontSize = Font.getDefault().getSize();
assertEquals(0.833 * fontSize, padding.getTop(), 0.01);
assertEquals(0, padding.getRight(), 0.0);
assertEquals(0, padding.getBottom(), 0.0);
assertEquals(0.833 * fontSize, padding.getLeft(), 0.01);
}
@Test
public void testLookupButtonIsReturningCorrectButton() {
String id1 = "Test";
dialogPane.getButtonTypes().setAll(ButtonType.OK);
assertEquals(1, dialogPane.getButtonTypes().size());
Node button = dialogPane.lookupButton(ButtonType.OK);
button.setId(id1);
verifyIdOfButtonInButtonBar(id1);
String id2 = "Test2";
dialogPane.getButtonTypes().setAll(ButtonType.OK);
assertEquals(1, dialogPane.getButtonTypes().size());
button = dialogPane.lookupButton(ButtonType.OK);
button.setId(id2);
verifyIdOfButtonInButtonBar(id2);
}
private void verifyIdOfButtonInButtonBar(String id) {
for (Node children : dialogPane.getChildren()) {
if (children instanceof ButtonBar) {
ObservableList<Node> buttons = ((ButtonBar) children).getButtons();
assertEquals(1, buttons.size());
Node button = buttons.get(0);
assertEquals(id, button.getId());
}
}
}
}

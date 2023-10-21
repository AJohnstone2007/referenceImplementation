package test.javafx.scene.control;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.*;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.shape.Rectangle;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
public class SeparatorMenuItemTest {
private SeparatorMenuItem separatorMenuItem, smi;
private Node node;
private Toolkit tk;
@Before public void setup() {
tk = (StubToolkit)Toolkit.getToolkit();
node = new Rectangle();
separatorMenuItem = smi = new SeparatorMenuItem();
}
@Test public void defaultConstructorShouldSetStyleClassTo_separatormenuitem() {
assertStyleClassContains(separatorMenuItem, "separator-menu-item");
}
@Test public void defaultSeparatorNotNullAndHorizontal() {
assertNotNull(separatorMenuItem.getContent());
assertTrue(separatorMenuItem.getContent() instanceof Separator);
assertSame(((Separator)(separatorMenuItem.getContent())).getOrientation(), Orientation.HORIZONTAL);
}
@Test public void defaultHideOnClickFalse() {
assertFalse(separatorMenuItem.isHideOnClick());
}
@Test public void defaultConstructorShouldHaveNotNullContent() {
assertNotNull(smi.getContent());
}
@Test public void defaultConstructorShouldBeSeparator() {
assertTrue(smi.getContent() instanceof Separator);
}
@Test public void defaultConstructorShouldBeHorizontalSeparator() {
Separator sep = (Separator)(smi.getContent());
assertEquals(Orientation.HORIZONTAL, sep.getOrientation());
}
@Test public void defaultConstructorCanChangeSeparatorOrientation() {
Separator sep = (Separator)(smi.getContent());
sep.setOrientation(Orientation.VERTICAL);
assertEquals(Orientation.VERTICAL, sep.getOrientation());
}
@Test public void defaultConstructorShouldHaveFalseHideClick() {
assertFalse(smi.isHideOnClick());
}
@Test public void defaultConstructorShouldHaveNullGraphic() {
assertNull(smi.getGraphic());
}
@Test public void defaultConstructorShouldHaveNullText() {
assertNull(smi.getText());
}
}

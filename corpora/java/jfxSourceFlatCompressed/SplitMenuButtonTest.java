package test.javafx.scene.control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class SplitMenuButtonTest {
private SplitMenuButton splitMenuButton;
@Before public void setup() {
splitMenuButton = new SplitMenuButton();
}
@Test public void defaultConstructorHasSizeZeroItems() {
assertEquals(0, splitMenuButton.getItems().size());
}
@Test public void defaultConstructorHasTrueMnemonicParsing() {
assertTrue(splitMenuButton.isMnemonicParsing());
}
@Test public void oneArgConstructorNullHasSizeZeroItems() {
SplitMenuButton smb2 = new SplitMenuButton((MenuItem[])null);
assertEquals(0, smb2.getItems().size());
}
@Test public void oneArgConstructorNullHasTrueMnemonicParsing() {
SplitMenuButton smb2 = new SplitMenuButton((MenuItem[])null);
assertTrue(smb2.isMnemonicParsing());
}
@Test public void oneArgConstructorSpecifiedMenuItemHasGreaterThanZeroItems() {
SplitMenuButton smb2 = new SplitMenuButton(new MenuItem());
assertTrue(smb2.getItems().size() > 0);
}
@Test public void oneArgConstructorSpecifiedMenuItemHasTrueMnemonicParsing() {
SplitMenuButton smb2 = new SplitMenuButton(new MenuItem());
assertTrue(smb2.isMnemonicParsing());
}
@Test public void oneArgConstructorSpecifiedMenuItemsHasGreaterThanZeroItems() {
SplitMenuButton smb2 = new SplitMenuButton(new MenuItem("One"), new MenuItem("Two"));
assertTrue(smb2.getItems().size() > 0);
}
@Test public void oneArgConstructorSpecifiedMenuItemsHasTrueMnemonicParsing() {
SplitMenuButton smb2 = new SplitMenuButton(new MenuItem("One"), new MenuItem("Two"));
assertTrue(smb2.isMnemonicParsing());
}
@Test public void splitMenuButtonIsFiredIsNoOp() {
splitMenuButton.fire();
}
}

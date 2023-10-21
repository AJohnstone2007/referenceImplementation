package test.com.sun.javafx.scene;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public final class KeyboardShortcutsTest {
private Stage stage;
private Scene scene;
@Before
public void setUp() {
stage = new Stage();
scene = new Scene(new Group(), 500, 500);
stage.setScene(scene);
stage.show();
}
@After
public void tearDown() {
stage = null;
scene = null;
}
@Test
public void addMnemonicTest() {
boolean nodeFound = false;
final Text node = new Text("text");
((Group)scene.getRoot()).getChildren().add(node);
KeyCodeCombination mnemonicKeyCombo =
new KeyCodeCombination(KeyCode.Q,KeyCombination.ALT_DOWN);
Mnemonic myMnemonic = new Mnemonic(node, mnemonicKeyCombo);
scene.addMnemonic(myMnemonic);
ObservableList<Mnemonic> mnemonicsList = scene.getMnemonics().get(mnemonicKeyCombo);
if (mnemonicsList != null) {
for (int i = 0 ; i < mnemonicsList.size() ; i++) {
if (mnemonicsList.get(i).getNode() == node) {
nodeFound = true;
}
}
}
assertTrue(nodeFound);
}
@Test
public void addAndRemoveMnemonicTest() {
boolean nodeFound = false;
final Text node = new Text("text");
((Group)scene.getRoot()).getChildren().add(node);
KeyCodeCombination mnemonicKeyCombo =
new KeyCodeCombination(KeyCode.Q,KeyCombination.ALT_DOWN);
Mnemonic myMnemonic = new Mnemonic(node, mnemonicKeyCombo);
scene.addMnemonic(myMnemonic);
scene.removeMnemonic(myMnemonic);
ObservableList<Mnemonic> mnemonicsList = scene.getMnemonics().get(mnemonicKeyCombo);
if (mnemonicsList != null) {
for (int i = 0 ; i < mnemonicsList.size() ; i++) {
if (mnemonicsList.get(i).getNode() == node) {
nodeFound = true;
}
}
}
assertTrue(!nodeFound);
}
@Test
public void mnemonicRemovedWithNodeTest() {
final Text node = new Text("text");
((Group)scene.getRoot()).getChildren().add(node);
KeyCodeCombination mnemonicKeyCombo =
new KeyCodeCombination(KeyCode.Q,KeyCombination.ALT_DOWN);
Mnemonic myMnemonic = new Mnemonic(node, mnemonicKeyCombo);
scene.addMnemonic(myMnemonic);
ObservableList<Mnemonic> mnemonicsList = scene.getMnemonics().get(mnemonicKeyCombo);
assertTrue(mnemonicsList.contains(myMnemonic));
scene.setRoot(new Group());
assertFalse(mnemonicsList.contains(myMnemonic));
}
}

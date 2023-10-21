package test.com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.control.behavior.MnemonicInfo;
import javafx.scene.input.KeyCombination;
import org.junit.Test;
import static org.junit.Assert.*;
public class MnemonicInfoTest {
private static void assertKeyCombination(String expected, KeyCombination actual) {
if (com.sun.javafx.PlatformUtil.isMac()) {
assertSame(KeyCombination.ModifierValue.DOWN, actual.getMeta());
} else {
assertSame(KeyCombination.ModifierValue.DOWN, actual.getAlt());
}
assertEquals(expected, ((MnemonicInfo.MnemonicKeyCombination)actual).getCharacter());
}
@Test
public void testSimpleMnemonicLetter() {
var mnemonicInfo = new MnemonicInfo("foo _bar");
assertEquals("foo bar", mnemonicInfo.getText());
assertEquals("b", mnemonicInfo.getMnemonic());
assertKeyCombination("b", mnemonicInfo.getMnemonicKeyCombination());
assertEquals(4, mnemonicInfo.getMnemonicIndex());
}
@Test
public void testSimpleMnemonicDigit() {
var mnemonicInfo = new MnemonicInfo("foo _1 bar");
assertEquals("foo 1 bar", mnemonicInfo.getText());
assertEquals("1", mnemonicInfo.getMnemonic());
assertKeyCombination("1", mnemonicInfo.getMnemonicKeyCombination());
assertEquals(4, mnemonicInfo.getMnemonicIndex());
}
@Test
public void testExtendedMnemonicLetter() {
var mnemonicInfo = new MnemonicInfo("foo _(x)bar");
assertEquals("foo bar", mnemonicInfo.getText());
assertEquals("x", mnemonicInfo.getMnemonic());
assertKeyCombination("x", mnemonicInfo.getMnemonicKeyCombination());
assertEquals(4, mnemonicInfo.getMnemonicIndex());
}
@Test
public void testExtendedMnemonicUnderscore() {
var mnemonicInfo = new MnemonicInfo("foo _(_)bar");
assertEquals("foo bar", mnemonicInfo.getText());
assertEquals("_", mnemonicInfo.getMnemonic());
assertKeyCombination("_", mnemonicInfo.getMnemonicKeyCombination());
assertEquals(4, mnemonicInfo.getMnemonicIndex());
}
@Test
public void testExtendedMnemonicClosingBrace() {
var mnemonicInfo = new MnemonicInfo("foo _())bar");
assertEquals("foo bar", mnemonicInfo.getText());
assertEquals(")", mnemonicInfo.getMnemonic());
assertKeyCombination(")", mnemonicInfo.getMnemonicKeyCombination());
assertEquals(4, mnemonicInfo.getMnemonicIndex());
}
@Test
public void testEscapedMnemonicSymbol() {
var mnemonicInfo = new MnemonicInfo("foo __bar");
assertEquals("foo _bar", mnemonicInfo.getText());
assertNull(mnemonicInfo.getMnemonic());
assertNull(mnemonicInfo.getMnemonicKeyCombination());
assertEquals(-1, mnemonicInfo.getMnemonicIndex());
}
@Test
public void testWhitespaceIsNotProcessedAsExtendedMnemonic() {
var mnemonicInfo = new MnemonicInfo("foo _( ) bar");
assertEquals("foo ( ) bar", mnemonicInfo.getText());
assertEquals("(", mnemonicInfo.getMnemonic());
assertKeyCombination("(", mnemonicInfo.getMnemonicKeyCombination());
assertEquals(4, mnemonicInfo.getMnemonicIndex());
}
@Test
public void testUnderscoreNotFollowedByAlphabeticCharIsNotAMnemonic() {
var mnemonicInfo = new MnemonicInfo("foo_ bar");
assertEquals("foo_ bar", mnemonicInfo.getText());
assertNull(mnemonicInfo.getMnemonic());
assertNull(mnemonicInfo.getMnemonicKeyCombination());
assertEquals(-1, mnemonicInfo.getMnemonicIndex());
}
@Test
public void testUnderscoreAtEndOfTextIsNotAMnemonic() {
var mnemonicInfo = new MnemonicInfo("foo_");
assertEquals("foo_", mnemonicInfo.getText());
assertNull(mnemonicInfo.getMnemonic());
assertNull(mnemonicInfo.getMnemonicKeyCombination());
assertEquals(-1, mnemonicInfo.getMnemonicIndex());
}
@Test
public void testMnemonicParsingStopsAfterFirstSimpleMnemonic() {
var mnemonicInfo = new MnemonicInfo("_foo _bar _qux");
assertEquals("foo _bar _qux", mnemonicInfo.getText());
assertEquals("f", mnemonicInfo.getMnemonic());
assertKeyCombination("f", mnemonicInfo.getMnemonicKeyCombination());
assertEquals(0, mnemonicInfo.getMnemonicIndex());
}
@Test
public void testMnemonicParsingStopsAfterFirstExtendedMnemonic() {
var mnemonicInfo = new MnemonicInfo("_(x)foo _bar _qux");
assertEquals("foo _bar _qux", mnemonicInfo.getText());
assertEquals("x", mnemonicInfo.getMnemonic());
assertKeyCombination("x", mnemonicInfo.getMnemonicKeyCombination());
assertEquals(0, mnemonicInfo.getMnemonicIndex());
}
}

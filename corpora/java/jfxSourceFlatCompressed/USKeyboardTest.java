package test.robot.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.TestLogShim;
import test.robot.com.sun.glass.ui.monocle.TestApplication;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
public class USKeyboardTest {
private UInput ui;
private Character [] bigChars;
private Character [] smallChars;
private Character [] digits;
private Character [] digitsShift;
private Character [] signs;
private Character [] signsShift;
private String [] signsKeyNames;
@Before
public void initDevice() {
TestLogShim.reset();
ui = new UInput();
}
@After
public void destroyDevice() throws InterruptedException {
ui.waitForQuiet();
try {
ui.processLine("DESTROY");
} catch (RuntimeException e) { }
ui.processLine("CLOSE");
ui.dispose();
}
private void createUSKeyboard() {
bigChars = new Character[26];
for(int i = 65; i < 91; i++) {
bigChars[i-65] = (char) i;
}
smallChars = new Character[26];
for(int i = 97; i < 123; i++) {
smallChars[i-97] = (char) i;
}
digits = new Character [] {'1','2','3','4','5','6','7','8','9','0'};
digitsShift = new Character [] {'!','@','#','$','%','^','&','*','(',')'};
signs = new Character [] {'`','-','=','[',']',';','\'','\\',',','.','/'};
signsShift = new Character [] {'~','_','+','{','}',':','"','|','<','>','?'};
signsKeyNames = new String [] {"GRAVE", "MINUS","EQUAL","LEFTBRACE",
"RIGHTBRACE","SEMICOLON","APOSTROPHE",
"BACKSLASH","COMMA","DOT","SLASH"};
ui.processLine("OPEN");
ui.processLine("EVBIT EV_KEY");
ui.processLine("EVBIT EV_SYN");
for(int i = 0; i < 26; i++) {
ui.processLine("KEYBIT KEY_" + bigChars[i]);
}
for(int i = 0; i < 10; i++) {
ui.processLine("KEYBIT KEY_" + digits[i]);
}
for(int i = 0; i < 11; i++) {
ui.processLine("KEYBIT KEY_" + signsKeyNames[i]);
}
ui.processLine("KEYBIT 0x0033");
ui.processLine("KEYBIT KEY_LEFTSHIFT");
ui.processLine("KEYBIT KEY_CAPSLOCK");
ui.processLine("PROPERTY ID_INPUT_KEYBOARD 1");
ui.processLine("CREATE");
}
private void checkShift(String key, char unShifted, char shifted) throws Exception {
checkKey(key, unShifted, false);
checkKey(key, shifted, true);
}
private void checkKey(String key, char c, boolean shiftPressed) throws Exception {
if (shiftPressed) {
ui.processLine("EV_KEY KEY_LEFTSHIFT 1");
ui.processLine("EV_SYN");
}
ui.processLine("EV_KEY " + key + " 1");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY " + key + " 0");
ui.processLine("EV_SYN");
if (shiftPressed) {
ui.processLine("EV_KEY KEY_LEFTSHIFT 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key released: SHIFT");
}
TestLogShim.waitForLog("Key typed: %0$c", new Object[] { c });
}
@Test
public void testShift() throws Exception {
TestApplication.showFullScreenScene();
TestApplication.addKeyListeners();
createUSKeyboard();
for(int i = 0; i < 26; i++) {
checkShift("KEY_"+ bigChars[i], smallChars[i], bigChars[i]);
}
for(int i = 0; i < 10; i++) {
checkShift("KEY_"+ digits[i], digits[i], digitsShift[i]);
}
for(int i = 0; i < 11; i++) {
checkShift("KEY_"+ signsKeyNames[i], signs[i], signsShift[i]);
}
}
@Test
public void testCapsLock() throws Exception {
TestApplication.showFullScreenScene();
TestApplication.addKeyListeners();
createUSKeyboard();
ui.processLine("EV_KEY KEY_CAPSLOCK 1");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_CAPSLOCK 0");
ui.processLine("EV_SYN");
for(int i = 0; i < 26; i++) {
checkShift("KEY_"+ bigChars[i], bigChars[i], smallChars[i]);
}
for(int i = 0; i < 10; i++) {
checkShift("KEY_"+ digits[i], digits[i], digitsShift[i]);
}
for(int i = 0; i < 11; i++) {
checkShift("KEY_"+ signsKeyNames[i], signs[i], signsShift[i]);
}
ui.processLine("EV_KEY KEY_CAPSLOCK 1");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_CAPSLOCK 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key released: CAPS");
}
@Test
public void testPressReleaseOrder() throws Exception {
TestApplication.showFullScreenScene();
TestApplication.addKeyListeners();
ui.processLine("OPEN");
ui.processLine("EVBIT EV_KEY");
ui.processLine("EVBIT EV_SYN");
ui.processLine("KEYBIT KEY_1");
ui.processLine("KEYBIT KEY_2");
ui.processLine("KEYBIT KEY_3");
ui.processLine("KEYBIT KEY_4");
ui.processLine("KEYBIT KEY_CAPSLOCK");
ui.processLine("PROPERTY ID_INPUT_KEYBOARD 1");
ui.processLine("CREATE");
ui.processLine("EV_KEY KEY_1 1");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key pressed: DIGIT1");
TestLogShim.waitForLog("Key typed: 1");
ui.processLine("EV_KEY KEY_2 1");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key pressed: DIGIT2");
TestLogShim.waitForLog("Key typed: 2");
ui.processLine("EV_KEY KEY_1 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key released: DIGIT1");
ui.processLine("EV_KEY KEY_3 1");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key pressed: DIGIT3");
TestLogShim.waitForLog("Key typed: 3");
ui.processLine("EV_KEY KEY_2 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key released: DIGIT2");
ui.processLine("EV_KEY KEY_4 1");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key pressed: DIGIT4");
TestLogShim.waitForLog("Key typed: 4");
ui.processLine("EV_KEY KEY_3 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key released: DIGIT3");
ui.processLine("EV_KEY KEY_4 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key released: DIGIT3");
}
@Test
public void testBackspace() throws Exception {
TestApplication.showFullScreenScene();
TestApplication.addKeyListeners();
ui.processLine("OPEN");
ui.processLine("EVBIT EV_KEY");
ui.processLine("EVBIT EV_SYN");
ui.processLine("KEYBIT KEY_BACKSPACE");
ui.processLine("KEYBIT KEY_LEFTSHIFT");
ui.processLine("KEYBIT KEY_CAPSLOCK");
ui.processLine("PROPERTY ID_INPUT_KEYBOARD 1");
ui.processLine("CREATE");
ui.processLine("EV_KEY KEY_BACKSPACE 1");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key pressed: BACK_SPACE");
ui.processLine("EV_KEY KEY_BACKSPACE 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key released: BACK_SPACE");
Assert.assertEquals(0l, TestLogShim.countLogContaining("Key typed"));
TestLogShim.reset();
ui.processLine("EV_KEY KEY_LEFTSHIFT 1");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_BACKSPACE 1");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key pressed: BACK_SPACE");
ui.processLine("EV_KEY KEY_BACKSPACE 0");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_LEFTSHIFT 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key released: BACK_SPACE");
Assert.assertEquals(0l, TestLogShim.countLogContaining("Key typed"));
TestLogShim.reset();
ui.processLine("EV_KEY KEY_CAPSLOCK 1");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_CAPSLOCK 0");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_BACKSPACE 1");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key pressed: BACK_SPACE");
ui.processLine("EV_KEY KEY_BACKSPACE 0");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_CAPSLOCK 1");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_CAPSLOCK 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key released: BACK_SPACE");
Assert.assertEquals(0l, TestLogShim.countLogContaining("Key typed"));
TestLogShim.reset();
ui.processLine("EV_KEY KEY_CAPSLOCK 1");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_CAPSLOCK 0");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_LEFTSHIFT 1");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_BACKSPACE 1");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key pressed: BACK_SPACE");
ui.processLine("EV_KEY KEY_BACKSPACE 0");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_LEFTSHIFT 0");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_CAPSLOCK 1");
ui.processLine("EV_SYN");
ui.processLine("EV_KEY KEY_CAPSLOCK 0");
ui.processLine("EV_SYN");
TestLogShim.waitForLog("Key released: BACK_SPACE");
Assert.assertEquals(0l, TestLogShim.countLogContaining("Key typed"));
}
}

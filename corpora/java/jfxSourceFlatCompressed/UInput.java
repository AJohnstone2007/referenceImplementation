package test.robot.com.sun.glass.ui.monocle;
import test.robot.com.sun.glass.ui.monocle.MonocleUInput;
import test.com.sun.glass.ui.monocle.LensUInput;
import com.sun.glass.ui.Application;
import test.com.sun.glass.ui.monocle.LensUInput;
import test.com.sun.glass.ui.monocle.NativeUInput;
public class UInput {
private static final boolean isMonocle;
private static final Boolean verbose = Boolean.getBoolean("verbose");
private static final Boolean sync = Boolean.getBoolean("sync");
private final NativeUInput nativeUInput = createNativeUInput();
static {
if (System.getProperty("glass.platform") == null) {
System.setProperty("glass.platform", "Monocle");
System.setProperty("monocle.platform", "Headless");
System.setProperty("prism.order", "sw");
System.setProperty("com.sun.javafx.gestures.zoom", "true");
System.setProperty("com.sun.javafx.gestures.rotate", "true");
System.setProperty("com.sun.javafx.gestures.scroll", "true");
}
isMonocle = "Monocle".equals(System.getProperty("glass.platform"));
setup();
}
public UInput() {
nativeUInput.setup();
nativeUInput.init();
}
private static NativeUInput createNativeUInput() {
if (isMonocle) {
return new MonocleUInput();
} else {
return new LensUInput();
}
}
public void dispose() {
nativeUInput.dispose();
}
public static void setup() {
createNativeUInput().setup();
}
public void processLines(String[] lines) {
for (String line : lines) {
processLine(line);
}
}
public void waitForQuiet() throws InterruptedException {
nativeUInput.waitForQuiet();
}
public void write(byte[] data, int offset, int length) {
if (sync && !Application.isEventThread()) {
Application.invokeAndWait(() -> nativeUInput.write(data, offset, length));
} else {
nativeUInput.write(data, offset, length);
}
}
public int writeTime(byte[] data, int offset) {
return nativeUInput.writeTime(data, offset);
}
public int writeCode(byte[] data, int offset, String code) {
return nativeUInput.writeCode(data, offset, code);
}
public int writeValue(byte[] data, int offset, String value) {
return nativeUInput.writeValue(data, offset, value);
}
public int writeValue(byte[] data, int offset, int value) {
return nativeUInput.writeValue(data, offset, value);
}
public int writeLine(byte[] data, int offset, String line) {
String[] args = line.split(" ");
offset = writeTime(data, offset);
offset = writeCode(data, offset, args[0]);
if (args.length > 1) {
offset = writeCode(data, offset, args[1]);
} else {
offset = writeCode(data, offset, "0");
}
if (args.length > 2) {
offset = writeValue(data, offset, args[2]);
} else {
offset = writeValue(data, offset, 0);
}
return offset;
}
public void processLine(String line) {
if (sync && !Application.isEventThread()) {
Application.invokeAndWait(() -> processLineImpl(line));
} else {
processLineImpl(line);
}
}
private void processLineImpl(String line) {
if (verbose) {
System.out.println(line);
}
int i = line.indexOf('#');
if (i >= 0) {
line = line.substring(0, i);
}
line = line.substring(line.lastIndexOf(':') + 1).trim();
if (line.length() > 0) {
String[] args = line.split(" ");
switch (args.length) {
case 0: break;
case 1: nativeUInput.processLine1(line, args[0]); break;
case 2: nativeUInput.processLine2(line, args[0], args[1]); break;
case 3: nativeUInput.processLine3(line, args[0], args[1], args[2]); break;
default:
throw new IllegalArgumentException(line);
}
}
}
}

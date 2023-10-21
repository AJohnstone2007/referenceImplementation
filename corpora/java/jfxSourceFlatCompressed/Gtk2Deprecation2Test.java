package test.com.sun.glass.ui.gtk;
import com.sun.javafx.PlatformUtil;
import java.io.ByteArrayOutputStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assume.assumeTrue;
public class Gtk2Deprecation2Test extends Gtk2DeprecationCommon {
@BeforeClass
public static void setup() throws Exception {
doSetup(false);
}
@AfterClass
public static void teardown() {
doTeardown();
}
@Test
public void testNoDeprecationMessage() throws Exception {
assumeTrue(PlatformUtil.isLinux());
final String output = out.toString();
System.err.println(output);
assertFalse("Unexpected warning message", output.contains("WARNING"));
assertFalse("Unexpected warning message", output.contains("deprecated"));
assertFalse("Unexpected warning message", output.contains("removed"));
}
}

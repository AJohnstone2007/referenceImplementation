package test.com.sun.glass.ui.gtk;
import com.sun.javafx.PlatformUtil;
import java.io.ByteArrayOutputStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
public class Gtk2Deprecation1Test extends Gtk2DeprecationCommon {
@BeforeClass
public static void setup() throws Exception {
doSetup(true);
}
@AfterClass
public static void teardown() {
doTeardown();
}
@Test
public void testDeprecationMessage() throws Exception {
assumeTrue(PlatformUtil.isLinux());
final String output = out.toString();
System.err.println(output);
assertTrue("Missing warning message", output.contains("WARNING"));
assertTrue("Missing warning message", output.contains("deprecated"));
assertTrue("Missing warning message", output.contains("removed"));
}
}

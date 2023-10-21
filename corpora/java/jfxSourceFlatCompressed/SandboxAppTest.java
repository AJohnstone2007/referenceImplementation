package test.sandbox;
import com.sun.javafx.PlatformUtil;
import java.util.ArrayList;
import junit.framework.AssertionFailedError;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static test.sandbox.Constants.*;
public class SandboxAppTest {
private static final String className = SandboxAppTest.class.getName();
private static final String pkgName = className.substring(0, className.lastIndexOf("."));
private static String getTestPolicyFile() {
return SandboxAppTest.class.getResource("test.policy").toExternalForm();
}
private void runSandboxedApp(String appName) throws Exception {
runSandboxedApp(appName, ERROR_NONE);
}
private void runSandboxedApp(String appName, int exitCode) throws Exception {
final String testAppName = pkgName + ".app." + appName;
final String testPolicy = getTestPolicyFile();
final ArrayList<String> cmd =
test.util.Util.createApplicationLaunchCommand(
testAppName,
null,
testPolicy
);
final ProcessBuilder builder = new ProcessBuilder(cmd);
builder.redirectError(ProcessBuilder.Redirect.INHERIT);
builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
Process process = builder.start();
int retVal = process.waitFor();
switch (retVal) {
case 0:
case ERROR_NONE:
assertEquals(testAppName + ": Unexpected 'success' exit code;",
exitCode, retVal);
break;
case 1:
throw new AssertionFailedError(testAppName
+ ": unable to launch java application");
case ERROR_TIMEOUT:
throw new AssertionFailedError(testAppName
+ ": Application timeout");
case ERROR_SECURITY_EXCEPTION:
throw new AssertionFailedError(testAppName
+ ": Application failed with a security exception");
case ERROR_NO_SECURITY_EXCEPTION:
throw new AssertionFailedError(testAppName
+ ": Application did not get expected security exception");
case ERROR_UNEXPECTED_EXCEPTION:
throw new AssertionFailedError(testAppName
+ ": Application failed with unexpected exception");
default:
throw new AssertionFailedError(testAppName
+ ": Unexpected error exit: " + retVal);
}
}
@Before
public void setupEach() {
if (PlatformUtil.isWindows()) {
assumeTrue(Boolean.getBoolean("unstable.test"));
}
}
@Test (timeout = 25000)
public void testFXApp() throws Exception {
runSandboxedApp("FXApp");
}
@Test (timeout = 25000)
public void testFXNonApp() throws Exception {
runSandboxedApp("FXNonApp");
}
@Ignore("JDK-8202451")
@Test (timeout = 25000)
public void testJFXPanelApp() throws Exception {
runSandboxedApp("JFXPanelApp");
}
@Ignore("JDK-8202451")
@Test (timeout = 25000)
public void testJFXPanelImplicitExitApp() throws Exception {
runSandboxedApp("JFXPanelImplicitExitApp", 0);
}
}

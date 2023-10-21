package test.launchertest;
import java.util.ArrayList;
import junit.framework.AssertionFailedError;
import org.junit.Test;
import static org.junit.Assert.*;
import static test.launchertest.Constants.*;
public class JarLauncherTest {
private final String testAppName = System.getProperty("launchertest.testapp1.jar");
private final int testExitCode = ERROR_NONE;
@Test (timeout = 15000)
public void testJarLauncher() throws Exception {
assertNotNull(testAppName);
final ArrayList<String> cmd =
test.util.Util.createApplicationLaunchCommand(
testAppName,
null,
null
);
final ProcessBuilder builder = new ProcessBuilder(cmd);
builder.redirectError(ProcessBuilder.Redirect.INHERIT);
builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
Process process = builder.start();
int retVal = process.waitFor();
switch (retVal) {
case 0:
case ERROR_NONE:
if (retVal != testExitCode) {
throw new AssertionFailedError(testAppName
+ ": Unexpected 'success' exit; expected:"
+ testExitCode + " was:" + retVal);
}
return;
case 1:
throw new AssertionFailedError(testAppName
+ ": unable to launch java application");
case ERROR_UNEXPECTED_EXCEPTION:
throw new AssertionFailedError(testAppName
+ ": unexpected exception");
default:
throw new AssertionFailedError(testAppName
+ ": Unexpected error exit: " + retVal);
}
}
}

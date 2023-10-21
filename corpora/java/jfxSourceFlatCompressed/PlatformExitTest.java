package test.launchertest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import junit.framework.AssertionFailedError;
import org.junit.Test;
import test.util.Util;
import static test.launchertest.Constants.*;
public class PlatformExitTest {
private static final String className = PlatformExitTest.class.getName();
private static final String pkgName = className.substring(0, className.lastIndexOf("."));
private static final String testAppName = pkgName + "." + "PlatformExitApp";
@Test (timeout = 15000)
public void testPlatformExit() throws Exception {
final ArrayList<String> cmd =
Util.createApplicationLaunchCommand(testAppName, null, null);
ProcessBuilder builder = new ProcessBuilder(cmd);
builder.redirectErrorStream(true);
Process process = builder.start();
final InputStream in = process.getInputStream();
int retVal = process.waitFor();
switch (retVal) {
case 0:
case ERROR_NONE:
break;
case 1:
throw new AssertionFailedError(testAppName
+ ": unable to launch java application");
case ERROR_TIMEOUT:
throw new AssertionFailedError(testAppName
+ ": application timeout");
case ERROR_UNEXPECTED_EXCEPTION:
throw new AssertionFailedError(testAppName
+ ": unexpected exception");
default:
throw new AssertionFailedError(testAppName
+ ": Unexpected error exit: " + retVal);
}
BufferedReader reader = new BufferedReader(new InputStreamReader(in));
StringBuilder stringBuilder = new StringBuilder();
String line;
while ((line = reader.readLine()) != null) {
stringBuilder = stringBuilder.append(line).append("\n");
}
if (stringBuilder.indexOf("Java has been detached") >= 0) {
System.err.println(stringBuilder);
throw new AssertionFailedError(testAppName + ": tried to use JNI after Java was detached");
}
}
}

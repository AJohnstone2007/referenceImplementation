package test.launchertest;
import com.sun.javafx.PlatformUtil;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.AssertionFailedError;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.Test;
import static test.launchertest.Constants.*;
import static org.junit.Assume.*;
@RunWith(Parameterized.class)
public class MainLauncherTest {
private static final String className = MainLauncherTest.class.getName();
private static final String pkgName = className.substring(0, className.lastIndexOf("."));
private static Collection params = null;
public static class TestData {
final String appName;
final String pldrName;
final boolean headless;
final int exitCode;
public TestData(String appName) {
this(appName, 0);
}
public TestData(String appName, int exitCode) {
this(appName, null, exitCode);
}
public TestData(String appName, String pldrName, int exitCode) {
this(appName, pldrName, false, exitCode);
}
public TestData(String appName, boolean headless, int exitCode) {
this(appName, null, headless, exitCode);
}
public TestData(String appName, String pldrName, boolean headless, int exitCode) {
this.appName = pkgName + "." + appName;
this.pldrName = pldrName == null ? null : pkgName + "." + pldrName;
this.headless = headless;
this.exitCode = exitCode;
}
}
private static final TestData[] testData = {
new TestData("TestApp"),
new TestData("TestAppNoMain"),
new TestData("TestNotApplication"),
new TestData("TestStartupApp1", ERROR_NONE),
new TestData("TestStartupApp2", ERROR_NONE),
new TestData("TestStartupAppNoMain", ERROR_NONE),
new TestData("TestStartupJFXPanel", ERROR_NONE),
new TestData("TestStartupNotApplication", ERROR_NONE),
new TestData("TestAppThreadCheck", ERROR_NONE),
new TestData("TestAppNoMainThreadCheck", ERROR_NONE),
new TestData("TestNotApplicationThreadCheck", ERROR_NONE),
new TestData("TestAppThreadCheck", "TestPreloader", ERROR_NONE),
new TestData("TestAppNoMainThreadCheck", "TestPreloader", ERROR_NONE),
new TestData("TestAppCCL", ERROR_NONE),
new TestData("TestAppCCL1", ERROR_NONE),
new TestData("TestAppCCL2", ERROR_NONE),
new TestData("TestAppNoMainCCL", ERROR_NONE),
new TestData("TestAppNoMainCCL2", ERROR_NONE),
new TestData("TestAppNoMainCCL3", ERROR_NONE),
new TestData("TestNotApplicationCCL", ERROR_NONE),
new TestData("TestHeadlessApp", true, ERROR_NONE),
};
@Parameters
public static Collection getParams() {
if (params == null) {
params = new ArrayList();
for (TestData data : testData) {
params.add(new TestData[] { data });
}
}
return params;
}
private final String testAppName;
private final String testPldrName;
private final boolean headless;
private final int testExitCode;
public MainLauncherTest(TestData testData) {
this.testAppName = testData.appName;
this.testPldrName = testData.pldrName;
this.headless = testData.headless;
this.testExitCode = testData.exitCode;
}
@Test (timeout = 15000)
public void testMainLauncher() throws Exception {
if (headless) {
assumeTrue(PlatformUtil.isLinux());
}
final ArrayList<String> cmd =
test.util.Util.createApplicationLaunchCommand(
testAppName,
testPldrName,
null
);
final ProcessBuilder builder = new ProcessBuilder(cmd);
if (headless) {
builder.environment().put("DISPLAY", "");
}
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
case ERROR_TOOLKIT_NOT_RUNNING:
throw new AssertionFailedError(testAppName
+ ": Toolkit not running prior to loading application class");
case ERROR_TOOLKIT_IS_RUNNING:
throw new AssertionFailedError(testAppName
+ ": Toolkit is running but should not be");
case ERROR_INIT_BEFORE_MAIN:
throw new AssertionFailedError(testAppName
+ ": main method not called before init");
case ERROR_START_BEFORE_MAIN:
throw new AssertionFailedError(testAppName
+ ": main method not called before start");
case ERROR_STOP_BEFORE_MAIN:
throw new AssertionFailedError(testAppName
+ ": main method not called before stop");
case ERROR_START_BEFORE_INIT:
throw new AssertionFailedError(testAppName
+ ": init method not called before start");
case ERROR_STOP_BEFORE_INIT:
throw new AssertionFailedError(testAppName
+ ": init method not called before stop");
case ERROR_STOP_BEFORE_START:
throw new AssertionFailedError(testAppName
+ ": start method not called before stop");
case ERROR_CLASS_INIT_WRONG_THREAD:
throw new AssertionFailedError(testAppName
+ ": class initialization called on wrong thread");
case ERROR_MAIN_WRONG_THREAD:
throw new AssertionFailedError(testAppName
+ ": main called on wrong thread");
case ERROR_CONSTRUCTOR_WRONG_THREAD:
throw new AssertionFailedError(testAppName
+ ": constructor called on wrong thread");
case ERROR_INIT_WRONG_THREAD:
throw new AssertionFailedError(testAppName
+ ": init called on wrong thread");
case ERROR_START_WRONG_THREAD:
throw new AssertionFailedError(testAppName
+ ": start called on wrong thread");
case ERROR_STOP_WRONG_THREAD:
throw new AssertionFailedError(testAppName
+ ": stop called on wrong thread");
case ERROR_PRELOADER_CLASS_INIT_WRONG_THREAD:
throw new AssertionFailedError(testAppName
+ ": preloader class initialization called on wrong thread");
case ERROR_PRELOADER_CONSTRUCTOR_WRONG_THREAD:
throw new AssertionFailedError(testAppName
+ ": preloader constructor called on wrong thread");
case ERROR_PRELOADER_INIT_WRONG_THREAD:
throw new AssertionFailedError(testAppName
+ ": preloader init called on wrong thread");
case ERROR_PRELOADER_START_WRONG_THREAD:
throw new AssertionFailedError(testAppName
+ ": preloader start called on wrong thread");
case ERROR_PRELOADER_STOP_WRONG_THREAD:
throw new AssertionFailedError(testAppName
+ ": preloader stop called on wrong thread");
case ERROR_CONSTRUCTOR_WRONG_CCL:
throw new AssertionFailedError(testAppName
+ ": constructor has wrong CCL");
case ERROR_START_WRONG_CCL:
throw new AssertionFailedError(testAppName
+ ": start has wrong CCL");
case ERROR_LAUNCH_SUCCEEDED:
throw new AssertionFailedError(testAppName
+ ": Application.launch unexpectedly succeeded");
case ERROR_STARTUP_SUCCEEDED:
throw new AssertionFailedError(testAppName
+ ": Plataform.startup unexpectedly succeeded");
case ERROR_STARTUP_FAILED:
throw new AssertionFailedError(testAppName
+ ": Plataform.startup failed");
case ERROR_ASSERTION_FAILURE:
throw new AssertionFailedError(testAppName
+ ": Assertion failure in test application");
case ERROR_UNEXPECTED_EXCEPTION:
throw new AssertionFailedError(testAppName
+ ": unexpected exception");
default:
throw new AssertionFailedError(testAppName
+ ": Unexpected error exit: " + retVal);
}
}
}

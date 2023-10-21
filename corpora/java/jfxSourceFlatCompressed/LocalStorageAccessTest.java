package test.com.sun.webkit;
import com.sun.javafx.PlatformUtil;
import java.io.File;
import static java.util.Arrays.asList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;
import org.junit.Test;
public class LocalStorageAccessTest {
@Test (timeout = 15000)
public void testMainThreadDoesNotSegfault() throws Exception {
if (PlatformUtil.isWindows()) {
assumeTrue(Boolean.getBoolean("unstable.test"));
}
final String appModulePath = System.getProperty("launchertest.testapp7.module.path");
final String workerModulePath = System.getProperty("worker.module.path");
final String javaLibraryPath = System.getProperty("java.library.path");
final String workerJavaCmd = System.getProperty("worker.java.cmd");
final List<String> cmd = asList(
workerJavaCmd,
"-cp", appModulePath + "/mymod",
"-Djava.library.path=" + javaLibraryPath,
"-Dmodule.path=" + appModulePath + "/mymod" + File.pathSeparator + workerModulePath,
"myapp7.LocalStorageAccessWithModuleLayerLauncher"
);
final ProcessBuilder builder = new ProcessBuilder(cmd);
builder.redirectError(ProcessBuilder.Redirect.INHERIT);
builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
Process process = builder.start();
int retVal = process.waitFor();
assertEquals("Process did not exit cleanly", 0, retVal);
}
}

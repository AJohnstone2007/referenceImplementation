package test.javafx.scene.shape.meshmanagercacheleaktest;
import java.util.ArrayList;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static test.javafx.scene.shape.meshmanagercacheleaktest.Constants.*;
public class MeshManagerCacheLeakTest {
private final String className = MeshManagerCacheLeakTest.class.getName();
private final String pkgName = className.substring(0, className.lastIndexOf("."));
private final String testAppName = pkgName + "." + "MeshManagerCacheLeakApp";
@Before
public void setUp() {
assumeTrue(Platform.isSupported(ConditionalFeature.SCENE3D));
assumeTrue(Boolean.getBoolean("unstable.test"));
}
@Test (timeout = 15000)
public void testSphereCacheLeakTest() throws Exception {
testMeshManagerCacheLeak("Sphere", "10");
}
@Test (timeout = 15000)
public void testCylinderCacheLeakTest() throws Exception {
testMeshManagerCacheLeak("Cylinder", "25");
}
@Test (timeout = 20000)
public void testBoxCacheLeakTest() throws Exception {
testMeshManagerCacheLeak("Box", "350");
}
private void testMeshManagerCacheLeak(String shape, String count) throws Exception {
String[] jvmArgs = {"-Xmx16m"};
final ArrayList<String> cmd = test.util.Util.createApplicationLaunchCommand(
testAppName, null, null, jvmArgs);
cmd.add(String.valueOf(shape));
cmd.add(String.valueOf(count));
ProcessBuilder builder = new ProcessBuilder(cmd);
builder.redirectError(ProcessBuilder.Redirect.INHERIT);
builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
Process process = builder.start();
int retVal = process.waitFor();
switch (retVal) {
case 0:
fail(testAppName + ": Unexpected exit 0 with cache test of : " + shape);
break;
case 1:
fail(testAppName + ": Unable to launch java application with cache test of : " + shape);
break;
case ERROR_NONE:
break;
case ERROR_OOM:
fail(testAppName + ": OOM occured with cache test of : " + shape);
break;
case ERROR_LAUNCH:
fail(testAppName + ": Window was not shown for more than 10 secs, with cache test of : " + shape);
break;
default:
fail(testAppName + ": Unexpected error exit: " + retVal + " with cache test of : " + shape);
break;
}
}
}

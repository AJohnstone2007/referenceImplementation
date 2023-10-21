package test.javafx.css.imagecacheleaktest;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.fail;
import static test.javafx.css.imagecacheleaktest.Constants.*;
public class ImageCacheLeakTest {
private static final String className = ImageCacheLeakTest.class.getName();
private static final String pkgName = className.substring(0, className.lastIndexOf("."));
private final String testAppName = pkgName + "." + "ImageCacheLeakApp";
@Test (timeout = 15000)
public void testImageCacheLeak() throws Exception {
String[] jvmArgs = new String[1];
jvmArgs[0] = new String("-Xmx16m");
final ArrayList<String> cmd = test.util.Util.createApplicationLaunchCommand(
testAppName, null, null, jvmArgs);
ProcessBuilder builder = new ProcessBuilder(cmd);
builder.redirectError(ProcessBuilder.Redirect.INHERIT);
builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
Process process = builder.start();
int retVal = process.waitFor();
switch (retVal) {
case 0:
fail(testAppName + ": Unexpected exit 0");
break;
case 1:
fail(testAppName + ": Unexpected exit 1, unable to launch java application");
break;
case ERROR_NONE:
break;
case ERROR_LEAK:
fail(testAppName + ": CSS styled image1 causes memory leak.");
break;
case ERROR_INCORRECT_GC:
fail(testAppName + ": CSS styled image2 is incorrectly GCed.");
break;
case ERROR_IMAGE_VIEW:
fail(testAppName + ": Style class is not applied correctly to ImageView");
break;
default:
fail(testAppName + ": Unexpected error exit: " + retVal);
}
}
}

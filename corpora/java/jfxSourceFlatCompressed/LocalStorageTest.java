package test.javafx.scene.web;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import org.junit.AfterClass;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
public class LocalStorageTest extends TestBase {
private static final File LOCAL_STORAGE_DIR = new File("LocalStorageDir");
private static void deleteRecursively(File file) throws IOException {
if (file.isDirectory()) {
for (File f : file.listFiles()) {
deleteRecursively(f);
}
}
if (!file.delete()) {
file.deleteOnExit();
}
}
private WebEngine createWebEngine() {
return submit(() -> new WebEngine());
}
void checkLocalStorageAfterWindowClose(WebEngine webEngine) {
load(new File("src/test/resources/test/html/localstorage.html"));
submit(() -> {
assertNotNull(webEngine.executeScript("localStorage;"));
getEngine().executeScript("window.close();");
assertNotNull(webEngine.executeScript("localStorage;"));
});
}
@AfterClass
public static void afterClass() throws IOException {
deleteRecursively(LOCAL_STORAGE_DIR);
}
@Test
public void testLocalStorage() throws Exception {
final WebEngine webEngine = getEngine();
webEngine.setJavaScriptEnabled(true);
webEngine.setUserDataDirectory(LOCAL_STORAGE_DIR);
checkLocalStorageAfterWindowClose(webEngine);
}
@Test
public void testLocalStorageData() {
final WebEngine webEngine = getEngine();
webEngine.setJavaScriptEnabled(true);
webEngine.setUserDataDirectory(LOCAL_STORAGE_DIR);
load(new File("src/test/resources/test/html/localstorage.html"));
submit(() -> {
WebView view = getView();
view.getEngine().executeScript("test_local_storage_set();");
getEngine().executeScript("window.close();");
String s = (String) view.getEngine().executeScript("document.getElementById('key').innerText;");
assertEquals("1001", s);
});
}
@Test
public void testLocalStorageSet() {
final WebEngine webEngine = getEngine();
webEngine.setJavaScriptEnabled(true);
webEngine.setUserDataDirectory(LOCAL_STORAGE_DIR);
load(new File("src/test/resources/test/html/localstorage.html"));
submit(() -> {
WebView view = getView();
view.getEngine().executeScript("test_local_storage_set();");
String s = (String) view.getEngine().executeScript("document.getElementById('key').innerText;");
assertEquals("1001", s);
});
}
@Test
public void testLocalStoargeClear() {
final WebEngine webEngine = getEngine();
webEngine.setJavaScriptEnabled(true);
webEngine.setUserDataDirectory(LOCAL_STORAGE_DIR);
load(new File("src/test/resources/test/html/localstorage.html"));
submit(() -> {
WebView view = getView();
view.getEngine().executeScript("test_local_storage_set();");
view.getEngine().executeScript("delete_items();");
String s = (String) view.getEngine().executeScript("document.getElementById('key').innerText;");
boolean res = (s == null || s.length() == 0);
assertTrue(res);
});
}
}

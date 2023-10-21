package test.javafx.fxml;
import com.sun.javafx.fxml.FXMLLoaderHelper;
import org.junit.Test;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadListener;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
public class FXMLLoader_ScriptTest {
@Test
@SuppressWarnings("deprecation")
public void testStaticScriptLoad() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("static_script_load.fxml"));
FXMLLoaderHelper.setStaticLoad(fxmlLoader, true);
AtomicBoolean scriptCalled = new AtomicBoolean();
AtomicBoolean scriptEndCalled = new AtomicBoolean();
fxmlLoader.setLoadListener(new LoadListener() {
@Override
public void readImportProcessingInstruction(String target) {
}
@Override
public void readLanguageProcessingInstruction(String language) {
}
@Override
public void readComment(String comment) {
}
@Override
public void beginInstanceDeclarationElement(Class<?> type) {
}
@Override
public void beginUnknownTypeElement(String name) {
}
@Override
public void beginIncludeElement() {
}
@Override
public void beginReferenceElement() {
}
@Override
public void beginCopyElement() {
}
@Override
public void beginRootElement() {
}
@Override
public void beginPropertyElement(String name, Class<?> sourceType) {
}
@Override
public void beginUnknownStaticPropertyElement(String name) {
}
@Override
public void beginScriptElement() {
assertFalse(scriptCalled.getAndSet(true));
}
@Override
public void beginDefineElement() {
}
@Override
public void readInternalAttribute(String name, String value) {
}
@Override
public void readPropertyAttribute(String name, Class<?> sourceType, String value) {
}
@Override
public void readUnknownStaticPropertyAttribute(String name, String value) {
}
@Override
public void readEventHandlerAttribute(String name, String value) {
}
@Override
public void endElement(Object value) {
if (value instanceof String && ((String) value).contains("doSomething")) {
assertTrue(scriptCalled.get());
assertFalse(scriptEndCalled.getAndSet(true));
}
}
});
fxmlLoader.load();
assertTrue(scriptCalled.get());
assertTrue(scriptEndCalled.get());
}
@Test
public void testScriptHandler() throws IOException {
assumeTrue(isNashornEngineAvailable());
FXMLLoader loader = new FXMLLoader(getClass().getResource("script_handler.fxml"));
loader.load();
Widget w = (Widget) loader.getNamespace().get("w");
assertNotNull(w);
loader.getNamespace().put("actionDone", new AtomicBoolean(false));
w.fire();
assertTrue(((AtomicBoolean) loader.getNamespace().get("actionDone")).get());
}
@Test
public void testExternalScriptHandler() throws IOException {
assumeTrue(isNashornEngineAvailable());
FXMLLoader loader = new FXMLLoader(getClass().getResource("script_handler_external.fxml"));
loader.load();
Widget w = (Widget) loader.getNamespace().get("w");
assertNotNull(w);
loader.getNamespace().put("actionDone", new AtomicBoolean(false));
w.fire();
assertTrue(((AtomicBoolean)loader.getNamespace().get("actionDone")).get());
}
private boolean isNashornEngineAvailable() {
ScriptEngineManager factory = new ScriptEngineManager();
ScriptEngine engine = factory.getEngineByName("nashorn");
return (engine != null);
}
}

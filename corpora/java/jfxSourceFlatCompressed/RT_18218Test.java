package test.javafx.fxml;
import com.sun.javafx.fxml.FXMLLoaderHelper;
import java.io.IOException;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadListener;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_18218Test {
@Test
@SuppressWarnings({"unchecked", "deprecation"})
public void testStaticScriptLoad() throws IOException {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_18218.fxml"));
FXMLLoaderHelper.setStaticLoad(fxmlLoader, true);
fxmlLoader.setLoadListener(new LoadListener() {
private String unknownStaticPropertyElementName = null;
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
unknownStaticPropertyElementName = name;
}
@Override
public void beginScriptElement() {
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
assertEquals(name, "Gadget.bar");
assertEquals(value, "123456");
}
@Override
public void readEventHandlerAttribute(String name, String value) {
}
@Override
public void endElement(Object value) {
if (unknownStaticPropertyElementName != null) {
if (unknownStaticPropertyElementName.equals("Gadget.bar")) {
assertEquals(value, "abcdef");
} else if (unknownStaticPropertyElementName.equals("Gadget.baz")) {
assertEquals(value.getClass(), Widget.class);
} else {
throw new RuntimeException();
}
unknownStaticPropertyElementName = null;
}
}
});
fxmlLoader.load();
Map<String, Object> gadget = (Map<String, Object>)fxmlLoader.getNamespace().get("gadget");
assertNotNull(gadget);
Widget widget2 = (Widget)fxmlLoader.getNamespace().get("widget2");
assertNotNull(widget2);
assertEquals(widget2.getName(), "Widget 2");
}
}

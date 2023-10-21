package myapp1;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.script.Bindings;
import javax.script.ScriptContext;
import static myapp1.Constants.*;
import pseudoScriptEngine.InvocationInfos;
import pseudoScriptEngine.RgfPseudoScriptEngine;
public class FXMLScriptDeployment extends Application {
static boolean bDebug = false;
public static void main(String[] args) {
try {
if (args.length > 0) {
bDebug = true;
}
new FXMLScriptDeployment().launch();
if (bDebug) { dumpEvalInformation(); }
assertCorrectInvocations();
} catch (AssertionError ex) {
System.err.println("ASSERTION ERROR: caught unexpected exception: " + ex);
ex.printStackTrace(System.err);
System.exit(ERROR_ASSERTION_FAILURE);
} catch (Error | Exception ex) {
System.err.println("ERROR: caught unexpected exception: " + ex);
ex.printStackTrace(System.err);
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
System.exit(ERROR_NONE);
}
@Override
public void start(Stage mainStage) {
URL fxmlUrl = null;
Parent rootNode = null;
Scene scene = null;
Button btn = null;
try {
fxmlUrl = Util.getURL(FXMLScriptDeployment.class, "demo_01");
rootNode = FXMLLoader.load(fxmlUrl);
scene = new Scene(rootNode);
btn = (Button) scene.lookup("#idButton");
}
catch (Exception ioe) {
ioe.printStackTrace();
System.exit(ERROR_UNEXPECTED_EXCEPTION);
}
btn.fire();
btn.fireEvent(new ActionEvent());
btn.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
0,
0,
0,
0,
MouseButton.PRIMARY,
0,
false,
false,
false,
false,
true,
false,
false,
false,
false,
false,
null
)
);
Platform.exit();
}
static void dumpEvalInformation() {
System.err.println("\nListing eval() invocation information (invocationList):");
Iterator<RgfPseudoScriptEngine> it = RgfPseudoScriptEngine.getEnginesUsed().iterator();
while (it.hasNext()) {
RgfPseudoScriptEngine rpse = it.next();
ArrayList invocationList = rpse.getInvocationList();
System.err.println("ScriptEngine: [" + rpse + "]");
Iterator<InvocationInfos> itEval = invocationList.iterator();
int count = 1;
while (itEval.hasNext()) {
System.err.println("\teval() invocation # " + count + ": ");
InvocationInfos entry = itEval.next();
System.err.println(entry.toDebugFormat("\t\t"));
count++;
System.err.println();
}
}
}
static void assertCorrectInvocations() {
Util.assertTrue("exactly one pseudo script engine instance",
RgfPseudoScriptEngine.getEnginesUsed().size() == 1);
RgfPseudoScriptEngine rpse = RgfPseudoScriptEngine.getEnginesUsed().get(0);
ArrayList invocationList = rpse.getInvocationList();
Util.assertTrue("exactly nine script engine invocations", invocationList.size() == 9);
final String FILENAME = "javax.script.filename";
final String ARGV = "javax.script.argv";
final String EVENT = "event";
final String IDBUTTON = "idButton";
final String IDROOT = "idRoot";
final String LOCATION = "location";
final String RESOURCES = "resources";
for (Integer invocation = 1; invocation <= invocationList.size(); invocation++) {
InvocationInfos entry = (InvocationInfos) invocationList.get(invocation - 1);
String script = entry.script;
TreeMap<Integer,TreeMap> scopes = (TreeMap) entry.bindings;
TreeMap<String,Object> engineBindings = scopes.get(100);
TreeMap<String,Object> globalBindings = scopes.get(200);
Object obj = null;
Button btn = null;
Util.assertExists(IDROOT + " in global scope Bindings", globalBindings.containsKey(IDROOT));
obj = globalBindings.get(IDROOT);
Util.assertType(IDROOT, AnchorPane.class, obj);
Util.assertExists(LOCATION + " in global scope Bindings", globalBindings.containsKey(LOCATION));
obj = globalBindings.get(LOCATION);
Util.assertType(LOCATION, URL.class, obj);
Util.assertExists(RESOURCES + " in global scope Bindings", globalBindings.containsKey(RESOURCES));
obj = globalBindings.get(RESOURCES);
Util.assertNull(RESOURCES,obj);
if (invocation == 1) {
Util.assertNotExists(IDBUTTON + " in global scope Bindings", globalBindings.containsKey(IDBUTTON));
}
else {
Util.assertExists(IDBUTTON + " in global scope Bindings", globalBindings.containsKey(IDBUTTON));
obj = globalBindings.get(IDBUTTON);
Util.assertType(IDBUTTON, Button.class, obj);
btn = (Button) obj;
}
Util.assertExists(FILENAME + " in engine scope Bindings", engineBindings.containsKey(FILENAME));
if (invocation < 7) {
Util.assertNotExists(ARGV + " in engine scope Bindings", engineBindings.containsKey(ARGV));
Util.assertNotExists(EVENT + " in engine scope Bindings", engineBindings.containsKey(EVENT));
}
else {
Util.assertExists(ARGV + " in engine scope Bindings", engineBindings.containsKey(ARGV));
Object[] argv = (Object[]) engineBindings.get(ARGV);
Util.assertExists(EVENT + " in engine scope Bindings", engineBindings.containsKey(EVENT));
obj = engineBindings.get(EVENT);
Util.assertSame("argv[0] == event", argv[0], obj);
if (invocation == 9) {
Util.assertType(EVENT, MouseEvent.class, obj);
MouseEvent ev = (MouseEvent) obj;
Util.assertSame("MouseEvent.getSource() == btn", ev.getSource(), btn);
Util.assertSame("MouseEvent.MOUSE_CLICKED", MouseEvent.MOUSE_CLICKED, ev.getEventType());
} else {
Util.assertType(EVENT, ActionEvent.class, obj);
ActionEvent ev = (ActionEvent) obj;
Util.assertSame("ActionEvent.getSource() == btn", ev.getSource(), btn);
}
}
String filename = (String) engineBindings.get(FILENAME);
boolean ok = false;
switch (invocation) {
case 1:
Util.assertEndsWith ("demo_01_topscript.rpsl", filename);
Util.assertStartsWith("demo_01_topscript.rpsl file - pseudo script", script);
break;
case 2:
Util.assertEndsWith ("demo_01_middlescript.rpsl", filename);
Util.assertStartsWith("demo_01_middlescript.rpsl file - pseudo script", script);
break;
case 3:
Util.assertEndsWith("demo_01.fxml-script_starting_at_line_52", filename);
Util.assertStartsWith("demo_01.fxml embedded script rpsl - line # 52", script);
break;
case 4:
Util.assertEndsWith ("demo_01_bottomscript.rpsl", filename);
Util.assertStartsWith("demo_01_bottomscript.rpsl file - pseudo script", script);
break;
case 5:
Util.assertEndsWith("demo_01.fxml-script_starting_at_line_56", filename);
Util.assertStartsWith("something (line # 56)", script);
break;
case 6:
Util.assertEndsWith("demo_01.fxml-script_starting_at_line_59", filename);
Util.assertStartsWith("demo_01.fxml (line # 59):", script);
break;
case 7:
Util.assertEndsWith("demo_01.fxml-onAction_attribute_in_element_ending_at_line_46", filename);
Util.assertStartsWith("demo_01.fxml embedded event - ActionEvent - line # 45 -", script);
break;
case 8:
Util.assertEndsWith("demo_01.fxml-onAction_attribute_in_element_ending_at_line_46", filename);
Util.assertStartsWith("demo_01.fxml embedded event - ActionEvent - line # 45 -", script);
break;
case 9:
Util.assertEndsWith("demo_01.fxml-onMouseClicked_attribute_in_element_ending_at_line_46", filename);
Util.assertStartsWith("demo_01.fxml embedded event - MouseClicked - line # 44", script);
break;
}
}
}
}

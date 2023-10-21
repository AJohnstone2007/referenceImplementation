package test.javafx.stage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.stage.Popup;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.PropertiesTestBase;
import test.com.sun.javafx.test.objects.TestGroup;
import test.com.sun.javafx.test.objects.TestNode;
import test.com.sun.javafx.test.objects.TestScene;
import test.com.sun.javafx.test.objects.TestStage;
@Ignore("JDK-8234153")
@RunWith(Parameterized.class)
public final class Popup_parentWindow_Test extends PropertiesTestBase {
@Parameters
public static Collection<Object> data() {
final List<Object> configurations = new ArrayList<Object>();
TestObjects to;
to = new TestObjects();
configurations.add(
config(to.testPopup,
"owner", to.testStage1, to.testStage2));
to = new TestObjects();
configurations.add(
config(to.testPopup,
"owner", to.testScene1, to.testScene2));
to = new TestObjects();
configurations.add(
config(to.testPopup,
"owner", to.testRoot1, to.testRoot2));
to = new TestObjects();
configurations.add(
config(to.testPopup,
"owner", to.testNode1, to.testNode2));
return configurations;
}
public Popup_parentWindow_Test(final Configuration configuration) {
super(configuration);
}
private static final class TestObjects {
public final Popup testPopup;
public final TestNode testNode1;
public final TestNode testNode2;
public final TestGroup testRoot1;
public final TestGroup testRoot2;
public final TestScene testScene1;
public final TestScene testScene2;
public final TestStage testStage1;
public final TestStage testStage2;
public TestObjects() {
testRoot1 = new TestGroup("ROOT_1");
testRoot2 = new TestGroup("ROOT_2");
testNode1 = new TestNode("NODE_1");
testNode2 = new TestNode("NODE_2");
testRoot1.getChildren().add(testNode1);
testRoot2.getChildren().add(testNode2);
testScene1 = new TestScene("SCENE_1", testRoot1);
testScene2 = new TestScene("SCENE_2", testRoot2);
testStage1 = new TestStage("STAGE_1");
testStage2 = new TestStage("STAGE_2");
testStage1.setScene(testScene1);
testStage2.setScene(testScene2);
testPopup = new Popup();
}
}
}

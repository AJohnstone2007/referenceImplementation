package test.javafx.scene.control.skin;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlowShim;
import javafx.scene.control.skin.VirtualFlow;
import org.junit.Before;
import org.junit.Test;
class SkinWithCustomVirtualFlow<T> extends ListViewSkin<T> {
public SkinWithCustomVirtualFlow(final ListView<T> control) {
super(control);
}
@Override
protected VirtualFlow<ListCell<T>> createVirtualFlow() {
return new VirtualFlowShim<ListCell<T>>();
}
public boolean isVirtualFlowNull() {
return (getVirtualFlow() == null);
}
public boolean isCustomVirtualFlow() {
return (getVirtualFlow() instanceof VirtualFlowShim);
}
}
class SkinWithDefaultVirtualFlow<T> extends ListViewSkin<T> {
public SkinWithDefaultVirtualFlow(final ListView<T> control) {
super(control);
}
public boolean isVirtualFlowNull() {
return (getVirtualFlow() == null);
}
public boolean isDefaultVirtualFlow() {
return (getVirtualFlow().getClass().equals(VirtualFlow.class));
}
}
public class CustomListViewSkinTest {
private ListView<String> listViewObj = null;
@Before public void setup() {
listViewObj = new ListView<String>();
}
@Test public void testCustomVirtualFlow() {
SkinWithCustomVirtualFlow skin =
new SkinWithCustomVirtualFlow<String>(listViewObj);
listViewObj.setSkin(skin);
assertFalse(skin.isVirtualFlowNull());
assertTrue(skin.isCustomVirtualFlow());
}
@Test public void testDefaultVirtualFlow() {
SkinWithDefaultVirtualFlow skin =
new SkinWithDefaultVirtualFlow<String>(listViewObj);
listViewObj.setSkin(skin);
assertFalse(skin.isVirtualFlowNull());
assertTrue(skin.isDefaultVirtualFlow());
}
}

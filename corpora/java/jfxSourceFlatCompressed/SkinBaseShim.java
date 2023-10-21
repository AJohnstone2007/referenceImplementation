package javafx.scene.control;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Node;
public class SkinBaseShim<C extends Control> extends SkinBase<C> {
public SkinBaseShim(final C control) {
super(control);
}
public static List<Node> getChildren(SkinBase<?> skin) {
return skin.getChildren();
}
public static Consumer<ObservableValue<?>> unregisterChangeListeners(SkinBase<?> skin, ObservableValue<?> ov) {
return skin.unregisterChangeListeners(ov);
}
public static Consumer<Observable> unregisterInvalidationListeners(SkinBase<?> skin, Observable ov) {
return skin.unregisterInvalidationListeners(ov);
}
public static Consumer<Change<?>> unregisterListChangeListeners(SkinBase<?> skin, ObservableList<?> ov) {
return skin.unregisterListChangeListeners(ov);
}
}

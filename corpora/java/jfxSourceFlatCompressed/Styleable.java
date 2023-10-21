package javafx.css;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
public interface Styleable {
String getTypeSelector();
String getId();
ObservableList<String> getStyleClass();
String getStyle();
List<CssMetaData<? extends Styleable, ?>> getCssMetaData();
Styleable getStyleableParent();
ObservableSet<PseudoClass> getPseudoClassStates();
default Node getStyleableNode() {
return null;
}
}

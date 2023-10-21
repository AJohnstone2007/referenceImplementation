package ensemble.control;
import java.util.Arrays;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
public class TitledToolBar extends HBox {
private String defaultTitle = "JavaFX Ensemble";
private Label titleLabel = new Label(defaultTitle);
private StringProperty titleText = new SimpleStringProperty(null);
public StringProperty titleTextProperty() { return titleText; };
public String getTitleText() { return titleText.get(); }
public void setTitleText(String text) { titleText.set(text);}
public TitledToolBar() {
getStyleClass().addAll("tool-bar","ensmeble-tool-bar");
titleLabel.getStyleClass().add("title");
titleLabel.setManaged(false);
titleLabel.textProperty().bind(titleText);
getChildren().add(titleLabel);
Pane spacer = new Pane();
setHgrow(spacer, Priority.ALWAYS);
getChildren().add(spacer);
}
public void addLeftItems(Node ... items) {
getChildren().addAll(0, Arrays.asList(items));
}
public void addRightItems(Node ... items) {
getChildren().addAll(items);
}
@Override protected void layoutChildren() {
super.layoutChildren();
final double w = getWidth();
final double h = getHeight();
final double titleWidth = titleLabel.prefWidth(h);
double leftItemsWidth = getPadding().getLeft();
for(Node item: getChildren()) {
if (item == titleLabel) break;
leftItemsWidth += item.getLayoutBounds().getWidth();
Insets margins = getMargin(item);
if (margins != null) leftItemsWidth += margins.getLeft() + margins.getRight();
leftItemsWidth += getSpacing();
}
if ((leftItemsWidth+(titleWidth/2)) < (w/2)) {
titleLabel.setVisible(true);
layoutInArea(titleLabel, 0, 0, getWidth(), h, 0, HPos.CENTER, VPos.CENTER);
} else {
titleLabel.setVisible(false);
}
}
}

package javafx.scene.control;
import java.net.URL;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
abstract class FXDialog {
protected Object owner;
protected FXDialog() {
}
public boolean requestPermissionToClose(final Dialog<?> dialog) {
boolean denyClose = true;
DialogPane dialogPane = dialog.getDialogPane();
if (dialogPane != null) {
List<ButtonType> buttons = dialogPane.getButtonTypes();
if (buttons.size() == 1) {
denyClose = false;
} else {
for (ButtonType button : buttons) {
if (button == null) continue;
ButtonBar.ButtonData type = button.getButtonData();
if (type == null) continue;
if (type == ButtonBar.ButtonData.CANCEL_CLOSE || type.isCancelButton()) {
denyClose = false;
break;
}
}
}
}
return !denyClose;
}
public abstract void show();
public abstract void showAndWait();
public abstract void close();
public abstract void initOwner(Window owner);
public abstract Window getOwner();
public abstract void initModality(Modality modality);
public abstract Modality getModality();
public abstract ReadOnlyBooleanProperty showingProperty();
public abstract Window getWindow();
public abstract void sizeToScene();
public abstract double getX();
public abstract void setX(double x);
public abstract ReadOnlyDoubleProperty xProperty();
public abstract double getY();
public abstract void setY(double y);
public abstract ReadOnlyDoubleProperty yProperty();
abstract BooleanProperty resizableProperty();
abstract ReadOnlyBooleanProperty focusedProperty();
abstract StringProperty titleProperty();
public abstract void setDialogPane(DialogPane node);
public abstract Node getRoot();
abstract ReadOnlyDoubleProperty widthProperty();
abstract void setWidth(double width);
abstract ReadOnlyDoubleProperty heightProperty();
abstract void setHeight(double height);
abstract void initStyle(StageStyle style);
abstract StageStyle getStyle();
abstract double getSceneHeight();
}

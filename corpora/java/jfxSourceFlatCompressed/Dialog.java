package javafx.scene.control;
import java.lang.ref.WeakReference;
import java.util.Optional;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import com.sun.javafx.event.EventHandlerManager;
import com.sun.javafx.tk.Toolkit;
public class Dialog<R> implements EventTarget {
final FXDialog dialog;
private boolean isClosing;
public Dialog() {
this.dialog = new HeavyweightDialog(this);
setDialogPane(new DialogPane());
initModality(Modality.APPLICATION_MODAL);
}
public final void show() {
Toolkit.getToolkit().checkFxUserThread();
Event.fireEvent(this, new DialogEvent(this, DialogEvent.DIALOG_SHOWING));
if (Double.isNaN(getWidth()) && Double.isNaN(getHeight())) {
dialog.sizeToScene();
}
dialog.show();
Event.fireEvent(this, new DialogEvent(this, DialogEvent.DIALOG_SHOWN));
}
public final Optional<R> showAndWait() {
Toolkit.getToolkit().checkFxUserThread();
if (!Toolkit.getToolkit().canStartNestedEventLoop()) {
throw new IllegalStateException("showAndWait is not allowed during animation or layout processing");
}
Event.fireEvent(this, new DialogEvent(this, DialogEvent.DIALOG_SHOWING));
if (Double.isNaN(getWidth()) && Double.isNaN(getHeight())) {
dialog.sizeToScene();
}
Event.fireEvent(this, new DialogEvent(this, DialogEvent.DIALOG_SHOWN));
dialog.showAndWait();
return Optional.ofNullable(getResult());
}
public final void close() {
if (isClosing) return;
isClosing = true;
final R result = getResult();
if (result == null && ! dialog.requestPermissionToClose(this)) {
isClosing = false;
return;
}
if (result == null) {
ButtonType cancelButton = null;
for (ButtonType button : getDialogPane().getButtonTypes()) {
ButtonData buttonData = button.getButtonData();
if (buttonData == null) continue;
if (buttonData == ButtonData.CANCEL_CLOSE) {
cancelButton = button;
break;
}
if (buttonData.isCancelButton()) {
cancelButton = button;
}
}
setResultAndClose(cancelButton, false);
}
Event.fireEvent(this, new DialogEvent(this, DialogEvent.DIALOG_HIDING));
DialogEvent closeRequestEvent = new DialogEvent(this, DialogEvent.DIALOG_CLOSE_REQUEST);
Event.fireEvent(this, closeRequestEvent);
if (closeRequestEvent.isConsumed()) {
isClosing = false;
return;
}
dialog.close();
Event.fireEvent(this, new DialogEvent(this, DialogEvent.DIALOG_HIDDEN));
isClosing = false;
}
public final void hide() {
close();
}
public final void initModality(Modality modality) {
dialog.initModality(modality);
}
public final Modality getModality() {
return dialog.getModality();
}
public final void initStyle(StageStyle style) {
dialog.initStyle(style);
}
public final void initOwner(Window window) {
dialog.initOwner(window);
}
public final Window getOwner() {
return dialog.getOwner();
}
private ObjectProperty<DialogPane> dialogPane = new SimpleObjectProperty<DialogPane>(this, "dialogPane", new DialogPane()) {
final InvalidationListener expandedListener = o -> {
DialogPane dialogPane = getDialogPane();
if (dialogPane == null) return;
final Node content = dialogPane.getExpandableContent();
final boolean isExpanded = content == null ? false : content.isVisible();
setResizable(isExpanded);
Dialog.this.dialog.sizeToScene();
};
final InvalidationListener headerListener = o -> {
updatePseudoClassState();
};
WeakReference<DialogPane> dialogPaneRef = new WeakReference<>(null);
@Override
protected void invalidated() {
DialogPane oldDialogPane = dialogPaneRef.get();
if (oldDialogPane != null) {
oldDialogPane.expandedProperty().removeListener(expandedListener);
oldDialogPane.headerProperty().removeListener(headerListener);
oldDialogPane.headerTextProperty().removeListener(headerListener);
oldDialogPane.setDialog(null);
}
final DialogPane newDialogPane = getDialogPane();
if (newDialogPane != null) {
newDialogPane.setDialog(Dialog.this);
newDialogPane.getButtonTypes().addListener((ListChangeListener<ButtonType>) c -> {
newDialogPane.requestLayout();
});
newDialogPane.expandedProperty().addListener(expandedListener);
newDialogPane.headerProperty().addListener(headerListener);
newDialogPane.headerTextProperty().addListener(headerListener);
updatePseudoClassState();
newDialogPane.requestLayout();
}
dialog.setDialogPane(newDialogPane);
dialogPaneRef = new WeakReference<DialogPane>(newDialogPane);
}
};
public final ObjectProperty<DialogPane> dialogPaneProperty() {
return dialogPane;
}
public final DialogPane getDialogPane() {
return dialogPane.get();
}
public final void setDialogPane(DialogPane value) {
dialogPane.set(value);
}
public final StringProperty contentTextProperty() {
return getDialogPane().contentTextProperty();
}
public final String getContentText() {
return getDialogPane().getContentText();
}
public final void setContentText(String contentText) {
getDialogPane().setContentText(contentText);
}
public final StringProperty headerTextProperty() {
return getDialogPane().headerTextProperty();
}
public final String getHeaderText() {
return getDialogPane().getHeaderText();
}
public final void setHeaderText(String headerText) {
getDialogPane().setHeaderText(headerText);
}
public final ObjectProperty<Node> graphicProperty() {
return getDialogPane().graphicProperty();
}
public final Node getGraphic() {
return getDialogPane().getGraphic();
}
public final void setGraphic(Node graphic) {
getDialogPane().setGraphic(graphic);
}
private final ObjectProperty<R> resultProperty = new SimpleObjectProperty<R>() {
protected void invalidated() {
close();
}
};
public final ObjectProperty<R> resultProperty() {
return resultProperty;
}
public final R getResult() {
return resultProperty().get();
}
public final void setResult(R value) {
this.resultProperty().set(value);
}
private final ObjectProperty<Callback<ButtonType, R>> resultConverterProperty
= new SimpleObjectProperty<>(this, "resultConverter");
public final ObjectProperty<Callback<ButtonType, R>> resultConverterProperty() {
return resultConverterProperty;
}
public final Callback<ButtonType, R> getResultConverter() {
return resultConverterProperty().get();
}
public final void setResultConverter(Callback<ButtonType, R> value) {
this.resultConverterProperty().set(value);
}
public final ReadOnlyBooleanProperty showingProperty() {
return dialog.showingProperty();
}
public final boolean isShowing() {
return showingProperty().get();
}
public final BooleanProperty resizableProperty() {
return dialog.resizableProperty();
}
public final boolean isResizable() {
return resizableProperty().get();
}
public final void setResizable(boolean resizable) {
resizableProperty().set(resizable);
}
public final ReadOnlyDoubleProperty widthProperty() {
return dialog.widthProperty();
}
public final double getWidth() {
return widthProperty().get();
}
public final void setWidth(double width) {
dialog.setWidth(width);
}
public final ReadOnlyDoubleProperty heightProperty() {
return dialog.heightProperty();
}
public final double getHeight() {
return heightProperty().get();
}
public final void setHeight(double height) {
dialog.setHeight(height);
}
public final StringProperty titleProperty(){
return this.dialog.titleProperty();
}
public final String getTitle(){
return this.dialog.titleProperty().get();
}
public final void setTitle(String title){
this.dialog.titleProperty().set(title);
}
public final double getX() {
return dialog.getX();
}
public final void setX(double x) {
dialog.setX(x);
}
public final ReadOnlyDoubleProperty xProperty() {
return dialog.xProperty();
}
public final double getY() {
return dialog.getY();
}
public final void setY(double y) {
dialog.setY(y);
}
public final ReadOnlyDoubleProperty yProperty() {
return dialog.yProperty();
}
private final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);
@Override public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
return tail.prepend(eventHandlerManager);
}
private ObjectProperty<EventHandler<DialogEvent>> onShowing;
public final void setOnShowing(EventHandler<DialogEvent> value) { onShowingProperty().set(value); }
public final EventHandler<DialogEvent> getOnShowing() {
return onShowing == null ? null : onShowing.get();
}
public final ObjectProperty<EventHandler<DialogEvent>> onShowingProperty() {
if (onShowing == null) {
onShowing = new SimpleObjectProperty<EventHandler<DialogEvent>>(this, "onShowing") {
@Override protected void invalidated() {
eventHandlerManager.setEventHandler(DialogEvent.DIALOG_SHOWING, get());
}
};
}
return onShowing;
}
private ObjectProperty<EventHandler<DialogEvent>> onShown;
public final void setOnShown(EventHandler<DialogEvent> value) { onShownProperty().set(value); }
public final EventHandler<DialogEvent> getOnShown() {
return onShown == null ? null : onShown.get();
}
public final ObjectProperty<EventHandler<DialogEvent>> onShownProperty() {
if (onShown == null) {
onShown = new SimpleObjectProperty<EventHandler<DialogEvent>>(this, "onShown") {
@Override protected void invalidated() {
eventHandlerManager.setEventHandler(DialogEvent.DIALOG_SHOWN, get());
}
};
}
return onShown;
}
private ObjectProperty<EventHandler<DialogEvent>> onHiding;
public final void setOnHiding(EventHandler<DialogEvent> value) { onHidingProperty().set(value); }
public final EventHandler<DialogEvent> getOnHiding() {
return onHiding == null ? null : onHiding.get();
}
public final ObjectProperty<EventHandler<DialogEvent>> onHidingProperty() {
if (onHiding == null) {
onHiding = new SimpleObjectProperty<EventHandler<DialogEvent>>(this, "onHiding") {
@Override protected void invalidated() {
eventHandlerManager.setEventHandler(DialogEvent.DIALOG_HIDING, get());
}
};
}
return onHiding;
}
private ObjectProperty<EventHandler<DialogEvent>> onHidden;
public final void setOnHidden(EventHandler<DialogEvent> value) { onHiddenProperty().set(value); }
public final EventHandler<DialogEvent> getOnHidden() {
return onHidden == null ? null : onHidden.get();
}
public final ObjectProperty<EventHandler<DialogEvent>> onHiddenProperty() {
if (onHidden == null) {
onHidden = new SimpleObjectProperty<EventHandler<DialogEvent>>(this, "onHidden") {
@Override protected void invalidated() {
eventHandlerManager.setEventHandler(DialogEvent.DIALOG_HIDDEN, get());
}
};
}
return onHidden;
}
private ObjectProperty<EventHandler<DialogEvent>> onCloseRequest;
public final void setOnCloseRequest(EventHandler<DialogEvent> value) {
onCloseRequestProperty().set(value);
}
public final EventHandler<DialogEvent> getOnCloseRequest() {
return (onCloseRequest != null) ? onCloseRequest.get() : null;
}
public final ObjectProperty<EventHandler<DialogEvent>>
onCloseRequestProperty() {
if (onCloseRequest == null) {
onCloseRequest = new SimpleObjectProperty<EventHandler<DialogEvent>>(this, "onCloseRequest") {
@Override protected void invalidated() {
eventHandlerManager.setEventHandler(DialogEvent.DIALOG_CLOSE_REQUEST, get());
}
};
}
return onCloseRequest;
}
@SuppressWarnings("unchecked")
void setResultAndClose(ButtonType cmd, boolean close) {
Callback<ButtonType, R> resultConverter = getResultConverter();
R priorResultValue = getResult();
R newResultValue = null;
if (resultConverter == null) {
newResultValue = (R) cmd;
} else {
newResultValue = resultConverter.call(cmd);
}
setResult(newResultValue);
if (close && priorResultValue == newResultValue) {
close();
}
}
private static final PseudoClass HEADER_PSEUDO_CLASS =
PseudoClass.getPseudoClass("header");
private static final PseudoClass NO_HEADER_PSEUDO_CLASS =
PseudoClass.getPseudoClass("no-header");
private void updatePseudoClassState() {
DialogPane dialogPane = getDialogPane();
if (dialogPane != null) {
final boolean hasHeader = getDialogPane().hasHeader();
dialogPane.pseudoClassStateChanged(HEADER_PSEUDO_CLASS, hasHeader);
dialogPane.pseudoClassStateChanged(NO_HEADER_PSEUDO_CLASS, !hasHeader);
}
}
}

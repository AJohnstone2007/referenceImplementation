package javafx.scene.control;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import com.sun.javafx.scene.control.skin.Utils;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleableStringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import com.sun.javafx.css.StyleManager;
import javafx.css.converter.StringConverter;
@DefaultProperty("buttonTypes")
public class DialogPane extends Pane {
static Label createContentLabel(String text) {
Label label = new Label(text);
label.setMaxWidth(Double.MAX_VALUE);
label.setMaxHeight(Double.MAX_VALUE);
label.getStyleClass().add("content");
label.setWrapText(true);
label.setPrefWidth(360);
return label;
}
private final GridPane headerTextPanel;
private final Label contentLabel;
private final StackPane graphicContainer;
private final Node buttonBar;
private final ObservableList<ButtonType> buttons = FXCollections.observableArrayList();
private final Map<ButtonType, Node> buttonNodes = new WeakHashMap<>();
private Node detailsButton;
private Dialog<?> dialog;
public DialogPane() {
getStyleClass().add("dialog-pane");
headerTextPanel = new GridPane();
getChildren().add(headerTextPanel);
graphicContainer = new StackPane();
contentLabel = createContentLabel("");
getChildren().add(contentLabel);
buttons.addListener((ListChangeListener<ButtonType>) c -> {
while (c.next()) {
if (c.wasRemoved()) {
for (ButtonType cmd : c.getRemoved()) {
buttonNodes.remove(cmd);
}
}
if (c.wasAdded()) {
for (ButtonType cmd : c.getAddedSubList()) {
if (! buttonNodes.containsKey(cmd)) {
buttonNodes.put(cmd, createButton(cmd));
}
}
}
}
});
buttonBar = createButtonBar();
if (buttonBar != null) {
getChildren().add(buttonBar);
}
}
private final ObjectProperty<Node> graphicProperty = new StyleableObjectProperty<Node>() {
@Override public CssMetaData getCssMetaData() {
return StyleableProperties.GRAPHIC;
}
@Override public Object getBean() {
return DialogPane.this;
}
@Override public String getName() {
return "graphic";
}
WeakReference<Node> graphicRef = new WeakReference<>(null);
protected void invalidated() {
Node oldGraphic = graphicRef.get();
if (oldGraphic != null) {
getChildren().remove(oldGraphic);
}
Node newGraphic = getGraphic();
graphicRef = new WeakReference<>(newGraphic);
updateHeaderArea();
}
};
public final ObjectProperty<Node> graphicProperty() {
return graphicProperty;
}
public final Node getGraphic() {
return graphicProperty.get();
}
public final void setGraphic(Node graphic) {
this.graphicProperty.set(graphic);
}
private StyleableStringProperty imageUrl = null;
private StyleableStringProperty imageUrlProperty() {
if (imageUrl == null) {
imageUrl = new StyleableStringProperty() {
StyleOrigin origin = StyleOrigin.USER;
@Override
public void applyStyle(StyleOrigin origin, String v) {
this.origin = origin;
if (graphicProperty == null || graphicProperty.isBound() == false) super.applyStyle(origin, v);
this.origin = StyleOrigin.USER;
}
@Override
protected void invalidated() {
final String url = super.get();
if (url == null) {
((StyleableProperty<Node>)(WritableValue<Node>)graphicProperty()).applyStyle(origin, null);
} else {
final Node graphicNode = DialogPane.this.getGraphic();
if (graphicNode instanceof ImageView) {
final ImageView imageView = (ImageView)graphicNode;
final Image image = imageView.getImage();
if (image != null) {
final String imageViewUrl = image.getUrl();
if (url.equals(imageViewUrl)) return;
}
}
final Image img = StyleManager.getInstance().getCachedImage(url);
if (img != null) {
((StyleableProperty<Node>)(WritableValue<Node>)graphicProperty()).applyStyle(origin, new ImageView(img));
}
}
}
@Override
public String get() {
final Node graphic = getGraphic();
if (graphic instanceof ImageView) {
final Image image = ((ImageView)graphic).getImage();
if (image != null) {
return image.getUrl();
}
}
return null;
}
@Override
public StyleOrigin getStyleOrigin() {
return graphicProperty != null ? ((StyleableProperty<Node>)(WritableValue<Node>)graphicProperty).getStyleOrigin() : null;
}
@Override
public Object getBean() {
return DialogPane.this;
}
@Override
public String getName() {
return "imageUrl";
}
@Override
public CssMetaData<DialogPane,String> getCssMetaData() {
return StyleableProperties.GRAPHIC;
}
};
}
return imageUrl;
}
private final ObjectProperty<Node> header = new SimpleObjectProperty<Node>(null) {
WeakReference<Node> headerRef = new WeakReference<>(null);
@Override protected void invalidated() {
Node oldHeader = headerRef.get();
if (oldHeader != null) {
getChildren().remove(oldHeader);
}
Node newHeader = getHeader();
headerRef = new WeakReference<>(newHeader);
updateHeaderArea();
}
};
public final Node getHeader() {
return header.get();
}
public final void setHeader(Node header) {
this.header.setValue(header);
}
public final ObjectProperty<Node> headerProperty() {
return header;
}
private final StringProperty headerText = new SimpleStringProperty(this, "headerText") {
@Override protected void invalidated() {
updateHeaderArea();
requestLayout();
}
};
public final void setHeaderText(String headerText) {
this.headerText.set(headerText);
}
public final String getHeaderText() {
return headerText.get();
}
public final StringProperty headerTextProperty() {
return headerText;
}
private final ObjectProperty<Node> content = new SimpleObjectProperty<Node>(null) {
WeakReference<Node> contentRef = new WeakReference<>(null);
@Override protected void invalidated() {
Node oldContent = contentRef.get();
if (oldContent != null) {
getChildren().remove(oldContent);
}
Node newContent = getContent();
contentRef = new WeakReference<>(newContent);
updateContentArea();
}
};
public final Node getContent() {
return content.get();
}
public final void setContent(Node content) {
this.content.setValue(content);
}
public final ObjectProperty<Node> contentProperty() {
return content;
}
private final StringProperty contentText = new SimpleStringProperty(this, "contentText") {
@Override protected void invalidated() {
updateContentArea();
requestLayout();
}
};
public final void setContentText(String contentText) {
this.contentText.set(contentText);
}
public final String getContentText() {
return contentText.get();
}
public final StringProperty contentTextProperty() {
return contentText;
}
private final ObjectProperty<Node> expandableContentProperty = new SimpleObjectProperty<Node>(null) {
WeakReference<Node> expandableContentRef = new WeakReference<>(null);
@Override protected void invalidated() {
Node oldExpandableContent = expandableContentRef.get();
if (oldExpandableContent != null) {
getChildren().remove(oldExpandableContent);
}
Node newExpandableContent = getExpandableContent();
expandableContentRef = new WeakReference<Node>(newExpandableContent);
if (newExpandableContent != null) {
newExpandableContent.setVisible(isExpanded());
newExpandableContent.setManaged(isExpanded());
if (!newExpandableContent.getStyleClass().contains("expandable-content")) {
newExpandableContent.getStyleClass().add("expandable-content");
}
getChildren().add(newExpandableContent);
}
}
};
public final ObjectProperty<Node> expandableContentProperty() {
return expandableContentProperty;
}
public final Node getExpandableContent() {
return expandableContentProperty.get();
}
public final void setExpandableContent(Node content) {
this.expandableContentProperty.set(content);
}
private final BooleanProperty expandedProperty = new SimpleBooleanProperty(this, "expanded", false) {
protected void invalidated() {
final Node expandableContent = getExpandableContent();
if (expandableContent != null) {
expandableContent.setVisible(isExpanded());
}
requestLayout();
}
};
public final BooleanProperty expandedProperty() {
return expandedProperty;
}
public final boolean isExpanded() {
return expandedProperty().get();
}
public final void setExpanded(boolean value) {
expandedProperty().set(value);
}
public final ObservableList<ButtonType> getButtonTypes() {
return buttons;
}
public final Node lookupButton(ButtonType buttonType) {
return buttonNodes.get(buttonType);
}
protected Node createButtonBar() {
ButtonBar buttonBar = new ButtonBar();
buttonBar.setMaxWidth(Double.MAX_VALUE);
updateButtons(buttonBar);
getButtonTypes().addListener((ListChangeListener<? super ButtonType>) c -> updateButtons(buttonBar));
expandableContentProperty().addListener(o -> updateButtons(buttonBar));
return buttonBar;
}
protected Node createButton(ButtonType buttonType) {
final Button button = new Button(buttonType.getText());
final ButtonData buttonData = buttonType.getButtonData();
ButtonBar.setButtonData(button, buttonData);
button.setDefaultButton(buttonData.isDefaultButton());
button.setCancelButton(buttonData.isCancelButton());
button.addEventHandler(ActionEvent.ACTION, ae -> {
if (ae.isConsumed()) return;
if (dialog != null) {
dialog.setResultAndClose(buttonType, true);
}
});
return button;
}
protected Node createDetailsButton() {
final Hyperlink detailsButton = new Hyperlink();
final String moreText = ControlResources.getString("Dialog.detail.button.more");
final String lessText = ControlResources.getString("Dialog.detail.button.less");
InvalidationListener expandedListener = o -> {
final boolean isExpanded = isExpanded();
detailsButton.setText(isExpanded ? lessText : moreText);
detailsButton.getStyleClass().setAll("details-button", (isExpanded ? "less" : "more"));
};
expandedListener.invalidated(null);
expandedProperty().addListener(expandedListener);
detailsButton.setOnAction(ae -> setExpanded(!isExpanded()));
return detailsButton;
}
private double oldHeight = -1;
@Override protected void layoutChildren() {
final boolean hasHeader = hasHeader();
final double w = Math.max(minWidth(-1), getWidth());
final double minHeight = minHeight(w);
final double prefHeight = prefHeight(w);
final double maxHeight = maxHeight(w);
final double currentHeight = getHeight();
final double dialogHeight = dialog == null ? 0 : dialog.dialog.getSceneHeight();
double h;
if (prefHeight > currentHeight && prefHeight > minHeight && (prefHeight <= dialogHeight || dialogHeight == 0)) {
h = Utils.boundedSize(prefHeight, minHeight, maxHeight);
resize(w, h);
} else {
boolean isDialogGrowing = currentHeight > oldHeight;
if (isDialogGrowing) {
double _h = currentHeight < prefHeight ?
Math.min(prefHeight, currentHeight) : Math.max(prefHeight, dialogHeight);
h = Utils.boundedSize(_h, minHeight, maxHeight);
} else {
h = Utils.boundedSize(Math.min(currentHeight, dialogHeight), minHeight, maxHeight);
}
resize(w, h);
}
h -= (snappedTopInset() + snappedBottomInset());
oldHeight = h;
final double leftPadding = snappedLeftInset();
final double topPadding = snappedTopInset();
final double rightPadding = snappedRightInset();
final Node header = getActualHeader();
final Node content = getActualContent();
final Node graphic = getActualGraphic();
final Node expandableContent = getExpandableContent();
final double graphicPrefWidth = hasHeader || graphic == null ? 0 : graphic.prefWidth(-1);
final double headerPrefHeight = hasHeader ? header.prefHeight(w) : 0;
final double buttonBarPrefHeight = buttonBar == null ? 0 : buttonBar.prefHeight(w);
final double graphicPrefHeight = hasHeader || graphic == null ? 0 : graphic.prefHeight(-1);
final double expandableContentPrefHeight;
final double contentAreaHeight;
final double contentAndGraphicHeight;
final double availableContentWidth = w - graphicPrefWidth - leftPadding - rightPadding;
if (isExpanded()) {
contentAreaHeight = isExpanded() ? content.prefHeight(availableContentWidth) : 0;
contentAndGraphicHeight = hasHeader ? contentAreaHeight : Math.max(graphicPrefHeight, contentAreaHeight);
expandableContentPrefHeight = h - (headerPrefHeight + contentAndGraphicHeight + buttonBarPrefHeight);
} else {
expandableContentPrefHeight = isExpanded() ? expandableContent.prefHeight(w) : 0;
contentAreaHeight = h - (headerPrefHeight + expandableContentPrefHeight + buttonBarPrefHeight);
contentAndGraphicHeight = hasHeader ? contentAreaHeight : Math.max(graphicPrefHeight, contentAreaHeight);
}
double x = leftPadding;
double y = topPadding;
if (! hasHeader) {
if (graphic != null) {
graphic.resizeRelocate(x, y, graphicPrefWidth, graphicPrefHeight);
x += graphicPrefWidth;
}
} else {
header.resizeRelocate(x, y, w - (leftPadding + rightPadding), headerPrefHeight);
y += headerPrefHeight;
}
content.resizeRelocate(x, y, availableContentWidth, contentAreaHeight);
y += hasHeader ? contentAreaHeight : contentAndGraphicHeight;
if (expandableContent != null) {
expandableContent.resizeRelocate(leftPadding, y, w - rightPadding, expandableContentPrefHeight);
y += expandableContentPrefHeight;
}
if (buttonBar != null) {
buttonBar.resizeRelocate(leftPadding,
y,
w - (leftPadding + rightPadding),
buttonBarPrefHeight);
}
}
@Override protected double computeMinWidth(double height) {
double headerMinWidth = hasHeader() ? getActualHeader().minWidth(height) + 10 : 0;
double contentMinWidth = getActualContent().minWidth(height);
double buttonBarMinWidth = buttonBar == null ? 0 : buttonBar.minWidth(height);
double graphicMinWidth = getActualGraphic().minWidth(height);
double expandableContentMinWidth = 0;
final Node expandableContent = getExpandableContent();
if (isExpanded() && expandableContent != null) {
expandableContentMinWidth = expandableContent.minWidth(height);
}
double minWidth = snappedLeftInset() +
(hasHeader() ? 0 : graphicMinWidth) +
Math.max(Math.max(headerMinWidth, expandableContentMinWidth), Math.max(contentMinWidth, buttonBarMinWidth)) +
snappedRightInset();
return snapSizeX(minWidth);
}
@Override protected double computeMinHeight(double width) {
final boolean hasHeader = hasHeader();
double headerMinHeight = hasHeader ? getActualHeader().minHeight(width) : 0;
double buttonBarMinHeight = buttonBar == null ? 0 : buttonBar.minHeight(width);
Node graphic = getActualGraphic();
double graphicMinWidth = hasHeader ? 0 : graphic.minWidth(-1);
double graphicMinHeight = hasHeader ? 0 : graphic.minHeight(width);
Node content = getActualContent();
double contentAvailableWidth = width == Region.USE_COMPUTED_SIZE ? Region.USE_COMPUTED_SIZE :
hasHeader ? width : (width - graphicMinWidth);
double contentMinHeight = content.minHeight(contentAvailableWidth);
double expandableContentMinHeight = 0;
final Node expandableContent = getExpandableContent();
if (isExpanded() && expandableContent != null) {
expandableContentMinHeight = expandableContent.minHeight(width);
}
double minHeight = snappedTopInset() +
headerMinHeight +
Math.max(graphicMinHeight, contentMinHeight) +
expandableContentMinHeight +
buttonBarMinHeight +
snappedBottomInset();
return snapSizeY(minHeight);
}
@Override protected double computePrefWidth(double height) {
double headerPrefWidth = hasHeader() ? getActualHeader().prefWidth(height) + 10 : 0;
double contentPrefWidth = getActualContent().prefWidth(height);
double buttonBarPrefWidth = buttonBar == null ? 0 : buttonBar.prefWidth(height);
double graphicPrefWidth = getActualGraphic().prefWidth(height);
double expandableContentPrefWidth = 0;
final Node expandableContent = getExpandableContent();
if (isExpanded() && expandableContent != null) {
expandableContentPrefWidth = expandableContent.prefWidth(height);
}
double prefWidth = snappedLeftInset() +
(hasHeader() ? 0 : graphicPrefWidth) +
Math.max(Math.max(headerPrefWidth, expandableContentPrefWidth), Math.max(contentPrefWidth, buttonBarPrefWidth)) +
snappedRightInset();
return snapSizeX(prefWidth);
}
@Override protected double computePrefHeight(double width) {
final boolean hasHeader = hasHeader();
double headerPrefHeight = hasHeader ? getActualHeader().prefHeight(width) : 0;
double buttonBarPrefHeight = buttonBar == null ? 0 : buttonBar.prefHeight(width);
Node graphic = getActualGraphic();
double graphicPrefWidth = hasHeader ? 0 : graphic.prefWidth(-1);
double graphicPrefHeight = hasHeader ? 0 : graphic.prefHeight(width);
Node content = getActualContent();
double contentAvailableWidth = width == Region.USE_COMPUTED_SIZE ? Region.USE_COMPUTED_SIZE :
hasHeader ? width : (width - graphicPrefWidth);
double contentPrefHeight = content.prefHeight(contentAvailableWidth);
double expandableContentPrefHeight = 0;
final Node expandableContent = getExpandableContent();
if (isExpanded() && expandableContent != null) {
expandableContentPrefHeight = expandableContent.prefHeight(width);
}
double prefHeight = snappedTopInset() +
headerPrefHeight +
Math.max(graphicPrefHeight, contentPrefHeight) +
expandableContentPrefHeight +
buttonBarPrefHeight +
snappedBottomInset();
return snapSizeY(prefHeight);
}
private void updateButtons(ButtonBar buttonBar) {
buttonBar.getButtons().clear();
if (hasExpandableContent()) {
if (detailsButton == null) {
detailsButton = createDetailsButton();
}
ButtonBar.setButtonData(detailsButton, ButtonData.HELP_2);
buttonBar.getButtons().add(detailsButton);
ButtonBar.setButtonUniformSize(detailsButton, false);
}
boolean hasDefault = false;
for (ButtonType cmd : getButtonTypes()) {
Node button = buttonNodes.get(cmd);
if (button instanceof Button) {
ButtonData buttonType = cmd.getButtonData();
((Button)button).setDefaultButton(!hasDefault && buttonType != null && buttonType.isDefaultButton());
((Button)button).setCancelButton(buttonType != null && buttonType.isCancelButton());
hasDefault |= buttonType != null && buttonType.isDefaultButton();
}
buttonBar.getButtons().add(button);
}
}
private Node getActualContent() {
Node content = getContent();
return content == null ? contentLabel : content;
}
private Node getActualHeader() {
Node header = getHeader();
return header == null ? headerTextPanel : header;
}
private Node getActualGraphic() {
return headerTextPanel;
}
private void updateHeaderArea() {
Node header = getHeader();
if (header != null) {
if (! getChildren().contains(header)) {
getChildren().add(header);
}
headerTextPanel.setVisible(false);
headerTextPanel.setManaged(false);
} else {
final String headerText = getHeaderText();
headerTextPanel.getChildren().clear();
headerTextPanel.getStyleClass().clear();
headerTextPanel.setMaxWidth(Double.MAX_VALUE);
if (headerText != null && ! headerText.isEmpty()) {
headerTextPanel.getStyleClass().add("header-panel");
}
Label headerLabel = new Label(headerText);
headerLabel.setWrapText(true);
headerLabel.setAlignment(Pos.CENTER_LEFT);
headerLabel.setMaxWidth(Double.MAX_VALUE);
headerLabel.setMaxHeight(Double.MAX_VALUE);
headerTextPanel.add(headerLabel, 0, 0);
graphicContainer.getChildren().clear();
if (! graphicContainer.getStyleClass().contains("graphic-container")) {
graphicContainer.getStyleClass().add("graphic-container");
}
final Node graphic = getGraphic();
if (graphic != null) {
graphicContainer.getChildren().add(graphic);
}
headerTextPanel.add(graphicContainer, 1, 0);
ColumnConstraints textColumn = new ColumnConstraints();
textColumn.setFillWidth(true);
textColumn.setHgrow(Priority.ALWAYS);
ColumnConstraints graphicColumn = new ColumnConstraints();
graphicColumn.setFillWidth(false);
graphicColumn.setHgrow(Priority.NEVER);
headerTextPanel.getColumnConstraints().setAll(textColumn , graphicColumn);
headerTextPanel.setVisible(true);
headerTextPanel.setManaged(true);
}
}
private void updateContentArea() {
Node content = getContent();
if (content != null) {
if (! getChildren().contains(content)) {
getChildren().add(content);
}
if (! content.getStyleClass().contains("content")) {
content.getStyleClass().add("content");
}
contentLabel.setVisible(false);
contentLabel.setManaged(false);
} else {
final String contentText = getContentText();
final boolean visible = contentText != null && !contentText.isEmpty();
contentLabel.setText(visible ? contentText : "");
contentLabel.setVisible(visible);
contentLabel.setManaged(visible);
}
}
boolean hasHeader() {
return getHeader() != null || isTextHeader();
}
private boolean isTextHeader() {
String headerText = getHeaderText();
return headerText != null && !headerText.isEmpty();
}
boolean hasExpandableContent() {
return getExpandableContent() != null;
}
void setDialog(Dialog<?> dialog) {
this.dialog = dialog;
}
private static class StyleableProperties {
private static final CssMetaData<DialogPane,String> GRAPHIC =
new CssMetaData<DialogPane,String>("-fx-graphic",
StringConverter.getInstance()) {
@Override
public boolean isSettable(DialogPane n) {
return n.graphicProperty == null || !n.graphicProperty.isBound();
}
@Override
public StyleableProperty<String> getStyleableProperty(DialogPane n) {
return n.imageUrlProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Region.getClassCssMetaData());
Collections.addAll(styleables,
GRAPHIC
);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
}

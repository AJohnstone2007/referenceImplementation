package javafx.scene.control;
import com.sun.javafx.beans.IDProperty;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.stage.PopupWindowHelper;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.StyleOrigin;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableStringProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.StringConverter;
import javafx.css.converter.DurationConverter;
import javafx.scene.control.skin.TooltipSkin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import javafx.util.Duration;
@IDProperty("id")
public class Tooltip extends PopupControl {
private static String TOOLTIP_PROP_KEY = "javafx.scene.control.Tooltip";
private static int TOOLTIP_XOFFSET = 10;
private static int TOOLTIP_YOFFSET = 7;
private static TooltipBehavior BEHAVIOR = new TooltipBehavior(false);
public static void install(Node node, Tooltip t) {
BEHAVIOR.install(node, t);
}
public static void uninstall(Node node, Tooltip t) {
BEHAVIOR.uninstall(node);
}
public Tooltip() {
this(null);
}
public Tooltip(String text) {
super();
if (text != null) setText(text);
bridge = new CSSBridge();
PopupWindowHelper.getContent(this).setAll(bridge);
getStyleClass().setAll("tooltip");
}
public final StringProperty textProperty() { return text; }
public final void setText(String value) {
textProperty().setValue(value);
}
public final String getText() { return text.getValue() == null ? "" : text.getValue(); }
private final StringProperty text = new SimpleStringProperty(this, "text", "") {
@Override protected void invalidated() {
super.invalidated();
final String value = get();
if (isShowing() && value != null && !value.equals(getText())) {
setAnchorX(BEHAVIOR.lastMouseX);
setAnchorY(BEHAVIOR.lastMouseY);
}
}
};
public final ObjectProperty<TextAlignment> textAlignmentProperty() {
return textAlignment;
}
public final void setTextAlignment(TextAlignment value) {
textAlignmentProperty().setValue(value);
}
public final TextAlignment getTextAlignment() {
return textAlignmentProperty().getValue();
}
private final ObjectProperty<TextAlignment> textAlignment =
new SimpleStyleableObjectProperty<>(TEXT_ALIGNMENT, this, "textAlignment", TextAlignment.LEFT);;
public final ObjectProperty<OverrunStyle> textOverrunProperty() {
return textOverrun;
}
public final void setTextOverrun(OverrunStyle value) {
textOverrunProperty().setValue(value);
}
public final OverrunStyle getTextOverrun() {
return textOverrunProperty().getValue();
}
private final ObjectProperty<OverrunStyle> textOverrun =
new SimpleStyleableObjectProperty<OverrunStyle>(TEXT_OVERRUN, this, "textOverrun", OverrunStyle.ELLIPSIS);
public final BooleanProperty wrapTextProperty() {
return wrapText;
}
public final void setWrapText(boolean value) {
wrapTextProperty().setValue(value);
}
public final boolean isWrapText() {
return wrapTextProperty().getValue();
}
private final BooleanProperty wrapText =
new SimpleStyleableBooleanProperty(WRAP_TEXT, this, "wrapText", false);
public final ObjectProperty<Font> fontProperty() {
return font;
}
public final void setFont(Font value) {
fontProperty().setValue(value);
}
public final Font getFont() {
return fontProperty().getValue();
}
private final ObjectProperty<Font> font = new StyleableObjectProperty<Font>(Font.getDefault()) {
private boolean fontSetByCss = false;
@Override public void applyStyle(StyleOrigin newOrigin, Font value) {
try {
fontSetByCss = true;
super.applyStyle(newOrigin, value);
} catch(Exception e) {
throw e;
} finally {
fontSetByCss = false;
}
}
@Override public void set(Font value) {
final Font oldValue = get();
StyleOrigin origin = ((StyleableObjectProperty<Font>)font).getStyleOrigin();
if (origin == null || (value != null ? !value.equals(oldValue) : oldValue != null)) {
super.set(value);
}
}
@Override protected void invalidated() {
if(fontSetByCss == false) {
NodeHelper.reapplyCSS(Tooltip.this.bridge);
}
}
@Override public CssMetaData<Tooltip.CSSBridge,Font> getCssMetaData() {
return FONT;
}
@Override public Object getBean() {
return Tooltip.this;
}
@Override public String getName() {
return "font";
}
};
public final ObjectProperty<Duration> showDelayProperty() {
return showDelayProperty;
}
public final void setShowDelay(Duration showDelay) {
showDelayProperty.set(showDelay);
}
public final Duration getShowDelay() {
return showDelayProperty.get();
}
private final ObjectProperty<Duration> showDelayProperty
= new SimpleStyleableObjectProperty<>(SHOW_DELAY, this, "showDelay", new Duration(1000));
public final ObjectProperty<Duration> showDurationProperty() {
return showDurationProperty;
}
public final void setShowDuration(Duration showDuration) {
showDurationProperty.set(showDuration);
}
public final Duration getShowDuration() {
return showDurationProperty.get();
}
private final ObjectProperty<Duration> showDurationProperty
= new SimpleStyleableObjectProperty<>(SHOW_DURATION, this, "showDuration", new Duration(5000));
public final ObjectProperty<Duration> hideDelayProperty() {
return hideDelayProperty;
}
public final void setHideDelay(Duration hideDelay) {
hideDelayProperty.set(hideDelay);
}
public final Duration getHideDelay() {
return hideDelayProperty.get();
}
private final ObjectProperty<Duration> hideDelayProperty
= new SimpleStyleableObjectProperty<>(HIDE_DELAY, this, "hideDelay", new Duration(200));
public final ObjectProperty<Node> graphicProperty() {
return graphic;
}
public final void setGraphic(Node value) {
graphicProperty().setValue(value);
}
public final Node getGraphic() {
return graphicProperty().getValue();
}
private final ObjectProperty<Node> graphic = new StyleableObjectProperty<Node>() {
@Override public CssMetaData getCssMetaData() {
return GRAPHIC;
}
@Override public Object getBean() {
return Tooltip.this;
}
@Override public String getName() {
return "graphic";
}
};
private StyleableStringProperty imageUrlProperty() {
if (imageUrl == null) {
imageUrl = new StyleableStringProperty() {
StyleOrigin origin = StyleOrigin.USER;
@Override public void applyStyle(StyleOrigin origin, String v) {
this.origin = origin;
if (graphic == null || graphic.isBound() == false) super.applyStyle(origin, v);
this.origin = StyleOrigin.USER;
}
@Override protected void invalidated() {
final String url = super.get();
if (url == null) {
((StyleableProperty<Node>)(WritableValue<Node>)graphicProperty()).applyStyle(origin, null);
} else {
final Node graphicNode = Tooltip.this.getGraphic();
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
@Override public String get() {
final Node graphic = getGraphic();
if (graphic instanceof ImageView) {
final Image image = ((ImageView)graphic).getImage();
if (image != null) {
return image.getUrl();
}
}
return null;
}
@Override public StyleOrigin getStyleOrigin() {
return graphic != null ? ((StyleableProperty<Node>)(WritableValue<Node>)graphic).getStyleOrigin() : null;
}
@Override public Object getBean() {
return Tooltip.this;
}
@Override public String getName() {
return "imageUrl";
}
@Override public CssMetaData<Tooltip.CSSBridge,String> getCssMetaData() {
return GRAPHIC;
}
};
}
return imageUrl;
}
private StyleableStringProperty imageUrl = null;
public final ObjectProperty<ContentDisplay> contentDisplayProperty() {
return contentDisplay;
}
public final void setContentDisplay(ContentDisplay value) {
contentDisplayProperty().setValue(value);
}
public final ContentDisplay getContentDisplay() {
return contentDisplayProperty().getValue();
}
private final ObjectProperty<ContentDisplay> contentDisplay =
new SimpleStyleableObjectProperty<>(CONTENT_DISPLAY, this, "contentDisplay", ContentDisplay.LEFT);
public final DoubleProperty graphicTextGapProperty() {
return graphicTextGap;
}
public final void setGraphicTextGap(double value) {
graphicTextGapProperty().setValue(value);
}
public final double getGraphicTextGap() {
return graphicTextGapProperty().getValue();
}
private final DoubleProperty graphicTextGap =
new SimpleStyleableDoubleProperty(GRAPHIC_TEXT_GAP, this, "graphicTextGap", 4d);
private final ReadOnlyBooleanWrapper activated = new ReadOnlyBooleanWrapper(this, "activated");
final void setActivated(boolean value) { activated.set(value); }
public final boolean isActivated() { return activated.get(); }
public final ReadOnlyBooleanProperty activatedProperty() { return activated.getReadOnlyProperty(); }
@Override protected Skin<?> createDefaultSkin() {
return new TooltipSkin(this);
}
private static final CssMetaData<Tooltip.CSSBridge,Font> FONT =
new FontCssMetaData<Tooltip.CSSBridge>("-fx-font", Font.getDefault()) {
@Override
public boolean isSettable(Tooltip.CSSBridge cssBridge) {
return !cssBridge.tooltip.fontProperty().isBound();
}
@Override
public StyleableProperty<Font> getStyleableProperty(Tooltip.CSSBridge cssBridge) {
return (StyleableProperty<Font>)(WritableValue<Font>)cssBridge.tooltip.fontProperty();
}
};
private static final CssMetaData<Tooltip.CSSBridge,TextAlignment> TEXT_ALIGNMENT =
new CssMetaData<Tooltip.CSSBridge,TextAlignment>("-fx-text-alignment",
new EnumConverter<TextAlignment>(TextAlignment.class),
TextAlignment.LEFT) {
@Override
public boolean isSettable(Tooltip.CSSBridge cssBridge) {
return !cssBridge.tooltip.textAlignmentProperty().isBound();
}
@Override
public StyleableProperty<TextAlignment> getStyleableProperty(Tooltip.CSSBridge cssBridge) {
return (StyleableProperty<TextAlignment>)(WritableValue<TextAlignment>)cssBridge.tooltip.textAlignmentProperty();
}
};
private static final CssMetaData<Tooltip.CSSBridge,OverrunStyle> TEXT_OVERRUN =
new CssMetaData<Tooltip.CSSBridge,OverrunStyle>("-fx-text-overrun",
new EnumConverter<OverrunStyle>(OverrunStyle.class),
OverrunStyle.ELLIPSIS) {
@Override
public boolean isSettable(Tooltip.CSSBridge cssBridge) {
return !cssBridge.tooltip.textOverrunProperty().isBound();
}
@Override
public StyleableProperty<OverrunStyle> getStyleableProperty(Tooltip.CSSBridge cssBridge) {
return (StyleableProperty<OverrunStyle>)(WritableValue<OverrunStyle>)cssBridge.tooltip.textOverrunProperty();
}
};
private static final CssMetaData<Tooltip.CSSBridge,Boolean> WRAP_TEXT =
new CssMetaData<Tooltip.CSSBridge,Boolean>("-fx-wrap-text",
BooleanConverter.getInstance(), Boolean.FALSE) {
@Override
public boolean isSettable(Tooltip.CSSBridge cssBridge) {
return !cssBridge.tooltip.wrapTextProperty().isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(Tooltip.CSSBridge cssBridge) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)cssBridge.tooltip.wrapTextProperty();
}
};
private static final CssMetaData<Tooltip.CSSBridge,String> GRAPHIC =
new CssMetaData<Tooltip.CSSBridge,String>("-fx-graphic",
StringConverter.getInstance()) {
@Override
public boolean isSettable(Tooltip.CSSBridge cssBridge) {
return !cssBridge.tooltip.graphicProperty().isBound();
}
@Override
public StyleableProperty<String> getStyleableProperty(Tooltip.CSSBridge cssBridge) {
return (StyleableProperty<String>)cssBridge.tooltip.imageUrlProperty();
}
};
private static final CssMetaData<Tooltip.CSSBridge,ContentDisplay> CONTENT_DISPLAY =
new CssMetaData<Tooltip.CSSBridge,ContentDisplay>("-fx-content-display",
new EnumConverter<ContentDisplay>(ContentDisplay.class),
ContentDisplay.LEFT) {
@Override
public boolean isSettable(Tooltip.CSSBridge cssBridge) {
return !cssBridge.tooltip.contentDisplayProperty().isBound();
}
@Override
public StyleableProperty<ContentDisplay> getStyleableProperty(Tooltip.CSSBridge cssBridge) {
return (StyleableProperty<ContentDisplay>)(WritableValue<ContentDisplay>)cssBridge.tooltip.contentDisplayProperty();
}
};
private static final CssMetaData<Tooltip.CSSBridge,Number> GRAPHIC_TEXT_GAP =
new CssMetaData<Tooltip.CSSBridge,Number>("-fx-graphic-text-gap",
SizeConverter.getInstance(), 4.0) {
@Override
public boolean isSettable(Tooltip.CSSBridge cssBridge) {
return !cssBridge.tooltip.graphicTextGapProperty().isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Tooltip.CSSBridge cssBridge) {
return (StyleableProperty<Number>)(WritableValue<Number>)cssBridge.tooltip.graphicTextGapProperty();
}
};
private static final CssMetaData<Tooltip.CSSBridge,Duration> SHOW_DELAY =
new CssMetaData<Tooltip.CSSBridge,Duration>("-fx-show-delay",
DurationConverter.getInstance(), new Duration(1000)) {
@Override
public boolean isSettable(Tooltip.CSSBridge cssBridge) {
return !cssBridge.tooltip.showDelayProperty().isBound();
}
@Override
public StyleableProperty<Duration> getStyleableProperty(Tooltip.CSSBridge cssBridge) {
return (StyleableProperty<Duration>)(WritableValue<Duration>)cssBridge.tooltip.showDelayProperty();
}
};
private static final CssMetaData<Tooltip.CSSBridge,Duration> SHOW_DURATION =
new CssMetaData<Tooltip.CSSBridge,Duration>("-fx-show-duration",
DurationConverter.getInstance(), new Duration(5000)) {
@Override
public boolean isSettable(Tooltip.CSSBridge cssBridge) {
return !cssBridge.tooltip.showDurationProperty().isBound();
}
@Override
public StyleableProperty<Duration> getStyleableProperty(Tooltip.CSSBridge cssBridge) {
return (StyleableProperty<Duration>)(WritableValue<Duration>)cssBridge.tooltip.showDurationProperty();
}
};
private static final CssMetaData<Tooltip.CSSBridge,Duration> HIDE_DELAY =
new CssMetaData<Tooltip.CSSBridge,Duration>("-fx-hide-delay",
DurationConverter.getInstance(), new Duration(200)) {
@Override
public boolean isSettable(Tooltip.CSSBridge cssBridge) {
return !cssBridge.tooltip.hideDelayProperty().isBound();
}
@Override
public StyleableProperty<Duration> getStyleableProperty(Tooltip.CSSBridge cssBridge) {
return (StyleableProperty<Duration>)(WritableValue<Duration>)cssBridge.tooltip.hideDelayProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(PopupControl.getClassCssMetaData());
styleables.add(FONT);
styleables.add(TEXT_ALIGNMENT);
styleables.add(TEXT_OVERRUN);
styleables.add(WRAP_TEXT);
styleables.add(GRAPHIC);
styleables.add(CONTENT_DISPLAY);
styleables.add(GRAPHIC_TEXT_GAP);
styleables.add(SHOW_DELAY);
styleables.add(SHOW_DURATION);
styleables.add(HIDE_DELAY);
STYLEABLES = Collections.unmodifiableList(styleables);
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
@Override public Styleable getStyleableParent() {
if (BEHAVIOR.hoveredNode == null) {
return super.getStyleableParent();
}
return BEHAVIOR.hoveredNode;
}
private final class CSSBridge extends PopupControl.CSSBridge {
private Tooltip tooltip = Tooltip.this;
CSSBridge() {
super();
setAccessibleRole(AccessibleRole.TOOLTIP);
}
}
private static class TooltipBehavior {
private Timeline activationTimer = new Timeline();
private Timeline hideTimer = new Timeline();
private Timeline leftTimer = new Timeline();
private Node hoveredNode;
private Tooltip activatedTooltip;
private Tooltip visibleTooltip;
private double lastMouseX;
private double lastMouseY;
private boolean hideOnExit;
private boolean cssForced = false;
TooltipBehavior(final boolean hideOnExit) {
this.hideOnExit = hideOnExit;
activationTimer.setOnFinished(event -> {
assert activatedTooltip != null;
final Window owner = getWindow(hoveredNode);
final boolean treeVisible = isWindowHierarchyVisible(hoveredNode);
if (owner != null && owner.isShowing() && treeVisible) {
double x = lastMouseX;
double y = lastMouseY;
NodeOrientation nodeOrientation = hoveredNode.getEffectiveNodeOrientation();
activatedTooltip.getScene().setNodeOrientation(nodeOrientation);
if (nodeOrientation == NodeOrientation.RIGHT_TO_LEFT) {
x -= activatedTooltip.getWidth();
}
activatedTooltip.show(owner, x+TOOLTIP_XOFFSET, y+TOOLTIP_YOFFSET);
if ((y+TOOLTIP_YOFFSET) > activatedTooltip.getAnchorY()) {
activatedTooltip.hide();
y -= activatedTooltip.getHeight();
activatedTooltip.show(owner, x+TOOLTIP_XOFFSET, y);
}
visibleTooltip = activatedTooltip;
hoveredNode = null;
if (activatedTooltip.getShowDuration() != null) {
hideTimer.getKeyFrames().setAll(new KeyFrame(activatedTooltip.getShowDuration()));
}
hideTimer.playFromStart();
}
activatedTooltip.setActivated(false);
activatedTooltip = null;
});
hideTimer.setOnFinished(event -> {
assert visibleTooltip != null;
visibleTooltip.hide();
visibleTooltip = null;
hoveredNode = null;
});
leftTimer.setOnFinished(event -> {
if (!hideOnExit) {
assert visibleTooltip != null;
visibleTooltip.hide();
visibleTooltip = null;
hoveredNode = null;
}
});
}
private EventHandler<MouseEvent> MOVE_HANDLER = (MouseEvent event) -> {
lastMouseX = event.getScreenX();
lastMouseY = event.getScreenY();
if (hideTimer.getStatus() == Timeline.Status.RUNNING) {
return;
}
hoveredNode = (Node) event.getSource();
Tooltip t = (Tooltip) hoveredNode.getProperties().get(TOOLTIP_PROP_KEY);
if (t != null) {
final Window owner = getWindow(hoveredNode);
final boolean treeVisible = isWindowHierarchyVisible(hoveredNode);
if (owner != null && treeVisible) {
if (leftTimer.getStatus() == Timeline.Status.RUNNING) {
if (visibleTooltip != null) visibleTooltip.hide();
visibleTooltip = t;
t.show(owner, event.getScreenX()+TOOLTIP_XOFFSET,
event.getScreenY()+TOOLTIP_YOFFSET);
leftTimer.stop();
if (t.getShowDuration() != null) {
hideTimer.getKeyFrames().setAll(new KeyFrame(t.getShowDuration()));
}
hideTimer.playFromStart();
} else {
if (!cssForced) {
double opacity = t.getOpacity();
t.setOpacity(0);
t.show(owner);
t.hide();
t.setOpacity(opacity);
cssForced = true;
}
t.setActivated(true);
activatedTooltip = t;
activationTimer.stop();
if (t.getShowDelay() != null) {
activationTimer.getKeyFrames().setAll(new KeyFrame(t.getShowDelay()));
}
activationTimer.playFromStart();
}
}
} else {
}
};
private EventHandler<MouseEvent> LEAVING_HANDLER = (MouseEvent event) -> {
if (activationTimer.getStatus() == Timeline.Status.RUNNING) {
activationTimer.stop();
} else if (hideTimer.getStatus() == Timeline.Status.RUNNING) {
assert visibleTooltip != null;
hideTimer.stop();
if (hideOnExit) visibleTooltip.hide();
Node source = (Node) event.getSource();
Tooltip t = (Tooltip) source.getProperties().get(TOOLTIP_PROP_KEY);
if (t != null) {
if (t.getHideDelay() != null) {
leftTimer.getKeyFrames().setAll(new KeyFrame(t.getHideDelay()));
}
leftTimer.playFromStart();
}
}
hoveredNode = null;
activatedTooltip = null;
if (hideOnExit) visibleTooltip = null;
};
private EventHandler<MouseEvent> KILL_HANDLER = (MouseEvent event) -> {
activationTimer.stop();
hideTimer.stop();
leftTimer.stop();
if (visibleTooltip != null) visibleTooltip.hide();
hoveredNode = null;
activatedTooltip = null;
visibleTooltip = null;
};
private void install(Node node, Tooltip t) {
if (node == null) return;
node.addEventHandler(MouseEvent.MOUSE_MOVED, MOVE_HANDLER);
node.addEventHandler(MouseEvent.MOUSE_EXITED, LEAVING_HANDLER);
node.addEventHandler(MouseEvent.MOUSE_PRESSED, KILL_HANDLER);
node.getProperties().put(TOOLTIP_PROP_KEY, t);
}
private void uninstall(Node node) {
if (node == null) return;
node.removeEventHandler(MouseEvent.MOUSE_MOVED, MOVE_HANDLER);
node.removeEventHandler(MouseEvent.MOUSE_EXITED, LEAVING_HANDLER);
node.removeEventHandler(MouseEvent.MOUSE_PRESSED, KILL_HANDLER);
Tooltip t = (Tooltip)node.getProperties().get(TOOLTIP_PROP_KEY);
if (t != null) {
node.getProperties().remove(TOOLTIP_PROP_KEY);
if (t.equals(visibleTooltip) || t.equals(activatedTooltip)) {
KILL_HANDLER.handle(null);
}
}
}
private Window getWindow(final Node node) {
final Scene scene = node == null ? null : node.getScene();
return scene == null ? null : scene.getWindow();
}
private boolean isWindowHierarchyVisible(Node node) {
boolean treeVisible = node != null;
Parent parent = node == null ? null : node.getParent();
while (parent != null && treeVisible) {
treeVisible = parent.isVisible();
parent = parent.getParent();
}
return treeVisible;
}
}
}

package javafx.scene.control;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.NodeHelper;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.InsetsConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.StringConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.beans.DefaultProperty;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleableStringProperty;
@DefaultProperty("text")
public abstract class Labeled extends Control {
private final static String DEFAULT_ELLIPSIS_STRING = "...";
public Labeled() { }
public Labeled(String text) {
setText(text);
}
public Labeled(String text, Node graphic) {
setText(text);
((StyleableProperty<Node>)(WritableValue<Node>)graphicProperty()).applyStyle(null, graphic);
}
public final StringProperty textProperty() {
if (text == null) {
text = new SimpleStringProperty(this, "text", "");
}
return text;
}
private StringProperty text;
public final void setText(String value) { textProperty().setValue(value); }
public final String getText() { return text == null ? "" : text.getValue(); }
public final ObjectProperty<Pos> alignmentProperty() {
if (alignment == null) {
alignment = new StyleableObjectProperty<Pos>(Pos.CENTER_LEFT) {
@Override public CssMetaData<Labeled,Pos> getCssMetaData() {
return StyleableProperties.ALIGNMENT;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "alignment";
}
};
}
return alignment;
}
private ObjectProperty<Pos> alignment;
public final void setAlignment(Pos value) { alignmentProperty().set(value); }
public final Pos getAlignment() { return alignment == null ? Pos.CENTER_LEFT : alignment.get(); }
public final ObjectProperty<TextAlignment> textAlignmentProperty() {
if (textAlignment == null) {
textAlignment = new StyleableObjectProperty<TextAlignment>(TextAlignment.LEFT) {
@Override
public CssMetaData<Labeled,TextAlignment> getCssMetaData() {
return StyleableProperties.TEXT_ALIGNMENT;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "textAlignment";
}
};
}
return textAlignment;
}
private ObjectProperty<TextAlignment> textAlignment;
public final void setTextAlignment(TextAlignment value) { textAlignmentProperty().setValue(value); }
public final TextAlignment getTextAlignment() { return textAlignment == null ? TextAlignment.LEFT : textAlignment.getValue(); }
public final ObjectProperty<OverrunStyle> textOverrunProperty() {
if (textOverrun == null) {
textOverrun = new StyleableObjectProperty<OverrunStyle>(OverrunStyle.ELLIPSIS) {
@Override
public CssMetaData<Labeled,OverrunStyle> getCssMetaData() {
return StyleableProperties.TEXT_OVERRUN;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "textOverrun";
}
};
}
return textOverrun;
}
private ObjectProperty<OverrunStyle> textOverrun;
public final void setTextOverrun(OverrunStyle value) { textOverrunProperty().setValue(value); }
public final OverrunStyle getTextOverrun() { return textOverrun == null ? OverrunStyle.ELLIPSIS : textOverrun.getValue(); }
public final StringProperty ellipsisStringProperty() {
if (ellipsisString == null) {
ellipsisString = new StyleableStringProperty(DEFAULT_ELLIPSIS_STRING) {
@Override public Object getBean() {
return Labeled.this;
}
@Override public String getName() {
return "ellipsisString";
}
@Override public CssMetaData<Labeled,String> getCssMetaData() {
return StyleableProperties.ELLIPSIS_STRING;
}
};
}
return ellipsisString;
}
private StringProperty ellipsisString;
public final void setEllipsisString(String value) { ellipsisStringProperty().set((value == null) ? "" : value); }
public final String getEllipsisString() { return ellipsisString == null ? DEFAULT_ELLIPSIS_STRING : ellipsisString.get(); }
public final BooleanProperty wrapTextProperty() {
if (wrapText == null) {
wrapText = new StyleableBooleanProperty() {
@Override
public CssMetaData<Labeled,Boolean> getCssMetaData() {
return StyleableProperties.WRAP_TEXT;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "wrapText";
}
};
}
return wrapText;
}
private BooleanProperty wrapText;
public final void setWrapText(boolean value) { wrapTextProperty().setValue(value); }
public final boolean isWrapText() { return wrapText == null ? false : wrapText.getValue(); }
@Override public Orientation getContentBias() {
return isWrapText()? Orientation.HORIZONTAL : null;
}
public final ObjectProperty<Font> fontProperty() {
if (font == null) {
font = new StyleableObjectProperty<Font>(Font.getDefault()) {
private boolean fontSetByCss = false;
@Override
public void applyStyle(StyleOrigin newOrigin, Font value) {
try {
fontSetByCss = true;
super.applyStyle(newOrigin, value);
} catch(Exception e) {
throw e;
} finally {
fontSetByCss = false;
}
}
@Override
public void set(Font value) {
final Font oldValue = get();
if (value != null ? !value.equals(oldValue) : oldValue != null) {
super.set(value);
NodeHelper.recalculateRelativeSizeProperties(Labeled.this, value);
}
}
@Override
protected void invalidated() {
if(fontSetByCss == false) {
NodeHelper.reapplyCSS(Labeled.this);
}
}
@Override
public CssMetaData<Labeled,Font> getCssMetaData() {
return StyleableProperties.FONT;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "font";
}
};
}
return font;
}
private ObjectProperty<Font> font;
public final void setFont(Font value) { fontProperty().setValue(value); }
public final Font getFont() { return font == null ? Font.getDefault() : font.getValue(); }
public final ObjectProperty<Node> graphicProperty() {
if (graphic == null) {
graphic = new StyleableObjectProperty<Node>() {
@Override
public CssMetaData getCssMetaData() {
return StyleableProperties.GRAPHIC;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "graphic";
}
};
}
return graphic;
}
private ObjectProperty<Node> graphic;
public final void setGraphic(Node value) {
graphicProperty().setValue(value);
}
public final Node getGraphic() { return graphic == null ? null : graphic.getValue(); }
private StyleableStringProperty imageUrl = null;
private StyleableStringProperty imageUrlProperty() {
if (imageUrl == null) {
imageUrl = new StyleableStringProperty() {
StyleOrigin origin = StyleOrigin.USER;
@Override
public void applyStyle(StyleOrigin origin, String v) {
this.origin = origin;
if (graphic == null || graphic.isBound() == false) super.applyStyle(origin, v);
this.origin = StyleOrigin.USER;
}
@Override
protected void invalidated() {
final String url = super.get();
if (url == null) {
((StyleableProperty<Node>)(WritableValue<Node>)graphicProperty()).applyStyle(origin, null);
} else {
final Node graphicNode = Labeled.this.getGraphic();
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
return graphic != null ? ((StyleableProperty<Node>)(WritableValue<Node>)graphic).getStyleOrigin() : null;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "imageUrl";
}
@Override
public CssMetaData<Labeled,String> getCssMetaData() {
return StyleableProperties.GRAPHIC;
}
};
}
return imageUrl;
}
public final BooleanProperty underlineProperty() {
if (underline == null) {
underline = new StyleableBooleanProperty(false) {
@Override
public CssMetaData<Labeled, Boolean> getCssMetaData() {
return StyleableProperties.UNDERLINE;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "underline";
}
};
}
return underline;
}
private BooleanProperty underline;
public final void setUnderline(boolean value) { underlineProperty().setValue(value); }
public final boolean isUnderline() { return underline == null ? false : underline.getValue(); }
public final DoubleProperty lineSpacingProperty() {
if (lineSpacing == null) {
lineSpacing = new StyleableDoubleProperty(0) {
@Override
public CssMetaData<Labeled,Number> getCssMetaData() {
return StyleableProperties.LINE_SPACING;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "lineSpacing";
}
};
}
return lineSpacing;
}
private DoubleProperty lineSpacing;
public final void setLineSpacing(double value) { lineSpacingProperty().setValue(value); }
public final double getLineSpacing() { return lineSpacing == null ? 0 : lineSpacing.getValue(); }
public final ObjectProperty<ContentDisplay> contentDisplayProperty() {
if (contentDisplay == null) {
contentDisplay = new StyleableObjectProperty<ContentDisplay>(ContentDisplay.LEFT) {
@Override
public CssMetaData<Labeled,ContentDisplay> getCssMetaData() {
return StyleableProperties.CONTENT_DISPLAY;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "contentDisplay";
}
};
}
return contentDisplay;
}
private ObjectProperty<ContentDisplay> contentDisplay;
public final void setContentDisplay(ContentDisplay value) { contentDisplayProperty().setValue(value); }
public final ContentDisplay getContentDisplay() { return contentDisplay == null ? ContentDisplay.LEFT : contentDisplay.getValue(); }
public final ReadOnlyObjectProperty<Insets> labelPaddingProperty() {
return labelPaddingPropertyImpl();
}
private ObjectProperty<Insets> labelPaddingPropertyImpl() {
if (labelPadding == null) {
labelPadding = new StyleableObjectProperty<Insets>(Insets.EMPTY) {
private Insets lastValidValue = Insets.EMPTY;
@Override
public void invalidated() {
final Insets newValue = get();
if (newValue == null) {
set(lastValidValue);
throw new NullPointerException("cannot set labelPadding to null");
}
lastValidValue = newValue;
requestLayout();
}
@Override
public CssMetaData<Labeled,Insets> getCssMetaData() {
return StyleableProperties.LABEL_PADDING;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "labelPadding";
}
};
}
return labelPadding;
}
private ObjectProperty<Insets> labelPadding;
private void setLabelPadding(Insets value) { labelPaddingPropertyImpl().set(value); }
public final Insets getLabelPadding() { return labelPadding == null ? Insets.EMPTY : labelPadding.get(); }
public final DoubleProperty graphicTextGapProperty() {
if (graphicTextGap == null) {
graphicTextGap = new StyleableDoubleProperty(4) {
@Override
public CssMetaData<Labeled,Number> getCssMetaData() {
return StyleableProperties.GRAPHIC_TEXT_GAP;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "graphicTextGap";
}
};
}
return graphicTextGap;
}
private DoubleProperty graphicTextGap;
public final void setGraphicTextGap(double value) { graphicTextGapProperty().setValue(value); }
public final double getGraphicTextGap() { return graphicTextGap == null ? 4 : graphicTextGap.getValue(); }
private ObjectProperty<Paint> textFill;
public final void setTextFill(Paint value) {
textFillProperty().set(value);
}
public final Paint getTextFill() {
return textFill == null ? Color.BLACK : textFill.get();
}
public final ObjectProperty<Paint> textFillProperty() {
if (textFill == null) {
textFill = new StyleableObjectProperty<Paint>(Color.BLACK) {
@Override
public CssMetaData<Labeled,Paint> getCssMetaData() {
return StyleableProperties.TEXT_FILL;
}
@Override
public Object getBean() {
return Labeled.this;
}
@Override
public String getName() {
return "textFill";
}
};
}
return textFill;
}
private BooleanProperty mnemonicParsing;
public final void setMnemonicParsing(boolean value) {
mnemonicParsingProperty().set(value);
}
public final boolean isMnemonicParsing() {
return mnemonicParsing == null ? false : mnemonicParsing.get();
}
public final BooleanProperty mnemonicParsingProperty() {
if (mnemonicParsing == null) {
mnemonicParsing = new SimpleBooleanProperty(this, "mnemonicParsing");
}
return mnemonicParsing;
}
@Override public String toString() {
StringBuilder builder =
new StringBuilder(super.toString())
.append("'").append(getText()).append("'");
return builder.toString();
}
protected Pos getInitialAlignment() {
return Pos.CENTER_LEFT;
}
private static class StyleableProperties {
private static final FontCssMetaData<Labeled> FONT =
new FontCssMetaData<Labeled>("-fx-font", Font.getDefault()) {
@Override
public boolean isSettable(Labeled n) {
return n.font == null || !n.font.isBound();
}
@Override
public StyleableProperty<Font> getStyleableProperty(Labeled n) {
return (StyleableProperty<Font>)(WritableValue<Font>)n.fontProperty();
}
};
private static final CssMetaData<Labeled,Pos> ALIGNMENT =
new CssMetaData<Labeled,Pos>("-fx-alignment",
new EnumConverter<Pos>(Pos.class), Pos.CENTER_LEFT ) {
@Override
public boolean isSettable(Labeled n) {
return n.alignment == null || !n.alignment.isBound();
}
@Override
public StyleableProperty<Pos> getStyleableProperty(Labeled n) {
return (StyleableProperty<Pos>)(WritableValue<Pos>)n.alignmentProperty();
}
@Override
public Pos getInitialValue(Labeled n) {
return n.getInitialAlignment();
}
};
private static final CssMetaData<Labeled,TextAlignment> TEXT_ALIGNMENT =
new CssMetaData<Labeled,TextAlignment>("-fx-text-alignment",
new EnumConverter<TextAlignment>(TextAlignment.class),
TextAlignment.LEFT) {
@Override
public boolean isSettable(Labeled n) {
return n.textAlignment == null || !n.textAlignment.isBound();
}
@Override
public StyleableProperty<TextAlignment> getStyleableProperty(Labeled n) {
return (StyleableProperty<TextAlignment>)(WritableValue<TextAlignment>)n.textAlignmentProperty();
}
};
private static final CssMetaData<Labeled,Paint> TEXT_FILL =
new CssMetaData<Labeled,Paint>("-fx-text-fill",
PaintConverter.getInstance(), Color.BLACK) {
@Override
public boolean isSettable(Labeled n) {
return n.textFill == null || !n.textFill.isBound();
}
@Override
public StyleableProperty<Paint> getStyleableProperty(Labeled n) {
return (StyleableProperty<Paint>)(WritableValue<Paint>)n.textFillProperty();
}
};
private static final CssMetaData<Labeled,OverrunStyle> TEXT_OVERRUN =
new CssMetaData<Labeled,OverrunStyle>("-fx-text-overrun",
new EnumConverter<OverrunStyle>(OverrunStyle.class),
OverrunStyle.ELLIPSIS) {
@Override
public boolean isSettable(Labeled n) {
return n.textOverrun == null || !n.textOverrun.isBound();
}
@Override
public StyleableProperty<OverrunStyle> getStyleableProperty(Labeled n) {
return (StyleableProperty<OverrunStyle>)(WritableValue<OverrunStyle>)n.textOverrunProperty();
}
};
private static final CssMetaData<Labeled,String> ELLIPSIS_STRING =
new CssMetaData<Labeled,String>("-fx-ellipsis-string",
StringConverter.getInstance(), DEFAULT_ELLIPSIS_STRING) {
@Override public boolean isSettable(Labeled n) {
return n.ellipsisString == null || !n.ellipsisString.isBound();
}
@Override public StyleableProperty<String> getStyleableProperty(Labeled n) {
return (StyleableProperty<String>)(WritableValue<String>)n.ellipsisStringProperty();
}
};
private static final CssMetaData<Labeled,Boolean> WRAP_TEXT =
new CssMetaData<Labeled,Boolean>("-fx-wrap-text",
BooleanConverter.getInstance(), false) {
@Override
public boolean isSettable(Labeled n) {
return n.wrapText == null || !n.wrapText.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(Labeled n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.wrapTextProperty();
}
};
private static final CssMetaData<Labeled,String> GRAPHIC =
new CssMetaData<Labeled,String>("-fx-graphic",
StringConverter.getInstance()) {
@Override
public boolean isSettable(Labeled n) {
return n.graphic == null || !n.graphic.isBound();
}
@Override
public StyleableProperty<String> getStyleableProperty(Labeled n) {
return n.imageUrlProperty();
}
};
private static final CssMetaData<Labeled,Boolean> UNDERLINE =
new CssMetaData<Labeled,Boolean>("-fx-underline",
BooleanConverter.getInstance(), Boolean.FALSE) {
@Override
public boolean isSettable(Labeled n) {
return n.underline == null || !n.underline.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(Labeled n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.underlineProperty();
}
};
private static final CssMetaData<Labeled,Number> LINE_SPACING =
new CssMetaData<Labeled,Number>("-fx-line-spacing",
SizeConverter.getInstance(), 0) {
@Override
public boolean isSettable(Labeled n) {
return n.lineSpacing == null || !n.lineSpacing.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Labeled n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.lineSpacingProperty();
}
};
private static final CssMetaData<Labeled,ContentDisplay> CONTENT_DISPLAY =
new CssMetaData<Labeled,ContentDisplay>("-fx-content-display",
new EnumConverter<ContentDisplay>(ContentDisplay.class),
ContentDisplay.LEFT) {
@Override
public boolean isSettable(Labeled n) {
return n.contentDisplay == null || !n.contentDisplay.isBound();
}
@Override
public StyleableProperty<ContentDisplay> getStyleableProperty(Labeled n) {
return (StyleableProperty<ContentDisplay>)(WritableValue<ContentDisplay>)n.contentDisplayProperty();
}
};
private static final CssMetaData<Labeled,Insets> LABEL_PADDING =
new CssMetaData<Labeled,Insets>("-fx-label-padding",
InsetsConverter.getInstance(), Insets.EMPTY) {
@Override
public boolean isSettable(Labeled n) {
return n.labelPadding == null || !n.labelPadding.isBound();
}
@Override
public StyleableProperty<Insets> getStyleableProperty(Labeled n) {
return (StyleableProperty<Insets>)(WritableValue<Insets>)n.labelPaddingPropertyImpl();
}
};
private static final CssMetaData<Labeled,Number> GRAPHIC_TEXT_GAP =
new CssMetaData<Labeled,Number>("-fx-graphic-text-gap",
SizeConverter.getInstance(), 4.0) {
@Override
public boolean isSettable(Labeled n) {
return n.graphicTextGap == null || !n.graphicTextGap.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(Labeled n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.graphicTextGapProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
Collections.addAll(styleables,
FONT,
ALIGNMENT,
TEXT_ALIGNMENT,
TEXT_FILL,
TEXT_OVERRUN,
ELLIPSIS_STRING,
WRAP_TEXT,
GRAPHIC,
UNDERLINE,
LINE_SPACING,
CONTENT_DISPLAY,
LABEL_PADDING,
GRAPHIC_TEXT_GAP
);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
return getClassCssMetaData();
}
}

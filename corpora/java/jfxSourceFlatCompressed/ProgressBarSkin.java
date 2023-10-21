package javafx.scene.control.skin;
import com.sun.javafx.scene.NodeHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.Styleable;
public class ProgressBarSkin extends ProgressIndicatorSkin {
private StackPane bar;
private StackPane track;
private Region clipRegion;
private double barWidth;
public ProgressBarSkin(ProgressBar control) {
super(control);
barWidth = ((int) (control.getWidth() - snappedLeftInset() - snappedRightInset()) * 2 * Math.min(1, Math.max(0, control.getProgress()))) / 2.0F;
registerChangeListener(control.widthProperty(), o -> updateProgress());
initialize();
getSkinnable().requestLayout();
}
private DoubleProperty indeterminateBarLength = null;
private DoubleProperty indeterminateBarLengthProperty() {
if (indeterminateBarLength == null) {
indeterminateBarLength = new StyleableDoubleProperty(60.0) {
@Override
public Object getBean() {
return ProgressBarSkin.this;
}
@Override
public String getName() {
return "indeterminateBarLength";
}
@Override
public CssMetaData<ProgressBar,Number> getCssMetaData() {
return StyleableProperties.INDETERMINATE_BAR_LENGTH;
}
};
}
return indeterminateBarLength;
}
private Double getIndeterminateBarLength() {
return indeterminateBarLength == null ? 60.0 : indeterminateBarLength.get();
}
private BooleanProperty indeterminateBarEscape = null;
private BooleanProperty indeterminateBarEscapeProperty() {
if (indeterminateBarEscape == null) {
indeterminateBarEscape = new StyleableBooleanProperty(true) {
@Override
public Object getBean() {
return ProgressBarSkin.this;
}
@Override
public String getName() {
return "indeterminateBarEscape";
}
@Override
public CssMetaData<ProgressBar,Boolean> getCssMetaData() {
return StyleableProperties.INDETERMINATE_BAR_ESCAPE;
}
};
}
return indeterminateBarEscape;
}
private Boolean getIndeterminateBarEscape() {
return indeterminateBarEscape == null ? true : indeterminateBarEscape.get();
}
private BooleanProperty indeterminateBarFlip = null;
private BooleanProperty indeterminateBarFlipProperty() {
if (indeterminateBarFlip == null) {
indeterminateBarFlip = new StyleableBooleanProperty(true) {
@Override
public Object getBean() {
return ProgressBarSkin.this;
}
@Override
public String getName() {
return "indeterminateBarFlip";
}
@Override
public CssMetaData<ProgressBar,Boolean> getCssMetaData() {
return StyleableProperties.INDETERMINATE_BAR_FLIP;
}
};
}
return indeterminateBarFlip;
}
private Boolean getIndeterminateBarFlip() {
return indeterminateBarFlip == null ? true : indeterminateBarFlip.get();
}
private DoubleProperty indeterminateBarAnimationTime = null;
private DoubleProperty indeterminateBarAnimationTimeProperty() {
if (indeterminateBarAnimationTime == null) {
indeterminateBarAnimationTime = new StyleableDoubleProperty(2.0) {
@Override
public Object getBean() {
return ProgressBarSkin.this;
}
@Override
public String getName() {
return "indeterminateBarAnimationTime";
}
@Override
public CssMetaData<ProgressBar,Number> getCssMetaData() {
return StyleableProperties.INDETERMINATE_BAR_ANIMATION_TIME;
}
};
}
return indeterminateBarAnimationTime;
}
private double getIndeterminateBarAnimationTime() {
return indeterminateBarAnimationTime == null ? 2.0 : indeterminateBarAnimationTime.get();
}
@Override public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
return Node.BASELINE_OFFSET_SAME_AS_HEIGHT;
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return Math.max(100, leftInset + bar.prefWidth(getSkinnable().getWidth()) + rightInset);
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return topInset + bar.prefHeight(width) + bottomInset;
}
@Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefWidth(height);
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefHeight(width);
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
final ProgressIndicator control = getSkinnable();
boolean isIndeterminate = control.isIndeterminate();
clipRegion.resizeRelocate(0, 0, w, h);
track.resizeRelocate(x, y, w, h);
bar.resizeRelocate(x, y, isIndeterminate ? getIndeterminateBarLength() : barWidth, h);
track.setVisible(true);
if (isIndeterminate) {
createIndeterminateTimeline();
if (NodeHelper.isTreeShowing(getSkinnable())) {
indeterminateTransition.play();
}
bar.setClip(clipRegion);
} else if (indeterminateTransition != null) {
indeterminateTransition.stop();
indeterminateTransition = null;
bar.setClip(null);
bar.setScaleX(1);
bar.setTranslateX(0);
clipRegion.translateXProperty().unbind();
}
}
@Override void initialize() {
track = new StackPane();
track.getStyleClass().setAll("track");
bar = new StackPane();
bar.getStyleClass().setAll("bar");
getChildren().setAll(track, bar);
clipRegion = new Region();
bar.backgroundProperty().addListener((observable, oldValue, newValue) -> {
if (newValue != null && !newValue.getFills().isEmpty()) {
final BackgroundFill[] fills = new BackgroundFill[newValue.getFills().size()];
for (int i = 0; i < newValue.getFills().size(); i++) {
BackgroundFill bf = newValue.getFills().get(i);
fills[i] = new BackgroundFill(Color.BLACK,bf.getRadii(),bf.getInsets());
}
clipRegion.setBackground(new Background(fills));
}
});
}
@Override void createIndeterminateTimeline() {
if (indeterminateTransition != null) indeterminateTransition.stop();
ProgressIndicator control = getSkinnable();
final double w = control.getWidth() - (snappedLeftInset() + snappedRightInset());
final double startX = getIndeterminateBarEscape() ? -getIndeterminateBarLength() : 0;
final double endX = getIndeterminateBarEscape() ? w : w - getIndeterminateBarLength();
indeterminateTransition = new IndeterminateTransition(startX, endX, this);
indeterminateTransition.setCycleCount(Timeline.INDEFINITE);
clipRegion.translateXProperty().bind(new When(bar.scaleXProperty().isEqualTo(-1.0, 1e-100)).
then(bar.translateXProperty().subtract(w).add(indeterminateBarLengthProperty())).
otherwise(bar.translateXProperty().negate()));
}
boolean wasIndeterminate = false;
@Override void updateProgress() {
ProgressIndicator control = getSkinnable();
final boolean isIndeterminate = control.isIndeterminate();
if (!(isIndeterminate && wasIndeterminate)) {
barWidth = ((int) (control.getWidth() - snappedLeftInset() - snappedRightInset()) * 2 * Math.min(1, Math.max(0, control.getProgress()))) / 2.0F;
getSkinnable().requestLayout();
}
wasIndeterminate = isIndeterminate;
}
private static class StyleableProperties {
private static final CssMetaData<ProgressBar, Number> INDETERMINATE_BAR_LENGTH =
new CssMetaData<ProgressBar, Number>("-fx-indeterminate-bar-length",
SizeConverter.getInstance(), 60.0) {
@Override
public boolean isSettable(ProgressBar n) {
final ProgressBarSkin skin = (ProgressBarSkin) n.getSkin();
return skin.indeterminateBarLength == null ||
!skin.indeterminateBarLength.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(ProgressBar n) {
final ProgressBarSkin skin = (ProgressBarSkin) n.getSkin();
return (StyleableProperty<Number>) (WritableValue<Number>) skin.indeterminateBarLengthProperty();
}
};
private static final CssMetaData<ProgressBar, Boolean> INDETERMINATE_BAR_ESCAPE =
new CssMetaData<ProgressBar, Boolean>("-fx-indeterminate-bar-escape",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(ProgressBar n) {
final ProgressBarSkin skin = (ProgressBarSkin) n.getSkin();
return skin.indeterminateBarEscape == null ||
!skin.indeterminateBarEscape.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(ProgressBar n) {
final ProgressBarSkin skin = (ProgressBarSkin) n.getSkin();
return (StyleableProperty<Boolean>) (WritableValue<Boolean>) skin.indeterminateBarEscapeProperty();
}
};
private static final CssMetaData<ProgressBar, Boolean> INDETERMINATE_BAR_FLIP =
new CssMetaData<ProgressBar, Boolean>("-fx-indeterminate-bar-flip",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(ProgressBar n) {
final ProgressBarSkin skin = (ProgressBarSkin) n.getSkin();
return skin.indeterminateBarFlip == null ||
!skin.indeterminateBarFlip.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(ProgressBar n) {
final ProgressBarSkin skin = (ProgressBarSkin) n.getSkin();
return (StyleableProperty<Boolean>) (WritableValue<Boolean>) skin.indeterminateBarFlipProperty();
}
};
private static final CssMetaData<ProgressBar, Number> INDETERMINATE_BAR_ANIMATION_TIME =
new CssMetaData<ProgressBar, Number>("-fx-indeterminate-bar-animation-time",
SizeConverter.getInstance(), 2.0) {
@Override
public boolean isSettable(ProgressBar n) {
final ProgressBarSkin skin = (ProgressBarSkin) n.getSkin();
return skin.indeterminateBarAnimationTime == null ||
!skin.indeterminateBarAnimationTime.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(ProgressBar n) {
final ProgressBarSkin skin = (ProgressBarSkin) n.getSkin();
return (StyleableProperty<Number>) (WritableValue<Number>) skin.indeterminateBarAnimationTimeProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(SkinBase.getClassCssMetaData());
styleables.add(INDETERMINATE_BAR_LENGTH);
styleables.add(INDETERMINATE_BAR_ESCAPE);
styleables.add(INDETERMINATE_BAR_FLIP);
styleables.add(INDETERMINATE_BAR_ANIMATION_TIME);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
private static class IndeterminateTransition extends Transition {
private final WeakReference<ProgressBarSkin> skin;
private final double startX;
private final double endX;
private final boolean flip;
public IndeterminateTransition(double startX, double endX, ProgressBarSkin progressBarSkin) {
this.startX = startX;
this.endX = endX;
this.skin = new WeakReference<>(progressBarSkin);
this.flip = progressBarSkin.getIndeterminateBarFlip();
progressBarSkin.getIndeterminateBarEscape();
setCycleDuration(Duration.seconds(progressBarSkin.getIndeterminateBarAnimationTime() * (flip ? 2 : 1)));
}
@Override
protected void interpolate(double frac) {
ProgressBarSkin s = skin.get();
if (s == null) {
stop();
} else {
if (frac <= 0.5 || !flip) {
s.bar.setScaleX(-1);
s.bar.setTranslateX(startX + (flip ? 2 : 1) * frac * (endX - startX));
} else {
s.bar.setScaleX(1);
s.bar.setTranslateX(startX + 2 * (1 - frac) * (endX - startX));
}
}
}
}
}

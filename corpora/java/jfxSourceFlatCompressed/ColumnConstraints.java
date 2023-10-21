package javafx.scene.layout;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.HPos;
public class ColumnConstraints extends ConstraintsBase {
public ColumnConstraints() {
super();
}
public ColumnConstraints(double width) {
this();
setMinWidth(USE_PREF_SIZE);
setPrefWidth(width);
setMaxWidth(USE_PREF_SIZE);
}
public ColumnConstraints(double minWidth, double prefWidth, double maxWidth) {
this();
setMinWidth(minWidth);
setPrefWidth(prefWidth);
setMaxWidth(maxWidth);
}
public ColumnConstraints(double minWidth, double prefWidth, double maxWidth, Priority hgrow, HPos halignment, boolean fillWidth) {
this(minWidth, prefWidth, maxWidth);
setHgrow(hgrow);
setHalignment(halignment);
setFillWidth(fillWidth);
}
private DoubleProperty minWidth;
public final void setMinWidth(double value) {
minWidthProperty().set(value);
}
public final double getMinWidth() {
return minWidth == null ? USE_COMPUTED_SIZE : minWidth.get();
}
public final DoubleProperty minWidthProperty() {
if (minWidth == null) {
minWidth = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return ColumnConstraints.this;
}
@Override
public String getName() {
return "minWidth";
}
};
}
return minWidth;
}
private DoubleProperty prefWidth;
public final void setPrefWidth(double value) {
prefWidthProperty().set(value);
}
public final double getPrefWidth() {
return prefWidth == null ? USE_COMPUTED_SIZE : prefWidth.get();
}
public final DoubleProperty prefWidthProperty() {
if (prefWidth == null) {
prefWidth = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return ColumnConstraints.this;
}
@Override
public String getName() {
return "prefWidth";
}
};
}
return prefWidth;
}
private DoubleProperty maxWidth;
public final void setMaxWidth(double value) {
maxWidthProperty().set(value);
}
public final double getMaxWidth() {
return maxWidth == null ? USE_COMPUTED_SIZE : maxWidth.get();
}
public final DoubleProperty maxWidthProperty() {
if (maxWidth == null) {
maxWidth = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return ColumnConstraints.this;
}
@Override
public String getName() {
return "maxWidth";
}
};
}
return maxWidth;
}
private DoubleProperty percentWidth;
public final void setPercentWidth(double value) {
percentWidthProperty().set(value);
}
public final double getPercentWidth() {
return percentWidth == null ? -1 : percentWidth.get();
}
public final DoubleProperty percentWidthProperty() {
if (percentWidth == null) {
percentWidth = new DoublePropertyBase(-1) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return ColumnConstraints.this;
}
@Override
public String getName() {
return "percentWidth";
}
};
}
return percentWidth;
}
private ObjectProperty<Priority> hgrow;
public final void setHgrow(Priority value) {
hgrowProperty().set(value);
}
public final Priority getHgrow() {
return hgrow == null ? null : hgrow.get();
}
public final ObjectProperty<Priority> hgrowProperty() {
if (hgrow == null) {
hgrow = new ObjectPropertyBase<Priority>() {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return ColumnConstraints.this;
}
@Override
public String getName() {
return "hgrow";
}
};
}
return hgrow;
}
private ObjectProperty<HPos> halignment;
public final void setHalignment(HPos value) {
halignmentProperty().set(value);
}
public final HPos getHalignment() {
return halignment == null ? null : halignment.get();
}
public final ObjectProperty<HPos> halignmentProperty() {
if (halignment == null) {
halignment = new ObjectPropertyBase<HPos>() {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return ColumnConstraints.this;
}
@Override
public String getName() {
return "halignment";
}
};
}
return halignment;
}
private BooleanProperty fillWidth;
public final void setFillWidth(boolean value) {
fillWidthProperty().set(value);
}
public final boolean isFillWidth() {
return fillWidth == null ? true : fillWidth.get();
}
public final BooleanProperty fillWidthProperty() {
if (fillWidth == null) {
fillWidth = new BooleanPropertyBase(true) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return ColumnConstraints.this;
}
@Override
public String getName() {
return "fillWidth";
}
};
}
return fillWidth;
}
@Override public String toString() {
return "ColumnConstraints percentWidth="+getPercentWidth()+
" minWidth="+getMinWidth()+
" prefWidth="+getPrefWidth()+
" maxWidth="+getMaxWidth()+
" hgrow="+getHgrow()+
" fillWidth="+isFillWidth()+
" halignment="+getHalignment();
}
}

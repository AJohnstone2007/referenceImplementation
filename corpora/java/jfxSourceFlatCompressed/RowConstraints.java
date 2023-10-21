package javafx.scene.layout;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.VPos;
public class RowConstraints extends ConstraintsBase {
public RowConstraints() {
super();
}
public RowConstraints(double height) {
this();
setMinHeight(USE_PREF_SIZE);
setPrefHeight(height);
setMaxHeight(USE_PREF_SIZE);
}
public RowConstraints(double minHeight, double prefHeight, double maxHeight) {
this();
setMinHeight(minHeight);
setPrefHeight(prefHeight);
setMaxHeight(maxHeight);
}
public RowConstraints(double minHeight, double prefHeight, double maxHeight, Priority vgrow, VPos valignment, boolean fillHeight) {
this(minHeight, prefHeight, maxHeight);
setVgrow(vgrow);
setValignment(valignment);
setFillHeight(fillHeight);
}
private DoubleProperty minHeight;
public final void setMinHeight(double value) {
minHeightProperty().set(value);
}
public final double getMinHeight() {
return minHeight == null ? USE_COMPUTED_SIZE : minHeight.get();
}
public final DoubleProperty minHeightProperty() {
if (minHeight == null) {
minHeight = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return RowConstraints.this;
}
@Override
public String getName() {
return "minHeight";
}
};
}
return minHeight;
}
private DoubleProperty prefHeight;
public final void setPrefHeight(double value) {
prefHeightProperty().set(value);
}
public final double getPrefHeight() {
return prefHeight == null ? USE_COMPUTED_SIZE : prefHeight.get();
}
public final DoubleProperty prefHeightProperty() {
if (prefHeight == null) {
prefHeight = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return RowConstraints.this;
}
@Override
public String getName() {
return "prefHeight";
}
};
}
return prefHeight;
}
private DoubleProperty maxHeight;
public final void setMaxHeight(double value) {
maxHeightProperty().set(value);
}
public final double getMaxHeight() {
return maxHeight == null ? USE_COMPUTED_SIZE : maxHeight.get();
}
public final DoubleProperty maxHeightProperty() {
if (maxHeight == null) {
maxHeight = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return RowConstraints.this;
}
@Override
public String getName() {
return "maxHeight";
}
};
}
return maxHeight;
}
private DoubleProperty percentHeight;
public final void setPercentHeight(double value) {
percentHeightProperty().set(value);
}
public final double getPercentHeight() {
return percentHeight == null ? -1 : percentHeight.get();
}
public final DoubleProperty percentHeightProperty() {
if (percentHeight == null) {
percentHeight = new DoublePropertyBase(-1) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return RowConstraints.this;
}
@Override
public String getName() {
return "percentHeight";
}
};
}
return percentHeight;
}
private ObjectProperty<Priority> vgrow;
public final void setVgrow(Priority value) {
vgrowProperty().set(value);
}
public final Priority getVgrow() {
return vgrow == null ? null : vgrow.get();
}
public final ObjectProperty<Priority> vgrowProperty() {
if (vgrow == null) {
vgrow = new ObjectPropertyBase<Priority>() {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return RowConstraints.this;
}
@Override
public String getName() {
return "vgrow";
}
};
}
return vgrow;
}
private ObjectProperty<VPos> valignment;
public final void setValignment(VPos value) {
valignmentProperty().set(value);
}
public final VPos getValignment() {
return valignment == null ? null : valignment.get();
}
public final ObjectProperty<VPos> valignmentProperty() {
if (valignment == null) {
valignment = new ObjectPropertyBase<VPos>() {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return RowConstraints.this;
}
@Override
public String getName() {
return "valignment";
}
};
}
return valignment;
}
private BooleanProperty fillHeight;
public final void setFillHeight(boolean value) {
fillHeightProperty().set(value);
}
public final boolean isFillHeight() {
return fillHeight == null ? true : fillHeight.get();
}
public final BooleanProperty fillHeightProperty() {
if (fillHeight == null) {
fillHeight = new BooleanPropertyBase(true) {
@Override
protected void invalidated() {
requestLayout();
}
@Override
public Object getBean() {
return RowConstraints.this;
}
@Override
public String getName() {
return "fillHeight";
}
};
}
return fillHeight;
}
@Override public String toString() {
return "RowConstraints percentHeight="+getPercentHeight()+
" minHeight="+getMinHeight()+
" prefHeight="+getPrefHeight()+
" maxHeight="+getMaxHeight()+
" vgrow="+getVgrow()+
" fillHeight="+isFillHeight()+
" valignment="+getValignment();
}
}

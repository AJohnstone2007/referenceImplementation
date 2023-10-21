package javafx.scene.effect;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import com.sun.javafx.util.Utils;
import com.sun.javafx.tk.Toolkit;
public abstract class Light {
protected Light() {
markDirty();
}
abstract com.sun.scenario.effect.light.Light createPeer();
private com.sun.scenario.effect.light.Light peer;
com.sun.scenario.effect.light.Light getPeer() {
if (peer == null) {
peer = createPeer();
}
return peer;
}
private ObjectProperty<Color> color;
public final void setColor(Color value) {
colorProperty().set(value);
}
public final Color getColor() {
return color == null ? Color.WHITE : color.get();
}
public final ObjectProperty<Color> colorProperty() {
if (color == null) {
color = new ObjectPropertyBase<Color>(Color.WHITE) {
@Override
public void invalidated() {
markDirty();
}
@Override
public Object getBean() {
return Light.this;
}
@Override
public String getName() {
return "color";
}
};
}
return color;
}
void sync() {
if (isEffectDirty()) {
update();
clearDirty();
}
}
private Color getColorInternal() {
Color c = getColor();
return c == null ? Color.WHITE : c;
}
void update() {
getPeer().setColor(Toolkit.getToolkit().toColor4f(getColorInternal()));
}
private BooleanProperty effectDirty;
private void setEffectDirty(boolean value) {
effectDirtyProperty().set(value);
}
final BooleanProperty effectDirtyProperty() {
if (effectDirty == null) {
effectDirty = new SimpleBooleanProperty(this, "effectDirty");
}
return effectDirty;
}
boolean isEffectDirty() {
return effectDirty == null ? false : effectDirty.get();
}
final void markDirty() {
setEffectDirty(true);
}
final void clearDirty() {
setEffectDirty(false);
}
public static class Distant extends Light {
public Distant() {}
public Distant(double azimuth, double elevation, Color color) {
setAzimuth(azimuth);
setElevation(elevation);
setColor(color);
}
@Override
com.sun.scenario.effect.light.DistantLight createPeer() {
return new com.sun.scenario.effect.light.DistantLight();
}
private DoubleProperty azimuth;
public final void setAzimuth(double value) {
azimuthProperty().set(value);
}
public final double getAzimuth() {
return azimuth == null ? 45 : azimuth.get();
}
public final DoubleProperty azimuthProperty() {
if (azimuth == null) {
azimuth = new DoublePropertyBase(45) {
@Override
public void invalidated() {
markDirty();
}
@Override
public Object getBean() {
return Distant.this;
}
@Override
public String getName() {
return "azimuth";
}
};
}
return azimuth;
}
private DoubleProperty elevation;
public final void setElevation(double value) {
elevationProperty().set(value);
}
public final double getElevation() {
return elevation == null ? 45 : elevation.get();
}
public final DoubleProperty elevationProperty() {
if (elevation == null) {
elevation = new DoublePropertyBase(45) {
@Override
public void invalidated() {
markDirty();
}
@Override
public Object getBean() {
return Distant.this;
}
@Override
public String getName() {
return "elevation";
}
};
}
return elevation;
}
@Override
void update() {
super.update();
com.sun.scenario.effect.light.DistantLight peer =
(com.sun.scenario.effect.light.DistantLight) getPeer();
peer.setAzimuth((float) getAzimuth());
peer.setElevation((float) getElevation());
}
}
public static class Point extends Light {
public Point() {}
public Point(double x, double y, double z, Color color) {
setX(x);
setY(y);
setZ(z);
setColor(color);
}
@Override
com.sun.scenario.effect.light.PointLight createPeer() {
return new com.sun.scenario.effect.light.PointLight();
}
private DoubleProperty x;
public final void setX(double value) {
xProperty().set(value);
}
public final double getX() {
return x == null ? 0 : x.get();
}
public final DoubleProperty xProperty() {
if (x == null) {
x = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty();
}
@Override
public Object getBean() {
return Point.this;
}
@Override
public String getName() {
return "x";
}
};
}
return x;
}
private DoubleProperty y;
public final void setY(double value) {
yProperty().set(value);
}
public final double getY() {
return y == null ? 0 : y.get();
}
public final DoubleProperty yProperty() {
if (y == null) {
y = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty();
}
@Override
public Object getBean() {
return Point.this;
}
@Override
public String getName() {
return "y";
}
};
}
return y;
}
private DoubleProperty z;
public final void setZ(double value) {
zProperty().set(value);
}
public final double getZ() {
return z == null ? 0 : z.get();
}
public final DoubleProperty zProperty() {
if (z == null) {
z = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty();
}
@Override
public Object getBean() {
return Point.this;
}
@Override
public String getName() {
return "z";
}
};
}
return z;
}
@Override
void update() {
super.update();
com.sun.scenario.effect.light.PointLight peer =
(com.sun.scenario.effect.light.PointLight) getPeer();
peer.setX((float) getX());
peer.setY((float) getY());
peer.setZ((float) getZ());
}
}
public static class Spot extends Light.Point {
public Spot() {}
public Spot(double x, double y, double z, double specularExponent, Color color) {
setX(x);
setY(y);
setZ(z);
setSpecularExponent(specularExponent);
setColor(color);
}
@Override
com.sun.scenario.effect.light.SpotLight createPeer() {
return new com.sun.scenario.effect.light.SpotLight();
}
private DoubleProperty pointsAtX;
public final void setPointsAtX(double value) {
pointsAtXProperty().set(value);
}
public final double getPointsAtX() {
return pointsAtX == null ? 0 : pointsAtX.get();
}
public final DoubleProperty pointsAtXProperty() {
if (pointsAtX == null) {
pointsAtX = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty();
}
@Override
public Object getBean() {
return Spot.this;
}
@Override
public String getName() {
return "pointsAtX";
}
};
}
return pointsAtX;
}
private DoubleProperty pointsAtY;
public final void setPointsAtY(double value) {
pointsAtYProperty().set(value);
}
public final double getPointsAtY() {
return pointsAtY == null ? 0 : pointsAtY.get();
}
public final DoubleProperty pointsAtYProperty() {
if (pointsAtY == null) {
pointsAtY = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty();
}
@Override
public Object getBean() {
return Spot.this;
}
@Override
public String getName() {
return "pointsAtY";
}
};
}
return pointsAtY;
}
private DoubleProperty pointsAtZ;
public final void setPointsAtZ(double value) {
pointsAtZProperty().set(value);
}
public final double getPointsAtZ() {
return pointsAtZ == null ? 0 : pointsAtZ.get();
}
public final DoubleProperty pointsAtZProperty() {
if (pointsAtZ == null) {
pointsAtZ = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty();
}
@Override
public Object getBean() {
return Spot.this;
}
@Override
public String getName() {
return "pointsAtZ";
}
};
}
return pointsAtZ;
}
private DoubleProperty specularExponent;
public final void setSpecularExponent(double value) {
specularExponentProperty().set(value);
}
public final double getSpecularExponent() {
return specularExponent == null ? 1 : specularExponent.get();
}
public final DoubleProperty specularExponentProperty() {
if (specularExponent == null) {
specularExponent = new DoublePropertyBase(1) {
@Override
public void invalidated() {
markDirty();
}
@Override
public Object getBean() {
return Spot.this;
}
@Override
public String getName() {
return "specularExponent";
}
};
}
return specularExponent;
}
@Override
void update() {
super.update();
com.sun.scenario.effect.light.SpotLight peer =
(com.sun.scenario.effect.light.SpotLight) getPeer();
peer.setPointsAtX((float) getPointsAtX());
peer.setPointsAtY((float) getPointsAtY());
peer.setPointsAtZ((float) getPointsAtZ());
peer.setSpecularExponent((float) Utils.clamp(0, getSpecularExponent(), 4));
}
}
}

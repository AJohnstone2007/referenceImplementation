package javafx.scene.paint;
import com.sun.javafx.beans.event.AbstractNotifyListener;
import com.sun.javafx.scene.paint.MaterialHelper;
import com.sun.javafx.sg.prism.NGPhongMaterial;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.AmbientLight;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
public class PhongMaterial extends Material {
private boolean diffuseColorDirty = true;
private boolean specularColorDirty = true;
private boolean specularPowerDirty = true;
private boolean diffuseMapDirty = true;
private boolean specularMapDirty = true;
private boolean bumpMapDirty = true;
private boolean selfIlluminationMapDirty = true;
public PhongMaterial() {
setDiffuseColor(Color.WHITE);
}
public PhongMaterial(Color diffuseColor) {
setDiffuseColor(diffuseColor);
}
public PhongMaterial(Color diffuseColor, Image diffuseMap,
Image specularMap, Image bumpMap, Image selfIlluminationMap) {
setDiffuseColor(diffuseColor);
setDiffuseMap(diffuseMap);
setSpecularMap(specularMap);
setBumpMap(bumpMap);
setSelfIlluminationMap(selfIlluminationMap);
}
private ObjectProperty<Color> diffuseColor;
public final void setDiffuseColor(Color value) {
diffuseColorProperty().set(value);
}
public final Color getDiffuseColor() {
return diffuseColor == null ? null : diffuseColor.get();
}
public final ObjectProperty<Color> diffuseColorProperty() {
if (diffuseColor == null) {
diffuseColor = new SimpleObjectProperty<Color>(PhongMaterial.this,
"diffuseColor") {
@Override
protected void invalidated() {
diffuseColorDirty = true;
setDirty(true);
}
};
}
return diffuseColor;
}
private ObjectProperty<Color> specularColor;
public final void setSpecularColor(Color value) {
specularColorProperty().set(value);
}
public final Color getSpecularColor() {
return specularColor == null ? null : specularColor.get();
}
public final ObjectProperty<Color> specularColorProperty() {
if (specularColor == null) {
specularColor = new SimpleObjectProperty<Color>(PhongMaterial.this,
"specularColor") {
@Override
protected void invalidated() {
specularColorDirty = true;
setDirty(true);
}
};
}
return specularColor;
}
private DoubleProperty specularPower;
public final void setSpecularPower(double value) {
specularPowerProperty().set(value);
}
public final double getSpecularPower() {
return specularPower == null ? 32 : specularPower.get();
}
public final DoubleProperty specularPowerProperty() {
if (specularPower == null) {
specularPower = new SimpleDoubleProperty(PhongMaterial.this,
"specularPower", 32.0) {
@Override
public void invalidated() {
specularPowerDirty = true;
setDirty(true);
}
};
}
return specularPower;
}
private final AbstractNotifyListener platformImageChangeListener = new AbstractNotifyListener() {
@Override
public void invalidated(Observable valueModel) {
if (oldDiffuseMap != null
&& valueModel == Toolkit.getImageAccessor().getImageProperty(oldDiffuseMap)) {
diffuseMapDirty = true;
} else if (oldSpecularMap != null
&& valueModel == Toolkit.getImageAccessor().getImageProperty(oldSpecularMap)) {
specularMapDirty = true;
} else if (oldBumpMap != null
&& valueModel == Toolkit.getImageAccessor().getImageProperty(oldBumpMap)) {
bumpMapDirty = true;
} else if (oldSelfIlluminationMap != null
&& valueModel == Toolkit.getImageAccessor().getImageProperty(oldSelfIlluminationMap)) {
selfIlluminationMapDirty = true;
}
setDirty(true);
}
};
private ObjectProperty<Image> diffuseMap;
public final void setDiffuseMap(Image value) {
diffuseMapProperty().set(value);
}
public final Image getDiffuseMap() {
return diffuseMap == null ? null : diffuseMap.get();
}
private Image oldDiffuseMap;
public final ObjectProperty<Image> diffuseMapProperty() {
if (diffuseMap == null) {
diffuseMap = new SimpleObjectProperty<Image>(PhongMaterial.this,
"diffuseMap") {
private boolean needsListeners = false;
@Override
public void invalidated() {
Image _image = get();
if (needsListeners) {
Toolkit.getImageAccessor().getImageProperty(oldDiffuseMap).
removeListener(platformImageChangeListener.getWeakListener());
}
needsListeners = _image != null && (Toolkit.getImageAccessor().isAnimation(_image)
|| _image.getProgress() < 1);
if (needsListeners) {
Toolkit.getImageAccessor().getImageProperty(_image).
addListener(platformImageChangeListener.getWeakListener());
}
oldDiffuseMap = _image;
diffuseMapDirty = true;
setDirty(true);
}
};
}
return diffuseMap;
}
private ObjectProperty<Image> specularMap;
public final void setSpecularMap(Image value) {
specularMapProperty().set(value);
}
public final Image getSpecularMap() {
return specularMap == null ? null : specularMap.get();
}
private Image oldSpecularMap;
public final ObjectProperty<Image> specularMapProperty() {
if (specularMap == null) {
specularMap = new SimpleObjectProperty<Image>(PhongMaterial.this,
"specularMap") {
private boolean needsListeners = false;
@Override
public void invalidated() {
Image _image = get();
if (needsListeners) {
Toolkit.getImageAccessor().getImageProperty(oldSpecularMap).
removeListener(platformImageChangeListener.getWeakListener());
}
needsListeners = _image != null && (Toolkit.getImageAccessor().isAnimation(_image)
|| _image.getProgress() < 1);
if (needsListeners) {
Toolkit.getImageAccessor().getImageProperty(_image).
addListener(platformImageChangeListener.getWeakListener());
}
oldSpecularMap = _image;
specularMapDirty = true;
setDirty(true);
}
};
}
return specularMap;
}
private ObjectProperty<Image> bumpMap;
public final void setBumpMap(Image value) {
bumpMapProperty().set(value);
}
public final Image getBumpMap() {
return bumpMap == null ? null : bumpMap.get();
}
private Image oldBumpMap;
public final ObjectProperty<Image> bumpMapProperty() {
if (bumpMap == null) {
bumpMap = new SimpleObjectProperty<Image>(PhongMaterial.this,
"bumpMap") {
private boolean needsListeners = false;
@Override
public void invalidated() {
Image _image = get();
if (needsListeners) {
Toolkit.getImageAccessor().getImageProperty(oldBumpMap).
removeListener(platformImageChangeListener.getWeakListener());
}
needsListeners = _image != null && (Toolkit.getImageAccessor().isAnimation(_image)
|| _image.getProgress() < 1);
if (needsListeners) {
Toolkit.getImageAccessor().getImageProperty(_image).
addListener(platformImageChangeListener.getWeakListener());
}
oldBumpMap = _image;
bumpMapDirty = true;
setDirty(true);
}
};
}
return bumpMap;
}
private ObjectProperty<Image> selfIlluminationMap;
public final void setSelfIlluminationMap(Image value) {
selfIlluminationMapProperty().set(value);
}
public final Image getSelfIlluminationMap() {
return selfIlluminationMap == null ? null : selfIlluminationMap.get();
}
private Image oldSelfIlluminationMap;
public final ObjectProperty<Image> selfIlluminationMapProperty() {
if (selfIlluminationMap == null) {
selfIlluminationMap = new SimpleObjectProperty<Image>(PhongMaterial.this,
"selfIlluminationMap") {
private boolean needsListeners = false;
@Override
public void invalidated() {
Image _image = get();
if (needsListeners) {
Toolkit.getImageAccessor().getImageProperty(oldSelfIlluminationMap).
removeListener(platformImageChangeListener.getWeakListener());
}
needsListeners = _image != null && (Toolkit.getImageAccessor().isAnimation(_image)
|| _image.getProgress() < 1);
if (needsListeners) {
Toolkit.getImageAccessor().getImageProperty(_image).
addListener(platformImageChangeListener.getWeakListener());
}
oldSelfIlluminationMap = _image;
selfIlluminationMapDirty = true;
setDirty(true);
}
};
}
return selfIlluminationMap;
}
@Override
void setDirty(boolean value) {
super.setDirty(value);
if (!value) {
diffuseColorDirty = false;
specularColorDirty = false;
specularPowerDirty = false;
diffuseMapDirty = false;
specularMapDirty = false;
bumpMapDirty = false;
selfIlluminationMapDirty = false;
}
}
private NGPhongMaterial peer;
@Override
NGPhongMaterial getNGMaterial() {
if (peer == null) {
peer = new NGPhongMaterial();
}
return peer;
}
@Override
void updatePG(){
if (!isDirty()) {
return;
}
final NGPhongMaterial pMaterial = MaterialHelper.getNGMaterial(this);
if (diffuseColorDirty) {
pMaterial.setDiffuseColor(getDiffuseColor() == null ? null
: Toolkit.getPaintAccessor().getPlatformPaint(getDiffuseColor()));
}
if (specularColorDirty) {
pMaterial.setSpecularColor(getSpecularColor() == null ? null
: Toolkit.getPaintAccessor().getPlatformPaint(getSpecularColor()));
}
if (specularPowerDirty) {
pMaterial.setSpecularPower((float)getSpecularPower());
}
if (diffuseMapDirty) {
pMaterial.setDiffuseMap(getDiffuseMap()
== null ? null : Toolkit.getImageAccessor().getPlatformImage(getDiffuseMap()));
}
if (specularMapDirty) {
pMaterial.setSpecularMap(getSpecularMap()
== null ? null : Toolkit.getImageAccessor().getPlatformImage(getSpecularMap()));
}
if (bumpMapDirty) {
pMaterial.setBumpMap(getBumpMap()
== null ? null : Toolkit.getImageAccessor().getPlatformImage(getBumpMap()));
}
if (selfIlluminationMapDirty) {
pMaterial.setSelfIllumMap(getSelfIlluminationMap()
== null ? null : Toolkit.getImageAccessor().getPlatformImage(getSelfIlluminationMap()));
}
setDirty(false);
}
@Override public String toString() {
return "PhongMaterial[" + "diffuseColor=" + getDiffuseColor() +
", specularColor=" + getSpecularColor() +
", specularPower=" + getSpecularPower() +
", diffuseMap=" + getDiffuseMap() +
", specularMap=" + getSpecularMap() +
", bumpMap=" + getBumpMap() +
", selfIlluminationMap=" + getSelfIlluminationMap() + "]";
}
}

package javafx.scene.effect;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import com.sun.javafx.util.Utils;
public class FloatMap {
private com.sun.scenario.effect.FloatMap map;
private float[] buf;
private boolean mapBufferDirty = true;
com.sun.scenario.effect.FloatMap getImpl() {
return map;
}
private void updateBuffer() {
if (getWidth() > 0 && getHeight() > 0) {
int w = Utils.clampMax(getWidth(), 4096);
int h = Utils.clampMax(getHeight(), 4096);
int size = w * h * 4;
buf = new float[size];
mapBufferDirty = true;
}
}
private void update() {
if (mapBufferDirty) {
map = new com.sun.scenario.effect.FloatMap(
Utils.clamp(1, getWidth(), 4096),
Utils.clamp(1, getHeight(), 4096));
mapBufferDirty = false;
}
map.put(buf);
}
void sync() {
if (isEffectDirty()) {
update();
clearDirty();
}
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
private void markDirty() {
setEffectDirty(true);
}
private void clearDirty() {
setEffectDirty(false);
}
public FloatMap() {
updateBuffer();
markDirty();
}
public FloatMap(int width, int height) {
setWidth(width);
setHeight(height);
updateBuffer();
markDirty();
}
private IntegerProperty width;
public final void setWidth(int value) {
widthProperty().set(value);
}
public final int getWidth() {
return width == null ? 1 : width.get();
}
public final IntegerProperty widthProperty() {
if (width == null) {
width = new IntegerPropertyBase(1) {
@Override
public void invalidated() {
updateBuffer();
markDirty();
}
@Override
public Object getBean() {
return FloatMap.this;
}
@Override
public String getName() {
return "width";
}
};
}
return width;
}
private IntegerProperty height;
public final void setHeight(int value) {
heightProperty().set(value);
}
public final int getHeight() {
return height == null ? 1 : height.get();
}
public final IntegerProperty heightProperty() {
if (height == null) {
height = new IntegerPropertyBase(1) {
@Override
public void invalidated() {
updateBuffer();
markDirty();
}
@Override
public Object getBean() {
return FloatMap.this;
}
@Override
public String getName() {
return "height";
}
};
}
return height;
}
public void setSample(int x, int y, int band, float s) {
buf[((x+(y*getWidth()))*4) + band] = s;
markDirty();
}
public void setSamples(int x, int y, float s0)
{
int index = (x+(y*getWidth()))*4;
buf[index + 0] = s0;
markDirty();
}
public void setSamples(int x, int y, float s0, float s1)
{
int index = (x+(y*getWidth()))*4;
buf[index + 0] = s0;
buf[index + 1] = s1;
markDirty();
}
public void setSamples(int x, int y, float s0, float s1, float s2)
{
int index = (x+(y*getWidth()))*4;
buf[index + 0] = s0;
buf[index + 1] = s1;
buf[index + 2] = s2;
markDirty();
}
public void setSamples(int x, int y,
float s0, float s1, float s2, float s3)
{
int index = (x+(y*getWidth()))*4;
buf[index + 0] = s0;
buf[index + 1] = s1;
buf[index + 2] = s2;
buf[index + 3] = s3;
markDirty();
}
FloatMap copy() {
FloatMap dest = new FloatMap(this.getWidth(), this.getHeight());
System.arraycopy(buf, 0, dest.buf, 0, buf.length);
return dest;
}
}

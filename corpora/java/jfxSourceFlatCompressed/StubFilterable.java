package test.com.sun.javafx.pgstub;
import com.sun.scenario.effect.Filterable;
public class StubFilterable implements Filterable {
private final StubPlatformImage image;
private StubFilterable(StubPlatformImage image) {
this.image = image;
}
public static StubFilterable create(StubPlatformImage image) {
return new StubFilterable(image);
}
@Override
public Object getData() {
throw new UnsupportedOperationException();
}
@Override
public int getContentWidth() {
return image.getImageInfo().getWidth();
}
@Override
public int getContentHeight() {
return image.getImageInfo().getHeight();
}
@Override
public int getPhysicalWidth() {
return image.getImageInfo().getWidth();
}
@Override
public int getPhysicalHeight() {
return image.getImageInfo().getHeight();
}
@Override
public float getPixelScale() {
return image.getPixelScale();
}
@Override
public void flush() {
}
@Override
public void lock() {
}
@Override
public void unlock() {
}
@Override
public boolean isLost() {
return false;
}
@Override
public int getMaxContentWidth() {
return image.getImageInfo().getWidth();
}
@Override
public int getMaxContentHeight() {
return image.getImageInfo().getHeight();
}
@Override
public void setContentWidth(int contentW) {
throw new UnsupportedOperationException("Not supported.");
}
@Override
public void setContentHeight(int contentH) {
throw new UnsupportedOperationException("Not supported.");
}
}

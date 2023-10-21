package test.com.sun.javafx.pgstub;
public class StubPlatformImageCursor extends StubPlatformCursor {
private final StubPlatformImage platformImage;
private final float hotspotX;
private final float hotspotY;
public StubPlatformImageCursor(final StubPlatformImage platformImage,
final float hotspotX,
final float hotspotY) {
this.platformImage = platformImage;
this.hotspotX = hotspotX;
this.hotspotY = hotspotY;
}
public StubPlatformImage getPlatformImage() {
return platformImage;
}
public float getHotspotX() {
return hotspotX;
}
public float getHotspotY() {
return hotspotY;
}
}

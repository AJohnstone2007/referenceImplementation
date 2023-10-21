package javafx.scene.input;
import java.security.Permission;
import java.util.Set;
import com.sun.javafx.scene.input.DragboardHelper;
import com.sun.javafx.tk.PermissionHelper;
import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.TKScene;
import javafx.scene.image.Image;
public final class Dragboard extends Clipboard {
private boolean dataAccessRestricted = true;
Dragboard(TKClipboard peer) {
super(peer);
}
@Override
Object getContentImpl(DataFormat dataFormat) {
if (dataAccessRestricted) {
PermissionHelper.checkClipboardPermission();
}
return super.getContentImpl(dataFormat);
}
public final Set<TransferMode> getTransferModes() {
return peer.getTransferModes();
}
TKClipboard getPeer() {
return peer;
}
static Dragboard createDragboard(TKClipboard peer) {
return new Dragboard(peer);
}
public void setDragView(Image image, double offsetX, double offsetY) {
peer.setDragView(image);
peer.setDragViewOffsetX(offsetX);
peer.setDragViewOffsetY(offsetY);
}
public void setDragView(Image image) {
peer.setDragView(image);
}
public void setDragViewOffsetX(double offsetX) {
peer.setDragViewOffsetX(offsetX);
}
public void setDragViewOffsetY(double offsetY) {
peer.setDragViewOffsetY(offsetY);
}
public Image getDragView() {
return peer.getDragView();
}
public double getDragViewOffsetX() {
return peer.getDragViewOffsetX();
}
public double getDragViewOffsetY() {
return peer.getDragViewOffsetY();
}
static {
DragboardHelper.setDragboardAccessor(new DragboardHelper.DragboardAccessor() {
@Override
public void setDataAccessRestriction(Dragboard dragboard, boolean restricted) {
dragboard.dataAccessRestricted = restricted;
}
@Override
public TKClipboard getPeer(Dragboard dragboard) {
return dragboard.getPeer();
}
@Override
public Dragboard createDragboard(TKClipboard peer) {
return Dragboard.createDragboard(peer);
}
});
}
}

package javafx.embed.swt;
import java.nio.ByteBuffer;
import javafx.beans.NamedArg;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;
class CustomTransfer extends ByteArrayTransfer {
private String name, mime;
public CustomTransfer (@NamedArg("name") String name, @NamedArg("mime") String mime) {
this.name = name;
this.mime = mime;
}
public String getName () {
return name;
}
public String getMime () {
return mime;
}
public void javaToNative (Object object, TransferData transferData) {
if (!checkCustom(object) || !isSupportedType(transferData)) {
DND.error(DND.ERROR_INVALID_DATA);
}
byte [] bytes = null;
if (object instanceof ByteBuffer) {
bytes = ((ByteBuffer)object).array();
} else {
if (object instanceof byte []) bytes = (byte []) object;
}
if (bytes == null) DND.error(DND.ERROR_INVALID_DATA);
super.javaToNative(bytes, transferData);
}
public Object nativeToJava(TransferData transferData){
if (isSupportedType(transferData)) {
return super.nativeToJava(transferData);
}
return null;
}
protected String[] getTypeNames(){
return new String [] {name};
}
protected int[] getTypeIds(){
return new int [] {registerType(name)};
}
boolean checkByteArray(Object object) {
return (object != null && object instanceof byte[] && ((byte[])object).length > 0);
}
boolean checkByteBuffer(Object object) {
return (object != null && object instanceof ByteBuffer && ((ByteBuffer)object).limit() > 0);
}
boolean checkCustom(Object object) {
return checkByteArray(object) || checkByteBuffer(object);
}
protected boolean validate(Object object) {
return checkCustom(object);
}
}

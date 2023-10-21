package javafx.scene.web;
import java.io.File;
import java.io.IOException;
public class DirectoryLockShim {
DirectoryLock lock;
DirectoryLockShim(DirectoryLock lock) {
this.lock = lock;
}
public DirectoryLockShim(File directory) throws IOException,
DirectoryLockShim.DirectoryAlreadyInUseException
{
try {
lock = new DirectoryLock(directory);
} catch (DirectoryLock.DirectoryAlreadyInUseException e) {
throw new DirectoryAlreadyInUseException(
e.getMessage(),
e.getCause());
} catch (Exception e) {
throw e;
}
}
public void close() {
lock.close();
}
public static int referenceCount(File directory) throws IOException {
return DirectoryLock.referenceCount(directory);
}
public final class DirectoryAlreadyInUseException extends Exception {
DirectoryAlreadyInUseException(String message, Throwable cause) {
super(message, cause);
}
}
}

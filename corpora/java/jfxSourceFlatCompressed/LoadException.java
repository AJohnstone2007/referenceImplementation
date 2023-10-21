package javafx.fxml;
import java.io.IOException;
public class LoadException extends IOException {
private static final long serialVersionUID = 0;
public LoadException() {
super();
}
public LoadException(String message) {
super(message);
}
public LoadException(Throwable cause) {
super(cause);
}
public LoadException(String message, Throwable cause) {
super(message, cause);
}
}

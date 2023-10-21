package com.sun.javafx.iio;
import java.io.IOException;
public class ImageStorageException extends IOException {
private static final long serialVersionUID = 1L;
public ImageStorageException(String message) {
super(message);
}
public ImageStorageException(String message, Throwable cause) {
super(message);
initCause(cause);
}
}

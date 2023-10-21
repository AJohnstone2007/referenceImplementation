package com.sun.javafx.fxml;
public class PropertyNotFoundException extends RuntimeException {
private static final long serialVersionUID = 0;
public PropertyNotFoundException() {
super();
}
public PropertyNotFoundException(String message) {
super(message);
}
public PropertyNotFoundException(Throwable cause) {
super(cause);
}
public PropertyNotFoundException(String message, Throwable cause) {
super(message, cause);
}
}

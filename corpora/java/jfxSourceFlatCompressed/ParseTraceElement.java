package com.sun.javafx.fxml;
import java.net.URL;
public class ParseTraceElement {
private URL location;
private int lineNumber;
public ParseTraceElement(URL location, int lineNumber) {
this.location = location;
this.lineNumber = lineNumber;
}
public URL getLocation() {
return location;
}
public int getLineNumber() {
return lineNumber;
}
@Override
public String toString() {
return ((location == null) ? "?" : location.getPath()) + ": " + lineNumber;
}
}

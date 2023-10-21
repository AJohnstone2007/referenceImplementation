package com.sun.javafx.fxml.builder.web;
import java.util.AbstractMap;
import java.util.Set;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.util.Builder;
public class JavaFXWebViewBuilder extends AbstractMap<String, Object> implements Builder<WebView> {
private final WebView view = new WebView();
private String location = "";
@Override
public WebView build() {
WebEngine engine = view.getEngine();
if (location != null) {
engine.load(location);
}
return view;
}
@Override
public Object put(String key, Object value) {
if ( value != null) {
String str = value.toString();
if ( "location".equals( key)) {
location = str;
} else if ( "onAlert".equals(key)) {
} else {
}
}
return null;
}
@Override
public Set<Entry<String, Object>> entrySet() {
throw new UnsupportedOperationException("Not yet implemented");
}
}

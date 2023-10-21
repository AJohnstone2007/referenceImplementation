package com.sun.javafx.fxml.builder.web;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.util.Builder;
import javafx.util.Callback;
@Deprecated
public final class WebEngineBuilder
implements Builder<WebEngine> {
public static WebEngineBuilder create() {
return new WebEngineBuilder();
}
public WebEngine build() {
WebEngine engine = new WebEngine();
applyTo(engine);
return engine;
}
public void applyTo(WebEngine engine) {
if (confirmHandlerSet) {
engine.setConfirmHandler(confirmHandler);
}
if (createPopupHandlerSet) {
engine.setCreatePopupHandler(createPopupHandler);
}
if (onAlertSet) {
engine.setOnAlert(onAlert);
}
if (onResizedSet) {
engine.setOnResized(onResized);
}
if (onStatusChangedSet) {
engine.setOnStatusChanged(onStatusChanged);
}
if (onVisibilityChangedSet) {
engine.setOnVisibilityChanged(onVisibilityChanged);
}
if (promptHandlerSet) {
engine.setPromptHandler(promptHandler);
}
if (locationSet) {
engine.load(location);
}
}
public WebEngineBuilder confirmHandler(Callback<String, Boolean> value) {
confirmHandler = value;
confirmHandlerSet = true;
return this;
}
private Callback<String, Boolean> confirmHandler;
private boolean confirmHandlerSet;
public WebEngineBuilder createPopupHandler(Callback<PopupFeatures, WebEngine> value) {
createPopupHandler = value;
createPopupHandlerSet = true;
return this;
}
private Callback<PopupFeatures, WebEngine> createPopupHandler;
private boolean createPopupHandlerSet;
public WebEngineBuilder onAlert(EventHandler<WebEvent<String>> value) {
onAlert = value;
onAlertSet = true;
return this;
}
private EventHandler<WebEvent<String>> onAlert;
private boolean onAlertSet;
public WebEngineBuilder onResized(EventHandler<WebEvent<Rectangle2D>> value) {
onResized = value;
onResizedSet = true;
return this;
}
private EventHandler<WebEvent<Rectangle2D>> onResized;
private boolean onResizedSet;
public WebEngineBuilder onStatusChanged(EventHandler<WebEvent<String>> value) {
onStatusChanged = value;
onStatusChangedSet = true;
return this;
}
private EventHandler<WebEvent<String>> onStatusChanged;
private boolean onStatusChangedSet;
public WebEngineBuilder onVisibilityChanged(EventHandler<WebEvent<Boolean>> value) {
onVisibilityChanged = value;
onVisibilityChangedSet = true;
return this;
}
private EventHandler<WebEvent<Boolean>> onVisibilityChanged;
private boolean onVisibilityChangedSet;
public WebEngineBuilder promptHandler(Callback<PromptData, String> value) {
promptHandler = value;
promptHandlerSet = true;
return this;
}
private Callback<PromptData, String> promptHandler;
private boolean promptHandlerSet;
public WebEngineBuilder location(String value) {
location = value;
locationSet = true;
return this;
}
private String location;
private boolean locationSet;
}

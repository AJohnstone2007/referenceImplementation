package com.sun.javafx.fxml.builder.web;
import javafx.util.Builder;
import javafx.util.Callback;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
@Deprecated
public final class WebViewBuilder
extends ParentBuilder<WebViewBuilder>
implements Builder<WebView> {
public static WebViewBuilder create() {
return new WebViewBuilder();
}
public WebView build() {
WebView x = new WebView();
applyTo(x);
return x;
}
public void applyTo(WebView view) {
super.applyTo(view);
if (fontScaleSet) {
view.setFontScale(fontScale);
}
if (maxHeightSet) {
view.setMaxHeight(maxHeight);
}
if (maxWidthSet) {
view.setMaxWidth(maxWidth);
}
if (minHeightSet) {
view.setMinHeight(minHeight);
}
if (minWidthSet) {
view.setMinWidth(minWidth);
}
if (prefHeightSet) {
view.setPrefHeight(prefHeight);
}
if (prefWidthSet) {
view.setPrefWidth(prefWidth);
}
if (engineBuilder != null) {
engineBuilder.applyTo(view.getEngine());
}
}
public WebViewBuilder fontScale(double value) {
fontScale = value;
fontScaleSet = true;
return this;
}
private double fontScale;
private boolean fontScaleSet;
public WebViewBuilder maxHeight(double value) {
maxHeight = value;
maxHeightSet = true;
return this;
}
private double maxHeight;
private boolean maxHeightSet;
public WebViewBuilder maxWidth(double value) {
maxWidth = value;
maxWidthSet = true;
return this;
}
private double maxWidth;
private boolean maxWidthSet;
public WebViewBuilder minHeight(double value) {
minHeight = value;
minHeightSet = true;
return this;
}
private double minHeight;
private boolean minHeightSet;
public WebViewBuilder minWidth(double value) {
minWidth = value;
minWidthSet = true;
return this;
}
private double minWidth;
private boolean minWidthSet;
public WebViewBuilder prefHeight(double value) {
prefHeight = value;
prefHeightSet = true;
return this;
}
private double prefHeight;
private boolean prefHeightSet;
public WebViewBuilder prefWidth(double value) {
prefWidth = value;
prefWidthSet = true;
return this;
}
private double prefWidth;
private boolean prefWidthSet;
public WebViewBuilder confirmHandler(Callback<String, Boolean> value) {
engineBuilder().confirmHandler(value);
return this;
}
public WebViewBuilder createPopupHandler(Callback<PopupFeatures, WebEngine> value) {
engineBuilder().createPopupHandler(value);
return this;
}
public WebViewBuilder onAlert(EventHandler<WebEvent<String>> value) {
engineBuilder().onAlert(value);
return this;
}
public WebViewBuilder onResized(EventHandler<WebEvent<Rectangle2D>> value) {
engineBuilder().onResized(value);
return this;
}
public WebViewBuilder onStatusChanged(EventHandler<WebEvent<String>> value) {
engineBuilder().onStatusChanged(value);
return this;
}
public WebViewBuilder onVisibilityChanged(EventHandler<WebEvent<Boolean>> value) {
engineBuilder().onVisibilityChanged(value);
return this;
}
public WebViewBuilder promptHandler(Callback<PromptData, String> value) {
engineBuilder().promptHandler(value);
return this;
}
public WebViewBuilder location(String value) {
engineBuilder().location(value);
return this;
}
private WebEngineBuilder engineBuilder;
private WebEngineBuilder engineBuilder() {
if (engineBuilder == null) {
engineBuilder = WebEngineBuilder.create();
}
return engineBuilder;
}
}

package com.sun.javafx.scene.web;
import javafx.util.Callback;
public interface Debugger {
boolean isEnabled();
void setEnabled(boolean enabled);
void sendMessage(String message);
Callback<String,Void> getMessageCallback();
void setMessageCallback(Callback<String,Void> callback);
}

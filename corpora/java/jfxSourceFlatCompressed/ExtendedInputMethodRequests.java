package com.sun.javafx.scene.input;
import javafx.scene.input.InputMethodRequests;
public interface ExtendedInputMethodRequests extends InputMethodRequests {
int getInsertPositionOffset();
String getCommittedText(int begin, int end);
int getCommittedTextLength();
}

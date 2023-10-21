package com.sun.javafx.scene.control.behavior;
import javafx.scene.control.PasswordField;
import javafx.scene.text.HitInfo;
public class PasswordFieldBehavior extends TextFieldBehavior {
public PasswordFieldBehavior(PasswordField passwordField) {
super(passwordField);
}
protected void deletePreviousWord() { }
protected void deleteNextWord() { }
protected void selectPreviousWord() { }
public void selectNextWord() { }
protected void previousWord() { }
protected void nextWord() { }
protected void selectWord() {
selectAll();
}
protected void mouseDoubleClick(HitInfo hit) {
getNode().selectAll();
}
}

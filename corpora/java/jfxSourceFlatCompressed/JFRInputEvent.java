package com.sun.javafx.logging.jfr;
import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;
@Name("javafx.Input")
@Label("JavaFX Input")
@Category("JavaFX")
@Description("JavaFX input event")
@StackTrace(false)
@Enabled(false)
public final class JFRInputEvent extends Event {
@Label("Input Type")
@Description("Input event type")
private String input;
public String getInput() {
return input;
}
public void setInput(String input) {
this.input = input;
}
}

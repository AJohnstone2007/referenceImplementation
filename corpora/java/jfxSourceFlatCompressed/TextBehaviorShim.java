package com.sun.javafx.scene.control.behavior;
import java.text.Bidi;
public class TextBehaviorShim {
public static Bidi getRawBidi(TextInputControlBehavior<?> behavior) {
return behavior.getRawBidi();
}
public static boolean isRTLText(TextInputControlBehavior<?> behavior) {
return behavior.isRTLText();
}
private TextBehaviorShim() {};
}

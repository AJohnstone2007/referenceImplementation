package javafx.css;
import com.sun.javafx.css.PseudoClassState;
public abstract class PseudoClass {
public PseudoClass() {
}
public static PseudoClass getPseudoClass(String pseudoClass) {
return PseudoClassState.getPseudoClass(pseudoClass);
}
abstract public String getPseudoClassName();
}

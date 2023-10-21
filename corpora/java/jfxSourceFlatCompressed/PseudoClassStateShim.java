package com.sun.javafx.css;
import java.util.List;
import java.util.Map;
import javafx.css.PseudoClass;
public class PseudoClassStateShim {
public static final Map<String,Integer> pseudoClassMap = PseudoClassState.pseudoClassMap;
public static final List<PseudoClass> pseudoClasses = PseudoClassState.pseudoClasses;
}

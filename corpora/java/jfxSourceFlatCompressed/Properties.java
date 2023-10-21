package com.sun.javafx.scene.control;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
public class Properties {
public final static boolean IS_TOUCH_SUPPORTED = Platform.isSupported(ConditionalFeature.INPUT_TOUCH);
public static final String BUTTON_DATA_PROPERTY = "javafx.scene.control.ButtonBar.ButtonData";
public static final String BUTTON_SIZE_INDEPENDENCE = "javafx.scene.control.ButtonBar.independentSize";
public static final String COMBO_BOX_STYLE_CLASS = "combo-box-popup";
public static String getColorPickerString(String key) {
return ControlResources.getString("ColorPicker." + key);
}
public static final String REFRESH = "refreshKey";
public static final String RECREATE = "recreateKey";
public final static int DEFAULT_LENGTH = 100;
public final static int DEFAULT_WIDTH = 20;
public static final double DEFAULT_EMBEDDED_SB_BREADTH = 8.0;
public static final String DEFER_TO_PARENT_PREF_WIDTH = "deferToParentPrefWidth";
}

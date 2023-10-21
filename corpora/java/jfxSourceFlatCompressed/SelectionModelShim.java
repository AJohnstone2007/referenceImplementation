package javafx.scene.control;
public class SelectionModelShim {
public static void setSelectedItem(SelectionModel sb, Object value) {
sb.setSelectedItem(value);
}
public static SelectionModel
newInstance_from_class(
Class<? extends SelectionModel>modelClass,
Class<? extends Control>viewClass,
Control view) throws Exception {
return modelClass.getConstructor(viewClass).newInstance(view);
}
}

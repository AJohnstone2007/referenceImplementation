package javafx.scene.control;
public class TabPaneShim {
public static SingleSelectionModel<Tab> getTabPaneSelectionModel(TabPane tp) {
return new TabPane.TabPaneSelectionModel(tp);
}
}

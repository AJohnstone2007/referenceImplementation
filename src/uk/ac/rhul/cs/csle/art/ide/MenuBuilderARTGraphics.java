package uk.ac.rhul.cs.csle.art.ide;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SeparatorMenuItem;

public class MenuBuilderARTGraphics extends MenuBuilder {
  @Override
  public MenuBar buildMenuBar() {
    MenuBar menuBar = new MenuBar();
    Menu menu;

    menu = new Menu("_File");
    addMenuItem(menu, "_Run", "shortcut+R");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_New");
    addMenuItem(menu, "_Open");
    addMenuItem(menu, "_Close");
    addMenuItem(menu, "_Save", "shortcut+S");
    addMenuItem(menu, "Save _As");
    addMenuItem(menu, "Save A_ll");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Export");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "E_xit", "shortcut+Q");

    menuBar.getMenus().add(menu);

    menu = new Menu("_Edit");
    addMenuItem(menu, "_Find", "shortcut+F");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Copy", "shortcut+C");
    addMenuItem(menu, "Cu_t", "shortcut+X");
    addMenuItem(menu, "_Paste", "shortcut+V");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Co_mment", "shortcut+/");
    addMenuItem(menu, "_Reformat", "shortcut+T");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Preferences");
    menuBar.getMenus().add(menu);

    menu = new Menu("_Insert");
    addMenuItem(menu, "_LOM");
    addMenuItem(menu, "B_all");
    addMenuItem(menu, "_Box");
    addMenuItem(menu, "_Cone");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Function");
    addMenuItem(menu, "_Text");
    addMenuItem(menu, "_Import");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_JavaSandbox");
    menuBar.getMenus().add(menu);

    menu = new Menu("_Transform");
    addMenuItem(menu, "_Union");
    addMenuItem(menu, "_Difference");
    addMenuItem(menu, "_Intersection");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Translate");
    addMenuItem(menu, "_Rotate");
    addMenuItem(menu, "_Scale");
    addMenuItem(menu, "_Mirror");
    addMenuItem(menu, "_Affine");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Bloat");
    addMenuItem(menu, "_Hull");
    addMenuItem(menu, "_Fillet");
    addMenuItem(menu, "_Chamfer");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Material");
    menuBar.getMenus().add(menu);

    menu = new Menu("_View");
    addMenuItem(menu, "_Home", "shortcut+H");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Above");
    addMenuItem(menu, "_Under");
    addMenuItem(menu, "_Left");
    addMenuItem(menu, "_Right");
    addMenuItem(menu, "_Front", "shortcut+O");
    addMenuItem(menu, "_Back");
    addMenuItem(menu, "_Turntable");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Zoom In", "shortcut+=");
    addMenuItem(menu, "Zoom Out", "shortcut+-");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Pan Up", "shift+UP");
    addMenuItem(menu, "Pan Down", "shift+DOWN");
    addMenuItem(menu, "Pan Left", "shift+LEFT");
    addMenuItem(menu, "Pan Right", "shift+RIGHT");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Rotate Up", "shortcut+UP");
    addMenuItem(menu, "Rotate Down", "shortcut+DOWN");
    addMenuItem(menu, "Rotate Left", "shortcut+LEFT");
    addMenuItem(menu, "Rotate Right", "shortcut+RIGHT");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Parallel _view");
    addMenuItem(menu, "Perspective _view");
    addMenuItem(menu, "A_xes");
    addMenuItem(menu, "Fa_ces");
    addMenuItem(menu, "_Edges");
    menuBar.getMenus().add(menu);

    menu = new Menu("_Analyse");
    addMenuItem(menu, "_Statistics");
    addMenuItem(menu, "_Validity");
    addMenuItem(menu, "_Repair");
    menuBar.getMenus().add(menu);

    menu = new Menu("_Generate");
    addMenuItem(menu, "_Box");
    addMenuItem(menu, "_Roof");
    addMenuItem(menu, "_Wheel");
    menuBar.getMenus().add(menu);

    menu = new Menu("_Help");
    addMenuItem(menu, "_Contents");
    addMenuItem(menu, "_About");
    menuBar.getMenus().add(menu);

    return menuBar;
  }

}

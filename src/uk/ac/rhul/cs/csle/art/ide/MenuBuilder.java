package uk.ac.rhul.cs.csle.art.ide;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;

public abstract class MenuBuilder {
  public abstract MenuBar buildMenuBar();

  protected void addMenuItem(Menu menu, String label) {
    addMenuItem(menu, label, null);
  }

  public void addMenuItem(Menu menu, String label, String accelerator) {
    MenuItem ret = new MenuItem(label);
    menu.getItems().add(ret);
    ret.setOnAction(e -> menuAction(label));
    if (accelerator != null) ret.setAccelerator(KeyCombination.keyCombination(accelerator));
  }

  public void menuAction(String s) {
    switch (s) {
    case "_Run":
      break;
    case "E_xit":
      Platform.exit();
      break;
    default:
      System.err.println("Action " + s + " not yet implemented\n");
    }
  }

}

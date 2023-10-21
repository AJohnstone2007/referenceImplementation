package javafx.scene.web;
import com.sun.javafx.scene.control.ControlHelper;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.print.PrinterJob;
import javafx.scene.control.Skin;
public class HTMLEditor extends Control {
public HTMLEditor() {
((StyleableProperty) ControlHelper.skinClassNameProperty(this)).applyStyle(
null,
"javafx.scene.web.HTMLEditorSkin"
);
getStyleClass().add("html-editor");
}
@Override protected Skin<?> createDefaultSkin() {
return new HTMLEditorSkin(this);
}
public String getHtmlText() {
return ((HTMLEditorSkin)getSkin()).getHTMLText();
}
public void setHtmlText(String htmlText) {
((HTMLEditorSkin)getSkin()).setHTMLText(htmlText);
}
public void print(PrinterJob job) {
((HTMLEditorSkin)getSkin()).print(job);
}
}

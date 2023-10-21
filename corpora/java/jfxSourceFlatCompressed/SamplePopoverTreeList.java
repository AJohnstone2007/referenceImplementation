package ensemble;
import ensemble.control.Popover;
import ensemble.control.PopoverTreeList;
import ensemble.generated.Samples;
import java.util.Comparator;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
public class SamplePopoverTreeList extends PopoverTreeList implements Popover.Page {
private Popover popover;
private SampleCategory category;
private PageBrowser pageBrowser;
public SamplePopoverTreeList(SampleCategory category, PageBrowser pageBrowser) {
this.category = category;
this.pageBrowser = pageBrowser;
if (category.subCategories!=null) getItems().addAll((Object[])category.subCategories);
if (category.samples!=null) getItems().addAll((Object[])category.samples);
getItems().sort(new Comparator() {
private String getName(Object o) {
if (o instanceof SampleCategory) {
return ((SampleCategory) o).name;
} else if (o instanceof SampleInfo) {
return ((SampleInfo) o).name;
} else {
return "";
}
}
@Override
public int compare(Object o1, Object o2) {
return getName(o1).compareTo(getName(o2));
}
});
}
@Override public ListCell call(ListView p) {
return new SampleItemListCell();
}
@Override protected void itemClicked(Object item) {
if (item instanceof SampleCategory) {
popover.pushPage(new SamplePopoverTreeList((SampleCategory)item, pageBrowser));
} else if (item instanceof SampleInfo) {
popover.hide();
pageBrowser.goToSample((SampleInfo)item);
}
}
@Override public void setPopover(Popover popover) {
this.popover = popover;
}
@Override public Popover getPopover() {
return popover;
}
@Override public Node getPageNode() {
return this;
}
@Override public String getPageTitle() {
return "Samples";
}
@Override public String leftButtonText() {
return category == Samples.ROOT ? null : "< Back";
}
@Override public void handleLeftButton() {
popover.popPage();
}
@Override public String rightButtonText() {
return "Done";
}
@Override public void handleRightButton() {
popover.hide();
}
@Override public void handleShown() { }
@Override public void handleHidden() { }
private class SampleItemListCell extends ListCell implements EventHandler<MouseEvent> {
private ImageView arrow = new ImageView(RIGHT_ARROW);
private Region icon = new Region();
private SampleItemListCell() {
super();
getStyleClass().setAll("sample-tree-list-cell");
setOnMouseClicked(this);
setGraphic(icon);
}
@Override public void handle(MouseEvent t) {
itemClicked(getItem());
}
@Override protected double computePrefWidth(double height) {
return 100;
}
@Override protected double computePrefHeight(double width) {
return 44;
}
@Override protected void layoutChildren() {
if (arrow.getParent() != this) getChildren().add(arrow);
super.layoutChildren();
final int w = (int)getWidth();
final int h = (int)getHeight();
final Bounds arrowBounds = arrow.getLayoutBounds();
arrow.setLayoutX(w - arrowBounds.getWidth() - 12);
arrow.setLayoutY((int)((h - arrowBounds.getHeight())/2d));
}
@Override protected void updateItem(Object item, boolean empty) {
super.updateItem(item,empty);
if (item == null) {
setText(null);
arrow.setVisible(false);
icon.getStyleClass().clear();
} else {
setText(item.toString());
arrow.setVisible(true);
if (item instanceof SampleCategory) {
icon.getStyleClass().setAll("folder-icon");
} else {
icon.getStyleClass().setAll("samples-icon");
}
}
}
}
}
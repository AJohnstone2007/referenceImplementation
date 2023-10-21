package com.sun.javafx.charts;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
public class Legend extends TilePane {
private static final int GAP = 5;
private ListChangeListener<LegendItem> itemsListener = c -> {
List<Label> labels = getItems().stream()
.map(i -> i.label)
.collect(Collectors.toList());
getChildren().setAll(labels);
if(isVisible()) requestLayout();
};
private BooleanProperty vertical = new BooleanPropertyBase(false) {
@Override protected void invalidated() {
setOrientation(get() ? Orientation.VERTICAL : Orientation.HORIZONTAL);
}
@Override
public Object getBean() {
return Legend.this;
}
@Override
public String getName() {
return "vertical";
}
};
public final boolean isVertical() { return vertical.get(); }
public final void setVertical(boolean value) { vertical.set(value); }
public final BooleanProperty verticalProperty() { return vertical; }
private ObjectProperty<ObservableList<LegendItem>> items = new ObjectPropertyBase<ObservableList<LegendItem>>() {
ObservableList<LegendItem> oldItems = null;
@Override protected void invalidated() {
if (oldItems != null) oldItems.removeListener(itemsListener);
getChildren().clear();
ObservableList<LegendItem> newItems = get();
if (newItems != null) {
newItems.addListener(itemsListener);
List<Label> labels = newItems.stream()
.map(i -> i.label)
.collect(Collectors.toList());
getChildren().addAll(labels);
}
oldItems = newItems;
requestLayout();
}
@Override
public Object getBean() {
return Legend.this;
}
@Override
public String getName() {
return "items";
}
};
public final void setItems(ObservableList<LegendItem> value) {itemsProperty().set(value);}
public final ObservableList<LegendItem> getItems() { return items.get();}
public final ObjectProperty<ObservableList<LegendItem>> itemsProperty() {return items;}
public Legend() {
super(GAP, GAP);
setTileAlignment(Pos.CENTER_LEFT);
setItems(FXCollections.<LegendItem>observableArrayList());
getStyleClass().setAll("chart-legend");
}
@Override
protected double computePrefWidth(double forHeight) {
return (getItems().size() > 0) ? super.computePrefWidth(forHeight) : 0;
}
@Override
protected double computePrefHeight(double forWidth) {
return (getItems().size() > 0) ? super.computePrefHeight(forWidth) : 0;
}
public static class LegendItem {
private Label label = new Label();
private StringProperty text = new StringPropertyBase() {
@Override protected void invalidated() {
label.setText(get());
}
@Override
public Object getBean() {
return LegendItem.this;
}
@Override
public String getName() {
return "text";
}
};
public final String getText() { return text.getValue(); }
public final void setText(String value) { text.setValue(value); }
public final StringProperty textProperty() { return text; }
private ObjectProperty<Node> symbol = new ObjectPropertyBase<Node>(new Region()) {
@Override protected void invalidated() {
Node symbol = get();
if(symbol != null) symbol.getStyleClass().setAll("chart-legend-item-symbol");
label.setGraphic(symbol);
}
@Override
public Object getBean() {
return LegendItem.this;
}
@Override
public String getName() {
return "symbol";
}
};
public final Node getSymbol() { return symbol.getValue(); }
public final void setSymbol(Node value) { symbol.setValue(value); }
public final ObjectProperty<Node> symbolProperty() { return symbol; }
public LegendItem(String text) {
setText(text);
label.getStyleClass().add("chart-legend-item");
label.setAlignment(Pos.CENTER_LEFT);
label.setContentDisplay(ContentDisplay.LEFT);
label.setGraphic(getSymbol());
getSymbol().getStyleClass().setAll("chart-legend-item-symbol");
}
public LegendItem(String text, Node symbol) {
this(text);
setSymbol(symbol);
}
}
}

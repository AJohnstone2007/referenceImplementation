package test.javafx.scene.layout;
import javafx.scene.layout.Region;
public class MockRegion extends Region {
private double minWidth = 0;
private double minHeight = 0;
private double prefWidth;
private double prefHeight;
private double maxWidth = 500;
private double maxHeight = 500;
public MockRegion(double prefWidth, double prefHeight) {
this.prefWidth = prefWidth;
this.prefHeight = prefHeight;
}
public MockRegion(double minWidth, double minHeight, double prefWidth, double prefHeight, double maxWidth, double maxHeight) {
this.minWidth = minWidth;
this.minHeight = minHeight;
this.prefWidth = prefWidth;
this.prefHeight = prefHeight;
this.maxWidth = maxWidth;
this.maxHeight = maxHeight;
}
@Override protected double computeMinWidth(double height) {
return minWidth;
}
@Override protected double computeMinHeight(double width) {
return minHeight;
}
@Override protected double computePrefWidth(double height) {
return prefWidth;
}
@Override protected double computePrefHeight(double width) {
return prefHeight;
}
@Override protected double computeMaxWidth(double height) {
return maxWidth;
}
@Override protected double computeMaxHeight(double width) {
return maxHeight;
}
}

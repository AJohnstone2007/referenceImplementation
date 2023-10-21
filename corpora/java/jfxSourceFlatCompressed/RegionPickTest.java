package test.javafx.scene.layout;
import java.util.EnumSet;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class RegionPickTest {
private static final double X = 0;
private static final double Y = 0;
private static final double WIDTH = 100;
private static final double HEIGHT = 100;
private static final double CENTER_X = X + (WIDTH / 2.0);
private static final double CENTER_Y = Y + (HEIGHT / 2.0);
private static final double LEFT_OF = X - 10;
private static final double ABOVE = Y - 10;
private static final double RIGHT_OF = X + WIDTH + 10;
private static final double BELOW = Y + HEIGHT + 10;
private Region region;
@Before public void setup() {
region = new Region();
region.resizeRelocate(X, Y, WIDTH, HEIGHT);
region.setPickOnBounds(false);
}
@Test public void pickingNormalRegion() {
region.setPickOnBounds(true);
assertFalse(region.contains(LEFT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, ABOVE));
assertFalse(region.contains(RIGHT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, BELOW));
assertTrue(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingEmptyRegionDoesNotWork() {
assertFalse(region.contains(LEFT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, ABOVE));
assertFalse(region.contains(RIGHT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, BELOW));
assertFalse(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingRectangularFillWorks() {
region.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
assertFalse(region.contains(LEFT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, ABOVE));
assertFalse(region.contains(RIGHT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, BELOW));
assertTrue(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingRectangularFillWithInsetsWorks() {
region.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, new Insets(10))));
assertFalse(region.contains(X + 9, CENTER_Y));
assertFalse(region.contains(CENTER_X, Y + 9));
assertFalse(region.contains(X + WIDTH - 9, CENTER_Y));
assertFalse(region.contains(CENTER_X, Y + HEIGHT - 9));
assertTrue(region.contains(X + 10, CENTER_Y));
assertTrue(region.contains(CENTER_X, Y + 10));
assertTrue(region.contains(X + WIDTH - 10, CENTER_Y));
assertTrue(region.contains(CENTER_X, Y + HEIGHT - 10));
assertTrue(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingRectangularFillWithUniformRadiusWorks() {
region.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), Insets.EMPTY)));
assertTrue(region.contains(X, Y + 10));
assertTrue(region.contains(X + 10, Y));
assertTrue(region.contains(X + 10 - (10 * Math.cos(45)), Y + 10 - (10 * Math.sin(45))));
assertTrue(region.contains(X + 10 - (9 * Math.cos(45)), Y + 10 - (9 * Math.sin(45))));
assertFalse(region.contains(X + 10 - (11 * Math.cos(45)), Y + 10 - (11 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH, Y + 10));
assertTrue(region.contains(X + WIDTH - 10, Y));
assertTrue(region.contains(X + WIDTH - 10 + (10 * Math.cos(45)), Y + 10 - (10 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 10 + (9 * Math.cos(45)), Y + 10 - (9 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 10 + (11 * Math.cos(45)), Y + 10 - (11 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH, Y + HEIGHT - 10));
assertTrue(region.contains(X + WIDTH - 10, Y + HEIGHT));
assertTrue(region.contains(X + WIDTH - 10 + (10 * Math.cos(45)), Y + HEIGHT - 10 + (10 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 10 + (9 * Math.cos(45)), Y + HEIGHT - 10 + (9 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 10 + (11 * Math.cos(45)), Y + HEIGHT - 10 + (11 * Math.sin(45))));
assertTrue(region.contains(X, Y + HEIGHT - 10));
assertTrue(region.contains(X + 10, Y + HEIGHT));
assertTrue(region.contains(X + 10 - (10 * Math.cos(45)), Y + HEIGHT - 10 + (10 * Math.sin(45))));
assertTrue(region.contains(X + 10 - (9 * Math.cos(45)), Y + HEIGHT - 10 + (9 * Math.sin(45))));
assertFalse(region.contains(X + 10 - (11 * Math.cos(45)), Y + HEIGHT - 10 + (11 * Math.sin(45))));
assertTrue(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingRectangularFillWithUniformRadiusWithInsetsWorks() {
region.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10), new Insets(10))));
assertTrue(region.contains(X + 10, Y + 20));
assertTrue(region.contains(X + 20, Y + 10));
assertTrue(region.contains(X + 20 - (10 * Math.cos(45)), Y + 20 - (10 * Math.sin(45))));
assertTrue(region.contains(X + 20 - (9 * Math.cos(45)), Y + 20 - (9 * Math.sin(45))));
assertFalse(region.contains(X + 20 - (11 * Math.cos(45)), Y + 20 - (11 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 10, Y + 20));
assertTrue(region.contains(X + WIDTH - 20, Y + 10));
assertTrue(region.contains(X + WIDTH - 20 + (10 * Math.cos(45)), Y + 20 - (10 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 20 + (9 * Math.cos(45)), Y + 20 - (9 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 20 + (11 * Math.cos(45)), Y + 20 - (11 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 10, Y + HEIGHT - 20));
assertTrue(region.contains(X + WIDTH - 20, Y + HEIGHT - 10));
assertTrue(region.contains(X + WIDTH - 20 + (10 * Math.cos(45)), Y + HEIGHT - 20 + (10 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 20 + (9 * Math.cos(45)), Y + HEIGHT - 20 + (9 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 20 + (11 * Math.cos(45)), Y + HEIGHT - 20 + (11 * Math.sin(45))));
assertTrue(region.contains(X + 10, Y + HEIGHT - 20));
assertTrue(region.contains(X + 20, Y + HEIGHT - 10));
assertTrue(region.contains(X + 20 - (10 * Math.cos(45)), Y + HEIGHT - 20 + (10 * Math.sin(45))));
assertTrue(region.contains(X + 20 - (9 * Math.cos(45)), Y + HEIGHT - 20 + (9 * Math.sin(45))));
assertFalse(region.contains(X + 20 - (11 * Math.cos(45)), Y + HEIGHT - 20 + (11 * Math.sin(45))));
assertTrue(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingRectangularFillWithUniformVERYLARGERadiusWorks() {
region.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(10000000), Insets.EMPTY)));
assertTrue(region.contains(X, Y + 50));
assertTrue(region.contains(X + 50, Y));
assertTrue(region.contains(X + 50 - (50 * Math.cos(45)), Y + 50 - (50 * Math.sin(45))));
assertTrue(region.contains(X + 50 - (49 * Math.cos(45)), Y + 50 - (49 * Math.sin(45))));
assertFalse(region.contains(X + 50 - (51 * Math.cos(45)), Y + 50 - (51 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH, Y + 50));
assertTrue(region.contains(X + WIDTH - 50, Y));
assertTrue(region.contains(X + WIDTH - 50 + (50 * Math.cos(45)), Y + 50 - (50 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 50 + (49 * Math.cos(45)), Y + 50 - (49 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 50 + (51 * Math.cos(45)), Y + 50 - (51 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH, Y + HEIGHT - 50));
assertTrue(region.contains(X + WIDTH - 50, Y + HEIGHT));
assertTrue(region.contains(X + WIDTH - 50 + (50 * Math.cos(45)), Y + HEIGHT - 50 + (50 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 50 + (49 * Math.cos(45)), Y + HEIGHT - 50 + (49 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 50 + (51 * Math.cos(45)), Y + HEIGHT - 50 + (51 * Math.sin(45))));
assertTrue(region.contains(X, Y + HEIGHT - 50));
assertTrue(region.contains(X + 50, Y + HEIGHT));
assertTrue(region.contains(X + 50 - (50 * Math.cos(45)), Y + HEIGHT - 50 + (50 * Math.sin(45))));
assertTrue(region.contains(X + 50 - (49 * Math.cos(45)), Y + HEIGHT - 50 + (49 * Math.sin(45))));
assertFalse(region.contains(X + 50 - (51 * Math.cos(45)), Y + HEIGHT - 50 + (51 * Math.sin(45))));
assertTrue(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingRectangularFillWithIndependentRadiusWorks() {
region.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(1, 2, 3, 4, false),
Insets.EMPTY)));
assertTrue(region.contains(X, Y + 1));
assertTrue(region.contains(X + 1, Y));
assertTrue(region.contains(X + 1 - (1 * Math.cos(45)), Y + 1 - (1 * Math.sin(45))));
assertTrue(region.contains(X + 1 - (.5 * Math.cos(45)), Y + 1 - (.5 * Math.sin(45))));
assertFalse(region.contains(X + 1 - (2 * Math.cos(45)), Y + 1 - (2 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH, Y + 2));
assertTrue(region.contains(X + WIDTH - 2, Y));
assertTrue(region.contains(X + WIDTH - 2 + (2 * Math.cos(45)), Y + 2 - (2 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 2 + (1 * Math.cos(45)), Y + 2 - (1 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 2 + (3 * Math.cos(45)), Y + 2 - (3 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH, Y + HEIGHT - 3));
assertTrue(region.contains(X + WIDTH - 3, Y + HEIGHT));
assertTrue(region.contains(X + WIDTH - 3 + (3 * Math.cos(45)), Y + HEIGHT - 3 + (3 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 3 + (2 * Math.cos(45)), Y + HEIGHT - 3 + (2 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 3 + (4 * Math.cos(45)), Y + HEIGHT - 3 + (4 * Math.sin(45))));
assertTrue(region.contains(X, Y + HEIGHT - 4));
assertTrue(region.contains(X + 4, Y + HEIGHT));
assertTrue(region.contains(X + 4 - (4 * Math.cos(45)), Y + HEIGHT - 4 + (4 * Math.sin(45))));
assertTrue(region.contains(X + 4 - (3 * Math.cos(45)), Y + HEIGHT - 4 + (3 * Math.sin(45))));
assertFalse(region.contains(X + 4 - (5 * Math.cos(45)), Y + HEIGHT - 4 + (5 * Math.sin(45))));
assertTrue(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingRectangularFillWithIndependentRadiusWorks2() {
region.setBackground(new Background(new BackgroundFill(Color.RED,
new CornerRadii(1, 2, 3, 4, 5, 6, 7, 8, false, false, false, false, false, false, false, false),
Insets.EMPTY)));
assertTrue(region.contains(X, Y + 2));
assertTrue(region.contains(X + 1, Y));
assertTrue(region.contains(X + 1 - (1 * Math.cos(45)), Y + 2 - (2 * Math.sin(45))));
assertTrue(region.contains(X + 1 - (.5 * Math.cos(45)), Y + 2 - (1 * Math.sin(45))));
assertFalse(region.contains(X + 1 - (2 * Math.cos(45)), Y + 2 - (3 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH, Y + 3));
assertTrue(region.contains(X + WIDTH - 4, Y));
assertTrue(region.contains(X + WIDTH - 4 + (4 * Math.cos(45)), Y + 3 - (3 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 4 + (3 * Math.cos(45)), Y + 3 - (2 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 4 + (5 * Math.cos(45)), Y + 3 - (4 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH, Y + HEIGHT - 6));
assertTrue(region.contains(X + WIDTH - 5, Y + HEIGHT));
assertTrue(region.contains(X + WIDTH - 5 + (5 * Math.cos(45)), Y + HEIGHT - 6 + (6 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 5 + (4 * Math.cos(45)), Y + HEIGHT - 6 + (5 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 5 + (6 * Math.cos(45)), Y + HEIGHT - 6 + (7 * Math.sin(45))));
assertTrue(region.contains(X, Y + HEIGHT - 7));
assertTrue(region.contains(X + 8, Y + HEIGHT));
assertTrue(region.contains(X + 8 - (8 * Math.cos(45)), Y + HEIGHT - 7 + (7 * Math.sin(45))));
assertTrue(region.contains(X + 8 - (7 * Math.cos(45)), Y + HEIGHT - 7 + (6 * Math.sin(45))));
assertFalse(region.contains(X + 8 - (9 * Math.cos(45)), Y + HEIGHT - 7 + (8 * Math.sin(45))));
assertTrue(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingRectangularFillWithIndependentRadiusWithInsetsWorks() {
region.setBackground(new Background(new BackgroundFill(Color.RED,
new CornerRadii(1, 2, 3, 4, 5, 6, 7, 8, false, false, false, false, false, false, false, false),
new Insets(4, 3, 2, 1))));
assertTrue(region.contains(X + 1, Y + 2 + 4));
assertTrue(region.contains(X + 1 + 1, Y + 4));
assertTrue(region.contains(X + 1 + 1 - (1 * Math.cos(45)), Y + 2 + 4 - (2 * Math.sin(45))));
assertTrue(region.contains(X + 1 + 1 - (.5 * Math.cos(45)), Y + 2 + 4 - (1 * Math.sin(45))));
assertFalse(region.contains(X + 1 + 1 - (2 * Math.cos(45)), Y + 2 + 4 - (3 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 3, Y + 3 + 4));
assertTrue(region.contains(X + WIDTH - 4 - 3, Y + 4));
assertTrue(region.contains(X + WIDTH - 4 - 3 + (4 * Math.cos(45)), Y + 4 + 3 - (3 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 4 - 3 + (3 * Math.cos(45)), Y + 4 + 3 - (2 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 4 - 3 + (5 * Math.cos(45)), Y + 4 + 3 - (4 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 3, Y + HEIGHT - 2 - 6));
assertTrue(region.contains(X + WIDTH - 3 - 5, Y + HEIGHT - 2));
assertTrue(region.contains(X + WIDTH - 3 - 5 + (5 * Math.cos(45)), Y + HEIGHT - 2 - 6 + (6 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 3 - 5 + (4 * Math.cos(45)), Y + HEIGHT - 2 - 6 + (5 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 3 - 5 + (6 * Math.cos(45)), Y + HEIGHT - 2 - 6 + (7 * Math.sin(45))));
assertTrue(region.contains(X + 1, Y + HEIGHT - 2 - 7));
assertTrue(region.contains(X + 1 + 8, Y + HEIGHT - 2));
assertTrue(region.contains(X + 1 + 8 - (8 * Math.cos(45)), Y + HEIGHT - 2 - 7 + (7 * Math.sin(45))));
assertTrue(region.contains(X + 1 + 8 - (7 * Math.cos(45)), Y + HEIGHT - 2 - 7 + (6 * Math.sin(45))));
assertFalse(region.contains(X + 1 + 8 - (9 * Math.cos(45)), Y + HEIGHT - 2 - 7 + (8 * Math.sin(45))));
assertTrue(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingRectangularBorderWorks() {
region.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
new BorderWidths(1))));
assertFalse(region.contains(LEFT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, ABOVE));
assertFalse(region.contains(RIGHT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, BELOW));
assertFalse(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingRectangularBorderWithThickBorder() {
region.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
new BorderWidths(10))));
assertFalse(region.contains(LEFT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, ABOVE));
assertFalse(region.contains(RIGHT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, BELOW));
assertFalse(region.contains(CENTER_X, CENTER_Y));
assertTrue(region.contains(X, Y));
assertTrue(region.contains(X+5, Y+5));
assertFalse(region.contains(X+10, Y+10));
}
@Test public void pickingRectangularBorderWithIndependentBorderWidths() {
region.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
new BorderWidths(5, 10, 15, 20))));
assertFalse(region.contains(LEFT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, ABOVE));
assertFalse(region.contains(RIGHT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, BELOW));
assertFalse(region.contains(CENTER_X, CENTER_Y));
assertTrue(region.contains(CENTER_X, Y));
assertTrue(region.contains(CENTER_X, Y + 4));
assertFalse(region.contains(CENTER_X, Y + 5));
assertTrue(region.contains(WIDTH, CENTER_Y));
assertTrue(region.contains(WIDTH - 9, CENTER_Y));
assertFalse(region.contains(WIDTH - 10, CENTER_Y));
assertTrue(region.contains(CENTER_X, HEIGHT));
assertTrue(region.contains(CENTER_X, HEIGHT - 14));
assertFalse(region.contains(CENTER_X, HEIGHT - 15));
assertTrue(region.contains(X, CENTER_Y));
assertTrue(region.contains(X + 19, CENTER_Y));
assertFalse(region.contains(X + 20, CENTER_Y));
}
@Test public void pickingRectangularBorderWithIndependentPercentageBorderWidths() {
region.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
new BorderWidths(.05, .10, .15, .20, true, true, true, true))));
assertFalse(region.contains(LEFT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, ABOVE));
assertFalse(region.contains(RIGHT_OF, CENTER_Y));
assertFalse(region.contains(CENTER_X, BELOW));
assertFalse(region.contains(CENTER_X, CENTER_Y));
assertTrue(region.contains(CENTER_X, Y));
assertTrue(region.contains(CENTER_X, Y + 4));
assertFalse(region.contains(CENTER_X, Y + 5));
assertTrue(region.contains(WIDTH, CENTER_Y));
assertTrue(region.contains(WIDTH - 9, CENTER_Y));
assertFalse(region.contains(WIDTH - 10, CENTER_Y));
assertTrue(region.contains(CENTER_X, HEIGHT));
assertTrue(region.contains(CENTER_X, HEIGHT - 14));
assertFalse(region.contains(CENTER_X, HEIGHT - 15));
assertTrue(region.contains(X, CENTER_Y));
assertTrue(region.contains(X + 19, CENTER_Y));
assertFalse(region.contains(X + 20, CENTER_Y));
}
@Test public void pickingRectangularBorderWithIndependentBorderWidthsAndInsets() {
region.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
new BorderWidths(5, 10, 15, 20), new Insets(1, 2, 3, 4))));
assertFalse(region.contains(CENTER_X, Y));
assertTrue(region.contains(CENTER_X, Y+1));
assertTrue(region.contains(CENTER_X, Y+1 + 4));
assertFalse(region.contains(CENTER_X, Y+1 + 5));
assertFalse(region.contains(WIDTH-1, CENTER_Y));
assertTrue(region.contains(WIDTH-2, CENTER_Y));
assertTrue(region.contains(WIDTH-2 - 9, CENTER_Y));
assertFalse(region.contains(WIDTH-2 - 10, CENTER_Y));
assertFalse(region.contains(CENTER_X, HEIGHT-2));
assertTrue(region.contains(CENTER_X, HEIGHT-3));
assertTrue(region.contains(CENTER_X, HEIGHT-3 - 14));
assertFalse(region.contains(CENTER_X, HEIGHT-3 - 15));
assertFalse(region.contains(X+3, CENTER_Y));
assertTrue(region.contains(X+4, CENTER_Y));
assertTrue(region.contains(X+4 + 19, CENTER_Y));
assertFalse(region.contains(X+4 + 20, CENTER_Y));
}
@Test public void pickingRectangularBorderWithIndependentRadiusWithInsetsWorks() {
region.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
new CornerRadii(1, 2, 3, 4, 5, 6, 7, 8, false, false, false, false, false, false, false, false),
new BorderWidths(5, 10, 15, 20), new Insets(4, 3, 2, 1))));
assertTrue(region.contains(X + 1, Y + 2 + 4));
assertTrue(region.contains(X + 1 + 1, Y + 4));
assertTrue(region.contains(X + 1 + 1 - (1 * Math.cos(45)), Y + 2 + 4 - (2 * Math.sin(45))));
assertTrue(region.contains(X + 1 + 1 - (.5 * Math.cos(45)), Y + 2 + 4 - (1 * Math.sin(45))));
assertFalse(region.contains(X + 1 + 1 - (2 * Math.cos(45)), Y + 2 + 4 - (3 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 3, Y + 3 + 4));
assertTrue(region.contains(X + WIDTH - 4 - 3, Y + 4));
assertTrue(region.contains(X + WIDTH - 4 - 3 + (4 * Math.cos(45)), Y + 4 + 3 - (3 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 4 - 3 + (3 * Math.cos(45)), Y + 4 + 3 - (2 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 4 - 3 + (5 * Math.cos(45)), Y + 4 + 3 - (4 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 3, Y + HEIGHT - 2 - 6));
assertTrue(region.contains(X + WIDTH - 3 - 5, Y + HEIGHT - 2));
assertTrue(region.contains(X + WIDTH - 3 - 5 + (5 * Math.cos(45)), Y + HEIGHT - 2 - 6 + (6 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 3 - 5 + (4 * Math.cos(45)), Y + HEIGHT - 2 - 6 + (5 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 3 - 5 + (6 * Math.cos(45)), Y + HEIGHT - 2 - 6 + (7 * Math.sin(45))));
assertTrue(region.contains(X + 1, Y + HEIGHT - 2 - 7));
assertTrue(region.contains(X + 1 + 8, Y + HEIGHT - 2));
assertTrue(region.contains(X + 1 + 8 - (8 * Math.cos(45)), Y + HEIGHT - 2 - 7 + (7 * Math.sin(45))));
assertTrue(region.contains(X + 1 + 8 - (7 * Math.cos(45)), Y + HEIGHT - 2 - 7 + (6 * Math.sin(45))));
assertFalse(region.contains(X + 1 + 8 - (9 * Math.cos(45)), Y + HEIGHT - 2 - 7 + (8 * Math.sin(45))));
assertFalse(region.contains(CENTER_X, CENTER_Y));
}
@Test public void pickingRectangularBorderWithIndependentPercentageRadiusWithInsetsWorks() {
region.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
new CornerRadii(.01, .02, .03, .04, .05, .06, .07, .08, true, true, true, true, true, true, true, true),
new BorderWidths(5, 10, 15, 20), new Insets(4, 3, 2, 1))));
assertTrue(region.contains(X + 1, Y + 2 + 4));
assertTrue(region.contains(X + 1 + 1, Y + 4));
assertTrue(region.contains(X + 1 + 1 - (1 * Math.cos(45)), Y + 2 + 4 - (2 * Math.sin(45))));
assertTrue(region.contains(X + 1 + 1 - (.5 * Math.cos(45)), Y + 2 + 4 - (1 * Math.sin(45))));
assertFalse(region.contains(X + 1 + 1 - (2 * Math.cos(45)), Y + 2 + 4 - (3 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 3, Y + 3 + 4));
assertTrue(region.contains(X + WIDTH - 4 - 3, Y + 4));
assertTrue(region.contains(X + WIDTH - 4 - 3 + (4 * Math.cos(45)), Y + 4 + 3 - (3 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 4 - 3 + (3 * Math.cos(45)), Y + 4 + 3 - (2 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 4 - 3 + (5 * Math.cos(45)), Y + 4 + 3 - (4 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 3, Y + HEIGHT - 2 - 6));
assertTrue(region.contains(X + WIDTH - 3 - 5, Y + HEIGHT - 2));
assertTrue(region.contains(X + WIDTH - 3 - 5 + (5 * Math.cos(45)), Y + HEIGHT - 2 - 6 + (6 * Math.sin(45))));
assertTrue(region.contains(X + WIDTH - 3 - 5 + (4 * Math.cos(45)), Y + HEIGHT - 2 - 6 + (5 * Math.sin(45))));
assertFalse(region.contains(X + WIDTH - 3 - 5 + (6 * Math.cos(45)), Y + HEIGHT - 2 - 6 + (7 * Math.sin(45))));
assertTrue(region.contains(X + 1, Y + HEIGHT - 2 - 7));
assertTrue(region.contains(X + 1 + 8, Y + HEIGHT - 2));
assertTrue(region.contains(X + 1 + 8 - (8 * Math.cos(45)), Y + HEIGHT - 2 - 7 + (7 * Math.sin(45))));
assertTrue(region.contains(X + 1 + 8 - (7 * Math.cos(45)), Y + HEIGHT - 2 - 7 + (6 * Math.sin(45))));
assertFalse(region.contains(X + 1 + 8 - (9 * Math.cos(45)), Y + HEIGHT - 2 - 7 + (8 * Math.sin(45))));
assertFalse(region.contains(CENTER_X, CENTER_Y));
}
private void setupRegionShapeWith(double finalShapeSize, double insets, double centerPos) {
region.setShape(new Circle(centerPos, centerPos, finalShapeSize + insets));
region.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY,
new Insets(insets))));
}
@Test public void pickingSimpleShape() {
region.setPickOnBounds(false);
region.setScaleShape(false);
region.setCenterShape(false);
double variants[][] = new double[][]{new double[]{30, 0, 50}, new double[]{30, 10, 50}};
for (double[] v : variants) {
setupRegionShapeWith(v[0], v[1], v[2]);
assertFalse(region.contains(X + 50, Y + 81));
assertFalse(region.contains(X + 50, Y + 19));
assertFalse(region.contains(X + 81, Y + 50));
assertFalse(region.contains(X + 19, Y + 50));
assertTrue(region.contains(X + 50, Y + 79));
assertTrue(region.contains(X + 50, Y + 21));
assertTrue(region.contains(X + 79, Y + 50));
assertTrue(region.contains(X + 21, Y + 50));
}
}
@Test public void pickingCenteredShape() {
region.setPickOnBounds(false);
region.setScaleShape(false);
region.setCenterShape(true);
double variants[][] = new double[][]{new double[]{30, 0, 50}, new double[]{30, 10, 50},
new double[] {30, 0, 0}, new double[] { 30, 10, 0}};
for (double[] v : variants) {
setupRegionShapeWith(v[0], v[1], v[2]);
assertFalse(region.contains(X + 50, Y + 81));
assertFalse(region.contains(X + 50, Y + 19));
assertFalse(region.contains(X + 81, Y + 50));
assertFalse(region.contains(X + 19, Y + 50));
assertTrue(region.contains(X + 50, Y + 79));
assertTrue(region.contains(X + 50, Y + 21));
assertTrue(region.contains(X + 79, Y + 50));
assertTrue(region.contains(X + 21, Y + 50));
}
}
@Test public void pickingScaledShape() {
region.setPickOnBounds(false);
region.setScaleShape(true);
region.setCenterShape(false);
double variants[][] = new double[][]{new double[]{30, 0, 0}, new double[]{30, 10, 0}};
for (double[] v : variants) {
setupRegionShapeWith(v[0], v[1], v[2]);
double shapeWidth = WIDTH - 2* v[1];
double shapeHeight = HEIGHT - 2 * v[1];
double shapeX = X + v[1];
double shapeY = Y + v[1];
assertFalse(region.contains(shapeX, shapeY + shapeHeight / 2 + 1));
assertFalse(region.contains(shapeX, shapeY - shapeHeight / 2 - 1));
assertFalse(region.contains(shapeX + shapeWidth / 2 + 1, shapeY));
assertFalse(region.contains(shapeX - shapeWidth / 2 - 1, shapeY));
assertTrue(region.contains(shapeX + 1, shapeY + shapeHeight / 2 - 1));
assertTrue(region.contains(shapeX + shapeWidth / 2 - 1, shapeY));
assertFalse(region.contains(shapeX + 1, shapeY - shapeHeight / 2 + 1));
assertFalse(region.contains(shapeX - shapeWidth / 2 + 1, shapeY));
}
}
@Test public void pickingScaledAndCenteredShape() {
region.setPickOnBounds(false);
region.setScaleShape(true);
region.setCenterShape(true);
double variants[][] = new double[][]{new double[]{30, 0, 50}, new double[]{30, 10, 50},
new double[] {30, 0, 0}, new double[] { 30, 10, 0}};
for (double[] v : variants) {
setupRegionShapeWith(v[0], v[1], v[2]);
double shapeWidth = WIDTH - 2* v[1];
double shapeHeight = HEIGHT - 2 * v[1];
double shapeX = X + v[1];
double shapeY = Y + v[1];
assertFalse(region.contains(X + WIDTH / 2, shapeY + shapeHeight + 1));
assertFalse(region.contains(X + WIDTH / 2, shapeY - 1));
assertFalse(region.contains(shapeX + shapeWidth + 1, Y + HEIGHT / 2));
assertFalse(region.contains(shapeX - 1, Y + HEIGHT / 2));
assertTrue(region.contains(X + WIDTH / 2, shapeY + shapeHeight - 1));
assertTrue(region.contains(X + WIDTH / 2, shapeY + 1));
assertTrue(region.contains(shapeX + shapeWidth - 1, Y + HEIGHT / 2));
assertTrue(region.contains(shapeX + 1, Y + HEIGHT / 2));
}
}
}

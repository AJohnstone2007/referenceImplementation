package javafx.css;
import javafx.scene.text.Font;
public class SizeUnitsShim {
public static double points(SizeUnits su,
double value, double multiplier, Font font) {
return su.points(value, multiplier, font);
}
public static double pixels(SizeUnits su,
double value, double multiplier, Font font) {
return su.pixels(value, multiplier, font);
}
}

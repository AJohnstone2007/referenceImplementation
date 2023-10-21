package javafx.css;
import javafx.scene.text.Font;
public class SizeShim {
public static double points(Size su,
Font font) {
return su.points(font);
}
public static double points(Size su,
double multiplier, Font font) {
return su.pixels(multiplier, font);
}
}

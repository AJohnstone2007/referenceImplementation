package javafx.print;
import com.sun.javafx.print.Units;
import static com.sun.javafx.print.Units.*;
public final class Paper {
private String name;
private double width, height;
private Units units;
Paper(String paperName,
double paperWidth, double paperHeight, Units units)
throws IllegalArgumentException {
if (paperWidth <= 0 || paperHeight <= 0) {
throw new IllegalArgumentException("Illegal dimension");
}
if (paperName == null) {
throw new IllegalArgumentException("Null name");
}
name = paperName;
width = paperWidth;
height = paperHeight;
this.units = units;
}
public final String getName() {
return name;
}
private double getSizeInPoints(double dim) {
switch (units) {
case POINT : return (int)(dim+0.5);
case INCH : return (int)((dim * 72) + 0.5);
case MM : return (int)(((dim * 72) / 25.4) + 0.5);
}
return dim;
}
public final double getWidth() {
return getSizeInPoints(width);
}
public final double getHeight() {
return getSizeInPoints(height);
}
@Override
public final int hashCode() {
return (int)width+((int)height<<16)+units.hashCode();
}
@Override
public final boolean equals(Object o) {
return (o != null &&
o instanceof Paper &&
this.name.equals(((Paper)o).name) &&
this.width == (((Paper)o).width) &&
this.height == (((Paper)o).height) &&
this.units == (((Paper)o).units));
}
@Override
public final String toString() {
return "Paper: " + name+" size="+width+"x"+height+" " + units;
}
public static final Paper A0 = new Paper("A0", 841, 1189, MM);
public static final Paper A1 = new Paper("A1", 594, 841, MM);
public static final Paper A2 = new Paper("A2", 420, 594, MM);
public static final Paper A3 = new Paper("A3", 297, 420, MM);
public static final Paper A4 = new Paper("A4", 210, 297, MM);
public static final Paper A5 = new Paper("A5", 148, 210, MM);
public static final Paper A6 = new Paper("A6", 105, 148, MM);
public static final Paper
DESIGNATED_LONG = new Paper("Designated Long", 110, 220, MM);
public static final Paper NA_LETTER = new Paper("Letter", 8.5, 11, INCH);
public static final Paper LEGAL = new Paper("Legal", 8.4, 14, INCH);
public static final Paper TABLOID = new Paper("Tabloid", 11.0, 17.0, INCH);
public static final Paper
EXECUTIVE = new Paper("Executive", 7.25, 10.5, INCH);
public static final Paper NA_8X10 = new Paper("8x10", 8, 10, INCH);
public static final Paper
MONARCH_ENVELOPE = new Paper("Monarch Envelope", 3.87, 7.5, INCH);
public static final Paper
NA_NUMBER_10_ENVELOPE = new Paper("Number 10 Envelope",
4.125, 9.5, INCH);
public static final Paper C = new Paper("C", 17.0, 22.0, INCH);
public static final Paper JIS_B4 = new Paper("B4", 257, 364, MM);
public static final Paper JIS_B5 = new Paper("B5", 182, 257, MM);
public static final Paper JIS_B6 = new Paper("B6", 128, 182, MM);
public static final Paper
JAPANESE_POSTCARD = new Paper("Japanese Postcard", 100, 148, MM);
}

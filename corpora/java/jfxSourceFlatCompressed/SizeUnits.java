package javafx.css;
import javafx.scene.text.Font;
public enum SizeUnits {
PERCENT(false) {
@Override
public String toString() { return "%"; }
@Override
public double points(double value, double multiplier, Font font_not_used) {
return (value/100.0) * multiplier;
}
@Override
public double pixels(double value, double multiplier, Font font_not_used) {
return (value/100.0) * multiplier;
}
},
IN(true) {
@Override
public String toString() { return "in"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return value * POINTS_PER_INCH;
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return value * DOTS_PER_INCH;
}
},
CM(true) {
@Override
public String toString() { return "cm"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return (value / CM_PER_INCH) * POINTS_PER_INCH;
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return (value / CM_PER_INCH) * DOTS_PER_INCH;
}
},
MM(true) {
@Override
public String toString() { return "mm"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return (value / MM_PER_INCH) * POINTS_PER_INCH;
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return (value / MM_PER_INCH) * DOTS_PER_INCH;
}
},
EM(false) {
@Override
public String toString() { return "em"; }
@Override
public double points(double value, double multiplier_not_used, Font font) {
return round(value * pointSize(font));
}
@Override
public double pixels(double value, double multiplier_not_used, Font font) {
return round(value * pixelSize(font));
}
},
EX(false) {
@Override
public String toString() { return "ex"; }
@Override
public double points(double value, double multiplier_not_used, Font font) {
return round(value / 2.0 * pointSize(font));
}
@Override
public double pixels(double value, double multiplier_not_used, Font font) {
return round(value / 2.0 * pixelSize(font));
}
},
PT(true) {
@Override
public String toString() { return "pt"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return value;
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return value * (DOTS_PER_INCH / POINTS_PER_INCH);
}
},
PC(true) {
@Override
public String toString() { return "pc"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return value * POINTS_PER_PICA;
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return (value * POINTS_PER_PICA) * (DOTS_PER_INCH / POINTS_PER_INCH);
}
},
PX(true) {
@Override
public String toString() { return "px"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return value * (POINTS_PER_INCH / DOTS_PER_INCH);
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return value;
}
},
DEG(true) {
@Override
public String toString() { return "deg"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return round(value);
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return round(value);
}
},
GRAD(true) {
@Override
public String toString() { return "grad"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return round(value*9/10);
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return round(value*9/10);
}
},
RAD(true) {
@Override
public String toString() { return "rad"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return round(value*180/Math.PI);
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return round(value*180/Math.PI);
}
},
TURN(true) {
@Override
public String toString() { return "turn"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return round(value*360);
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return round(value*360);
}
},
S(true) {
@Override
public String toString() { return "s"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return value;
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return value;
}
},
MS(true) {
@Override
public String toString() { return "ms"; }
@Override
public double points(double value, double multiplier_not_used, Font font_not_used) {
return value;
}
@Override
public double pixels(double value, double multiplier_not_used, Font font_not_used) {
return value;
}
};
public abstract double points(double value, double multiplier, Font font);
public abstract double pixels(double value, double multiplier, Font font);
private SizeUnits(boolean absolute) {
this.absolute = absolute;
}
private final boolean absolute;
public boolean isAbsolute() {
return absolute;
}
static final private double DOTS_PER_INCH = 96.0;
static final private double POINTS_PER_INCH = 72.0;
static final private double CM_PER_INCH = 2.54;
static final private double MM_PER_INCH = CM_PER_INCH * 10;
static final private double POINTS_PER_PICA = 12.0;
private static double pointSize(Font font) {
return pixelSize(font) * (POINTS_PER_INCH/DOTS_PER_INCH);
}
private static double pixelSize(Font font) {
return (font != null) ? font.getSize() : Font.getDefault().getSize();
}
private static double round(double d) {
if (d == 0) return d;
final double r = (d < 0) ? -0.05 : 0.05;
return ((long)((d + r) * 10)) / 10.0;
}
}

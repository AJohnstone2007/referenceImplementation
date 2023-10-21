package javafx.geometry;
import static javafx.geometry.HPos.LEFT;
import static javafx.geometry.HPos.RIGHT;
import static javafx.geometry.VPos.BASELINE;
import static javafx.geometry.VPos.BOTTOM;
import static javafx.geometry.VPos.TOP;
public enum Pos {
TOP_LEFT(TOP, LEFT),
TOP_CENTER(TOP, HPos.CENTER),
TOP_RIGHT(TOP, RIGHT),
CENTER_LEFT(VPos.CENTER, LEFT),
CENTER(VPos.CENTER, HPos.CENTER),
CENTER_RIGHT(VPos.CENTER, RIGHT),
BOTTOM_LEFT(BOTTOM, LEFT),
BOTTOM_CENTER(BOTTOM, HPos.CENTER),
BOTTOM_RIGHT(BOTTOM, RIGHT),
BASELINE_LEFT(BASELINE, LEFT),
BASELINE_CENTER(BASELINE, HPos.CENTER),
BASELINE_RIGHT(BASELINE, RIGHT);
private final VPos vpos;
private final HPos hpos;
private Pos(VPos vpos, HPos hpos) {
this.vpos = vpos;
this.hpos = hpos;
}
public VPos getVpos() {
return vpos;
}
public HPos getHpos() {
return hpos;
}
}

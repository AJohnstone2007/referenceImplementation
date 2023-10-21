package javafx.print;
import static javafx.print.PageOrientation.*;
public final class PageLayout {
private PageOrientation orient;
private Paper paper;
private double lMargin, rMargin, tMargin, bMargin;
PageLayout(Paper paper, PageOrientation orient) {
this(paper, orient, 56, 56, 56, 56);
}
PageLayout(Paper paper, PageOrientation orient,
double leftMargin, double rightMargin,
double topMargin, double bottomMargin) {
if (paper == null || orient == null ||
leftMargin < 0 || rightMargin < 0 ||
topMargin < 0 || bottomMargin < 0) {
throw new IllegalArgumentException("Illegal parameters");
}
if (orient == PORTRAIT || orient == REVERSE_PORTRAIT) {
if (leftMargin+rightMargin > paper.getWidth() ||
topMargin+bottomMargin > paper.getHeight()) {
throw new IllegalArgumentException("Bad margins");
}
} else if (leftMargin+rightMargin > paper.getHeight() ||
topMargin+bottomMargin > paper.getWidth()) {
throw new IllegalArgumentException("Bad margins");
}
this.paper = paper;
this.orient = orient;
this.lMargin = leftMargin;
this.rMargin = rightMargin;
this.tMargin = topMargin;
this.bMargin = bottomMargin;
}
public PageOrientation getPageOrientation() {
return orient;
}
public Paper getPaper() {
return paper;
}
public double getPrintableWidth() {
double pw = 0;
if (orient == PORTRAIT || orient == REVERSE_PORTRAIT) {
pw = paper.getWidth();
} else {
pw = paper.getHeight();
}
pw -= (lMargin+rMargin);
if (pw < 0) {
pw = 0;
}
return pw;
}
public double getPrintableHeight() {
double ph = 0;
if (orient == PORTRAIT || orient == REVERSE_PORTRAIT) {
ph = paper.getHeight();
} else {
ph = paper.getWidth();
}
ph -= (tMargin+bMargin);
if (ph < 0) {
ph = 0;
}
return ph;
}
public double getLeftMargin() {
return lMargin;
}
public double getRightMargin() {
return rMargin;
}
public double getTopMargin() {
return tMargin;
}
public double getBottomMargin() {
return bMargin;
}
@Override public boolean equals(Object o) {
if (o instanceof PageLayout) {
PageLayout other = (PageLayout)o;
return
paper.equals(other.paper) &&
orient.equals(other.orient) &&
tMargin == other.tMargin &&
bMargin == other.bMargin &&
rMargin == other.rMargin &&
lMargin == other.lMargin;
} else {
return false;
}
}
@Override public int hashCode() {
return paper.hashCode() + orient.hashCode()+
(int)(tMargin+bMargin+lMargin+rMargin);
}
@Override public String toString() {
return
"Paper="+paper+
" Orient="+orient+
" leftMargin="+lMargin+
" rightMargin="+rMargin+
" topMargin="+tMargin+
" bottomMargin="+bMargin;
}
}

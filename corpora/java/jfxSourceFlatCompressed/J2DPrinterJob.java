package com.sun.prism.j2d.print;
import javafx.print.Collation;
import javafx.print.JobSettings;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.PageRange;
import javafx.print.Paper;
import javafx.print.PaperSource;
import javafx.print.PrintColor;
import javafx.print.PrintResolution;
import javafx.print.PrintSides;
import javafx.print.Printer;
import javafx.print.Printer.MarginType;
import javafx.print.PrinterAttributes;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Set;
import com.sun.glass.ui.Application;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.PrinterImpl;
import com.sun.javafx.print.PrinterJobImpl;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.stage.WindowHelper;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.Toolkit;
import com.sun.glass.utils.NativeLibLoader;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.j2d.PrismPrintGraphics;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
public class J2DPrinterJob implements PrinterJobImpl {
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
String libName = "prism_common";
if (PrismSettings.verbose) {
System.out.println("Loading Prism common native library ...");
}
NativeLibLoader.loadLibrary(libName);
if (PrismSettings.verbose) {
System.out.println("\tsucceeded.");
}
return null;
});
}
javafx.print.PrinterJob fxPrinterJob;
java.awt.print.PrinterJob pJob2D;
javafx.print.Printer fxPrinter;
J2DPrinter j2dPrinter;
private JobSettings settings;
private PrintRequestAttributeSet printReqAttrSet;
private volatile Object elo = null;
private static Class onTopClass = null;
@SuppressWarnings("removal")
PrintRequestAttribute getAlwaysOnTop(final long id) {
return AccessController.doPrivileged(
(PrivilegedAction<PrintRequestAttribute>) () -> {
PrintRequestAttribute alwaysOnTop = null;
try {
if (onTopClass == null) {
onTopClass =
Class.forName("javax.print.attribute.standard.DialogOwner");
}
if (id == 0) {
Constructor<PrintRequestAttribute>
cons = onTopClass.getConstructor();
alwaysOnTop = cons.newInstance();
} else {
alwaysOnTop = getAlwaysOnTop(onTopClass, id);
}
} catch (Throwable t) {
}
return alwaysOnTop;
});
}
private static native
PrintRequestAttribute getAlwaysOnTop(Class onTopClass, long id);
public J2DPrinterJob(javafx.print.PrinterJob fxJob) {
fxPrinterJob = fxJob;
fxPrinter = fxPrinterJob.getPrinter();
j2dPrinter = getJ2DPrinter(fxPrinter);
settings = fxPrinterJob.getJobSettings();
pJob2D = java.awt.print.PrinterJob.getPrinterJob();
try {
pJob2D.setPrintService(j2dPrinter.getService());
} catch (PrinterException pe) {
}
printReqAttrSet = new HashPrintRequestAttributeSet();
printReqAttrSet.add(DialogTypeSelection.NATIVE);
j2dPageable = new J2DPageable();
pJob2D.setPageable(j2dPageable);
}
private void setEnabledState(Window owner, boolean state) {
if (owner == null) {
return;
}
final TKStage stage = WindowHelper.getPeer(owner);
if (stage == null) {
return;
}
Application.invokeAndWait(() -> stage.setEnabled(state));
}
public boolean showPrintDialog(Window owner) {
if (jobRunning || jobDone) {
return false;
}
if (GraphicsEnvironment.isHeadless()) {
return true;
}
if (onTopClass != null) {
printReqAttrSet.remove(onTopClass);
}
if (owner != null) {
long id = 0L;
if (PlatformUtil.isWindows()) {
id = WindowHelper.getPeer(owner).getRawHandle();
}
PrintRequestAttribute alwaysOnTop = getAlwaysOnTop(id);
if (alwaysOnTop != null) {
printReqAttrSet.add(alwaysOnTop);
}
}
boolean rv = false;
syncSettingsToAttributes();
try {
setEnabledState(owner, false);
if (!Toolkit.getToolkit().isFxUserThread()) {
rv = pJob2D.printDialog(printReqAttrSet);
} else {
if (!Toolkit.getToolkit().canStartNestedEventLoop()) {
throw new IllegalStateException(
"Printing is not allowed during animation or layout processing");
}
rv = showPrintDialogWithNestedLoop(owner);
}
if (rv) {
updateSettingsFromDialog();
}
} finally {
setEnabledState(owner, true);
}
return rv;
}
private class PrintDialogRunnable implements Runnable {
public void run() {
boolean rv = false;
try {
rv = pJob2D.printDialog(printReqAttrSet);
} catch (Exception e) {
} finally {
Application.invokeLater(new ExitLoopRunnable(this, rv));
}
}
}
private boolean showPrintDialogWithNestedLoop(Window owner) {
PrintDialogRunnable dr = new PrintDialogRunnable();
Thread prtThread = new Thread(dr, "FX Print Dialog Thread");
prtThread.start();
Object rv = Toolkit.getToolkit().enterNestedEventLoop(dr);
boolean rvbool = false;
try {
rvbool = ((Boolean)rv).booleanValue();
} catch (Exception e) {
}
return rvbool;
}
public boolean showPageDialog(Window owner) {
if (jobRunning || jobDone) {
return false;
}
if (GraphicsEnvironment.isHeadless()) {
return true;
}
if (onTopClass != null) {
printReqAttrSet.remove(onTopClass);
}
if (owner != null) {
long id = 0L;
if (PlatformUtil.isWindows()) {
id = WindowHelper.getPeer(owner).getRawHandle();
}
PrintRequestAttribute alwaysOnTop = getAlwaysOnTop(id);
if (alwaysOnTop != null) {
printReqAttrSet.add(alwaysOnTop);
}
}
boolean rv = false;
syncSettingsToAttributes();
try {
setEnabledState(owner, false);
if (!Toolkit.getToolkit().isFxUserThread()) {
PageFormat pf = pJob2D.pageDialog(printReqAttrSet);
rv = pf != null;
} else {
if (!Toolkit.getToolkit().canStartNestedEventLoop()) {
throw new IllegalStateException(
"Printing is not allowed during animation or layout processing");
}
rv = showPageDialogFromNestedLoop(owner);
}
} finally {
setEnabledState(owner, true);
}
if (rv) {
updateSettingsFromDialog();
}
return rv;
}
private class PageDialogRunnable implements Runnable {
public void run() {
PageFormat pf = null;
try {
pf = pJob2D.pageDialog(printReqAttrSet);
} catch (Exception e) {
} finally {
Boolean rv = Boolean.valueOf(pf != null);
Application.invokeLater(new ExitLoopRunnable(this, rv));
}
}
}
private boolean showPageDialogFromNestedLoop(Window owner) {
PageDialogRunnable dr = new PageDialogRunnable();
Thread prtThread = new Thread(dr, "FX Page Setup Dialog Thread");
prtThread.start();
Object rv = Toolkit.getToolkit().enterNestedEventLoop(dr);
boolean rvbool = false;
try {
rvbool = ((Boolean)rv).booleanValue();
} catch (Exception e) {
}
return rvbool;
}
private void updateJobName() {
String name = pJob2D.getJobName();
if (!name.equals(settings.getJobName())) {
settings.setJobName(name);
}
}
private void updateOutputFile() {
Destination dest =
(Destination)printReqAttrSet.get(Destination.class);
if (dest != null) {
settings.setOutputFile(dest.getURI().getPath());
} else {
settings.setOutputFile("");
}
}
private void updateCopies() {
int nCopies = pJob2D.getCopies();
if (settings.getCopies() != nCopies) {
settings.setCopies(nCopies);
}
}
private void updatePageRanges() {
PageRanges ranges = (PageRanges)printReqAttrSet.get(PageRanges.class);
if (ranges != null) {
int[][] members = ranges.getMembers();
if (members.length == 1) {
PageRange range = new PageRange(members[0][0], members[0][1]);
settings.setPageRanges(range);
} else if (members.length > 0) {
try {
ArrayList<PageRange> prList = new ArrayList<PageRange>();
int last = 0;
for (int i=0; i<members.length;i++) {
int s = members[i][0];
int e = members[i][1];
if (s <= last || e < s) {
return;
}
last = e;
prList.add(new PageRange(s, e));
}
settings.setPageRanges(prList.toArray(new PageRange[0]));
} catch (Exception e) {
}
}
}
}
private void updateSides() {
Sides sides = (Sides)printReqAttrSet.get(Sides.class);
if (sides == null) {
sides = (Sides)j2dPrinter.getService().
getDefaultAttributeValue(Sides.class);
}
if (sides == Sides.ONE_SIDED) {
settings.setPrintSides(PrintSides.ONE_SIDED);
} else if (sides == Sides.DUPLEX) {
settings.setPrintSides(PrintSides.DUPLEX);
} else if (sides == Sides.TUMBLE) {
settings.setPrintSides(PrintSides.TUMBLE);
}
}
private void updateCollation() {
SheetCollate collate =
(SheetCollate)printReqAttrSet.get(SheetCollate.class);
if (collate == null) {
collate = j2dPrinter.getDefaultSheetCollate();
}
if (collate == SheetCollate.UNCOLLATED) {
settings.setCollation(Collation.UNCOLLATED);
} else {
settings.setCollation(Collation.COLLATED);
}
}
private void updateColor() {
Chromaticity color =
(Chromaticity)printReqAttrSet.get(Chromaticity.class);
if (color == null) {
color = j2dPrinter.getDefaultChromaticity();
}
if (color == Chromaticity.COLOR) {
settings.setPrintColor(PrintColor.COLOR);
} else {
settings.setPrintColor(PrintColor.MONOCHROME);
}
}
private void updatePrintQuality() {
PrintQuality quality =
(PrintQuality)printReqAttrSet.get(PrintQuality.class);
if (quality == null) {
quality = j2dPrinter.getDefaultPrintQuality();
}
if (quality == PrintQuality.DRAFT) {
settings.
setPrintQuality(javafx.print.PrintQuality.DRAFT);
} else if (quality == PrintQuality.HIGH) {
settings.
setPrintQuality(javafx.print.PrintQuality.HIGH);
} else {
settings.
setPrintQuality(javafx.print.PrintQuality.NORMAL);
}
}
private void updatePrintResolution() {
PrinterResolution res =
(PrinterResolution)printReqAttrSet.get(PrinterResolution.class);
if (res == null) {
res = j2dPrinter.getDefaultPrinterResolution();
}
int cfr = res.getCrossFeedResolution(ResolutionSyntax.DPI);
int fr = res.getFeedResolution(ResolutionSyntax.DPI);
settings.setPrintResolution(PrintHelper.createPrintResolution(cfr, fr));
}
private void updatePageLayout() {
Media media = (Media)printReqAttrSet.get(Media.class);
Paper paper = j2dPrinter.getPaperForMedia(media);
OrientationRequested o = (OrientationRequested)
printReqAttrSet.get(OrientationRequested.class);
PageOrientation orient = J2DPrinter.reverseMapOrientation(o);
MediaPrintableArea mpa =
(MediaPrintableArea)printReqAttrSet.get(MediaPrintableArea.class);
PageLayout newLayout;
if (mpa == null) {
newLayout = fxPrinter.createPageLayout(paper, orient,
MarginType.DEFAULT);
} else {
double pWid = paper.getWidth();
double pHgt = paper.getHeight();
int INCH = MediaPrintableArea.INCH;
double mpaX = mpa.getX(INCH) * 72;
double mpaY = mpa.getY(INCH) * 72;
double mpaW = mpa.getWidth(INCH) * 72;
double mpaH = mpa.getHeight(INCH) * 72;
double lm=0, rm=0, tm=0, bm=0;
switch (orient) {
case PORTRAIT:
lm = mpaX;
rm = pWid - mpaX - mpaW;
tm = mpaY;
bm = pHgt - mpaY - mpaH;
break;
case REVERSE_PORTRAIT:
lm = pWid - mpaX - mpaW;
rm = mpaX;
tm = pHgt - mpaY - mpaH;
bm = mpaY;
break;
case LANDSCAPE:
lm = mpaY;
rm = pHgt - mpaY - mpaH;
tm = pWid - mpaX - mpaW;
bm = mpaX;
break;
case REVERSE_LANDSCAPE:
lm = pHgt - mpaY - mpaH;
tm = mpaX;
rm = mpaY;
bm = pWid - mpaX - mpaW;
break;
}
if (Math.abs(lm) < 0.01) lm = 0;
if (Math.abs(rm) < 0.01) rm = 0;
if (Math.abs(tm) < 0.01) tm = 0;
if (Math.abs(bm) < 0.01) bm = 0;
newLayout = fxPrinter.createPageLayout(paper, orient,
lm, rm, tm, bm);
}
settings.setPageLayout(newLayout);
}
private void updatePaperSource() {
Media m = (Media)printReqAttrSet.get(Media.class);
if (m instanceof MediaTray) {
PaperSource s = j2dPrinter.getPaperSource((MediaTray)m);
if (s != null) {
settings.setPaperSource(s);
}
}
}
private Printer getFXPrinterForService(PrintService service) {
Set<Printer> printerSet = Printer.getAllPrinters();
for (Printer p : printerSet) {
J2DPrinter p2d = (J2DPrinter)PrintHelper.getPrinterImpl(p);
PrintService s = p2d.getService();
if (s.equals(service)) {
return p;
}
}
return fxPrinter;
}
public void setPrinterImpl(PrinterImpl impl) {
j2dPrinter = (J2DPrinter)impl;
fxPrinter = j2dPrinter.getPrinter();
try {
pJob2D.setPrintService(j2dPrinter.getService());
} catch (PrinterException pe) {
}
}
public PrinterImpl getPrinterImpl() {
return j2dPrinter;
}
private J2DPrinter getJ2DPrinter(Printer printer) {
return (J2DPrinter)PrintHelper.getPrinterImpl(printer);
}
public Printer getPrinter() {
return fxPrinter;
}
public void setPrinter(Printer printer) {
fxPrinter = printer;
j2dPrinter = getJ2DPrinter(printer);
try {
pJob2D.setPrintService(j2dPrinter.getService());
} catch (PrinterException pe) {
}
}
private void updatePrinter() {
PrintService currService = j2dPrinter.getService();
PrintService jobService = pJob2D.getPrintService();
if (currService.equals(jobService)) {
return;
}
Printer newFXPrinter = getFXPrinterForService(jobService);
fxPrinterJob.setPrinter(newFXPrinter);
}
private void updateSettingsFromDialog() {
updatePrinter();
updateJobName();
updateOutputFile();
updateCopies();
updatePageRanges();
updateSides();
updateCollation();
updatePageLayout();
updatePaperSource();
updateColor();
updatePrintQuality();
updatePrintResolution();
}
private void syncSettingsToAttributes() {
syncJobName();
syncOutputFile();
syncCopies();
syncPageRanges();
syncSides();
syncCollation();
syncPageLayout();
syncPaperSource();
syncColor();
syncPrintQuality();
syncPrintResolution();
}
private void syncJobName() {
pJob2D.setJobName(settings.getJobName());
}
private void syncOutputFile() {
printReqAttrSet.remove(Destination.class);
String file = settings.getOutputFile();
if (file != null && !file.isEmpty()) {
URI uri = (new File(file)).toURI();
Destination d = new Destination(uri);
printReqAttrSet.add(d);
}
}
private void syncCopies() {
pJob2D.setCopies(settings.getCopies());
printReqAttrSet.add(new Copies(settings.getCopies()));
}
private void syncPageRanges() {
printReqAttrSet.remove(PageRanges.class);
PageRange[] prArr = settings.getPageRanges();
if (prArr != null && prArr.length>0) {
int len = prArr.length;
int[][] ranges = new int[len][2];
for (int i=0;i<len;i++) {
ranges[i][0] = prArr[i].getStartPage();
ranges[i][1] = prArr[i].getEndPage();
}
printReqAttrSet.add(new PageRanges(ranges));
}
}
private void syncSides() {
Sides j2dSides = Sides.ONE_SIDED;
PrintSides sides = settings.getPrintSides();
if (sides == PrintSides.DUPLEX) {
j2dSides = Sides.DUPLEX;
} else if (sides == PrintSides.TUMBLE) {
j2dSides = Sides.TUMBLE;
}
printReqAttrSet.add(j2dSides);
}
private void syncCollation() {
if (settings.getCollation() == Collation.UNCOLLATED) {
printReqAttrSet.add(SheetCollate.UNCOLLATED);
} else {
printReqAttrSet.add(SheetCollate.COLLATED);
}
}
private void syncPageLayout() {
PageLayout layout = settings.getPageLayout();
PageOrientation orient = layout.getPageOrientation();
printReqAttrSet.add(J2DPrinter.mapOrientation(orient));
double pWid = layout.getPaper().getWidth();
double pHgt = layout.getPaper().getHeight();
float widthInInches = (float)(pWid/72.0);
float heightInInches = (float)(pHgt/72.0);
MediaSizeName media = MediaSize.findMedia(widthInInches,
heightInInches,
Size2DSyntax.INCH);
if (media == null) {
media = MediaSizeName.NA_LETTER;
}
printReqAttrSet.add(media);
double ix=0, iy=0, iw=pWid, ih=pHgt;
switch (orient) {
case PORTRAIT:
ix = layout.getLeftMargin();
iy = layout.getTopMargin();
iw = pWid - ix - layout.getRightMargin();
ih = pHgt - iy - layout.getBottomMargin();
break;
case REVERSE_PORTRAIT:
ix = layout.getRightMargin();
iy = layout.getBottomMargin();
iw = pWid - ix - layout.getLeftMargin();
ih = pHgt - iy - layout.getTopMargin();
break;
case LANDSCAPE:
ix = layout.getBottomMargin();
iy = layout.getLeftMargin();
iw = pWid - ix - layout.getTopMargin();
ih = pHgt - iy - layout.getRightMargin();
break;
case REVERSE_LANDSCAPE:
ix = layout.getTopMargin();
iy = layout.getRightMargin();
iw = pWid - ix - layout.getBottomMargin();
ih = pHgt - iy - layout.getLeftMargin();
}
ix /= 72.0;
iy /= 72.0;
ih /= 72.0;
iw /= 72.0;
MediaPrintableArea mpa =
new MediaPrintableArea((float)ix, (float)iy,
(float)iw, (float)ih,
MediaPrintableArea.INCH);
printReqAttrSet.add(mpa);
}
private void syncPaperSource() {
Media m = (Media)printReqAttrSet.get(Media.class);
if (m != null && m instanceof MediaTray) {
printReqAttrSet.remove(Media.class);
}
PaperSource source = settings.getPaperSource();
if (!source.equals(j2dPrinter.defaultPaperSource())) {
MediaTray tray = j2dPrinter.getTrayForPaperSource(source);
if (tray != null) {
printReqAttrSet.add(tray);
}
}
}
private void syncColor() {
if (settings.getPrintColor() == PrintColor.MONOCHROME) {
printReqAttrSet.add(Chromaticity.MONOCHROME);
} else {
printReqAttrSet.add(Chromaticity.COLOR);
}
}
private void syncPrintQuality() {
javafx.print.PrintQuality
quality = settings.getPrintQuality();
PrintQuality j2DQuality;
if (quality == javafx.print.PrintQuality.DRAFT) {
j2DQuality = PrintQuality.DRAFT;
} else if (quality == javafx.print.PrintQuality.HIGH) {
j2DQuality = PrintQuality.HIGH;
} else {
j2DQuality = PrintQuality.NORMAL;
}
printReqAttrSet.add(j2DQuality);
}
private void syncPrintResolution() {
PrintService ps = pJob2D.getPrintService();
if (!ps.isAttributeCategorySupported(PrinterResolution.class)) {
printReqAttrSet.remove(PrinterResolution.class);
return;
}
PrinterResolution pres =
(PrinterResolution)printReqAttrSet.get(PrinterResolution.class);
if (pres != null && !ps.isAttributeValueSupported(pres, null, null)) {
printReqAttrSet.remove(PrinterResolution.class);
};
PrintResolution res = settings.getPrintResolution();
if (res == null) {
return;
}
int cfRes = res.getCrossFeedResolution();
int fRes = res.getFeedResolution();
pres = new PrinterResolution(cfRes, fRes, ResolutionSyntax.DPI);
if (!ps.isAttributeValueSupported(pres, null, null)) {
return;
}
printReqAttrSet.add(pres);
}
public PageLayout validatePageLayout(PageLayout pageLayout) {
boolean needsNewLayout = false;
PrinterAttributes caps = fxPrinter.getPrinterAttributes();
Paper p = pageLayout.getPaper();
if (!caps.getSupportedPapers().contains(p)) {
needsNewLayout = true;
p = caps.getDefaultPaper();
}
PageOrientation o = pageLayout.getPageOrientation();
if (!caps.getSupportedPageOrientations().contains(o)) {
needsNewLayout = true;
o = caps.getDefaultPageOrientation();
}
if (needsNewLayout) {
pageLayout = fxPrinter.createPageLayout(p, o, MarginType.DEFAULT);
}
return pageLayout;
}
private boolean jobRunning = false;
private boolean jobError = false;
private boolean jobDone = false;
private J2DPageable j2dPageable = null;
private void checkPermissions() {
@SuppressWarnings("removal")
SecurityManager security = System.getSecurityManager();
if (security != null) {
security.checkPrintJobAccess();
String file = settings.getOutputFile();
if (file != null && !file.isEmpty()) {
security.checkWrite(file);
}
}
}
public boolean print(PageLayout pageLayout, Node node) {
if (Toolkit.getToolkit().isFxUserThread()) {
if (!Toolkit.getToolkit().canStartNestedEventLoop()) {
throw new IllegalStateException("Printing is not allowed during animation or layout processing");
}
}
if (jobError || jobDone) {
return false;
}
if (!jobRunning) {
checkPermissions();
syncSettingsToAttributes();
PrintJobRunnable runnable = new PrintJobRunnable();
Thread prtThread = new Thread(runnable, "Print Job Thread");
prtThread.start();
jobRunning = true;
}
try {
j2dPageable.implPrintPage(pageLayout, node);
} catch (Throwable t) {
if (com.sun.prism.impl.PrismSettings.debug) {
System.err.println("printPage caught exception.");
t.printStackTrace();
}
jobError = true;
jobDone = true;
}
return !jobError;
}
private class PrintJobRunnable implements Runnable {
public void run() {
try {
pJob2D.print(printReqAttrSet);
jobDone = true;
} catch (Throwable t) {
if (com.sun.prism.impl.PrismSettings.debug) {
System.err.println("print caught exception.");
t.printStackTrace();
}
jobError = true;
jobDone = true;
}
if (elo != null) {
Application.invokeLater(new ExitLoopRunnable(elo, null));
}
}
}
static class LayoutRunnable implements Runnable {
PageInfo pageInfo;
LayoutRunnable(PageInfo info) {
pageInfo = info;
}
public void run() {
if (pageInfo.tempScene && pageInfo.root.getScene() == null) {
new Scene(pageInfo.root);
}
NodeHelper.layoutNodeForPrinting(pageInfo.root);
}
}
static class ClearSceneRunnable implements Runnable {
PageInfo pageInfo;
ClearSceneRunnable(PageInfo info) {
pageInfo = info;
}
public void run() {
pageInfo.clearScene();
}
}
private static class PageInfo {
private PageLayout pageLayout;
private Node node;
private Parent root;
private Node topNode;
private Group group;
private boolean tempGroup;
private boolean tempScene;
private boolean sceneInited;
PageInfo(PageLayout pageLayout, Node node) {
this.pageLayout = pageLayout;
this.node = node;
}
Node getNode() {
initScene();
return node;
}
PageLayout getPageLayout() {
return pageLayout;
}
void initScene() {
if (sceneInited) {
return;
}
if (node.getScene() == null) {
tempScene = true;
Node topNode = node;
while (topNode.getParent() != null) {
topNode = topNode.getParent();
}
if (topNode instanceof Group) {
group = (Group)topNode;
} else {
tempGroup = true;
group = new Group();
group.getChildren().add(topNode);
}
root = group;
} else {
root = node.getScene().getRoot();
}
if (Toolkit.getToolkit().isFxUserThread()) {
if (tempScene && root.getScene() == null) {
new Scene(root);
}
NodeHelper.layoutNodeForPrinting(root);
} else {
Application.invokeAndWait(new LayoutRunnable(this));
}
sceneInited = true;
}
private void clearScene() {
if (tempGroup) {
group.getChildren().removeAll(root);
}
tempGroup = false;
tempScene = false;
root = null;
group = null;
topNode = null;
sceneInited = false;
}
}
private Object monitor = new Object();
static class ExitLoopRunnable implements Runnable {
Object elo, rv;
ExitLoopRunnable(Object elo, Object rv) {
this.elo = elo;
this.rv = rv;
}
public void run() {
Toolkit.getToolkit().exitNestedEventLoop(elo, rv);
}
}
private class J2DPageable implements Pageable, Printable {
private volatile boolean pageDone;
private int currPageIndex = -1;
private volatile PageInfo newPageInfo = null;
private PageInfo currPageInfo;
private PageFormat currPageFormat;
private boolean waitForNextPage(int pageIndex) {
if (elo != null && currPageInfo != null) {
Application.invokeLater(new ExitLoopRunnable(elo, null));
}
if (currPageInfo != null) {
if (Toolkit.getToolkit().isFxUserThread()) {
currPageInfo.clearScene();
} else {
Application.
invokeAndWait(new ClearSceneRunnable(currPageInfo));
}
}
currPageInfo = null;
pageDone = true;
synchronized (monitor) {
if (newPageInfo == null) {
monitor.notify();
}
while (newPageInfo == null && !jobDone && !jobError) {
try {
monitor.wait(1000);
} catch (InterruptedException e) {
}
}
}
if (jobDone || jobError) {
return false;
}
currPageInfo = newPageInfo;
newPageInfo = null;
currPageIndex = pageIndex;
currPageFormat = getPageFormatFromLayout(currPageInfo.getPageLayout());
return true;
}
private PageFormat getPageFormatFromLayout(PageLayout layout) {
java.awt.print.Paper paper = new java.awt.print.Paper();
double pWid = layout.getPaper().getWidth();
double pHgt = layout.getPaper().getHeight();
double ix=0, iy=0, iw=pWid, ih=pHgt;
PageOrientation orient = layout.getPageOrientation();
switch (orient) {
case PORTRAIT:
ix = layout.getLeftMargin();
iy = layout.getTopMargin();
iw = pWid - ix - layout.getRightMargin();
ih = pHgt - iy - layout.getBottomMargin();
break;
case REVERSE_PORTRAIT:
ix = layout.getRightMargin();
iy = layout.getBottomMargin();
iw = pWid - ix - layout.getLeftMargin();
ih = pHgt - iy - layout.getTopMargin();
break;
case LANDSCAPE:
ix = layout.getBottomMargin();
iy = layout.getLeftMargin();
iw = pWid - ix - layout.getTopMargin();
ih = pHgt - iy - layout.getRightMargin();
break;
case REVERSE_LANDSCAPE:
ix = layout.getTopMargin();
iy = layout.getRightMargin();
iw = pWid - ix - layout.getBottomMargin();
ih = pHgt - iy - layout.getLeftMargin();
}
paper.setSize(pWid, pHgt);
paper.setImageableArea(ix, iy, iw, ih);
PageFormat format = new PageFormat();
format.setOrientation(J2DPrinter.getOrientID(orient));
format.setPaper(paper);
return format;
}
private boolean getPage(int pageIndex) {
if (pageIndex == currPageIndex) {
return true;
}
boolean nextPage = false;
if (pageIndex > currPageIndex) {
nextPage = waitForNextPage(pageIndex);
}
return nextPage;
}
public int print(Graphics g, PageFormat pf, int pageIndex) {
if (jobError || jobDone || !getPage(pageIndex)) {
return Printable.NO_SUCH_PAGE;
}
int x = (int)pf.getImageableX();
int y = (int)pf.getImageableY();
int w = (int)pf.getImageableWidth();
int h = (int)pf.getImageableHeight();
Node appNode = currPageInfo.getNode();
g.translate(x, y);
printNode(appNode, g, w, h);
return Printable.PAGE_EXISTS;
}
private void printNode(Node node, Graphics g, int w, int h) {
PrismPrintGraphics ppg =
new PrismPrintGraphics((Graphics2D) g, w, h);
NGNode pgNode = NodeHelper.getPeer(node);
boolean errored = false;
try {
pgNode.render(ppg);
} catch (Throwable t) {
if (com.sun.prism.impl.PrismSettings.debug) {
System.err.println("printNode caught exception.");
t.printStackTrace();
}
errored = true;
}
ppg.getResourceFactory()
.getTextureResourcePool()
.freeDisposalRequestedAndCheckResources(errored);
}
public Printable getPrintable(int pageIndex) {
getPage(pageIndex);
return this;
}
public PageFormat getPageFormat(int pageIndex) {
getPage(pageIndex);
return currPageFormat;
}
public int getNumberOfPages() {
return Pageable.UNKNOWN_NUMBER_OF_PAGES;
}
private void implPrintPage(PageLayout pageLayout, Node node) {
pageDone = false;
synchronized (monitor) {
newPageInfo = new PageInfo(pageLayout, node);
monitor.notify();
}
if (Toolkit.getToolkit().isFxUserThread()) {
elo = new Object();
Toolkit.getToolkit().enterNestedEventLoop(elo);
elo = null;
} else {
while (!pageDone && !jobDone && !jobError) {
synchronized (monitor) {
try {
if (!pageDone) {
monitor.wait(1000);
}
} catch (InterruptedException e) {
}
}
}
}
}
}
public boolean endJob() {
if (jobRunning && !jobDone && !jobError) {
jobDone = true;
try {
synchronized (monitor) {
monitor.notify();
return jobDone;
}
} catch (IllegalStateException e) {
if (com.sun.prism.impl.PrismSettings.debug) {
System.err.println("Internal Error " + e);
}
}
} else {
return jobDone && !jobError;
}
return jobDone;
}
public void cancelJob() {
if (!pJob2D.isCancelled()) {
pJob2D.cancel();
}
jobDone = true;
if (jobRunning) {
jobRunning = false;
try {
synchronized (monitor) {
monitor.notify();
}
} catch (IllegalStateException e) {
if (com.sun.prism.impl.PrismSettings.debug) {
System.err.println("Internal Error " + e);
}
}
}
}
}

package ensemble.samples.charts.candlestick;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
public class Candle extends Group {
private Line highLowLine = new Line();
private Region bar = new Region();
private String seriesStyleClass;
private String dataStyleClass;
private boolean openAboveClose = true;
private Tooltip tooltip = new Tooltip();
Candle(String seriesStyleClass, String dataStyleClass) {
setAutoSizeChildren(false);
getChildren().addAll(highLowLine, bar);
this.seriesStyleClass = seriesStyleClass;
this.dataStyleClass = dataStyleClass;
updateStyleClasses();
tooltip.setGraphic(new TooltipContent());
Tooltip.install(bar, tooltip);
}
public void setSeriesAndDataStyleClasses(String seriesStyleClass,
String dataStyleClass) {
this.seriesStyleClass = seriesStyleClass;
this.dataStyleClass = dataStyleClass;
updateStyleClasses();
}
public void update(double closeOffset, double highOffset,
double lowOffset, double candleWidth) {
openAboveClose = closeOffset > 0;
updateStyleClasses();
highLowLine.setStartY(highOffset);
highLowLine.setEndY(lowOffset);
if (candleWidth == -1) {
candleWidth = bar.prefWidth(-1);
}
if (openAboveClose) {
bar.resizeRelocate(-candleWidth / 2, 0,
candleWidth, closeOffset);
} else {
bar.resizeRelocate(-candleWidth / 2, closeOffset,
candleWidth, -closeOffset);
}
}
public void updateTooltip(double open, double close, double high, double low) {
TooltipContent tooltipContent = (TooltipContent) tooltip.getGraphic();
tooltipContent.update(open, close, high, low);
}
private void updateStyleClasses() {
final String aboveClose =
openAboveClose ? "open-above-close" : "close-above-open";
getStyleClass().setAll("candlestick-candle",
seriesStyleClass, dataStyleClass);
highLowLine.getStyleClass().setAll("candlestick-line",
seriesStyleClass, dataStyleClass,
aboveClose);
bar.getStyleClass().setAll("candlestick-bar",
seriesStyleClass, dataStyleClass,
aboveClose);
}
}

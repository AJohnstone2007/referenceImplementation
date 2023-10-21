package com.sun.webkit;
import com.sun.javafx.scene.control.CustomColorDialog;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
public final class ColorChooser {
private static final double COLOR_DOUBLE_TO_UCHAR_FACTOR = 255.0;
private final CustomColorDialog colorChooserDialog;
private final long pdata;
private ColorChooser(WebPage webPage, Color color, long data) {
this.pdata = data;
WebPageClient<WebView> client = webPage.getPageClient();
assert (client != null);
colorChooserDialog = new CustomColorDialog(client.getContainer().getScene().getWindow());
colorChooserDialog.setSaveBtnToOk();
colorChooserDialog.setShowUseBtn(false);
colorChooserDialog.setShowOpacitySlider(false);
colorChooserDialog.setOnSave(() -> {
twkSetSelectedColor(pdata,
(int) Math.round(colorChooserDialog.getCustomColor().getRed() * COLOR_DOUBLE_TO_UCHAR_FACTOR),
(int) Math.round(colorChooserDialog.getCustomColor().getGreen() * COLOR_DOUBLE_TO_UCHAR_FACTOR),
(int) Math.round(colorChooserDialog.getCustomColor().getBlue() * COLOR_DOUBLE_TO_UCHAR_FACTOR));
});
colorChooserDialog.setCurrentColor(color);
colorChooserDialog.show();
}
private static ColorChooser fwkCreateAndShowColorChooser(WebPage webPage, int r, int g, int b, long pdata) {
return new ColorChooser(webPage, Color.rgb(r,g,b), pdata);
}
private void fwkShowColorChooser(int r, int g, int b) {
colorChooserDialog.setCurrentColor(Color.rgb(r,g,b));
colorChooserDialog.show();
}
private void fwkHideColorChooser() {
colorChooserDialog.hide();
}
private native void twkSetSelectedColor(long data, int r, int g, int b);
}

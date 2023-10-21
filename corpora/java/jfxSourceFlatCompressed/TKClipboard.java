package com.sun.javafx.tk;
import java.security.AccessControlContext;
import java.util.Set;
import javafx.scene.image.Image;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;
import javafx.util.Pair;
public interface TKClipboard {
public void setSecurityContext(@SuppressWarnings("removal") AccessControlContext ctx);
public Set<DataFormat> getContentTypes();
public boolean putContent(Pair<DataFormat, Object>... content);
public Object getContent(DataFormat dataFormat);
public boolean hasContent(DataFormat dataFormat);
public Set<TransferMode> getTransferModes();
public void setDragView(Image image);
public void setDragViewOffsetX(double offsetX);
public void setDragViewOffsetY(double offsetY);
public Image getDragView();
public double getDragViewOffsetX();
public double getDragViewOffsetY();
}

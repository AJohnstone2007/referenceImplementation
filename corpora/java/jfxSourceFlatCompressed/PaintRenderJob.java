package com.sun.javafx.tk.quantum;
import com.sun.javafx.tk.CompletionListener;
import com.sun.javafx.tk.RenderJob;
class PaintRenderJob extends RenderJob {
private GlassScene scene;
public PaintRenderJob(GlassScene gs, CompletionListener cl, Runnable r) {
super(r, cl);
this.scene = gs;
}
public GlassScene getScene() {
return scene;
}
}

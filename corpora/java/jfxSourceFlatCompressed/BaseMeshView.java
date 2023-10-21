package com.sun.prism.impl;
import com.sun.prism.MeshView;
public abstract class BaseMeshView extends BaseGraphicsResource implements MeshView {
protected BaseMeshView(Disposer.Record disposerRecord) {
super(disposerRecord);
}
@Override
public boolean isValid() {
return true;
}
}

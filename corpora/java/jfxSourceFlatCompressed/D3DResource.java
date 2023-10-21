package com.sun.prism.d3d;
import com.sun.prism.impl.BaseGraphicsResource;
import com.sun.prism.impl.Disposer;
class D3DResource extends BaseGraphicsResource {
protected final D3DRecord d3dResRecord;
D3DResource(D3DRecord disposerRecord) {
super(disposerRecord);
this.d3dResRecord = disposerRecord;
}
@Override
public void dispose() {
d3dResRecord.dispose();
}
static class D3DRecord implements Disposer.Record {
private final D3DContext context;
private long pResource;
private boolean isDefaultPool;
D3DRecord(D3DContext context, long pResource) {
this.context = context;
this.pResource = pResource;
if (pResource != 0L) {
context.getResourceFactory().addRecord(this);
isDefaultPool = D3DResourceFactory.nIsDefaultPool(pResource);
} else {
isDefaultPool = false;
}
}
long getResource() {
return pResource;
}
D3DContext getContext() {
return context;
}
boolean isDefaultPool() {
return isDefaultPool;
}
protected void markDisposed() {
pResource = 0L;
}
@Override
public void dispose() {
if (pResource != 0L) {
context.getResourceFactory().removeRecord(this);
D3DResourceFactory.nReleaseResource(context.getContextHandle(),
pResource);
pResource = 0L;
}
}
}
}

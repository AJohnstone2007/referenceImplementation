package com.sun.prism.es2;
import com.sun.prism.impl.DisposerManagedResource;
class ES2TextureResource<T extends ES2TextureData>
extends DisposerManagedResource<T>
{
ES2TextureResource(T resource) {
super(resource, ES2VramPool.instance, resource);
}
@Override
public void free() {
if (resource != null) {
resource.dispose();
}
}
}

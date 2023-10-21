package com.sun.javafx.webkit.prism;
public final class PrismInvokerShim {
public static void runOnRenderThread(final Runnable r) {
PrismInvoker.runOnRenderThread(r);
}
}

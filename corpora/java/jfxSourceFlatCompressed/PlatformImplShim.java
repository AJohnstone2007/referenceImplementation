package com.sun.javafx.application;
import java.util.concurrent.CountDownLatch;
public class PlatformImplShim {
public static CountDownLatch test_getPlatformExitLatch() {
return PlatformImpl.test_getPlatformExitLatch();
}
}

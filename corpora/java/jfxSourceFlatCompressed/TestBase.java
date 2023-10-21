package test.javafx.scene.web;
import java.awt.Color;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import com.sun.javafx.application.PlatformImpl;
import java.util.concurrent.ExecutionException;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.junit.BeforeClass;
import org.w3c.dom.Document;
public class TestBase implements ChangeListener, InvalidationListener {
private static final AtomicBoolean LOCK = new AtomicBoolean(false);
private static final int INIT_TIMEOUT = 10000;
private static final int LOAD_TIMEOUT = 60000;
private static WebView view;
@BeforeClass
public static void setupOnce() {
final CountDownLatch startupLatch = new CountDownLatch(1);
PlatformImpl.startup(() -> {
startupLatch.countDown();
});
try {
startupLatch.await();
} catch (InterruptedException ex) {}
}
public TestBase() {
Platform.runLater(() -> {
view = new WebView();
WebEngine web = view.getEngine();
web.documentProperty().addListener((ChangeListener)TestBase.this);
web.documentProperty().addListener((InvalidationListener)TestBase.this);
web.titleProperty().addListener((ChangeListener)TestBase.this);
web.titleProperty().addListener((InvalidationListener)TestBase.this);
web.locationProperty().addListener((ChangeListener)TestBase.this);
web.locationProperty().addListener((InvalidationListener)TestBase.this);
Worker loadTask = web.getLoadWorker();
loadTask.exceptionProperty().addListener((ChangeListener)TestBase.this);
loadTask.exceptionProperty().addListener((InvalidationListener)TestBase.this);
loadTask.messageProperty().addListener((ChangeListener)TestBase.this);
loadTask.messageProperty().addListener((InvalidationListener)TestBase.this);
loadTask.progressProperty().addListener((ChangeListener)TestBase.this);
loadTask.progressProperty().addListener((InvalidationListener)TestBase.this);
loadTask.runningProperty().addListener((ChangeListener)TestBase.this);
loadTask.runningProperty().addListener((InvalidationListener)TestBase.this);
loadTask.stateProperty().addListener((ChangeListener)TestBase.this);
loadTask.stateProperty().addListener((InvalidationListener)TestBase.this);
loadTask.titleProperty().addListener((ChangeListener)TestBase.this);
loadTask.titleProperty().addListener((InvalidationListener)TestBase.this);
loadTask.totalWorkProperty().addListener((ChangeListener)TestBase.this);
loadTask.totalWorkProperty().addListener((InvalidationListener)TestBase.this);
loadTask.valueProperty().addListener((ChangeListener)TestBase.this);
loadTask.valueProperty().addListener((InvalidationListener)TestBase.this);
loadTask.workDoneProperty().addListener((ChangeListener)TestBase.this);
loadTask.workDoneProperty().addListener((InvalidationListener)TestBase.this);
loadTask.runningProperty().addListener(new LoadFinishedListener());
TestBase.this.notify(LOCK);
});
wait(LOCK, INIT_TIMEOUT);
}
protected void load(final String url) {
Platform.runLater(() -> {
getEngine().load(url);
});
waitLoadFinished();
}
protected void reload() {
Platform.runLater(() -> {
getEngine().reload();
});
waitLoadFinished();
}
protected void load(File file) {
load(file.toURI().toASCIIString());
}
protected Document getDocumentFor(String fileName) {
load(new File(fileName));
return getEngine().getDocument();
}
protected void loadContent(final String content, final String contentType) {
Platform.runLater(() -> {
getEngine().loadContent(content, contentType);
});
waitLoadFinished();
}
protected void loadContent(final String content) {
loadContent(content, "text/html");
}
protected void submit(Runnable job) {
final FutureTask<Void> future = new FutureTask<Void>(job, null);
Platform.runLater(future);
try {
future.get();
} catch (ExecutionException e) {
Throwable cause = e.getCause();
if (cause instanceof AssertionError) {
throw (AssertionError) e.getCause();
} else if (cause instanceof RuntimeException) {
throw (RuntimeException) cause;
}
throw new AssertionError(cause);
} catch (InterruptedException e) {
throw new AssertionError(e);
}
}
protected <T> T submit(Callable<T> job) {
final FutureTask<T> future = new FutureTask<T>(job);
Platform.runLater(future);
try {
return future.get();
} catch (ExecutionException e) {
Throwable cause = e.getCause();
if (cause instanceof AssertionError) {
throw (AssertionError) e.getCause();
}
throw new AssertionError(cause);
} catch (InterruptedException e) {
throw new AssertionError(e);
}
}
protected Object executeScript(final String script) {
return submit(() -> getEngine().executeScript(script));
}
private class LoadFinishedListener implements ChangeListener<Boolean> {
@Override
public void changed(ObservableValue<? extends Boolean> observable,
Boolean oldValue, Boolean newValue) {
if (! newValue) {
TestBase.this.notify(LOCK);
}
}
}
private void wait(AtomicBoolean condition, long timeout) {
synchronized (condition) {
long startTime = System.currentTimeMillis();
while (!condition.get()) {
try {
condition.wait(timeout);
} catch (InterruptedException e) {
} finally {
if (System.currentTimeMillis() - startTime >= timeout) {
throw new AssertionError("Waiting timed out");
}
}
}
condition.set(false);
}
}
private void notify(AtomicBoolean condition) {
synchronized (condition) {
condition.set(true);
condition.notifyAll();
}
}
@Override public void invalidated(Observable value) {
}
@Override public void changed(ObservableValue value, Object oldValue, Object newValue) {
}
protected WebEngine getEngine() {
return view.getEngine();
}
protected WebView getView() {
return view;
}
protected int getLoadTimeOut() {
return LOAD_TIMEOUT;
}
public void waitLoadFinished() {
wait(LOCK, getLoadTimeOut());
}
public boolean isJigsawMode() {
Class clazz = null;
try {
clazz = Class.forName("java.lang.reflect.ModuleDescriptor", false, TestBase.class.getClassLoader());
} catch (Exception e) { }
return clazz != null;
}
protected static float getColorDifference(final Color base, final Color c) {
final float red = (c.getRed() - base.getRed()) / Math.max(255.0f - base.getRed(), base.getRed());
final float green = (c.getGreen() - base.getGreen()) / Math.max(255.0f - base.getGreen(), base.getGreen());
final float blue = (c.getBlue() - base.getBlue()) / Math.max(255.0f - base.getBlue(), base.getBlue());
final float alpha = (c.getAlpha() - base.getAlpha()) / Math.max(255.0f - base.getAlpha(), base.getAlpha());
final float distance = ((float) Math.sqrt(red * red + green * green + blue * blue + alpha * alpha)) / 2.0f;
return distance >= (1 / 255.0f) ? distance * 100.0f : 0;
}
protected static boolean isColorsSimilar(final Color base, final Color c, float toleranceInPercentage) {
return toleranceInPercentage >= getColorDifference(base, c);
}
}

package test.com.sun.javafx.pgstub;
import com.sun.javafx.runtime.async.AsyncOperation;
import com.sun.javafx.runtime.async.AsyncOperationListener;
import com.sun.javafx.tk.ImageLoader;
public class StubAsyncImageLoader implements AsyncOperation {
private final ImageLoader imageLoader;
private final AsyncOperationListener<ImageLoader> listener;
private boolean started;
private boolean cancelled;
private boolean finished;
public StubAsyncImageLoader(
final ImageLoader imageLoader,
final AsyncOperationListener<ImageLoader> listener) {
this.imageLoader = imageLoader;
this.listener = listener;
}
@Override
public void start() {
started = true;
}
@Override
public void cancel() {
cancelled = true;
finished = true;
listener.onCancel();
}
@Override
public boolean isCancelled() {
return cancelled;
}
@Override
public boolean isDone() {
return finished;
}
public boolean isStarted() {
return started;
}
public void finish() {
finished = true;
listener.onProgress(100, 100);
listener.onCompletion(imageLoader);
}
public void finish(final Exception e) {
finished = true;
listener.onException(e);
}
public void setProgress(final int cur, final int max) {
listener.onProgress(cur, max);
}
}

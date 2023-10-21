package com.sun.javafx.runtime.async;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
public abstract class AbstractAsyncOperation<V> implements AsyncOperation,
Callable<V> {
protected final FutureTask<V> future;
protected final AsyncOperationListener listener;
private int progressGranularity = 100;
private int progressMax, lastProgress, progressIncrement, nextProgress, bytesRead;
protected AbstractAsyncOperation(final AsyncOperationListener<V> listener) {
this.listener = listener;
Callable<V> callable = () -> AbstractAsyncOperation.this.call();
final Runnable completionRunnable = new Runnable() {
public void run() {
if (future.isCancelled()) {
listener.onCancel();
}
else
try {
listener.onCompletion(future.get());
}
catch (InterruptedException e) {
listener.onCancel();
}
catch (ExecutionException e) {
listener.onException(e);
}
}
};
future = new FutureTask<V>(callable) {
@Override
protected void done() {
try {
Platform.runLater(completionRunnable);
}
finally {
super.done();
}
}
};
}
public boolean isCancelled() {
return future.isCancelled();
}
public boolean isDone() {
return future.isDone();
}
public void cancel() {
future.cancel(true);
}
public void start() {
BackgroundExecutor.getExecutor().execute(future);
}
protected void notifyProgress() {
final int last = lastProgress;
final int max = progressMax;
Platform.runLater(() -> listener.onProgress(last, max));
}
protected void addProgress(int amount) {
bytesRead += amount;
if (bytesRead > nextProgress) {
lastProgress = bytesRead;
notifyProgress();
nextProgress = ((lastProgress / progressIncrement) + 1) * progressIncrement;
}
}
protected int getProgressMax() {
return progressMax;
}
protected void setProgressMax(int progressMax) {
if (progressMax == 0) {
progressIncrement = progressGranularity;
}
else if (progressMax == -1) {
progressIncrement = progressGranularity;
}
else {
this.progressMax = progressMax;
progressIncrement = progressMax / progressGranularity;
if(progressIncrement < 1) {
progressIncrement = 1;
}
}
nextProgress = ((lastProgress / progressIncrement) + 1) * progressIncrement;
notifyProgress();
}
protected int getProgressGranularity() {
return progressGranularity;
}
protected void setProgressGranularity(int progressGranularity) {
this.progressGranularity = progressGranularity;
progressIncrement = progressMax / progressGranularity;
nextProgress = ((lastProgress / progressIncrement) + 1) * progressIncrement;
notifyProgress();
}
}

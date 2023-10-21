package com.sun.javafx.tk;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
public class RenderJob extends FutureTask {
private CompletionListener listener;
private Object futureReturn;
public RenderJob(Runnable pen) {
super(pen, null);
}
public RenderJob(Runnable pen, CompletionListener cl) {
super(pen, null);
setCompletionListener(cl);
}
public CompletionListener getCompletionListener() {
return listener;
}
public void setCompletionListener(CompletionListener cl) {
listener = cl;
}
@Override public void run() {
if (super.runAndReset() == false) {
try {
Object value = super.get();
System.err.println("RenderJob.run: failed no exception: " + value);
} catch (CancellationException ce) {
System.err.println("RenderJob.run: task cancelled");
} catch (ExecutionException ee) {
System.err.println("RenderJob.run: internal exception");
ee.getCause().printStackTrace();
} catch (Throwable th) {
th.printStackTrace();
}
} else {
if (listener != null) {
try {
listener.done(this);
} catch (Throwable th) {
th.printStackTrace();
}
}
}
}
@Override public Object get() {
return (futureReturn);
}
public void setFutureReturn(Object o) {
futureReturn = o;
}
}

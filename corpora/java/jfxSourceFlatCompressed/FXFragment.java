package javafxports.android;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import java.util.concurrent.CountDownLatch;
public class FXFragment extends Fragment {
private Activity activity;
private String fxAppClassName;
private static final String TAG = "FXFragment";
private static CountDownLatch cdlEvLoopFinished;
private static Launcher launcher;
static {
System.loadLibrary("activity");
}
private FXDalvikEntity fxDalvikEntity;
private SurfaceView mView;
protected FXFragment() {
activity = getActivity();
}
public void setName(String appname) {
fxAppClassName = appname;
}
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
Bundle metadata) {
activity = getActivity();
if (metadata == null) {
metadata = new Bundle();
}
metadata.putSerializable(FXDalvikEntity.META_DATA_MAIN_CLASS, fxAppClassName);
fxDalvikEntity = new FXDalvikEntity(metadata, activity);
mView = fxDalvikEntity.createView();
return mView;
}
}

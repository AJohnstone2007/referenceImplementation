package com.sun.javafx.charts;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.chart.Axis;
public final class ChartLayoutAnimator extends AnimationTimer implements EventHandler<ActionEvent> {
private Parent nodeToLayout;
private final Map<Object,Animation> activeTimeLines = new HashMap<Object, Animation>();
private final boolean isAxis;
public ChartLayoutAnimator(Parent nodeToLayout) {
this.nodeToLayout = nodeToLayout;
isAxis = nodeToLayout instanceof Axis;
}
@Override public void handle(long l) {
if(isAxis) {
((Axis<?>)nodeToLayout).requestAxisLayout();
} else {
nodeToLayout.requestLayout();
}
}
@Override public void handle(ActionEvent actionEvent) {
activeTimeLines.remove(actionEvent.getSource());
if(activeTimeLines.isEmpty()) stop();
handle(0l);
}
public void stop(Object animationID) {
Animation t = activeTimeLines.remove(animationID);
if(t!=null) t.stop();
if(activeTimeLines.isEmpty()) stop();
}
public Object animate(KeyFrame...keyFrames) {
Timeline t = new Timeline();
t.setAutoReverse(false);
t.setCycleCount(1);
t.getKeyFrames().addAll(keyFrames);
t.setOnFinished(this);
if(activeTimeLines.isEmpty()) start();
activeTimeLines.put(t, t);
t.play();
return t;
}
public Object animate(Animation animation) {
SequentialTransition t = new SequentialTransition();
t.getChildren().add(animation);
t.setOnFinished(this);
if(activeTimeLines.isEmpty()) start();
activeTimeLines.put(t, t);
t.play();
return t;
}
}

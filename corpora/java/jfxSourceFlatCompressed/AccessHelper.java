package com.sun.scenario.effect.impl.state;
import com.sun.scenario.effect.Effect;
public class AccessHelper {
public interface StateAccessor {
public Object getState(Effect effect);
}
private static StateAccessor theStateAccessor;
public static void setStateAccessor(StateAccessor accessor) {
if (theStateAccessor != null) {
throw new InternalError("EffectAccessor already initialized");
}
theStateAccessor = accessor;
}
public static Object getState(Effect effect) {
if (effect == null) {
return null;
}
return theStateAccessor.getState(effect);
}
}

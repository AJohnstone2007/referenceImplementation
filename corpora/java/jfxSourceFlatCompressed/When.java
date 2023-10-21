package javafx.beans.binding;
import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.NamedArg;
import javafx.beans.Observable;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableFloatValue;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.sun.javafx.binding.DoubleConstant;
import com.sun.javafx.binding.FloatConstant;
import com.sun.javafx.binding.IntegerConstant;
import com.sun.javafx.binding.Logging;
import com.sun.javafx.binding.LongConstant;
public class When {
private final ObservableBooleanValue condition;
public When(final @NamedArg("condition") ObservableBooleanValue condition) {
if (condition == null) {
throw new NullPointerException("Condition must be specified.");
}
this.condition = condition;
}
private static class WhenListener implements InvalidationListener {
private final ObservableBooleanValue condition;
private final ObservableValue<?> thenValue;
private final ObservableValue<?> otherwiseValue;
private final WeakReference<Binding<?>> ref;
private WhenListener(Binding<?> binding, ObservableBooleanValue condition, ObservableValue<?> thenValue, ObservableValue<?> otherwiseValue) {
this.ref = new WeakReference<Binding<?>>(binding);
this.condition = condition;
this.thenValue = thenValue;
this.otherwiseValue = otherwiseValue;
}
@Override
public void invalidated(Observable observable) {
final Binding<?> binding = ref.get();
if (binding == null) {
condition.removeListener(this);
if (thenValue != null) {
thenValue.removeListener(this);
}
if (otherwiseValue != null) {
otherwiseValue.removeListener(this);
}
} else {
if (condition.equals(observable) || (binding.isValid() && (condition.get() == observable.equals(thenValue)))) {
binding.invalidate();
}
}
}
}
private static NumberBinding createNumberCondition(
final ObservableBooleanValue condition,
final ObservableNumberValue thenValue,
final ObservableNumberValue otherwiseValue) {
if ((thenValue instanceof ObservableDoubleValue) || (otherwiseValue instanceof ObservableDoubleValue)) {
return new DoubleBinding() {
final InvalidationListener observer = new WhenListener(this, condition, thenValue, otherwiseValue);
{
condition.addListener(observer);
thenValue.addListener(observer);
otherwiseValue.addListener(observer);
}
@Override
public void dispose() {
condition.removeListener(observer);
thenValue.removeListener(observer);
otherwiseValue.removeListener(observer);
}
@Override
protected double computeValue() {
final boolean conditionValue = condition.get();
Logging.getLogger().finest("Condition of ternary binding expression was evaluated: {0}", conditionValue);
return conditionValue ? thenValue.doubleValue() : otherwiseValue.doubleValue();
}
@Override
public ObservableList<ObservableValue<?>> getDependencies() {
return FXCollections.unmodifiableObservableList(
FXCollections.<ObservableValue<?>> observableArrayList(condition, thenValue, otherwiseValue));
}
};
} else if ((thenValue instanceof ObservableFloatValue) || (otherwiseValue instanceof ObservableFloatValue)) {
return new FloatBinding() {
final InvalidationListener observer = new WhenListener(this, condition, thenValue, otherwiseValue);
{
condition.addListener(observer);
thenValue.addListener(observer);
otherwiseValue.addListener(observer);
}
@Override
public void dispose() {
condition.removeListener(observer);
thenValue.removeListener(observer);
otherwiseValue.removeListener(observer);
}
@Override
protected float computeValue() {
final boolean conditionValue = condition.get();
Logging.getLogger().finest("Condition of ternary binding expression was evaluated: {0}", conditionValue);
return conditionValue ? thenValue.floatValue() : otherwiseValue.floatValue();
}
@Override
public ObservableList<ObservableValue<?>> getDependencies() {
return FXCollections.unmodifiableObservableList(
FXCollections.<ObservableValue<?>> observableArrayList(condition, thenValue, otherwiseValue));
}
};
} else if ((thenValue instanceof ObservableLongValue) || (otherwiseValue instanceof ObservableLongValue)) {
return new LongBinding() {
final InvalidationListener observer = new WhenListener(this, condition, thenValue, otherwiseValue);
{
condition.addListener(observer);
thenValue.addListener(observer);
otherwiseValue.addListener(observer);
}
@Override
public void dispose() {
condition.removeListener(observer);
thenValue.removeListener(observer);
otherwiseValue.removeListener(observer);
}
@Override
protected long computeValue() {
final boolean conditionValue = condition.get();
Logging.getLogger().finest("Condition of ternary binding expression was evaluated: {0}", conditionValue);
return conditionValue ? thenValue.longValue() : otherwiseValue.longValue();
}
@Override
public ObservableList<ObservableValue<?>> getDependencies() {
return FXCollections.unmodifiableObservableList(
FXCollections.<ObservableValue<?>> observableArrayList(condition, thenValue, otherwiseValue));
}
};
} else {
return new IntegerBinding() {
final InvalidationListener observer = new WhenListener(this, condition, thenValue, otherwiseValue);
{
condition.addListener(observer);
thenValue.addListener(observer);
otherwiseValue.addListener(observer);
}
@Override
public void dispose() {
condition.removeListener(observer);
thenValue.removeListener(observer);
otherwiseValue.removeListener(observer);
}
@Override
protected int computeValue() {
final boolean conditionValue = condition.get();
Logging.getLogger().finest("Condition of ternary binding expression was evaluated: {0}", conditionValue);
return conditionValue ? thenValue.intValue(): otherwiseValue.intValue();
}
@Override
public ObservableList<ObservableValue<?>> getDependencies() {
return FXCollections.unmodifiableObservableList(
FXCollections.<ObservableValue<?>> observableArrayList(condition, thenValue, otherwiseValue));
}
};
}
}
public class NumberConditionBuilder {
private ObservableNumberValue thenValue;
private NumberConditionBuilder(final ObservableNumberValue thenValue) {
this.thenValue = thenValue;
}
public NumberBinding otherwise(ObservableNumberValue otherwiseValue) {
if (otherwiseValue == null) {
throw new NullPointerException("Value needs to be specified");
}
return When.createNumberCondition(condition, thenValue, otherwiseValue);
}
public DoubleBinding otherwise(double otherwiseValue) {
return (DoubleBinding) otherwise(DoubleConstant.valueOf(otherwiseValue));
}
public NumberBinding otherwise(float otherwiseValue) {
return otherwise(FloatConstant.valueOf(otherwiseValue));
}
public NumberBinding otherwise(long otherwiseValue) {
return otherwise(LongConstant.valueOf(otherwiseValue));
}
public NumberBinding otherwise(int otherwiseValue) {
return otherwise(IntegerConstant.valueOf(otherwiseValue));
}
}
public NumberConditionBuilder then(final ObservableNumberValue thenValue) {
if (thenValue == null) {
throw new NullPointerException("Value needs to be specified");
}
return new NumberConditionBuilder(thenValue);
}
public NumberConditionBuilder then(double thenValue) {
return new NumberConditionBuilder(DoubleConstant.valueOf(thenValue));
}
public NumberConditionBuilder then(float thenValue) {
return new NumberConditionBuilder(FloatConstant.valueOf(thenValue));
}
public NumberConditionBuilder then(long thenValue) {
return new NumberConditionBuilder(LongConstant.valueOf(thenValue));
}
public NumberConditionBuilder then(int thenValue) {
return new NumberConditionBuilder(IntegerConstant.valueOf(thenValue));
}
private class BooleanCondition extends BooleanBinding {
private final ObservableBooleanValue trueResult;
private final boolean trueResultValue;
private final ObservableBooleanValue falseResult;
private final boolean falseResultValue;
private final InvalidationListener observer;
private BooleanCondition(final ObservableBooleanValue then, final ObservableBooleanValue otherwise) {
this.trueResult = then;
this.trueResultValue = false;
this.falseResult = otherwise;
this.falseResultValue = false;
this.observer = new WhenListener(this, condition, then, otherwise);
condition.addListener(observer);
then.addListener(observer);
otherwise.addListener(observer);
}
private BooleanCondition(final boolean then, final ObservableBooleanValue otherwise) {
this.trueResult = null;
this.trueResultValue = then;
this.falseResult = otherwise;
this.falseResultValue = false;
this.observer = new WhenListener(this, condition, null, otherwise);
condition.addListener(observer);
otherwise.addListener(observer);
}
private BooleanCondition(final ObservableBooleanValue then, final boolean otherwise) {
this.trueResult = then;
this.trueResultValue = false;
this.falseResult = null;
this.falseResultValue = otherwise;
this.observer = new WhenListener(this, condition, then, null);
condition.addListener(observer);
then.addListener(observer);
}
private BooleanCondition(final boolean then, final boolean otherwise) {
this.trueResult = null;
this.trueResultValue = then;
this.falseResult = null;
this.falseResultValue = otherwise;
this.observer = null;
super.bind(condition);
}
@Override
protected boolean computeValue() {
final boolean conditionValue = condition.get();
Logging.getLogger().finest("Condition of ternary binding expression was evaluated: {0}", conditionValue);
return conditionValue ? (trueResult != null ? trueResult.get() : trueResultValue)
: (falseResult != null ? falseResult.get() : falseResultValue);
}
@Override
public void dispose() {
if (observer == null) {
super.unbind(condition);
} else {
condition.removeListener(observer);
if (trueResult != null) {
trueResult.removeListener(observer);
}
if (falseResult != null) {
falseResult.removeListener(observer);
}
}
}
@Override
public ObservableList<ObservableValue<?>> getDependencies() {
assert condition != null;
final ObservableList<ObservableValue<?>> seq = FXCollections.<ObservableValue<?>> observableArrayList(condition);
if (trueResult != null) {
seq.add(trueResult);
}
if (falseResult != null) {
seq.add(falseResult);
}
return FXCollections.unmodifiableObservableList(seq);
}
}
public class BooleanConditionBuilder {
private ObservableBooleanValue trueResult;
private boolean trueResultValue;
private BooleanConditionBuilder(final ObservableBooleanValue thenValue) {
this.trueResult = thenValue;
}
private BooleanConditionBuilder(final boolean thenValue) {
this.trueResultValue = thenValue;
}
public BooleanBinding otherwise(final ObservableBooleanValue otherwiseValue) {
if (otherwiseValue == null) {
throw new NullPointerException("Value needs to be specified");
}
if (trueResult != null)
return new BooleanCondition(trueResult, otherwiseValue);
else
return new BooleanCondition(trueResultValue, otherwiseValue);
}
public BooleanBinding otherwise(final boolean otherwiseValue) {
if (trueResult != null)
return new BooleanCondition(trueResult, otherwiseValue);
else
return new BooleanCondition(trueResultValue, otherwiseValue);
}
}
public BooleanConditionBuilder then(final ObservableBooleanValue thenValue) {
if (thenValue == null) {
throw new NullPointerException("Value needs to be specified");
}
return new BooleanConditionBuilder(thenValue);
}
public BooleanConditionBuilder then(final boolean thenValue) {
return new BooleanConditionBuilder(thenValue);
}
private class StringCondition extends StringBinding {
private final ObservableStringValue trueResult;
private final String trueResultValue;
private final ObservableStringValue falseResult;
private final String falseResultValue;
private final InvalidationListener observer;
private StringCondition(final ObservableStringValue then, final ObservableStringValue otherwise) {
this.trueResult = then;
this.trueResultValue = "";
this.falseResult = otherwise;
this.falseResultValue = "";
this.observer = new WhenListener(this, condition, then, otherwise);
condition.addListener(observer);
then.addListener(observer);
otherwise.addListener(observer);
}
private StringCondition(final String then, final ObservableStringValue otherwise) {
this.trueResult = null;
this.trueResultValue = then;
this.falseResult = otherwise;
this.falseResultValue = "";
this.observer = new WhenListener(this, condition, null, otherwise);
condition.addListener(observer);
otherwise.addListener(observer);
}
private StringCondition(final ObservableStringValue then, final String otherwise) {
this.trueResult = then;
this.trueResultValue = "";
this.falseResult = null;
this.falseResultValue = otherwise;
this.observer = new WhenListener(this, condition, then, null);
condition.addListener(observer);
then.addListener(observer);
}
private StringCondition(final String then, final String otherwise) {
this.trueResult = null;
this.trueResultValue = then;
this.falseResult = null;
this.falseResultValue = otherwise;
this.observer = null;
super.bind(condition);
}
@Override
protected String computeValue() {
final boolean conditionValue = condition.get();
Logging.getLogger().finest("Condition of ternary binding expression was evaluated: {0}", conditionValue);
return conditionValue ? (trueResult != null ? trueResult.get() : trueResultValue)
: (falseResult != null ? falseResult.get() : falseResultValue);
}
@Override
public void dispose() {
if (observer == null) {
super.unbind(condition);
} else {
condition.removeListener(observer);
if (trueResult != null) {
trueResult.removeListener(observer);
}
if (falseResult != null) {
falseResult.removeListener(observer);
}
}
}
@Override
public ObservableList<ObservableValue<?>> getDependencies() {
assert condition != null;
final ObservableList<ObservableValue<?>> seq = FXCollections.<ObservableValue<?>> observableArrayList(condition);
if (trueResult != null) {
seq.add(trueResult);
}
if (falseResult != null) {
seq.add(falseResult);
}
return FXCollections.unmodifiableObservableList(seq);
}
}
public class StringConditionBuilder {
private ObservableStringValue trueResult;
private String trueResultValue;
private StringConditionBuilder(final ObservableStringValue thenValue) {
this.trueResult = thenValue;
}
private StringConditionBuilder(final String thenValue) {
this.trueResultValue = thenValue;
}
public StringBinding otherwise(final ObservableStringValue otherwiseValue) {
if (trueResult != null)
return new StringCondition(trueResult, otherwiseValue);
else
return new StringCondition(trueResultValue, otherwiseValue);
}
public StringBinding otherwise(final String otherwiseValue) {
if (trueResult != null)
return new StringCondition(trueResult, otherwiseValue);
else
return new StringCondition(trueResultValue, otherwiseValue);
}
}
public StringConditionBuilder then(final ObservableStringValue thenValue) {
if (thenValue == null) {
throw new NullPointerException("Value needs to be specified");
}
return new StringConditionBuilder(thenValue);
}
public StringConditionBuilder then(final String thenValue) {
return new StringConditionBuilder(thenValue);
}
private class ObjectCondition<T> extends ObjectBinding<T> {
private final ObservableObjectValue<T> trueResult;
private final T trueResultValue;
private final ObservableObjectValue<T> falseResult;
private final T falseResultValue;
private final InvalidationListener observer;
private ObjectCondition(final ObservableObjectValue<T> then, final ObservableObjectValue<T> otherwise) {
this.trueResult = then;
this.trueResultValue = null;
this.falseResult = otherwise;
this.falseResultValue = null;
this.observer = new WhenListener(this, condition, then, otherwise);
condition.addListener(observer);
then.addListener(observer);
otherwise.addListener(observer);
}
private ObjectCondition(final T then, final ObservableObjectValue<T> otherwise) {
this.trueResult = null;
this.trueResultValue = then;
this.falseResult = otherwise;
this.falseResultValue = null;
this.observer = new WhenListener(this, condition, null, otherwise);
condition.addListener(observer);
otherwise.addListener(observer);
}
private ObjectCondition(final ObservableObjectValue<T> then, final T otherwise) {
this.trueResult = then;
this.trueResultValue = null;
this.falseResult = null;
this.falseResultValue = otherwise;
this.observer = new WhenListener(this, condition, then, null);
condition.addListener(observer);
then.addListener(observer);
}
private ObjectCondition(final T then, final T otherwise) {
this.trueResult = null;
this.trueResultValue = then;
this.falseResult = null;
this.falseResultValue = otherwise;
this.observer = null;
super.bind(condition);
}
@Override
protected T computeValue() {
final boolean conditionValue = condition.get();
Logging.getLogger().finest("Condition of ternary binding expression was evaluated: {0}", conditionValue);
return conditionValue ? (trueResult != null ? trueResult.get() : trueResultValue)
: (falseResult != null ? falseResult.get() : falseResultValue);
}
@Override
public void dispose() {
if (observer == null) {
super.unbind(condition);
} else {
condition.removeListener(observer);
if (trueResult != null) {
trueResult.removeListener(observer);
}
if (falseResult != null) {
falseResult.removeListener(observer);
}
}
}
@Override
public ObservableList<ObservableValue<?>> getDependencies() {
assert condition != null;
final ObservableList<ObservableValue<?>> seq = FXCollections.<ObservableValue<?>> observableArrayList(condition);
if (trueResult != null) {
seq.add(trueResult);
}
if (falseResult != null) {
seq.add(falseResult);
}
return FXCollections.unmodifiableObservableList(seq);
}
}
public class ObjectConditionBuilder<T> {
private ObservableObjectValue<T> trueResult;
private T trueResultValue;
private ObjectConditionBuilder(final ObservableObjectValue<T> thenValue) {
this.trueResult = thenValue;
}
private ObjectConditionBuilder(final T thenValue) {
this.trueResultValue = thenValue;
}
public ObjectBinding<T> otherwise(final ObservableObjectValue<T> otherwiseValue) {
if (otherwiseValue == null) {
throw new NullPointerException("Value needs to be specified");
}
if (trueResult != null)
return new ObjectCondition<T>(trueResult, otherwiseValue);
else
return new ObjectCondition<T>(trueResultValue, otherwiseValue);
}
public ObjectBinding<T> otherwise(final T otherwiseValue) {
if (trueResult != null)
return new ObjectCondition<T>(trueResult, otherwiseValue);
else
return new ObjectCondition<T>(trueResultValue, otherwiseValue);
}
}
public <T> ObjectConditionBuilder<T> then(final ObservableObjectValue<T> thenValue) {
if (thenValue == null) {
throw new NullPointerException("Value needs to be specified");
}
return new ObjectConditionBuilder<T>(thenValue);
}
public <T> ObjectConditionBuilder<T> then(final T thenValue) {
return new ObjectConditionBuilder<T>(thenValue);
}
}

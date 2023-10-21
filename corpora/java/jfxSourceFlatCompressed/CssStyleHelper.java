package javafx.scene;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import com.sun.javafx.css.CascadingStyle;
import javafx.css.CssMetaData;
import javafx.css.CssParser;
import javafx.css.FontCssMetaData;
import javafx.css.ParsedValue;
import javafx.css.PseudoClass;
import javafx.css.Rule;
import javafx.css.Selector;
import javafx.css.Style;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.Stylesheet;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import com.sun.javafx.css.CalculatedValue;
import com.sun.javafx.css.ParsedValueImpl;
import com.sun.javafx.css.PseudoClassState;
import com.sun.javafx.css.StyleCache;
import com.sun.javafx.css.StyleCacheEntry;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.css.StyleMap;
import javafx.css.converter.FontConverter;
import com.sun.javafx.util.Logging;
import com.sun.javafx.util.Utils;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import static com.sun.javafx.css.CalculatedValue.*;
final class CssStyleHelper {
private static final PlatformLogger LOGGER = com.sun.javafx.util.Logging.getCSSLogger();
private CssStyleHelper() {
this.triggerStates = new PseudoClassState();
}
static CssStyleHelper createStyleHelper(final Node node) {
Styleable parent = node;
int depth = 0;
while(parent != null) {
depth++;
parent = parent.getStyleableParent();
}
final PseudoClassState[] triggerStates = new PseudoClassState[depth];
final StyleMap styleMap =
StyleManager.getInstance().findMatchingStyles(node, node.getSubScene(), triggerStates);
if ( canReuseStyleHelper(node, styleMap) ) {
if (node.styleHelper.cacheContainer != null && node.styleHelper.isUserSetFont(node)) {
node.styleHelper.cacheContainer.fontSizeCache.clear();
}
node.styleHelper.cacheContainer.forceSlowpath = true;
node.styleHelper.triggerStates.addAll(triggerStates[0]);
updateParentTriggerStates(node, depth, triggerStates);
return node.styleHelper;
}
if (styleMap == null || styleMap.isEmpty()) {
boolean mightInherit = false;
final List<CssMetaData<? extends Styleable, ?>> props = node.getCssMetaData();
final int pMax = props != null ? props.size() : 0;
for (int p=0; p<pMax; p++) {
final CssMetaData<? extends Styleable, ?> prop = props.get(p);
if (prop.isInherits()) {
mightInherit = true;
break;
}
}
if (mightInherit == false) {
if (node.styleHelper != null) {
node.styleHelper.resetToInitialValues(node);
}
return null;
}
}
final CssStyleHelper helper = new CssStyleHelper();
helper.triggerStates.addAll(triggerStates[0]);
updateParentTriggerStates(node, depth, triggerStates);
helper.cacheContainer = new CacheContainer(node, styleMap, depth);
helper.firstStyleableAncestor = new WeakReference<>(findFirstStyleableAncestor(node));
if (node.styleHelper != null) {
node.styleHelper.resetToInitialValues(node);
}
return helper;
}
private static void updateParentTriggerStates(Styleable styleable, int depth, PseudoClassState[] triggerStates) {
Styleable parent = styleable.getStyleableParent();
for(int n=1; n<depth; n++) {
if (parent instanceof Node == false) {
parent=parent.getStyleableParent();
continue;
}
Node parentNode = (Node)parent;
final PseudoClassState triggerState = triggerStates[n];
if (triggerState != null && triggerState.size() > 0) {
if (parentNode.styleHelper == null) {
parentNode.styleHelper = new CssStyleHelper();
parentNode.styleHelper.firstStyleableAncestor = new WeakReference(findFirstStyleableAncestor(parentNode)) ;
}
parentNode.styleHelper.triggerStates.addAll(triggerState);
}
parent=parent.getStyleableParent();
}
}
private boolean isUserSetFont(Styleable node) {
if (node == null) return false;
CssMetaData<Styleable, Font> fontCssMetaData = cacheContainer != null ? cacheContainer.fontProp : null;
if (fontCssMetaData != null) {
StyleableProperty<Font> fontStyleableProperty = fontCssMetaData != null ? fontCssMetaData.getStyleableProperty(node) : null;
if (fontStyleableProperty != null && fontStyleableProperty.getStyleOrigin() == StyleOrigin.USER) return true;
}
Styleable styleableParent = firstStyleableAncestor.get();
CssStyleHelper parentStyleHelper = getStyleHelper(firstStyleableAncestor.get());
if (parentStyleHelper != null) {
return parentStyleHelper.isUserSetFont(styleableParent);
} else {
return false;
}
}
private static CssStyleHelper getStyleHelper(Node n) {
return (n != null)? n.styleHelper : null;
}
private static Node findFirstStyleableAncestor(Styleable st) {
Node ancestor = null;
Styleable parent = st.getStyleableParent();
while (parent != null) {
if (parent instanceof Node) {
if (((Node) parent).styleHelper != null) {
ancestor = (Node) parent;
break;
}
}
parent = parent.getStyleableParent();
}
return ancestor;
}
private static boolean isTrue(WritableValue<Boolean> booleanProperty) {
return booleanProperty != null && booleanProperty.getValue();
}
private static void setTrue(WritableValue<Boolean> booleanProperty) {
if (booleanProperty != null) booleanProperty.setValue(true);
}
private static boolean canReuseStyleHelper(final Node node, final StyleMap styleMap) {
if (node == null || node.styleHelper == null) {
return false;
}
if (styleMap == null) {
return false;
}
StyleMap currentMap = node.styleHelper.getStyleMap(node);
if (currentMap != styleMap) {
return false;
}
node.styleHelper.firstStyleableAncestor = new WeakReference<>(findFirstStyleableAncestor(node));
if (node.styleHelper.cacheContainer == null) {
return true;
}
Styleable parent = node.getStyleableParent();
if (parent == null) {
return true;
}
CssStyleHelper parentHelper = getStyleHelper(node.styleHelper.firstStyleableAncestor.get());
if (parentHelper != null && parentHelper.cacheContainer != null) {
int[] parentIds = parentHelper.cacheContainer.styleCacheKey.getStyleMapIds();
int[] nodeIds = node.styleHelper.cacheContainer.styleCacheKey.getStyleMapIds();
if (parentIds.length == nodeIds.length - 1) {
boolean isSame = true;
for (int i = 0; i < parentIds.length; i++) {
if (nodeIds[i + 1] != parentIds[i]) {
isSame = false;
break;
}
}
return isSame;
}
}
return false;
}
private static final WeakReference<Node> EMPTY_NODE = new WeakReference<>(null);
private WeakReference<Node> firstStyleableAncestor = EMPTY_NODE;
private CacheContainer cacheContainer;
private final static class CacheContainer {
private CacheContainer(
Node node,
final StyleMap styleMap,
int depth) {
int ctr = 0;
int[] smapIds = new int[depth];
smapIds[ctr++] = this.smapId = styleMap.getId();
Styleable parent = node.getStyleableParent();
for(int d=1; d<depth; d++) {
if ( parent instanceof Node) {
Node parentNode = (Node)parent;
final CssStyleHelper helper = parentNode.styleHelper;
if (helper != null && helper.cacheContainer != null) {
smapIds[ctr++] = helper.cacheContainer.smapId;
}
}
parent = parent.getStyleableParent();
}
this.styleCacheKey = new StyleCache.Key(smapIds, ctr);
CssMetaData<Styleable,Font> styleableFontProperty = null;
final List<CssMetaData<? extends Styleable, ?>> props = node.getCssMetaData();
final int pMax = props != null ? props.size() : 0;
for (int p=0; p<pMax; p++) {
final CssMetaData<? extends Styleable, ?> prop = props.get(p);
if ("-fx-font".equals(prop.getProperty())) {
styleableFontProperty = (CssMetaData<Styleable, Font>) prop;
break;
}
}
this.fontProp = styleableFontProperty;
this.fontSizeCache = new HashMap<>();
this.cssSetProperties = new HashMap<>();
}
private StyleMap getStyleMap(Styleable styleable) {
if (styleable != null) {
SubScene subScene = (styleable instanceof Node) ? ((Node) styleable).getSubScene() : null;
return StyleManager.getInstance().getStyleMap(styleable, subScene, smapId);
} else {
return StyleMap.EMPTY_MAP;
}
}
private final StyleCache.Key styleCacheKey;
private final CssMetaData<Styleable,Font> fontProp;
private final int smapId;
private final Map<StyleCacheEntry.Key, CalculatedValue> fontSizeCache;
private final Map<CssMetaData, CalculatedValue> cssSetProperties;
private boolean forceSlowpath = false;
}
private boolean resetInProgress = false;
private void resetToInitialValues(final Styleable styleable) {
if (cacheContainer == null ||
cacheContainer.cssSetProperties == null ||
cacheContainer.cssSetProperties.isEmpty()) return;
resetInProgress = true;
Set<Entry<CssMetaData, CalculatedValue>> entrySet = new HashSet<>(cacheContainer.cssSetProperties.entrySet());
cacheContainer.cssSetProperties.clear();
for (Entry<CssMetaData, CalculatedValue> resetValues : entrySet) {
final CssMetaData metaData = resetValues.getKey();
final StyleableProperty styleableProperty = metaData.getStyleableProperty(styleable);
final StyleOrigin styleOrigin = styleableProperty.getStyleOrigin();
if (styleOrigin != null && styleOrigin != StyleOrigin.USER) {
final CalculatedValue calculatedValue = resetValues.getValue();
styleableProperty.applyStyle(calculatedValue.getOrigin(), calculatedValue.getValue());
}
}
resetInProgress = false;
}
private StyleMap getStyleMap(Styleable styleable) {
if (cacheContainer == null || styleable == null) return null;
return cacheContainer.getStyleMap(styleable);
}
private PseudoClassState triggerStates = new PseudoClassState();
boolean pseudoClassStateChanged(PseudoClass pseudoClass) {
return triggerStates.contains(pseudoClass);
}
private Set<PseudoClass>[] getTransitionStates(final Node node) {
if (cacheContainer == null) return null;
int depth = 0;
Node parent = node;
while (parent != null) {
depth += 1;
parent = parent.getParent();
}
final Set<PseudoClass>[] retainedStates = new PseudoClassState[depth];
int count = 0;
parent = node;
while (parent != null) {
final CssStyleHelper helper = (parent instanceof Node) ? parent.styleHelper : null;
if (helper != null) {
final Set<PseudoClass> pseudoClassState = parent.pseudoClassStates;
retainedStates[count] = new PseudoClassState();
retainedStates[count].addAll(pseudoClassState);
retainedStates[count].retainAll(helper.triggerStates);
count += 1;
}
parent = parent.getParent();
}
final Set<PseudoClass>[] transitionStates = new PseudoClassState[count];
System.arraycopy(retainedStates, 0, transitionStates, 0, count);
return transitionStates;
}
void recalculateRelativeSizeProperties(final Node node, Font fontForRelativeSizes) {
if (transitionStateInProgress || resetInProgress) {
return;
}
if (cacheContainer == null) {
return;
}
final StyleMap styleMap = getStyleMap(node);
if (styleMap == null) {
return;
}
final boolean inheritOnly = styleMap.isEmpty();
final Set<PseudoClass>[] transitionStates = getTransitionStates(node);
CalculatedValue cachedFont = new CalculatedValue(fontForRelativeSizes, null, false);
final List<CssMetaData<? extends Styleable, ?>> styleables = node.getCssMetaData();
final int numStyleables = styleables.size();
for (int n = 0; n < numStyleables; n++) {
@SuppressWarnings("unchecked")
final CssMetaData<Styleable,Object> cssMetaData =
(CssMetaData<Styleable,Object>)styleables.get(n);
if (inheritOnly && cssMetaData.isInherits() == false) {
continue;
}
if (!cssMetaData.isSettable(node)) {
continue;
}
final String property = cssMetaData.getProperty();
boolean isFontProperty = property.equals("-fx-font") || property.equals("-fx-font-size");
if (isFontProperty) {
continue;
}
CascadingStyle style = getStyle(node, property, styleMap, transitionStates[0]);
if (style != null) {
final ParsedValue cssValue = style.getParsedValue();
ObjectProperty<StyleOrigin> whence = new SimpleObjectProperty<>(style.getOrigin());
ParsedValue resolved = resolveLookups(node, cssValue, styleMap, transitionStates[0], whence, new HashSet<>());
boolean isRelative = ParsedValueImpl.containsFontRelativeSize(resolved, false);
if (!isRelative) {
continue;
}
} else {
final List<CssMetaData<? extends Styleable, ?>> subProperties = cssMetaData.getSubProperties();
final int numSubProperties = (subProperties != null) ? subProperties.size() : 0;
if (numSubProperties == 0) {
continue;
} else {
}
}
CalculatedValue calculatedValue = lookup(node, cssMetaData, styleMap, transitionStates[0],
node, cachedFont);
if (calculatedValue == null || calculatedValue == SKIP) {
continue;
}
try {
StyleableProperty styleableProperty = cssMetaData.getStyleableProperty(node);
final StyleOrigin originOfCurrentValue = styleableProperty.getStyleOrigin();
final StyleOrigin originOfCalculatedValue = calculatedValue.getOrigin();
if (originOfCalculatedValue == null) {
continue;
}
if (originOfCurrentValue == StyleOrigin.USER) {
if (originOfCalculatedValue == StyleOrigin.USER_AGENT) {
continue;
}
}
final Object value = calculatedValue.getValue();
final Object currentValue = styleableProperty.getValue();
if ((originOfCurrentValue != originOfCalculatedValue)
|| (currentValue != null
? currentValue.equals(value) == false
: value != null)) {
if (LOGGER.isLoggable(Level.FINER)) {
LOGGER.finer(property + ", call applyStyle: " + styleableProperty + ", value =" +
String.valueOf(value) + ", originOfCalculatedValue=" + originOfCalculatedValue);
}
styleableProperty.applyStyle(originOfCalculatedValue, value);
CalculatedValue initialValue = new CalculatedValue(currentValue, originOfCurrentValue, true);
cacheContainer.cssSetProperties.put(cssMetaData, initialValue);
}
} catch (Exception e) {
StyleableProperty styleableProperty = cssMetaData.getStyleableProperty(node);
final String msg = String.format("Failed to recalculate and set css [%s] on [%s] due to '%s'\n",
cssMetaData.getProperty(), styleableProperty, e.getMessage());
PlatformLogger logger = Logging.getCSSLogger();
if (logger.isLoggable(Level.WARNING)) {
logger.warning(msg);
}
}
}
}
private boolean transitionStateInProgress = false;
void transitionToState(final Node node) {
if (cacheContainer == null) {
return;
}
final StyleMap styleMap = getStyleMap(node);
if (styleMap == null) {
cacheContainer = null;
node.reapplyCSS();
return;
}
final boolean inheritOnly = styleMap.isEmpty();
final StyleCache sharedCache = StyleManager.getInstance().getSharedCache(node, node.getSubScene(), cacheContainer.styleCacheKey);
if (sharedCache == null) {
cacheContainer = null;
node.reapplyCSS();
return;
}
final Set<PseudoClass>[] transitionStates = getTransitionStates(node);
final StyleCacheEntry.Key fontCacheKey = new StyleCacheEntry.Key(transitionStates, Font.getDefault());
CalculatedValue cachedFont = cacheContainer.fontSizeCache.get(fontCacheKey);
if (cachedFont == null) {
cachedFont = lookupFont(node, "-fx-font", styleMap, cachedFont);
if (cachedFont == SKIP) cachedFont = getCachedFont(node.getStyleableParent());
if (cachedFont == null) cachedFont = new CalculatedValue(Font.getDefault(), null, false);
cacheContainer.fontSizeCache.put(fontCacheKey,cachedFont);
}
final Font fontForRelativeSizes = (Font)cachedFont.getValue();
final StyleCacheEntry.Key cacheEntryKey = new StyleCacheEntry.Key(transitionStates, fontForRelativeSizes);
StyleCacheEntry cacheEntry = sharedCache.getStyleCacheEntry(cacheEntryKey);
final boolean fastpath = cacheEntry != null;
if (cacheEntry == null) {
cacheEntry = new StyleCacheEntry();
sharedCache.addStyleCacheEntry(cacheEntryKey, cacheEntry);
}
final List<CssMetaData<? extends Styleable, ?>> styleables = node.getCssMetaData();
final int max = styleables.size();
final boolean isForceSlowpath = cacheContainer.forceSlowpath;
cacheContainer.forceSlowpath = false;
transitionStateInProgress = true;
for(int n=0; n<max; n++) {
@SuppressWarnings("unchecked")
final CssMetaData<Styleable,Object> cssMetaData =
(CssMetaData<Styleable,Object>)styleables.get(n);
if (inheritOnly && cssMetaData.isInherits() == false) {
continue;
}
if (!cssMetaData.isSettable(node)) continue;
final String property = cssMetaData.getProperty();
CalculatedValue calculatedValue = cacheEntry.get(property);
final boolean forceSlowpath =
fastpath && calculatedValue == null && isForceSlowpath;
final boolean addToCache =
(!fastpath && calculatedValue == null) || forceSlowpath;
if (fastpath && !forceSlowpath) {
if (calculatedValue == SKIP) {
continue;
}
} else if (calculatedValue == null) {
calculatedValue = lookup(node, cssMetaData, styleMap, transitionStates[0],
node, cachedFont);
if (calculatedValue == null) {
assert false : "lookup returned null for " + property;
continue;
}
}
try {
if (calculatedValue == null || calculatedValue == SKIP) {
CalculatedValue initialValue = cacheContainer.cssSetProperties.get(cssMetaData);
if (initialValue != null) {
StyleableProperty styleableProperty = cssMetaData.getStyleableProperty(node);
if (styleableProperty.getStyleOrigin() != StyleOrigin.USER) {
styleableProperty.applyStyle(initialValue.getOrigin(), initialValue.getValue());
}
}
continue;
}
if (addToCache) {
cacheEntry.put(property, calculatedValue);
}
StyleableProperty styleableProperty = cssMetaData.getStyleableProperty(node);
final StyleOrigin originOfCurrentValue = styleableProperty.getStyleOrigin();
final StyleOrigin originOfCalculatedValue = calculatedValue.getOrigin();
if (originOfCalculatedValue == null) {
assert false : styleableProperty.toString();
continue;
}
if (originOfCurrentValue == StyleOrigin.USER) {
if (originOfCalculatedValue == StyleOrigin.USER_AGENT) {
continue;
}
}
final Object value = calculatedValue.getValue();
final Object currentValue = styleableProperty.getValue();
if ((originOfCurrentValue != originOfCalculatedValue)
|| (currentValue != null
? currentValue.equals(value) == false
: value != null)) {
if (LOGGER.isLoggable(Level.FINER)) {
LOGGER.finer(property + ", call applyStyle: " + styleableProperty + ", value =" +
String.valueOf(value) + ", originOfCalculatedValue=" + originOfCalculatedValue);
}
styleableProperty.applyStyle(originOfCalculatedValue, value);
if (cacheContainer.cssSetProperties.containsKey(cssMetaData) == false) {
CalculatedValue initialValue = new CalculatedValue(currentValue, originOfCurrentValue, false);
cacheContainer.cssSetProperties.put(cssMetaData, initialValue);
}
}
} catch (Exception e) {
StyleableProperty styleableProperty = cssMetaData.getStyleableProperty(node);
final String msg = String.format("Failed to set css [%s] on [%s] due to '%s'\n",
cssMetaData.getProperty(), styleableProperty, e.getMessage());
List<CssParser.ParseError> errors = null;
if ((errors = StyleManager.getErrors()) != null) {
final CssParser.ParseError error = new CssParser.ParseError.PropertySetError(cssMetaData, node, msg);
errors.add(error);
}
PlatformLogger logger = Logging.getCSSLogger();
if (logger.isLoggable(Level.WARNING)) {
logger.warning(msg);
}
cacheEntry.put(property, SKIP);
CalculatedValue cachedValue = null;
if (cacheContainer != null && cacheContainer.cssSetProperties != null) {
cachedValue = cacheContainer.cssSetProperties.get(cssMetaData);
}
Object value = (cachedValue != null) ? cachedValue.getValue() : cssMetaData.getInitialValue(node);
StyleOrigin origin = (cachedValue != null) ? cachedValue.getOrigin() : null;
try {
styleableProperty.applyStyle(origin, value);
} catch (Exception ebad) {
if (logger.isLoggable(Level.SEVERE)) {
logger.severe(String.format("Could not reset [%s] on [%s] due to %s\n" ,
cssMetaData.getProperty(), styleableProperty, e.getMessage()));
}
}
}
}
transitionStateInProgress = false;
}
private CascadingStyle getStyle(final Styleable styleable, final String property, final StyleMap styleMap, final Set<PseudoClass> states){
if (styleMap == null || styleMap.isEmpty()) return null;
final Map<String, List<CascadingStyle>> cascadingStyleMap = styleMap.getCascadingStyles();
if (cascadingStyleMap == null || cascadingStyleMap.isEmpty()) return null;
List<CascadingStyle> styles = cascadingStyleMap.get(property);
if ((styles == null) || styles.isEmpty()) return null;
CascadingStyle style = null;
final int max = (styles == null) ? 0 : styles.size();
for (int i=0; i<max; i++) {
final CascadingStyle s = styles.get(i);
final Selector sel = s == null ? null : s.getSelector();
if (sel == null) continue;
if (sel.stateMatches(styleable, states)) {
style = s;
break;
}
}
return style;
}
private CalculatedValue lookup(final Styleable styleable,
final CssMetaData cssMetaData,
final StyleMap styleMap,
final Set<PseudoClass> states,
final Styleable originatingStyleable,
final CalculatedValue cachedFont) {
if (cssMetaData.getConverter() == FontConverter.getInstance()) {
return lookupFont(styleable, cssMetaData.getProperty(), styleMap, cachedFont);
}
final String property = cssMetaData.getProperty();
CascadingStyle style = getStyle(styleable, property, styleMap, states);
final List<CssMetaData<? extends Styleable, ?>> subProperties = cssMetaData.getSubProperties();
final int numSubProperties = (subProperties != null) ? subProperties.size() : 0;
if (style == null) {
if (numSubProperties == 0) {
return handleNoStyleFound(styleable, cssMetaData,
styleMap, states, originatingStyleable, cachedFont);
} else {
Map<CssMetaData,Object> subs = null;
StyleOrigin origin = null;
boolean isRelative = false;
for (int i=0; i<numSubProperties; i++) {
CssMetaData subkey = subProperties.get(i);
CalculatedValue constituent =
lookup(styleable, subkey, styleMap, states,
originatingStyleable, cachedFont);
if (constituent != SKIP) {
if (subs == null) {
subs = new HashMap<>();
}
subs.put(subkey, constituent.getValue());
if ((origin != null && constituent.getOrigin() != null)
? origin.compareTo(constituent.getOrigin()) < 0
: constituent.getOrigin() != null) {
origin = constituent.getOrigin();
}
isRelative = isRelative || constituent.isRelative();
}
}
if (subs == null || subs.isEmpty()) {
return handleNoStyleFound(styleable, cssMetaData,
styleMap, states, originatingStyleable, cachedFont);
}
try {
final StyleConverter keyType = cssMetaData.getConverter();
Object ret = keyType.convert(subs);
return new CalculatedValue(ret, origin, isRelative);
} catch (ClassCastException cce) {
final String msg = formatExceptionMessage(styleable, cssMetaData, null, cce);
List<CssParser.ParseError> errors = null;
if ((errors = StyleManager.getErrors()) != null) {
final CssParser.ParseError error = new CssParser.ParseError.PropertySetError(cssMetaData, styleable, msg);
errors.add(error);
}
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(msg);
LOGGER.fine("caught: ", cce);
LOGGER.fine("styleable = " + cssMetaData);
LOGGER.fine("node = " + styleable.toString());
}
return SKIP;
}
}
} else {
if (style.getOrigin() == StyleOrigin.USER_AGENT) {
StyleableProperty styleableProperty = cssMetaData.getStyleableProperty(originatingStyleable);
if (styleableProperty != null && styleableProperty.getStyleOrigin() == StyleOrigin.USER) {
return SKIP;
}
}
final ParsedValue cssValue = style.getParsedValue();
if (cssValue != null && "inherit".equals(cssValue.getValue())) {
style = getInheritedStyle(styleable, property);
if (style == null) return SKIP;
}
}
return calculateValue(style, styleable, cssMetaData, styleMap, states,
originatingStyleable, cachedFont);
}
private CalculatedValue handleNoStyleFound(final Styleable styleable,
final CssMetaData cssMetaData,
final StyleMap styleMap, Set<PseudoClass> pseudoClassStates, Styleable originatingStyleable,
final CalculatedValue cachedFont) {
if (cssMetaData.isInherits()) {
StyleableProperty styleableProperty = cssMetaData.getStyleableProperty(styleable);
StyleOrigin origin = styleableProperty != null ? styleableProperty.getStyleOrigin() : null;
if (origin == StyleOrigin.USER) {
return SKIP;
}
CascadingStyle style = getInheritedStyle(styleable, cssMetaData.getProperty());
if (style == null) return SKIP;
CalculatedValue cv =
calculateValue(style, styleable, cssMetaData,
styleMap, pseudoClassStates, originatingStyleable,
cachedFont);
return cv;
} else {
return SKIP;
}
}
private CascadingStyle getInheritedStyle(
final Styleable styleable,
final String property) {
Styleable parent = ((Node)styleable).styleHelper.firstStyleableAncestor.get();
CssStyleHelper parentStyleHelper = getStyleHelper((Node) parent);
if (parent != null && parentStyleHelper != null) {
StyleMap parentStyleMap = parentStyleHelper.getStyleMap(parent);
Set<PseudoClass> transitionStates = ((Node)parent).pseudoClassStates;
CascadingStyle cascadingStyle = parentStyleHelper.getStyle(parent, property, parentStyleMap, transitionStates);
if (cascadingStyle != null) {
final ParsedValue cssValue = cascadingStyle.getParsedValue();
if ("inherit".equals(cssValue.getValue())) {
return getInheritedStyle(parent, property);
}
return cascadingStyle;
}
}
return null;
}
private static final Set<PseudoClass> NULL_PSEUDO_CLASS_STATE = null;
private CascadingStyle resolveRef(final Styleable styleable, final String property, final StyleMap styleMap, final Set<PseudoClass> states) {
final CascadingStyle style = getStyle(styleable, property, styleMap, states);
if (style != null) {
return style;
} else {
if (states != null && states.size() > 0) {
return resolveRef(styleable,property, styleMap, NULL_PSEUDO_CLASS_STATE);
} else {
Styleable styleableParent = ((Node)styleable).styleHelper.firstStyleableAncestor.get();
CssStyleHelper parentStyleHelper = getStyleHelper((Node) styleableParent);
if (styleableParent == null || parentStyleHelper == null) {
return null;
}
StyleMap parentStyleMap = parentStyleHelper.getStyleMap(styleableParent);
Set<PseudoClass> styleableParentPseudoClassStates =
styleableParent instanceof Node
? ((Node)styleableParent).pseudoClassStates
: styleable.getPseudoClassStates();
return parentStyleHelper.resolveRef(styleableParent, property,
parentStyleMap, styleableParentPseudoClassStates);
}
}
}
private ParsedValue resolveLookups(
final Styleable styleable,
final ParsedValue parsedValue,
final StyleMap styleMap, Set<PseudoClass> states,
final ObjectProperty<StyleOrigin> whence,
Set<ParsedValue> resolves) {
if (parsedValue.isLookup()) {
final Object val = parsedValue.getValue();
if (val instanceof String) {
final String sval = ((String) val).toLowerCase(Locale.ROOT);
CascadingStyle resolved =
resolveRef(styleable, sval, styleMap, states);
if (resolved != null) {
if (resolves.contains(resolved.getParsedValue())) {
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning("Loop detected in " + resolved.getRule().toString() + " while resolving '" + sval + "'");
}
throw new IllegalArgumentException("Loop detected in " + resolved.getRule().toString() + " while resolving '" + sval + "'");
} else {
resolves.add(parsedValue);
}
final StyleOrigin wOrigin = whence.get();
final StyleOrigin rOrigin = resolved.getOrigin();
if (rOrigin != null && (wOrigin == null || wOrigin.compareTo(rOrigin) < 0)) {
whence.set(rOrigin);
}
ParsedValue pv = resolveLookups(styleable, resolved.getParsedValue(), styleMap, states, whence, resolves);
if (resolves != null) {
resolves.remove(parsedValue);
}
return pv;
}
}
}
if (!parsedValue.isContainsLookups()) {
return parsedValue;
}
final Object val = parsedValue.getValue();
if (val instanceof ParsedValue[][]) {
final ParsedValue[][] layers = (ParsedValue[][])val;
ParsedValue[][] resolved = new ParsedValue[layers.length][0];
for (int l=0; l<layers.length; l++) {
resolved[l] = new ParsedValue[layers[l].length];
for (int ll=0; ll<layers[l].length; ll++) {
if (layers[l][ll] == null) continue;
resolved[l][ll] =
resolveLookups(styleable, layers[l][ll], styleMap, states, whence, resolves);
}
}
resolves.clear();
return new ParsedValueImpl(resolved, parsedValue.getConverter(), false);
} else if (val instanceof ParsedValueImpl[]) {
final ParsedValue[] layer = (ParsedValue[])val;
ParsedValue[] resolved = new ParsedValue[layer.length];
for (int l=0; l<layer.length; l++) {
if (layer[l] == null) continue;
resolved[l] =
resolveLookups(styleable, layer[l], styleMap, states, whence, resolves);
}
resolves.clear();
return new ParsedValueImpl(resolved, parsedValue.getConverter(), false);
}
return parsedValue;
}
private String getUnresolvedLookup(final ParsedValue resolved) {
Object value = resolved.getValue();
if (resolved.isLookup() && value instanceof String) {
return (String)value;
}
if (value instanceof ParsedValue[][]) {
final ParsedValue[][] layers = (ParsedValue[][])value;
for (int l=0; l<layers.length; l++) {
for (int ll=0; ll<layers[l].length; ll++) {
if (layers[l][ll] == null) continue;
String unresolvedLookup = getUnresolvedLookup(layers[l][ll]);
if (unresolvedLookup != null) return unresolvedLookup;
}
}
} else if (value instanceof ParsedValue[]) {
final ParsedValue[] layer = (ParsedValue[])value;
for (int l=0; l<layer.length; l++) {
if (layer[l] == null) continue;
String unresolvedLookup = getUnresolvedLookup(layer[l]);
if (unresolvedLookup != null) return unresolvedLookup;
}
}
return null;
}
private String formatUnresolvedLookupMessage(Styleable styleable, CssMetaData cssMetaData, Style style, ParsedValue resolved, ClassCastException cce) {
String missingLookup = resolved != null && resolved.isContainsLookups() ? getUnresolvedLookup(resolved) : null;
StringBuilder sbuf = new StringBuilder();
if (missingLookup != null) {
sbuf.append("Could not resolve '")
.append(missingLookup)
.append("'")
.append(" while resolving lookups for '")
.append(cssMetaData.getProperty())
.append("'");
} else {
sbuf.append("Caught '")
.append(cce)
.append("'")
.append(" while converting value for '")
.append(cssMetaData.getProperty())
.append("'");
}
final Rule rule = style != null ? style.getDeclaration().getRule(): null;
final Stylesheet stylesheet = rule != null ? rule.getStylesheet() : null;
final String url = stylesheet != null ? stylesheet.getUrl() : null;
if (url != null) {
sbuf.append(" from rule '")
.append(style.getSelector())
.append("' in stylesheet ").append(url);
} else if (stylesheet != null && StyleOrigin.INLINE == stylesheet.getOrigin()) {
sbuf.append(" from inline style on " )
.append(styleable.toString());
}
return sbuf.toString();
}
private String formatExceptionMessage(Styleable styleable, CssMetaData cssMetaData, Style style, Exception e) {
StringBuilder sbuf = new StringBuilder();
sbuf.append("Caught ")
.append(String.valueOf(e));
if (cssMetaData != null) {
sbuf.append("'")
.append(" while calculating value for '")
.append(cssMetaData.getProperty())
.append("'");
}
if (style != null) {
final Rule rule = style.getDeclaration().getRule();
final Stylesheet stylesheet = rule != null ? rule.getStylesheet() : null;
final String url = stylesheet != null ? stylesheet.getUrl() : null;
if (url != null) {
sbuf.append(" from rule '")
.append(style.getSelector())
.append("' in stylesheet ").append(url);
} else if (styleable != null && stylesheet != null && StyleOrigin.INLINE == stylesheet.getOrigin()) {
sbuf.append(" from inline style on " )
.append(styleable.toString());
} else {
sbuf.append(" from style '")
.append(String.valueOf(style))
.append("'");
}
}
return sbuf.toString();
}
private CalculatedValue calculateValue(
final CascadingStyle style,
final Styleable styleable,
final CssMetaData cssMetaData,
final StyleMap styleMap, final Set<PseudoClass> states,
final Styleable originatingStyleable,
final CalculatedValue fontFromCacheEntry) {
final ParsedValue cssValue = style.getParsedValue();
if (cssValue != null && !("null".equals(cssValue.getValue()) || "none".equals(cssValue.getValue()))) {
ParsedValue resolved = null;
try {
ObjectProperty<StyleOrigin> whence = new SimpleObjectProperty<>(style.getOrigin());
resolved = resolveLookups(styleable, cssValue, styleMap, states, whence, new HashSet<>());
final String property = cssMetaData.getProperty();
Object val = null;
boolean isFontProperty =
"-fx-font".equals(property) ||
"-fx-font-size".equals(property);
boolean isRelative = ParsedValueImpl.containsFontRelativeSize(resolved, isFontProperty);
Font fontForFontRelativeSizes = null;
if (isRelative && isFontProperty &&
(fontFromCacheEntry == null || fontFromCacheEntry.isRelative())) {
Styleable parent = styleable;
CalculatedValue childsCachedFont = fontFromCacheEntry;
do {
CalculatedValue parentsCachedFont = getCachedFont(parent.getStyleableParent());
if (parentsCachedFont != null) {
if (parentsCachedFont.isRelative()) {
if (childsCachedFont == null || parentsCachedFont.equals(childsCachedFont)) {
childsCachedFont = parentsCachedFont;
} else {
fontForFontRelativeSizes = (Font)parentsCachedFont.getValue();
}
} else {
fontForFontRelativeSizes = (Font)parentsCachedFont.getValue();
}
}
} while(fontForFontRelativeSizes == null &&
(parent = parent.getStyleableParent()) != null);
}
if (fontForFontRelativeSizes == null) {
if (fontFromCacheEntry != null && (!fontFromCacheEntry.isRelative() || !isFontProperty)) {
fontForFontRelativeSizes = (Font)fontFromCacheEntry.getValue();
} else {
fontForFontRelativeSizes = Font.getDefault();
}
}
final StyleConverter cssMetaDataConverter = cssMetaData.getConverter();
if (cssMetaDataConverter == StyleConverter.getInsetsConverter()) {
if (resolved.getValue() instanceof ParsedValue) {
resolved = new ParsedValueImpl(new ParsedValue[] {(ParsedValue)resolved.getValue()}, null, false);
}
val = cssMetaDataConverter.convert(resolved, fontForFontRelativeSizes);
}
else if (resolved.getConverter() != null)
val = resolved.convert(fontForFontRelativeSizes);
else
val = cssMetaData.getConverter().convert(resolved, fontForFontRelativeSizes);
final StyleOrigin origin = whence.get();
return new CalculatedValue(val, origin, isRelative);
} catch (ClassCastException cce) {
final String msg = formatUnresolvedLookupMessage(styleable, cssMetaData, style.getStyle(),resolved, cce);
List<CssParser.ParseError> errors = null;
if ((errors = StyleManager.getErrors()) != null) {
final CssParser.ParseError error = new CssParser.ParseError.PropertySetError(cssMetaData, styleable, msg);
errors.add(error);
}
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(msg);
LOGGER.fine("node = " + styleable.toString());
LOGGER.fine("cssMetaData = " + cssMetaData);
LOGGER.fine("styles = " + getMatchingStyles(styleable, cssMetaData));
}
return SKIP;
} catch (IllegalArgumentException iae) {
final String msg = formatExceptionMessage(styleable, cssMetaData, style.getStyle(), iae);
List<CssParser.ParseError> errors = null;
if ((errors = StyleManager.getErrors()) != null) {
final CssParser.ParseError error = new CssParser.ParseError.PropertySetError(cssMetaData, styleable, msg);
errors.add(error);
}
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(msg);
LOGGER.fine("caught: ", iae);
LOGGER.fine("styleable = " + cssMetaData);
LOGGER.fine("node = " + styleable.toString());
}
return SKIP;
} catch (NullPointerException npe) {
final String msg = formatExceptionMessage(styleable, cssMetaData, style.getStyle(), npe);
List<CssParser.ParseError> errors = null;
if ((errors = StyleManager.getErrors()) != null) {
final CssParser.ParseError error = new CssParser.ParseError.PropertySetError(cssMetaData, styleable, msg);
errors.add(error);
}
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(msg);
LOGGER.fine("caught: ", npe);
LOGGER.fine("styleable = " + cssMetaData);
LOGGER.fine("node = " + styleable.toString());
}
return SKIP;
}
}
return new CalculatedValue(null, style.getOrigin(), false);
}
private static final CssMetaData dummyFontProperty =
new FontCssMetaData<Node>("-fx-font", Font.getDefault()) {
@Override
public boolean isSettable(Node node) {
return true;
}
@Override
public StyleableProperty<Font> getStyleableProperty(Node node) {
return null;
}
};
private CalculatedValue getCachedFont(final Styleable styleable) {
if (styleable instanceof Node == false) return null;
CalculatedValue cachedFont = null;
Node parent = (Node)styleable;
final CssStyleHelper parentHelper = parent.styleHelper;
if (parentHelper == null || parentHelper.cacheContainer == null) {
cachedFont = getCachedFont(parent.getStyleableParent());
} else {
CacheContainer parentCacheContainer = parentHelper.cacheContainer;
if ( parentCacheContainer != null
&& parentCacheContainer.fontSizeCache != null
&& parentCacheContainer.fontSizeCache.isEmpty() == false) {
Set<PseudoClass>[] transitionStates = parentHelper.getTransitionStates(parent);
StyleCacheEntry.Key parentCacheEntryKey = new StyleCacheEntry.Key(transitionStates, Font.getDefault());
cachedFont = parentCacheContainer.fontSizeCache.get(parentCacheEntryKey);
}
if (cachedFont == null) {
StyleMap smap = parentHelper.getStyleMap(parent);
cachedFont = parentHelper.lookupFont(parent, "-fx-font", smap, null);
}
}
return cachedFont != SKIP ? cachedFont : null;
}
FontPosture getFontPosture(Font font) {
if (font == null) return FontPosture.REGULAR;
String fontName = font.getName().toLowerCase(Locale.ROOT);
if (fontName.contains("italic")) {
return FontPosture.ITALIC;
}
return FontPosture.REGULAR;
}
FontWeight getFontWeight(Font font) {
if (font == null) return FontWeight.NORMAL;
String fontName = font.getName().toLowerCase(Locale.ROOT);
if (fontName.contains("bold")) {
if (fontName.contains("extra")) return FontWeight.EXTRA_BOLD;
if (fontName.contains("ultra")) return FontWeight.EXTRA_BOLD;
else if (fontName.contains("semi")) return FontWeight.SEMI_BOLD;
else if (fontName.contains("demi")) return FontWeight.SEMI_BOLD;
else return FontWeight.BOLD;
} else if (fontName.contains("light")) {
if (fontName.contains("extra")) return FontWeight.EXTRA_LIGHT;
if (fontName.contains("ultra")) return FontWeight.EXTRA_LIGHT;
else return FontWeight.LIGHT;
} else if (fontName.contains("black")) {
return FontWeight.BLACK;
} else if (fontName.contains("heavy")) {
return FontWeight.BLACK;
} else if (fontName.contains("medium")) {
return FontWeight.MEDIUM;
}
return FontWeight.NORMAL;
}
String getFontFamily(Font font) {
if (font == null) return Font.getDefault().getFamily();
return font.getFamily();
}
Font deriveFont(
Font font,
String fontFamily,
FontWeight fontWeight,
FontPosture fontPosture,
double fontSize) {
if (font != null && fontFamily == null) fontFamily = getFontFamily(font);
else if (fontFamily != null) fontFamily = Utils.stripQuotes(fontFamily);
if (font != null && fontWeight == null) fontWeight = getFontWeight(font);
if (font != null && fontPosture == null) fontPosture = getFontPosture(font);
if (font != null && fontSize <= 0) fontSize = font.getSize();
return Font.font(
fontFamily,
fontWeight,
fontPosture,
fontSize);
}
CalculatedValue lookupFont(
final Styleable styleable,
final String property,
final StyleMap styleMap,
final CalculatedValue cachedFont)
{
StyleOrigin origin = null;
int distance = 0;
boolean foundStyle = false;
String family = null;
double size = -1;
FontWeight weight = null;
FontPosture posture = null;
CalculatedValue cvFont = cachedFont;
Set<PseudoClass> states = styleable instanceof Node ? ((Node)styleable).pseudoClassStates : styleable.getPseudoClassStates();
if (cacheContainer.fontProp != null) {
StyleableProperty<Font> styleableProp = cacheContainer.fontProp.getStyleableProperty(styleable);
StyleOrigin fpOrigin = styleableProp.getStyleOrigin();
Font font = styleableProp.getValue();
if (font == null) font = Font.getDefault();
if (fpOrigin == StyleOrigin.USER) {
origin = fpOrigin;
family = getFontFamily(font);
size = font.getSize();
weight = getFontWeight(font);
posture = getFontPosture(font);
cvFont = new CalculatedValue(font, fpOrigin, false);
}
}
CalculatedValue parentCachedFont = getCachedFont(styleable.getStyleableParent());
if (parentCachedFont == null) parentCachedFont = new CalculatedValue(Font.getDefault(), null, false);
CascadingStyle fontShorthand = getStyle(styleable, property, styleMap, states);
if (fontShorthand == null && origin != StyleOrigin.USER) {
Styleable parent = styleable != null ? styleable.getStyleableParent() : null;
while (parent != null) {
CssStyleHelper parentStyleHelper = parent instanceof Node ? ((Node)parent).styleHelper : null;
if (parentStyleHelper != null) {
distance += 1;
StyleMap parentStyleMap = parentStyleHelper.getStyleMap(parent);
Set<PseudoClass> transitionStates = ((Node)parent).pseudoClassStates;
CascadingStyle cascadingStyle = parentStyleHelper.getStyle(parent, property, parentStyleMap, transitionStates);
if (cascadingStyle != null) {
final ParsedValue cssValue = cascadingStyle.getParsedValue();
if ("inherit".equals(cssValue.getValue()) == false) {
fontShorthand = cascadingStyle;
break;
}
}
}
parent = parent.getStyleableParent();
}
}
if (fontShorthand != null) {
if (origin == null || origin.compareTo(fontShorthand.getOrigin()) <= 0) {
final CalculatedValue cv =
calculateValue(fontShorthand, styleable, dummyFontProperty,
styleMap, states, styleable, parentCachedFont);
if (cv.getValue() instanceof Font) {
origin = cv.getOrigin();
Font font = (Font)cv.getValue();
family = getFontFamily(font);
size = font.getSize();
weight = getFontWeight(font);
posture = getFontPosture(font);
cvFont = cv;
foundStyle = true;
}
}
}
CascadingStyle fontSize = getStyle(styleable, property.concat("-size"), styleMap, states);
if (fontSize != null) {
if (fontShorthand != null && fontShorthand.compareTo(fontSize) < 0) {
fontSize = null;
} else if (origin == StyleOrigin.USER) {
if (StyleOrigin.USER.compareTo(fontSize.getOrigin()) > 0) {
fontSize = null;
}
}
} else if (origin != StyleOrigin.USER) {
fontSize = lookupInheritedFontProperty(styleable, property.concat("-size"), styleMap, distance, fontShorthand);
}
if (fontSize != null) {
final CalculatedValue cv =
calculateValue(fontSize, styleable, dummyFontProperty,
styleMap, states, styleable, parentCachedFont);
if (cv.getValue() instanceof Double) {
if (origin == null || origin.compareTo(fontSize.getOrigin()) <= 0) {
origin = cv.getOrigin();
}
size = (Double) cv.getValue();
if (cvFont != null) {
boolean isRelative = cvFont.isRelative() || cv.isRelative();
Font font = deriveFont((Font) cvFont.getValue(), family, weight, posture, size);
cvFont = new CalculatedValue(font, origin, isRelative);
} else {
boolean isRelative = cv.isRelative();
Font font = deriveFont(Font.getDefault(), family, weight, posture, size);
cvFont = new CalculatedValue(font, origin, isRelative);
}
foundStyle = true;
}
}
if (cachedFont == null) {
return (cvFont != null) ? cvFont : SKIP;
}
CascadingStyle fontWeight = getStyle(styleable, property.concat("-weight"), styleMap, states);
if (fontWeight != null) {
if (fontShorthand != null && fontShorthand.compareTo(fontWeight) < 0) {
fontWeight = null;
}
} else if (origin != StyleOrigin.USER) {
fontWeight = lookupInheritedFontProperty(styleable, property.concat("-weight"), styleMap, distance, fontShorthand);
}
if (fontWeight != null) {
final CalculatedValue cv =
calculateValue(fontWeight, styleable, dummyFontProperty,
styleMap, states, styleable, null);
if (cv.getValue() instanceof FontWeight) {
if (origin == null || origin.compareTo(fontWeight.getOrigin()) <= 0) {
origin = cv.getOrigin();
}
weight = (FontWeight)cv.getValue();
foundStyle = true;
}
}
CascadingStyle fontStyle = getStyle(styleable, property.concat("-style"), styleMap, states);
if (fontStyle != null) {
if (fontShorthand != null && fontShorthand.compareTo(fontStyle) < 0) {
fontStyle = null;
}
} else if (origin != StyleOrigin.USER) {
fontStyle = lookupInheritedFontProperty(styleable, property.concat("-style"), styleMap, distance, fontShorthand);
}
if (fontStyle != null) {
final CalculatedValue cv =
calculateValue(fontStyle, styleable, dummyFontProperty,
styleMap, states, styleable, null);
if (cv.getValue() instanceof FontPosture) {
if (origin == null || origin.compareTo(fontStyle.getOrigin()) <= 0) {
origin = cv.getOrigin();
}
posture = (FontPosture)cv.getValue();
foundStyle = true;
}
}
CascadingStyle fontFamily = getStyle(styleable, property.concat("-family"), styleMap, states);
if (fontFamily != null) {
if (fontShorthand != null && fontShorthand.compareTo(fontFamily) < 0) {
fontFamily = null;
}
} else if (origin != StyleOrigin.USER) {
fontFamily = lookupInheritedFontProperty(styleable, property.concat("-family"), styleMap, distance, fontShorthand);
}
if (fontFamily != null) {
final CalculatedValue cv =
calculateValue(fontFamily, styleable, dummyFontProperty,
styleMap, states, styleable, null);
if (cv.getValue() instanceof String) {
if (origin == null || origin.compareTo(fontFamily.getOrigin()) <= 0) {
origin = cv.getOrigin();
}
family = (String)cv.getValue();
foundStyle = true;
}
}
if (foundStyle) {
Font font = cvFont != null ? (Font)cvFont.getValue() : Font.getDefault();
Font derivedFont = deriveFont(font, family, weight, posture, size);
return new CalculatedValue(derivedFont,origin,false);
}
return SKIP;
}
private CascadingStyle lookupInheritedFontProperty(
final Styleable styleable,
final String property,
final StyleMap styleMap,
final int distance,
CascadingStyle fontShorthand) {
Styleable parent = styleable != null ? styleable.getStyleableParent() : null;
int nlooks = distance;
while (parent != null && nlooks > 0) {
CssStyleHelper parentStyleHelper = parent instanceof Node ? ((Node)parent).styleHelper : null;
if (parentStyleHelper != null) {
nlooks -= 1;
StyleMap parentStyleMap = parentStyleHelper.getStyleMap((parent));
Set<PseudoClass> transitionStates = ((Node)parent).pseudoClassStates;
CascadingStyle cascadingStyle = parentStyleHelper.getStyle(parent, property, parentStyleMap, transitionStates);
if (cascadingStyle != null) {
if (fontShorthand != null && nlooks == 0) {
if (fontShorthand.compareTo(cascadingStyle) < 0) {
return null;
}
}
final ParsedValue cssValue = cascadingStyle.getParsedValue();
if ("inherit".equals(cssValue.getValue()) == false) {
return cascadingStyle;
}
}
}
parent = parent.getStyleableParent();
}
return null;
}
static List<Style> getMatchingStyles(final Styleable styleable, final CssMetaData styleableProperty) {
if (!(styleable instanceof Node)) return Collections.<Style>emptyList();
Node node = (Node)styleable;
final CssStyleHelper helper = (node.styleHelper != null) ? node.styleHelper : createStyleHelper(node);
if (helper != null) {
return helper.getMatchingStyles(node, styleableProperty, false);
}
else {
return Collections.<Style>emptyList();
}
}
static Map<StyleableProperty<?>, List<Style>> getMatchingStyles(Map<StyleableProperty<?>, List<Style>> map, final Node node) {
final CssStyleHelper helper = (node.styleHelper != null) ? node.styleHelper : createStyleHelper(node);
if (helper != null) {
if (map == null) map = new HashMap<>();
for (CssMetaData metaData : node.getCssMetaData()) {
List<Style> styleList = helper.getMatchingStyles(node, metaData, true);
if (styleList != null && !styleList.isEmpty()) {
StyleableProperty prop = metaData.getStyleableProperty(node);
map.put(prop, styleList);
}
}
}
if (node instanceof Parent) {
for (Node child : ((Parent)node).getChildren()) {
map = getMatchingStyles(map, child);
}
}
return map;
}
private List<Style> getMatchingStyles(final Styleable node, final CssMetaData styleableProperty, boolean matchState) {
final List<CascadingStyle> styleList = new ArrayList<>();
getMatchingStyles(node, styleableProperty, styleList, matchState);
List<CssMetaData<? extends Styleable, ?>> subProperties = styleableProperty.getSubProperties();
if (subProperties != null) {
for (int n=0,nMax=subProperties.size(); n<nMax; n++) {
final CssMetaData subProperty = subProperties.get(n);
getMatchingStyles(node, subProperty, styleList, matchState);
}
}
Collections.sort(styleList);
final List<Style> matchingStyles = new ArrayList<>(styleList.size());
for (int n=0,nMax=styleList.size(); n<nMax; n++) {
final Style style = styleList.get(n).getStyle();
if (!matchingStyles.contains(style)) matchingStyles.add(style);
}
return matchingStyles;
}
private void getMatchingStyles(final Styleable node, final CssMetaData styleableProperty, final List<CascadingStyle> styleList, boolean matchState) {
if (node != null) {
String property = styleableProperty.getProperty();
Node _node = node instanceof Node ? (Node)node : null;
final StyleMap smap = getStyleMap(_node);
if (smap == null) return;
if (matchState) {
CascadingStyle cascadingStyle = getStyle(node, styleableProperty.getProperty(), smap, _node.pseudoClassStates);
if (cascadingStyle != null) {
styleList.add(cascadingStyle);
final ParsedValue parsedValue = cascadingStyle.getParsedValue();
getMatchingLookupStyles(node, parsedValue, styleList, matchState);
}
} else {
Map<String, List<CascadingStyle>> cascadingStyleMap = smap.getCascadingStyles();
List<CascadingStyle> styles = cascadingStyleMap.get(property);
if (styles != null) {
styleList.addAll(styles);
for (int n=0, nMax=styles.size(); n<nMax; n++) {
final CascadingStyle style = styles.get(n);
final ParsedValue parsedValue = style.getParsedValue();
getMatchingLookupStyles(node, parsedValue, styleList, matchState);
}
}
}
if (styleableProperty.isInherits()) {
Styleable parent = node.getStyleableParent();
while (parent != null) {
CssStyleHelper parentHelper = parent instanceof Node
? ((Node)parent).styleHelper
: null;
if (parentHelper != null) {
parentHelper.getMatchingStyles(parent, styleableProperty, styleList, matchState);
}
parent = parent.getStyleableParent();
}
}
}
}
private void getMatchingLookupStyles(final Styleable node, final ParsedValue parsedValue, final List<CascadingStyle> styleList, boolean matchState) {
if (parsedValue.isLookup()) {
Object value = parsedValue.getValue();
if (value instanceof String) {
final String property = (String)value;
Styleable parent = node;
do {
final Node _parent = parent instanceof Node ? (Node)parent : null;
final CssStyleHelper helper = _parent != null
? _parent.styleHelper
: null;
if (helper != null) {
StyleMap styleMap = helper.getStyleMap(parent);
if (styleMap == null || styleMap.isEmpty()) continue;
final int start = styleList.size();
if (matchState) {
CascadingStyle cascadingStyle = helper.resolveRef(_parent, property, styleMap, _parent.pseudoClassStates);
if (cascadingStyle != null) {
styleList.add(cascadingStyle);
}
} else {
final Map<String, List<CascadingStyle>> smap = styleMap.getCascadingStyles();
List<CascadingStyle> styles = smap.get(property);
if (styles != null) {
styleList.addAll(styles);
}
}
final int end = styleList.size();
for (int index=start; index<end; index++) {
final CascadingStyle style = styleList.get(index);
getMatchingLookupStyles(parent, style.getParsedValue(), styleList, matchState);
}
}
} while ((parent = parent.getStyleableParent()) != null);
}
}
if (!parsedValue.isContainsLookups()) {
return;
}
final Object val = parsedValue.getValue();
if (val instanceof ParsedValue[][]) {
final ParsedValue[][] layers = (ParsedValue[][])val;
for (int l=0; l<layers.length; l++) {
for (int ll=0; ll<layers[l].length; ll++) {
if (layers[l][ll] == null) continue;
getMatchingLookupStyles(node, layers[l][ll], styleList, matchState);
}
}
} else if (val instanceof ParsedValue[]) {
final ParsedValue[] layer = (ParsedValue[])val;
for (int l=0; l<layer.length; l++) {
if (layer[l] == null) continue;
getMatchingLookupStyles(node, layer[l], styleList, matchState);
}
}
}
}

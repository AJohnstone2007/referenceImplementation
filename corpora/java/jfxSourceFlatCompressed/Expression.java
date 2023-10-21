package com.sun.javafx.fxml.expression;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.sun.javafx.fxml.BeanAdapter;
import static com.sun.javafx.fxml.expression.Operator.*;
public abstract class Expression<T> {
private static class Parser {
public static class Token {
public Token(TokenType type, Object value) {
this.type = type;
this.value = value;
}
public final TokenType type;
public final Object value;
@Override
public String toString() {
return value.toString();
}
}
public enum TokenType {
LITERAL,
VARIABLE,
FUNCTION,
UNARY_OPERATOR,
BINARY_OPERATOR,
BEGIN_GROUP,
END_GROUP
}
private int c = -1;
private char[] pushbackBuffer = new char[PUSHBACK_BUFFER_SIZE];
private static final int PUSHBACK_BUFFER_SIZE = 6;
public Expression parse(Reader reader) throws IOException {
LinkedList<Token> tokens = tokenize(new PushbackReader(reader, PUSHBACK_BUFFER_SIZE));
LinkedList<Expression> stack = new LinkedList<Expression>();
for (Token token : tokens) {
Expression<?> expression;
switch (token.type) {
case LITERAL: {
expression = new LiteralExpression(token.value);
break;
}
case VARIABLE: {
expression = new VariableExpression((KeyPath)token.value);
break;
}
case FUNCTION: {
expression = null;
break;
}
case UNARY_OPERATOR: {
Operator operator = (Operator)token.value;
Expression operand = stack.pop();
switch(operator) {
case NEGATE:
expression = negate(operand);
break;
case NOT:
expression = not(operand);
break;
default:
throw new UnsupportedOperationException();
}
break;
}
case BINARY_OPERATOR: {
Operator operator = (Operator)token.value;
Expression right = stack.pop();
Expression left = stack.pop();
switch(operator) {
case ADD:
expression = add(left, right);
break;
case SUBTRACT:
expression = subtract(left, right);
break;
case MULTIPLY:
expression = multiply(left, right);
break;
case DIVIDE:
expression = divide(left, right);
break;
case MODULO:
expression = modulo(left, right);
break;
case GREATER_THAN:
expression = greaterThan(left, right);
break;
case GREATER_THAN_OR_EQUAL_TO:
expression = greaterThanOrEqualTo(left, right);
break;
case LESS_THAN:
expression = lessThan(left, right);
break;
case LESS_THAN_OR_EQUAL_TO:
expression = lessThanOrEqualTo(left, right);
break;
case EQUAL_TO:
expression = equalTo(left, right);
break;
case NOT_EQUAL_TO:
expression = notEqualTo(left, right);
break;
case AND:
expression = and(left, right);
break;
case OR:
expression = or(left, right);
break;
default:
throw new UnsupportedOperationException();
}
break;
}
default: {
throw new UnsupportedOperationException();
}
}
stack.push(expression);
}
if (stack.size() != 1) {
throw new IllegalArgumentException("Invalid expression.");
}
return stack.peek();
}
private LinkedList<Token> tokenize(PushbackReader reader) throws IOException {
LinkedList<Token> tokens = new LinkedList<Token>();
LinkedList<Token> stack = new LinkedList<Token>();
c = reader.read();
boolean unary = true;
while (c != -1) {
while (c != -1 && Character.isWhitespace(c)) {
c = reader.read();
}
if (c != -1) {
Token token;
if (c == 'n') {
if (readKeyword(reader, NULL_KEYWORD)) {
token = new Token(TokenType.LITERAL, null);
} else {
token = new Token(TokenType.VARIABLE, KeyPath.parse(reader));
c = reader.read();
}
} else if (c == '"' || c == '\'') {
StringBuilder stringBuilder = new StringBuilder();
int t = c;
c = reader.read();
while (c != -1 && c != t) {
if (!Character.isISOControl(c)) {
if (c == '\\') {
c = reader.read();
if (c == 'b') {
c = '\b';
} else if (c == 'f') {
c = '\f';
} else if (c == 'n') {
c = '\n';
} else if (c == 'r') {
c = '\r';
} else if (c == 't') {
c = '\t';
} else if (c == 'u') {
StringBuilder unicodeValueBuilder = new StringBuilder();
while (unicodeValueBuilder.length() < 4) {
c = reader.read();
unicodeValueBuilder.append((char)c);
}
String unicodeValue = unicodeValueBuilder.toString();
c = (char)Integer.parseInt(unicodeValue, 16);
} else {
if (!(c == '\\'
|| c == '/'
|| c == '\"'
|| c == '\''
|| c == t)) {
throw new IllegalArgumentException("Unsupported escape sequence.");
}
}
}
stringBuilder.append((char)c);
}
c = reader.read();
}
if (c != t) {
throw new IllegalArgumentException("Unterminated string.");
}
c = reader.read();
token = new Token(TokenType.LITERAL, stringBuilder.toString());
} else if (Character.isDigit(c)) {
StringBuilder numberBuilder = new StringBuilder();
boolean integer = true;
while (c != -1 && (Character.isDigit(c) || c == '.'
|| c == 'e' || c == 'E')) {
numberBuilder.append((char)c);
integer &= !(c == '.');
c = reader.read();
}
Number value;
if (integer) {
value = Long.parseLong(numberBuilder.toString());
} else {
value = Double.parseDouble(numberBuilder.toString());
}
token = new Token(TokenType.LITERAL, value);
} else if (c == 't') {
if (readKeyword(reader, TRUE_KEYWORD)) {
token = new Token(TokenType.LITERAL, true);
} else {
token = new Token(TokenType.VARIABLE, KeyPath.parse(reader));
c = reader.read();
}
} else if (c == 'f') {
if (readKeyword(reader, FALSE_KEYWORD)) {
token = new Token(TokenType.LITERAL, false);
} else {
token = new Token(TokenType.VARIABLE, KeyPath.parse(reader));
c = reader.read();
}
} else if (Character.isJavaIdentifierStart(c)) {
reader.unread(c);
token = new Token(TokenType.VARIABLE, KeyPath.parse(reader));
c = reader.read();
} else {
boolean readNext = true;
if (unary) {
switch(c) {
case '-':
token = new Token(TokenType.UNARY_OPERATOR, NEGATE);
break;
case '!':
token = new Token(TokenType.UNARY_OPERATOR, NOT);
break;
case '(':
token = new Token(TokenType.BEGIN_GROUP, null);
break;
default:
throw new IllegalArgumentException("Unexpected character in expression.");
}
} else {
switch(c) {
case '+':
token = new Token(TokenType.BINARY_OPERATOR, ADD);
break;
case '-':
token = new Token(TokenType.BINARY_OPERATOR, SUBTRACT);
break;
case '*':
token = new Token(TokenType.BINARY_OPERATOR, MULTIPLY);
break;
case '/':
token = new Token(TokenType.BINARY_OPERATOR, DIVIDE);
break;
case '%':
token = new Token(TokenType.BINARY_OPERATOR, MODULO);
break;
case '=':
c = reader.read();
if (c == '=') {
token = new Token(TokenType.BINARY_OPERATOR, EQUAL_TO);
} else {
throw new IllegalArgumentException("Unexpected character in expression.");
}
break;
case '!':
c = reader.read();
if (c == '=') {
token = new Token(TokenType.BINARY_OPERATOR, NOT_EQUAL_TO);
} else {
throw new IllegalArgumentException("Unexpected character in expression.");
}
break;
case '>':
c = reader.read();
if (c == '=') {
token = new Token(TokenType.BINARY_OPERATOR, GREATER_THAN_OR_EQUAL_TO);
} else {
readNext = false;
token = new Token(TokenType.BINARY_OPERATOR, GREATER_THAN);
}
break;
case '<':
c = reader.read();
if (c == '=') {
token = new Token(TokenType.BINARY_OPERATOR, LESS_THAN_OR_EQUAL_TO);
} else {
readNext = false;
token = new Token(TokenType.BINARY_OPERATOR, LESS_THAN);
}
break;
case '&':
c = reader.read();
if (c == '&') {
token = new Token(TokenType.BINARY_OPERATOR, AND);
} else {
throw new IllegalArgumentException("Unexpected character in expression.");
}
break;
case '|':
c = reader.read();
if (c == '|') {
token = new Token(TokenType.BINARY_OPERATOR, OR);
} else {
throw new IllegalArgumentException("Unexpected character in expression.");
}
break;
case ')':
token = new Token(TokenType.END_GROUP, null);
break;
default:
throw new IllegalArgumentException("Unexpected character in expression.");
}
}
if (readNext) {
c = reader.read();
}
}
switch (token.type) {
case LITERAL:
case VARIABLE: {
tokens.add(token);
break;
}
case UNARY_OPERATOR:
case BINARY_OPERATOR: {
int priority = ((Operator)token.value).getPriority();
while (!stack.isEmpty()
&& stack.peek().type != TokenType.BEGIN_GROUP
&& ((Operator)stack.peek().value).getPriority() >= priority
&& ((Operator)stack.peek().value).getPriority() != Operator.MAX_PRIORITY) {
tokens.add(stack.pop());
}
stack.push(token);
break;
}
case BEGIN_GROUP: {
stack.push(token);
break;
}
case END_GROUP: {
for (Token t = stack.pop(); t.type != TokenType.BEGIN_GROUP; t = stack.pop()) {
tokens.add(t);
}
break;
}
default: {
throw new UnsupportedOperationException();
}
}
unary = !(token.type == TokenType.LITERAL || token.type == TokenType.VARIABLE || token.type == TokenType.END_GROUP);
}
}
while (!stack.isEmpty()) {
tokens.add(stack.pop());
}
return tokens;
}
private boolean readKeyword(PushbackReader reader, String keyword) throws IOException {
int n = keyword.length();
int i = 0;
while (c != -1 && i < n) {
pushbackBuffer[i] = (char)c;
if (keyword.charAt(i) != c) {
break;
}
c = reader.read();
i++;
}
boolean result;
if (i < n) {
reader.unread(pushbackBuffer, 0, i + 1);
result = false;
} else {
result = true;
}
return result;
}
}
private static final String NULL_KEYWORD = "null";
private static final String TRUE_KEYWORD = "true";
private static final String FALSE_KEYWORD = "false";
public abstract T evaluate(Object namespace);
public abstract void update(Object namespace, T value);
public abstract boolean isDefined(Object namespace);
public abstract boolean isLValue();
public List<KeyPath> getArguments() {
ArrayList<KeyPath> arguments = new ArrayList<KeyPath>();
getArguments(arguments);
return arguments;
}
protected abstract void getArguments(List<KeyPath> arguments);
@SuppressWarnings("unchecked")
public static <T> T get(Object namespace, KeyPath keyPath) {
if (keyPath == null) {
throw new NullPointerException();
}
return (T)get(namespace, keyPath.iterator());
}
@SuppressWarnings("unchecked")
private static <T> T get(Object namespace, Iterator<String> keyPathIterator) {
if (keyPathIterator == null) {
throw new NullPointerException();
}
T value;
if (keyPathIterator.hasNext()) {
value = (T)get(get(namespace, keyPathIterator.next()), keyPathIterator);
} else {
value = (T)namespace;
}
return value;
}
@SuppressWarnings("unchecked")
public static <T> T get(Object namespace, String key) {
if (key == null) {
throw new NullPointerException();
}
Object value;
if (namespace instanceof List<?>) {
List<Object> list = (List<Object>)namespace;
value = list.get(Integer.parseInt(key));
} else if (namespace != null) {
Map<String, Object> map;
if (namespace instanceof Map<?, ?>) {
map = (Map<String, Object>)namespace;
} else {
map = new BeanAdapter(namespace);
}
value = map.get(key);
} else {
value = null;
}
return (T)value;
}
public static void set(Object namespace, KeyPath keyPath, Object value) {
if (keyPath == null) {
throw new NullPointerException();
}
set(namespace, keyPath.iterator(), value);
}
private static void set(Object namespace, Iterator<String> keyPathIterator, Object value) {
if (keyPathIterator == null) {
throw new NullPointerException();
}
if (!keyPathIterator.hasNext()) {
throw new IllegalArgumentException();
}
String key = keyPathIterator.next();
if (keyPathIterator.hasNext()) {
set(get(namespace, key), keyPathIterator, value);
} else {
set(namespace, key, value);
}
}
@SuppressWarnings("unchecked")
public static void set(Object namespace, String key, Object value) {
if (key == null) {
throw new NullPointerException();
}
if (namespace instanceof List<?>) {
List<Object> list = (List<Object>)namespace;
list.set(Integer.parseInt(key), value);
} else if (namespace != null) {
Map<String, Object> map;
if (namespace instanceof Map<?, ?>) {
map = (Map<String, Object>)namespace;
} else {
map = new BeanAdapter(namespace);
}
map.put(key, value);
} else {
throw new IllegalArgumentException();
}
}
public static boolean isDefined(Object namespace, KeyPath keyPath) {
if (keyPath == null) {
throw new NullPointerException();
}
return isDefined(namespace, keyPath.iterator());
}
private static boolean isDefined(Object namespace, Iterator<String> keyPathIterator) {
if (keyPathIterator == null) {
throw new NullPointerException();
}
if (!keyPathIterator.hasNext()) {
throw new IllegalArgumentException();
}
String key = keyPathIterator.next();
boolean defined;
if (keyPathIterator.hasNext()) {
defined = isDefined(get(namespace, key), keyPathIterator);
} else {
defined = isDefined(namespace, key);
}
return defined;
}
@SuppressWarnings("unchecked")
public static boolean isDefined(Object namespace, String key) {
if (key == null) {
throw new NullPointerException();
}
boolean defined;
if (namespace instanceof List<?>) {
List<Object> list = (List<Object>)namespace;
defined = Integer.parseInt(key) < list.size();
} else if (namespace != null) {
Map<String, Object> map;
if (namespace instanceof Map<?, ?>) {
map = (Map<String, Object>)namespace;
} else {
map = new BeanAdapter(namespace);
}
defined = map.containsKey(key);
} else {
defined = false;
}
return defined;
}
public static BinaryExpression add(Expression left, Expression right) {
return new BinaryExpression(left, right, (leftValue, rightValue) -> {
Object value;
if (leftValue instanceof String || rightValue instanceof String) {
value = leftValue.toString().concat(rightValue.toString());
} else {
Number leftNumber = (Number)leftValue;
Number rightNumber = (Number)rightValue;
if (leftNumber instanceof Double || rightNumber instanceof Double) {
value = leftNumber.doubleValue() + rightNumber.doubleValue();
} else if (leftNumber instanceof Float || rightNumber instanceof Float) {
value = leftNumber.floatValue() + rightNumber.floatValue();
} else if (leftNumber instanceof Long || rightNumber instanceof Long) {
value = leftNumber.longValue() + rightNumber.longValue();
} else if (leftNumber instanceof Integer || rightNumber instanceof Integer) {
value = leftNumber.intValue() + rightNumber.intValue();
} else if (leftNumber instanceof Short || rightNumber instanceof Short) {
value = leftNumber.shortValue() + rightNumber.shortValue();
} else if (leftNumber instanceof Byte || rightNumber instanceof Byte) {
value = leftNumber.byteValue() + rightNumber.byteValue();
} else {
throw new UnsupportedOperationException();
}
}
return value;
});
}
public static BinaryExpression add(Expression left, Object right) {
return add(left, new LiteralExpression(right));
}
public static BinaryExpression add(Object left, Expression right) {
return add(new LiteralExpression(left), right);
}
public static BinaryExpression add(Object left, Object right) {
return add(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression subtract(Expression left, Expression right) {
return new BinaryExpression<Number, Number>(left, right, (Number leftValue, Number rightValue) -> {
Number value;
if (leftValue instanceof Double || rightValue instanceof Double) {
value = leftValue.doubleValue() - rightValue.doubleValue();
} else if (leftValue instanceof Float || rightValue instanceof Float) {
value = leftValue.floatValue() - rightValue.floatValue();
} else if (leftValue instanceof Long || rightValue instanceof Long) {
value = leftValue.longValue() - rightValue.longValue();
} else if (leftValue instanceof Integer || rightValue instanceof Integer) {
value = leftValue.intValue() - rightValue.intValue();
} else if (leftValue instanceof Short || rightValue instanceof Short) {
value = leftValue.shortValue() - rightValue.shortValue();
} else if (leftValue instanceof Byte || rightValue instanceof Byte) {
value = leftValue.byteValue() - rightValue.byteValue();
} else {
throw new UnsupportedOperationException();
}
return value;
});
}
public static BinaryExpression subtract(Expression left, Number right) {
return subtract(left, new LiteralExpression(right));
}
public static BinaryExpression subtract(Number left, Expression right) {
return subtract(new LiteralExpression(left), right);
}
public static BinaryExpression subtract(Number left, Number right) {
return subtract(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression multiply(Expression left, Expression right) {
return new BinaryExpression<Number, Number>(left, right, (Number leftValue, Number rightValue) -> {
Number value;
if (leftValue instanceof Double || rightValue instanceof Double) {
value = leftValue.doubleValue() * rightValue.doubleValue();
} else if (leftValue instanceof Float || rightValue instanceof Float) {
value = leftValue.floatValue() * rightValue.floatValue();
} else if (leftValue instanceof Long || rightValue instanceof Long) {
value = leftValue.longValue() * rightValue.longValue();
} else if (leftValue instanceof Integer || rightValue instanceof Integer) {
value = leftValue.intValue() * rightValue.intValue();
} else if (leftValue instanceof Short || rightValue instanceof Short) {
value = leftValue.shortValue() * rightValue.shortValue();
} else if (leftValue instanceof Byte || rightValue instanceof Byte) {
value = leftValue.byteValue() * rightValue.byteValue();
} else {
throw new UnsupportedOperationException();
}
return value;
});
}
public static BinaryExpression multiply(Expression left, Number right) {
return multiply(left, new LiteralExpression(right));
}
public static BinaryExpression multiply(Number left, Expression right) {
return multiply(new LiteralExpression(left), right);
}
public static BinaryExpression multiply(Number left, Number right) {
return multiply(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression divide(Expression left, Expression right) {
return new BinaryExpression<Number, Number>(left, right, (Number leftValue, Number rightValue) -> {
Number value;
if (leftValue instanceof Double || rightValue instanceof Double) {
value = leftValue.doubleValue() / rightValue.doubleValue();
} else if (leftValue instanceof Float || rightValue instanceof Float) {
value = leftValue.floatValue() / rightValue.floatValue();
} else if (leftValue instanceof Long || rightValue instanceof Long) {
value = leftValue.longValue() / rightValue.longValue();
} else if (leftValue instanceof Integer || rightValue instanceof Integer) {
value = leftValue.intValue() / rightValue.intValue();
} else if (leftValue instanceof Short || rightValue instanceof Short) {
value = leftValue.shortValue() / rightValue.shortValue();
} else if (leftValue instanceof Byte || rightValue instanceof Byte) {
value = leftValue.byteValue() / rightValue.byteValue();
} else {
throw new UnsupportedOperationException();
}
return value;
});
}
public static BinaryExpression divide(Expression left, Number right) {
return divide(left, new LiteralExpression(right));
}
public static BinaryExpression divide(Number left, Expression<Number> right) {
return divide(new LiteralExpression(left), right);
}
public static BinaryExpression divide(Number left, Number right) {
return divide(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression modulo(Expression left, Expression right) {
return new BinaryExpression<Number, Number>(left, right, (Number leftValue, Number rightValue) -> {
Number value;
if (leftValue instanceof Double || rightValue instanceof Double) {
value = leftValue.doubleValue() % rightValue.doubleValue();
} else if (leftValue instanceof Float || rightValue instanceof Float) {
value = leftValue.floatValue() % rightValue.floatValue();
} else if (leftValue instanceof Long || rightValue instanceof Long) {
value = leftValue.longValue() % rightValue.longValue();
} else if (leftValue instanceof Integer || rightValue instanceof Integer) {
value = leftValue.intValue() % rightValue.intValue();
} else if (leftValue instanceof Short || rightValue instanceof Short) {
value = leftValue.shortValue() % rightValue.shortValue();
} else if (leftValue instanceof Byte || rightValue instanceof Byte) {
value = leftValue.byteValue() % rightValue.byteValue();
} else {
throw new UnsupportedOperationException();
}
return value;
});
}
public static BinaryExpression modulo(Expression<Number> left, Number right) {
return modulo(left, new LiteralExpression(right));
}
public static BinaryExpression modulo(Number left, Expression<Number> right) {
return modulo(new LiteralExpression(left), right);
}
public static BinaryExpression modulo(Number left, Number right) {
return modulo(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression equalTo(Expression left, Expression right) {
return new BinaryExpression<Comparable, Boolean>(left, right, (Comparable leftValue, Comparable rightValue) ->
leftValue.compareTo(rightValue) == 0
);
}
public static BinaryExpression equalTo(Expression left, Object right) {
return equalTo(left, new LiteralExpression(right));
}
public static BinaryExpression equalTo(Object left, Expression right) {
return equalTo(new LiteralExpression(left), right);
}
public static BinaryExpression equalTo(Object left, Object right) {
return equalTo(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression notEqualTo(Expression left, Expression right) {
return new BinaryExpression<Comparable, Boolean>(left, right, (leftValue, rightValue) ->
leftValue.compareTo(rightValue) != 0
);
}
public static BinaryExpression notEqualTo(Expression left, Object right) {
return notEqualTo(left, new LiteralExpression(right));
}
public static BinaryExpression notEqualTo(Object left, Expression right) {
return notEqualTo(new LiteralExpression(left), right);
}
public static BinaryExpression notEqualTo(Object left, Object right) {
return notEqualTo(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression greaterThan(Expression left, Expression right) {
return new BinaryExpression<Comparable, Boolean>(left, right, (leftValue, rightValue) ->
leftValue.compareTo(rightValue) > 0
);
}
public static BinaryExpression greaterThan(Expression left, Object right) {
return greaterThan(left, new LiteralExpression(right));
}
public static BinaryExpression greaterThan(Object left, Expression right) {
return greaterThan(new LiteralExpression(left), right);
}
public static BinaryExpression greaterThan(Object left, Object right) {
return greaterThan(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression greaterThanOrEqualTo(Expression left, Expression right) {
return new BinaryExpression<Comparable, Boolean>(left, right, (leftValue, rightValue) ->
leftValue.compareTo(rightValue) >= 0
);
}
public static BinaryExpression greaterThanOrEqualTo(Expression left, Object right) {
return greaterThanOrEqualTo(left, new LiteralExpression(right));
}
public static BinaryExpression greaterThanOrEqualTo(Object left, Expression right) {
return greaterThanOrEqualTo(new LiteralExpression(left), right);
}
public static BinaryExpression greaterThanOrEqualTo(Object left, Object right) {
return greaterThanOrEqualTo(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression lessThan(Expression left, Expression right) {
return new BinaryExpression<Comparable, Boolean>(left, right, (leftValue, rightValue) ->
leftValue.compareTo(rightValue) < 0
);
}
public static BinaryExpression lessThan(Expression left, Object right) {
return lessThan(left, new LiteralExpression(right));
}
public static BinaryExpression lessThan(Object left, Expression right) {
return lessThan(new LiteralExpression(left), right);
}
public static BinaryExpression lessThan(Object left, Object right) {
return lessThan(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression lessThanOrEqualTo(Expression left, Expression right) {
return new BinaryExpression<Comparable, Boolean>(left, right, (leftValue, rightValue) ->
leftValue.compareTo(rightValue) <= 0
);
}
public static BinaryExpression lessThanOrEqualTo(Expression left, Object right) {
return lessThanOrEqualTo(left, new LiteralExpression(right));
}
public static BinaryExpression lessThanOrEqualTo(Object left, Expression right) {
return lessThanOrEqualTo(new LiteralExpression(left), right);
}
public static BinaryExpression lessThanOrEqualTo(Object left, Object right) {
return lessThanOrEqualTo(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression and(Expression left, Expression right) {
return new BinaryExpression<Boolean, Boolean>(left, right, (leftValue, rightValue) ->
leftValue && rightValue
);
}
public static BinaryExpression and(Expression left, Boolean right) {
return and(left, new LiteralExpression(right));
}
public static BinaryExpression and(Boolean left, Expression right) {
return and(new LiteralExpression(left), right);
}
public static BinaryExpression and(Boolean left, Boolean right) {
return and(new LiteralExpression(left), new LiteralExpression(right));
}
public static BinaryExpression or(Expression left, Expression right) {
return new BinaryExpression<Boolean, Boolean>(left, right, (leftValue, rightValue) ->
leftValue || rightValue
);
}
public static BinaryExpression or(Expression left, Boolean right) {
return or(left, new LiteralExpression(right));
}
public static BinaryExpression or(Boolean left, Expression right) {
return or(new LiteralExpression(left), right);
}
public static BinaryExpression or(Boolean left, Boolean right) {
return or(new LiteralExpression(left), new LiteralExpression(right));
}
public static UnaryExpression negate(Expression operand) {
return new UnaryExpression<Number, Number>(operand, (value) -> {
Class<? extends Number> type = value.getClass();
if (type == Byte.class) {
return -value.byteValue();
} else if (type == Short.class) {
return -value.shortValue();
} else if (type == Integer.class) {
return -value.intValue();
} else if (type == Long.class) {
return -value.longValue();
} else if (type == Float.class) {
return -value.floatValue();
} else if (type == Double.class) {
return -value.doubleValue();
} else {
throw new UnsupportedOperationException();
}
});
}
public static UnaryExpression negate(Number operand) {
return negate(new LiteralExpression(operand));
}
public static UnaryExpression not(Expression operand) {
return new UnaryExpression<Boolean, Boolean>(operand, (value) -> !value);
}
public static UnaryExpression not(Boolean operand) {
return not(new LiteralExpression(operand));
}
public static Expression valueOf(String value) {
if (value == null) {
throw new NullPointerException();
}
Parser parser = new Parser();
Expression expression;
try {
expression = parser.parse(new StringReader(value));
} catch (IOException exception) {
throw new RuntimeException(exception);
}
return expression;
}
}

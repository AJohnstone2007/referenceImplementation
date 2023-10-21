package com.sun.glass.ui.mac;
import java.lang.annotation.Native;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import javafx.geometry.Bounds;
final class MacVariant {
@Native final static int NSArray_id = 1;
@Native final static int NSArray_NSString = 2;
@Native final static int NSArray_int = 3;
@Native final static int NSArray_range = 4;
@Native final static int NSAttributedString = 5;
@Native final static int NSData = 6;
@Native final static int NSDate = 7;
@Native final static int NSDictionary = 8;
@Native final static int NSNumber_Boolean = 9;
@Native final static int NSNumber_Int = 10;
@Native final static int NSNumber_Float = 11;
@Native final static int NSNumber_Double = 12;
@Native final static int NSString = 13;
@Native final static int NSURL = 14;
@Native final static int NSValue_point = 15;
@Native final static int NSValue_size = 16;
@Native final static int NSValue_rectangle = 17;
@Native final static int NSValue_range = 18;
@Native final static int NSObject = 19;
int type;
long[] longArray;
int[] intArray;
String[] stringArray;
MacVariant[] variantArray;
float float1;
float float2;
float float3;
float float4;
int int1;
int int2;
String string;
long long1;
double double1;
int location;
int length;
long key;
static MacVariant createNSArray(Object result) {
MacVariant variant = new MacVariant();
variant.type = NSArray_id;
variant.longArray = (long[])result;
return variant;
}
static MacVariant createNSObject(Object result) {
MacVariant variant = new MacVariant();
variant.type = NSObject;
variant.long1 = (Long)result;
return variant;
}
static MacVariant createNSString(Object result) {
MacVariant variant = new MacVariant();
variant.type = NSString;
variant.string = (String)result;
return variant;
}
static MacVariant createNSAttributedString(Object result) {
MacVariant variant = new MacVariant();
variant.type = NSAttributedString;
variant.string = (String)result;
return variant;
}
static MacVariant createNSDate(Object result) {
MacVariant variant = new MacVariant();
variant.type = NSDate;
variant.long1 = ((LocalDate)result).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
return variant;
}
static MacVariant createNSValueForSize(Object result) {
Bounds bounds = (Bounds)result;
MacVariant variant = new MacVariant();
variant.type = NSValue_size;
variant.float1 = (float)bounds.getWidth();
variant.float2 = (float)bounds.getHeight();
return variant;
}
static MacVariant createNSValueForPoint(Object result) {
Bounds bounds = (Bounds)result;
MacVariant variant = new MacVariant();
variant.type = NSValue_point;
variant.float1 = (float)bounds.getMinX();
variant.float2 = (float)bounds.getMinY();
return variant;
}
static MacVariant createNSValueForRectangle(Object result) {
Bounds bounds = (Bounds)result;
MacVariant variant = new MacVariant();
variant.type = NSValue_rectangle;
variant.float1 = (float)bounds.getMinX();
variant.float2 = (float)bounds.getMinY();
variant.float3 = (float)bounds.getWidth();
variant.float4 = (float)bounds.getHeight();
return variant;
}
static MacVariant createNSValueForRange(Object result) {
int[] range = (int[])result;
MacVariant variant = new MacVariant();
variant.type = NSValue_range;
variant.int1 = range[0];
variant.int2 = range[1];
return variant;
}
static MacVariant createNSNumberForBoolean(Object result) {
Boolean value = (Boolean)result;
MacVariant variant = new MacVariant();
variant.type = NSNumber_Boolean;
variant.int1 = value ? 1 : 0;
return variant;
}
static MacVariant createNSNumberForDouble(Object result) {
MacVariant variant = new MacVariant();
variant.type = NSNumber_Double;
variant.double1 = (Double)result;
return variant;
}
static MacVariant createNSNumberForInt(Object result) {
MacVariant variant = new MacVariant();
variant.type = NSNumber_Int;
variant.int1 = (Integer)result;
return variant;
}
Object getValue() {
switch (type) {
case NSNumber_Boolean: return int1 != 0;
case NSNumber_Int: return int1;
case NSNumber_Double: return double1;
case NSArray_id: return longArray;
case NSArray_int: return intArray;
case NSValue_range: return new int[] {int1, int2};
case NSValue_point: return new float[] {float1, float2};
case NSValue_size: return new float[] {float1, float2};
case NSValue_rectangle: return new float[] {float1, float2, float3, float4};
case NSString: return string;
case NSAttributedString: return string;
}
return null;
}
@Override
public String toString() {
Object v = getValue();
switch (type) {
case NSArray_id: v = Arrays.toString((long[])v); break;
case NSArray_int: v = Arrays.toString((int[])v); break;
case NSValue_range: v = Arrays.toString((int[])v); break;
case NSAttributedString: v += Arrays.toString(variantArray); break;
case NSDictionary: v = "keys: " + Arrays.toString(longArray) + " values: " + Arrays.toString(variantArray);
}
return "MacVariant type: " + type + " value " + v;
}
}

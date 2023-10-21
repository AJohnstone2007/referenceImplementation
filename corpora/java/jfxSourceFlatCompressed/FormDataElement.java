package com.sun.webkit.network;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
abstract class FormDataElement {
private InputStream inputStream;
void open() throws IOException {
inputStream = createInputStream();
}
long getSize() {
if (inputStream == null) {
throw new IllegalStateException();
}
return doGetSize();
}
InputStream getInputStream() {
if (inputStream == null) {
throw new IllegalStateException();
}
return inputStream;
}
void close() throws IOException {
if (inputStream != null) {
inputStream.close();
inputStream = null;
}
}
protected abstract InputStream createInputStream() throws IOException;
protected abstract long doGetSize();
private static FormDataElement fwkCreateFromByteArray(byte[] byteArray) {
return new ByteArrayElement(byteArray);
}
private static FormDataElement fwkCreateFromFile(String fileName) {
return new FileElement(fileName);
}
private static final class ByteArrayElement extends FormDataElement {
private final byte[] byteArray;
private ByteArrayElement(byte[] byteArray) {
this.byteArray = byteArray;
}
@Override
protected InputStream createInputStream() {
return new ByteArrayInputStream(byteArray);
}
@Override
protected long doGetSize() {
return byteArray.length;
}
}
private static final class FileElement extends FormDataElement {
private final File file;
private FileElement(String filename) {
file = new File(filename);
}
@Override
protected InputStream createInputStream() throws IOException {
return new BufferedInputStream(new FileInputStream(file));
}
@Override
protected long doGetSize() {
return file.length();
}
}
}

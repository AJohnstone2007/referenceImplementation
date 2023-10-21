package demo.parallel;
public class Complex {
private double re;
private double im;
public Complex(double real, double imag) {
re = real;
im = imag;
}
public Complex plus(Complex b) {
re += b.re;
im += b.im;
return this;
}
public Complex times(Complex b) {
Complex a = this;
double real = a.re * b.re - a.im * b.im;
double imag = a.re * b.im + a.im * b.re;
re = real;
im = imag;
return this;
}
public double lengthSQ() {
return re * re + im * im;
}
}
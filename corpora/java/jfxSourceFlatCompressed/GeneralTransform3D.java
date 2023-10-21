package com.sun.javafx.geom.transform;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.Vec3f;
public final class GeneralTransform3D implements CanTransformVec3d {
protected double[] mat = new double[16];
private boolean identity;
public GeneralTransform3D() {
setIdentity();
}
public boolean isAffine() {
if (!isInfOrNaN() &&
almostZero(mat[12]) &&
almostZero(mat[13]) &&
almostZero(mat[14]) &&
almostOne(mat[15])) {
return true;
} else {
return false;
}
}
public GeneralTransform3D set(GeneralTransform3D t1) {
System.arraycopy(t1.mat, 0, mat, 0, mat.length);
updateState();
return this;
}
public GeneralTransform3D set(double[] m) {
System.arraycopy(m, 0, mat, 0, mat.length);
updateState();
return this;
}
public double[] get(double[] rv) {
if (rv == null) {
rv = new double[mat.length];
}
System.arraycopy(mat, 0, rv, 0, mat.length);
return rv;
}
public double get(int index) {
assert ((index >= 0) && (index < mat.length));
return mat[index];
}
private Vec3d tempV3d;
public BaseBounds transform(BaseBounds src, BaseBounds dst) {
if (tempV3d == null) {
tempV3d = new Vec3d();
}
return TransformHelper.general3dBoundsTransform(this, src, dst, tempV3d);
}
public Point2D transform(Point2D point, Point2D pointOut) {
if (pointOut == null) {
pointOut = new Point2D();
}
double w = mat[12] * point.x + mat[13] * point.y + mat[15];
float outX = (float) (mat[0] * point.x + mat[1] * point.y + mat[3]);
pointOut.y = (float) (mat[4] * point.x + mat[5] * point.y + mat[7]);
pointOut.x = outX;
if (w != 0.0) {
pointOut.x /= w;
pointOut.y /= w;
}
return pointOut;
}
public Vec3d transform(Vec3d point, Vec3d pointOut) {
if (pointOut == null) {
pointOut = new Vec3d();
}
double w = mat[12] * point.x + mat[13] * point.y
+ mat[14] * point.z + mat[15];
double outX = mat[0] * point.x + mat[1] * point.y
+ mat[2] * point.z + mat[3];
double outY = mat[4] * point.x + mat[5] * point.y
+ mat[6] * point.z + mat[7];
pointOut.z = mat[8] * point.x + mat[9] * point.y
+ mat[10] * point.z + mat[11];
pointOut.x = outX;
pointOut.y = outY;
if (w != 0.0) {
pointOut.x /= w;
pointOut.y /= w;
pointOut.z /= w;
}
return pointOut;
}
public Vec3d transform(Vec3d point) {
return transform(point, point);
}
public Vec3f transformNormal(Vec3f normal, Vec3f normalOut) {
if (normalOut == null) {
normalOut = new Vec3f();
}
float outX = (float) (mat[0] * normal.x + mat[1] * normal.y +
mat[2] * normal.z);
float outY = (float) (mat[4] * normal.x + mat[5] * normal.y +
mat[6] * normal.z);
normalOut.z = (float) (mat[8] * normal.x + mat[9] * normal.y +
mat[10] * normal.z);
normalOut.x = outX;
normalOut.y = outY;
return normalOut;
}
public Vec3f transformNormal(Vec3f normal) {
return transformNormal(normal, normal);
}
public GeneralTransform3D perspective(boolean verticalFOV,
double fov, double aspect, double zNear, double zFar) {
double sine;
double cotangent;
double deltaZ;
double half_fov = fov * 0.5;
deltaZ = zFar - zNear;
sine = Math.sin(half_fov);
cotangent = Math.cos(half_fov) / sine;
mat[0] = verticalFOV ? cotangent / aspect : cotangent;
mat[5] = verticalFOV ? cotangent : cotangent * aspect;
mat[10] = -(zFar + zNear) / deltaZ;
mat[11] = -2.0 * zNear * zFar / deltaZ;
mat[14] = -1.0;
mat[1] = mat[2] = mat[3] = mat[4] = mat[6] = mat[7] = mat[8] = mat[9] = mat[12] = mat[13] = mat[15] = 0;
updateState();
return this;
}
public GeneralTransform3D ortho(double left, double right, double bottom,
double top, double near, double far) {
double deltax = 1 / (right - left);
double deltay = 1 / (top - bottom);
double deltaz = 1 / (far - near);
mat[0] = 2.0 * deltax;
mat[3] = -(right + left) * deltax;
mat[5] = 2.0 * deltay;
mat[7] = -(top + bottom) * deltay;
mat[10] = 2.0 * deltaz;
mat[11] = (far + near) * deltaz;
mat[1] = mat[2] = mat[4] = mat[6] = mat[8] =
mat[9] = mat[12] = mat[13] = mat[14] = 0;
mat[15] = 1;
updateState();
return this;
}
public double computeClipZCoord() {
double zEc = (1.0 - mat[15]) / mat[14];
double zCc = mat[10] * zEc + mat[11];
return zCc;
}
public double determinant() {
return mat[0]*(mat[5]*(mat[10]*mat[15] - mat[11]*mat[14]) -
mat[6]*(mat[ 9]*mat[15] - mat[11]*mat[13]) +
mat[7]*(mat[ 9]*mat[14] - mat[10]*mat[13])) -
mat[1]*(mat[4]*(mat[10]*mat[15] - mat[11]*mat[14]) -
mat[6]*(mat[ 8]*mat[15] - mat[11]*mat[12]) +
mat[7]*(mat[ 8]*mat[14] - mat[10]*mat[12])) +
mat[2]*(mat[4]*(mat[ 9]*mat[15] - mat[11]*mat[13]) -
mat[5]*(mat[ 8]*mat[15] - mat[11]*mat[12]) +
mat[7]*(mat[ 8]*mat[13] - mat[ 9]*mat[12])) -
mat[3]*(mat[4]*(mat[ 9]*mat[14] - mat[10]*mat[13]) -
mat[5]*(mat[ 8]*mat[14] - mat[10]*mat[12]) +
mat[6]*(mat[ 8]*mat[13] - mat[ 9]*mat[12]));
}
public GeneralTransform3D invert() {
return invert(this);
}
private GeneralTransform3D invert(GeneralTransform3D t1) {
double[] tmp = new double[16];
int[] row_perm = new int[4];
System.arraycopy(t1.mat, 0, tmp, 0, tmp.length);
if (!luDecomposition(tmp, row_perm)) {
throw new SingularMatrixException();
}
mat[0] = 1.0; mat[1] = 0.0; mat[2] = 0.0; mat[3] = 0.0;
mat[4] = 0.0; mat[5] = 1.0; mat[6] = 0.0; mat[7] = 0.0;
mat[8] = 0.0; mat[9] = 0.0; mat[10] = 1.0; mat[11] = 0.0;
mat[12] = 0.0; mat[13] = 0.0; mat[14] = 0.0; mat[15] = 1.0;
luBacksubstitution(tmp, row_perm, this.mat);
updateState();
return this;
}
private static boolean luDecomposition(double[] matrix0,
int[] row_perm) {
double row_scale[] = new double[4];
{
int i, j;
int ptr, rs;
double big, temp;
ptr = 0;
rs = 0;
i = 4;
while (i-- != 0) {
big = 0.0;
j = 4;
while (j-- != 0) {
temp = matrix0[ptr++];
temp = Math.abs(temp);
if (temp > big) {
big = temp;
}
}
if (big == 0.0) {
return false;
}
row_scale[rs++] = 1.0 / big;
}
}
{
int j;
int mtx;
mtx = 0;
for (j = 0; j < 4; j++) {
int i, imax, k;
int target, p1, p2;
double sum, big, temp;
for (i = 0; i < j; i++) {
target = mtx + (4*i) + j;
sum = matrix0[target];
k = i;
p1 = mtx + (4*i);
p2 = mtx + j;
while (k-- != 0) {
sum -= matrix0[p1] * matrix0[p2];
p1++;
p2 += 4;
}
matrix0[target] = sum;
}
big = 0.0;
imax = -1;
for (i = j; i < 4; i++) {
target = mtx + (4*i) + j;
sum = matrix0[target];
k = j;
p1 = mtx + (4*i);
p2 = mtx + j;
while (k-- != 0) {
sum -= matrix0[p1] * matrix0[p2];
p1++;
p2 += 4;
}
matrix0[target] = sum;
if ((temp = row_scale[i] * Math.abs(sum)) >= big) {
big = temp;
imax = i;
}
}
if (imax < 0) {
return false;
}
if (j != imax) {
k = 4;
p1 = mtx + (4*imax);
p2 = mtx + (4*j);
while (k-- != 0) {
temp = matrix0[p1];
matrix0[p1++] = matrix0[p2];
matrix0[p2++] = temp;
}
row_scale[imax] = row_scale[j];
}
row_perm[j] = imax;
if (matrix0[(mtx + (4*j) + j)] == 0.0) {
return false;
}
if (j != (4-1)) {
temp = 1.0 / (matrix0[(mtx + (4*j) + j)]);
target = mtx + (4*(j+1)) + j;
i = 3 - j;
while (i-- != 0) {
matrix0[target] *= temp;
target += 4;
}
}
}
}
return true;
}
private static void luBacksubstitution(double[] matrix1,
int[] row_perm,
double[] matrix2) {
int i, ii, ip, j, k;
int rp;
int cv, rv;
rp = 0;
for (k = 0; k < 4; k++) {
cv = k;
ii = -1;
for (i = 0; i < 4; i++) {
double sum;
ip = row_perm[rp+i];
sum = matrix2[cv+4*ip];
matrix2[cv+4*ip] = matrix2[cv+4*i];
if (ii >= 0) {
rv = i*4;
for (j = ii; j <= i-1; j++) {
sum -= matrix1[rv+j] * matrix2[cv+4*j];
}
}
else if (sum != 0.0) {
ii = i;
}
matrix2[cv+4*i] = sum;
}
rv = 3*4;
matrix2[cv+4*3] /= matrix1[rv+3];
rv -= 4;
matrix2[cv+4*2] = (matrix2[cv+4*2] -
matrix1[rv+3] * matrix2[cv+4*3]) / matrix1[rv+2];
rv -= 4;
matrix2[cv+4*1] = (matrix2[cv+4*1] -
matrix1[rv+2] * matrix2[cv+4*2] -
matrix1[rv+3] * matrix2[cv+4*3]) / matrix1[rv+1];
rv -= 4;
matrix2[cv+4*0] = (matrix2[cv+4*0] -
matrix1[rv+1] * matrix2[cv+4*1] -
matrix1[rv+2] * matrix2[cv+4*2] -
matrix1[rv+3] * matrix2[cv+4*3]) / matrix1[rv+0];
}
}
public GeneralTransform3D mul(BaseTransform t1) {
if (t1.isIdentity()) {
return this;
}
double tmp0, tmp1, tmp2, tmp3;
double tmp4, tmp5, tmp6, tmp7;
double tmp8, tmp9, tmp10, tmp11;
double tmp12, tmp13, tmp14, tmp15;
double mxx = t1.getMxx();
double mxy = t1.getMxy();
double mxz = t1.getMxz();
double mxt = t1.getMxt();
double myx = t1.getMyx();
double myy = t1.getMyy();
double myz = t1.getMyz();
double myt = t1.getMyt();
double mzx = t1.getMzx();
double mzy = t1.getMzy();
double mzz = t1.getMzz();
double mzt = t1.getMzt();
tmp0 = mat[0] * mxx + mat[1] * myx + mat[2] * mzx;
tmp1 = mat[0] * mxy + mat[1] * myy + mat[2] * mzy;
tmp2 = mat[0] * mxz + mat[1] * myz + mat[2] * mzz;
tmp3 = mat[0] * mxt + mat[1] * myt + mat[2] * mzt + mat[3];
tmp4 = mat[4] * mxx + mat[5] * myx + mat[6] * mzx;
tmp5 = mat[4] * mxy + mat[5] * myy + mat[6] * mzy;
tmp6 = mat[4] * mxz + mat[5] * myz + mat[6] * mzz;
tmp7 = mat[4] * mxt + mat[5] * myt + mat[6] * mzt + mat[7];
tmp8 = mat[8] * mxx + mat[9] * myx + mat[10] * mzx;
tmp9 = mat[8] * mxy + mat[9] * myy + mat[10] * mzy;
tmp10 = mat[8] * mxz + mat[9] * myz + mat[10] * mzz;
tmp11 = mat[8] * mxt + mat[9] * myt + mat[10] * mzt + mat[11];
if (isAffine()) {
tmp12 = tmp13 = tmp14 = 0;
tmp15 = 1;
}
else {
tmp12 = mat[12] * mxx + mat[13] * myx + mat[14] * mzx;
tmp13 = mat[12] * mxy + mat[13] * myy + mat[14] * mzy;
tmp14 = mat[12] * mxz + mat[13] * myz + mat[14] * mzz;
tmp15 = mat[12] * mxt + mat[13] * myt + mat[14] * mzt + mat[15];
}
mat[0] = tmp0;
mat[1] = tmp1;
mat[2] = tmp2;
mat[3] = tmp3;
mat[4] = tmp4;
mat[5] = tmp5;
mat[6] = tmp6;
mat[7] = tmp7;
mat[8] = tmp8;
mat[9] = tmp9;
mat[10] = tmp10;
mat[11] = tmp11;
mat[12] = tmp12;
mat[13] = tmp13;
mat[14] = tmp14;
mat[15] = tmp15;
updateState();
return this;
}
public GeneralTransform3D scale(double sx, double sy, double sz) {
boolean update = false;
if (sx != 1.0) {
mat[0] *= sx;
mat[4] *= sx;
mat[8] *= sx;
mat[12] *= sx;
update = true;
}
if (sy != 1.0) {
mat[1] *= sy;
mat[5] *= sy;
mat[9] *= sy;
mat[13] *= sy;
update = true;
}
if (sz != 1.0) {
mat[2] *= sz;
mat[6] *= sz;
mat[10] *= sz;
mat[14] *= sz;
update = true;
}
if (update) {
updateState();
}
return this;
}
public GeneralTransform3D mul(GeneralTransform3D t1) {
if (t1.isIdentity()) {
return this;
}
double tmp0, tmp1, tmp2, tmp3;
double tmp4, tmp5, tmp6, tmp7;
double tmp8, tmp9, tmp10, tmp11;
double tmp12, tmp13, tmp14, tmp15;
if (t1.isAffine()) {
tmp0 = mat[0] * t1.mat[0] + mat[1] * t1.mat[4] + mat[2] * t1.mat[8];
tmp1 = mat[0] * t1.mat[1] + mat[1] * t1.mat[5] + mat[2] * t1.mat[9];
tmp2 = mat[0] * t1.mat[2] + mat[1] * t1.mat[6] + mat[2] * t1.mat[10];
tmp3 = mat[0] * t1.mat[3] + mat[1] * t1.mat[7] + mat[2] * t1.mat[11] + mat[3];
tmp4 = mat[4] * t1.mat[0] + mat[5] * t1.mat[4] + mat[6] * t1.mat[8];
tmp5 = mat[4] * t1.mat[1] + mat[5] * t1.mat[5] + mat[6] * t1.mat[9];
tmp6 = mat[4] * t1.mat[2] + mat[5] * t1.mat[6] + mat[6] * t1.mat[10];
tmp7 = mat[4] * t1.mat[3] + mat[5] * t1.mat[7] + mat[6] * t1.mat[11] + mat[7];
tmp8 = mat[8] * t1.mat[0] + mat[9] * t1.mat[4] + mat[10] * t1.mat[8];
tmp9 = mat[8] * t1.mat[1] + mat[9] * t1.mat[5] + mat[10] * t1.mat[9];
tmp10 = mat[8] * t1.mat[2] + mat[9] * t1.mat[6] + mat[10] * t1.mat[10];
tmp11 = mat[8] * t1.mat[3] + mat[9] * t1.mat[7] + mat[10] * t1.mat[11] + mat[11];
if (isAffine()) {
tmp12 = tmp13 = tmp14 = 0;
tmp15 = 1;
}
else {
tmp12 = mat[12] * t1.mat[0] + mat[13] * t1.mat[4] +
mat[14] * t1.mat[8];
tmp13 = mat[12] * t1.mat[1] + mat[13] * t1.mat[5] +
mat[14] * t1.mat[9];
tmp14 = mat[12] * t1.mat[2] + mat[13] * t1.mat[6] +
mat[14] * t1.mat[10];
tmp15 = mat[12] * t1.mat[3] + mat[13] * t1.mat[7] +
mat[14] * t1.mat[11] + mat[15];
}
} else {
tmp0 = mat[0] * t1.mat[0] + mat[1] * t1.mat[4] + mat[2] * t1.mat[8] +
mat[3] * t1.mat[12];
tmp1 = mat[0] * t1.mat[1] + mat[1] * t1.mat[5] + mat[2] * t1.mat[9] +
mat[3] * t1.mat[13];
tmp2 = mat[0] * t1.mat[2] + mat[1] * t1.mat[6] + mat[2] * t1.mat[10] +
mat[3] * t1.mat[14];
tmp3 = mat[0] * t1.mat[3] + mat[1] * t1.mat[7] + mat[2] * t1.mat[11] +
mat[3] * t1.mat[15];
tmp4 = mat[4] * t1.mat[0] + mat[5] * t1.mat[4] + mat[6] * t1.mat[8] +
mat[7] * t1.mat[12];
tmp5 = mat[4] * t1.mat[1] + mat[5] * t1.mat[5] + mat[6] * t1.mat[9] +
mat[7] * t1.mat[13];
tmp6 = mat[4] * t1.mat[2] + mat[5] * t1.mat[6] + mat[6] * t1.mat[10] +
mat[7] * t1.mat[14];
tmp7 = mat[4] * t1.mat[3] + mat[5] * t1.mat[7] + mat[6] * t1.mat[11] +
mat[7] * t1.mat[15];
tmp8 = mat[8] * t1.mat[0] + mat[9] * t1.mat[4] + mat[10] * t1.mat[8] +
mat[11] * t1.mat[12];
tmp9 = mat[8] * t1.mat[1] + mat[9] * t1.mat[5] + mat[10] * t1.mat[9] +
mat[11] * t1.mat[13];
tmp10 = mat[8] * t1.mat[2] + mat[9] * t1.mat[6] +
mat[10] * t1.mat[10] + mat[11] * t1.mat[14];
tmp11 = mat[8] * t1.mat[3] + mat[9] * t1.mat[7] +
mat[10] * t1.mat[11] + mat[11] * t1.mat[15];
if (isAffine()) {
tmp12 = t1.mat[12];
tmp13 = t1.mat[13];
tmp14 = t1.mat[14];
tmp15 = t1.mat[15];
} else {
tmp12 = mat[12] * t1.mat[0] + mat[13] * t1.mat[4] +
mat[14] * t1.mat[8] + mat[15] * t1.mat[12];
tmp13 = mat[12] * t1.mat[1] + mat[13] * t1.mat[5] +
mat[14] * t1.mat[9] + mat[15] * t1.mat[13];
tmp14 = mat[12] * t1.mat[2] + mat[13] * t1.mat[6] +
mat[14] * t1.mat[10] + mat[15] * t1.mat[14];
tmp15 = mat[12] * t1.mat[3] + mat[13] * t1.mat[7] +
mat[14] * t1.mat[11] + mat[15] * t1.mat[15];
}
}
mat[0] = tmp0;
mat[1] = tmp1;
mat[2] = tmp2;
mat[3] = tmp3;
mat[4] = tmp4;
mat[5] = tmp5;
mat[6] = tmp6;
mat[7] = tmp7;
mat[8] = tmp8;
mat[9] = tmp9;
mat[10] = tmp10;
mat[11] = tmp11;
mat[12] = tmp12;
mat[13] = tmp13;
mat[14] = tmp14;
mat[15] = tmp15;
updateState();
return this;
}
public GeneralTransform3D setIdentity() {
mat[0] = 1.0; mat[1] = 0.0; mat[2] = 0.0; mat[3] = 0.0;
mat[4] = 0.0; mat[5] = 1.0; mat[6] = 0.0; mat[7] = 0.0;
mat[8] = 0.0; mat[9] = 0.0; mat[10] = 1.0; mat[11] = 0.0;
mat[12] = 0.0; mat[13] = 0.0; mat[14] = 0.0; mat[15] = 1.0;
identity = true;
return this;
}
public boolean isIdentity() {
return identity;
}
private void updateState() {
identity =
mat[0] == 1.0 && mat[5] == 1.0 && mat[10] == 1.0 && mat[15] == 1.0 &&
mat[1] == 0.0 && mat[2] == 0.0 && mat[3] == 0.0 &&
mat[4] == 0.0 && mat[6] == 0.0 && mat[7] == 0.0 &&
mat[8] == 0.0 && mat[9] == 0.0 && mat[11] == 0.0 &&
mat[12] == 0.0 && mat[13] == 0.0 && mat[14] == 0.0;
}
boolean isInfOrNaN() {
double d = 0.0;
for (int i = 0; i < mat.length; i++) {
d *= mat[i];
}
return d != 0.0;
}
private static final double EPSILON_ABSOLUTE = 1.0e-5;
static boolean almostZero(double a) {
return ((a < EPSILON_ABSOLUTE) && (a > -EPSILON_ABSOLUTE));
}
static boolean almostOne(double a) {
return ((a < 1+EPSILON_ABSOLUTE) && (a > 1-EPSILON_ABSOLUTE));
}
public GeneralTransform3D copy() {
GeneralTransform3D newTransform = new GeneralTransform3D();
newTransform.set(this);
return newTransform;
}
@Override
public String toString() {
return mat[0] + ", " + mat[1] + ", " + mat[2] + ", " + mat[3] + "\n" +
mat[4] + ", " + mat[5] + ", " + mat[6] + ", " + mat[7] + "\n" +
mat[8] + ", " + mat[9] + ", " + mat[10] + ", " + mat[11] + "\n" +
mat[12] + ", " + mat[13] + ", " + mat[14] + ", " + mat[15] + "\n";
}
}

// src/main/java/com/mcweb/core/Matrix4f.java
package com.mcweb.core;

public class Matrix4f {

    public float[] m = new float[16];

    public Matrix4f() {
        identity();
    }

    public void identity() {
        for (int i = 0; i < 16; i++) m[i] = 0;
        m[0] = m[5] = m[10] = m[15] = 1;
    }

    public void perspective(float fov, float aspect, float near, float far) {
        float f = (float) (1.0 / Math.tan(fov * 0.5));
        float rangeInv = 1.0f / (near - far);

        identity();
        m[0] = f / aspect;
        m[5] = f;
        m[10] = (near + far) * rangeInv;
        m[11] = -1;
        m[14] = 2 * near * far * rangeInv;
        m[15] = 0;
    }

    public void translate(float x, float y, float z) {
        Matrix4f t = new Matrix4f();
        t.m[12] = x;
        t.m[13] = y;
        t.m[14] = z;
        set(multiply(this, t));
    }

    public void rotateX(float angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        Matrix4f r = new Matrix4f();
        r.m[5] = c;
        r.m[6] = s;
        r.m[9] = -s;
        r.m[10] = c;
        set(multiply(this, r));
    }

    public void rotateY(float angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        Matrix4f r = new Matrix4f();
        r.m[0] = c;
        r.m[2] = -s;
        r.m[8] = s;
        r.m[10] = c;
        set(multiply(this, r));
    }

    public void scale(float x, float y, float z) {
        Matrix4f s = new Matrix4f();
        s.m[0] = x;
        s.m[5] = y;
        s.m[10] = z;
        set(multiply(this, s));
    }

    private void set(Matrix4f other) {
        System.arraycopy(other.m, 0, this.m, 0, 16);
    }

    public static Matrix4f multiply(Matrix4f a, Matrix4f b) {
        Matrix4f result = new Matrix4f();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                float sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += a.m[row + k * 4] * b.m[k + col * 4];
                }
                result.m[row + col * 4] = sum;
            }
        }
        return result;
    }
}

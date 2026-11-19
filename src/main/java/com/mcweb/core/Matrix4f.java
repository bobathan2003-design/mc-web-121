// src/main/java/com/mcweb/core/Matrix4f.java
package com.mcweb.core;

public class Matrix4f {

    public float[] m = new float[16];

    public Matrix4f() {
        identity();
    }

    public void identity() {
        for (int i = 0; i < 16; i++) m[i] = 0;
        m[0] = 1; m[5] = 1; m[10] = 1; m[15] = 1;
    }

    public void perspective(float fov, float aspect, float near, float far) {
        identity();
        float f = (float)(1.0 / Math.tan(fov * 0.5));
        float nf = 1.0f / (near - far);

        m[0] = f / aspect;
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;

        m[4] = 0;
        m[5] = f;
        m[6] = 0;
        m[7] = 0;

        m[8] = 0;
        m[9] = 0;
        m[10] = (far + near) * nf;
        m[11] = -1;

        m[12] = 0;
        m[13] = 0;
        m[14] = 2 * far * near * nf;
        m[15] = 0;
    }

    public void translate(float x, float y, float z) {
        // Multiply current matrix by translation
        m[12] = m[0]*x + m[4]*y + m[8]*z + m[12];
        m[13] = m[1]*x + m[5]*y + m[9]*z + m[13];
        m[14] = m[2]*x + m[6]*y + m[10]*z + m[14];
        m[15] = m[3]*x + m[7]*y + m[11]*z + m[15];
    }

    public void rotateX(float angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);

        float m4  = m[4],  m5  = m[5],  m6  = m[6],  m7  = m[7];
        float m8  = m[8],  m9  = m[9],  m10 = m[10], m11 = m[11];

        m[4]  = m4 * c + m8 * s;
        m[5]  = m5 * c + m9 * s;
        m[6]  = m6 * c + m10 * s;
        m[7]  = m7 * c + m11 * s;
        m[8]  = m8 * c - m4 * s;
        m[9]  = m9 * c - m5 * s;
        m[10] = m10 * c - m6 * s;
        m[11] = m11 * c - m7 * s;
    }

    public void rotateY(float angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);

        float m0 = m[0], m1 = m[1], m2  = m[2],  m3  = m[3];
        float m8 = m[8], m9 = m[9], m10 = m[10], m11 = m[11];

        m[0]  = m0 * c - m8 * s;
        m[1]  = m1 * c - m9 * s;
        m[2]  = m2 * c - m10 * s;
        m[3]  = m3 * c - m11 * s;
        m[8]  = m0 * s + m8 * c;
        m[9]  = m1 * s + m9 * c;
        m[10] = m2 * s + m10 * c;
        m[11] = m3 * s + m11 * c;
    }

    public void scale(float x, float y, float z) {
        m[0] *= x; m[1] *= x; m[2]  *= x; m[3]  *= x;
        m[4] *= y; m[5] *= y; m[6]  *= y; m[7]  *= y;
        m[8] *= z; m[9] *= z; m[10] *= z; m[11] *= z;
    }
}

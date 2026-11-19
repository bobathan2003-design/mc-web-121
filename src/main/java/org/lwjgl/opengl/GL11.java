// src/main/java/org/lwjgl/opengl/GL11.java
package org.lwjgl.opengl;

import com.mcweb.core.WebGL2;
import com.mcweb.registry.ObjectRegistry;
import org.teavm.jso.JSObject;

public class GL11 {

    private static WebGL2 gl;
    private static final ObjectRegistry textures = new ObjectRegistry();

    // Constants matching real OpenGL
    public static final int GL_TRUE = 1;
    public static final int GL_FALSE = 0;
    public static final int GL_COLOR_BUFFER_BIT = 0x4000;
    public static final int GL_DEPTH_BUFFER_BIT = 0x100;
    public static final int GL_STENCIL_BUFFER_BIT = 0x400;
    public static final int GL_DEPTH_TEST = 0x0B71;
    public static final int GL_BLEND = 0x0BE2;
    public static final int GL_CULL_FACE = 0x0B44;
    public static final int GL_SCISSOR_TEST = 0x0C11;
    public static final int GL_TEXTURE_2D = 0x0DE1;
    public static final int GL_FLOAT = 0x1406;
    public static final int GL_UNSIGNED_BYTE = 0x1401;
    public static final int GL_UNSIGNED_SHORT = 0x1403;
    public static final int GL_UNSIGNED_INT = 0x1405;
    public static final int GL_INT = 0x1404;
    public static final int GL_BYTE = 0x1400;
    public static final int GL_TRIANGLES = 0x0004;
    public static final int GL_TRIANGLE_STRIP = 0x0005;
    public static final int GL_TRIANGLE_FAN = 0x0006;
    public static final int GL_LINES = 0x0001;
    public static final int GL_POINTS = 0x0000;
    public static final int GL_SRC_ALPHA = 0x0302;
    public static final int GL_ONE_MINUS_SRC_ALPHA = 0x0303;
    public static final int GL_ONE = 1;
    public static final int GL_ZERO = 0;
    public static final int GL_RGBA = 0x1908;
    public static final int GL_RGB = 0x1907;
    public static final int GL_TEXTURE_MIN_FILTER = 0x2801;
    public static final int GL_TEXTURE_MAG_FILTER = 0x2800;
    public static final int GL_TEXTURE_WRAP_S = 0x2802;
    public static final int GL_TEXTURE_WRAP_T = 0x2803;
    public static final int GL_NEAREST = 0x2600;
    public static final int GL_LINEAR = 0x2601;
    public static final int GL_REPEAT = 0x2901;
    public static final int GL_CLAMP_TO_EDGE = 0x812F;
    public static final int GL_NO_ERROR = 0;
    public static final int GL_VENDOR = 0x1F00;
    public static final int GL_RENDERER = 0x1F01;
    public static final int GL_VERSION = 0x1F02;

    public static void init(WebGL2 context) {
        gl = context;
    }

    public static WebGL2 getContext() {
        return gl;
    }

    public static ObjectRegistry getTextureRegistry() {
        return textures;
    }

    public static void glEnable(int cap) {
        gl.enable(cap);
    }

    public static void glDisable(int cap) {
        gl.disable(cap);
    }

    public static void glClear(int mask) {
        gl.clear(mask);
    }

    public static void glClearColor(float r, float g, float b, float a) {
        gl.clearColor(r, g, b, a);
    }

    public static void glViewport(int x, int y, int w, int h) {
        gl.viewport(x, y, w, h);
    }

    public static void glBlendFunc(int sfactor, int dfactor) {
        gl.blendFunc(sfactor, dfactor);
    }

    public static void glDepthMask(boolean flag) {
        gl.depthMask(flag);
    }

    public static int glGenTextures() {
        JSObject tex = gl.createTexture();
        return textures.register(tex);
    }

    public static void glDeleteTextures(int texture) {
        textures.delete(texture);
    }

    public static void glBindTexture(int target, int texture) {
        gl.bindTexture(target, textures.get(texture));
    }

    public static void glTexParameteri(int target, int pname, int param) {
        gl.texParameteri(target, pname, param);
    }

    public static void glDrawArrays(int mode, int first, int count) {
        gl.drawArrays(mode, first, count);
    }

    public static void glDrawElements(int mode, int count, int type, long offset) {
        gl.drawElements(mode, count, type, (int) offset);
    }

    public static int glGetError() {
        return GL_NO_ERROR; // TODO: implement properly
    }

    public static String glGetString(int name) {
        return switch (name) {
            case GL_VENDOR -> "MCWeb";
            case GL_RENDERER -> "WebGL2 Translation Layer";
            case GL_VERSION -> "WebGL 2.0 (OpenGL ES 3.0)";
            default -> "";
        };
    }
}

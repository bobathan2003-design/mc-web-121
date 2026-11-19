// src/main/java/com/mcweb/core/WebGL2.java
package com.mcweb.core;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Uint8Array;

public abstract class WebGL2 implements JSObject {

    // Constants
    public static final int COLOR_BUFFER_BIT = 0x4000;
    public static final int DEPTH_BUFFER_BIT = 0x100;
    public static final int STENCIL_BUFFER_BIT = 0x400;

    public static final int TRIANGLES = 0x0004;
    public static final int TRIANGLE_STRIP = 0x0005;
    public static final int TRIANGLE_FAN = 0x0006;
    public static final int LINES = 0x0001;
    public static final int POINTS = 0x0000;

    public static final int DEPTH_TEST = 0x0B71;
    public static final int BLEND = 0x0BE2;
    public static final int CULL_FACE = 0x0B44;
    public static final int SCISSOR_TEST = 0x0C11;

    public static final int FLOAT = 0x1406;
    public static final int UNSIGNED_BYTE = 0x1401;
    public static final int UNSIGNED_SHORT = 0x1403;
    public static final int UNSIGNED_INT = 0x1405;
    public static final int INT = 0x1404;
    public static final int BYTE = 0x1400;

    public static final int ARRAY_BUFFER = 0x8892;
    public static final int ELEMENT_ARRAY_BUFFER = 0x8893;
    public static final int STATIC_DRAW = 0x88E4;
    public static final int DYNAMIC_DRAW = 0x88E8;

    public static final int VERTEX_SHADER = 0x8B31;
    public static final int FRAGMENT_SHADER = 0x8B30;
    public static final int COMPILE_STATUS = 0x8B81;
    public static final int LINK_STATUS = 0x8B82;

    public static final int TEXTURE_2D = 0x0DE1;
    public static final int TEXTURE0 = 0x84C0;
    public static final int TEXTURE_MIN_FILTER = 0x2801;
    public static final int TEXTURE_MAG_FILTER = 0x2800;
    public static final int TEXTURE_WRAP_S = 0x2802;
    public static final int TEXTURE_WRAP_T = 0x2803;
    public static final int NEAREST = 0x2600;
    public static final int LINEAR = 0x2601;
    public static final int CLAMP_TO_EDGE = 0x812F;
    public static final int REPEAT = 0x2901;
    public static final int RGBA = 0x1908;
    public static final int RGB = 0x1907;

    public static final int SRC_ALPHA = 0x0302;
    public static final int ONE_MINUS_SRC_ALPHA = 0x0303;
    public static final int ONE = 1;
    public static final int ZERO = 0;

    public static final int FRAMEBUFFER = 0x8D40;
    public static final int COLOR_ATTACHMENT0 = 0x8CE0;
    public static final int DEPTH_ATTACHMENT = 0x8D00;

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    // Get context
    @JSBody(params = {"canvas"}, script = 
        "return canvas.getContext('webgl2', {antialias: false, alpha: false});")
    public static native WebGL2 create(HTMLCanvasElement canvas);

    // Clear
    @JSBody(params = {"r","g","b","a"}, script = "this.clearColor(r,g,b,a);")
    public native void clearColor(float r, float g, float b, float a);

    @JSBody(params = {"mask"}, script = "this.clear(mask);")
    public native void clear(int mask);

    // State
    @JSBody(params = {"cap"}, script = "this.enable(cap);")
    public native void enable(int cap);

    @JSBody(params = {"cap"}, script = "this.disable(cap);")
    public native void disable(int cap);

    @JSBody(params = {"x","y","w","h"}, script = "this.viewport(x,y,w,h);")
    public native void viewport(int x, int y, int w, int h);

    @JSBody(params = {"sfactor","dfactor"}, script = "this.blendFunc(sfactor,dfactor);")
    public native void blendFunc(int sfactor, int dfactor);

    @JSBody(params = {"mask"}, script = "this.depthMask(mask);")
    public native void depthMask(boolean mask);

    // Shaders
    @JSBody(params = {"type"}, script = "return this.createShader(type);")
    public native JSObject createShader(int type);

    @JSBody(params = {"shader","source"}, script = "this.shaderSource(shader, source);")
    public native void shaderSource(JSObject shader, String source);

    @JSBody(params = {"shader"}, script = "this.compileShader(shader);")
    public native void compileShader(JSObject shader);

    @JSBody(params = {"shader","pname"}, script = "return this.getShaderParameter(shader, pname);")
    public native boolean getShaderParameter(JSObject shader, int pname);

    @JSBody(params = {"shader"}, script = "return this.getShaderInfoLog(shader);")
    public native String getShaderInfoLog(JSObject shader);

    @JSBody(params = {}, script = "return this.createProgram();")
    public native JSObject createProgram();

    @JSBody(params = {"program","shader"}, script = "this.attachShader(program, shader);")
    public native void attachShader(JSObject program, JSObject shader);

    @JSBody(params = {"program"}, script = "this.linkProgram(program);")
    public native void linkProgram(JSObject program);

    @JSBody(params = {"program","pname"}, script = "return this.getProgramParameter(program, pname);")
    public native boolean getProgramParameter(JSObject program, int pname);

    @JSBody(params = {"program"}, script = "return this.getProgramInfoLog(program);")
    public native String getProgramInfoLog(JSObject program);

    @JSBody(params = {"program"}, script = "this.useProgram(program);")
    public native void useProgram(JSObject program);

    @JSBody(params = {"program","name"}, script = "return this.getUniformLocation(program, name);")
    public native JSObject getUniformLocation(JSObject program, String name);

    @JSBody(params = {"program","name"}, script = "return this.getAttribLocation(program, name);")
    public native int getAttribLocation(JSObject program, String name);

    // Uniforms
    @JSBody(params = {"loc","x"}, script = "this.uniform1i(loc, x);")
    public native void uniform1i(JSObject loc, int x);

    @JSBody(params = {"loc","x"}, script = "this.uniform1f(loc, x);")
    public native void uniform1f(JSObject loc, float x);

    @JSBody(params = {"loc","x","y","z","w"}, script = "this.uniform4f(loc,x,y,z,w);")
    public native void uniform4f(JSObject loc, float x, float y, float z, float w);

    @JSBody(params = {"loc","x","y","z"}, script = "this.uniform3f(loc,x,y,z);")
    public native void uniform3f(JSObject loc, float x, float y, float z);

    @JSBody(params = {"loc","transpose","value"}, 
        script = "this.uniformMatrix4fv(loc, transpose, value);")
    public native void uniformMatrix4fv(JSObject loc, boolean transpose, Float32Array value);

    // Buffers
    @JSBody(params = {}, script = "return this.createBuffer();")
    public native JSObject createBuffer();

    @JSBody(params = {"target","buffer"}, script = "this.bindBuffer(target, buffer);")
    public native void bindBuffer(int target, JSObject buffer);

    @JSBody(params = {"target","data","usage"}, script = "this.bufferData(target, data, usage);")
    public native void bufferData(int target, Float32Array data, int usage);

    @JSBody(params = {"target","data","usage"}, script = "this.bufferData(target, data, usage);")
    public native void bufferDataInt(int target, Int32Array data, int usage);

    @JSBody(params = {"target","data","usage"}, script = "this.bufferData(target, data, usage);")
    public native void bufferDataUint8(int target, Uint8Array data, int usage);

    // VAO
    @JSBody(params = {}, script = "return this.createVertexArray();")
    public native JSObject createVertexArray();

    @JSBody(params = {"vao"}, script = "this.bindVertexArray(vao);")
    public native void bindVertexArray(JSObject vao);

    @JSBody(params = {"index"}, script = "this.enableVertexAttribArray(index);")
    public native void enableVertexAttribArray(int index);

    @JSBody(params = {"index","size","type","normalized","stride","offset"},
        script = "this.vertexAttribPointer(index, size, type, normalized, stride, offset);")
    public native void vertexAttribPointer(int index, int size, int type, 
        boolean normalized, int stride, int offset);

    // Draw
    @JSBody(params = {"mode","first","count"}, script = "this.drawArrays(mode, first, count);")
    public native void drawArrays(int mode, int first, int count);

    @JSBody(params = {"mode","count","type","offset"}, 
        script = "this.drawElements(mode, count, type, offset);")
    public native void drawElements(int mode, int count, int type, int offset);

    // Textures
    @JSBody(params = {}, script = "return this.createTexture();")
    public native JSObject createTexture();

    @JSBody(params = {"target","texture"}, script = "this.bindTexture(target, texture);")
    public native void bindTexture(int target, JSObject texture);

    @JSBody(params = {"target","pname","param"}, script = "this.texParameteri(target,pname,param);")
    public native void texParameteri(int target, int pname, int param);

    @JSBody(params = {"unit"}, script = "this.activeTexture(unit);")
    public native void activeTexture(int unit);

    // Framebuffers
    @JSBody(params = {}, script = "return this.createFramebuffer();")
    public native JSObject createFramebuffer();

    @JSBody(params = {"target","fb"}, script = "this.bindFramebuffer(target, fb);")
    public native void bindFramebuffer(int target, JSObject fb);
}

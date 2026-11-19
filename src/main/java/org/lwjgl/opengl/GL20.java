// src/main/java/org/lwjgl/opengl/GL20.java
package org.lwjgl.opengl;

import com.mcweb.core.WebGL2;
import com.mcweb.glsl.GLSLTranslator;
import com.mcweb.registry.ObjectRegistry;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.Float32Array;

public class GL20 {

    private static final ObjectRegistry shaders = new ObjectRegistry();
    private static final ObjectRegistry programs = new ObjectRegistry();
    private static final ObjectRegistry uniforms = new ObjectRegistry();

    public static final int GL_VERTEX_SHADER = 0x8B31;
    public static final int GL_FRAGMENT_SHADER = 0x8B30;
    public static final int GL_COMPILE_STATUS = 0x8B81;
    public static final int GL_LINK_STATUS = 0x8B82;

    private static WebGL2 gl() {
        return GL11.getContext();
    }

    public static int glCreateShader(int type) {
        JSObject shader = gl().createShader(type);
        return shaders.register(shader);
    }

    public static void glShaderSource(int shader, CharSequence source) {
        String translated = GLSLTranslator.translate(source.toString());
        gl().shaderSource(shaders.get(shader), translated);
    }

    public static void glCompileShader(int shader) {
        JSObject s = shaders.get(shader);
        gl().compileShader(s);
        if (!gl().getShaderParameter(s, GL_COMPILE_STATUS)) {
            String log = gl().getShaderInfoLog(s);
            System.err.println("Shader compile error: " + log);
        }
    }

    public static int glCreateProgram() {
        JSObject program = gl().createProgram();
        return programs.register(program);
    }

    public static void glAttachShader(int program, int shader) {
        gl().attachShader(programs.get(program), shaders.get(shader));
    }

    public static void glLinkProgram(int program) {
        JSObject p = programs.get(program);
        gl().linkProgram(p);
        if (!gl().getProgramParameter(p, GL_LINK_STATUS)) {
            String log = gl().getProgramInfoLog(p);
            System.err.println("Program link error: " + log);
        }
    }

    public static void glUseProgram(int program) {
        gl().useProgram(program == 0 ? null : programs.get(program));
    }

    public static int glGetUniformLocation(int program, CharSequence name) {
        JSObject loc = gl().getUniformLocation(programs.get(program), name.toString());
        if (loc == null) return -1;
        return uniforms.register(loc);
    }

    public static int glGetAttribLocation(int program, CharSequence name) {
        return gl().getAttribLocation(programs.get(program), name.toString());
    }

    public static void glUniform1i(int location, int value) {
        if (location == -1) return;
        gl().uniform1i(uniforms.get(location), value);
    }

    public static void glUniform1f(int location, float value) {
        if (location == -1) return;
        gl().uniform1f(uniforms.get(location), value);
    }

    public static void glUniform3f(int location, float x, float y, float z) {
        if (location == -1) return;
        gl().uniform3f(uniforms.get(location), x, y, z);
    }

    public static void glUniform4f(int location, float x, float y, float z, float w) {
        if (location == -1) return;
        gl().uniform4f(uniforms.get(location), x, y, z, w);
    }

    public static void glUniformMatrix4fv(int location, boolean transpose, float[] value) {
        if (location == -1) return;
        Float32Array arr = Float32Array.create(value.length);
        for (int i = 0; i < value.length; i++) {
            arr.set(i, value[i]);
        }
        gl().uniformMatrix4fv(uniforms.get(location), transpose, arr);
    }

    public static void glEnableVertexAttribArray(int index) {
        gl().enableVertexAttribArray(index);
    }

    public static void glVertexAttribPointer(int index, int size, int type,
            boolean normalized, int stride, long offset) {
        gl().vertexAttribPointer(index, size, type, normalized, stride, (int) offset);
    }

    public static void glDeleteShader(int shader) {
        shaders.delete(shader);
    }

    public static void glDeleteProgram(int program) {
        programs.delete(program);
    }

    public static ObjectRegistry getProgramRegistry() {
        return programs;
    }
}

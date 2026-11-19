// src/main/java/org/lwjgl/opengl/GL15.java
package org.lwjgl.opengl;

import com.mcweb.core.WebGL2;
import com.mcweb.registry.ObjectRegistry;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.Float32Array;

public class GL15 {

    private static final ObjectRegistry buffers = new ObjectRegistry();

    public static final int GL_ARRAY_BUFFER = 0x8892;
    public static final int GL_ELEMENT_ARRAY_BUFFER = 0x8893;
    public static final int GL_STATIC_DRAW = 0x88E4;
    public static final int GL_DYNAMIC_DRAW = 0x88E8;

    private static WebGL2 gl() {
        return GL11.getContext();
    }

    public static int glGenBuffers() {
        JSObject buf = gl().createBuffer();
        return buffers.register(buf);
    }

    public static void glBindBuffer(int target, int buffer) {
        gl().bindBuffer(target, buffer == 0 ? null : buffers.get(buffer));
    }

    public static void glBufferData(int target, float[] data, int usage) {
        Float32Array arr = Float32Array.create(data.length);
        for (int i = 0; i < data.length; i++) {
            arr.set(i, data[i]);
        }
        gl().bufferData(target, arr, usage);
    }

    public static void glDeleteBuffers(int buffer) {
        buffers.delete(buffer);
    }
}

// src/main/java/org/lwjgl/opengl/GL30.java
package org.lwjgl.opengl;

import com.mcweb.core.WebGL2;
import com.mcweb.registry.ObjectRegistry;
import org.teavm.jso.JSObject;

public class GL30 {

    private static final ObjectRegistry vaos = new ObjectRegistry();

    private static WebGL2 gl() {
        return GL11.getContext();
    }

    public static int glGenVertexArrays() {
        JSObject vao = gl().createVertexArray();
        return vaos.register(vao);
    }

    public static void glBindVertexArray(int vao) {
        gl().bindVertexArray(vao == 0 ? null : vaos.get(vao));
    }

    public static void glDeleteVertexArrays(int vao) {
        vaos.delete(vao);
    }
}

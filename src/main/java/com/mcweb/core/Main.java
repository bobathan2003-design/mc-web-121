// src/main/java/com/mcweb/core/Main.java
package com.mcweb.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.html.HTMLCanvasElement;

public class Main {

    private static int shaderProgram;
    private static int vao;
    private static int vbo;
    private static long window;

    private static final String VERTEX_SHADER =
        "#version 330 core\n" +
        "in vec3 aPosition;\n" +
        "in vec3 aColor;\n" +
        "out vec3 vColor;\n" +
        "void main() {\n" +
        "    gl_Position = vec4(aPosition, 1.0);\n" +
        "    vColor = aColor;\n" +
        "}\n";

    private static final String FRAGMENT_SHADER =
        "#version 330 core\n" +
        "in vec3 vColor;\n" +
        "out vec4 FragColor;\n" +
        "void main() {\n" +
        "    FragColor = vec4(vColor, 1.0);\n" +
        "}\n";

    // TeaVM-compatible callback interface
    @JSFunctor
    public interface FrameCallback extends JSObject {
        void onFrame();
    }

    @JSBody(params = {"callback"}, script =
        "requestAnimationFrame(function() { callback.onFrame(); });")
    private static native void scheduleFrame(FrameCallback callback);

    @JSBody(params = {"msg"}, script = "console.log('[MCWeb] ' + msg);")
    private static native void log(String msg);

    public static void main(String[] args) {
        log("MCWeb 1.21.11 - Translation Layer Test");
        log("Initializing...");

        GLFW.glfwInit();
        window = GLFW.glfwCreateWindow(854, 480, "MCWeb", 0, 0);

        HTMLCanvasElement canvas = GLFW.getCanvasElement();
        WebGL2 gl = WebGL2.create(canvas);

        if (gl == null) {
            log("ERROR: WebGL2 not supported!");
            return;
        }

        GL11.init(gl);
        log("WebGL2 context created");

        shaderProgram = createShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        log("Shader program created: " + shaderProgram);

        float[] vertices = {
             0.0f,  0.5f, 0.0f,  0.34f, 0.75f, 0.20f,
            -0.5f, -0.5f, 0.0f,  0.54f, 0.36f, 0.17f,
             0.5f, -0.5f, 0.0f,  0.47f, 0.68f, 0.98f,
        };

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

        int posLoc = GL20.glGetAttribLocation(shaderProgram, "aPosition");
        GL20.glVertexAttribPointer(posLoc, 3, GL11.GL_FLOAT, false, 24, 0);
        GL20.glEnableVertexAttribArray(posLoc);

        int colorLoc = GL20.glGetAttribLocation(shaderProgram, "aColor");
        GL20.glVertexAttribPointer(colorLoc, 3, GL11.GL_FLOAT, false, 24, 12);
        GL20.glEnableVertexAttribArray(colorLoc);

        GL30.glBindVertexArray(0);

        log("Geometry uploaded");
        log("Starting render loop...");

        doFrame();
    }

    private static int createShaderProgram(String vertSrc, String fragSrc) {
        int vert = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vert, vertSrc);
        GL20.glCompileShader(vert);

        int frag = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(frag, fragSrc);
        GL20.glCompileShader(frag);

        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vert);
        GL20.glAttachShader(program, frag);
        GL20.glLinkProgram(program);

        GL20.glDeleteShader(vert);
        GL20.glDeleteShader(frag);

        return program;
    }

    private static void doFrame() {
        GL11.glClearColor(0.06f, 0.06f, 0.12f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL20.glUseProgram(shaderProgram);
        GL30.glBindVertexArray(vao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        GL30.glBindVertexArray(0);

        GLFW.glfwPollEvents();

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            log("W pressed!");
        }

        GLFW.glfwSwapBuffers(window);

        // Schedule next frame using JSFunctor interface
        scheduleFrame(Main::doFrame);
    }
}

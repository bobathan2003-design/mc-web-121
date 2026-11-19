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
    private static float rotation = 0;
    private static float camRotX = 0.4f;
    private static float camRotY = 0.5f;
    private static float camDist = 4.0f;
    private static double lastTime = 0;

    private static int uProjection;
    private static int uView;
    private static int uModel;

    private static final String VERTEX_SHADER =
        "#version 330 core\n" +
        "in vec3 aPosition;\n" +
        "in vec3 aColor;\n" +
        "in vec3 aNormal;\n" +
        "out vec3 vColor;\n" +
        "out vec3 vNormal;\n" +
        "uniform mat4 uProjection;\n" +
        "uniform mat4 uView;\n" +
        "uniform mat4 uModel;\n" +
        "void main() {\n" +
        "    gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0);\n" +
        "    vColor = aColor;\n" +
        "    vNormal = mat3(uModel) * aNormal;\n" +
        "}\n";

    private static final String FRAGMENT_SHADER =
        "#version 330 core\n" +
        "in vec3 vColor;\n" +
        "in vec3 vNormal;\n" +
        "out vec4 FragColor;\n" +
        "void main() {\n" +
        "    vec3 lightDir = normalize(vec3(0.5, 1.0, 0.3));\n" +
        "    vec3 norm = normalize(vNormal);\n" +
        "    float diff = max(dot(norm, lightDir), 0.0);\n" +
        "    float ambient = 0.45;\n" +
        "    float light = ambient + diff * 0.55;\n" +
        "    FragColor = vec4(vColor * light, 1.0);\n" +
        "}\n";

    @JSFunctor
    public interface FrameCallback extends JSObject {
        void onFrame();
    }

    @JSBody(params = {"callback"}, script =
        "requestAnimationFrame(function() { callback.onFrame(); });")
    private static native void scheduleFrame(FrameCallback callback);

    @JSBody(params = {"msg"}, script = "console.log('[MCWeb] ' + msg);")
    private static native void log(String msg);

    @JSBody(params = {}, script = "return performance.now() / 1000.0;")
    private static native double getTime();

    public static void main(String[] args) {
        log("MCWeb 1.21.11 - 3D Block Test");

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

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        // Don't enable cull face yet - let all faces show
        // GL11.glEnable(GL11.GL_CULL_FACE);

        shaderProgram = createShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        log("Shader program created: " + shaderProgram);

        uProjection = GL20.glGetUniformLocation(shaderProgram, "uProjection");
        uView = GL20.glGetUniformLocation(shaderProgram, "uView");
        uModel = GL20.glGetUniformLocation(shaderProgram, "uModel");

        log("uProjection=" + uProjection + " uView=" + uView + " uModel=" + uModel);

        float[] blockVertices = buildGrassBlock();

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, blockVertices, GL15.GL_STATIC_DRAW);

        int stride = 36; // 9 floats * 4 bytes

        int posLoc = GL20.glGetAttribLocation(shaderProgram, "aPosition");
        log("aPosition location: " + posLoc);
        GL20.glVertexAttribPointer(posLoc, 3, GL11.GL_FLOAT, false, stride, 0);
        GL20.glEnableVertexAttribArray(posLoc);

        int colorLoc = GL20.glGetAttribLocation(shaderProgram, "aColor");
        log("aColor location: " + colorLoc);
        GL20.glVertexAttribPointer(colorLoc, 3, GL11.GL_FLOAT, false, stride, 12);
        GL20.glEnableVertexAttribArray(colorLoc);

        int normalLoc = GL20.glGetAttribLocation(shaderProgram, "aNormal");
        log("aNormal location: " + normalLoc);
        GL20.glVertexAttribPointer(normalLoc, 3, GL11.GL_FLOAT, false, stride, 24);
        GL20.glEnableVertexAttribArray(normalLoc);

        GL30.glBindVertexArray(0);

        log("Block uploaded. Starting render loop...");
        log("Click canvas to capture mouse. Drag to rotate. Scroll to zoom.");

        lastTime = getTime();
        doFrame();
    }

    private static float[] buildGrassBlock() {
        float s = 0.5f;

        // Colors
        float gr = 0.34f, gg = 0.75f, gb = 0.20f; // grass green
        float dr = 0.54f, dg = 0.36f, db = 0.17f; // dirt brown
        float br = 0.45f, bg = 0.30f, bb = 0.14f; // bottom dark

        // 6 faces * 2 triangles * 3 vertices * 9 floats = 324
        float[] v = {
            // === TOP FACE (Y+) - GRASS GREEN ===
            -s, s, -s,  gr, gg, gb,  0, 1, 0,
             s, s, -s,  gr, gg, gb,  0, 1, 0,
             s, s,  s,  gr, gg, gb,  0, 1, 0,
            -s, s, -s,  gr, gg, gb,  0, 1, 0,
             s, s,  s,  gr, gg, gb,  0, 1, 0,
            -s, s,  s,  gr, gg, gb,  0, 1, 0,

            // === BOTTOM FACE (Y-) - DARK DIRT ===
            -s, -s,  s,  br, bg, bb,  0, -1, 0,
             s, -s,  s,  br, bg, bb,  0, -1, 0,
             s, -s, -s,  br, bg, bb,  0, -1, 0,
            -s, -s,  s,  br, bg, bb,  0, -1, 0,
             s, -s, -s,  br, bg, bb,  0, -1, 0,
            -s, -s, -s,  br, bg, bb,  0, -1, 0,

            // === FRONT FACE (Z+) - DIRT ===
            -s, -s, s,  dr, dg, db,  0, 0, 1,
             s, -s, s,  dr, dg, db,  0, 0, 1,
             s,  s, s,  dr, dg, db,  0, 0, 1,
            -s, -s, s,  dr, dg, db,  0, 0, 1,
             s,  s, s,  dr, dg, db,  0, 0, 1,
            -s,  s, s,  dr, dg, db,  0, 0, 1,

            // === BACK FACE (Z-) - DIRT ===
             s, -s, -s,  dr, dg, db,  0, 0, -1,
            -s, -s, -s,  dr, dg, db,  0, 0, -1,
            -s,  s, -s,  dr, dg, db,  0, 0, -1,
             s, -s, -s,  dr, dg, db,  0, 0, -1,
            -s,  s, -s,  dr, dg, db,  0, 0, -1,
             s,  s, -s,  dr, dg, db,  0, 0, -1,

            // === RIGHT FACE (X+) - DIRT ===
            s, -s,  s,  dr, dg, db,  1, 0, 0,
            s, -s, -s,  dr, dg, db,  1, 0, 0,
            s,  s, -s,  dr, dg, db,  1, 0, 0,
            s, -s,  s,  dr, dg, db,  1, 0, 0,
            s,  s, -s,  dr, dg, db,  1, 0, 0,
            s,  s,  s,  dr, dg, db,  1, 0, 0,

            // === LEFT FACE (X-) - DIRT ===
            -s, -s, -s,  dr, dg, db,  -1, 0, 0,
            -s, -s,  s,  dr, dg, db,  -1, 0, 0,
            -s,  s,  s,  dr, dg, db,  -1, 0, 0,
            -s, -s, -s,  dr, dg, db,  -1, 0, 0,
            -s,  s,  s,  dr, dg, db,  -1, 0, 0,
            -s,  s, -s,  dr, dg, db,  -1, 0, 0,
        };

        return v;
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
        double currentTime = getTime();
        float deltaTime = (float)(currentTime - lastTime);
        lastTime = currentTime;

        // Auto-spin the block
        rotation += deltaTime * 0.8f;

        // Mouse camera control
        double dx = GLFW.getMouseDX();
        double dy = GLFW.getMouseDY();

        if (dx != 0 || dy != 0) {
            camRotY += (float)(dx * 0.01);
            camRotX += (float)(dy * 0.01);

            if (camRotX > 1.5f) camRotX = 1.5f;
            if (camRotX < -1.5f) camRotX = -1.5f;
        }

        // Scroll zoom
        double scroll = GLFW.getScrollDY();
        if (scroll != 0) {
            camDist -= (float)(scroll * 0.5);
            if (camDist < 1.5f) camDist = 1.5f;
            if (camDist > 10.0f) camDist = 10.0f;
        }

        // Clear with sky blue
        GL11.glClearColor(0.53f, 0.81f, 0.98f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL20.glUseProgram(shaderProgram);

        // Projection
        Matrix4f projection = new Matrix4f();
        projection.perspective(
            (float)(70.0 * Math.PI / 180.0),
            854.0f / 480.0f,
            0.1f, 100.0f
        );
        GL20.glUniformMatrix4fv(uProjection, false, projection.m);

        // View (camera)
        Matrix4f view = new Matrix4f();
        view.translate(0, 0, -camDist);
        view.rotateX(camRotX);
        view.rotateY(camRotY);
        GL20.glUniformMatrix4fv(uView, false, view.m);

        // Model (spinning block)
        Matrix4f model = new Matrix4f();
        model.rotateY(rotation);
        GL20.glUniformMatrix4fv(uModel, false, model.m);

        // Draw
        GL30.glBindVertexArray(vao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);
        GL30.glBindVertexArray(0);

        GLFW.glfwPollEvents();
        GLFW.glfwSwapBuffers(window);

        scheduleFrame(Main::doFrame);
    }
}

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
    private static float camRotY = 0;
    private static float camDist = 4.0f;

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
        "out vec3 vWorldPos;\n" +
        "uniform mat4 uProjection;\n" +
        "uniform mat4 uView;\n" +
        "uniform mat4 uModel;\n" +
        "void main() {\n" +
        "    vec4 worldPos = uModel * vec4(aPosition, 1.0);\n" +
        "    gl_Position = uProjection * uView * worldPos;\n" +
        "    vColor = aColor;\n" +
        "    vNormal = aNormal;\n" +
        "    vWorldPos = worldPos.xyz;\n" +
        "}\n";

    private static final String FRAGMENT_SHADER =
        "#version 330 core\n" +
        "in vec3 vColor;\n" +
        "in vec3 vNormal;\n" +
        "in vec3 vWorldPos;\n" +
        "out vec4 FragColor;\n" +
        "void main() {\n" +
        "    vec3 lightDir = normalize(vec3(0.5, 1.0, 0.3));\n" +
        "    float diff = max(dot(vNormal, lightDir), 0.0);\n" +
        "    float ambient = 0.4;\n" +
        "    float light = ambient + diff * 0.6;\n" +
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

    @JSBody(params = {}, script = "return performance.now();")
    private static native double now();

    public static void main(String[] args) {
        log("MCWeb 1.21.11 - 3D Block Test");
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

        // Enable depth testing and backface culling
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);

        shaderProgram = createShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        log("Shader program created: " + shaderProgram);

        // Get uniform locations
        uProjection = GL20.glGetUniformLocation(shaderProgram, "uProjection");
        uView = GL20.glGetUniformLocation(shaderProgram, "uView");
        uModel = GL20.glGetUniformLocation(shaderProgram, "uModel");

        // Build a Minecraft-style block
        // Each face has position + color + normal
        // 9 floats per vertex, 6 vertices per face, 6 faces
        float[] blockVertices = buildBlock();

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, blockVertices, GL15.GL_STATIC_DRAW);

        // Position: 3 floats
        int posLoc = GL20.glGetAttribLocation(shaderProgram, "aPosition");
        GL20.glVertexAttribPointer(posLoc, 3, GL11.GL_FLOAT, false, 36, 0);
        GL20.glEnableVertexAttribArray(posLoc);

        // Color: 3 floats
        int colorLoc = GL20.glGetAttribLocation(shaderProgram, "aColor");
        GL20.glVertexAttribPointer(colorLoc, 3, GL11.GL_FLOAT, false, 36, 12);
        GL20.glEnableVertexAttribArray(colorLoc);

        // Normal: 3 floats
        int normalLoc = GL20.glGetAttribLocation(shaderProgram, "aNormal");
        GL20.glVertexAttribPointer(normalLoc, 3, GL11.GL_FLOAT, false, 36, 24);
        GL20.glEnableVertexAttribArray(normalLoc);

        GL30.glBindVertexArray(0);

        log("Block geometry uploaded (36 vertices)");
        log("Controls: Click to capture mouse, move mouse to rotate camera");
        log("Starting render loop...");

        doFrame();
    }

    private static float[] buildBlock() {
        // Grass block colors
        float[] grassTop = {0.34f, 0.75f, 0.20f};    // green top
        float[] dirtSide = {0.54f, 0.36f, 0.17f};     // brown sides
        float[] dirtSideGreen = {0.45f, 0.55f, 0.18f}; // greenish-brown side top
        float[] dirtBottom = {0.45f, 0.30f, 0.14f};    // darker brown bottom

        float s = 0.5f; // half-size

        // 6 faces * 6 vertices * 9 floats = 324 floats
        float[] v = new float[324];
        int i = 0;

        // Top face (grass green) - Y+
        i = addFace(v, i,
            -s, s, -s,   s, s, -s,   s, s, s,
            -s, s, -s,   s, s, s,   -s, s, s,
            grassTop, 0, 1, 0);

        // Bottom face (dark dirt) - Y-
        i = addFace(v, i,
            -s, -s, s,   s, -s, s,   s, -s, -s,
            -s, -s, s,   s, -s, -s,  -s, -s, -s,
            dirtBottom, 0, -1, 0);

        // Front face (dirt) - Z+
        i = addFace(v, i,
            -s, -s, s,   -s, s, s,   s, s, s,
            -s, -s, s,   s, s, s,    s, -s, s,
            dirtSide, 0, 0, 1);

        // Back face (dirt) - Z-
        i = addFace(v, i,
            s, -s, -s,   s, s, -s,   -s, s, -s,
            s, -s, -s,   -s, s, -s,  -s, -s, -s,
            dirtSide, 0, 0, -1);

        // Right face (dirt) - X+
        i = addFace(v, i,
            s, -s, s,    s, s, s,    s, s, -s,
            s, -s, s,    s, s, -s,   s, -s, -s,
            dirtSide, 1, 0, 0);

        // Left face (dirt) - X-
        i = addFace(v, i,
            -s, -s, -s,  -s, s, -s,  -s, s, s,
            -s, -s, -s,  -s, s, s,   -s, -s, s,
            dirtSide, -1, 0, 0);

        return v;
    }

    private static int addFace(float[] v, int i,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            float x5, float y5, float z5,
            float x6, float y6, float z6,
            float[] color, float nx, float ny, float nz) {

        float[][] positions = {
            {x1,y1,z1}, {x2,y2,z2}, {x3,y3,z3},
            {x4,y4,z4}, {x5,y5,z5}, {x6,y6,z6}
        };

        for (int j = 0; j < 6; j++) {
            v[i++] = positions[j][0]; // pos x
            v[i++] = positions[j][1]; // pos y
            v[i++] = positions[j][2]; // pos z
            v[i++] = color[0];        // color r
            v[i++] = color[1];        // color g
            v[i++] = color[2];        // color b
            v[i++] = nx;              // normal x
            v[i++] = ny;              // normal y
            v[i++] = nz;              // normal z
        }
        return i;
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
        // Handle mouse input for camera
        double dx = GLFW.getMouseDX();
        double dy = GLFW.getMouseDY();
        camRotY += (float)(dx * 0.005);
        camRotX += (float)(dy * 0.005);

        // Clamp vertical rotation
        if (camRotX > 1.5f) camRotX = 1.5f;
        if (camRotX < -1.5f) camRotX = -1.5f;

        // Scroll to zoom
        double scroll = GLFW.getScrollDY();
        camDist -= (float)(scroll * 0.5);
        if (camDist < 2.0f) camDist = 2.0f;
        if (camDist > 10.0f) camDist = 10.0f;

        // Auto rotate slowly
        rotation += 0.01f;

        // Clear
        GL11.glClearColor(0.53f, 0.81f, 0.98f, 1.0f); // Minecraft sky blue!
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL20.glUseProgram(shaderProgram);

        // Projection matrix
        Matrix4f projection = new Matrix4f();
        projection.perspective(
            (float)(70.0 * Math.PI / 180.0),
            854.0f / 480.0f,
            0.1f,
            100.0f
        );
        GL20.glUniformMatrix4fv(uProjection, false, projection.m);

        // View matrix (camera)
        Matrix4f view = new Matrix4f();
        view.translate(0, 0, -camDist);
        view.rotateX(camRotX);
        view.rotateY(camRotY);
        GL20.glUniformMatrix4fv(uView, false, view.m);

        // Model matrix (block rotation)
        Matrix4f model = new Matrix4f();
        model.rotateY(rotation);
        GL20.glUniformMatrix4fv(uModel, false, model.m);

        // Draw the block
        GL30.glBindVertexArray(vao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);
        GL30.glBindVertexArray(0);

        GLFW.glfwPollEvents();
        GLFW.glfwSwapBuffers(window);

        // Next frame
        scheduleFrame(Main::doFrame);
    }
}

// src/main/java/org/lwjgl/glfw/GLFW.java
package org.lwjgl.glfw;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.events.KeyboardEvent;
import org.teavm.jso.dom.events.MouseEvent;

public class GLFW {

    public static final int GLFW_KEY_W = 87;
    public static final int GLFW_KEY_A = 65;
    public static final int GLFW_KEY_S = 83;
    public static final int GLFW_KEY_D = 68;
    public static final int GLFW_KEY_SPACE = 32;
    public static final int GLFW_KEY_LEFT_SHIFT = 340;
    public static final int GLFW_KEY_ESCAPE = 256;
    public static final int GLFW_KEY_E = 69;
    public static final int GLFW_KEY_Q = 81;
    public static final int GLFW_KEY_F = 70;

    public static final int GLFW_PRESS = 1;
    public static final int GLFW_RELEASE = 0;
    public static final int GLFW_REPEAT = 2;

    public static final int GLFW_MOUSE_BUTTON_LEFT = 0;
    public static final int GLFW_MOUSE_BUTTON_RIGHT = 2;
    public static final int GLFW_MOUSE_BUTTON_MIDDLE = 1;

    private static boolean[] keys = new boolean[512];
    private static boolean[] mouseButtons = new boolean[8];
    private static double mouseX = 0, mouseY = 0;
    private static double scrollX = 0, scrollY = 0;
    private static int windowWidth = 854, windowHeight = 480;
    private static HTMLCanvasElement canvas;
    private static boolean pointerLocked = false;
    private static double mouseDX = 0, mouseDY = 0;

    public static boolean glfwInit() {
        return true;
    }

    @JSBody(params = {}, script = "return document.getElementById('game');")
    private static native HTMLCanvasElement getCanvas();

    @JSBody(params = {"canvas"}, script = """
        canvas.addEventListener('keydown', function(e) {
            e.preventDefault();
        });
        canvas.addEventListener('contextmenu', function(e) {
            e.preventDefault();
        });
        canvas.tabIndex = 1;
        canvas.focus();
    """)
    private static native void setupCanvas(HTMLCanvasElement canvas);

    public static long glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share) {
        canvas = getCanvas();
        windowWidth = width;
        windowHeight = height;
        canvas.setWidth(width);
        canvas.setHeight(height);
        setupCanvas(canvas);
        setupInputListeners();
        return 1L; // fake window handle
    }

    private static void setupInputListeners() {
        setupKeyListeners(canvas);
        setupMouseListeners(canvas);
    }

    @JSBody(params = {"canvas"}, script = """
        canvas.addEventListener('keydown', function(e) {
            var code = e.keyCode || e.which;
            if (typeof $rt_globals !== 'undefined') {
                // TeaVM callback
            }
            window._mcweb_keys = window._mcweb_keys || {};
            window._mcweb_keys[code] = true;
        });
        canvas.addEventListener('keyup', function(e) {
            var code = e.keyCode || e.which;
            window._mcweb_keys = window._mcweb_keys || {};
            window._mcweb_keys[code] = false;
        });
    """)
    private static native void setupKeyListeners(HTMLCanvasElement canvas);

    @JSBody(params = {"canvas"}, script = """
        window._mcweb_mouseX = 0;
        window._mcweb_mouseY = 0;
        window._mcweb_mouseDX = 0;
        window._mcweb_mouseDY = 0;
        window._mcweb_mouseButtons = {};
        window._mcweb_scrollY = 0;
        
        canvas.addEventListener('mousemove', function(e) {
            window._mcweb_mouseX = e.clientX;
            window._mcweb_mouseY = e.clientY;
            window._mcweb_mouseDX += e.movementX || 0;
            window._mcweb_mouseDY += e.movementY || 0;
        });
        canvas.addEventListener('mousedown', function(e) {
            window._mcweb_mouseButtons[e.button] = true;
            if (!document.pointerLockElement) {
                canvas.requestPointerLock();
            }
        });
        canvas.addEventListener('mouseup', function(e) {
            window._mcweb_mouseButtons[e.button] = false;
        });
        canvas.addEventListener('wheel', function(e) {
            window._mcweb_scrollY += e.deltaY > 0 ? -1 : 1;
        });
        document.addEventListener('pointerlockchange', function() {
            window._mcweb_pointerLocked = !!document.pointerLockElement;
        });
    """)
    private static native void setupMouseListeners(HTMLCanvasElement canvas);

    @JSBody(params = {"keyCode"}, script = """
        return !!(window._mcweb_keys && window._mcweb_keys[keyCode]);
    """)
    private static native boolean isKeyDown(int keyCode);

    @JSBody(params = {"button"}, script = """
        return !!(window._mcweb_mouseButtons && window._mcweb_mouseButtons[button]);
    """)
    private static native boolean isMouseDown(int button);

    @JSBody(params = {}, script = "return window._mcweb_mouseX || 0;")
    private static native double getMouseX();

    @JSBody(params = {}, script = "return window._mcweb_mouseY || 0;")
    private static native double getMouseY();

    @JSBody(params = {}, script = """
        var dx = window._mcweb_mouseDX || 0;
        window._mcweb_mouseDX = 0;
        return dx;
    """)
    private static native double getAndResetMouseDX();

    @JSBody(params = {}, script = """
        var dy = window._mcweb_mouseDY || 0;
        window._mcweb_mouseDY = 0;
        return dy;
    """)
    private static native double getAndResetMouseDY();

    @JSBody(params = {}, script = """
        var s = window._mcweb_scrollY || 0;
        window._mcweb_scrollY = 0;
        return s;
    """)
    private static native double getAndResetScroll();

    public static int glfwGetKey(long window, int key) {
        return isKeyDown(key) ? GLFW_PRESS : GLFW_RELEASE;
    }

    public static int glfwGetMouseButton(long window, int button) {
        return isMouseDown(button) ? GLFW_PRESS : GLFW_RELEASE;
    }

    public static double[] glfwGetCursorPos(long window) {
        return new double[]{getMouseX(), getMouseY()};
    }

    public static double getMouseDX() {
        return getAndResetMouseDX();
    }

    public static double getMouseDY() {
        return getAndResetMouseDY();
    }

    public static double getScrollDY() {
        return getAndResetScroll();
    }

    public static void glfwSwapBuffers(long window) {
        // WebGL auto-presents, nothing needed
    }

    public static void glfwPollEvents() {
        // Events are handled by browser listeners
    }

    public static boolean glfwWindowShouldClose(long window) {
        return false; // browser tab handles closing
    }

    public static void glfwTerminate() {
        // cleanup
    }

    public static void glfwMakeContextCurrent(long window) {
        // already done during init
    }

    public static HTMLCanvasElement getCanvasElement() {
        return canvas;
    }

    public static int getWidth() { return windowWidth; }
    public static int getHeight() { return windowHeight; }
}

package UI

import org.jetbrains.skija.*
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil
import java.nio.DoubleBuffer
import kotlin.concurrent.thread


class UIWindowLWJGLImpl(width: Int,height: Int): UIWindow {

    private var windowHandler:Long = 0

    override var width: Int = width
        set(value) {

            if(windowHandler != 0L)
                GLFW.glfwSetWindowSize(windowHandler,value,height)

            field = value
        }

    override var height: Int = height
        set(value) {

            if(windowHandler != 0L)
                GLFW.glfwSetWindowSize(windowHandler,width,value)

            field = value
        }

    override var x: Int = 0
        set(value) {
            GLFW.glfwSetWindowPos(
                windowHandler,
                value,
                y
            )

            field = value
        }

    override var y: Int = 0
        set(value) {
            GLFW.glfwSetWindowPos(
                windowHandler,
                x,
                value
            )

            field = value
        }


    override val screenWidth: Int
        get(){
            val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
            return vidmode!!.width()
        }

    override val screenHeight: Int
        get(){
            val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
            return vidmode!!.height()
        }


    override var visible: Boolean = true
        set(value) {
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, if(value) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE)
            field = value
        }


    private lateinit var canvas:UICanvas
    override var pressCallback: (point: UIPoint) -> Unit={}
    override var drawContentCallback: (UICanvas) -> Unit = {}

    init {
        thread {
            init()
            loop()

        }
    }


    private fun loop()
    {
        GL.createCapabilities()

        val context = DirectContext.makeGL()
        val fbId = GL11.glGetInteger(0x8CA6) // GL_FRAMEBUFFER_BINDING

        val renderTarget = BackendRenderTarget.makeGL(
            width,
            height,  /*samples*/
            0,  /*stencil*/
            8,
            fbId,
            FramebufferFormat.GR_GL_RGBA8
        )

        val surface: Surface = Surface.makeFromBackendRenderTarget(
            context,
            renderTarget,
            SurfaceOrigin.BOTTOM_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.getSRGB()
        )

        canvas = UICanvasSKIAImpl( surface.canvas)

/*

        val paint = Paint()
        paint.color = 0x7f00ff00

        val face = FontMgr.getDefault().matchFamilyStyle("Menlo", FontStyle.NORMAL)
        val font = Font(face, 30f)

*/

        GL11.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

        while (!GLFW.glfwWindowShouldClose(windowHandler)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

            drawContentCallback(canvas)

            context.flush()
            GLFW.glfwSwapBuffers(windowHandler)
            GLFW.glfwPollEvents()
        }
    }

    private fun init(): Long {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }

        // Configure GLFW
        GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE) // the window will be resizable

        // Create the window
        windowHandler = GLFW.glfwCreateWindow(width, height, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL)
        if (windowHandler == MemoryUtil.NULL) throw RuntimeException("Failed to create the GLFW window")

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.glfwSetKeyCallback(
            windowHandler
        ) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) GLFW.glfwSetWindowShouldClose(
                window,
                true
            ) // We will detect this in the rendering loop
        }


        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(windowHandler)
        // Enable v-sync
        GLFW.glfwSwapInterval(1)

        // Make the window visible
        GLFW.glfwShowWindow(windowHandler)


        GLFW.glfwSetMouseButtonCallback(windowHandler) { window, button, action, mods ->
            val da1 = DoubleArray(1)
            val da2 = DoubleArray(1)
            GLFW.glfwGetCursorPos(window, da1, da2)
            val x = da1[0].toInt()
            val y = da2[0].toInt()
            pressCallback(UIPoint(x,y))
        }


        return windowHandler
    }


    override fun cleanUp() {

        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(windowHandler)
        GLFW.glfwDestroyWindow(windowHandler)

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)!!.free()
    }

    companion object
    {
        fun createNewWindow(width:Int,height:Int):UIWindow
        {

            val window = UIWindowLWJGLImpl(width,height)
            return window
        }



    }
}
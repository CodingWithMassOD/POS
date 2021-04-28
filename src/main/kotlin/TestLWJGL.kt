import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import kotlin.math.cos
import kotlin.math.sin

class HelloWorld {
    // The window handle
    private var window: Long = 0
    private var windowWidth:Int = 300
    private var windowHeight:Int = 300

    fun run() {
        println("Hello LWJGL " + Version.getVersion() + "!")
        init()
        loop()

        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(window)
        GLFW.glfwDestroyWindow(window)

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)!!.free()
    }

    private fun init() {
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
        window = GLFW.glfwCreateWindow(windowWidth, windowHeight, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL)
        if (window == MemoryUtil.NULL) throw RuntimeException("Failed to create the GLFW window")

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.glfwSetKeyCallback(
            window
        ) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) GLFW.glfwSetWindowShouldClose(
                window,
                true
            ) // We will detect this in the rendering loop
        }
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())

            // Center the window
            GLFW.glfwSetWindowPos(
                window,
                (vidmode!!.width() - pWidth[0]) / 2,
                (vidmode.height() - pHeight[0]) / 2
            )
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window)
        // Enable v-sync
        GLFW.glfwSwapInterval(1)

        // Make the window visible
        GLFW.glfwShowWindow(window)
    }

    private fun loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Set the clear color
        GL11.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)



        while (!GLFW.glfwWindowShouldClose(window)) {
            glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

            glColor3d(.0,1.0,.0)
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
            glLineWidth(5f)

            drawContent()
            GLFW.glfwSwapBuffers(window)
            GLFW.glfwPollEvents()
        }
    }

    fun drawContent()
    {
        drawRectangle(Point(10,10),Point(100,100))
        drawCircle(Point(150,150),50)
    }

    fun drawCircle(center:Point,radius:Int)
    {
        val pointsList = (0..3600).map {

            val x = center.x + radius * cos(Math.toRadians(it/10.0))
            val y = center.y + radius * sin(Math.toRadians(it/10.0))

            Point(x.toInt(),y.toInt())
        }

        drawPolygon(pointsList)
    }

    fun drawRectangle(lb:Point,tr:Point)
    {
        drawPolygon(
            lb,
            Point(lb.x,tr.y),
            tr,
            Point(tr.x,lb.y),
            lb
        )
    }

    fun drawPolygon(vararg points:Point)
    {
        glBegin(GL_POLYGON)

        points.forEach {
            glVertex2f(convertPointXToOpenGLX(it.x) , convertPointYToOpenGLY(it.y))
        }

        glEnd()
    }

    fun drawPolygon( points:List<Point>)
    {
        glBegin(GL_POLYGON)

        points.forEach {
            glVertex2f(convertPointXToOpenGLX(it.x) , convertPointYToOpenGLY(it.y))
        }

        glEnd()
    }


    fun drawLine(vararg points:Point)
    {
        glBegin(GL_LINE_STRIP)

        points.forEach {
            glVertex2f(convertPointXToOpenGLX(it.x) , convertPointYToOpenGLY(it.y))
        }

        glEnd()
    }

    fun drawLine(p1:Point,p2:Point)
    {
        glBegin(GL_LINE_STRIP)

        glVertex2f(convertPointXToOpenGLX(p1.x) , convertPointYToOpenGLY(p1.y))
        glVertex2f(convertPointXToOpenGLX(p2.x) , convertPointYToOpenGLY(p2.y))

        glEnd()
    }

    fun convertPointXToOpenGLX(x:Int) = 2f*(x.toFloat()/windowWidth) - 1
    fun convertPointYToOpenGLY(y:Int) = 2f*(y.toFloat()/windowHeight) - 1
}


class Point(val x:Int,val y:Int)



fun main(args: Array<String>) {
    HelloWorld().run()
}
import UI.UIPaint
import UI.UIPoint
import UI.UIWindowLWJGLImpl


fun main(args: Array<String>) {


    val window = UIWindowLWJGLImpl.createNewWindow(500,500)
    val paint = UIPaint()
    paint.color = 0xff234156.toInt()

    val points = mutableListOf<UIPoint>()

    window.pressCallback = {
        points.add(it)
    }

    window.drawContentCallback = {
        it.drawLines(points,paint)
    }

}
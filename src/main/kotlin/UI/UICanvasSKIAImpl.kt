package UI

import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Paint

class UICanvasSKIAImpl(private val canvas:  Canvas) :UICanvas {

    private var p =Paint()

    override fun drawLines(points: List<UIPoint>,paint:UIPaint) {

        p.color = paint.color

        (0 until points.lastIndex).forEach {

            val p1 = points[it]
            val p2 = points[it+1]
            canvas.drawLine(
                p1.x.toFloat(),
                p1.y.toFloat(),
                p2.x.toFloat(),
                p2.y.toFloat(),p)
        }


    }

    override fun drawText(text: String, anchor: UIPoint,paint:UIPaint) {
        TODO("Not yet implemented")
    }
}
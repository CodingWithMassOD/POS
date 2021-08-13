package UI

interface UICanvas {

    fun drawLines(points:List<UIPoint>,paint:UIPaint)
    fun drawText(text:String , anchor:UIPoint,paint:UIPaint)

}
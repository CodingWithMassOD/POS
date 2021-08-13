package UI

interface UIWindow {

    val screenWidth:Int
    val screenHeight:Int
    var width:Int
    var height:Int
    var x:Int
    var y:Int
    var visible:Boolean

    var drawContentCallback:(UICanvas)->Unit
    var pressCallback:(point:UIPoint)->Unit

    fun cleanUp()
}
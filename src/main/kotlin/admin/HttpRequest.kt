package admin

class HttpRequest(val host:String,val port:Int,var  method:String,val requestURI:String,
                  val headers:Map<String,String>, val body:String)

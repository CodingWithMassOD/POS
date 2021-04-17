package admin



interface NewConnectionCallback
{
    fun handleRequest(request: HttpRequest):HttpResponse
}
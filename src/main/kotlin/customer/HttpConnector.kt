package customer

import admin.HttpRequest
import admin.HttpResponse
import java.io.InputStream
import java.net.Socket

class HttpConnector
{
    fun send(request:HttpRequest):HttpResponse
    {
        val socket = Socket(request.host,request.port)
        val inStream = socket.getInputStream()
        val outStream = socket.getOutputStream()

        val requestUri = if(request.requestURI.startsWith("/"))
            request.requestURI
        else
            "/${request.requestURI}"

        var requestString  = ""
        val requestLine = "${request.method} ${requestUri} HTTP/1.1\r\n"

        requestString += requestLine

        request.headers.forEach {
            requestString += "${it.key}:${it.value}\r\n"
        }


        if(request.body.isNotEmpty())
            requestString += "Content-length:${request.body.length}\r\n"


        requestString +="\r\n"

        requestString += request.body


        outStream.write(requestString.toByteArray(Charsets.UTF_8))

        return receiveResponse(request,inStream)
    }

    fun receiveResponse(request: HttpRequest, inStream:InputStream):HttpResponse
    {
        var body = ""
        var messageSoFar = ""


        while (true)
        {
            val byte = inStream.read()
            messageSoFar += byte.toChar()

            if(messageSoFar.endsWith("\r\n\r\n"))
                break
        }

        val statusCode = messageSoFar
            .substringBefore("\r\n")
            .substringAfter(" ")
            .substringBefore(" ")

        val headersString:List<String> = messageSoFar
            .substringAfter("\r\n")
            .substringBeforeLast("\r\n")
            .split("\r\n")

        val headersMap = headersString.map {
            Pair(it.substringBefore(":"), it.substringAfter(":"))
        }.toMap()

        val contentLength = headersMap.entries.firstOrNull { it.key.toLowerCase() == "content-length" }?.value

        if(contentLength!=null)
            body = inStream.readNBytes(contentLength.trim().toInt()).toString(Charsets.UTF_8)



        return HttpResponse( statusCode.toInt(), headersMap,body )
    }


}
package admin

import java.net.ServerSocket

class HttpServer(val port: Int)
{

    val server = ServerSocket(port)


    fun start()
    {

    }

    fun notifyMeOnNewRequest(callback: NewConnectionCallback)
    {
        while (true)
        {
            val clientSocket = server.accept()
            val inStream = clientSocket.getInputStream()
            val outStream = clientSocket.getOutputStream()

            var requestAndHeaders = ""

            while(true)
            {
                val byte = inStream.read()
                requestAndHeaders += byte.toChar()

                if(requestAndHeaders.endsWith("\r\n\r\n"))
                    break
            }


            val requestLine:String = requestAndHeaders.substringBefore("\r\n")
            val headersString:List<String> = requestAndHeaders.substringAfter("\r\n").split("\r\n")

            val method = requestLine.substringBefore(" ")
            val requestURI = requestLine.substringAfter(" ").substringBefore(" ")

            val headers:Map<String,String> =headersString.map {
                val key = it.substringBefore(":")
                val value = it.substringAfter(":")
                Pair(key,value)
            }.toMap()

            val contentLength = headers.entries.firstOrNull { it.key.toLowerCase() == "content-length" }?.value

            val body = if(contentLength!=null)
            {
                inStream.readNBytes(contentLength.trim().toInt()).toString(Charsets.UTF_8)
            }else
                ""

            val request = HttpRequest("",-1,method,requestURI, headers,body)

            val response = callback.handleRequest(request)



            val responseStatusLine = "HTTP/1.1 ${response.status} -"
            var responseHeaders = response.headers.toList().joinToString(separator = "\r\n") { "${it.first}:${it.second}" }

            responseHeaders +="content-length:${response.body.length}\r\n"

            val responseString = "$responseStatusLine\r\n$responseHeaders\r\n${response.body}"


            println(responseString)

            outStream.write(responseString.toByteArray())

            clientSocket.close()
        }
    }


}
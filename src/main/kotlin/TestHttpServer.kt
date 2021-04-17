import admin.HttpRequest
import customer.HttpConnector


fun main()
{
    val httpConnector = HttpConnector()


    val request = HttpRequest("cleancoder.com",80,"GET","/products", mapOf(
        "UserAgent" to "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36",
        "Host" to "cleancoder.com"
    ),"")

    val response = httpConnector.send(request)
    println(response.body)
    println(response.status)
    println(response.headers)
}

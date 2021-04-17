import java.net.HttpURLConnection
import java.net.URL

fun main()
{
    val url = URL("http://localhost:12345/GetProductList")
    val httpConnection = url.openConnection() as HttpURLConnection

    httpConnection.requestMethod = "POST"
    httpConnection.setRequestProperty("A","B")
    httpConnection.setRequestProperty("name","MassOD")
    httpConnection.connect()
    println("response code is ${httpConnection.responseCode}")
    println("response message is ${httpConnection.responseMessage}")

    val inputStream = httpConnection.inputStream

    while(true)
    {
        val nextByte = inputStream.read()
        print(nextByte.toChar())

        if(nextByte==-1)
            break
    }


}
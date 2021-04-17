import admin.HttpRequest
import customer.HttpConnector
import org.json.JSONArray
import org.json.JSONObject

val connector = HttpConnector()

fun main()
{

    while (true)
    {
        println("""
            Customer Main Menu
            1:Ask for product list
            2:Place Order
            
        """.trimIndent())

        val response = readLine()

        if(response == "1")
            askForProductList()
        else if(response=="2")
            placeOrder()
    }


}

fun placeOrder()
{

    var order = JSONObject()
    var items = JSONArray()

    while (true)
    {
        println("enter product name")
        val productName = readLine()
        println("enter amount")
        val amount = readLine()

        val item = JSONObject()
        item.put("product",productName)
        item.put("amount",amount)
        items.put(item)

        println("do you want to add more to order?")
        val answer = readLine()

        if(answer!= "yes")
            break
    }

    order.put("items",items)
    order.put("CustomerName","MassOD")

    val serializedBody = order.toString()

    val request = HttpRequest("127.0.0.1",12345,"POST","PlaceOrder", emptyMap(),serializedBody)
    val response = connector.send(request)
    println("response was ${response.status}  ${response.body}")
}

fun askForProductList()
{
    val request = HttpRequest("127.0.0.1",12345,"GET","GetProductList", emptyMap(),"get me all the products please!")
    val response = connector.send(request)
    println("response was ${response.status}  ${response.body}")
}
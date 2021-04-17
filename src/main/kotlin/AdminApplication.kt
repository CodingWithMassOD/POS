
import admin.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.concurrent.thread
import kotlin.system.exitProcess


val shoppingCarts = mutableListOf<ShoppingCart>()
val orders = mutableListOf<Order>()


var shopName = ""

var customerNames = mutableListOf<String>()

val productRepository = ProductRepository()


class ShoppingCart
{
    var customerName:String = ""
    val items = mutableListOf<ShoppingCartItem>()

    fun getMeTotalPrice():Int
    {
        var totalPrice = 0

        for(item in items)
        {
            totalPrice += item.getMeTotalPrice()
        }

        return totalPrice
    }
}


class ShoppingCartItem
{
    var productName:String = ""
    var amount :Int = 0

    fun getMeTotalPrice():Int
    {
        return amount * getPriceOfProduct(productName)
    }
}



class Product
{
    var name:String = ""
    var price:Int = 0
}

class Order(val customerName:String,val items:List<OrderItem>)
class OrderItem(val productName: String,val amount:Int)


fun askUntilYesOrNo(message : String):String
{
    println(" $message yes / no")

    var answer = readLine()

    while(answer != "yes" && answer!="no")
    {
        println("$message yes / no")
        println("please only enter yes or no : ")
        answer = readLine()
    }

    return answer

}


fun createSingleProduct()
{
    val product : Product = Product()

    val productName = askForStringFromUser("enter product name?")
    product.name = productName

    val productPrice = askForNumberFromUser("enter product price?")
    product.price = productPrice

    productRepository.save(product)
}


fun askForStringFromUser(message: String):String
{
    println(message)
    return readLine()!!
}

fun askForNumberFromUser(message: String):Int
{
    val string:String = askForStringFromUser(message)
    return string.toInt()
}

fun creatingItemsOfShoppingCart():ShoppingCart
{
    val shoppingCart = ShoppingCart()

    do
    {
        val cartItem =  ShoppingCartItem()
        cartItem.productName =  askForStringFromUser("Please enter item product name :")
        cartItem.amount = askForNumberFromUser("Please enter the amount of item :")

        shoppingCart.items.add(cartItem)

        val answer = askUntilYesOrNo("Is there any more items : ")

    }while (answer != "no")

    return shoppingCart
}

fun printShoppingCart(shoppingCart: ShoppingCart)
{
    println()
    println()
    println("Printing the receipt")


    for (item in shoppingCart.items)
        println("Item > ${item.productName} ${item.amount}$")

    println("Total price = ${shoppingCart.getMeTotalPrice()}")
}

fun createShoppingCart()
{
    val customerOfCart = askForStringFromUser("enter customer name for this new cart")

    val shoppingCart = creatingItemsOfShoppingCart()
    shoppingCart.customerName = customerOfCart

    shoppingCarts.add(shoppingCart)
}


fun getPriceOfProduct(productName:String):Int
{
    val product = productRepository.fetchByProductName(productName)
    return product.price
}

fun changeShopName()
{
    shopName = askForStringFromUser("please enter your shop name?")
}


fun createNewCustomer()
{
    val customerName = askForStringFromUser("enter customer name")
    customerNames.add(customerName)
}

fun showMainMenu()
{
    val menu = createMainMenu()

    val answer = askForStringFromUser(menu)

    when (answer)
    {
        "0" -> changeShopName()
        "1" -> createSingleProduct()
        "2" -> createNewCustomer()
        "3" -> createShoppingCart()
        "4" -> modifyProduct()
        "5" -> modifyCustomer()
        "6" -> printProductList()
        "7" -> printingTheCustomerList()
        "8" -> printCustomerPurchasesList()

        "exit" -> exitProcess(0)

        else -> println("enter correct value, number 0 to 8 or exit")
    }
}

private fun createMainMenu(): String
{
    val menu = """
Main Menu.
        
0-Change Shop Name
1-Create New product
2-Create New Customer
3-Creating Shopping Cart
4-Modify Product
5-Modify Customer
6-Print Product List
7-Print Customer List
8-Print Customer Purchases List
9-Print Pending Orders(${orders.size})


Enter number(0-8) or exit:
    """
    return menu
}

fun printCustomerPurchasesList()
{
    println("----------------------------------")

    val customerName = askForStringFromUser("Who is the customer ? ")

    for(cart in shoppingCarts)
    {
        if(cart.customerName == customerName)
            printShoppingCart(cart)
    }


    println("----------------------------------")
}

fun modifyCustomer()
{

}

fun modifyProduct()
{
    val product = askForStringFromUser("enter product name you want to modify")

    val newPrice = askForNumberFromUser("enter new price for $product")


}

fun printProductList()
{
    println("---------------------------")

    val productList = productRepository.fetchAll()

    if(productList.size<1)
    {
        println("There is no product")
        return
    }

    println("printing products list:")

    for(p in productList)
        println("Product > ${p.name} , ${p.price}")

    println("---------------------------")

}

fun printingTheCustomerList()
{
     if (customerNames.size<1)
     {
         println("There is no customer ...")
         return
     }


     println("List of Customers:")
     for(cn in customerNames)
     {
         println("customer : $cn")
     }
     println("---------------------------")
 }

fun newOrderHasBeenReceived(order: Order)
{
    orders.add(order)
    println("New order has been received from ${order.customerName}")
}

fun main()
{
    thread {
        while (true)
        {
            showMainMenu()
        }
    }



    val server = HttpServer(12345)
    server.start()

    server.notifyMeOnNewRequest(object : NewConnectionCallback {
        override fun handleRequest(request: HttpRequest):HttpResponse
        {
            println(">>>>>>>"+request.method)
            println(">>>>>>>"+request.body)
            println(request.requestURI)

            when(request.requestURI)
            {
                "/GetProductList"-> {
                    val productList = productRepository.fetchAll()
                    var response = ""

                    productList.forEach { product ->
                        response += "${product.name} = ${product.price}\n"
                    }

                    return HttpResponse(200, emptyMap(),response)
                }

                "/PlaceOrder"->{
                    val order = JSONObject(request.body)
                    val customerName = order.getString("CustomerName")
                    val items:JSONArray = order.getJSONArray("items")

                    val orderItems:List<OrderItem> = items.map {
                        val jsonItem = it as JSONObject
                        val productName = jsonItem.getString("product")
                        val amount = jsonItem.getString("amount").toInt()

                        OrderItem(productName, amount)
                    }

                    newOrderHasBeenReceived(Order(customerName,orderItems))


                    return HttpResponse(201, emptyMap(),"order was placed")
                }

                else-> return HttpResponse(404, emptyMap(),"")
            }


        }
    })



}

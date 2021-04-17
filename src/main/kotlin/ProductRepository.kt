import java.io.File


class ProductRepository
{
    fun save(product: Product)
    {
        val allProducts = restoreDataFromPersistence()
        allProducts.add(product)
        storeToPersistence(allProducts)
    }

    fun fetchByProductName(productName: String):Product
    {
        TODO("Not yet implemented")
    }

    fun fetchAll(): List<Product>
    {
        return restoreDataFromPersistence()
    }


    fun storeToPersistence(productList:List<Product>)
    {
        val storeFile = File("shop.txt")
        var data = ""


        for(product in productList)
        {
            data += "${product.name}|${product.price}"
            data += "+"
        }

        storeFile.writeText(data)
    }

    fun restoreDataFromPersistence():MutableList<Product>
    {
        val storeFile = File("shop.txt")

        if(!storeFile.exists())
            return mutableListOf()

        val productList = mutableListOf<Product>()
        val contentOfFile = storeFile.readText()

        var productName = ""
        var productPrice = ""
        var readingName = true

        for(ch in contentOfFile)
        {
            if(ch == '|')
            {
                readingName = false

            }else if( ch =='+')
            {
                readingName = true
                val product = Product()
                product.name = productName
                product.price = productPrice.toInt()

                productList.add(product)

                productName=""
                productPrice =""

            }else
            {
                if(readingName)
                    productName += ch
                else
                    productPrice += ch
            }
        }

        return productList
    }


}
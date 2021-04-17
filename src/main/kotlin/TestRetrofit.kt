import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


fun main()
{
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.jsonbin.io/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val endPoints:JsonBinIoEndPoints = retrofit.create(JsonBinIoEndPoints::class.java)
    val call = endPoints.getMyJson()

    val response = call.execute()

    println(response.code())
    println(response.message())
    println(response.body())
}
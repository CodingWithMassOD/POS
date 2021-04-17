import retrofit2.Call
import retrofit2.http.*

interface JsonBinIoEndPoints
{
    @Headers("content-type:application/json" , "secret-key:$2b$10\$PVGI3K2/raQc.h2Y9ivCjeT43sjbUOXG.Mn2m1JYSjMjI2cezK6XW")
    @POST("/b")
    fun createNewJson(@Body  content:String): Call<String>


    @Headers("secret-key:$2b$10\$PVGI3K2/raQc.h2Y9ivCjeT43sjbUOXG.Mn2m1JYSjMjI2cezK6XW")
    @GET("/b/6003ecd34f42973a289de5ec")
    fun getMyJson():Call<String>
}
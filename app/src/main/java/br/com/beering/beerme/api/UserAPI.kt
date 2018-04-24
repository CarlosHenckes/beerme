package br.com.beering.beerme.api

import br.com.beering.beerme.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface UserAPI {

    @GET("/user/email/{email}/senha/{senha}")
    fun login(@Path("email") email: String, @Path("senha") senha: String) : Call<Boolean>

    @POST("/user")
    fun salvar(@Body user : User) : Call<Void>

}
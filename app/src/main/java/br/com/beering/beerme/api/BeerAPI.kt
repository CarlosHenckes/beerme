package br.com.beering.beerme.api

import br.com.beerme.beerme.model.Beer
import retrofit2.Call
import retrofit2.http.*

interface BeerAPI {

    @GET("/beer")
    fun buscarTodos() : Call<List<Beer>>

    @POST("/beer")
    fun salvar(@Body beer : Beer) : Call<Void>

    @PUT("/beer")
    fun atualizar(@Body beer : Beer) : Call<Void>

    //DELETE("/beer")
    @HTTP(method = "DELETE", path = "/beer", hasBody = true)
    fun excluir(@Body beer : Beer) : Call<Void>

    @GET("/beer/rotulo/{rotulo}")
    fun pesquisarRotulo(@Path("rotulo") rotulo: String) : Call<List<Beer>>

}
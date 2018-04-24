package br.com.beering.beerme.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import br.com.beering.beerme.R
import br.com.beering.beerme.api.BeerAPI
import br.com.beering.beerme.api.RetrofiClient
import br.com.beerme.beerme.model.Beer
import kotlinx.android.synthetic.main.erro.*
import kotlinx.android.synthetic.main.fragment_beers.*
import kotlinx.android.synthetic.main.loading.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeersFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beers, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listarCervejas()

        btnPesquisar.setOnClickListener{
            val api = RetrofiClient.getInstance().create(BeerAPI::class.java)
            api.pesquisarRotulo(inputQuery.text.toString())
                    .enqueue(object : Callback<List<Beer>>{
                        override fun onFailure(call: Call<List<Beer>>?, t: Throwable?) {
                            Toast.makeText(context, t?.message, Toast.LENGTH_LONG).show()
                        }
                        override fun onResponse(call: Call<List<Beer>>?, response: Response<List<Beer>>?) {
                            if(response?.isSuccessful==true){
                                setupLista(response?.body())
                            } else {
                                Log.d("ERRO", R.string.msgErrorLoadingBeers.toString())
                            }
                        }
            })
        }
    }

    fun listarCervejas(){
        val api = RetrofiClient
                .getInstance()
                .create(BeerAPI::class.java)

        loading.visibility = View.VISIBLE

        api.buscarTodos().enqueue(object : Callback<List<Beer>>{
            override fun onFailure(call: Call<List<Beer>>?, t: Throwable?) {
                loading.visibility = View.GONE
                containerErro.visibility = View.VISIBLE
                Log.d("ERRO",t?.message)
            }

            override fun onResponse(call: Call<List<Beer>>?, response: Response<List<Beer>>?) {

                if(response?.isSuccessful==true){
                    loading.visibility = View.GONE
                    setupLista(response?.body())
                } else {
                    loading.visibility = View.GONE
                    containerErro.visibility = View.VISIBLE
                    Log.d("ERRO", R.string.msgErrorLoadingBeers.toString())
                }
            }
        })
    }

    fun setupLista(beers: List<Beer>?) {
        beers.let {
            rvBeers.adapter = ListaBeersAdapter(beers!!, requireContext())
            val layoutManager = LinearLayoutManager(context)
            rvBeers.layoutManager = layoutManager
        }
    }
}

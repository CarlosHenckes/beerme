package br.com.beering.beerme.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.beering.beerme.R
import br.com.beerme.beerme.model.Beer
import kotlinx.android.synthetic.main.item_beer.view.*
import java.io.File
import java.io.FileInputStream

class ListaBeersAdapter(private val beers: List<Beer>,
                        private val context: Context)
    : RecyclerView.Adapter<ListaBeersAdapter.MeuViewHolder>() {

    override fun onBindViewHolder(holder: MeuViewHolder, position: Int) {
        val beer = beers[position]
        holder?.let {
            it.bindView(beer, context)
        }
    }

    override fun getItemCount(): Int {
        return beers.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeuViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.item_beer, parent, false)
        return MeuViewHolder(view)
    }


    class MeuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(beer: Beer, context: Context) {
            var act = context as MainActivity

            itemView.tvRotulo.text = beer.rotulo
            itemView.tvCervejaria.text = beer.cervejaria
            itemView.tvPais.text = beer.pais
            itemView.tvTipo.text = beer.tipo
            itemView.tvTeorAlcoolico.text = " - " + beer.teorAlcoolico.toString() + "%"
            if (beer.urlRotulo.isNullOrEmpty()){
                itemView.ivFoto.setImageDrawable(ContextCompat.getDrawable(itemView.context,
                        R.drawable.nolabel))

            } else {

                val f = File(beer.urlRotulo)
                if(f.exists()==true) {
                    val b = BitmapFactory.decodeStream(FileInputStream(f))
                    itemView.ivFoto!!.setImageBitmap(b)
                } else {
                    itemView.ivFoto.setImageDrawable(ContextCompat.getDrawable(itemView.context,
                            R.drawable.nolabel))
                }
            }

            itemView.setOnClickListener{
                var form = FormFragment()
                form.biro = beer

                act.changeFragment(form)
            }
        }
    }

}

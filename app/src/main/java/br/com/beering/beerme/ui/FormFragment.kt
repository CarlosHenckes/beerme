package br.com.beering.beerme.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import br.com.beering.beerme.R
import br.com.beering.beerme.api.BeerAPI
import br.com.beering.beerme.api.RetrofiClient
import br.com.beerme.beerme.model.Beer
import kotlinx.android.synthetic.main.fragment_form.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*

class FormFragment : Fragment() {

    var biro: Beer = Beer("","","",0F, "","","")
    var myStrings = arrayOf("Pilsen", "Schwarzbier", "Bock", "Weissbier", "Stout", "Dubbel", "Faro", "Geuze", "Kriek")

   // var btn: Button? = null
    var imgview: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_form, container, false)
        //btn = view?.findViewById(R.id.btnGetPhoto) as Button
        imgview = view?.findViewById(R.id.iv) as ImageView

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputTipo.adapter = ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, myStrings)

        btnDelete.visibility = View.GONE

        if (!biro.id.isNullOrEmpty()) {
            inputRotulo.editText?.setText(biro.rotulo)
            inputCervejaria.editText?.setText(biro.cervejaria)
            inputAlcool.editText?.setText(biro.teorAlcoolico.toString())
            inputPais.editText?.setText(biro.pais)
            inputTipo.setSelection(makeSelection(biro.tipo))
            //inputURL.editText?.setText(biro.urlRotulo)
            val f = File(biro.urlRotulo)
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            imgview!!.setImageBitmap(b)

            btnDelete.visibility = View.VISIBLE
        }

        imgview?.setOnClickListener {
            v: View -> btnClick(v)
        }

        btnDelete.setOnClickListener{
            val api = RetrofiClient.getInstance().create(BeerAPI::class.java)
            var mainContext = context as MainActivity

            api.excluir(biro).enqueue(object : Callback<Void>{
                override fun onFailure(call: Call<Void>?, t: Throwable?) {
                    Toast.makeText(context, t?.message, Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                    if(response?.isSuccessful == true){
                        // back to the list
                        mainContext.changeFragment(BeersFragment())
                    } else {
                        Toast.makeText(context, response?.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        btnSalvar.setOnClickListener{
            val api = RetrofiClient.getInstance().create(BeerAPI::class.java)
            var mainContext = context as MainActivity

            biro.rotulo = inputRotulo.editText?.text.toString()
            biro.cervejaria = inputCervejaria.editText?.text.toString()
            biro.teorAlcoolico = inputAlcool.editText?.text.toString().toFloat()
            biro.pais = inputPais.editText?.text.toString()
            biro.tipo = inputTipo.selectedItem.toString()
            //biro.urlRotulo = inputURL.editText?.text.toString()

            if (biro.id.isNullOrEmpty()){
                // create new beer
                biro.id = null
                api.salvar(biro).enqueue(object : Callback<Void> {
                    override fun onFailure(call: Call<Void>?, t: Throwable?) {
                        Log.e("BEER", t?.message)
                    }

                    override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                        if (response?.isSuccessful == true){
                            limparCampos()
                            mainContext.changeFragment(BeersFragment())
                        } else {
                            Toast.makeText(context, "Deu ruim!", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                // update information
                api.atualizar(biro).enqueue(object : Callback<Void>{
                    override fun onFailure(call: Call<Void>?, t: Throwable?) {
                        Log.e("BEER", t?.message)
                    }

                    override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                        if(response?.isSuccessful == true){
                            limparCampos()
                            mainContext.changeFragment(BeersFragment())
                        } else {
                            Toast.makeText(context, "Deu ruim!", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }

    private fun limparCampos(){
        inputRotulo.editText?.setText("")
        inputCervejaria.editText?.setText("")
        inputPais.editText?.setText("")
        inputAlcool.editText?.setText("")
        inputTipo.setSelection(0)
        //inputURL.editText?.setText("")
    }

    fun makeSelection(str: String)
    : Int {
        var i: Int = 0
        for (item: String in myStrings) {
            if (item.equals(str)) {
                return i
            }
            i++
        }
        return 0
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)

        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(context,
                    arrayOf(f.getPath()),
                    arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())
            biro.urlRotulo = f.getAbsolutePath()
            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    private fun btnClick(v: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 123)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123){
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            saveImage(thumbnail)
            imgview?.setImageBitmap(thumbnail)
        }
    }

    companion object {
        private val IMAGE_DIRECTORY = "/beerme"
    }
}

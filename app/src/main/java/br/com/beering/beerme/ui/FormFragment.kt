package br.com.beering.beerme.ui

import android.app.Activity.RESULT_CANCELED
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import br.com.beering.beerme.R
import br.com.beering.beerme.api.BeerAPI
import br.com.beering.beerme.api.RetrofiClient
import br.com.beerme.beerme.model.Beer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_form.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FormFragment : Fragment() {

    var biro: Beer = Beer("","","",0F, "","","")
    var myStrings = arrayOf("Pilsen", "Schwarzbier", "Bock", "Weissbier", "Stout", "Dubbel", "Faro", "Geuze", "Kriek")

    var imgview: ImageView? = null

    private var filePath: Uri? = null

    // vars firebase
    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null
    internal var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_form, container, false)
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

            if (!biro.urlRotulo.isNullOrEmpty()){

                Picasso.get().load(biro.urlRotulo)
                        .placeholder(R.drawable.load)
                        .error(R.drawable.nolabel)
                        .into(imgview)
            }

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

            // make some check
            if (inputRotulo.editText?.text.toString().isNullOrEmpty()){
                Toast.makeText(context, R.string.msgRotuloVazio, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            biro.rotulo = inputRotulo.editText?.text.toString()
            biro.cervejaria = inputCervejaria.editText?.text.toString()
            var tmpAlcool = inputAlcool.editText?.text.toString()
            try {
                biro.teorAlcoolico = tmpAlcool.toFloat()

            } catch (e: NumberFormatException){
                Toast.makeText(context, R.string.msgAlcoolNotNumber, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            biro.pais = inputPais.editText?.text.toString()
            biro.tipo = inputTipo.selectedItem.toString()

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

    private fun btnClick(v: View) {
        showPictureDialog()
    }

    fun pictureFromGallery(){
        val galIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galIntent, 124)
    }

    fun pictureFromCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 123)
    }

    private fun showPictureDialog(){
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("GALLERY","CAMERA")
        pictureDialog.setItems(pictureDialogItems){
            dialog, which ->
            when (which){
                0 -> pictureFromGallery()
                1 -> pictureFromCamera()
            }
        }
        pictureDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        var mainContext = context as MainActivity

        if(resultCode == RESULT_CANCELED){
            return
        }

        if (requestCode == 123){
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            //saveImage(thumbnail)
            filePath = data.data
            upload()
            imgview?.setImageBitmap(thumbnail)
        } else if (requestCode == 124){
            filePath = data!!.data
            val thumbnail = MediaStore.Images.Media.getBitmap(mainContext.contentResolver, data!!.data)
            imgview?.setImageBitmap(thumbnail)
            upload()
        }
    }

    fun upload(){
        if(filePath != null){
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle(R.string.upCourse)
            progressDialog.show()

            val imageRef = storageReference!!.child("images/" + UUID.randomUUID().toString())
            imageRef.putFile(filePath!!)
                    .addOnSuccessListener { taskSnapshot ->
                        val name = taskSnapshot.metadata!!.name
                        val url = taskSnapshot.downloadUrl.toString()
                        biro.urlRotulo = url + name!!
                        progressDialog.dismiss()
                        Toast.makeText(context, R.string.upokresult, Toast.LENGTH_SHORT).show()
                    }
                    . addOnFailureListener{
                        progressDialog.dismiss()
                        Toast.makeText(context, R.string.uperrormessage, Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                        progressDialog.setMessage("" + progress.toInt() + "%...")
                    }
        }
    }
}

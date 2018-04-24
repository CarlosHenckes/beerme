package br.com.beering.beerme.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.com.beering.beerme.R
import br.com.beering.beerme.api.RetrofiClient
import br.com.beering.beerme.api.UserAPI
import br.com.beering.beerme.model.User
import kotlinx.android.synthetic.main.activity_user.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        btnCadastrar.setOnClickListener {
            var txtEmail: String = txtEmail.text.toString()
            var txtPass: String = txtPass.text.toString()

            if(txtEmail.isNullOrEmpty() || txtPass.isNullOrEmpty()){
                Toast.makeText(this, "Preencher ambos os campos", Toast.LENGTH_SHORT).show()

            } else {
                val api = RetrofiClient.getInstance().create(UserAPI::class.java)
                var user: User = User(null, txtEmail, txtPass)

                api.salvar(user).enqueue(object : Callback<Void> {
                    override fun onFailure(call: Call<Void>?, t: Throwable?) {
                        Toast.makeText(this@UserActivity, t?.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                        Toast.makeText(this@UserActivity, "Cadastro realizado", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@UserActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                })
            }
        }
    }
}

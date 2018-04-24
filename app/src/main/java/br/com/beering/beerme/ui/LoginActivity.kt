package br.com.beering.beerme.ui

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import br.com.beering.beerme.R
import br.com.beering.beerme.api.RetrofiClient
import br.com.beering.beerme.api.UserAPI
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var edit = this@LoginActivity.getSharedPreferences("login", Context.MODE_PRIVATE)

        val log = edit!!.getInt("logged", 0)
        if (log==1){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

        txtCadastroUsuario.setOnClickListener {
            val intent = Intent(this@LoginActivity, UserActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            var txtEmail: String = txtUserEmail.text.toString()
            var txtPass: String = txtPassword.text.toString()

            // clean fileds


            if (txtEmail.contains("@") && txtPass.length > 0) {

                val api = RetrofiClient.getInstance().create(UserAPI::class.java)

                api.login(txtEmail, txtPass).enqueue(object : Callback<Boolean> {
                    override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                        Log.e("LOGIN", t?.message)
                    }

                    override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {

                        if (response?.isSuccessful == true) {
                            if (response?.body() == true) {

                                if (chkLogin.isChecked){
                                    edit!!.edit().putInt("logged", 1).commit()
                                }

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@LoginActivity, R.string.loginErrorMsg, Toast.LENGTH_LONG).show()
                            }

                        } else {
                            Toast.makeText(this@LoginActivity, "Opps", Toast.LENGTH_LONG).show()
                        }
                    }
                })
            } else {
                Toast.makeText(this@LoginActivity, R.string.loginErrorMsg, Toast.LENGTH_LONG).show()
            }
        }

    }

}

package com.example.yellowcarscounter.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.yellowcarscounter.GetStartedActivity
import com.example.yellowcarscounter.R
import com.example.yellowcarscounter.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.forgot_password_dialog.view.*
import java.util.*


class LoginActivity : AppCompatActivity(){

    private var mAuth: FirebaseAuth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        loadLocate()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeMenu()

        b_login.setOnClickListener {
            startLogin()
        }

        b_forgot_password.setOnClickListener{
            sendNewPassword()
        }
    }

    private fun initializeMenu(){
        val menuToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(menuToolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.login)
    }

    private fun sendNewPassword(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.forgot_password_dialog,null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        //show dialog
        val  mAlertDialog = mBuilder.show()

        mDialogView.b_dialog_send.setOnClickListener {//mDialogView.b_dialog_send.isEnabled=false
            if (mDialogView.et_dialog_email.text.isEmpty()){
                Toast.makeText(this, getString(R.string.fill_empty_fields), Toast.LENGTH_SHORT).show()
            }
            else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(mDialogView.et_dialog_email.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            mAlertDialog.dismiss()
                            Toast.makeText(this, getString(R.string.email_send), Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(this, getString(R.string.error)+getString(R.string.no_user_with_email), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        //cancel button click of custom layout
        mDialogView.b_dialog_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun startLogin(){
        pb_login.visibility= View.VISIBLE
        b_login.isEnabled=false
        b_login.text=""
        if (et_email.text.isEmpty()||et_password.text.isEmpty()){
            pb_login.visibility= View.GONE
            b_login.isEnabled=true
            b_login.text="LOGIN"
            Toast.makeText(this, getString(R.string.fields_are_empty), Toast.LENGTH_SHORT).show()
        }
        else{
            mAuth.signInWithEmailAndPassword(et_email.text.toString(),et_password.text.toString()).addOnCompleteListener {
                if(!it.isSuccessful){
                    pb_login.visibility= View.GONE
                    b_login.isEnabled=true
                    b_login.text="LOGIN"
                    Toast.makeText(this, getString(R.string.login_problem), Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, GetStartedActivity::class.java))
            finish()
            return true
        }
        if (id == R.id.actionSettings) {
            showChangeLang()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, GetStartedActivity::class.java))
        finish()
    }

    private fun showChangeLang() {
        val listItmes = arrayOf( getString(R.string.sk_language), getString(R.string.en_language))

        val mBuilder = AlertDialog.Builder(this@LoginActivity)
        mBuilder.setTitle(getString(R.string.choose_language))
        mBuilder.setSingleChoiceItems(listItmes, -1) { dialog, which ->
            if (which == 0) {
                setLocate("sk")
                finish()
                overridePendingTransition(0, 0)
                startActivity(intent)
                overridePendingTransition(0, 0)
            } else if (which == 1) {
                setLocate("en")
                finish()
                overridePendingTransition(0, 0)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            dialog.dismiss()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun setLocate(Lang: String) {
        val locale = Locale(Lang)

        Locale.setDefault(locale)

        val config = Configuration()

        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()
    }

    private fun loadLocate() {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        if (language != null) {
            setLocate(language)
        }
    }
}

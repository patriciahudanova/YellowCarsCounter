package com.example.yellowcarscounter.register

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.yellowcarscounter.GetStartedActivity
import com.example.yellowcarscounter.R
import com.example.yellowcarscounter.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity(){

    private var mAuth: FirebaseAuth= FirebaseAuth.getInstance()
    private var mDatabase : DatabaseReference= FirebaseDatabase.getInstance().reference.child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {

        loadLocate()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeMenu()

        b_register.setOnClickListener {
            registerUser()
        }
    }

    private fun initializeMenu(){
        val menuToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(menuToolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.registration)
    }

    private fun saveUserData(uid : String, username: String, email:String, pic:String){
        val current_user_db = mDatabase.child(uid)
        current_user_db.child("name").setValue(username)
        current_user_db.child("email").setValue(email)
        current_user_db.child("pic").setValue(pic)
    }

    private fun registerUser(){
        var email = et_email_reg.text.toString()
        var password = et_password_reg.text.toString()
        var confirmPassword = et_conf_password_reg.text.toString()
        var username = et_username.text.toString()

        if(email.isEmpty()&&password.isEmpty()&&confirmPassword.isEmpty()&&username.isEmpty()){
            Toast.makeText(this,getString(R.string.fields_are_empty), Toast.LENGTH_SHORT).show()
        }
        else{
            if (confirmPassword != password){
                Toast.makeText(this,getString(R.string.passwords_dont_match), Toast.LENGTH_SHORT).show()
            }
            else{
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    if (it.isSuccessful){
                        val uid = mAuth.currentUser!!.uid
                        var pic = FirebaseStorage.getInstance().reference.child("pics/${uid}").path
                        saveUserData(uid,username.toLowerCase(),email,pic)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this, getString(R.string.register_problem), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, GetStartedActivity::class.java))
        finish()
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

    private fun showChangeLang() {
        val listItmes = arrayOf( getString(R.string.sk_language), getString(R.string.en_language))

        val mBuilder = AlertDialog.Builder(this@RegisterActivity)
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

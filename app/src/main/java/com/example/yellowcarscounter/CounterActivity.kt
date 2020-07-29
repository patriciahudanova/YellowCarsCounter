package com.example.yellowcarscounter

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
import com.example.yellowcarscounter.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_counter.*
import java.util.*


class CounterActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        loadLocate()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)

        initializeMenu()

        b_logout.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(this,GetStartedActivity::class.java))
            finish()
        }
    }

    private fun initializeMenu(){
        val menuToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(menuToolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.counter)
    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
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
            startActivity(Intent(this, MainActivity::class.java))
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

        val mBuilder = AlertDialog.Builder(this@CounterActivity)
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

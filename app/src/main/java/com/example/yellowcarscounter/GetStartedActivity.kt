package com.example.yellowcarscounter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yellowcarscounter.login.LoginActivity
import com.example.yellowcarscounter.main.MainActivity
import com.example.yellowcarscounter.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_get_started.*

class GetStartedActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        b_go_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        b_go_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}

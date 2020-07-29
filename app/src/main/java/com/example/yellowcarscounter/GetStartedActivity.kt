package com.example.yellowcarscounter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_get_started.*

class GetStartedActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        b_go_login.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

        b_go_register.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        val mAuth = FirebaseAuth.getInstance()

        val user = mAuth.currentUser
        if(user!=null){
            val intent = Intent(this,ManageFriendsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}

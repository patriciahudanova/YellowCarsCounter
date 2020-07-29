package com.example.yellowcarscounter.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.bumptech.glide.Glide
import com.example.yellowcarscounter.GetStartedActivity
import com.example.yellowcarscounter.R
import com.example.yellowcarscounter.main.tabs.TabsPagerAdapter
import com.example.yellowcarscounter.User
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class MainActivity : AppCompatActivity() {

    internal var user: User? = null
    private val mAuth: FirebaseAuth= FirebaseAuth.getInstance()

    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"

    private lateinit var imageUri: Uri
    private val REQUEST_IMAGE_CAPTURE = 100

    private val currentUser = FirebaseAuth.getInstance().currentUser

    var numOfYellowCars: Int =0

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLocate()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeMenu()

        initializeTextViewsAndPhoto()

        takeUsersPictureOnClick()

        initializeTabs()
    }

    private fun initializeTabs(){
        tab_layout.setSelectedTabIndicatorColor(Color.YELLOW)
        tab_layout.setBackgroundColor(ContextCompat.getColor(this, R.color.whiteAlmostTransparent))
        tab_layout.tabTextColors = ContextCompat.getColorStateList(this, R.color.white)

        // Set different Text Color for Tabs for when are selected or not
        //tab_layout.setTabTextColors(R.color.normalTabTextColor, R.color.selectedTabTextColor)

        // Number Of Tabs
        val numberOfTabs = 2

        // Set Tabs in the center
        //tab_layout.tabGravity = TabLayout.GRAVITY_CENTER

        // Show all Tabs in screen
        tab_layout.tabMode = TabLayout.MODE_FIXED

        // Scroll to see all Tabs
        //tab_layout.tabMode = TabLayout.MODE_SCROLLABLE

        // Set Tab icons next to the text, instead above the text
        tab_layout.isInlineLabel = true

        // Set the ViewPager Adapter
        val adapter = TabsPagerAdapter(supportFragmentManager, lifecycle, numberOfTabs)
        tabs_viewpager.adapter = adapter

        // Enable Swipe
        tabs_viewpager.isUserInputEnabled = true

        // Link the TabLayout and the ViewPager2 together and Set Text & Icons
        TabLayoutMediator(tab_layout, tabs_viewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.my_account)
                    tab.setIcon(R.drawable.ic_directions_car_24dp)
                }
                1 -> {
                    tab.text = getString(R.string.all_users)
                    tab.setIcon(R.drawable.ic_supervisor_account_black_24dp)
                }
            }
            // Change color of the icons
            tab.icon?.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                            Color.WHITE,
                            BlendModeCompat.SRC_ATOP
                    )
        }.attach()
    }

    private fun takeUsersPictureOnClick(){
        civ_userphoto.setOnClickListener {
            takePictureIntent()
            val photo = when {
                ::imageUri.isInitialized -> imageUri
                currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                else -> currentUser.photoUrl
            }

            val updates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(photo)
                    .build()

            pb_upload_pic.visibility = View.VISIBLE

            currentUser?.updateProfile(updates)
                    ?.addOnCompleteListener { task ->
                        pb_upload_pic.visibility = View.INVISIBLE
                        if (task.isSuccessful) {
                            Toast.makeText(this,getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this,getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private fun initializeTextViewsAndPhoto(){
        var ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)

        val menuListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                //TODO - handle error
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(User::class.java)
                tv_name.text = user?.name
                tv_email.text= user?.email
                numOfYellowCars= user?.numyc!!
            }
        }

        ref.addListenerForSingleValueEvent(menuListener)

        val imageref = FirebaseStorage.getInstance().reference.child("pics/${mAuth.uid}")

        imageref.downloadUrl.addOnSuccessListener {Uri->
            val imageURL = Uri.toString()

            Glide.with(this)
                    .load(imageURL)
                    .into(civ_userphoto)
        }
    }

    private fun initializeMenu(){
        val menuToolbar = findViewById<Toolbar>(R.id.toolbar)
        // Initializing toolbar menu
        setSupportActionBar(menuToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.home)
    }

    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(this?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)

        pb_upload_pic.visibility = View.VISIBLE
        upload.addOnCompleteListener { uploadTask ->
            pb_upload_pic.visibility = View.INVISIBLE

            if (uploadTask.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        civ_userphoto.setImageBitmap(bitmap)
                    }
                }
            } else {
                uploadTask.exception?.let {
                    //TODO
                }
            }
        }
    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, getString(R.string.logout), Toast.LENGTH_SHORT).show()
        mAuth.signOut()
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
            Toast.makeText(applicationContext, getString(R.string.logout), Toast.LENGTH_SHORT).show()
            mAuth.signOut()
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

        val mBuilder = AlertDialog.Builder(this@MainActivity)
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




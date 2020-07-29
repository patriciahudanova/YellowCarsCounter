package com.example.yellowcarscounter.main.tabs.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yellowcarscounter.CounterActivity
import com.example.yellowcarscounter.R
import com.example.yellowcarscounter.User
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.android.synthetic.main.fragment_users.view.*
import kotlinx.android.synthetic.main.recycle_list_single_user.view.*

class UsersFragment : Fragment() {

    private lateinit var mUsersList: RecyclerView
    private var mUsersDatabaseReference: DatabaseReference= FirebaseDatabase.getInstance().getReference("Users")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView = inflater.inflate(R.layout.fragment_users, container, false)

        mUsersList = rootView.rv_user_list
        mUsersList.layoutManager = LinearLayoutManager(context)

        searchViewSetup(mUsersDatabaseReference,rootView)

        swipeToRefreshSetup(mUsersDatabaseReference,rootView)

        logRecyclerView(mUsersDatabaseReference)
        return rootView
    }


    private fun searchViewSetup(databaseReference: Query, rootView: View){
        var searchView: SearchView? = rootView.sv_users

        val searchEditText: EditText =
                searchView?.findViewById(androidx.appcompat.R.id.search_src_text) as EditText

        searchEditText.setTextColor(resources.getColor(R.color.white))
        searchEditText.setHintTextColor(resources.getColor(R.color.white))

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(s: String?): Boolean {
                if (TextUtils.isEmpty(s)) {
                    logRecyclerView(databaseReference)
                } else {
                    if (s != null) {
                        logRecyclerView(databaseReference.orderByChild("name").startAt(s.toLowerCase()).endAt(s + "\uf8ff"))
                    }
                }
                return false
            }
        })
    }

    private fun swipeToRefreshSetup(databaseReference: Query, rootView: View){
        context?.let {
            ContextCompat.getColor(
                    it, R.color.transparent)
        }?.let { rootView.swipetorefresh.setProgressBackgroundColorSchemeColor(it) }
        rootView.swipetorefresh.setColorSchemeColors(Color.YELLOW)

        rootView.swipetorefresh.setOnRefreshListener {
            logRecyclerView(databaseReference)
            rootView.swipetorefresh.isRefreshing = false
        }
    }

    private fun logRecyclerView(databaseReference: Query) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!

        var firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<User, UserViewHolder>(
            User::class.java,
                R.layout.recycle_list_single_user,
            UserViewHolder::class.java,
                databaseReference
        ){
            override fun populateViewHolder(viewHolder: UserViewHolder, model: User, position: Int) {
                viewHolder.mView.tv_name.text = model.name
                viewHolder.mView.tv_email.text = model.email
                try {
                    val imageref = model.pic?.let { FirebaseStorage.getInstance().reference.child(it) }
                    imageref?.downloadUrl?.addOnSuccessListener { Uri ->
                        val imageURL = Uri.toString()
                        context?.let {
                            Glide.with(it)
                                .load(imageURL)
                                .into(viewHolder.mView.civ_profile_pic)
                        }
                    }
                }
                catch(e: StorageException){
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                }

                viewHolder.mView.setOnClickListener {
                    if(model.email != currentUser.email) {
                        Toast.makeText(
                            context,
                            getString(R.string.started_game_with) + model.name +  getString(R.string.enjoy),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(context, CounterActivity::class.java)
                        context?.startActivity(intent)
                    }
                    else{
                        Toast.makeText(
                            context,
                            getString(R.string.thats_your_account) + model.name ,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        mUsersList.adapter=firebaseRecyclerAdapter
    }

    class UserViewHolder(var mView: View) : RecyclerView.ViewHolder(mView)
}


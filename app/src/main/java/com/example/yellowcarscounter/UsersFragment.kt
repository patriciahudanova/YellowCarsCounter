package com.example.yellowcarscounter

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.android.synthetic.main.fragment_users.view.*
import kotlinx.android.synthetic.main.recycle_list_single_user.view.*

class UsersFragment : Fragment() {

    lateinit var mUsersList: RecyclerView
    lateinit var mUsersDatabaseReference: DatabaseReference
    val mAuth= FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        var rootView = inflater.inflate(R.layout.fragment_users, container, false)

        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        mUsersList = rootView.rv_user_list
        mUsersList.layoutManager = LinearLayoutManager(context)

        var searchView: SearchView? = rootView.sv_users


        val searchEditText: EditText =
            searchView?.findViewById(androidx.appcompat.R.id.search_src_text) as EditText

        searchEditText.setTextColor(getResources().getColor(R.color.white))
        searchEditText.setHintTextColor(getResources().getColor(R.color.white))

        val searchIcon: ImageView =
            searchView?.findViewById(androidx.appcompat.R.id.search_mag_icon) as ImageView
        searchIcon.setImageResource(R.drawable.ic_search_white)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                if (TextUtils.isEmpty(s)) {
                    logRecyclerView()
                } else {
                    var FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<User, UserViewHolder>(
                        User::class.java,
                        R.layout.recycle_list_single_user,
                        UserViewHolder::class.java,
                        mUsersDatabaseReference.orderByChild("name").startAt(s).endAt(s + "\uf8ff")
                    ){
                        override fun populateViewHolder(viewHolder: UserViewHolder, model: User, position: Int) {
                            viewHolder.mView.tv_name.text = model.name
                            viewHolder.mView.tv_email.text = model.email

                            val imageref = model.pic?.let { FirebaseStorage.getInstance().reference.child(it) }
                            imageref?.downloadUrl?.addOnSuccessListener { Uri ->
                                val imageURL = Uri.toString()
                                context?.let {
                                    Glide.with(it)
                                        .load(imageURL)
                                        .into(viewHolder.mView.civ_profile_pic)
                                }
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
                    mUsersList.adapter=FirebaseRecyclerAdapter
                }
                return false
            }
        })


        context?.let {
            ContextCompat.getColor(
                it, R.color.transparent)
        }?.let { rootView.swipetorefresh.setProgressBackgroundColorSchemeColor(it) }
        rootView.swipetorefresh.setColorSchemeColors(Color.YELLOW)

        rootView.swipetorefresh.setOnRefreshListener {
            mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
            mUsersList = rootView.findViewById(R.id.rv_user_list)
            mUsersList.layoutManager = LinearLayoutManager(context)
            logRecyclerView()
            rootView.swipetorefresh.isRefreshing = false
        }
        logRecyclerView()
        return rootView
    }


    private fun logRecyclerView() {
        var FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<User, UserViewHolder>(
            User::class.java,
            R.layout.recycle_list_single_user,
            UserViewHolder::class.java,
            mUsersDatabaseReference
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
        mUsersList.adapter=FirebaseRecyclerAdapter
    }

    class UserViewHolder(var mView: View) : RecyclerView.ViewHolder(mView)
}


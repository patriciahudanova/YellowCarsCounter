package com.example.yellowcarscounter.main.tabs.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.yellowcarscounter.R
import com.example.yellowcarscounter.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_counter.view.*

class CounterFragment : Fragment() {
    var numOfYellowCars: Int =0
    var numOfClicks: Int = 0
    internal var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_counter, container, false)

        var ref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)

        val menuListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                //TODO - handle error
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(User::class.java)
                numOfYellowCars= user?.numyc!!
                rootView.tv_score.text=numOfYellowCars.toString()
            }
        }

        ref.addListenerForSingleValueEvent(menuListener)

        rootView.b_plus.setOnClickListener {
            numOfYellowCars += 1
            ref.child("numyc").setValue(numOfYellowCars)
            rootView.tv_score.text=numOfYellowCars.toString()
            numOfClicks=0
        }

        rootView.b_minus.setOnClickListener {
            if (numOfYellowCars==0) {
                if (numOfClicks==0){
                    Toast.makeText(context,getString(R.string.negative_number), Toast.LENGTH_SHORT).show()
                    numOfClicks=1
                }
                else
                    Toast.makeText(context,getString(R.string.you_kidding), Toast.LENGTH_SHORT).show()
            }
            else{
                numOfYellowCars -= 1
                ref.child("numyc").setValue(numOfYellowCars)
                rootView.tv_score.text = numOfYellowCars.toString()
                numOfClicks = 0
            }
        }
        return rootView
    }

}

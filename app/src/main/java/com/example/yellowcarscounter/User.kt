package com.example.yellowcarscounter

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User() {
    var name: String? = ""
    var email: String? = ""
    var pic: String? = ""
    var numyc: Int = 0

    constructor(name:String?, email: String? = "",
                 pic: String? ="", numyc: Int = 0) : this() {
        this.name = name
        this.email = email
        this.pic = pic
        this.numyc = numyc

    }
}

package com.lukasz.myapplicatiokotlin.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (val uid: String, val username: String, val profileImageUrl: String): Parcelable {
    constructor() : this("", "", "")
}

// zeby skorzystac z parcela trzeba dodac do gradle module nad android ponizsze linie

//androidExtensions {
//    experimental = true
//}
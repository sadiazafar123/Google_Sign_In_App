package com.example.googlesigninapp.model

import android.os.Parcel
import android.os.Parcelable

data class AuthUserPostData(
    var userId:String?=null,
    var name:String?=null,
    var email:String?=null,
    var mobNumber:String?=null,
    var imaged:String?=null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(mobNumber)
        parcel.writeString(imaged)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AuthUserPostData> {
        override fun createFromParcel(parcel: Parcel): AuthUserPostData {
            return AuthUserPostData(parcel)
        }

        override fun newArray(size: Int): Array<AuthUserPostData?> {
            return arrayOfNulls(size)
        }
    }
}

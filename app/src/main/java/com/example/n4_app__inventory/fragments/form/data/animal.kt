package com.example.n4_app__inventory.fragments.form.data

import android.os.Parcel
import android.os.Parcelable

data class Animal(
    val anmlType: String = "",
    val breedType: String = "",
    val birthDate: String = "",
    val id: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val marriageStatus: String = "",
    val origin: String = "",
    val purchaseDate: String = "",
    val race: String = "",
    val sex: String = "",
    val inputDate: String = "",
    val konsumsiPakan: String = "",
    val inputPenmDate: String = "",
    val bbtPenm: String = "",
    val anmlPrice: String = "",
    val inputDateKejadian: String = "",
    val kejadianKhusus: String = "",
    val catatanKhusus: String = "",
    val imageUrlKejadian: String = "",
    val anmlPhysStat: String = "",
    val anmlNum: String = "",
    val anmlNumIndukan: String = "",
    val anmlNumPejantan: String = "",

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",

        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(anmlType)
        parcel.writeString(breedType)
        parcel.writeString(birthDate)
        parcel.writeString(id)
        parcel.writeString(imageUrl)
        parcel.writeString(location)
        parcel.writeString(marriageStatus)
        parcel.writeString(origin)
        parcel.writeString(purchaseDate)
        parcel.writeString(race)
        parcel.writeString(sex)
        parcel.writeString(inputDate)
        parcel.writeString(konsumsiPakan)
        parcel.writeString(inputPenmDate)
        parcel.writeString(bbtPenm)
        parcel.writeString(anmlPrice)
        parcel.writeString(inputDateKejadian)
        parcel.writeString(kejadianKhusus)
        parcel.writeString(catatanKhusus)
        parcel.writeString(imageUrlKejadian)
        parcel.writeString(anmlPhysStat)
        parcel.writeString(anmlNum)
        parcel.writeString(anmlNumIndukan)
        parcel.writeString(anmlNumPejantan)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Animal> {
        override fun createFromParcel(parcel: Parcel): Animal {
            return Animal(parcel)
        }

        override fun newArray(size: Int): Array<Animal?> {
            return arrayOfNulls(size)
        }
    }
}
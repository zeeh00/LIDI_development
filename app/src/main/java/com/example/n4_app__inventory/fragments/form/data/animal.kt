package com.example.n4_app__inventory.fragments.form.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
    val anmlNumPejantan: String = ""
) : Parcelable

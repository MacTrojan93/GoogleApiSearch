package com.example.googlebooksapi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BooksResponse(
    val items: List<ItemsDescription>
):Parcelable

@Parcelize
data class ItemsDescription(
    val volumeInfo: VolumeInfo
):Parcelable

@Parcelize
data class VolumeInfo(
    val title: String,
    val subtitle: String
):Parcelable

//Java Serializable
// convert into bytes, and its going to identify this bytes with
// UID
// Uses Reflection...

// Android Parcelable

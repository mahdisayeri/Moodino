package com.iranmobiledev.moodino.data

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import saman.zamani.persiandate.PersianDate


//TODO warning some of this can be null while adding a entry ! byTayeb.

@Entity(tableName = "table_entry")
@Parcelize
data class Entry(
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null,
    var note : String = "",
    var activities : MutableList<Activity> = mutableListOf(),
    var photoPath : String = "",
    var date : EntryDate = EntryDate(0,0,0),
    var time : EntryTime = EntryTime("",""),
    var emojiValue : Int = 3
) : Parcelable

@Parcelize
data class EntryDate(
    val year : Int,
    val month : Int,
    val day : Int,
) : Parcelable {

    fun getDate(): String {
        val monthName = PersianDate().monthName(month,PersianDate.Dialect.IRANIAN)
        return "$day $monthName $year"
    }
}

@Parcelize
data class EntryTime(
    var hour : String,
    var minutes : String
) : Parcelable {

    fun getTime(): String{
        var hour = hour
        var minute = hour
        if (hour.length == 1){
            hour = "0$hour"
        }
        if (minute.length == 1){
            minute = "0$minute"
        }
        return "$hour:$minute"
    }
}


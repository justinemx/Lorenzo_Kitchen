package com.mooncode.lorenzoskitchen

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId
import java.io.ByteArrayOutputStream
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

enum class Gender {
    MALE,
    FEMALE,
    OTHER;

    override fun toString(): String {
        return when (this) {
            MALE -> "Male"
            FEMALE -> "Female"
            OTHER -> "Other"
        }
    }

    companion object {
        // Convert a string to the corresponding enum constant (case-insensitive)
        fun fromString(value: String): Gender? {
            return try {
                valueOf(value.uppercase(Locale.ROOT))
            } catch (e: IllegalArgumentException) {
                null // Handle the case where the string does not match any enum constant
            }
        }
    }
}


open class User(
    @PrimaryKey var id: ObjectId = ObjectId.get(),
    var firstName: String = "",
    var middleName: String = "",
    var lastName: String = "",
    var profileUUID: String = "",
    var birthday: Long = 0,
    var gender: String = "",
    var address: String = "",
    var phoneNumber: String = "",
    var email: String = "",
    var password: String = "",
    var cart: RealmList<CartItem> = RealmList(),
    var isAdministrator: Boolean = false
): RealmObject() {
    fun setGenderEnum(genderEnum: Gender) {
        this.gender = genderEnum.name
    }

    fun getGenderEnum(): Gender {
        return try {
            Gender.valueOf(gender)
        } catch (e: IllegalArgumentException) {
            Gender.OTHER
        }
    }

    fun getId(): String {
        return id.toString()
    }
}

open class CartItem(
    @PrimaryKey var id: ObjectId = ObjectId.get(),
    var productUUID: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    var imageUUID: String = ""
): RealmObject() {

    fun getId(): String {
        return id.toString()
    }
}

open class Order(
    @PrimaryKey var id: ObjectId = ObjectId.get(),
    var user: User? = null,
    var itemsID: RealmList<CartItem> = RealmList(),
    var total: Double = 0.0,
    var status: String = "",
    var date: String = ""
): RealmObject() {

    fun getId(): String {
        return id.toString()
    }
}

open class Product(
    @PrimaryKey var id: ObjectId = ObjectId.get(),
    var name: String = "",
    var price: Double = 0.0,
    var category: String = "",
    var description: String = "",
    var imagesUUID: RealmList<String> = RealmList(),
    var quantity: Int = 0,
    var isAvailable: Boolean = true
): RealmObject() {

    fun getId(): String {
        return id.toString()
    }
}

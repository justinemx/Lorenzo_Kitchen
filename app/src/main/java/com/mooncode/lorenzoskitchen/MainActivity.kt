package com.mooncode.lorenzoskitchen

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import io.realm.RealmList
import org.bson.types.ObjectId
import java.util.UUID


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val navView = findViewById<FragmentContainerView>(R.id.navView)

        val existingPerson = realm.where(User::class.java).equalTo("email", "admin").findFirst()

        if (existingPerson == null) {
            val profileUUID = UUID.randomUUID().toString().replace("-", "")
            Thread {
                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    profileUUID,
                    ImageUtils.resize(
                        ImageUtils.fromBase64(getString(R.string.admin_profile_image)),
                        1000,
                        1000)
                )
            }.start()

            realm.executeTransaction {
                val user = realm.createObject(User::class.java, UUID.randomUUID().toString().replace("-", ""))
                user.firstName = "admin"
                user.profileUUID = profileUUID
                user.email = "admin"
                user.password = "admin"
                user.isAdministrator = true
            }
        }

        // check if Adobo is already in the menu database
        val existingAdobo = realm.where(Product::class.java).equalTo("name", "Adobo").findFirst()
        if (existingAdobo == null) {
            val adoboUUID1 = UUID.randomUUID().toString().replace("-", "")
            val adoboUUID2 = UUID.randomUUID().toString().replace("-", "")

            Thread {
                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    adoboUUID1,
                    ImageUtils.getFromDrawable(this, R.drawable.product_adobo_1)
                )

                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    adoboUUID2,
                    ImageUtils.getFromDrawable(this, R.drawable.product_adobo_2)
                )
            }.start()

            realm.executeTransaction {
                val adobo = realm.createObject(Product::class.java, UUID.randomUUID().toString().replace("-", ""))
                adobo.name = "Adobo"
                adobo.description = "Adobo is a popular dish and cooking process in Philippine cuisine that involves meat, seafood, or vegetables marinated in vinegar, soy sauce, garlic, bay leaves, and black peppercorns, which is browned in oil, and simmered in the marinade."
                adobo.price = 100.0
                adobo.imagesUUID = RealmList(adoboUUID1, adoboUUID2)
                adobo.category = "Main Dish"
                adobo.quantity = 100
                adobo.isAvailable = true
            }
        }


        // check if Lumpia is already in the menu database
        val existingLumpia = realm.where(Product::class.java).equalTo("name", "Lumpia").findFirst()
        if (existingLumpia == null) {
            val lumpiaUUID1 = UUID.randomUUID().toString().replace("-", "")
            val lumpiaUUID2 = UUID.randomUUID().toString().replace("-", "")

            Thread {
                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    lumpiaUUID1,
                    ImageUtils.getFromDrawable(this, R.drawable.product_lumpia_1)
                )

                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    lumpiaUUID2,
                    ImageUtils.getFromDrawable(this, R.drawable.product_lumpia_2)
                )
            }.start()

            realm.executeTransaction {
                val lumpia = realm.createObject(Product::class.java, UUID.randomUUID().toString().replace("-", ""))
                lumpia.name = "Lumpia"
                lumpia.description = "Lumpia is a spring roll of Chinese origin commonly found in Indonesia and the Philippines. It is a savoury snack made of thin crepe pastry skin called \"lumpia wrapper\" enveloping a mixture of savoury fillings, consists of chopped vegetables or also minced meat."
                lumpia.price = 50.0
                lumpia.imagesUUID = RealmList(lumpiaUUID1, lumpiaUUID2)
                lumpia.category = "Appetizer"
                lumpia.quantity = 100
                lumpia.isAvailable = true
            }
        }

        // check if PancitBihon is already in the menu database
        val existingPancitBihon = realm.where(Product::class.java).equalTo("name", "Pancit Bihon").findFirst()
        if (existingPancitBihon == null) {
            val pancitBihonUUID1 = UUID.randomUUID().toString().replace("-", "")
            val pancitBihonUUID2 = UUID.randomUUID().toString().replace("-", "")

            Thread {
                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    pancitBihonUUID1,
                    ImageUtils.getFromDrawable(this, R.drawable.product_pancitbihon_1)
                )

                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    pancitBihonUUID2,
                    ImageUtils.getFromDrawable(this, R.drawable.product_pancitbihon_2)
                )
            }.start()


            realm.executeTransaction {
                val pancitBihon = realm.createObject(Product::class.java, UUID.randomUUID().toString().replace("-", ""))
                pancitBihon.name = "Pancit Bihon"
                pancitBihon.description = "Pancit bihon or \"Pansit\" is a Chinese-Filipino food dish and one of the variety of Pancit Guisado recipes that I love to cook. This pansit bihon is the commonly requested recipe of my children specially my youngest daughter because she really love pancit or any noodle recipe."
                pancitBihon.price = 150.0
                pancitBihon.imagesUUID = RealmList(pancitBihonUUID1, pancitBihonUUID2)
                pancitBihon.category = "Main Dish"
                pancitBihon.quantity = 100
                pancitBihon.isAvailable = true
            }
        }

        // check if Sisig is already in the menu database
        val existingSisig = realm.where(Product::class.java).equalTo("name", "Sisig").findFirst()
        if (existingSisig == null) {
            val sisigUUID1 = UUID.randomUUID().toString().replace("-", "")
            val sisigUUID2 = UUID.randomUUID().toString().replace("-", "")

            Thread {
                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    sisigUUID1,
                    ImageUtils.getFromDrawable(this, R.drawable.product_sisig_1)
                )

                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    sisigUUID2,
                    ImageUtils.getFromDrawable(this, R.drawable.product_sisig_2)
                )
            }.start()


            realm.executeTransaction {
                val sisig = realm.createObject(Product::class.java, UUID.randomUUID().toString().replace("-", ""))
                sisig.name = "Sisig"
                sisig.description = "Sisig is a Filipino dish made from parts of pig head and chicken liver, usually seasoned with calamansi and chili peppers. Sisig was first mentioned in a Kapampangan dictionary in the 17th Century meaning \"to snack on something sour\"."
                sisig.price = 200.0
                sisig.imagesUUID = RealmList(sisigUUID1, sisigUUID2)
                sisig.category = "Main Dish"
                sisig.quantity = 100
                sisig.isAvailable = true
            }
        }

        // check if Tinola is already in the menu database
        val existingTinola = realm.where(Product::class.java).equalTo("name", "Tinola").findFirst()
        if (existingTinola == null) {
            val tinolaUUID1 = UUID.randomUUID().toString().replace("-", "")
            val tinolaUUID2 = UUID.randomUUID().toString().replace("-", "")

            Thread {
                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    tinolaUUID1,
                    ImageUtils.getFromDrawable(this, R.drawable.product_tinola_1)
                )
                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    tinolaUUID2,
                    ImageUtils.getFromDrawable(this, R.drawable.product_tinola_2)
                )
            }.start()

            realm.executeTransaction {
                val tinola = realm.createObject(Product::class.java, UUID.randomUUID().toString().replace("-", ""))
                tinola.name = "Tinola"
                tinola.description = "Tinola in Tagalog or Visayan, or la uya in Ilocano is a soup-based dish served as an appetizer or main entrée in the Philippines. Traditionally, this dish is cooked with chicken, wedges of green papaya, and leaves of the siling labuyo chili pepper in broth flavored with ginger, onions and fish sauce."
                tinola.price = 150.0
                tinola.imagesUUID = RealmList(tinolaUUID1, tinolaUUID2)
                tinola.category = "Main Dish"
                tinola.quantity = 100
                tinola.isAvailable = true
            }
        }


    }
    override fun onBackPressed() {
        // if the user is in the main container, exit the app
        if (findNavController(R.id.navView).currentDestination?.label == "fragment_main_container")
            if (arrayOf(
                    "fragment_manage_menu",
                    "fragment_manage_user",
                    "fragment_profile",
                    "fragment_cart",
                    "fragment_menu"
                ).contains(findNavController(R.id.mainNavigation).currentDestination?.label))
                moveTaskToBack(true)
            else
                super.onBackPressed()
        else
            super.onBackPressed()

    }


    override fun onResume() {
        super.onResume()

        if (findNavController(R.id.navView).currentDestination?.label != "fragment_main_container" && sharedPrefs.getString("current_user", "") != "") {
            val currentUser = realm.where(User::class.java)
                .equalTo("id", ObjectId(sharedPrefs.getString("current_user", "")))
                .findFirst()
            if (currentUser != null) {
                if (currentUser.isAdministrator){
                    findNavController(R.id.navView).findDestination(R.id.mainContainer)?.route = "admin_navigation"
                } else {
                    findNavController(R.id.navView).findDestination(R.id.mainContainer)?.route = "user_navigation"
                }
                findNavController(R.id.navView).popBackStack()
                findNavController(R.id.navView).navigate(R.id.mainContainer)

            }

        }

    }





}
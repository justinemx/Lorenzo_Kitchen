package com.mooncode.lorenzoskitchen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.text.capitalize
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import org.bson.types.ObjectId
import java.text.SimpleDateFormat
import java.util.Locale

class Profile : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPrefs.edit().remove("selected_userUUID").apply()
        if (sharedPrefs.getString("current_user", "") != "") {
            val currentUser = realm.where(User::class.java)
                .equalTo("id", ObjectId(sharedPrefs.getString("current_user", "")))
                .findFirst()

            if (currentUser != null) {
                if (currentUser.email == "admin") {
                    view.findViewById<LinearLayout>(R.id.viewPersonalProfile).visibility = View.GONE
                    view.findViewById<TextView>(R.id.btnDeleteAccount).visibility = View.GONE
                    view.findViewById<TextView>(R.id.btnUpdateAccount).visibility = View.GONE
                } else {
                    val profileImage = ImageUtils.getFromSharedPref(
                        sharedPrefs,
                        currentUser.profileUUID
                    )
                    view.findViewById<ShapeableImageView>(R.id.imgProfileImage).setImageBitmap(profileImage)
                    view.findViewById<TextView>(R.id.txtFullName).text = "${currentUser.firstName} ${currentUser.middleName} ${currentUser.lastName}"
                    view.findViewById<TextView>(R.id.txtBirthday).text = SimpleDateFormat("MMMM dd, yyyy").format(currentUser.birthday)
                    view.findViewById<TextView>(R.id.txtGender).text = currentUser.getGenderEnum().toString()
                    view.findViewById<TextView>(R.id.txtAddress).text = currentUser.address
                    view.findViewById<TextView>(R.id.txtPhone).text = currentUser.phoneNumber
                }

                view.findViewById<TextView>(R.id.txtAccountType).text = if (currentUser.isAdministrator) "Administrator" else "User"
                view.findViewById<TextView>(R.id.txtEmail).text = currentUser.email
                view.findViewById<TextView>(R.id.txtPassword).text = currentUser.password

            }
        }

        view.findViewById<TextView>(R.id.btnUpdateAccount).setOnClickListener {
            sharedPrefs.edit().putString("selected_userUUID", sharedPrefs.getString("current_user", "")).apply()
            Log.d("selected_userUUID", sharedPrefs.getString("selected_userUUID", "")!!)
            findNavController().navigate(R.id.updateUser,
                null,
                navOptions {
                    anim {
                        enter = R.anim.enter_from_big
                        exit = R.anim.exit_to_small
                        popEnter = R.anim.enter_from_small
                        popExit = R.anim.exit_to_big
                    }
                })
        }

        view.findViewById<TextView>(R.id.btnDeleteAccount).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    realm.executeTransaction { realm ->
                        val currentUser = realm.where(User::class.java)
                            .equalTo("id", ObjectId(sharedPrefs.getString("current_user", "")))
                            .findFirst()
                        sharedPrefs.edit().remove(currentUser!!.profileUUID).apply()
                        currentUser.deleteFromRealm()
                    }
                    sharedPrefs.edit().remove("current_user").apply()
                    requireActivity().findNavController(R.id.navView).navigate(R.id.start_navigation,
                        null,
                        navOptions {
                            anim {
                                enter = R.anim.enter_from_left
                                exit = R.anim.exit_to_right
                                popEnter = R.anim.enter_from_right
                                popExit = R.anim.exit_to_left
                            }
                            popUpTo(R.id.login) {
                                inclusive = true
                            }
                        }
                        , null
                    )
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }

        view.findViewById<TextView>(R.id.btnLogoutAccount).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    sharedPrefs.edit().remove("current_user").apply()
                    requireActivity().findNavController(R.id.navView).navigate(R.id.start_navigation,
                        null,
                        navOptions {
                            anim {
                                enter = R.anim.enter_from_left
                                exit = R.anim.exit_to_right
                                popEnter = R.anim.enter_from_right
                                popExit = R.anim.exit_to_left
                            }
                            popUpTo(R.id.login) {
                                inclusive = true
                            }
                        }
                        , null
                    )
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }
        return view
    }
}
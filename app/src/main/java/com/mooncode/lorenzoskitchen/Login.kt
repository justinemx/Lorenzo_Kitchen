package com.mooncode.lorenzoskitchen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import io.realm.Realm

class Login : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.findViewById<View>(R.id.btnLogin).setOnClickListener {

            val existingUser = realm.where(User::class.java)
                .equalTo("email", view.findViewById<TextInputEditText>(R.id.txtEmail).text.toString())
                .findFirst()

            if (existingUser != null) {
                if (existingUser.password == view.findViewById<TextInputEditText>(R.id.txtPassword).text.toString()) {
                    findNavController().findDestination(R.id.mainContainer)?.route = "logged_navigation"
                    sharedPrefs.edit().putString("current_user", existingUser.id.toString()).apply()

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Welcome to Lorenzo's Kitchen!")
                        .setMessage("Welcome back ${existingUser.firstName}!\nYou are now logged in.")
                        .setCancelable(false)
                        .setPositiveButton("OK") { _, _ ->
                            findNavController().navigate(R.id.mainContainer,
                                null,
                                navOptions {
                                    anim {
                                        enter =  R.anim.enter_from_right
                                        exit = R.anim.exit_to_left
                                        popEnter =  R.anim.enter_from_left
                                        popExit =  R.anim.exit_to_right
                                    }
                                    popUpTo(R.id.login) {
                                        inclusive = true
                                    }
                                }
                                , null
                            )
                        }
                        .show()

                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Error")
                        .setMessage("Invalid password!")
                        .setCancelable(false)
                        .setPositiveButton("OK") { _, _ ->
                            view.findViewById<TextInputEditText>(R.id.txtPassword).setText("")
                            view.findViewById<TextInputEditText>(R.id.txtPassword).requestFocus()
                        }
                        .show()
                }

            }
            else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Error")
                    .setMessage("Email address not found on the database.")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ ->
                        view.findViewById<TextInputEditText>(R.id.txtEmail).setText("")
                        view.findViewById<TextInputEditText>(R.id.txtPassword).setText("")
                        view.findViewById<TextInputEditText>(R.id.txtEmail).requestFocus()
                    }
                    .show()
            }
        }

        view.findViewById<View>(R.id.btnRegister).setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register, null,
                navOptions { anim {
                    enter =  R.anim.enter_from_right
                    exit = R.anim.exit_to_left
                    popEnter =  R.anim.enter_from_left
                    popExit =  R.anim.exit_to_right
                }}
                , null)
        }
        return view
    }

}
package com.mooncode.lorenzoskitchen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.bson.types.ObjectId

class MainContainer : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_container, container, false)

        val currentUser = realm.where(User::class.java)
            .equalTo("id", ObjectId(sharedPrefs.getString("current_user", "")))
            .findFirst()

        if (currentUser?.isAdministrator == true) {
            view.findViewById<BottomNavigationView>(R.id.navigationView).inflateMenu(R.menu.bottom_navigation_admin)
            view.findViewById<FragmentContainerView>(R.id.mainNavigation).post {
                view.findViewById<FragmentContainerView>(R.id.mainNavigation).findNavController()
                    .navigate(
                        R.id.manageMenu,
                        null,
                        navOptions {
                            anim {
                                enter = R.anim.enter_from_right
                                exit = R.anim.exit_to_left
                                popEnter = R.anim.enter_from_left
                                popExit = R.anim.exit_to_right
                            }
                            popUpTo(R.id.menu) {
                                inclusive = true
                            }
                        },
                        null)
            }
        } else {
            view.findViewById<BottomNavigationView>(R.id.navigationView).inflateMenu(R.menu.bottom_navigation_user)
            view.findViewById<FragmentContainerView>(R.id.mainNavigation).post {
                view.findViewById<FragmentContainerView>(R.id.mainNavigation).findNavController()
                    .navigate(
                        R.id.menu,
                        null,
                        navOptions {
                            anim {
                                enter = R.anim.enter_from_right
                                exit = R.anim.exit_to_left
                                popEnter = R.anim.enter_from_left
                                popExit = R.anim.exit_to_right
                            }
                            popUpTo(R.id.menu) {
                                inclusive = true
                            }
                        },
                        null)
            }
        }

        view.findViewById<BottomNavigationView>(R.id.navigationView).setOnNavigationItemSelectedListener { item ->
            // get the previously selected item id
            val previousItem = view.findViewById<BottomNavigationView>(R.id.navigationView).selectedItemId

            val navLeftRight = navOptions {
                anim {
                    enter = R.anim.enter_from_right
                    exit = R.anim.exit_to_left
                    popEnter = R.anim.enter_from_left
                    popExit = R.anim.exit_to_right
                }
            }

            val navRightLeft = navOptions {
                anim {
                    enter = R.anim.enter_from_left
                    exit = R.anim.exit_to_right
                    popEnter = R.anim.enter_from_right
                    popExit = R.anim.exit_to_left
                }
            }

            if (item.itemId == previousItem)
                return@setOnNavigationItemSelectedListener false

            when (item.itemId) {
                R.id.navigation_menu -> {
                    view.findViewById<FragmentContainerView>(R.id.mainNavigation).findNavController()
                        .navigate(
                            R.id.menu,
                            null,
                            if (
                                arrayOf(
                                    R.id.navigation_cart,
                                    R.id.navigation_profile
                                ).contains(previousItem)
                            ) navRightLeft else navLeftRight,
                            null)
                    true
                }
                R.id.navigation_manage_menu -> {

                    Log.d("test", previousItem.toString())
                    view.findViewById<FragmentContainerView>(R.id.mainNavigation)
                        .findNavController()
                        .navigate(
                            R.id.manageMenu,
                            null,
                            if (arrayOf(
                                    R.id.navigation_manage_users,
                                    R.id.navigation_profile
                                ).contains(previousItem)
                            ) navRightLeft else navLeftRight,
                            null
                        )
                    true
                }
                R.id.navigation_manage_users -> {
                    view.findViewById<FragmentContainerView>(R.id.mainNavigation).findNavController()
                        .navigate(
                            R.id.manageUser,
                            null,
                            if (previousItem == R.id.navigation_profile) navRightLeft else navLeftRight,
                            null)
                    true
                }
                R.id.navigation_cart -> {
                    view.findViewById<FragmentContainerView>(R.id.mainNavigation).findNavController()
                        .navigate(
                            R.id.cart,
                            null,
                            if (previousItem == R.id.navigation_profile) navRightLeft else navLeftRight,
                            null)

                    true
                }
                R.id.navigation_profile -> {
                    view.findViewById<FragmentContainerView>(R.id.mainNavigation).findNavController()
                        .navigate(
                            R.id.profile,
                            null,
                            if (
                                arrayOf(
                                    R.id.navigation_menu,
                                    R.id.navigation_cart,
                                    R.id.navigation_manage_users,
                                    R.id.navigation_manage_menu
                                ).contains(previousItem)) navLeftRight else navRightLeft,
                            null)

                    true
                }
                else -> false
            }
        }
        return view
    }
}
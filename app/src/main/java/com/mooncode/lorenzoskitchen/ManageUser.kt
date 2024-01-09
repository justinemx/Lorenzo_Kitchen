package com.mooncode.lorenzoskitchen

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel

class ManageUser : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_user, container, false)

        val userItems = realm.where(User::class.java).findAll()
        val userUUID = sharedPrefs.getString("current_user", "")

        view.findViewById<MaterialButton>(R.id.btnAddUser).setOnClickListener {
             findNavController().navigate(R.id.register_add,
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

        sharedPrefs.edit().remove("selected_userUUID").apply()

        for (user in userItems) {
            // check screen width and adjust constraints
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels

            val cardView = MaterialCardView(requireContext())
            cardView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            cardView.radius = typeDP(30f, requireActivity())
            val cardPad = typeDP(20, requireActivity())
            cardView.cardElevation = typeDP(10f, requireActivity())
            cardView.useCompatPadding = true
            cardView.clipToPadding = false
            cardView.strokeWidth = typeDP(2, requireActivity())

            val constraintLayout = ConstraintLayout(requireContext())
            constraintLayout.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            val image = ImageView(requireContext())
            val imgUUID = "${user.profileUUID}"

            cardView.alpha = 0f
            Thread {
                val imageBitmap = ImageUtils.getFromSharedPref(sharedPrefs, imgUUID)

                requireActivity().runOnUiThread {
                    image.setImageBitmap(imageBitmap)
                    cardView.animate().alpha(1f).setDuration(250).start()
                }
            }.start()


            // size the image in 2:3 ratio
            val imageParams = if (screenWidth < typeDP(550, requireActivity()))
                ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0
                )
            else
                ConstraintLayout.LayoutParams(
                    typeDP(250, requireActivity()),
                    0
                )

            imageParams.dimensionRatio = "1:1"
            imageParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            imageParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            image.layoutParams = imageParams
            image.id = user.id.hashCode()

            constraintLayout.addView(image)

            val userEmail = TextView(requireContext())
            val userEmailParams = ConstraintLayout.LayoutParams(
                0,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            userEmail.layoutParams = userEmailParams
            userEmail.text = user.email
            userEmail.textSize = 20f
            userEmail.ellipsize = TextUtils.TruncateAt.END
            userEmail.maxLines = 1
            userEmail.setTypeface(null, android.graphics.Typeface.BOLD)
            userEmail.id = user.id.hashCode() + 1
            constraintLayout.addView(userEmail)

            val userFullName = TextView(requireContext())
            val userFullNameParams = ConstraintLayout.LayoutParams(
                0,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            userFullName.layoutParams = userFullNameParams
            userFullName.text = "${user.firstName} ${user.middleName} ${user.lastName}"
            userFullName.textSize = 15f
            userFullName.ellipsize = TextUtils.TruncateAt.END
            userFullName.maxLines = 1
            userFullName.id = user.id.hashCode() + 2
            constraintLayout.addView(userFullName)

            val userType = MaterialCardView(requireContext())
            val userTypeParams = ConstraintLayout.LayoutParams(
                0,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            userType.id = user.id.hashCode() + 3
            userType.layoutParams = userTypeParams
            userType.radius = typeDP(20f, requireActivity())
            userType.cardElevation = typeDP(5f, requireActivity())
            userType.useCompatPadding = true
            userType.strokeWidth = 0
            val userColorType = TypedValue()
            requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorTertiary, userColorType, true)
            userType.backgroundTintList = ColorStateList.valueOf(userColorType.data)
            val userColorTypeFore = TypedValue()
            requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorOnTertiary, userColorTypeFore, true)

            val userTypeView = LinearLayout(requireContext())
            val userTypeViewParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            userTypeView.layoutParams = userTypeViewParams
            userTypeView.orientation = LinearLayout.HORIZONTAL
            userTypeView.setPadding(typeDP(12, requireActivity()),typeDP(8, requireActivity()),typeDP(12, requireActivity()),typeDP(8, requireActivity()))
            userType.addView(userTypeView)

            val userTypeIcon = ImageView(requireContext())
            val userTypeIconParams = ConstraintLayout.LayoutParams(
                typeDP(20, requireActivity()),
                typeDP(20, requireActivity())
            )
            userTypeIconParams.marginEnd = typeDP(5, requireActivity())
            userTypeIcon.layoutParams = userTypeIconParams
            userTypeIcon.setImageResource(if (user.isAdministrator) R.drawable.ic_admin else R.drawable.ic_user_square)
            userTypeIcon.setColorFilter(userColorTypeFore.data)

            userTypeView.addView(userTypeIcon)

            val userTypeText = TextView(requireContext())
            val userTypeTextParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            userTypeText.layoutParams = userTypeTextParams
            userTypeText.text = "${ if (user.isAdministrator) "Administrator" else "User"}"
            userTypeText.textSize = 15f
            userTypeText.setTextColor(userColorTypeFore.data)

            userTypeView.addView(userTypeText)

            constraintLayout.addView(userType)

            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)

            val deleteButton = MaterialButton(requireContext())
            deleteButton.layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            deleteButton.icon = requireContext().getDrawable(R.drawable.ic_remove)
            deleteButton.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            deleteButton.text = "Remove"
            deleteButton.setPadding(typeDP(10, requireActivity()),0, typeDP(10, requireActivity()),0)
            deleteButton.id = user.id.hashCode() + 4
            deleteButton.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user?")
                    .setNegativeButton("Cancel") { _, _ -> }
                    .setPositiveButton("Delete") { _, _ ->
                        realm.executeTransaction {
                            user.deleteFromRealm()
                        }
                        sharedPrefs.edit().remove(imgUUID).apply()
                        view.findViewById<LinearLayout>(R.id.listUsers).removeView(cardView)
                    }
                    .show()
            }
            deleteButton.backgroundTintList = ColorStateList.valueOf(typedValue.data)
            if (user.email == "admin" || user.getId() == userUUID) {
                deleteButton.isClickable = false
                deleteButton.alpha = 0.5f
            }
            constraintLayout.addView(deleteButton)


            val updateButton = MaterialButton(requireContext())
            updateButton.layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            updateButton.icon = requireContext().getDrawable(R.drawable.ic_edit)
            updateButton.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            updateButton.text = "Update"
            updateButton.setPadding(typeDP(10, requireActivity()),0, typeDP(10, requireActivity()),0)
            updateButton.id = user.id.hashCode() + 5
            updateButton.setOnClickListener {
                sharedPrefs.edit().putString("selected_userUUID", user.getId()).apply()
                findNavController().navigate(
                    R.id.updateUser,
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
            updateButton.backgroundTintList = ColorStateList.valueOf(typedValue.data)
            if (user.email == "admin" || user.getId() == userUUID) {
                updateButton.isClickable = false
                updateButton.alpha = 0.5f
            }
            constraintLayout.addView(updateButton)


            val set = ConstraintSet()
            set.clone(constraintLayout)

            if (screenWidth < typeDP(550, requireActivity())) {
                // If the screen width is small, place the image on top
                set.connect(image.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                set.connect(image.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                set.connect(image.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                set.connect(image.id, ConstraintSet.BOTTOM, userEmail.id, ConstraintSet.TOP)

                set.connect(userEmail.id, ConstraintSet.TOP, image.id, ConstraintSet.BOTTOM, cardPad)
                set.connect(userEmail.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, cardPad)

                set.connect(userFullName.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, cardPad)

                set.connect(deleteButton.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, cardPad)
                set.connect(deleteButton.id, ConstraintSet.TOP, userFullName.id, ConstraintSet.BOTTOM, cardPad)

                set.connect(updateButton.id, ConstraintSet.START, deleteButton.id, ConstraintSet.END, cardPad)
                set.connect(updateButton.id, ConstraintSet.TOP, userFullName.id, ConstraintSet.BOTTOM, cardPad)

            } else {
                // Otherwise, place the image on the left
                set.connect(image.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)

                set.connect(userEmail.id, ConstraintSet.START, image.id, ConstraintSet.END, cardPad)
                set.connect(userEmail.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, cardPad)

                set.connect(userFullName.id, ConstraintSet.START, image.id, ConstraintSet.END, cardPad)

                set.connect(deleteButton.id, ConstraintSet.START, image.id, ConstraintSet.END, cardPad)
            set.connect(updateButton.id, ConstraintSet.START, deleteButton.id, ConstraintSet.END, cardPad)

        }
        set.connect(userEmail.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, cardPad)

        set.connect(deleteButton.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, cardPad)
        set.connect(updateButton.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, cardPad)

        set.connect(userFullName.id, ConstraintSet.TOP, userEmail.id, ConstraintSet.BOTTOM)
        set.connect(userFullName.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, cardPad)

        set.connect(userType.id, ConstraintSet.START, image.id, ConstraintSet.START, typeDP(5, requireActivity()))
        set.connect(userType.id, ConstraintSet.BOTTOM, image.id, ConstraintSet.BOTTOM, typeDP(5, requireActivity()))

        set.applyTo(constraintLayout)

        cardView.addView(constraintLayout)
        view.findViewById<LinearLayout>(R.id.listUsers).addView(cardView)

        }
        return view
    }
}
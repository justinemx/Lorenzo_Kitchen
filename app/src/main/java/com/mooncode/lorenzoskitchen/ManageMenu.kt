package com.mooncode.lorenzoskitchen

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ManageMenu : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_menu, container, false)

        val menuItems = realm.where(Product::class.java).findAll()

        view.findViewById<MaterialButton>(R.id.btnAddUser).setOnClickListener {
            findNavController().navigate(
                R.id.add_menu,
                null,
                navOptions {
                    anim {
                        enter = R.anim.enter_from_big
                        exit = R.anim.exit_to_small
                        popEnter = R.anim.enter_from_small
                        popExit = R.anim.exit_to_big
                    }
                },
                null)

        }

        for (item in menuItems) {
            // Check screen width and adjust constraints accordingly
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

            val imgUUID = "${item.imagesUUID[0]}"

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

            imageParams.dimensionRatio = "3:2"
            imageParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            imageParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            image.layoutParams = imageParams
            image.id = item.id.hashCode()

            constraintLayout.addView(image)



            // delete button
            val deleteButton = MaterialButton(requireContext())
            deleteButton.layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            deleteButton.icon = requireContext().getDrawable(R.drawable.ic_remove)
            deleteButton.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            deleteButton.text = "Remove"
            deleteButton.setPadding(typeDP(10, requireActivity()),0, typeDP(10, requireActivity()),0)
            deleteButton.id = item.id.hashCode() + 4
            deleteButton.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Menu Item")
                    .setMessage("Are you sure you want to delete this menu item?")
                    .setNegativeButton("Cancel") { _, _ -> }
                    .setPositiveButton("Delete") { _, _ ->
                        for (itemUUID in item.imagesUUID) {
                            sharedPrefs.edit().remove(itemUUID).apply()
                        }
                        realm.executeTransaction {
                            item.deleteFromRealm()
                        }
                        view.findViewById<LinearLayout>(R.id.listMenus).removeView(cardView)
                    }
                    .show()
            }

            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
            deleteButton.backgroundTintList = ColorStateList.valueOf(typedValue.data)


            constraintLayout.addView(deleteButton)


            val menuTitle = TextView(requireContext())
            val menuTitleParams = ConstraintLayout.LayoutParams(
                0,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            menuTitle.layoutParams = menuTitleParams
            menuTitle.text = item.name.uppercase()
            menuTitle.textSize = 20f
            menuTitle.setTypeface(null, android.graphics.Typeface.BOLD)
            menuTitle.id = item.id.hashCode() + 1
            constraintLayout.addView(menuTitle)

            val menuDescription = TextView(requireContext())
            val menuDescriptionParams = ConstraintLayout.LayoutParams(
                0,
                if (screenWidth < typeDP(550, requireActivity())) ViewGroup.LayoutParams.WRAP_CONTENT else 0
            )
            menuDescription.layoutParams = menuDescriptionParams
            menuDescription.text = item.description
            menuDescription.textSize = 15f
            menuDescription.ellipsize = TextUtils.TruncateAt.END
            menuDescription.post {
                val layout = menuDescription.layout
                if (layout != null) {
                    val height = menuDescription.height
                    val lineHeight = menuDescription.lineHeight

                    menuDescription.maxLines = height / lineHeight
                }
            }
            menuDescription.id = item.id.hashCode() + 2
            constraintLayout.addView(menuDescription)

            val menuPrice = TextView(requireContext())
            val menuPriceParams = ConstraintLayout.LayoutParams(
                0,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            menuPrice.layoutParams = menuPriceParams
            menuPrice.text = "₱ ${item.price}"
            menuPrice.textSize = 15f
            menuPrice.id = item.id.hashCode() + 3
            constraintLayout.addView(menuPrice)

            val set = ConstraintSet()
            set.clone(constraintLayout)

            if (screenWidth < typeDP(550, requireActivity())) {
                // If the screen width is small, place the image on top
                set.connect(image.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                set.connect(image.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                set.connect(image.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                set.connect(image.id, ConstraintSet.BOTTOM, menuTitle.id, ConstraintSet.TOP)

                set.connect(menuTitle.id, ConstraintSet.TOP, image.id, ConstraintSet.BOTTOM, cardPad)
                set.connect(menuTitle.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, cardPad)

                set.connect(menuDescription.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, cardPad)

                set.connect(menuPrice.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, cardPad)
                set.connect(menuPrice.id, ConstraintSet.TOP, menuDescription.id, ConstraintSet.BOTTOM, typeDP(10, requireActivity()))

            } else {
                // Otherwise, place the image on the left
                set.connect(image.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)

                set.connect(menuTitle.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, cardPad)
                set.connect(menuTitle.id, ConstraintSet.START, image.id, ConstraintSet.END, cardPad)

                set.connect(menuDescription.id, ConstraintSet.START, image.id, ConstraintSet.END, cardPad)

                set.connect(menuPrice.id, ConstraintSet.START, image.id, ConstraintSet.END, cardPad)
                set.connect(menuPrice.id, ConstraintSet.TOP, menuDescription.id, ConstraintSet.BOTTOM)
            }

            set.connect(deleteButton.id, ConstraintSet.START, image.id, ConstraintSet.START, typeDP(15, requireActivity()))
            set.connect(deleteButton.id, ConstraintSet.TOP, image.id, ConstraintSet.TOP, typeDP(10, requireActivity()))

            set.connect(menuDescription.id, ConstraintSet.TOP, menuTitle.id, ConstraintSet.BOTTOM)
            set.connect(menuDescription.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, cardPad)
            set.connect(menuDescription.id, ConstraintSet.BOTTOM, menuPrice.id, ConstraintSet.TOP)

            set.connect(menuPrice.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, cardPad)
            set.connect(menuPrice.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, cardPad)

            set.applyTo(constraintLayout)

            cardView.addView(constraintLayout)
            view.findViewById<LinearLayout>(R.id.listMenus).addView(cardView)
        }
        return view
    }
}
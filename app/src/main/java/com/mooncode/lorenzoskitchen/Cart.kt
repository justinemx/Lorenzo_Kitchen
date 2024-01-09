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
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.bson.types.ObjectId

class Cart : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        val currentUser = realm.where(User::class.java)
            .equalTo("id", ObjectId(sharedPrefs.getString("current_user", "")))
            .findFirst()

        val cartItems = currentUser?.cart!!

        view.findViewById<MaterialButton>(R.id.btnCheckout).setOnClickListener {
            if (cartItems.size > 0) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Checkout")
                    .setMessage("Are you sure you want to checkout?")
                    .setNegativeButton("Cancel") { _, _ -> }
                    .setPositiveButton("Checkout") { _, _ ->
                        // function soon to be implemented
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Function not yet implemented")
                            .setMessage("This function is not yet implemented :(")
                            .setPositiveButton("OK") { _, _ -> }
                            .show()
                    }
                    .show()

            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Empty Cart")
                    .setMessage("Your cart is empty. Please add items to your cart before checking out.")
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
            }
        }

        Log.d("cart", cartItems.size.toString())

        for (item in cartItems) {
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

            val imgUUID = "${item.imageUUID}"


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
                    .setMessage("Are you sure you want to delete this cart item?")
                    .setNegativeButton("Cancel") { _, _ -> }
                    .setPositiveButton("Delete") { _, _ ->
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


            val cartTitle = TextView(requireContext())
            val cartTitleParams = ConstraintLayout.LayoutParams(
                0,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            cartTitle.layoutParams = cartTitleParams
            cartTitle.text = item.name.uppercase()
            cartTitle.textSize = 20f
            cartTitle.setTypeface(null, android.graphics.Typeface.BOLD)
            cartTitle.id = item.id.hashCode() + 1
            constraintLayout.addView(cartTitle)

            
            val cartPrice = TextView(requireContext())
            val cartPriceParams = ConstraintLayout.LayoutParams(
                0,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            cartPrice.layoutParams = cartPriceParams
            cartPrice.text = "₱ ${item.price}"
            cartPrice.textSize = 15f
            cartPrice.id = item.id.hashCode() + 2
            constraintLayout.addView(cartPrice)


            val cartQuantity = TextView(requireContext())
            val cartQuantityParams = ConstraintLayout.LayoutParams(
                0,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            cartQuantity.layoutParams = cartQuantityParams
            cartQuantity.text = "× ${item.quantity}"
            cartQuantity.textSize = 15f
            cartQuantity.id = item.id.hashCode() + 3
            cartQuantity.setTypeface(null, android.graphics.Typeface.ITALIC)

            constraintLayout.addView(cartQuantity)

            val cartTotal = TextView(requireContext())
            val cartTotalParams = ConstraintLayout.LayoutParams(
                0,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            cartTotal.layoutParams = cartTotalParams
            cartTotal.text = "TOTAL: ₱ ${item.price * item.quantity}"
            cartTotal.textSize = 15f
            cartTotal.id = item.id.hashCode() + 5
            cartTotal.setTypeface(null, android.graphics.Typeface.BOLD)

            constraintLayout.addView(cartTotal)

            val set = ConstraintSet()
            set.clone(constraintLayout)


            if (screenWidth < typeDP(550, requireActivity())) {
                // If the screen width is small, place the image on top
                set.connect(image.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                set.connect(image.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                set.connect(image.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                set.connect(image.id, ConstraintSet.BOTTOM, cartTitle.id, ConstraintSet.TOP)

                set.connect(cartTitle.id, ConstraintSet.TOP, image.id, ConstraintSet.BOTTOM, cardPad)
                set.connect(cartTitle.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, cardPad)

                set.connect(cartPrice.id, ConstraintSet.START,  ConstraintSet.PARENT_ID, ConstraintSet.START, cardPad)
                set.connect(cartPrice.id, ConstraintSet.TOP, cartTitle.id, ConstraintSet.BOTTOM, typeDP(10, requireActivity()))

                set.connect(cartQuantity.id, ConstraintSet.START, cartPrice.id, ConstraintSet.END, cardPad)
                set.connect(cartQuantity.id, ConstraintSet.TOP, cartTitle.id, ConstraintSet.BOTTOM, typeDP(10, requireActivity()))

                set.connect(cartTotal.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, cardPad)
                set.connect(cartTotal.id, ConstraintSet.TOP, cartPrice.id, ConstraintSet.BOTTOM, typeDP(1, requireActivity()))
                set.connect(cartTotal.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, cardPad)

            } else {
                // Otherwise, place the image on the left
                set.connect(image.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)

                set.connect(cartTitle.id, ConstraintSet.START, image.id, ConstraintSet.END, cardPad)
                set.connect(cartTitle.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, cardPad)

                set.connect(cartPrice.id, ConstraintSet.START, image.id, ConstraintSet.END, cardPad)
                set.connect(cartQuantity.id, ConstraintSet.START, cartPrice.id, ConstraintSet.END, typeDP(8, requireActivity()))
                set.connect(cartTotal.id, ConstraintSet.START, image.id, ConstraintSet.END, cardPad)

                set.connect(cartPrice.id, ConstraintSet.BOTTOM, cartTotal.id, ConstraintSet.BOTTOM, cardPad)
                set.connect(cartQuantity.id, ConstraintSet.BOTTOM, cartTotal.id, ConstraintSet.BOTTOM, cardPad)
                set.connect(cartTotal.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, cardPad)

            }


            set.connect(deleteButton.id, ConstraintSet.START, image.id, ConstraintSet.START, typeDP(15, requireActivity()))
            set.connect(deleteButton.id, ConstraintSet.TOP, image.id, ConstraintSet.TOP, typeDP(10, requireActivity()))



            set.applyTo(constraintLayout)

            cardView.addView(constraintLayout)
            view.findViewById<LinearLayout>(R.id.listMenus).addView(cardView)




        }


        return view


    }
}
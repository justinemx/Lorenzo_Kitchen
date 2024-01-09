package com.mooncode.lorenzoskitchen

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import java.util.UUID

class AddMenu : Fragment() {

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    val selectedImageUri = data.data

                    Thread{
                        val selectedBitmap = ImageUtils
                            .resize(
                                ImageUtils
                                    .cropToRatio(
                                        uriToBitmap(selectedImageUri!!)!!,
                                        3.0,
                                        2.0),
                                1000,
                                667)

                        imageBase64[UUID.randomUUID().toString().replace("-", "")] = (ImageUtils.toBase64(selectedBitmap))

                        activity?.runOnUiThread {
                            view?.findViewById<ViewGroup>(R.id.llImageContainer)?.removeAllViews()

                            for (image in imageBase64) {
                                val imageCard = MaterialCardView(requireContext())
                                val imageCardParams = MarginLayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                imageCardParams.setMargins(0, 0, 0, typeDP(10, requireActivity()))
                                imageCard.layoutParams = imageCardParams
                                imageCard.radius = 10f
                                imageCard.strokeWidth = 0
                                imageCard.cardElevation = 8f

                                val imageLayout = ConstraintLayout(requireContext())
                                imageLayout.layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )


                                val imageCardImage = ImageView(requireContext())
                                imageCardImage.layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                imageCardImage.id = View.generateViewId()
                                imageCardImage.scaleType = ImageView.ScaleType.CENTER_CROP
                                imageCardImage.adjustViewBounds = true
                                imageCardImage.setImageBitmap(ImageUtils.fromBase64(image.value))
                                imageLayout.addView(imageCardImage)


                                val imageRemoveBtn = MaterialButton(requireContext())
                                imageRemoveBtn.layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                imageRemoveBtn.id = View.generateViewId()
                                imageRemoveBtn.icon = resources.getDrawable(R.drawable.ic_remove, null)
                                imageRemoveBtn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                                imageRemoveBtn.setPadding(typeDP(10, requireActivity()),0, typeDP(10, requireActivity()),0)
                                imageRemoveBtn.text = "Remove"
                                imageRemoveBtn.setOnClickListener {
                                    imageBase64.remove(image.key)
                                    view?.findViewById<ViewGroup>(R.id.llImageContainer)?.removeView(imageCard)
                                }
                                imageLayout.addView(imageRemoveBtn)


                                val set = ConstraintSet()
                                set.clone(imageLayout)

                                set.connect(imageCardImage.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                                set.connect(imageCardImage.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                                set.connect(imageCardImage.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

                                set.connect(imageRemoveBtn.id, ConstraintSet.TOP, imageCardImage.id, ConstraintSet.TOP, typeDP(7, requireActivity()))
                                set.connect(imageRemoveBtn.id, ConstraintSet.START, imageCardImage.id, ConstraintSet.START, typeDP(12, requireActivity()))

                                set.applyTo(imageLayout)


                                imageCard.addView(imageLayout)

                                view?.findViewById<LinearLayout>(R.id.llImageContainer)?.addView(imageCard)
                            }
                        }
                    }.start()
                }
            }
        }

    private var imageBase64: HashMap<String, String> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_menu, container, false)

        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<View>(R.id.btnAddImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            imagePicker.launch(intent)
        }

        view.findViewById<MaterialButton>(R.id.btnRegister).setOnClickListener {
            if (view.findViewById<TextInputEditText>(R.id.txtFoodName).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtCategory).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtDescription).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtPrice).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtQuantity).text.isNullOrBlank() ||
                view.findViewById<AutoCompleteTextView>(R.id.selIsAvailable).text.isNullOrBlank() ||
                imageBase64.isEmpty()
            ) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Registration Failed")
                    .setMessage("Please fill out all the fields.")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
                return@setOnClickListener
            }

            // add to database
            realm.executeTransaction {
                val menu = realm.createObject(
                    Product::class.java,
                    UUID.randomUUID().toString().replace("-", ""))

                menu.name = view.findViewById<TextInputEditText>(R.id.txtFoodName).text.toString()
                menu.category = view.findViewById<TextInputEditText>(R.id.txtCategory).text.toString()
                menu.description = view.findViewById<TextInputEditText>(R.id.txtDescription).text.toString()
                menu.quantity = view.findViewById<TextInputEditText>(R.id.txtQuantity).text.toString().toInt()
                menu.price = view.findViewById<TextInputEditText>(R.id.txtPrice).text.toString().toDouble()
                menu.isAvailable = view.findViewById<AutoCompleteTextView>(R.id.selIsAvailable).text.toString() == "Yes"

                for (image in imageBase64) {
                    menu.imagesUUID.add(image.key)
                    ImageUtils.saveToSharedPref(sharedPrefs, image.key, image.value)
                }

            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Registration Successful")
                .setMessage("You have successfully registered.")
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    findNavController().popBackStack()
                }
                .show()
        }

        view.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancel Registration")
                .setMessage("Are you sure you want to cancel registration?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    findNavController().popBackStack()
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }


        view.findViewById<AutoCompleteTextView>(R.id.selIsAvailable)
            .setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    arrayOf("True", "False")
                )
            )

        return view
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
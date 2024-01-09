package com.mooncode.lorenzoskitchen

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import io.realm.Realm
import org.bson.types.ObjectId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class Register : Fragment() {

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    val selectedImageUri = data.data

                    Thread {
                        val selectedBitmap = ImageUtils
                            .resize(
                                ImageUtils
                                    .cropToRatio(
                                        uriToBitmap(selectedImageUri!!)!!,
                                        1.0,
                                        1.0),
                                1000,
                                1000)

                        imageBase64 = ImageUtils.toBase64(selectedBitmap)

                        activity?.runOnUiThread {
                            imageView?.setImageBitmap(selectedBitmap)
                        }
                    }.start()

                }
            }
        }
    private var imageView: ShapeableImageView? = null
    private var imageBase64 = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        var birthday: Long? = null

        imageView = view.findViewById(R.id.imgProfileImage)

        view.findViewById<View>(R.id.btnRegister).setOnClickListener {

            if (view.findViewById<TextInputEditText>(R.id.txtFirstName).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtMiddleInitial).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtLastName).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtAddress).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtPhone).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtEmail).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtPassword).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtConfPassword).text.isNullOrBlank() ||
                view.findViewById<AutoCompleteTextView>(R.id.selGender).text.isNullOrBlank() ||
                view.findViewById<TextInputEditText>(R.id.txtBirthday).text.isNullOrBlank() ||
                view.findViewById<AutoCompleteTextView>(R.id.selAccountType).text.isNullOrBlank() ||
                birthday == null ||
                imageBase64 == ""
            ) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Registration Failed")
                    .setMessage("Please fill out all the fields.")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .show()
                return@setOnClickListener
            }

            realm.where(User::class.java)
                .equalTo("email", view.findViewById<TextInputEditText>(R.id.txtEmail).text.toString())
                .findFirst()?.let {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Registration Failed")
                        .setMessage("The email address you have entered is already registered.")
                        .setCancelable(false)
                        .setPositiveButton("OK") { _, _ ->
                            view.findViewById<TextInputEditText>(R.id.txtEmail).setText("")
                            view.findViewById<TextInputEditText>(R.id.txtEmail).requestFocus()
                        }
                        .show()
                    return@setOnClickListener
                }

            realm.executeTransaction {
                val user = realm.createObject(User::class.java, UUID.randomUUID().toString().replace("-", ""))
                val profileUUID = UUID.randomUUID().toString().replace("-", "")

                ImageUtils.saveToSharedPref(
                    sharedPrefs,
                    profileUUID,
                    imageBase64
                )

                user.firstName = view.findViewById<TextInputEditText>(R.id.txtFirstName).text.toString()
                user.middleName = view.findViewById<TextInputEditText>(R.id.txtMiddleInitial).text.toString()
                user.lastName = view.findViewById<TextInputEditText>(R.id.txtLastName).text.toString()
                user.profileUUID = profileUUID
                user.birthday = birthday ?: 0
                user.setGenderEnum(Gender.fromString(view.findViewById<AutoCompleteTextView>(R.id.selGender).text.toString())!!)
                user.address = view.findViewById<TextInputEditText>(R.id.txtAddress).text.toString()
                user.phoneNumber = view.findViewById<TextInputEditText>(R.id.txtPhone).text.toString()
                user.email = view.findViewById<TextInputEditText>(R.id.txtEmail).text.toString()
                user.password = view.findViewById<TextInputEditText>(R.id.txtPassword).text.toString()
                user.isAdministrator = view.findViewById<AutoCompleteTextView>(R.id.selAccountType).text.toString() == "Administrator"
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Registration Successful")
                .setMessage("You have successfully registered your account. You can now login to your account.")
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    findNavController().popBackStack()
                }
                .show()
        }

        view.findViewById<View>(R.id.btnCancel).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancel Registration")
                .setMessage("Are you sure you want to cancel your registration?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    findNavController().popBackStack()
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }

        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<View>(R.id.btnProfileImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            imagePicker.launch(intent)
        }

        view.findViewById<View>(R.id.txtBirthday).setOnClickListener {
            it as TextInputEditText

            val constraint = CalendarConstraints.Builder()
            val calendar = Calendar.getInstance()

            calendar.add(Calendar.YEAR, -13)
            constraint.setEnd(calendar.timeInMillis)

            if (birthday != null)
                constraint.setOpenAt(if (it.text.isNullOrBlank()) calendar.timeInMillis else birthday!!)
            else
                constraint.setOpenAt(calendar.timeInMillis)

            calendar.add(Calendar.YEAR, -137)
            constraint.setStart(calendar.timeInMillis)

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraint.build())
                .setTitleText("Select your birthday")


            if (!it.text.isNullOrBlank())
                datePicker.setSelection(birthday)

            val datePickerBuilder = datePicker.build()

            datePickerBuilder.show(requireActivity().supportFragmentManager, "DatePicker")
            datePickerBuilder.addOnPositiveButtonClickListener {it2 ->
                val date = Date(it2)
                val cal = Calendar.getInstance()
                cal.time = date

                birthday = cal.timeInMillis
                it.setText(SimpleDateFormat("MMMM d, yyyy").format(date))
            }
        }

        view.findViewById<AutoCompleteTextView>(R.id.selGender)
            .setAdapter( ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Male", "Female", "Other")))
        view.findViewById<AutoCompleteTextView>(R.id.selAccountType)
            .setAdapter( ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, arrayOf("Administrator", "User")))
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
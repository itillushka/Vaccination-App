package com.example.vaccinationmanagerapp.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.vaccinationmanagerapp.LoginActivity
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.users.Users
import com.example.vaccinationmanagerapp.mySQLDatabase.users.UsersDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.users.gender
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    suspend fun fetchUserData() {
        withContext(Dispatchers.IO) {
            // Getting connection using DBConnection class
            val connection = DBconnection.getConnection()
            val dbQueries = UsersDBQueries(connection)

            val firebaseAuth = FirebaseAuth.getInstance()
            val firebaseUser = firebaseAuth.currentUser
            var firebaseUserId: String = ""

            if (firebaseUser != null) {
                firebaseUserId = firebaseUser.uid
            }

            // Fetching the user data from the database
            val userData = dbQueries.fetchUserData(firebaseUserId)

            connection.close() // Closing the database connection

            withContext(Dispatchers.Main) {
                val textViewPhoneProfile = view?.findViewById<TextView>(R.id.textViewPhoneProfile)
                val textViewAgeProfile = view?.findViewById<TextView>(R.id.textViewAgeProfile)
                val textViewGenderProfile = view?.findViewById<TextView>(R.id.textViewGenderProfile)

                textViewPhoneProfile?.text = userData[0]
                textViewAgeProfile?.text = userData[1]
                textViewGenderProfile?.text = userData[2]
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageViewSupport = view.findViewById<ImageView>(R.id.imageViewSupport)
        val imageViewChangePassword = view.findViewById<ImageView>(R.id.imageViewPassword)
        val imageViewAddInfo = view.findViewById<ImageView>(R.id.imageViewPhone)

        imageViewSupport.setOnClickListener {
            val dialog1 = SupportDialogFragment.newInstance()
            dialog1.show(childFragmentManager, "SupportDialogFragment")
        }

        imageViewChangePassword.setOnClickListener {
            val dialog2 = ChangePasswordDialogFragment.newInstance()
            dialog2.show(childFragmentManager, "ChangePasswordDialogFragment")
        }

        imageViewAddInfo.setOnClickListener {
            val dialog3 = AddMoreInfoDialogFragment.newInstance()
            dialog3.show(childFragmentManager, "AddMoreInfoDialogFragment")
        }

        val textViewLogout = view.findViewById<TextView>(R.id.textViewStatic14)
        val imageViewLogout = view.findViewById<ImageView>(R.id.imageView11)

        val clickListener = View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        textViewLogout.setOnClickListener(clickListener)
        imageViewLogout.setOnClickListener(clickListener)

        lifecycleScope.launch { fetchUserData() }



    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
            }
    }
}

class SupportDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogBlur)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.support_popup_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the close button from the popup view
        val closeButton = view.findViewById<Button>(R.id.buttonCloseSUpport)

        // Set an OnClickListener for the close button
        closeButton.setOnClickListener {
            // Dismiss the popup window when the close button is clicked
            dismiss()
        }
    }

    companion object {
        fun newInstance() = SupportDialogFragment()
    }
}

class ChangePasswordDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogBlur)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.change_password_popup_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the close button from the popup view
        val closeButton = view.findViewById<Button>(R.id.buttonClosePasswordPopup)

        // Set an OnClickListener for the close button
        closeButton.setOnClickListener {
            // Dismiss the popup window when the close button is clicked
            dismiss()
        }
    }

    companion object {
        fun newInstance() = ChangePasswordDialogFragment()
    }
}

class AddMoreInfoDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogBlur)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_more_info_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the spinner from the popup view
        val spinnerGender = view.findViewById<Spinner>(R.id.spinnerGender)

        // Get the fields from the popup view
        val editTextAddInfoPhone = view.findViewById<EditText>(R.id.editTextAddInfoPhone)
        val editTextAddInfoAge = view.findViewById<EditText>(R.id.editTextAddInfoAge)
        // Get the confirm button from the popup view
        val buttonConfirm = view.findViewById<Button>(R.id.buttonConfirmAddInfoPopup)
        val buttonClose = view.findViewById<Button>(R.id.buttonCloseAddInfoPopup)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerGender.adapter = adapter
        }
        spinnerGender.setSelection(0)


        // Set an OnClickListener for the confirm button
        buttonConfirm.setOnClickListener {
            // Reset the backgrounds
            editTextAddInfoPhone.setBackgroundResource(R.drawable.fancy_edittext)
            editTextAddInfoAge.setBackgroundResource(R.drawable.fancy_edittext)
            spinnerGender.setBackgroundColor(R.drawable.fancy_edittext)

            // Check if any of the fields are empty
            if (editTextAddInfoPhone.text.toString().isEmpty()) {
                editTextAddInfoPhone.setBackgroundResource(R.drawable.fancy_red_edittext)
            }
            if (editTextAddInfoAge.text.toString().isEmpty()) {
                editTextAddInfoAge.setBackgroundResource(R.drawable.fancy_red_edittext)
            }
            if (spinnerGender.selectedItemPosition == 0) {
                spinnerGender.setBackgroundResource(R.drawable.fancy_red_edittext)
            }
            // If all fields are filled, insert the user info into the database
            if (editTextAddInfoPhone.text.toString()
                    .isNotEmpty() && editTextAddInfoAge.text.toString()
                    .isNotEmpty() && spinnerGender.selectedItemPosition != 0
            ) {
                runBlocking { launch(Dispatchers.IO) {
                    updateAddInfoUser(
                        editTextAddInfoPhone.text.toString(),
                        editTextAddInfoAge.text.toString(),
                        spinnerGender.selectedItem.toString()

                    )
                }
                }
            }

            dismiss()
        }

        buttonClose.setOnClickListener {
            // Dismiss the popup window when the close button is clicked
            dismiss()
        }

    }
    suspend fun updateAddInfoUser(phone_number : String, age: String, userGender: String){
        withContext(Dispatchers.IO) {
            // Getting connection using DBConnection class
            val connection = DBconnection.getConnection()
            val dbQueries = UsersDBQueries(connection)

            val firebaseAuth = FirebaseAuth.getInstance()
            val firebaseUser = firebaseAuth.currentUser
            var firebaseUserId : String = ""

            if (firebaseUser != null) {
                firebaseUserId = firebaseUser.uid
            }

            val userGender = gender.valueOf(userGender)
            // Inserting the new user into the database
            dbQueries.updateUser(phone_number, age.toInt(), userGender, firebaseUserId)

            connection.close() // Closing the database connection
        }
    }

    companion object {
        fun newInstance() = AddMoreInfoDialogFragment()
    }
}


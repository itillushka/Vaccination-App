package com.example.vaccinationmanagerapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.vaccinationmanagerapp.LoginActivity
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.users.UsersDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.users.gender
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.EmailAuthProvider

/**
 * A [Fragment] subclass that represents the user's profile.
 * It fetches the user data from the database and sets up the UI.
 * It also provides options to change password, add more information, and log out.
 */
class ProfileFragment : Fragment() {
    /**
     * Fetches the user data from the database and sets up the UI.
     * It gets the user's Firebase user id, fetches the corresponding user data from the database,
     * and updates the UI with the fetched data.
     */
    @SuppressLint("SetTextI18n")
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
                val textViewUsername = view?.findViewById<TextView>(R.id.textViewUserName)

                if (userData[2] != "0") {
                    textViewUsername?.text = userData[0]
                    textViewPhoneProfile?.text = userData[1]
                    textViewAgeProfile?.text = userData[2]
                    textViewGenderProfile?.text = userData[3]
                }else{
                    textViewUsername?.text = userData[0]
                    textViewPhoneProfile?.text = "No Data"
                    textViewAgeProfile?.text = "No Data"
                    textViewGenderProfile?.text = "No Data"
            }}
        }
    }


    /**
     * Inflates the layout for this fragment and returns the inflated View.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    /**
     * Sets up the UI elements and their OnClickListeners after the view is created.
     * It sets up the notification switch, support, change password, add info, and logout options.
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageViewSupport = view.findViewById<ImageView>(R.id.imageViewSupport)
        val imageViewChangePassword = view.findViewById<ImageView>(R.id.imageViewPassword)
        val imageViewAddInfo = view.findViewById<ImageView>(R.id.imageViewPhone)

        val notificationSwitch = view.findViewById<Switch>(R.id.switchNotification)
        // Initialize the NotificationManagerCompat
        val notificationManager = NotificationManagerCompat.from(requireContext())

        notificationSwitch.isChecked = true

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // If notifications are disabled, navigate to app settings
                if (!notificationManager.areNotificationsEnabled()) {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    }
                    startActivity(intent)
                }
            } else {
                // If notifications are enabled, navigate to app settings
                if (notificationManager.areNotificationsEnabled()) {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    }
                    startActivity(intent)
                }
            }
        }

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
}
/**
 * A [DialogFragment] subclass that represents the support dialog.
 * It provides a UI for the user to get support.
 */
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
/**
 * A [DialogFragment] subclass that represents the change password dialog.
 * It provides a UI for the user to change their password.
 */
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

        val editTextOldPassword = view.findViewById<EditText>(R.id.editTextChangeOldPassword)
        val editTextNewPassword = view.findViewById<EditText>(R.id.editTextChangeNewPassword)
        val buttonConfirm = view.findViewById<Button>(R.id.buttonConfirmPasswordPopup)
        val closeButton = view.findViewById<Button>(R.id.buttonClosePasswordPopup)

        closeButton.setOnClickListener {
            dismiss()
        }

        buttonConfirm.setOnClickListener {
            val oldPassword = editTextOldPassword.text.toString()
            val newPassword = editTextNewPassword.text.toString()

            val user = FirebaseAuth.getInstance().currentUser
            val credential = EmailAuthProvider.getCredential(user!!.email!!, oldPassword)

            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                            dismiss()
                        } else {
                            Toast.makeText(
                                context,
                                "Error password not updated",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Error auth failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        fun newInstance() = ChangePasswordDialogFragment()
    }
}

/**
 * A [DialogFragment] subclass that represents the add more info dialog.
 * It provides a UI for the user to add more information to their profile.
 */
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


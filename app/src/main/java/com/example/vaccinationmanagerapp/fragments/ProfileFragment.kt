package com.example.vaccinationmanagerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.vaccinationmanagerapp.R

class ProfileFragment : Fragment() {

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
        fun newInstance() = SupportDialogFragment()
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

        // Get the close button from the popup view
        val closeButton = view.findViewById<Button>(R.id.buttonCloseAddInfoPopup)

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
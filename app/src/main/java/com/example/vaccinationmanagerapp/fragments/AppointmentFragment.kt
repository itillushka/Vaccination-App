package com.example.vaccinationmanagerapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.SetAppointmentActivity
import com.example.vaccinationmanagerapp.adapters.AppointmentItem
import com.example.vaccinationmanagerapp.adapters.AppointmentsListAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppointmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppointmentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val setAppointmentButton: Button = view.findViewById(R.id.setAppointmentButton)
        setAppointmentButton.setOnClickListener {
            val intent = Intent(activity, SetAppointmentActivity::class.java)
            startActivity(intent)
        }

        // Create dummy list of appointments
        val appointmentList = listOf(
            AppointmentItem("Vaccine name: Hepatitis B"),
            AppointmentItem("Vaccine name: Hepatitis A"),
            AppointmentItem("Vaccine name: Rotavirus"),
        )

        val recyclerView: RecyclerView = view.findViewById(R.id.recycleViewAppointments)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = AppointmentsListAdapter(appointmentList)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AppointmentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppointmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
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
import com.example.vaccinationmanagerapp.AddRecordActivity
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.SetAppointmentActivity
import com.example.vaccinationmanagerapp.adapters.AppointmentsListAdapter
import com.example.vaccinationmanagerapp.adapters.VaccinationHistoryListAdapter
import com.example.vaccinationmanagerapp.models.VaccinationRecord

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordsFragment : Fragment() {
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
    private lateinit var vaccinationRecordsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_records, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addRecordButton: Button = view.findViewById(R.id.addVaccinationButton)
        addRecordButton.setOnClickListener {
            val intent = Intent(activity, AddRecordActivity::class.java)
            startActivity(intent)
        }
        val recordsList = listOf(
            VaccinationRecord("Hepatitis A", 1, "April 15, 2023", "after 6-18 months"),
            VaccinationRecord("Hepatitis B", 2, "May 20, 2023", "after 6-18 months"),
            VaccinationRecord("Influenza", 1, "June 10, 2023", "after 1 year")
        )
        val recyclerView: RecyclerView = view.findViewById(R.id.vaccinationRecordsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = VaccinationHistoryListAdapter(recordsList)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecordsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
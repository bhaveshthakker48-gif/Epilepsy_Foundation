package com.india.epilepsyfoundation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.india.epilepsyfoundation.adapter.NotificationReminderAdapter
import com.india.epilepsyfoundation.databinding.FragmentReminderBinding
import com.india.epilepsyfoundation.viewmodel.ReminderViewmodel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReminderFragment : Fragment() {

    private lateinit var binding: FragmentReminderBinding
    private val viewModel: ReminderViewmodel by viewModels()
    private lateinit var adapter1: NotificationReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter1 = NotificationReminderAdapter(emptyList())
        binding.recycleViewReminder.adapter = adapter1
        binding.recycleViewReminder.layoutManager = LinearLayoutManager(requireContext())

        loadAllReminder()
    }

    private fun loadAllReminder() {
        viewModel.loadAllReminders { visits ->
            if (visits.isEmpty()) {
                binding.recycleViewReminder.visibility = View.GONE
                binding.noQolieData.visibility = View.VISIBLE
            } else {
                binding.recycleViewReminder.visibility = View.VISIBLE
                binding.noQolieData.visibility = View.GONE
                adapter1.updateData(visits.reversed())
            }
        }
    }
}

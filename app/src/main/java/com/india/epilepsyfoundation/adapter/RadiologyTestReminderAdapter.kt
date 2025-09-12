package com.india.epilepsyfoundation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.india.epilepsyfoundation.databinding.ItemRadiologyTestReminderBinding
import com.india.epilepsyfoundation.entity.RadiologyTestReminderEntity

class RadiologyTestReminderAdapter(
    private var list: List<RadiologyTestReminderEntity>,
    private val onDeleteClick: (RadiologyTestReminderEntity) -> Unit,
    private val onEditClick: (RadiologyTestReminderEntity) -> Unit
) : RecyclerView.Adapter<RadiologyTestReminderAdapter.RadiologyTestHolderViewHolder>() {

    inner class RadiologyTestHolderViewHolder(val binding: ItemRadiologyTestReminderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadiologyTestHolderViewHolder {
        val binding = ItemRadiologyTestReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RadiologyTestHolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RadiologyTestHolderViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            doctorName.text = item.testName
            visitDate.text = item.visitDate
            VisitTime.text = item.visitTime
            labName.text = item.labName
            reminderDate.text = item.reminderDate
            reminderTime.text = item.reminderTime

            deleteIcon.setOnClickListener { onDeleteClick(item) }
            editIcon.setOnClickListener { onEditClick(item) }
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<RadiologyTestReminderEntity>) {
        list = newList
        notifyDataSetChanged()
    }
}

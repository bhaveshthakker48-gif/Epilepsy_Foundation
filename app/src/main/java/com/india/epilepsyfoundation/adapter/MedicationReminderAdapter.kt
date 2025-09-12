package com.india.epilepsyfoundation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.india.epilepsyfoundation.databinding.ItemMedicationReminderBinding
import com.india.epilepsyfoundation.entity.MedicationReminderEntity

class MedicationReminderAdapter(
    private var list: List<MedicationReminderEntity>,
    private val onDeleteClick: (MedicationReminderEntity) -> Unit,
    private val onEditClick: (MedicationReminderEntity) -> Unit

) : RecyclerView.Adapter<MedicationReminderAdapter.MedicationHolderViewHolder>() {

    inner class MedicationHolderViewHolder(val binding: ItemMedicationReminderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationHolderViewHolder {
        val binding = ItemMedicationReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MedicationHolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicationHolderViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            doctorName.text = item.medicationName
            visitDate.text = item.startDate
            VisitTime.text = item.startTime
            duration.text = item.duration
            durationUnit.text = item.durationUnit
            frequency.text = item.frequency
            dose.text = item.dose

            deleteIcon.setOnClickListener { onDeleteClick(item) }
            editIcon.setOnClickListener { onEditClick(item) }
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<MedicationReminderEntity>) {
        list = newList
        notifyDataSetChanged()
    }
}

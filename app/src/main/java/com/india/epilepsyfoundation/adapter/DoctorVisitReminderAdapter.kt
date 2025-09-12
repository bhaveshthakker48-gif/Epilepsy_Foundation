package com.india.epilepsyfoundation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.india.epilepsyfoundation.databinding.ItemDoctorVisitReminderBinding
import com.india.epilepsyfoundation.entity.DoctorVisitReminderEntity

class DoctorVisitReminderAdapter(
    private var list: List<DoctorVisitReminderEntity>,
    private val onDeleteClick: (DoctorVisitReminderEntity) -> Unit,
    private val onEditClick: (DoctorVisitReminderEntity) -> Unit
) : RecyclerView.Adapter<DoctorVisitReminderAdapter.DoctorVisitViewHolder>() {

    inner class DoctorVisitViewHolder(val binding: ItemDoctorVisitReminderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorVisitViewHolder {
        val binding = ItemDoctorVisitReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DoctorVisitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorVisitViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            doctorName.text = item.doctorName
            visitDate.text = item.doctorVisitDate
            VisitTime.text = item.doctorVisitTime

            deleteIcon.setOnClickListener { onDeleteClick(item) }
            editIcon.setOnClickListener { onEditClick(item) }
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<DoctorVisitReminderEntity>) {
        list = newList
        notifyDataSetChanged()
    }
}

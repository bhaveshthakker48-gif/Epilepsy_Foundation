package com.india.epilepsyfoundation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.india.epilepsyfoundation.databinding.ItemPathologyTestReminderBinding
import com.india.epilepsyfoundation.entity.PathologyTestReminderEntity

class PathologyTestReminderAdapter(
    private var list: List<PathologyTestReminderEntity>,
    private val onDeleteClick: (PathologyTestReminderEntity) -> Unit,
    private val onEditClick: (PathologyTestReminderEntity) -> Unit
) : RecyclerView.Adapter<PathologyTestReminderAdapter.PathologyTestHolderViewHolder>() {

    inner class PathologyTestHolderViewHolder(val binding: ItemPathologyTestReminderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathologyTestHolderViewHolder {
        val binding = ItemPathologyTestReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PathologyTestHolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PathologyTestHolderViewHolder, position: Int) {
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

    fun updateData(newList: List<PathologyTestReminderEntity>) {
        list = newList
        notifyDataSetChanged()
    }
}

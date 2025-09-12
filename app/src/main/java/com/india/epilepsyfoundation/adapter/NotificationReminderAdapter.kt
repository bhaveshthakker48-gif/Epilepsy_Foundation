package com.india.epilepsyfoundation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.india.epilepsyfoundation.databinding.ReminderNotificationsItemBinding
import com.india.epilepsyfoundation.entity.ReminderNotificationEntity

class NotificationReminderAdapter(
    private var list: List<ReminderNotificationEntity>,
) : RecyclerView.Adapter<NotificationReminderAdapter.NotificationReminderViewHolder>() {

    inner class NotificationReminderViewHolder(val binding: ReminderNotificationsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationReminderViewHolder {
        val binding = ReminderNotificationsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationReminderViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            title.text = item.notificationTitle
            content.text = item.notificationContent
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<ReminderNotificationEntity>) {
        list = newList
        notifyDataSetChanged()
    }
}

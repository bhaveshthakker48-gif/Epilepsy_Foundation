package com.india.epilepsyfoundation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.india.epilepsyfoundation.databinding.ItemAttackDetailsBinding
import com.india.epilepsyfoundation.entity.AttackDetailsEntity

class AttackDetailsAdapter(
    private var list: List<AttackDetailsEntity>,
) : RecyclerView.Adapter<AttackDetailsAdapter.AttackDetailsViewHolder>() {

    inner class AttackDetailsViewHolder(val binding: ItemAttackDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttackDetailsViewHolder {
        val binding = ItemAttackDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AttackDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttackDetailsViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            attackDate.text = item.dateOfAttack
            timeOfattack.text = item.timeOfAttack
            duration.text = item.duration
            typeOfAttack.text = item.typeOfAttack
            attackDetails.text = item.detailsOfAttack
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<AttackDetailsEntity>) {
        list = newList
        notifyDataSetChanged()
    }
}

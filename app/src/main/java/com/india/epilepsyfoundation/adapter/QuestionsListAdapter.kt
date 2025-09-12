package com.india.epilepsyfoundation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.india.epilepsyfoundation.databinding.ItemQuestionBinding
import com.india.epilepsyfoundation.entity.QuestionnaireEntity

class QuestionsListAdapter(
    private var list: List<QuestionnaireEntity>
) : RecyclerView.Adapter<QuestionsListAdapter.QuestionsListHolderViewHolder>() {

    inner class QuestionsListHolderViewHolder(val binding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionsListHolderViewHolder {
        val binding = ItemQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestionsListHolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionsListHolderViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            date.text = item.date
            firstName.text = item.firstName
            lastName.text = item.lastName
            age.text = item.age
            gender.text = item.gender
            result.text = item.result
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<QuestionnaireEntity>) {
        list = newList
        notifyDataSetChanged()
    }
}

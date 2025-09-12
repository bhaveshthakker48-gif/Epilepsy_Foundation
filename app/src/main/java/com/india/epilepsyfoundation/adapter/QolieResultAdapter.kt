package com.india.epilepsyfoundation.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.india.epilepsyfoundation.databinding.ItemQulieBinding
import com.india.epilepsyfoundation.entity.QolieQuestionEntity

class QolieResultAdapter(
    private var qolieList: List<QolieQuestionEntity>
) : RecyclerView.Adapter<QolieResultAdapter.QolieViewHolder>() {

    inner class QolieViewHolder(val binding: ItemQulieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QolieViewHolder {
        val binding = ItemQulieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QolieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QolieViewHolder, position: Int) {
        val item = qolieList[position]
        holder.binding.dateText.text = item.date
        holder.binding.scoreText.text = item.totalScore.toString()
    }

    override fun getItemCount(): Int = qolieList.size

    fun updateData(newList: List<QolieQuestionEntity>) {
        qolieList = newList
        notifyDataSetChanged()
    }
}

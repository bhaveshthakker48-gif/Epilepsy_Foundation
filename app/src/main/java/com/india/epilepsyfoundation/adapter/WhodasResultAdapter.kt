package com.india.epilepsyfoundation.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.india.epilepsyfoundation.databinding.ItemQulieBinding
import com.india.epilepsyfoundation.databinding.ItemStigmaScaleBinding
import com.india.epilepsyfoundation.databinding.ItemWhodasBinding
import com.india.epilepsyfoundation.entity.QolieQuestionEntity
import com.india.epilepsyfoundation.entity.StigmaScaleQuestionEntity
import com.india.epilepsyfoundation.entity.WhodasQuestionEntity

class WhodasResultAdapter(
    private var qolieList: List<WhodasQuestionEntity>
) : RecyclerView.Adapter<WhodasResultAdapter.WhodasViewHolder>() {

    inner class WhodasViewHolder(val binding: ItemWhodasBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhodasViewHolder {
        val binding = ItemWhodasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WhodasViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WhodasViewHolder, position: Int) {
        val item = qolieList[position]
        holder.binding.dateText.text = item.date
        holder.binding.scoreText.text = item.totalScore.toString()
    }

    override fun getItemCount(): Int = qolieList.size

    fun updateData(newList: List<WhodasQuestionEntity>) {
        qolieList = newList
        notifyDataSetChanged()
    }
}

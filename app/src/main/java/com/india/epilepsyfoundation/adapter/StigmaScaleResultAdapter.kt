package com.india.epilepsyfoundation.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.india.epilepsyfoundation.databinding.ItemQulieBinding
import com.india.epilepsyfoundation.databinding.ItemStigmaScaleBinding
import com.india.epilepsyfoundation.entity.QolieQuestionEntity
import com.india.epilepsyfoundation.entity.StigmaScaleQuestionEntity

class StigmaScaleResultAdapter(
    private var qolieList: List<StigmaScaleQuestionEntity>
) : RecyclerView.Adapter<StigmaScaleResultAdapter.StigmaScaleViewHolder>() {

    inner class StigmaScaleViewHolder(val binding: ItemStigmaScaleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StigmaScaleViewHolder {
        val binding = ItemStigmaScaleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StigmaScaleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StigmaScaleViewHolder, position: Int) {
        val item = qolieList[position]
        holder.binding.dateText.text = item.date
        holder.binding.scoreText.text = item.totalScore.toString()
    }

    override fun getItemCount(): Int = qolieList.size

    fun updateData(newList: List<StigmaScaleQuestionEntity>) {
        qolieList = newList
        notifyDataSetChanged()
    }
}

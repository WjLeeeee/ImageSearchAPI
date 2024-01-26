package com.example.imagesave.KeepFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagesave.data.SelectedItem
import com.example.imagesave.databinding.RecyclerItemBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class KeepAdapter(val mItems: MutableList<SelectedItem>) : RecyclerView.Adapter<KeepAdapter.Holder>() {

    interface ItemClick {
        fun onClick(item: SelectedItem)
    }
    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.onClick(mItems[position])
        }
        val item = mItems[position]
        Glide.with(holder.itemView.context).load(item.thumbnail).into(holder.iconImageView)
        holder.title.text = item.siteName
        val parsed = OffsetDateTime.parse(item.time)
        val parseDate = parsed.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val parseTime = parsed.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        holder.time.text = parseDate + " " + parseTime
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class Holder(val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val iconImageView = binding.iconItem
        val title = binding.textItem
        val time = binding.textItem2
        val heart = binding.heartImage
    }
}
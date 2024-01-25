package com.example.imagesave

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagesave.data.SearchDocument
import com.example.imagesave.databinding.RecyclerItemBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class SearchAdapter(val mItems: MutableList<SearchDocument>) : RecyclerView.Adapter<SearchAdapter.Holder>() {

    interface ItemClick {
        fun onClick(view : View, position : Int)
    }

    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.setOnClickListener {  //클릭이벤트추가부분
            itemClick?.onClick(it, position)
        }
        val item = mItems[position]
        Glide.with(holder.itemView.context).load(item.thumbnail_url).into(holder.iconImageView)
        holder.title.text = item.display_sitename
        val parsed = OffsetDateTime.parse(item.datetime)
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
    }
}
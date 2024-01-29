package com.example.imagesave.ImageSearchFragment

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagesave.data.CombinedSearchItem
import com.example.imagesave.data.SearchDocument
import com.example.imagesave.data.SearchDocumentVideo
import com.example.imagesave.data.SearchItemType
import com.example.imagesave.databinding.RecyclerItemBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class SearchAdapter(val mItems: MutableList<CombinedSearchItem>) :
    RecyclerView.Adapter<SearchAdapter.Holder>() {

    interface ItemClick {
        fun onClick(item: CombinedSearchItem, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.setOnClickListener {  //클릭이벤트추가부분
            itemClick?.onClick(mItems[position], position)
            if (holder.heart.visibility == INVISIBLE) {
                holder.heart.visibility = VISIBLE
            } else {
                holder.heart.visibility = INVISIBLE
            }
        }
        val item = mItems[position]
        when(item.itemType){
            SearchItemType.IMAGE -> {
                val imageItem = item.searchItem as SearchDocument
                Glide.with(holder.itemView.context).load(imageItem.thumbnail_url).into(holder.iconImageView)
                holder.title.text = imageItem.display_sitename
                val parsed = OffsetDateTime.parse(imageItem.datetime)
                val parseDate = parsed.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val parseTime = parsed.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                holder.time.text = parseDate + " " + parseTime
                if(imageItem.isLike){
                    holder.heart.visibility = VISIBLE
                } else{
                    holder.heart.visibility = INVISIBLE
                }
            }
            SearchItemType.VIDEO -> {
                val videoItem = item.searchItem as SearchDocumentVideo
                Glide.with(holder.itemView.context).load(videoItem.thumbnail).into(holder.iconImageView)
                holder.title.text = videoItem.title
                val parsed = OffsetDateTime.parse(videoItem.datetime)
                val parseDate = parsed.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val parseTime = parsed.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                holder.time.text = parseDate + " " + parseTime
                if(videoItem.isLike){
                    holder.heart.visibility = VISIBLE
                } else{
                    holder.heart.visibility = INVISIBLE
                }
            }
        }

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
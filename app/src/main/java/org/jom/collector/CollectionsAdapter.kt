package org.jom.collector

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class CollectionItem(
    val name: String,
    val time: String,
    val amount: Int
)


class CollectionsAdapter(private val collectionItems: List<CollectionItem>) :
    RecyclerView.Adapter<CollectionsAdapter.CollectionsViewHolder>() {

    inner class CollectionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val collectionName: TextView = itemView.findViewById(R.id.tvCollectionName)
        val collectionTime: TextView = itemView.findViewById(R.id.tvCollectionTime)
        val collectionAmount: TextView = itemView.findViewById(R.id.tvCollectionAmount)
//        val selectButton: Button = itemView.findViewById(R.id.btnSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection_card, parent, false)
        return CollectionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        val currentItem = collectionItems[position]

        holder.collectionName.text = currentItem.name
        holder.collectionTime.text = currentItem.time
        holder.collectionAmount.text = currentItem.amount.toString()

        // Set your cash icon here, you might want to use a library like Glide or Picasso
        // depending on how you handle your image resources.
        // holder.cashIcon.setImageResource(R.drawable.ic_cash)

//        holder.selectButton.setOnClickListener {
//            // Handle button click here if needed
//        }
    }

    override fun getItemCount(): Int {
        return collectionItems.size
    }
}

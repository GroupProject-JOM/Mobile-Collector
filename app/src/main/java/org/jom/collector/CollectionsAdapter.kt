package org.jom.collector

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.jom.collector.collections.ViewCollectionActivity

data class CollectionItem(
    val location: String,
    val time: String,
    val amount: Int,
    val method: String
)


class CollectionsAdapter(private val collectionItems: List<CollectionItem>) :
    RecyclerView.Adapter<CollectionsAdapter.CollectionsViewHolder>() {

    inner class CollectionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val collectionLocation: TextView = itemView.findViewById(R.id.city)
        val collectionTime: TextView = itemView.findViewById(R.id.time)
        val collectionAmount: TextView = itemView.findViewById(R.id.coconut_amount)
        val collectionPayment: TextView = itemView.findViewById(R.id.payment_method)

        val collectionPaymentIcon: ImageView = itemView.findViewById(R.id.icon_cash)
//        val selectButton: Button = itemView.findViewById(R.id.btnSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection_card, parent, false)
        return CollectionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        val currentItem = collectionItems[position]

        holder.collectionLocation.text = currentItem.location
        holder.collectionTime.text = currentItem.time
        holder.collectionAmount.text = currentItem.amount.toString()
        holder.collectionPayment.text = currentItem.method.capitalize()

        if (currentItem.method == "Bank") {
            holder.collectionPaymentIcon.setImageResource(R.drawable.icon_bank)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ViewCollectionActivity::class.java)

            // Pass data to the next activity if needed
//            intent.putExtra("collectionName", currentItem.name)
//            intent.putExtra("collectionTime", currentItem.time)
//            intent.putExtra("collectionAmount", currentItem.amount)

            holder.itemView.context.startActivity(intent)
        }

        // Display a toast with all the data when the card is clicked
        holder.itemView.setOnLongClickListener {
            val toastMessage =
                "Name: ${currentItem.location}\nTime: ${currentItem.time}\nAmount: ${currentItem.amount}"
            Toast.makeText(holder.itemView.context, toastMessage, Toast.LENGTH_SHORT).show()

            true
        }

        // Set your cash icon here, you might want to use a library like Glide or Picasso
        // depending on how you handle your image resources.
//         holder.cashIcon.setImageResource(R.drawable.ic_cash)

//        holder.selectButton.setOnClickListener {
//            // Handle button click here if needed
//        }
    }

    override fun getItemCount(): Int {
        return collectionItems.size
    }
}

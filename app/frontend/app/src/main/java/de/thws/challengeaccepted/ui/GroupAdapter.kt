package de.thws.challengeaccepted.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.thws.challengeaccepted.R
import de.thws.challengeaccepted.data.entities.Gruppe

class GroupAdapter(
    private val onClick: (Gruppe) -> Unit
) : ListAdapter<Gruppe, GroupAdapter.GroupViewHolder>(GroupDiffCallback) {

    class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.tvGroupName)
        private val image: ImageView = view.findViewById(R.id.ivGroupImage)
        private val rootLayout: View = view.findViewById(R.id.rootLayout)

        fun bind(gruppe: Gruppe, onClick: (Gruppe) -> Unit) {
            name.text = gruppe.gruppenname

            if (!gruppe.gruppenbild.isNullOrEmpty()) {
                Glide.with(image.context).load(gruppe.gruppenbild).into(image)
            } else {
                image.setImageResource(R.drawable.group_profile_picture)
            }

            // Dynamischer Rahmen:
            if (gruppe.aufgabe) {
                rootLayout.setBackgroundResource(R.drawable.red_frame)
            } else {
                rootLayout.setBackgroundResource(R.drawable.green_frame)
            }

            itemView.setOnClickListener { onClick(gruppe) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val gruppe = getItem(position)
        holder.bind(gruppe, onClick)
    }
}

// Dieser "DiffCallback" berechnet die Unterschiede in der Liste und sorgt für flüssige Animationen.
object GroupDiffCallback : DiffUtil.ItemCallback<Gruppe>() {
    override fun areItemsTheSame(oldItem: Gruppe, newItem: Gruppe): Boolean {
        return oldItem.gruppeId == newItem.gruppeId
    }

    override fun areContentsTheSame(oldItem: Gruppe, newItem: Gruppe): Boolean {
        return oldItem == newItem
    }
}
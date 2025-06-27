package de.thws.challengeaccepted.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.thws.challengeaccepted.R
import de.thws.challengeaccepted.models.GroupResponse

class GroupAdapter(
    private val groups: List<GroupResponse>,
    private val onClick: (GroupResponse) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvGroupName)
        val image: ImageView = view.findViewById(R.id.ivGroupImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.name.text = group.gruppenname // <-- angepasst!

        // Beispiel für ein Gruppenbild (optional):
        // if (!group.gruppenbild.isNullOrEmpty()) {
        //     Glide.with(holder.image.context).load(group.gruppenbild).into(holder.image)
        // } else {
        //     holder.image.setImageResource(R.drawable.default_group_icon)
        // }

        holder.itemView.setOnClickListener { onClick(group) }
    }

    override fun getItemCount(): Int = groups.size
}

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
import de.thws.challengeaccepted.data.entities.User

class MemberAdapter : ListAdapter<User, MemberAdapter.MemberViewHolder>(MemberDiffCallback) {

    class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.tvMemberName) // Beispiel-IDs
        private val image: ImageView = view.findViewById(R.id.ivMemberImage)

        fun bind(user: User) {
            name.text = user.username
            if (!user.profilbild.isNullOrEmpty()) {
                Glide.with(image.context).load(user.profilbild).into(image)
            } else {
                image.setImageResource(R.drawable.group_profile_picture) // Fallback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false) // Du brauchst ein item_member.xml Layout
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object MemberDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.userId == newItem.userId
    override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
}
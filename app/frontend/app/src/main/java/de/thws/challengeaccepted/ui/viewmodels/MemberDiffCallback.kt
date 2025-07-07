package de.thws.challengeaccepted.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import de.thws.challengeaccepted.data.entities.User

// Dies ist die einzige, zentrale Definition des DiffUtil.ItemCallback für User.
object MemberDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}

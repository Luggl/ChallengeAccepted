package de.thws.challengeaccepted.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.thws.challengeaccepted.R
import de.thws.challengeaccepted.data.entities.BeitragEntity

class GroupFeedAdapter : ListAdapter<BeitragEntity, GroupFeedAdapter.FeedViewHolder>(BeitragDiffCallback) {

    class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvBeschreibung: TextView = view.findViewById(R.id.tvBeschreibung)
        private val feedImage: ImageView = view.findViewById(R.id.feedImage)
        private val feedVideo: VideoView = view.findViewById(R.id.feedVideo)
        private val btnAccepted: Button = view.findViewById(R.id.btn_accepted)
        private val btnRejected: Button = view.findViewById(R.id.btn_rejected)

        fun bind(beitrag: BeitragEntity) {
            tvBeschreibung.text = beitrag.beschreibung

            if (!beitrag.videoUrl.isNullOrBlank()) {
                feedImage.visibility = View.GONE
                feedVideo.visibility = View.VISIBLE

                val videoUrl = if (beitrag.videoUrl.startsWith("/")) {
                    "http://192.168.178.37:5000${beitrag.videoUrl.replace("\\", "/")}"
                } else {
                    beitrag.videoUrl
                }
                feedVideo.setVideoPath(videoUrl)
                feedVideo.seekTo(1)
                val mediaController = MediaController(itemView.context)
                mediaController.setAnchorView(feedVideo)
                feedVideo.setMediaController(mediaController)

            } else { // Annahme: Bild anzeigen, wenn kein Video da ist
                feedVideo.visibility = View.GONE
                feedImage.visibility = View.VISIBLE
                // Hier Glide verwenden, um beitrag.imageUrl zu laden
                Glide.with(feedImage.context)
                    .load(beitrag.imageUrl)
                    .placeholder(R.drawable.login_background_pic)
                    .into(feedImage)
            }

            btnAccepted.setOnClickListener {
                Toast.makeText(itemView.context, "Accepted geklickt!", Toast.LENGTH_SHORT).show()
            }
            btnRejected.setOnClickListener {
                Toast.makeText(itemView.context, "Rejected geklickt!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_beitrag, parent, false) // Du brauchst ein item_beitrag.xml
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object BeitragDiffCallback : DiffUtil.ItemCallback<BeitragEntity>() {
    override fun areItemsTheSame(oldItem: BeitragEntity, newItem: BeitragEntity): Boolean {
        return oldItem.beitragId == newItem.beitragId
    }
    override fun areContentsTheSame(oldItem: BeitragEntity, newItem: BeitragEntity): Boolean {
        return oldItem == newItem
    }
}

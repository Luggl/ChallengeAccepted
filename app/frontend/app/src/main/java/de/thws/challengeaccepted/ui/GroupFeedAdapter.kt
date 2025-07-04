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
import androidx.recyclerview.widget.RecyclerView
import de.thws.challengeaccepted.R
import de.thws.challengeaccepted.models.GroupFeedItem

class GroupFeedAdapter(
    private var feedList: List<GroupFeedItem>
) : RecyclerView.Adapter<GroupFeedAdapter.FeedViewHolder>() {

    class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBeschreibung: TextView = view.findViewById(R.id.tvBeschreibung)
        val feedImage: ImageView = view.findViewById(R.id.feedImage)
        val feedVideo: VideoView = view.findViewById(R.id.feedVideo)
        val btnAccepted: Button = view.findViewById(R.id.btn_accepted)
        val btnRejected: Button = view.findViewById(R.id.btn_rejected)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_beitrag, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val beitrag = feedList[position]
        holder.tvBeschreibung.text = beitrag.beschreibung

        // Video oder Bild zeigen (wie im anderen Adapter)
        if (!beitrag.video_url.isNullOrBlank()) {
            holder.feedImage.visibility = View.GONE
            holder.feedVideo.visibility = View.VISIBLE

            val videoUrl = if (beitrag.video_url.startsWith("/")) {
                "http://192.168.178.37:5000${beitrag.video_url.replace("\\", "/")}"
            } else {
                beitrag.video_url
            }
            holder.feedVideo.setVideoPath(videoUrl)
            holder.feedVideo.seekTo(1)
            val mediaController = MediaController(holder.itemView.context)
            mediaController.setAnchorView(holder.feedVideo)
            holder.feedVideo.setMediaController(mediaController)
        } else {
            holder.feedVideo.visibility = View.GONE
            holder.feedImage.visibility = View.VISIBLE
            holder.feedImage.setImageResource(R.drawable.login_background_pic)
        }

        holder.btnAccepted.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Accepted geklickt!", Toast.LENGTH_SHORT).show()
        }
        holder.btnRejected.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Rejected geklickt!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = feedList.size

    fun updateData(newFeed: List<GroupFeedItem>) {
        feedList = newFeed
        notifyDataSetChanged()
    }
}
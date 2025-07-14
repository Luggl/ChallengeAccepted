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



class GroupFeedAdapter(
    private var items: List<BeitragEntity>,
    private val onVote: (beitragId: String, vote: String) -> Unit
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

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val beitrag = items[position]
        holder.tvBeschreibung.text = beitrag.beschreibung

        // Video oder Bild anzeigen
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
            // Lade ggf. Thumbnail, sonst Platzhalter
            Glide.with(holder.feedImage.context)
                .load(beitrag.thumbnail_url)
                .placeholder(R.drawable.login_background_pic)
                .into(holder.feedImage)
        }

        val prefs = holder.itemView.context.getSharedPreferences("app", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getString("USER_ID", null)
        val isOwnBeitrag = beitrag.user_id == userId

        if (isOwnBeitrag) {
            // Beide Buttons ausgrauen & deaktivieren
            holder.btnAccepted.setBackgroundResource(R.drawable.button_grey)
            holder.btnRejected.setBackgroundResource(R.drawable.button_grey)
            holder.btnAccepted.alpha = 0.5f
            holder.btnRejected.alpha = 0.5f
            holder.btnAccepted.isEnabled = false
            holder.btnRejected.isEnabled = false
        } else {
            // Voting-UI: Buttons wie gehabt
            when (beitrag.user_vote) {
                "Accepted" -> {
                    holder.btnAccepted.setBackgroundResource(R.drawable.blue_frame)
                    holder.btnRejected.setBackgroundResource(R.drawable.button_grey)
                    holder.btnAccepted.alpha = 1f
                    holder.btnRejected.alpha = 0.6f
                }
                "abgelehnt" -> {
                    holder.btnRejected.setBackgroundResource(R.drawable.red_frame)
                    holder.btnAccepted.setBackgroundResource(R.drawable.button_grey)
                    holder.btnRejected.alpha = 1f
                    holder.btnAccepted.alpha = 0.6f
                }
                else -> {
                    holder.btnAccepted.setBackgroundResource(R.drawable.blue_frame)
                    holder.btnRejected.setBackgroundResource(R.drawable.red_frame)
                    holder.btnAccepted.alpha = 1f
                    holder.btnRejected.alpha = 1f
                }
            }
            holder.btnAccepted.isEnabled = true
            holder.btnRejected.isEnabled = true

            holder.btnAccepted.setOnClickListener {
                if (beitrag.user_vote != "Accepted") {
                    onVote(beitrag.beitrag_Id, "Accepted")
                }
            }
            holder.btnRejected.setOnClickListener {
                if (beitrag.user_vote != "abgelehnt") {
                    onVote(beitrag.beitrag_Id, "abgelehnt")
                }
            }
        }
    }

    // Damit du einfach eine neue Liste setzen kannst (wie submitList)
    fun submitList(newItems: List<BeitragEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}


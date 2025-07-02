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
import de.thws.challengeaccepted.models.Beitrag

class FeedAdapter(private val feedList: List<Beitrag>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

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

        if (!beitrag.video_url.isNullOrBlank()) {
            holder.feedImage.visibility = View.GONE
            holder.feedVideo.visibility = View.VISIBLE

            // -- WICHTIG: Video-URL ggf. mit Server-Adresse ergänzen --
            val videoUrl = if (beitrag.video_url.startsWith("/")) {
                "http://10.31.39.188:5000${
                    beitrag.video_url.replace(
                        "\\",
                        "/"
                    )
                }" // Passe die Base-URL an!
            } else {
                beitrag.video_url
            }

            holder.feedVideo.setVideoPath(videoUrl)
            holder.feedVideo.seekTo(1) // Zeigt Vorschaubild

            // Optional: MediaController für Play/Pause
            val mediaController = MediaController(holder.itemView.context)
            mediaController.setAnchorView(holder.feedVideo)
            holder.feedVideo.setMediaController(mediaController)

        } else {
            holder.feedVideo.visibility = View.GONE
            holder.feedImage.visibility = View.VISIBLE
            holder.feedImage.setImageResource(R.drawable.login_background_pic)
        }

        // Buttons: z.B. Listener setzen (hier nur Toast zum Test)
        holder.btnAccepted.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Accepted geklickt!", Toast.LENGTH_SHORT).show()
        }
        holder.btnRejected.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Rejected geklickt!", Toast.LENGTH_SHORT).show()
        }
    }
    override fun getItemCount(): Int {
        return feedList.size
    }
}

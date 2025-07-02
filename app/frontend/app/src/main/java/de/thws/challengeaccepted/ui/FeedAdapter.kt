package de.thws.challengeaccepted.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
            holder.feedVideo.setVideoPath(beitrag.video_url) // Pfad ggf. anpassen!
            holder.feedVideo.seekTo(1) // Vorschau anzeigen
            // Optional: Autoplay oder Button zum Abspielen
        } else {
            holder.feedVideo.visibility = View.GONE
            holder.feedImage.visibility = View.VISIBLE
            holder.feedImage.setImageResource(R.drawable.login_background_pic)
        }

        // Buttons: z.B. Listener setzen
        holder.btnAccepted.setOnClickListener { /* Bewertungscode */ }
        holder.btnRejected.setOnClickListener { /* Bewertungscode */ }
    }


    override fun getItemCount() = feedList.size
}